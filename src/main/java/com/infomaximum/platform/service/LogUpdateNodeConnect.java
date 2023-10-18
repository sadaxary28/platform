package com.infomaximum.platform.service;

import com.infomaximum.cluster.Node;
import com.infomaximum.cluster.UpdateNodeConnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUpdateNodeConnect implements UpdateNodeConnect {

    private final static Logger log = LoggerFactory.getLogger(LogUpdateNodeConnect.class);

    @Override
    public void onConnect(Node node) {
        log.info("Node connect, name: {}, runtimeId {}", node.getName(), node.getRuntimeId());
    }

    @Override
    public void onDisconnect(Node node) {
        log.info("Node disconnect, name: {}, runtimeId {}", node.getName(), node.getRuntimeId());
    }
}
