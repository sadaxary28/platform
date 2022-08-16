package com.infomaximum.platform.querypool.service;

import com.infomaximum.platform.utils.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Сервис отслеживающий заканчивающиеся свободные worker'ы
 */
public class DetectEndingWorker implements Runnable {

	private final static Logger log = LoggerFactory.getLogger(DetectEndingWorker.class);

	private final static Duration DETECTED_PERIOD = Duration.ofSeconds(10);
	private final static Duration CHECK_PERIOD = Duration.ofMillis(DETECTED_PERIOD.toMillis() - 100);

	private final ThreadPoolExecutor threadPool;
	private final Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

	private final ScheduledExecutorService scheduler;

	public DetectEndingWorker(ThreadPoolExecutor threadPool, Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
		this.threadPool = threadPool;
		this.uncaughtExceptionHandler = uncaughtExceptionHandler;

		this.scheduler = Executors.newScheduledThreadPool(
				1,
				new DefaultThreadFactory("QueryPool-DetectEndingWorker", uncaughtExceptionHandler)
		);
		scheduler.scheduleAtFixedRate(this, CHECK_PERIOD.toMillis(), CHECK_PERIOD.toMillis(), TimeUnit.MILLISECONDS);

		log.info("DetectEndingWorker starting.");
	}

	@Override
	public void run() {
		try {
			int activeCount = threadPool.getActiveCount();
			int maximumPoolSize = threadPool.getMaximumPoolSize();
			if (maximumPoolSize - activeCount <= 2) {
				//Если у нас заканчиваются свободные веркеры - оповещаем
				log.warn("On the verge of overload thread pool: {}/{}", activeCount, maximumPoolSize);
			}
		} catch (Throwable e) {
			uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e);
		}
	}

	public void shutdownAwait() {
		scheduler.shutdown();
		log.info("DetectEndingWorker stopping.");
	}

}
