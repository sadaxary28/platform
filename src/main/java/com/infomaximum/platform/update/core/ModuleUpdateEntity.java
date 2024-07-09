package com.infomaximum.platform.update.core;

import com.infomaximum.cluster.struct.Version;
import com.infomaximum.platform.sdk.component.Component;

public class ModuleUpdateEntity {

    private final Version oldVersion;
    private final Version newVersion;
    private final String componentUuid;

    private Component component;

    public ModuleUpdateEntity(Version oldVersion, Version newVersion, String componentUuid) {
        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
        this.componentUuid = componentUuid;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public Version getOldVersion() {
        return oldVersion;
    }

    public Version getNewVersion() {
        return newVersion;
    }

    public String getComponentUuid() {
        return componentUuid;
    }

    public Component getComponent() {
        return component;
    }

    @Override
    public String toString() {
        return "ModuleUpdateEntity{" +
                "oldVersion=" + oldVersion +
                ", newVersion=" + newVersion +
                ", componentUuid='" + componentUuid + '\'' +
                '}';
    }
}
