package com.infomaximum.platform.update;

import com.infomaximum.cluster.Cluster;
import com.infomaximum.cluster.exception.ClusterException;
import com.infomaximum.cluster.struct.Info;
import com.infomaximum.database.provider.DBProvider;
import com.infomaximum.platform.sdk.component.Component;

public class OtherTestSubsystem extends Component {

    public static final String UUID = "com.infomaximum.subsystem.other_testsubsystem";

    public static Info INFO = new Info.Builder(UUID).build();

    public OtherTestSubsystem(Cluster cluster) {
        super(cluster);
    }

    @Override
    public Info getInfo() {
        return null;
    }

    public OtherTestSubsystem() {
        super(null);
    }

    @Override
    protected DBProvider initDBProvider() throws ClusterException {
        return null;
    }
}
