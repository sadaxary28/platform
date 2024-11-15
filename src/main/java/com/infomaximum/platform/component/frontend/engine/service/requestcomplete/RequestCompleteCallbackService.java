package com.infomaximum.platform.component.frontend.engine.service.requestcomplete;

import com.infomaximum.network.event.HttpChannelListener;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.server.Request;

public class RequestCompleteCallbackService implements HttpChannelListener {

    public final static String ATTRIBUTE_COMPLETE_REQUEST_CALLBACK = "com.infomaximum.request.complete.callback";

    public abstract static class Callback {
        public abstract void exec(Request request) throws Throwable;
    }

    private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    public RequestCompleteCallbackService(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    @Override
    public void onComplete(Request request, int status, HttpFields headers, Throwable failure) {
        execCallback(request);
    }

    private void execCallback(Request request) {
        Callback callback = (Callback) request.getAttribute(ATTRIBUTE_COMPLETE_REQUEST_CALLBACK);
        if (callback != null) {
            try {
                callback.exec(request);
            } catch (Throwable throwable) {
                uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), throwable);
            }
            request.removeAttribute(ATTRIBUTE_COMPLETE_REQUEST_CALLBACK);
        }
    }

}
