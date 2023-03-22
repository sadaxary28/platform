package com.infomaximum.platform.update.core;

import com.infomaximum.database.anotation.Entity;
import com.infomaximum.database.domainobject.Transaction;
import com.infomaximum.database.domainobject.filter.HashFilter;
import com.infomaximum.database.domainobject.iterator.IteratorEntity;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.schema.Schema;
import com.infomaximum.database.schema.StructEntity;
import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.platform.sdk.component.Info;
import com.infomaximum.platform.sdk.component.version.Version;
import com.infomaximum.platform.sdk.domainobject.module.ModuleEditable;
import com.infomaximum.platform.update.UpdateTask;
import com.infomaximum.platform.update.util.UpdateUtil;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class UpdateService {

    private final static Logger log = LoggerFactory.getLogger(UpdateService.class);

    public static void updateComponents(Transaction transaction,
                                        ModuleUpdateEntity... updates) throws DatabaseException {
        Schema.resolve(ModuleEditable.class); //todo V.Bukharkin вынести отсюда
        List<UpdateUtil.ModuleTaskUpdate> moduleTaskUpdates = UpdateUtil.getUpdatesInCorrectOrder(updates);
        for (UpdateUtil.ModuleTaskUpdate moduleTaskUpdate : moduleTaskUpdates) {
            updateComponent(moduleTaskUpdate, transaction);
        }
    }

    public static <T extends Component> void updateComponent(Version prevVersion,
                                                             Version nextVersion,
                                                             T component,
                                                             Transaction transaction) throws DatabaseException {
        UpdateUtil.ModuleTaskUpdate moduleTaskUpdate = UpdateUtil.getUpdateTaskObj(prevVersion, nextVersion, component);
        updateComponent(moduleTaskUpdate, transaction);
    }

    private static void updateComponent(UpdateUtil.ModuleTaskUpdate moduleTaskUpdate,
                                        Transaction transaction) throws DatabaseException {
        Info componentInfo = (Info) moduleTaskUpdate.getComponent().getInfo();

        try (IteratorEntity<ModuleEditable> iter = transaction.find(ModuleEditable.class, new HashFilter(ModuleEditable.FIELD_UUID, componentInfo.getUuid()))) {
            if (iter.hasNext()) {
                ModuleEditable moduleEditable = iter.next();
                log.info("Updating subsystem: " + componentInfo.getUuid() + ". From version " + moduleEditable.getVersion() + " to version " + componentInfo.getVersion());

                UpdateTask<? extends Component> updateTask = moduleTaskUpdate.getUpdateTask();
                if (updateTask != null) {
                    updateTask.execute(moduleEditable, transaction);
                }

                //Сохраняем
                Version codeVersion = componentInfo.getVersion();
                moduleEditable.setVersion(codeVersion);
                transaction.save(moduleEditable);
            }
        }
        Set<StructEntity> domains = new HashSet<>();
        for (Class domainObjectClass : new Reflections(componentInfo.getUuid()).getTypesAnnotatedWith(Entity.class, true)) {
            domains.add(Schema.getEntity(domainObjectClass));
        }
        Schema.read(transaction.getDbProvider()).checkSubsystemIntegrity(domains, componentInfo.getUuid(), new HashMap<>());
    }
}
