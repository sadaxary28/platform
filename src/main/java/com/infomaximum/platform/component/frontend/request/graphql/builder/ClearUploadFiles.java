package com.infomaximum.platform.component.frontend.request.graphql.builder;

import com.infomaximum.platform.component.frontend.request.GRequestHttp;

import java.util.List;

public interface ClearUploadFiles {

    void clear(List<GRequestHttp.UploadFile> uploadFiles);
}
