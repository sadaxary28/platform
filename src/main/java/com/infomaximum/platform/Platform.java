package com.infomaximum.platform;

import com.infomaximum.platform.component.database.configure.DatabaseConfigure;

public class Platform {

    private static volatile Platform instant;

    private final DatabaseConfigure databaseConfigure;

    private Platform(Builder builder) {
        synchronized (Platform.class) {
            if (instant != null) throw new IllegalStateException();

            this.databaseConfigure = builder.databaseConfigure;

            instant = this;
        }
    }

    public DatabaseConfigure getDatabaseConfigure() {
        return databaseConfigure;
    }


    public static Platform get() {
        return instant;
    }

    public static class Builder {

        private DatabaseConfigure databaseConfigure;

        public Builder() {
        }

        public Builder withConfig(DatabaseConfigure databaseConfigure) {
            this.databaseConfigure = databaseConfigure;
            return this;
        }

        public Platform build() {
            return new Platform(this);
        }
    }
}
