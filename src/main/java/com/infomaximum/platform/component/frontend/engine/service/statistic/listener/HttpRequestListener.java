package com.infomaximum.platform.component.frontend.engine.service.statistic.listener;

import com.infomaximum.network.event.HttpChannelListener;
import com.infomaximum.platform.component.frontend.engine.service.statistic.StatisticService;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.server.Request;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class HttpRequestListener implements HttpChannelListener {

    private ConcurrentMap<Request, AtomicLong> fileDownloadRequests;

    public HttpRequestListener() {
        fileDownloadRequests = new ConcurrentHashMap<>();
    }

    @Override
    public void onResponseBegin(Request request, int status, HttpFields headers) {
        Long downloadFileSize = (Long) request.getAttribute(StatisticService.ATTRIBUTE_DOWNLOAD_FILE_SIZE);
        if (downloadFileSize != null) {
            fileDownloadRequests.put(request, new AtomicLong(downloadFileSize));
        }
    }

    @Override
    public void onResponseWrite(Request request, boolean last, ByteBuffer content) {
        AtomicLong size = fileDownloadRequests.get(request);
        if (size != null) {
            int capacity = content.capacity();
            size.updateAndGet(n -> (n > capacity) ? n - capacity : 0);
        }
    }

    @Override
    public void onComplete(Request request, int status, HttpFields headers, Throwable failure) {
        fileDownloadRequests.remove(request);
    }

    public long getQueueDownloadBytes() {
        return fileDownloadRequests.values()
                .stream().mapToLong(i -> i.longValue()).sum();
    }
}
