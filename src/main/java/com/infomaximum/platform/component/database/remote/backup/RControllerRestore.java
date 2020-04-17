package com.infomaximum.platform.component.database.remote.backup;

import com.infomaximum.subsystems.exception.SubsystemException;
import com.infomaximum.subsystems.querypool.QueryRemoteController;

public interface RControllerRestore extends QueryRemoteController {

    void restoreBackup(String backupDirPath, String backupName, String newDbPath) throws SubsystemException;
}
