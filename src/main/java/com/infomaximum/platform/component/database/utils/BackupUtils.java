package com.infomaximum.platform.component.database.utils;

import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.utils.PathUtils;
import com.infomaximum.rocksdb.RocksDBProvider;
import com.infomaximum.rocksdb.RocksDataBaseBuilder;
import org.rocksdb.*;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

public class BackupUtils {

    public static void createBackup(Path backupPath, Path dbPath) throws DatabaseException {
        try (RocksDBProvider dataBase = new RocksDataBaseBuilder()
                .withPath(dbPath)
                .build()) {
            createBackup(backupPath, dataBase.getRocksDB());
        }
    }

    public static void createBackup(Path backupPath, RocksDB rocksDB) throws DatabaseException {
        PathUtils.checkPath(backupPath);

        try (BackupableDBOptions dbOptions = new BackupableDBOptions(backupPath.toString());
             BackupEngine backupEngine = BackupEngine.open(Env.getDefault(), dbOptions)) {

            final boolean flushBeforeBackup = true;
            try {
                backupEngine.createNewBackup(rocksDB, flushBeforeBackup);
            } catch (RocksDBException e) {
                if (e.getStatus() == null || e.getStatus().getCode() != Status.Code.Corruption) {
                    throw e;
                }

                backupEngine.purgeOldBackups(0);
                backupEngine.createNewBackup(rocksDB, flushBeforeBackup);
            }

            List<BackupInfo> backups = backupEngine.getBackupInfo();
            if (backups.isEmpty()) {
                throw new RocksDBException("BackupEngine::getBackupInfo return empty list.");
            }

            BackupInfo lastBackup = backups.stream().max(Comparator.comparingLong(BackupInfo::backupId)).get();
            try {
                backupEngine.verifyBackup(lastBackup.backupId());
            } catch (RocksDBException e) {
                backupEngine.deleteBackup(lastBackup.backupId());
                throw e;
            }

            backupEngine.purgeOldBackups(1);
        } catch (IllegalArgumentException e) {
            throw new DatabaseException(e.getMessage());
        } catch (RocksDBException e) {
            throw new DatabaseException(e);
        }
    }

    public static void restoreFromBackup(Path backupPath, Path dbPath) throws DatabaseException {
        PathUtils.checkPath(backupPath);
        PathUtils.checkPath(dbPath);

        RocksDB.loadLibrary();

        try (BackupableDBOptions dbOptions = new BackupableDBOptions(backupPath.toString());
             BackupEngine backupEngine = BackupEngine.open(Env.getDefault(), dbOptions);
             RestoreOptions restoreOptions = new RestoreOptions(false)) {

            final String dbDir = dbPath.toString();
            backupEngine.restoreDbFromLatestBackup(dbDir, dbDir, restoreOptions);
        } catch (IllegalArgumentException e) {
            throw new DatabaseException(e.getMessage());
        } catch (RocksDBException e) {
            throw new DatabaseException(e);
        }
    }
}
