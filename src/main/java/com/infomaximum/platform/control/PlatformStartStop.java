package com.infomaximum.platform.control;

import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.schema.Schema;
import com.infomaximum.platform.Platform;
import com.infomaximum.platform.component.database.DatabaseComponent;
import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.platform.sdk.component.ComponentType;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.infomaximum.platform.sdk.context.impl.ContextTransactionImpl;
import com.infomaximum.platform.sdk.context.source.impl.SourceSystemImpl;
import com.infomaximum.platform.sdk.domainobject.module.ModuleReadable;
import com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder;
import com.infomaximum.platform.sdk.struct.querypool.QuerySystem;
import com.infomaximum.subsystems.exception.SubsystemException;
import com.infomaximum.subsystems.querypool.Query;
import com.infomaximum.subsystems.querypool.QueryTransaction;
import com.infomaximum.subsystems.querypool.ResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PlatformStartStop {

    private final static Logger log = LoggerFactory.getLogger(PlatformStartStop.class);

    private final Platform platform;

    public PlatformStartStop(Platform platform) {
        this.platform = platform;
    }

    /**
     * Запуск происходит в несколько фаз:
     * 1) onStarting - инициализирующая фаза старта компонента
     * 2) onStart - Все необходимые фазы пройденны - пользовательский запуск
     *
     * checkUpgrade - флаг указывающий что старт "урезанный", для проверки обновления
     * @throws SubsystemException
     */
    public void start(boolean checkUpgrade) throws SubsystemException {
        //initialize
        for (Component component : platform.getCluster().getDependencyOrderedComponentsOf(Component.class)) {
            if (component.getDbProvider() == null) {
                component.initialize();
            }
        }

        //Инициализируем ModuleReadable
        try {
            DatabaseComponent databaseSubsystem = platform.getCluster().getAnyComponent(DatabaseComponent.class);
            Schema schema = Schema.read(databaseSubsystem.getRocksDBProvider());
            log.warn("Schema on start: " + schema.getDbSchema().toTablesJsonString());
            Schema.resolve(ModuleReadable.class);
        } catch (DatabaseException e) {
            throw GeneralExceptionBuilder.buildDatabaseException(e);
        }

        //TODO Ulitin V. - где то потерялась валидация схемы база данных!!!

        //onStarting
        for (Component component : platform.getCluster().getDependencyOrderedComponentsOf(Component.class)) {
            component.onStarting();
        }


        //Режим обновления, упрощенный режим старт/стоп
        if (checkUpgrade) {
            return;
        }

        //onStart
        DatabaseComponent databaseComponent = platform.getCluster().getAnyComponent(DatabaseComponent.class);
        List<QuerySystem<Void>> startQueries = platform.getCluster().getDependencyOrderedComponentsOf(Component.class)
                .stream()
                .sorted((o1, o2) -> {//Необходимо, что бы фронт запустился самым последним
                    if (o1.getType() == ComponentType.FRONTEND) {
                        return 1;
                    } else if (o2.getType() == ComponentType.FRONTEND) {
                        return -1;
                    } else {
                        return 0;
                    }
                })
                .map(component -> component.onStart())
                .filter(query -> query != null)
                .collect(Collectors.toList());
        try {
            platform.getQueryPool().execute(databaseComponent, new Query<Void>() {

                @Override
                public void prepare(ResourceProvider resources) throws SubsystemException {
                    for (QuerySystem<Void> query : startQueries) {
                        query.prepare(resources);
                    }
                }

                @Override
                public Void execute(QueryTransaction transaction) throws SubsystemException {
                    ContextTransaction contextTransaction = new ContextTransactionImpl(new SourceSystemImpl(), transaction);
                    for (QuerySystem<Void> query : startQueries) {
                        query.execute(contextTransaction);
                    }
                    return null;
                }
            }).get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof SubsystemException) {
                throw (SubsystemException) cause;
            } else {
                throw new RuntimeException(e);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() throws SubsystemException {

    }
}
