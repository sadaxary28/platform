package com.infomaximum.platform.component.database.remote.backup;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.QueryRemoteController;

public interface RControllerRestore extends QueryRemoteController {

    void restoreBackup(String backupDirPath, String backupName, String newDbPath) throws PlatformException;
}
