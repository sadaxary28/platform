package com.infomaximum.platform.update.util;

import com.infomaximum.platform.exception.UpdateTaskDuplicateException;
import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.platform.sdk.component.version.Version;
import com.infomaximum.platform.update.UpdateTask;
import com.infomaximum.platform.update.annotation.Update;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UpdateTaskValidator {

    private void validateUpdates(List<Component> modules) {
        modules.forEach(this::validateUpdates);
    }

    private <T extends UpdateTask<? extends Component>> void validateUpdates(Component module) {
        Set<Class<?>> updateTasks = new Reflections(module.getInfo().getUuid()).getTypesAnnotatedWith(Update.class, true);

        Set<Version> versionSet = new HashSet<>();
        for (Class<?> updateTask : updateTasks) {
            final Update annotationEntity = UpdateUtil.getUpdateAnnotation(updateTask);
            Version version = Version.parse(annotationEntity.version());
            if (versionSet.contains(version)) {
                throw new UpdateTaskDuplicateException(annotationEntity.componentUUID(), annotationEntity.version());
            }
            versionSet.add(version);
        }
    }
}
