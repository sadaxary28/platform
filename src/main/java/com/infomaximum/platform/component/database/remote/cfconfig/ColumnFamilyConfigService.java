package com.infomaximum.platform.component.database.remote.cfconfig;

import com.infomaximum.platform.component.database.DatabaseComponent;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.rocksdb.options.columnfamily.ColumnFamilyConfig;

import java.util.Collection;
import java.util.HashMap;

public class ColumnFamilyConfigService {

    private final Collection<RControllerColumnFamilyConfig> rControllerColumnFamilyConfigs;

    public ColumnFamilyConfigService(DatabaseComponent databaseComponent) {
        rControllerColumnFamilyConfigs = databaseComponent.getRemotes().getControllers(RControllerColumnFamilyConfig.class);
    }

    public HashMap<String, ColumnFamilyConfig> getConfigs() throws PlatformException {
        final HashMap<String, ColumnFamilyConfig> columnConfigs = new HashMap<>();
        for (RControllerColumnFamilyConfig controller : rControllerColumnFamilyConfigs) {
            columnConfigs.putAll(controller.getConfiguredColumns());
        }
        return columnConfigs;
    }
}