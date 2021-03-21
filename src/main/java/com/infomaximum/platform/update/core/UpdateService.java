package com.infomaximum.platform.update.core;

import com.infomaximum.database.anotation.Entity;
import com.infomaximum.database.domainobject.Transaction;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.schema.Schema;
import com.infomaximum.database.schema.StructEntity;
import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.platform.sdk.component.version.Version;
import com.infomaximum.platform.sdk.domainobject.module.ModuleEditable;
import com.infomaximum.platform.update.UpdateTask;
import com.infomaximum.platform.update.util.UpdateUtil;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class UpdateService {

    public static void updateComponents(Transaction transaction, ModuleUpdateEntity... updates) throws DatabaseException {
        Schema.resolve(ModuleEditable.class); //todo V.Bukharkin вынести отсюда
        List<UpdateTask<? extends Component>> updateTasks = UpdateUtil.getUpdatesInCorrectOrder(updates);
        for (UpdateTask<? extends Component> updateTask : updateTasks) {
            updateComponent(updateTask, transaction);
        }
    }

    public static <T extends Component> void updateComponent(Version prevVersion, Version nextVersion, T component, Transaction transaction) throws DatabaseException {
        UpdateTask<T> updateTask = UpdateUtil.getUpdateTaskObj(prevVersion, nextVersion, component);
        updateComponent(updateTask, transaction);
    }

    private static void updateComponent(UpdateTask<? extends Component> updateTask, Transaction transaction) throws DatabaseException {
        updateTask.execute(transaction);
        Set<StructEntity> domains = new HashSet<>();
        for (Class domainObjectClass : new Reflections(updateTask.getComponentInfo().getUuid()).getTypesAnnotatedWith(Entity.class, true)) {
            domains.add(Schema.getEntity(domainObjectClass));
        }
        Schema.read(transaction.getDbProvider()).checkSubsystemIntegrity(domains, updateTask.getComponentInfo().getUuid());
    }
}
