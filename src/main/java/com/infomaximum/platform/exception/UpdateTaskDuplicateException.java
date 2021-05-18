package com.infomaximum.platform.exception;

public class UpdateTaskDuplicateException extends RuntimeException {
    public UpdateTaskDuplicateException(String componentUuid, String version) {
        super("Update task for component " + componentUuid + " with version " + version + "is already exist");
    }
}
