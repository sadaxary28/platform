package com.infomaximum.subsystems.exception;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

public class SubsystemException extends Exception {

    private final String subsystemUuid;
    private final String code;
    private final Map<String, Object> parameters;
    private final String comment;

    SubsystemException(String code, String comment, Map<String, Object> parameters, Throwable cause) {
        this(null, code, comment, parameters, cause);
    }

    SubsystemException(String code, String comment, Map<String, Object> parameters) {
        this(null, code, comment, parameters);
    }

    SubsystemException(String subsystemUuid, String code, String comment, Map<String, Object> parameters) {
        this(subsystemUuid, code, comment, parameters, null);
    }

    SubsystemException(String subsystemUuid, String code, String comment, Map<String, Object> parameters, Throwable cause) {
        super(
                buildMessage(subsystemUuid, code, parameters, comment),
                cause
        );

        if (subsystemUuid != null && subsystemUuid.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (StringUtils.isEmpty(code)) {
            throw new IllegalArgumentException();
        }

        this.subsystemUuid = subsystemUuid;
        this.code = code;
        this.parameters = parameters == null ? null : Collections.unmodifiableMap(parameters);
        this.comment = comment;
    }

    public String getSubsystemUuid() {
        return subsystemUuid;
    }

    public String getCode() {
        return code;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public <T> T getParameterValue(String paramName, T defaultValue) {
        return parameters != null ? (T) parameters.getOrDefault(paramName, defaultValue) : defaultValue;
    }

    public String getComment() {
        return comment;
    }

    private static String buildMessage(String subsystemUuid, String code, Map<String, Object> parameters, String comment) {
        StringJoiner builder = new StringJoiner(", ");
        if (comment != null) builder.add(comment);
        if (subsystemUuid != null) builder.add("subsystemUuid=" + subsystemUuid);
        builder.add("code=" + code);
        if (parameters != null) builder.add("parameters=" + parameters);
        return builder.toString();
    }

    public static boolean equals(SubsystemException e1, SubsystemException e2) {
        if (e1 == e2) {
            return true;
        } else if (e1 == null || e2 == null) {
            return false;
        }

        if (!Objects.equals(e1.getSubsystemUuid(), e2.getSubsystemUuid())) {
            return false;
        }

        if (!e1.getCode().equals(e2.getCode())) {
            return false;
        }

        if (!Objects.equals(e1.getComment(), e2.getComment())) {
            return false;
        }

        if (!Objects.equals(e1.getParameters(), e2.getParameters())) {
            return false;
        }

        return Objects.equals(e1.getCause(), e2.getCause());
    }
}
