package com.infomaximum.platform.component.database.remote.backup;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;

public interface RControllerBackup extends QueryRemoteController {

    void createBackup(String backupDirPath, String backupName) throws PlatformException;
}
