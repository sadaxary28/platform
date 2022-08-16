package com.infomaximum.platform.component.database.remote.backup;

import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.platform.component.database.DatabaseComponent;
import com.infomaximum.platform.component.database.exception.DatabaseExceptionBuilder;
import com.infomaximum.platform.component.database.utils.BackupUtils;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.AbstractQueryRController;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.exception.NotAbsolutePathException;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("unused")
public class RControllerRestoreImpl extends AbstractQueryRController<DatabaseComponent> implements RControllerRestore {

	public RControllerRestoreImpl(DatabaseComponent component, ResourceProvider resources) {
        super(component, resources);
    }

    @Override
    public void restoreBackup(String backupDirPath, String backupName, String newDbPath) throws PlatformException {
        Path dbPath = buildDbPath(newDbPath);
        Path backupPath = RControllerBackupImpl.buildBackupPath(backupDirPath, backupName);
        try {
            BackupUtils.restoreFromBackup(backupPath, dbPath);
        } catch (DatabaseException e) {
            throw DatabaseExceptionBuilder.buildRestoreException(e);
        }
    }

    private static Path buildDbPath(String path) throws PlatformException {
        try {
            Path dbPath = Paths.get(path);
            if (!dbPath.isAbsolute()) {
                throw new NotAbsolutePathException(path);
            }

            if (Files.exists(dbPath)) {
                throw new FileAlreadyExistsException(path);
            }

            return dbPath;
        } catch (IllegalArgumentException | FileAlreadyExistsException e) {
            throw DatabaseExceptionBuilder.buildInvalidDbPathException(e);
        }
    }
}
