package com.infomaximum.platform.control;

import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.platform.Platform;
import com.infomaximum.platform.component.database.DatabaseComponent;
import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.platform.sdk.context.ContextTransaction;
import com.infomaximum.platform.sdk.context.impl.ContextTransactionImpl;
import com.infomaximum.platform.sdk.context.source.impl.SourceSystemImpl;
import com.infomaximum.platform.sdk.domainobject.module.ModuleEditable;
import com.infomaximum.platform.sdk.struct.querypool.QuerySystem;
import com.infomaximum.subsystems.exception.SubsystemException;
import com.infomaximum.subsystems.querypool.Query;
import com.infomaximum.subsystems.querypool.QueryTransaction;
import com.infomaximum.subsystems.querypool.RemovableResource;
import com.infomaximum.subsystems.querypool.ResourceProvider;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PlatformStartStop {

    private final Platform platform;

    public PlatformStartStop(Platform platform) {
        this.platform = platform;
    }

    /**
     * Запуск происходит в несколько фаз:
     * 1) onStarting - инициализирующая фаза старта компонента
     * 2) onStart - Все необходимые фазы пройденны - пользовательский запуск
     * @throws SubsystemException
     */
    public void start() throws SubsystemException {
        //onStarting
        for (Component component: platform.getCluster().getDependencyOrderedComponentsOf(Component.class)) {
            component.onStarting();
        }

        //onStart
        DatabaseComponent databaseComponent = platform.getCluster().getAnyComponent(DatabaseComponent.class);
        List<QuerySystem<Void>> startQueries = platform.getCluster().getDependencyOrderedComponentsOf(Component.class)
                .stream().map(component -> component.onStart()).filter(query -> query != null)
                .collect(Collectors.toList());
        try {
            platform.getQueryPool().execute(databaseComponent, new Query<Void>() {

                private RemovableResource<ModuleEditable> moduleRemovableResource;

                @Override
                public void prepare(ResourceProvider resources) throws SubsystemException {

                    //TODO Улитин В. Удалить после 01.12.2020
                    moduleRemovableResource = resources.getRemovableResource(ModuleEditable.class);

                    for (QuerySystem<Void> query : startQueries) {
                        query.prepare(resources);
                    }
                }

                @Override
                public Void execute(QueryTransaction transaction) throws SubsystemException {
                    ContextTransaction contextTransaction = new ContextTransactionImpl(new SourceSystemImpl(), transaction);

                    //TODO Улитин В. Удалить после 01.12.2020
                    ModuleEditable moduleEditable = moduleRemovableResource.find(
                            new HashFilter(ModuleEditable.FIELD_UUID, "com.infomaximum.subsystems"),
                            transaction
                    );
                    if (moduleEditable != null) {
                        moduleRemovableResource.remove(moduleEditable, transaction);
                    }


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
