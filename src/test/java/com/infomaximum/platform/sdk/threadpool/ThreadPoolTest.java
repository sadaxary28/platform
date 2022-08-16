package com.infomaximum.platform.sdk.threadpool;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ThreadPoolTest {

    private volatile Throwable uncaughtException = null;
    private CountDownLatch uncaughtExceptionSync = null;

    @AfterAll
    public void checkException() throws Throwable {
        if (uncaughtException != null) {
            Throwable e = uncaughtException;
            uncaughtException = null;
            throw e;
        }
    }

    private ThreadPool buildPool() {
        return new ThreadPool((t, e) -> {
            uncaughtException = e;
            if (uncaughtExceptionSync != null) {
                uncaughtExceptionSync.countDown();
            }
        });
    }

    @Test
    public void invokeEmptyList() throws Exception {
        ThreadPool pool = buildPool();
        pool.invokeAll(Collections.emptyList());

        Assertions.assertTrue(true);
    }

    @Test
    public void
    invokeOneTask() throws Exception {
        final Thread currentThread = Thread.currentThread();
        final Boolean[] execThreadIsCurrentThread = new Boolean[1];

        ThreadPool pool = buildPool();

        pool.invokeAll(Collections.singletonList(() -> {
            execThreadIsCurrentThread[0] = currentThread == Thread.currentThread();
            return null;
        }));
        Assertions.assertTrue(execThreadIsCurrentThread[0]);

        try {
            pool.invokeAll(Collections.singletonList(() -> {
                throw GeneralExceptionBuilder.buildAccessDeniedException();
            }));
            Assertions.fail();
        } catch (PlatformException e) {
            Assertions.assertTrue(PlatformException.equals(GeneralExceptionBuilder.buildAccessDeniedException(), e));
        }
    }

    @Test
    public void invokeMultipleTask() throws Exception {
        final Thread currentThread = Thread.currentThread();
        final int taskCount = 5;
        final Boolean[] execThreadIsCurrentThread = new Boolean[taskCount];

        ThreadPool pool = buildPool();

        List<Callable<Void>> tasks = new ArrayList<>(taskCount);
        IntStream.range(0, taskCount).forEach(value -> tasks.add(() -> {
            execThreadIsCurrentThread[value] = currentThread == Thread.currentThread();
            return null;
        }));
        pool.invokeAll(tasks);
        Assertions.assertArrayEquals(new Boolean[] {
                true, false, false, false, false
        }, execThreadIsCurrentThread);

        tasks.clear();
        AtomicBoolean exitByTimeout = new AtomicBoolean(false);
        final int maximumThreadCount = ThreadPool.MAXIMUM_POOL_SIZE + 1;
        final CountDownLatch countDownLatch = new CountDownLatch(maximumThreadCount);
        IntStream.range(0, maximumThreadCount).forEach(value -> tasks.add(() -> {
            if (!exitByTimeout.get()) {
                countDownLatch.countDown();
                if (!countDownLatch.await(10, TimeUnit.SECONDS)) {
                    exitByTimeout.set(true);
                }
            }
            return null;
        }));
        pool.invokeAll(tasks);
        Assertions.assertFalse(exitByTimeout.get());
    }

    @Test
    public void invokeMultipleExceptionallyTask() throws Exception {
        final Thread currentThread = Thread.currentThread();
        final int taskCount = 5;
        final Boolean[] execThreadIsCurrentThread = new Boolean[taskCount];

        ThreadPool pool = buildPool();

        List<Callable<Void>> tasks = new ArrayList<>(taskCount);
        IntStream.range(0, taskCount).forEach(value -> tasks.add(() -> {
            execThreadIsCurrentThread[value] = currentThread == Thread.currentThread();
            if (value == 0) {
                return null;
            }
            if (value == 1) {
                throw GeneralExceptionBuilder.buildAccessDeniedException();
            } else {
                Thread.sleep(Duration.ofSeconds(1).toMillis());
                throw new IllegalArgumentException("Thread index: " + value);
            }
        }));

        try {
            pool.invokeAll(tasks);
            Assertions.fail();
        } catch (PlatformException e) {
            Assertions.assertArrayEquals(new Boolean[] {
                    true, false, false, false, false
            }, execThreadIsCurrentThread);
            Assertions.assertTrue(PlatformException.equals(GeneralExceptionBuilder.buildAccessDeniedException(), e));
        }

        uncaughtException = null;
    }

    @Test
    public void testManyAsyncTask() throws Throwable {
        ThreadPool pool = buildPool();
        AtomicReference<Throwable> ex = new AtomicReference<>(null);

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<CompletableFuture<Void>> tasks = new ArrayList <>();
        for (int threadCount = 0; threadCount < 2 * ThreadPool.MAXIMUM_POOL_SIZE; ++threadCount) {
            tasks.add(pool.runAsync(() -> {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).exceptionally(throwable -> {
                ex.set(throwable);
                return null;
            }));
        }

        countDownLatch.countDown();
        for (CompletableFuture<Void> task : tasks) {
            task.get();
        }

        if (ex.get() != null) {
            throw ex.get();
        }
    }

    @Test
    public void testUncaughtExceptionHandlerInvokeCount() throws Exception {
        AtomicLong invokeCount = new AtomicLong(0);
        ThreadPool pool = new ThreadPool((t, e) -> invokeCount.incrementAndGet());

        try {
            pool.invokeAll(Arrays.asList(() -> null, () -> {
                throw new UncaughtException();
            }));
            Assertions.fail();
        } catch (CancellationException ignore) {
        }
        Thread.sleep(Duration.ofSeconds(5).toMillis());
        Assertions.assertEquals(1, invokeCount.get());

        invokeCount.set(0);
        try {
            pool.invokeAll(Arrays.asList(() -> {
                throw new UncaughtException();
            }, () -> null));
            Assertions.fail();
        } catch (CancellationException ignore) {
        }
        Thread.sleep(Duration.ofSeconds(5).toMillis());
        Assertions.assertEquals(1, invokeCount.get());
    }

    @Test
    public void testUncaughtExceptionHandler() throws Exception {
        ThreadPool pool = buildPool();

        try {
            uncaughtExceptionSync = new CountDownLatch(1);
            pool.runAsync(() -> {
                throw new UncaughtException();
            }).get();
            Assertions.fail();
        } catch (CancellationException ignore) {
        }
        assertUncaughtException();

        try {
            uncaughtExceptionSync = new CountDownLatch(1);
            pool.supplyAsync(() -> {
                throw new UncaughtException();
            }).get();
            Assertions.fail();
        } catch (CancellationException ignore) {
        }
        assertUncaughtException();

        try {
            uncaughtExceptionSync = new CountDownLatch(1);
            pool.invokeAll(Collections.singletonList(() -> {
                throw new UncaughtException();
            }));
            Assertions.fail();
        } catch (CancellationException ignore) {
        }
        assertUncaughtException();

        try {
            uncaughtExceptionSync = new CountDownLatch(1);
            pool.invokeAll(Arrays.asList(() -> null, () -> {
                throw new UncaughtException();
            }));
            Assertions.fail();
        } catch (CancellationException ignore) {
        }
        assertUncaughtException();

        try {
            uncaughtExceptionSync = new CountDownLatch(1);
            pool.invokeAll(Collections.singletonList(() -> {
                throw GeneralExceptionBuilder.buildAccessDeniedException();
            }));
            Assertions.fail();
        } catch (PlatformException ignore) {
        }
        Assertions.assertEquals(uncaughtException, null);
    }

    private void assertUncaughtException() {
        try {
            uncaughtExceptionSync.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(new UncaughtException(), uncaughtException);
        uncaughtException = null;
    }

    private static class UncaughtException extends RuntimeException {

        @Override
        public boolean equals(Object obj) {
            return obj instanceof UncaughtException;
        }
    }
}
