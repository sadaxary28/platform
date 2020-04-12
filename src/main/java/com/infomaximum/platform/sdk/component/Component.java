package com.infomaximum.platform.sdk.component;

import com.infomaximum.cluster.Cluster;
import com.infomaximum.cluster.core.service.transport.executor.ExecutorTransport;
import com.infomaximum.cluster.core.service.transport.executor.ExecutorTransportImpl;
import com.infomaximum.cluster.exception.ClusterException;
import com.infomaximum.cluster.graphql.exception.GraphQLExecutorException;
import com.infomaximum.database.anotation.Entity;
import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.DomainObjectSource;
import com.infomaximum.database.domainobject.Transaction;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.exception.runtime.SchemaException;
import com.infomaximum.database.maintenance.SchemaService;
import com.infomaximum.database.provider.DBProvider;
import com.infomaximum.platform.sdk.dbprovider.ComponentDBProvider;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

public abstract class Component extends com.infomaximum.cluster.struct.Component {

	protected DBProvider dbProvider;
	protected DomainObjectSource domainObjectSource;

	public Component(Cluster cluster) {
		super(cluster);
	}

//	@Override
//	public Info getInfo() {
//		return null;
//	}

	protected DBProvider initDBProvider() throws ClusterException {
		if (dbProvider != null) {
			return dbProvider;
		}
		return new ComponentDBProvider(cluster, this);
	}

	public void install() throws Exception {
		dbProvider = initDBProvider();
		domainObjectSource = new DomainObjectSource(dbProvider);
		Set<Class<? extends DomainObject>> objects = new HashSet<>();
		for (Class domainObjectClass : new Reflections(getInfo().getUuid()).getTypesAnnotatedWith(Entity.class, true)) {
			objects.add(domainObjectClass);
		}
		try {
			SchemaService.install(objects, dbProvider);
		} catch (DatabaseException e) {
			throw new SchemaException(e);
		}
	}

	public void install(Transaction transaction) throws DatabaseException {

	}

	public void start() throws Exception {

	}

	public void onStarting() throws Exception {

	}

	@Override
	public ExecutorTransport initExecutorTransport() throws ClusterException {
		try {
			return new ExecutorTransportImpl.Builder(this)
					.build();
		} catch (GraphQLExecutorException e) {
			throw new ClusterException(e);
		}
	}

	public final DBProvider getDbProvider() {
		return dbProvider;
	}

	public final DomainObjectSource getDomainObjectSource() {
		return domainObjectSource;
	}

	public void initialize() throws ClusterException {
		if (!getClass().getPackage().getName().equals(getInfo().getUuid())) {
			throw new RuntimeException(getClass() + " is not correspond to uuid: " + getInfo().getUuid());
		}

		this.dbProvider = initDBProvider();
	}

	@Override
	public void destroying() throws ClusterException {

	}
}
