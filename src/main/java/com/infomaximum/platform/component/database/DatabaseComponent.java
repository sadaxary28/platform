package com.infomaximum.platform.component.database;

import com.infomaximum.cluster.Cluster;
import com.infomaximum.cluster.exception.ClusterException;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.provider.DBProvider;
import com.infomaximum.platform.Platform;
import com.infomaximum.platform.component.database.configure.DatabaseConfigure;
import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.component.database.remote.cfconfig.ColumnFamilyConfigService;
import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.platform.sdk.component.Info;
import com.infomaximum.rocksdb.RocksDBProvider;
import com.infomaximum.rocksdb.RocksDataBaseBuilder;
import com.infomaximum.rocksdb.options.columnfamily.ColumnFamilyConfig;

import java.util.HashMap;

@com.infomaximum.cluster.anotation.Info(uuid = DatabaseConsts.UUID)
public class DatabaseComponent extends Component {

    private final DatabaseConfigure databaseConfigure;
    private volatile RocksDBProvider dbProvider;

//    private DatabaseComponentExtension extension;

    public DatabaseComponent(DatabaseConfigure databaseConfigure) {
        this.databaseConfigure = databaseConfigure;
    }

    @Override
    protected DBProvider initDBProvider() throws ClusterException {
        if (dbProvider != null) {
            return dbProvider;
        }
        try {
            final HashMap<String, ColumnFamilyConfig> configuredColumnFamilies = new ColumnFamilyConfigService(this).getConfigs();
            dbProvider = new RocksDataBaseBuilder()
                    .withPath(databaseConfigure.dbPath)
                    .withConfigColumnFamilies(configuredColumnFamilies)
                    .build();
            return dbProvider;
        } catch (DatabaseException e) {
            throw new ClusterException(e);
        }
    }

    public void onStarting() throws PlatformException {
        super.onStarting();

        DatabaseComponentExtension extension = databaseConfigure.extension;
        if (extension != null) {
            extension.initialize(this);
        }
    }

    public RocksDBProvider getRocksDBProvider() {
        return dbProvider;
    }

    public DatabaseConfigure getDatabaseConfigure() {
        return databaseConfigure;
    }

    @Override
    public final void destroy() {
        if (dbProvider != null) {
            dbProvider.close();
        }
        super.destroy();
    }
}
