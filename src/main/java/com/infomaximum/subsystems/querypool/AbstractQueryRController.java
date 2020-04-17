package com.infomaximum.subsystems.querypool;

import com.infomaximum.cluster.anotation.DisableValidationRemoteMethod;
import com.infomaximum.cluster.core.remote.Remotes;
import com.infomaximum.cluster.core.remote.struct.RController;
import com.infomaximum.cluster.core.remote.utils.RemoteControllerUtils;
import com.infomaximum.cluster.struct.Component;
import com.infomaximum.cluster.utils.EqualsUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class AbstractQueryRController<TComponent extends Component> {

    protected final TComponent component;
    private final ResourceProvider resources;

    private final Map<Class<? extends RController>, Map<String, List<Method>>> hashControllersRemoteMethods;//Хеш методов

    protected AbstractQueryRController(TComponent component, ResourceProvider resources) {
        this.component = component;
        this.resources = resources;

        hashControllersRemoteMethods = new HashMap<>();
        for (Class interfaceClazz : this.getClass().getInterfaces()) {
            if (!RController.class.isAssignableFrom(interfaceClazz)) continue;
            Map<String, List<Method>> hashMethods = new HashMap<>();
            for (Method method : interfaceClazz.getDeclaredMethods()) {

                //Проверяем, что результат и аргументы сериализуемы
                if (!method.isAnnotationPresent(DisableValidationRemoteMethod.class)) {
                    RemoteControllerUtils.validationRemoteMethod(component, interfaceClazz, method);
                }

                //Игнорируем права доступа
                method.setAccessible(true);

                List<Method> methods = hashMethods.get(method.getName());
                if (methods == null) {
                    methods = new ArrayList<>();
                    hashMethods.put(method.getName(), methods);
                }
                methods.add(method);
            }
            hashControllersRemoteMethods.put(interfaceClazz, hashMethods);
        }
    }

    public Method getRemoteMethod(Class<? extends RController> remoteControllerClazz, String name, Class<?>[] parameterTypes) {
        Map<String, List<Method>> hashControllerRemoteMethods = hashControllersRemoteMethods.get(remoteControllerClazz);
        if (hashControllerRemoteMethods == null) return null;

        Method method = null;
        for (Method iMethod : hashControllerRemoteMethods.get(name)) {
            if (iMethod.getParameterCount() != parameterTypes.length) continue;

            boolean equals = true;
            for (int iArg = 0; iArg < parameterTypes.length; iArg++) {
                Class<?> iMethodArg = iMethod.getParameterTypes()[iArg];
                Class<?> methodArg = parameterTypes[iArg];

                //Если null, значит нет возможности сопоставить типы - идем дальше
                if (methodArg == null) continue;

                if (!(EqualsUtils.equalsType(iMethodArg, methodArg) || iMethodArg.isAssignableFrom(methodArg))) {
                    equals = false;
                    break;
                }
            }

            if (equals) {
                method = iMethod;
                break;
            }
        }
        return method;
    }

    public Remotes getRemotes() {
        return component.getRemotes();
    }


}
