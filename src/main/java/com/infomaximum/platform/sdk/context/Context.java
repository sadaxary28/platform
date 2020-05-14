package com.infomaximum.platform.sdk.context;

import com.infomaximum.platform.sdk.context.source.Source;

public interface Context<S extends Source> {

    S getSource();

}
