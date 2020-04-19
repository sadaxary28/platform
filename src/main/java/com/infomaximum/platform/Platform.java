package com.infomaximum.platform;

import com.infomaximum.cluster.Cluster;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.platform.component.database.configure.DatabaseConfigure;
import com.infomaximum.platform.upgrade.PlatformUpgrade;
import com.infomaximum.subsystems.exception.SubsystemException;
import com.infomaximum.subsystems.querypool.QueryPool;
import com.infomaximum.subsystems.utils.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Platform implements AutoCloseable {

	public static final String UUID = Platform.class.getPackage().getName();
	public static final Version VERSION = new Version(0, 0, 1);
	
	private final static Logger log = LoggerFactory.getLogger(Platform.class);

	private static volatile Platform instant;

	private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

	private final DatabaseConfigure databaseConfigure;

	private final Cluster cluster;
	private final QueryPool queryPool;

	private Platform(Builder builder) {
		synchronized (Platform.class) {
			if (instant != null) throw new IllegalStateException();

			this.uncaughtExceptionHandler = builder.uncaughtExceptionHandler;
			this.databaseConfigure = builder.databaseConfigure;
			this.cluster = builder.clusterBuilder.build();
			this.queryPool = new QueryPool(builder.uncaughtExceptionHandler);

			instant = this;
		}
	}

	public void install() throws SubsystemException {
        new PlatformUpgrade(this).install();
	}


	public void upgrade() throws DatabaseException {
        new PlatformUpgrade(this).upgrade();
	}

	public Thread.UncaughtExceptionHandler getUncaughtExceptionHandler() {
		return uncaughtExceptionHandler;
	}

	public DatabaseConfigure getDatabaseConfigure() {
		return databaseConfigure;
	}

	public Cluster getCluster() {
		return cluster;
	}

	public QueryPool getQueryPool() {
		return queryPool;
	}

	@Override
	public void close() {
		try {
			// дождемся конца всех работ с БД, иначе может быть креш в RocksDB
			queryPool.shutdownAwait();
		} catch (InterruptedException ignore) {}
		cluster.close();
		instant = null;
	}


	public static Platform get() {
		return instant;
	}

	public static class Builder {

		private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

		private DatabaseConfigure databaseConfigure;

		private Cluster.Builder clusterBuilder;

		public Builder() {

			//default configure
			this.uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
				@Override
				public void uncaughtException(Thread t, Throwable e) {
					log.error("Uncaught exception", e);
				}
			};

			this.clusterBuilder = new Cluster.Builder();
		}

		public Builder withConfig(DatabaseConfigure databaseConfigure) {
			this.databaseConfigure = databaseConfigure;
			return this;
		}

		public Builder withClusterBuilder(Cluster.Builder clusterBuilder) {
			this.clusterBuilder = clusterBuilder;
			return this;
		}

		public Builder withUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
			this.uncaughtExceptionHandler = uncaughtExceptionHandler;
			return this;
		}

		public Platform build() {
			return new Platform(this);
		}
	}
}
