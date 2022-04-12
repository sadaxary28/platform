package com.infomaximum.platform.update.util;

import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.platform.sdk.component.version.Version;
import com.infomaximum.platform.update.UpdateTask;
import com.infomaximum.platform.update.annotation.Dependency;
import com.infomaximum.platform.update.annotation.Update;
import com.infomaximum.platform.update.core.ModuleUpdateEntity;
import com.infomaximum.platform.update.exception.UpdateException;
import org.reflections.Reflections;

import java.util.*;
import java.util.stream.Collectors;

public class UpdateUtil {

    private final static Version START_VERSION = new Version(1, 0, 0, 0);

    @SuppressWarnings("unchecked")
    public static <T extends UpdateTask<? extends Component>> Update getUpdateAnnotation(Class<T> clazz) {
        while (!clazz.isAnnotationPresent(Update.class)) {
            if (!UpdateTask.class.isAssignableFrom(clazz.getSuperclass())) {
                throw new UpdateException("Not found " + Update.class + " annotation in " + clazz + ".");
            }
            clazz = (Class<T>) clazz.getSuperclass();
        }
        return clazz.getAnnotation(Update.class);
    }

    public static <T extends Component> void checkIntegrity(Set<Class<? extends UpdateTask<T>>> tasks) {
        if (tasks.isEmpty()) {
            return;
        }
        Set<Version> previousVersions = new HashSet<>();
        Set<Version> nextVersions = new HashSet<>();
        String componentUuid = null;
        for (Class<? extends UpdateTask<T>> task : tasks) {
            final Update annotationEntity = UpdateUtil.getUpdateAnnotation(task);
            if (componentUuid == null) {
                componentUuid = annotationEntity.componentUUID();
            }
            if (!componentUuid.equals(annotationEntity.componentUUID())) {
                throw new UpdateException("Subsystem uuid of task set isn't same");
            }
            Version prevVersion = parseVersion(annotationEntity.previousVersion());
            Version nextVersion = parseVersion(annotationEntity.version());
            if (Version.compare(prevVersion, nextVersion) != -1) {
                throw new UpdateException("Integrity error. Update version: " + nextVersion + " is less or equal to previous: " + prevVersion);
            }
            if (!previousVersions.add(prevVersion)) {
                throw new UpdateException("Integrity error. Previous update version is already exist: " + prevVersion);
            }
            if (!nextVersions.add(nextVersion)) {
                throw new UpdateException("Integrity error. Current update version is already exist: " + nextVersion);
            }
        }
        for (Version previousVersion : previousVersions) {
            if (!nextVersions.remove(previousVersion) && !previousVersion.equals(START_VERSION)) {
                throw new UpdateException("Integrity error. Can't find previous update version: " + previousVersion);
            }
        }
        if (nextVersions.size() > 1) {
            throw new UpdateException("Integrity error. Can't find next update versions: " + nextVersions);
        }
    }


    //todo V.Bukharkin нужно покрыть тестами
    public static List<UpdateTask<? extends Component>> getUpdatesInCorrectOrder(ModuleUpdateEntity[] updates) {
        checkUniqueModule(updates);
        List<Module> result = new ArrayList<>(updates.length);
        Map<String, ModuleUpdateEntity> subsystemUuids = Arrays.stream(updates).collect(Collectors.toMap(ModuleUpdateEntity::getComponentUuid, mu -> mu));
        Set<String> passedMds = new HashSet<>();
        for (ModuleUpdateEntity update : updates) {
            if (!passedMds.contains(update.getComponentUuid())){
                buildModuleDependency(update, subsystemUuids, passedMds, result);
            }
        }
        return result.stream().map(Module::getUpdateTask).collect(Collectors.toList());
    }

    public static <T extends Component> UpdateTask<T> getUpdateTaskObj(Version oldVersion, Version newVersion, T subsystem) {
        Class<UpdateTask<T>> updateTaskClass = getUpdateTaskClass(oldVersion, newVersion, subsystem);
        return getUpdateTaskObj(updateTaskClass, subsystem);
    }

    private static void buildModuleDependency(ModuleUpdateEntity update, Map<String, ModuleUpdateEntity> subsystemUpdates, Set<String> passedMds, List<Module> result) {
        if (passedMds.contains(update.getComponentUuid())) {
            return;
        }
        Class<UpdateTask<Component>> updateTaskClass = getUpdateTaskClass(update.getOldVersion(), update.getNewVersion(), update.getComponent());
        final Update annotationEntity = UpdateUtil.getUpdateAnnotation(updateTaskClass);
        UpdateTask<? extends Component> updateTask = getUpdateTaskObj(updateTaskClass, update.getComponent());
        if (annotationEntity.dependencies().length != 0) {
            Set<String> notCyclicDependencies = new HashSet<>();
            notCyclicDependencies.add(update.getComponentUuid());
            for (Dependency dependency : annotationEntity.dependencies()) {
                if (notCyclicDependencies.contains(dependency.componentUUID())) {
                    throw new UpdateException("Cyclic dependency error. Dependency: " + dependency.componentUUID());
                }
                if (!subsystemUpdates.containsKey(dependency.componentUUID())) {
                    continue;
                }
                notCyclicDependencies.add(dependency.componentUUID());
                buildModuleDependency(subsystemUpdates.get(dependency.componentUUID()), subsystemUpdates, passedMds, result);
            }
        }
        Module md = new Module(updateTask, update.getComponentUuid());
        result.add(md);
        passedMds.add(update.getComponentUuid());
    }

    private static void checkUniqueModule(ModuleUpdateEntity[] updates) {
        for (int i = 0; i < updates.length; i++) {
            for (int j = i+1; j < updates.length; j++) {
                if (updates[i].getComponentUuid().equals(updates[j].getComponentUuid())) {
                    throw new UpdateException("Updates have duplicate module: " + updates[i].getComponentUuid());
                }
            }
        }
    }

    private static <T extends Component> UpdateTask<T> getUpdateTaskObj(Class<UpdateTask<T>> updateTaskClass, Component component) {
        try {
            return updateTaskClass.getConstructor(component.getClass()).newInstance(component);
        } catch (ReflectiveOperationException e) {
            throw new UpdateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Component> Class<UpdateTask<T>> getUpdateTaskClass(Version oldVersion, Version newVersion, T component) {
        for (Class updateTask : new Reflections(component.getInfo().getUuid()).getTypesAnnotatedWith(Update.class, true)) {
            final Update annotationEntity = UpdateUtil.getUpdateAnnotation(updateTask);
            if (Version.parse(annotationEntity.previousVersion()).equals(oldVersion)
                    && Version.parse(annotationEntity.version()).equals(newVersion)) {
                return updateTask;
            }
        }
        throw new UpdateException("Can't find update task "+ oldVersion + "->" + newVersion + " for " + component);
    }

    public static Version parseVersion(String source) throws IllegalArgumentException {
        String[] parts = source.split("\\.");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Version string must be contains 4 parts: " + source);
        }
        if (!"x".equals(parts[3])) {
            throw new IllegalArgumentException("In version string field patch not equal 'x': " + source);
        }

        return new Version(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]),
                0
        );
    }

    private static class Module {
        private final UpdateTask<? extends Component> updateTask;
        private final String componentUuid;

        private Module(UpdateTask<? extends Component> updateTask, String componentUuid) {
            this.updateTask = updateTask;
            this.componentUuid = componentUuid;
        }

        public UpdateTask<? extends Component> getUpdateTask() {
            return updateTask;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Module md = (Module) o;
            return Objects.equals(componentUuid, md.componentUuid);
        }

        @Override
        public int hashCode() {
            return Objects.hash(componentUuid);
        }
    }
}
