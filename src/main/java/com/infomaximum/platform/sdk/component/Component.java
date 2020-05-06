package com.infomaximum.platform.sdk.component;

import com.infomaximum.cluster.Cluster;
import com.infomaximum.cluster.core.service.transport.executor.ExecutorTransport;
import com.infomaximum.cluster.core.service.transport.executor.ExecutorTransportImpl;
import com.infomaximum.cluster.exception.ClusterException;
import com.infomaximum.cluster.graphql.exception.GraphQLExecutorException;
import com.infomaximum.database.anotation.Entity;
import com.infomaximum.database.domainobject.DomainObjectSource;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.exception.runtime.SchemaException;
import com.infomaximum.database.maintenance.ChangeMode;
import com.infomaximum.database.maintenance.SchemaService;
import com.infomaximum.database.provider.DBProvider;
import com.infomaximum.database.schema.Schema;
import com.infomaximum.database.schema.StructEntity;
import com.infomaximum.platform.sdk.dbprovider.ComponentDBProvider;
import com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder;
import com.infomaximum.platform.sdk.remote.QueryRemotes;
import com.infomaximum.platform.sdk.struct.querypool.QuerySystem;
import com.infomaximum.platform.sdk.subscription.GraphQLSubscribeEvent;
import com.infomaximum.subsystems.exception.SubsystemException;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;

public abstract class Component extends com.infomaximum.cluster.struct.Component {

	protected DBProvider dbProvider;
	protected DomainObjectSource domainObjectSource;
	private Schema schema;

	private QueryRemotes queryRemotes;

	private GraphQLSubscribeEvent graphQLSubscribeEvent;

	public Component(Cluster cluster) {
		super(cluster);
	}

	protected DBProvider initDBProvider() throws ClusterException {
		if (dbProvider != null) {
			return dbProvider;
		}
		return new ComponentDBProvider(cluster, this);
	}

	public QuerySystem<Void> onStart() {
        return null;
	}

    public QuerySystem<Void> onStop() {
        return null;
    }

	public void onStarting() throws SubsystemException {
		try {
			Set<StructEntity> domains = new HashSet<>();
			for (Class domainObjectClass : new Reflections(getInfo().getUuid()).getTypesAnnotatedWith(Entity.class, true)) {
				domains.add(Schema.getEntity(domainObjectClass));
			}
			schema.checkSubsystemIntegrity(domains, getInfo().getUuid());
			buildSchemaService()
					.setChangeMode(ChangeMode.CREATION)
					.setValidationMode(false)
					.execute();
		} catch (DatabaseException e) {
			throw GeneralExceptionBuilder.buildDatabaseException(e);
		}

		this.graphQLSubscribeEvent = new GraphQLSubscribeEvent(this);
	}

	public SchemaService buildSchemaService() {
		return new SchemaService(getDbProvider())
				.setNamespace(getInfo().getUuid())
				.setSchema(getSchema());
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

	protected Schema getSchema() {
		return schema;
	}

	public void initialize() throws ClusterException {
		if (!getClass().getPackage().getName().equals(getInfo().getUuid())) {
			throw new RuntimeException(getClass() + " is not correspond to uuid: " + getInfo().getUuid());
		}

		this.dbProvider = initDBProvider();
		this.schema = initializeSchema(dbProvider);

		this.domainObjectSource = new DomainObjectSource(dbProvider);

		this.queryRemotes = new QueryRemotes(this);
	}

	public final QueryRemotes getQueryRemotes() {
		return queryRemotes;
	}

	public final GraphQLSubscribeEvent getGraphQLSubscribeEvent() {
		return graphQLSubscribeEvent;
	}

	@Override
	public void destroying() throws ClusterException {

	}

	private Schema initializeSchema(DBProvider dbProvider) {
		try {
			Schema schema;
			if (Schema.exists(dbProvider)) {
				schema = Schema.read(dbProvider);
			} else {
				schema = Schema.create(dbProvider);
			}
			for (Class domainObjectClass : new Reflections(getInfo().getUuid()).getTypesAnnotatedWith(Entity.class, true)) {
				Schema.resolve(domainObjectClass); //todo убрать resolve когда переведу функционал из StructEntity
			}
			return schema;
		} catch (DatabaseException e) {
			throw new SchemaException(e);
		}
	}


}
