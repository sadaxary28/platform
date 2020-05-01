package com.infomaximum.platform.sdk.domainobject.module;

import com.infomaximum.database.domainobject.DomainObjectEditable;
import com.infomaximum.platform.sdk.component.version.Version;

public class ModuleEditable extends ModuleReadable implements DomainObjectEditable {

    public ModuleEditable(long id) {
        super(id);
    }

    public void setUuid(String value) {
        set(FIELD_UUID, value);
    }

    public void setVersion(Version value) {
        set(FIELD_VERSION, value.toString());
    }
}
