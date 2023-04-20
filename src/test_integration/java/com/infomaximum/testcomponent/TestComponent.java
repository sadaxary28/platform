package com.infomaximum.testcomponent;

import com.infomaximum.cluster.Cluster;
import com.infomaximum.cluster.exception.ClusterException;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.provider.DBProvider;
import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.platform.sdk.component.Info;
import com.infomaximum.rocksdb.RocksDBProvider;
import com.infomaximum.rocksdb.RocksDataBaseBuilder;
import com.infomaximum.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestComponent extends Component {

    public static final String UUID = "com.infomaximum.testcomponent";

    public static final Info INFO = (Info) new Info.Builder(TestComponent.UUID, null)
            .withComponentClass(TestComponent.class)
            .build();

    private volatile RocksDBProvider dbProvider;

    private Path pathDatabase;

    public TestComponent() { }

    @Override
    public Info getInfo() {
        return INFO;
    }

    @Override
    protected DBProvider initDBProvider() throws ClusterException {
        if (dbProvider != null) {
            return dbProvider;
        }
        try {
            pathDatabase = Files.createTempDirectory("database-test");

            dbProvider = new RocksDataBaseBuilder()
                    .withPath(pathDatabase)
                    .build();
            return dbProvider;
        } catch (IOException e) {
            throw new ClusterException(e);
        } catch (DatabaseException e) {
            throw new ClusterException(e);
        }
    }

    @Override
    public void onDestroy() {
        if (pathDatabase != null) {
            try {
                FileUtils.deleteDirectoryIfExists(pathDatabase);
            } catch (IOException e) {
                throw new ClusterException(e);
            }
        }
    }
}
