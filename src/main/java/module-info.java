module com.infomaximum.platform {
    requires transitive com.infomaximum.rdao;
    requires transitive com.infomaximum.cluster;
    requires transitive com.infomaximum.cluster.graphql;
    requires org.slf4j;
    requires com.graphqljava.graphqljava;
    requires com.infomaximum.network;
    requires org.reflections.reflections;
    requires net.minidev.jsonsmart;
    requires org.eclipse.jetty.server;
    requires spring.core;
    requires spring.web;
    requires org.eclipse.jetty.websocket.jetty.api;
    requires org.apache.commons.commonslang3;
    requires org.rocksdb.rocksdbjni;
    requires com.google.guava.guava;

    exports com.infomaximum.platform.sdk.context;
    exports com.infomaximum.platform.querypool.iterator;
    exports com.infomaximum.platform.sdk.function;
    exports com.infomaximum.platform;
    exports com.infomaximum.platform.component.database;
    exports com.infomaximum.platform.component.database.configure;
    exports com.infomaximum.platform.sdk.component;
    exports com.infomaximum.platform.sdk.component.version;
    exports com.infomaximum.platform.sdk.remote.packer;
    exports com.infomaximum.platform.sdk.threadpool;
    exports com.infomaximum.platform.update.core;
    exports com.infomaximum.platform.component.frontend.authcontext;
    exports com.infomaximum.platform.component.frontend.context;
    exports com.infomaximum.platform.sdk.graphql.customfield.graphqlquery;
    exports com.infomaximum.platform.sdk.graphql.annotation;
    exports com.infomaximum.platform.sdk.struct.querypool;
    exports com.infomaximum.platform.exception.runtime;
    exports com.infomaximum.platform.exception;
    exports com.infomaximum.platform.sdk.exception;
    exports com.infomaximum.platform.sdk.utils;
    exports com.infomaximum.platform.sdk.iterator;
    exports com.infomaximum.platform.querypool;
    exports com.infomaximum.platform.component.frontend.context.source;
    exports com.infomaximum.platform.sdk.context.source;
    exports com.infomaximum.platform.sdk.context.source.impl;
    exports com.infomaximum.platform.component.frontend.engine.service.statistic;
    exports com.infomaximum.platform.component.frontend.request;
    exports com.infomaximum.platform.update;
    exports com.infomaximum.platform.update.annotation;
    exports com.infomaximum.platform.utils;
    exports com.infomaximum.platform.querypool.service.threadcontext;
    exports com.infomaximum.platform.component.frontend.context.source.impl;
    exports com.infomaximum.platform.component.frontend.engine;
    exports com.infomaximum.platform.component.frontend.engine.controller;
    exports com.infomaximum.platform.component.frontend.engine.network.protocol.graphqlws;
    exports com.infomaximum.platform.component.frontend.engine.network.protocol.graphqlws.handler.graphql;
    exports com.infomaximum.platform.component.frontend.engine.provider;
    exports com.infomaximum.platform.component.frontend.engine.service.errorhandler;
    exports com.infomaximum.platform.component.frontend.engine.service.graphqlrequestexecute;
    exports com.infomaximum.platform.component.frontend.request.graphql.builder.impl;
    exports com.infomaximum.platform.sdk.struct;
    exports com.infomaximum.platform.sdk.context.impl;
    exports com.infomaximum.platform.component.database.remote.backup;
    exports com.infomaximum.platform.component.frontend.context.impl;
    exports com.infomaximum.platform.component.frontend.engine.authorize;
    exports com.infomaximum.platform.component.frontend.utils;
    exports com.infomaximum.platform.update.exception;
    exports com.infomaximum.platform.component.frontend.request.graphql.builder.impl.attribute;
    exports com.infomaximum.platform.component.frontend.engine.controller.http.graphql;
    exports com.infomaximum.platform.component.frontend.engine.controller.websocket.graphql;
    exports com.infomaximum.platform.sdk.subscription;

    exports com.infomaximum.platform.sdk.domainobject.module to com.infomaximum.rdao;

    exports com.infomaximum.platform.sdk.graphql.datafetcher to com.infomaximum.cluster.graphql;
    exports com.infomaximum.platform.component.database.remote.dbprovider to com.infomaximum.cluster;
    exports com.infomaximum.platform.sdk.dbprovider.remote to com.infomaximum.cluster;
}