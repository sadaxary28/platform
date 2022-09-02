package com.infomaximum.platform.sdk.remote.excludeintegrity;

import com.infomaximum.cluster.core.remote.struct.RController;

import java.util.ArrayList;
import java.util.HashMap;

@Deprecated
public interface RControllerExcludeIntegrityTable extends RController {
    HashMap<String, ArrayList<String>> getExcludeIntegrityTables();
}