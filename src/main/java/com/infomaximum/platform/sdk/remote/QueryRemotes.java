package com.infomaximum.platform.sdk.remote;

import com.infomaximum.cluster.core.component.RuntimeComponentInfo;
import com.infomaximum.cluster.core.remote.utils.RemoteControllerAnalysis;
import com.infomaximum.platform.Platform;
import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.subsystems.querypool.QueryRemoteController;
import com.infomaximum.subsystems.querypool.ResourceProvider;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class QueryRemotes {

    private final Component component;

    private final Map<Class<? extends QueryRemoteController>, Constructor> queryRemoteControllers;

    public QueryRemotes(Component component) {
        this.component = component;

        //Собираем remoteController'ы
        try {
            this.queryRemoteControllers = new HashMap<>();
            for (Class<? extends QueryRemoteController> classRemoteController : new Reflections(component.getInfo().getUuid()).getSubTypesOf(QueryRemoteController.class)) {
                if (classRemoteController.isInterface()) {
                    //Валидируем интерфейс
                    new RemoteControllerAnalysis(component, classRemoteController);
                } else {
                    //Собираем реализации
                    for (Class interfaceClazz : classRemoteController.getInterfaces()) {
                        if (!QueryRemoteController.class.isAssignableFrom(interfaceClazz)) continue;

                        Constructor constructor = classRemoteController.getConstructor(component.getClass(), ResourceProvider.class);
                        constructor.setAccessible(true);

                        if (queryRemoteControllers.putIfAbsent(interfaceClazz, constructor) != null) {
                            throw new RuntimeException("Конфликт реализации, интерфейс " + interfaceClazz + " имеет дублирующие реализации");
                        }
                    }
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }


    //TODO Ulitin V. переписать на удаленые вызовы
    public <T extends QueryRemoteController> Set<T> getControllers(ResourceProvider resourceProvider, Class<T> remoteControllerClass) {
        if (!remoteControllerClass.isInterface()) throw new IllegalArgumentException("Class " + remoteControllerClass + " is not interface");

        try {
            Set<T> controllers = new HashSet<>();
            for (RuntimeComponentInfo componentInfo : component.getEnvironmentComponents().getActiveComponents()) {

                com.infomaximum.cluster.struct.Component iComponent = Platform.get().getCluster().getAnyComponent(componentInfo.info.getUuid());
                if (!Component.class.isAssignableFrom(iComponent.getClass())) continue;

                Constructor constructor = ((Component)iComponent).getQueryRemotes().queryRemoteControllers.get(remoteControllerClass);
                if (constructor == null) continue;

                T iQueryRemoteController = (T) constructor.newInstance(iComponent, resourceProvider);
                controllers.add(iQueryRemoteController);
            }
            return controllers;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO Ulitin V. переписать на удаленые вызовы
    public <T extends QueryRemoteController> T getController(ResourceProvider resourceProvider, Class<? extends Component> componentClass, Class<T> remoteControllerClass) {
        if (!remoteControllerClass.isInterface()) throw new IllegalArgumentException("Class " + remoteControllerClass + " is not interface");

        Component remoteComponent = Platform.get().getCluster().getAnyComponent(componentClass);
        Constructor constructor = remoteComponent.getQueryRemotes().queryRemoteControllers.get(remoteControllerClass);
        if (constructor == null) throw new RuntimeException("Implements: " + remoteControllerClass + " in component: " + componentClass + " not found");

        try {
            return (T) constructor.newInstance(remoteComponent, resourceProvider);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO Ulitin V. переписать на удаленые вызовы
    public <T extends QueryRemoteController> T getController(ResourceProvider resourceProvider, String componentUuid, Class<T> remoteControllerClass) {
        if (!remoteControllerClass.isInterface()) throw new IllegalArgumentException("Class " + remoteControllerClass + " is not interface");

        com.infomaximum.platform.sdk.component.Component remoteComponent = (com.infomaximum.platform.sdk.component.Component) Platform.get().getCluster().getAnyComponent(componentUuid);

        Constructor constructor = remoteComponent.getQueryRemotes().queryRemoteControllers.get(remoteControllerClass);

        if (constructor == null) {
            throw new RuntimeException("Implements: " + remoteControllerClass + " in subsystem: " + remoteComponent.getClass() + " not found");
        }

        try {
            return (T) constructor.newInstance(remoteComponent, resourceProvider);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO Ulitin V. переписать на удаленые вызовы
    public <T extends QueryRemoteController> boolean isController(String componentUuid, Class<T> remoteControllerClass) {
        if (!remoteControllerClass.isInterface()) throw new IllegalArgumentException("Class " + remoteControllerClass + " is not interface");

        com.infomaximum.platform.sdk.component.Component remoteComponent = (com.infomaximum.platform.sdk.component.Component) Platform.get().getCluster().getAnyComponent(componentUuid);

        return remoteComponent.getQueryRemotes().queryRemoteControllers.containsKey(remoteControllerClass);
    }
}
