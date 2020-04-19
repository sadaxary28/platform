package com.infomaximum.platform.sdk.component;

import com.infomaximum.subsystems.subsystem.CompatibleVersion;
import com.infomaximum.subsystems.utils.Version;

import java.util.HashMap;
import java.util.Map;

public class Info extends com.infomaximum.cluster.struct.Info {

    private final Version version;

    private Info(Info.Builder builder) {
        super(builder);
        this.version = builder.version;
    }

    public Version getVersion() {
        return version;
    }

    @Override
    public Class<? extends Component> getComponent() {
        return (Class<? extends Component>) super.getComponent();
    }

    public static class Builder extends com.infomaximum.cluster.struct.Info.Builder<Builder> {

        private Version version;
        private Map<Class<? extends Component>, CompatibleVersion> dependenceVersions = new HashMap<>();

        public Builder(String uuid, Version version) {
            super(uuid);
            this.version=version;
        }

        @Override
        public Info build() {
            return new Info(this);
        }

    }
}
