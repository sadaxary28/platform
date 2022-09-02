package com.infomaximum.platform.sdk.remote.excludeintegrity;

import com.infomaximum.platform.sdk.component.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ExcludeIntegrityTableService {

    private final Collection<RControllerExcludeIntegrityTable> rControllerExcludeIntegrityTables;

    public ExcludeIntegrityTableService(Component component) {
        rControllerExcludeIntegrityTables = component.getRemotes().getControllers(RControllerExcludeIntegrityTable.class);
    }

    public void initExcludeIntegrityTables(HashMap<String, ArrayList<String>> destination) {
        for (RControllerExcludeIntegrityTable controller : rControllerExcludeIntegrityTables) {
            destination.putAll(controller.getExcludeIntegrityTables());
        }
    }
}