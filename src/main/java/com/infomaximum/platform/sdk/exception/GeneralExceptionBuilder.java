package com.infomaximum.platform.sdk.exception;

import com.google.common.collect.ImmutableMap;
import com.infomaximum.database.domainobject.DomainObject;
import com.infomaximum.database.exception.DatabaseException;
import com.infomaximum.database.schema.Schema;
import com.infomaximum.database.schema.StructEntity;
import com.infomaximum.subsystems.exception.ExceptionFactory;
import com.infomaximum.subsystems.exception.GeneralExceptionFactory;
import com.infomaximum.subsystems.exception.SubsystemException;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GeneralExceptionBuilder {

    private static final ExceptionFactory EXCEPTION_FACTORY = new GeneralExceptionFactory();

    public static final String INVALID_CREDENTIALS = "invalid_credentials";
    public static final String NOT_FOUND_DOMAIN_OBJECT_CODE = "not_found_domain_object";
    public static final String NOT_EMPTY_DOMAIN_OBJECT_CODE = "not_empty_domain_object";
    private static final String NOT_UNIQUE_VALUE_CODE = "not_unique_value";
    private static final String INVALID_VALUE_CODE = "invalid_value";
    public static final String ACCESS_DENIED_CODE = "access_denied";
    private static final String OBLIGATORY_PARAM = "obligatory_param";

    private GeneralExceptionBuilder() {
    }

    public static SubsystemException buildDatabaseException(DatabaseException cause) {
        return EXCEPTION_FACTORY.build("database_error", cause);
    }

    public static SubsystemException buildDatabaseException(DatabaseException cause, Map<String, Object> params) {
        return EXCEPTION_FACTORY.build("database_error", cause, params);
    }

    public static SubsystemException buildNotFoundDomainObjectException(Class<? extends DomainObject> clazz, Long id) {
        return EXCEPTION_FACTORY.build(NOT_FOUND_DOMAIN_OBJECT_CODE, new HashMap<String, Object>() {{
            put("type", Schema.getEntity(clazz).getName());
            put("id", id);
        }});
    }

    public static SubsystemException buildNotFoundDomainObjectException(Class<? extends DomainObject> clazz, int fieldNumber, Object fieldValue) {
        StructEntity entity = Schema.getEntity(clazz);
        return EXCEPTION_FACTORY.build(NOT_FOUND_DOMAIN_OBJECT_CODE, new HashMap<String, Object>() {{
            put("type", entity.getName());
            put("field_name", entity.getField(fieldNumber).getName());
            put("field_value", fieldValue);
        }});
    }

    public static SubsystemException buildDomainObjectAlreadyExistsException(Class<? extends DomainObject> clazz, Long id) {
        return EXCEPTION_FACTORY.build("domain_object_already_exists", new HashMap<String, Object>() {{
            put("type", Schema.getEntity(clazz).getName());
            put("id", id);
        }});
    }

    public static SubsystemException buildNotEmptyDomainObjectException(Class<? extends DomainObject> clazz) {
        return EXCEPTION_FACTORY.build(NOT_EMPTY_DOMAIN_OBJECT_CODE, new HashMap<String, Object>() {{
            put("type", Schema.getEntity(clazz).getName());
        }});
    }

    public static Map<String, Object> buildParams(Class<? extends DomainObject> clazz, int fieldNumber) {
        StructEntity entity = Schema.getEntity(clazz);

        HashMap<String, Object> params = new HashMap<>();
        params.put("type", entity.getName());
        params.put("field_name", entity.getField(fieldNumber).getName());
        return params;
    }

    public static Map<String, Object> buildParams(Class<? extends DomainObject> clazz, int fieldNumber, Object fieldValue) {
        Map<String, Object> params = buildParams(clazz, fieldNumber);
        params.put("field_value", fieldValue);
        return params;
    }

    public static SubsystemException buildInvalidCredentialsException() {
        return EXCEPTION_FACTORY.build(INVALID_CREDENTIALS);
    }

    public static SubsystemException buildInvalidCredentialsException(String type, String name) {
        return EXCEPTION_FACTORY.build(INVALID_CREDENTIALS, ImmutableMap.of("type", type, "name", name));
    }

    public static SubsystemException buildInvalidJsonException() {
        return buildInvalidJsonException(null);
    }

    public static SubsystemException buildGraphQLInvalidSyntaxException() {
        return EXCEPTION_FACTORY.build("graphql_invalid_syntax");
    }

    public static SubsystemException buildGraphQLValidationException() {
        return buildGraphQLValidationException(null);
    }

    public static SubsystemException buildGraphQLValidationException(String message) {
        return EXCEPTION_FACTORY.build("graphql_validation_error", message);
    }

    public static SubsystemException buildInvalidJsonException(Throwable cause) {
        return EXCEPTION_FACTORY.build("invalid_json", cause);
    }

    public static SubsystemException buildIllegalStateException(String message) {
        return EXCEPTION_FACTORY.build("illegal_state_exception", message);
    }

    public static SubsystemException buildIOErrorException(IOException e) {
        return EXCEPTION_FACTORY.build("io_error", e);
    }

    public static SubsystemException buildSecurityException(SecurityException e) {
        return EXCEPTION_FACTORY.build("security_exception", e);
    }

    public static SubsystemException buildEmptyValueException(String fieldName) {
        return EXCEPTION_FACTORY.build("empty_value", Collections.singletonMap("fieldName", fieldName));
    }

    public static SubsystemException buildEmptyValueException(Class<? extends DomainObject> clazz, int fieldNumber) {
        return EXCEPTION_FACTORY.build("empty_value", buildParams( clazz, fieldNumber));
    }

    public static SubsystemException buildNotUniqueValueException(Class<? extends DomainObject> clazz, int fieldNumber, Object fieldValue) {
        return EXCEPTION_FACTORY.build(NOT_UNIQUE_VALUE_CODE, buildParams(clazz, fieldNumber, fieldValue));
    }

    public static SubsystemException buildNotUniqueValueException(String name, Object value) {
        return EXCEPTION_FACTORY.build(NOT_UNIQUE_VALUE_CODE, Collections.singletonMap(name, value));
    }

    public static SubsystemException buildInvalidValueException(String fieldName) {
        return EXCEPTION_FACTORY.build(INVALID_VALUE_CODE, new HashMap<String, Object>() {{
            put("field_name", fieldName);
        }});
    }

    public static SubsystemException buildInvalidValueExceptionWithCause(String cause) {
        return EXCEPTION_FACTORY.build(INVALID_VALUE_CODE, new HashMap<String, Object>() {{
            put("cause", cause);
        }});
    }

    public static SubsystemException buildInvalidValueException(String fieldName, Serializable fieldValue) {
        return EXCEPTION_FACTORY.build(INVALID_VALUE_CODE, new HashMap<String, Object>() {{
            put("field_name", fieldName);
            put("field_value", fieldValue);
        }});
    }

    public static SubsystemException buildInvalidValueException(String fieldName, Serializable fieldValue, String comment) {
        return EXCEPTION_FACTORY.build(INVALID_VALUE_CODE, comment, new HashMap<String, Object>() {{
            put("field_name", fieldName);
            put("field_value", fieldValue);
        }});
    }

    public static SubsystemException buildInvalidValueException(Class<? extends DomainObject> clazz, int fieldNumber, Serializable fieldValue) {
        return EXCEPTION_FACTORY.build(INVALID_VALUE_CODE, buildParams(clazz, fieldNumber, fieldValue));
    }

    public static SubsystemException buildInvalidValueException(Class<? extends DomainObject> clazz, int fieldNumber, Serializable fieldValue, String comment) {
        return EXCEPTION_FACTORY.build(INVALID_VALUE_CODE, comment, buildParams(clazz, fieldNumber, fieldValue));
    }

    public static SubsystemException buildUploadFileNotFoundException() {
        return EXCEPTION_FACTORY.build("upload_file_not_found");
    }

    public static SubsystemException buildOnlyWebsocket() {
        return EXCEPTION_FACTORY.build("only_websocket");
    }

    public static SubsystemException buildAccessDeniedException() {
        return EXCEPTION_FACTORY.build(ACCESS_DENIED_CODE);
    }

    public static SubsystemException buildServerBusyException(String cause) {
        return EXCEPTION_FACTORY.build("server_busy", Collections.singletonMap("cause", cause));
    }

    public static SubsystemException buildServerOverloadedException() {
        return EXCEPTION_FACTORY.build("server_overloaded");
    }

    public static SubsystemException buildServerTimeoutException() {
        return EXCEPTION_FACTORY.build("server_timeout");
    }

    public static SubsystemException buildServerShutsDownException() {
        return EXCEPTION_FACTORY.build("server_shuts_down");
    }

    public static SubsystemException buildAuthAmbiguityException(String message) {
        return EXCEPTION_FACTORY.build("auth_ambiguity", message);
    }

    /**
     * @return Не найден обязательный параметр.
     */
    public static SubsystemException buildNotFoundObligatoryParam(Class<? extends DomainObject> clazz, int fieldNumber) {
        return EXCEPTION_FACTORY.build(OBLIGATORY_PARAM, GeneralExceptionBuilder.buildParams(clazz, fieldNumber));
    }

    public static SubsystemException buildNotFoundObligatoryParam(String fieldName) {
        return EXCEPTION_FACTORY.build(OBLIGATORY_PARAM, fieldName);
    }

}


