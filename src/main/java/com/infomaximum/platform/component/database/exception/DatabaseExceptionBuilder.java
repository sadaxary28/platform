package com.infomaximum.platform.component.database.exception;

import com.infomaximum.platform.component.database.DatabaseConsts;
import com.infomaximum.subsystems.exception.ExceptionFactory;
import com.infomaximum.subsystems.exception.SubsystemException;
import com.infomaximum.subsystems.exception.SubsystemExceptionFactory;

public class DatabaseExceptionBuilder {

    private static final ExceptionFactory EXCEPTION_FACTORY = new SubsystemExceptionFactory(DatabaseConsts.UUID);

    public static SubsystemException buildBackupException(Throwable e) {
        return EXCEPTION_FACTORY.build("backup_error", e);
    }

    public static SubsystemException buildRestoreException(Throwable e) {
        return EXCEPTION_FACTORY.build("restore_error", e);
    }

    public static SubsystemException buildInvalidDbPathException() {
        return buildInvalidDbPathException(null);
    }

    public static SubsystemException buildInvalidDbPathException(Throwable e) {
        return EXCEPTION_FACTORY.build("invalid_db_path", e);
    }

    public static SubsystemException buildInvalidBackupNameException() {
        return EXCEPTION_FACTORY.build("invalid_backup_name");
    }

    public static SubsystemException buildInvalidBackupPathException() {
        return buildInvalidBackupPathException(null);
    }

    public static SubsystemException buildInvalidBackupPathException(Throwable e) {
        return EXCEPTION_FACTORY.build("invalid_backup_path", e);
    }
}
