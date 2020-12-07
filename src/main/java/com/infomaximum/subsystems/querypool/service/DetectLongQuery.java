package com.infomaximum.subsystems.querypool.service;

import com.infomaximum.platform.sdk.context.Context;
import com.infomaximum.subsystems.querypool.QueryPool;
import com.infomaximum.utils.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Сервис отслеживающий долгих query
 */
public class DetectLongQuery implements Runnable {

	private final static Logger log = LoggerFactory.getLogger(DetectLongQuery.class);

	private final static Duration DETECTED_PERIOD = Duration.ofSeconds(1);
	private final static Duration CHECK_PERIOD = Duration.ofMillis(DETECTED_PERIOD.toMillis() - 100);

	private final static Duration WARN_LOG_DETECTED_PERIOD =  Duration.ofSeconds(30);

	private final QueryPool queryPool;
	private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

	private final ScheduledExecutorService scheduler;

	public DetectLongQuery(QueryPool queryPool, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
		this.queryPool = queryPool;
		this.uncaughtExceptionHandler = uncaughtExceptionHandler;

		this.scheduler = Executors.newScheduledThreadPool(
				1,
				new DefaultThreadFactory("QueryPool-DetectLongQuery", uncaughtExceptionHandler)
		);
		scheduler.scheduleAtFixedRate(this, CHECK_PERIOD.toMillis(), CHECK_PERIOD.toMillis(), TimeUnit.MILLISECONDS);

		log.info("DetectLongQuery starting.");
	}

	@Override
	public void run() {
		try {
			Instant now = Instant.now();
			for (QueryPool.QueryWrapper queryWrapper : queryPool.getExecuteQueries()) {
				if (queryWrapper.getTimeComplete() != null) continue;

				Instant timeStart = queryWrapper.getTimeStart();
				if (timeStart == null) continue;

				Duration duration = Duration.between(timeStart, now);
				if (duration.compareTo(DETECTED_PERIOD) < 0) continue;

				Map<String, QueryPool.LockType> resources = queryWrapper.getResources();
				if (resources.isEmpty()) continue;

				Thread thread = queryWrapper.getThread();
				if (thread == null) continue;

				Context context = queryWrapper.getContext();

				if (duration.compareTo(WARN_LOG_DETECTED_PERIOD) < 0) {
					log.debug("Detect long query {}, start: {}, duration: {}, resources: {}, stackTrace: {}",
							context.getTrace(),
							queryWrapper.getTimeStart(),
							duration.toMillis(),
							toStringResources(resources),
							toStringStackTrace(thread.getStackTrace())
					);
				} else {
					log.warn("Detect long query {}, start: {}, duration: {}, resources: {}, stackTrace: {}",
							context.getTrace(),
							queryWrapper.getTimeStart(),
							duration.toMillis(),
							toStringResources(resources),
							toStringStackTrace(thread.getStackTrace())
					);
				}
			}
		} catch (Throwable e) {
			uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e);
		}
	}

	private static String toStringResources(Map<String, QueryPool.LockType> resources) {
		StringJoiner exclusive = new StringJoiner(", ");
		StringJoiner shared = new StringJoiner(", ");
		for (Map.Entry<String, QueryPool.LockType> entry : resources.entrySet()) {
			String clazzName = entry.getKey().substring(entry.getKey().lastIndexOf('.') + 1);
			switch (entry.getValue()) {
				case EXCLUSIVE:
					exclusive.add(clazzName);
					break;
				case SHARED:
					shared.add(clazzName);
					break;
				default:
					throw new RuntimeException("Unknown type: " + entry.getValue());
			}
		}

		StringBuilder out = new StringBuilder();
		out.append("{ ");
		if (exclusive.length() > 0) {
			out.append("exclusive: [").append(exclusive.toString()).append(']');
		}
		if (shared.length() > 0) {
			if (exclusive.length() > 0) {
				out.append(", ");
			}
			out.append("shared: [").append(shared.toString()).append(']');
		}
		out.append('}');
		return out.toString();
	}

	private static String toStringStackTrace(StackTraceElement[] stackTraceElements) {
		StringJoiner out = new StringJoiner(" ", "[", "]");
		for (StackTraceElement item: stackTraceElements) {
			out.add(item.toString());
		}
		return out.toString();
	}

	public void shutdownAwait() {
		scheduler.shutdown();
		log.info("DetectLongQuery stopping.");
	}

}
