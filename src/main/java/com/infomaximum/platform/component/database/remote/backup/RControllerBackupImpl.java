package com.infomaximum.platform.component.database.remote.backup;

import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.platform.component.database.DatabaseComponent;
import com.infomaximum.platform.component.database.exception.DatabaseExceptionBuilder;
import com.infomaximum.platform.component.database.utils.BackupUtils;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.AbstractQueryRController;
import com.infomaximum.platform.querypool.QueryPool;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.exception.NotAbsolutePathException;
import com.infomaximum.platform.sdk.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RControllerBackupImpl extends AbstractQueryRController<DatabaseComponent> implements RControllerBackup {

    public RControllerBackupImpl(DatabaseComponent component, ResourceProvider resources) {
        super(component, resources);
        resources.borrowAllDomainObjects(QueryPool.LockType.SHARED);
    }

    @Override
    public void createBackup(String backupDirPath, String backupName) throws PlatformException {
        Path backupPath = buildBackupPath(backupDirPath, backupName);
        FileUtils.ensureDirectory(backupPath);
        try {
            RocksDB rocksDB = component.getRocksDBProvider().getRocksDB();
            BackupUtils.createBackup(backupPath, rocksDB);
            rocksDB.compactRange();
        } catch (DatabaseException | RocksDBException e) {
            throw DatabaseExceptionBuilder.buildBackupException(e);
        }
    }

    public static Path buildBackupPath(String backupDirPath, String backupName) throws PlatformException {
        if (StringUtils.isEmpty(backupName) || !backupName.equals(new File(backupName).getName())) {
            throw DatabaseExceptionBuilder.buildInvalidBackupNameException();
        }
        if (StringUtils.isEmpty(backupDirPath)) {
            throw DatabaseExceptionBuilder.buildInvalidBackupPathException();
        }

        try {
            Path backupPath = Paths.get(backupDirPath);
            if (!backupPath.isAbsolute()) {
                throw new NotAbsolutePathException(backupDirPath);
            }
            return backupPath.resolve(backupName);
        } catch (IllegalArgumentException e) {
            throw DatabaseExceptionBuilder.buildInvalidBackupPathException(e);
        }
    }
}
