package com.infomaximum.platform.component.database.remote.cfconfig;

import com.infomaximum.cluster.core.remote.struct.RController;
import com.infomaximum.rocksdb.options.columnfamily.ColumnFamilyConfig;

import java.util.HashMap;

public interface RControllerColumnFamilyConfig extends RController {
    HashMap<String, ColumnFamilyConfig> getConfiguredColumns();
}