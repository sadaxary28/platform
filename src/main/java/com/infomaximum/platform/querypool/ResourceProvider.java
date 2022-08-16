package com.infomaximum.platform.querypool;

import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.domainobject.DomainObjectEditable;
import com.infomaximum.platform.sdk.component.Component;

import java.util.Set;

public interface ResourceProvider {

    <T extends QueryRemoteController> Set<T> getQueryRemoteControllers(Class<T> remoteControllerClass);

    <T extends QueryRemoteController> T getQueryRemoteController(Class<? extends Component> classComponent, Class<T> remoteControllerClass);

    <T extends QueryRemoteController> T getQueryRemoteController(String componentUuid, Class<T> remoteControllerClass);

    <T extends QueryRemoteController> boolean isQueryRemoteController(String componentUuid, Class<T> remoteControllerClass);

    <T extends DomainObject> ReadableResource<T> getReadableResource(Class<T> resClass);

    <T extends DomainObject & DomainObjectEditable> EditableResource<T> getEditableResource(Class<T> resClass);

    <T extends DomainObject & DomainObjectEditable> RemovableResource<T> getRemovableResource(Class<T> resClass);

    void borrowAllDomainObjects(QueryPool.LockType type);

    void borrowResource(Class resClass, QueryPool.LockType type);
}
