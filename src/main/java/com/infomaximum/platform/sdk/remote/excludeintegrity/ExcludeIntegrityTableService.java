package com.infomaximum.platform.sdk.remote.excludeintegrity;

import com.infomaximum.platform.sdk.component.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExcludeIntegrityTableService {

    private final Collection<RControllerExcludeIntegrityTable> rControllerExcludeIntegrityTables;

    public ExcludeIntegrityTableService(Component component) {
        rControllerExcludeIntegrityTables = component.getRemotes().getControllers(RControllerExcludeIntegrityTable.class);
    }

    public void initExcludeIntegrityTables(HashMap<String, ArrayList<String>> destination) {
        for (RControllerExcludeIntegrityTable controller : rControllerExcludeIntegrityTables) {
            final HashMap<String, ArrayList<String>> excludeIntegrityTables = controller.getExcludeIntegrityTables();
            checkProcessedTable(excludeIntegrityTables);
            destination.putAll(excludeIntegrityTables);
        }
    }

    private void checkProcessedTable(HashMap<String, ArrayList<String>> excludeIntegrityTables) {
        final Set<String> processedTables = getListProcessedTables();
        for (Map.Entry<String, ArrayList<String>> entry : excludeIntegrityTables.entrySet()) {
            final String subsystemUUID = entry.getKey();
            for (String tableName : entry.getValue()) {
                final String domainTableName = subsystemUUID.concat(".").concat(tableName);
                if (!processedTables.contains(domainTableName)) {
                    throw new RuntimeException(String.format(
                            """
                                    Domain table: "%s" not contains in list of possible tables [%s],
                                     please fix structure for domain table: "%s" """
                            , domainTableName
                            , processedTables.stream()
                                    .map(s -> "\"".concat(s).concat("\""))
                                    .collect(Collectors.joining(","))
                            , domainTableName
                    ));
                }
            }
        }
    }

    public Set<String> getListProcessedTables() {
        return Stream.of(
                "com.infomaximum.subsystem.workspaces. WorkspaceApiKey",
                "com.infomaximum.subsystem.workspaces. WorkspaceEmployee",
                "com.infomaximum.subsystem.activedirectory.AdDomain",
                "com.infomaximum.subsystem.dashboard.Sheet",
                "com.infomaximum.subsystem.dashboard.DisplayCondition",
                "com.infomaximum.subsystem.dashboard.ComponentVariable",
                "com.infomaximum.subsystem.dashboard.TableComponentSetting",
                "com.infomaximum.subsystem.dashboard.DynamicDimension",
                "com.infomaximum.subsystem.dashboard.EntityMeta",
                "com.infomaximum.subsystem.dashboard.Sorting"
        ).collect(Collectors.toSet());
    }
}