package com.infomaximum.platform.upgrade;

import com.infomaximum.database.anotation.Entity;
import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.DomainObjectSource;
import com.infomaximum.database.domainobject.Transaction;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.exception.runtime.SchemaException;
import com.infomaximum.database.maintenance.SchemaService;
import com.infomaximum.database.provider.DBProvider;
import com.infomaximum.database.schema.Schema;
import com.infomaximum.database.schema.StructEntity;
import com.infomaximum.platform.Platform;
import com.infomaximum.platform.component.database.DatabaseComponent;
import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.platform.sdk.component.Info;
import com.infomaximum.platform.sdk.domainobject.module.ModuleEditable;
import com.infomaximum.platform.sdk.domainobject.module.ModuleReadable;
import com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder;
import com.infomaximum.subsystems.exception.SubsystemException;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlatformUpgrade {

	private final Platform platform;

	public PlatformUpgrade(Platform platform) {
		this.platform = platform;
	}

	public void install() throws SubsystemException {
		try {
			DatabaseComponent databaseSubsystem = platform.getCluster().getAnyComponent(DatabaseComponent.class);
            databaseSubsystem.initialize();
			DBProvider provider = databaseSubsystem.getRocksDBProvider();
			Schema schema = Schema.read(provider);

			new DomainObjectSource(databaseSubsystem.getRocksDBProvider()).executeTransactional(transaction -> {
				schema.createTable(new StructEntity(ModuleReadable.class));

				//Регистрируем и установливаем модули
                List<Component> components = platform.getCluster().getDependencyOrderedComponentsOf(Component.class);
                for (Component component : components) {
                    installComponent(component, transaction);
                }
			});

		} catch (DatabaseException e) {
			throw GeneralExceptionBuilder.buildDatabaseException(e);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}


	public void upgrade() throws DatabaseException {
//		DatabaseComponent databaseSubsystem = platform.getCluster().getAnyComponent(DatabaseComponent.class);
//		DBProvider provider = databaseSubsystem.getRocksDBProvider();
//		Schema schema = Schema.read(provider);
	}

	private void installComponent(Component component, Transaction transaction) throws DatabaseException {
		if (!(component.getInfo() instanceof Info)) {
			return;
		}

		Info info = (Info) component.getInfo();
		if (info.getVersion() == null) {
			return;
		}

		//Регистрируем компонент
		ModuleEditable moduleEditable = transaction.create(ModuleEditable.class);
		moduleEditable.setUuid(info.getUuid());
		moduleEditable.setVersion(info.getVersion());
		transaction.save(moduleEditable);

		//Создаем доменные сущности
	    Set<Class<? extends DomainObject>> objects = new HashSet<>();
        for (Class domainObjectClass : new Reflections(info.getUuid()).getTypesAnnotatedWith(Entity.class, true)) {
            objects.add(domainObjectClass);
        }
        try {
            SchemaService.install(objects, transaction.getDbProvider());
        } catch (DatabaseException e) {
            throw new SchemaException(e);
        }
	}
}
