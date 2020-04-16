package com.infomaximum.platform;

import com.infomaximum.cluster.Cluster;
import com.infomaximum.platform.component.database.configure.DatabaseConfigure;

public class Platform implements AutoCloseable {

	private static volatile Platform instant;

	private final DatabaseConfigure databaseConfigure;
	private final Cluster cluster;

	private Platform(Builder builder) {
		synchronized (Platform.class) {
			if (instant != null) throw new IllegalStateException();

			this.databaseConfigure = builder.databaseConfigure;
			this.cluster = builder.clusterBuilder.build();

			instant = this;
		}
	}

	public DatabaseConfigure getDatabaseConfigure() {
		return databaseConfigure;
	}

	public Cluster getCluster() {
		return cluster;
	}

	@Override
	public void close() {
		cluster.close();
		instant = null;
	}


	public static Platform get() {
		return instant;
	}

	public static class Builder {

		private DatabaseConfigure databaseConfigure;

		private Cluster.Builder clusterBuilder;

		public Builder() {
			clusterBuilder = new Cluster.Builder();
		}

		public Builder withConfig(DatabaseConfigure databaseConfigure) {
			this.databaseConfigure = databaseConfigure;
			return this;
		}

		public Builder withClusterBuilder(Cluster.Builder clusterBuilder) {
			this.clusterBuilder = clusterBuilder;
			return this;
		}

		public Platform build() {
			return new Platform(this);
		}
	}
}
