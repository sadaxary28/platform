package com.infomaximum.platform.update;

import com.infomaximum.cluster.Cluster;
import com.infomaximum.cluster.exception.ClusterException;
import com.infomaximum.cluster.struct.Info;
import com.infomaximum.database.provider.DBProvider;
import com.infomaximum.platform.sdk.component.Component;

public class TestSubsystem extends Component {

    public static final String UUID = "com.infomaximum.subsystem.testsubsystem";

    public static Info INFO = new Info.Builder(UUID).build();

    public TestSubsystem() {
    }

    @Override
    public Info getInfo() {
        return null;
    }

    @Override
    protected DBProvider initDBProvider() throws ClusterException {
        return null;
    }
}
