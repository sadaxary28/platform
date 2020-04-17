package com.infomaximum.platform.component.database.remote.backup;

import com.infomaximum.subsystems.exception.SubsystemException;
import com.infomaximum.subsystems.querypool.QueryRemoteController;

public interface RControllerBackup extends QueryRemoteController {

    void createBackup(String backupDirPath, String backupName) throws SubsystemException;
}
