package com.infomaximum.platform.component.database;

import com.infomaximum.cluster.Cluster;
import com.infomaximum.cluster.exception.ClusterException;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.provider.DBProvider;
import com.infomaximum.platform.Platform;
import com.infomaximum.platform.component.database.utils.DatabaseUtils;
import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.platform.sdk.component.Info;
import com.infomaximum.rocksdb.RocksDBProvider;
import com.infomaximum.rocksdb.RocksDataBaseBuilder;

public class DatabaseComponent extends Component {

	public static final Info INFO = (Info) new Info.Builder(DatabaseConsts.UUID, null)
			.withComponentClass(DatabaseComponent.class)
			.build();

	private volatile RocksDBProvider dbProvider;

	private DatabaseComponentExtension extension;

	public DatabaseComponent(Cluster cluster) {
		super(cluster);
	}

	@Override
	public Info getInfo() {
		return INFO;
	}

	@Override
	protected String generateKey() {
		return DatabaseUtils.getDefaultKey();
	}

	@Override
	protected DBProvider initDBProvider() throws ClusterException {
		if (dbProvider != null) {
			return dbProvider;
		}
		try {
			dbProvider = new RocksDataBaseBuilder()
					.withPath(Platform.get().getDatabaseConfigure().dbPath)
					.build();
			return dbProvider;
		} catch (DatabaseException e) {
			throw new ClusterException(e);
		}
	}

	public void start() throws Exception {
		super.start();

		this.extension = Platform.get().getDatabaseConfigure().extension;
		if (extension != null) {
			extension.initialize(this);
		}
	}


	public RocksDBProvider getRocksDBProvider() {
		return dbProvider;
	}

	@Override
	public void destroying() {
		dbProvider.close();
	}
}
