package com.infomaximum.subsystems.querypool.service;

import com.infomaximum.subsystems.querypool.QueryPool;
import com.infomaximum.utils.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DetectLongQuery implements Runnable {

    private final static Logger log = LoggerFactory.getLogger(DetectLongQuery.class);

    private final static Duration DETECTED_PERIOD = Duration.ofSeconds(1);
	private final static Duration CHECK_PERIOD = Duration.ofMillis(DETECTED_PERIOD.toMillis() - 100);

    private final QueryPool queryPool;

    private final ScheduledExecutorService scheduler;

    public DetectLongQuery(QueryPool queryPool, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.queryPool = queryPool;

        this.scheduler = Executors.newScheduledThreadPool(
				1,
				new DefaultThreadFactory("QueryPool-DetectLongQuery", uncaughtExceptionHandler)
		);
		scheduler.scheduleAtFixedRate(this, CHECK_PERIOD.toMillis(), CHECK_PERIOD.toMillis(), TimeUnit.MILLISECONDS);
	}

	@Override
	public void run() {
		Instant now = Instant.now();
		for (QueryPool.QueryWrapper queryWrapper : queryPool.getExecuteQueries()) {
			if (queryWrapper.getTimeComplete() != null) continue;
			Duration duration = Duration.between(queryWrapper.getTimeStart(), now);
			if (duration.compareTo(DETECTED_PERIOD) < 0) continue;
			log.warn("Detect long query! start: {}, duration: {}, resources: {}, stackTrace: {}",
					queryWrapper.getTimeStart(),
					duration,
					queryWrapper.getResources(),
					queryWrapper.getThread().getStackTrace()
			);
		}
	}

	public void shutdownAwait() {
		scheduler.shutdown();
	}


}
