package com.infomaximum.platform.sdk.querypool;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.exception.runtime.ClosedObjectException;
import com.infomaximum.platform.querypool.*;
import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.platform.sdk.exception.GeneralExceptionBuilder;
import com.infomaximum.platform.sdk.function.Consumer;
import com.infomaximum.testcomponent.TestComponent;
import com.infomaximum.testcomponent.domainobject.employee.EmployeeEditable;
import com.infomaximum.testcomponent.domainobject.employee.EmployeeReadable;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QueryPoolTest {

    private static final long QUERY_DURATION_MILLIS = TimeUnit.SECONDS.toMillis(2);

    private static class SuccessResult {}

    private static class ResultCallback implements Function<Object, Void> {

        final LinkedBlockingQueue<Object> results = new LinkedBlockingQueue<>();

        @Override
        public Void apply(Object param) {
            try {
                results.put(param);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        boolean isSuccess(int expectedResultCount) {
            if (expectedResultCount != results.size()) {
                return false;
            }

            for (Object i : results) {
                if (!(i instanceof SuccessResult)) {
                    return false;
                }
            }

            return true;
        }
    }

    private static Component component = new TestComponent();

    private Throwable uncaughtException = null;

    @BeforeAll
    public static void checkPlatform() {
        final int minThreadCount = 3;
        Assertions.assertTrue(QueryPool.MAX_THREAD_COUNT >= minThreadCount, "Physical thread must be greater or equal than " + minThreadCount);
    }

    @AfterAll
    public void checkException() throws Throwable {
        if (uncaughtException != null) {
            Throwable e = uncaughtException;
            uncaughtException = null;
            throw e;
        }
    }

    private QueryPool buildPool() {
        return new QueryPool((t, e) -> uncaughtException = e);
    }

    @Test
    public void shutdownEmptyPool() throws InterruptedException {
        QueryPool pool = buildPool();
        pool.shutdownAwait();

        Assertions.assertTrue(true);
    }

    @Test
    public void executeOneQueryWithoutResources() throws PlatformException, InterruptedException {
        ResultCallback res = new ResultCallback();
        QueryPool pool = buildPool();
        //TODO Ulitin V.
//        pool.execute(component, createQuery(null)).thenApply(res);
        pool.shutdownAwait();

        Assertions.assertTrue(res.isSuccess(1));
    }

    @Test
    public void executeOneQueryWithOneResource() throws PlatformException, InterruptedException {
        ResultCallback res = new ResultCallback();
        QueryPool pool = buildPool();
        pool.execute(component, createQuery(Object.class, QueryPool.LockType.SHARED, null))
//TODO Ulitin V.
//                .thenApply(res)
        ;
        pool.shutdownAwait();

        Assertions.assertTrue(res.isSuccess(1));
    }

    @Test
    public void executeQueryForShutdownPool() throws PlatformException, InterruptedException {
        QueryPool pool = buildPool();
        pool.shutdownAwait();

        ExecutionException exception = null;
        try {
            pool.execute(component, createQuery(null)).get();
            Assertions.fail();
        } catch (ExecutionException ex) {
            exception = ex;
        }

        assertEquals(GeneralExceptionBuilder.buildServerShutsDownException(), exception.getCause());
    }

    @Test
    public void executeParalleledQuery() throws PlatformException, InterruptedException {
        ResultCallback res = new ResultCallback();
        QueryPool pool = buildPool();
        final int queryCount = QueryPool.MAX_THREAD_COUNT;

        final CountDownLatch countDownLatch = new CountDownLatch(queryCount);
        final AtomicBoolean isParallelShared = new AtomicBoolean(true);

        for (int i = 0; i < queryCount; ++i) {
            pool.execute(component, createQuery(Object.class, QueryPool.LockType.SHARED, () -> {
                countDownLatch.countDown();
                try {
                    if (isParallelShared.get() && !countDownLatch.await(10, TimeUnit.SECONDS)) {
                        isParallelShared.set(false);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }))
            //TODO Ulitin V.
//                    .thenApply(res)
            ;
        }
        pool.shutdownAwait();

        Assertions.assertTrue(res.isSuccess(queryCount), "Не все запросы выполнены успешно.");
        Assertions.assertTrue(isParallelShared.get());
    }

    @Test
    public void executeSerialQuery1() throws PlatformException, InterruptedException {
        ResultCallback res = new ResultCallback();
        QueryPool pool = buildPool();
        final int queryCount = QueryPool.MAX_THREAD_COUNT;

        final AtomicBoolean isExecuted = new AtomicBoolean(false);
        final AtomicBoolean isParallelExclusive = new AtomicBoolean(false);

        for (int i = 0; i < queryCount; ++i) {
            pool.execute(component, createQuery(Object.class, QueryPool.LockType.EXCLUSIVE, () -> {
                if (isExecuted.get()) {
                    isParallelExclusive.set(true);
                }
                isExecuted.set(true);
                try {
                    Thread.sleep(QUERY_DURATION_MILLIS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                isExecuted.set(false);
            }))
            //TODO Ulitin V.
//                    .thenApply(res)
;
        }
        pool.await();

        Assertions.assertTrue(res.isSuccess(queryCount), "Не все запросы выполнены успешно.");
        Assertions.assertFalse(isParallelExclusive.get());
    }

    @Test
    public void executeSerialQuery2() throws PlatformException, InterruptedException {
        ResultCallback res = new ResultCallback();
        QueryPool pool = buildPool();
        final int queryCount = 3;

        final AtomicBoolean isExecuted = new AtomicBoolean(false);
        final AtomicBoolean isParallelExclusive = new AtomicBoolean(false);

        Runnable function = buildSerialControlFunction(isExecuted, isParallelExclusive);

        pool.execute(component, createQuery(Object.class, QueryPool.LockType.EXCLUSIVE, function))
                //TODO Ulitin V.
//                .thenApply(res)
        ;
        pool.execute(component, createQuery(Object.class, QueryPool.LockType.SHARED, function))
//TODO Ulitin V.
// .thenApply(res)
        ;
        pool.execute(component, createQuery(Object.class, QueryPool.LockType.EXCLUSIVE, function))
                //TODO Ulitin V.
//                .thenApply(res)
        ;
        pool.await();

        Assertions.assertTrue(res.isSuccess(queryCount), "Не все запросы выполнены успешно.");
        Assertions.assertFalse(isParallelExclusive.get());
    }

    @Test
    public void executeSerialQueryWithBorrowAllDomainObjects() throws PlatformException, InterruptedException {
        ResultCallback res = new ResultCallback();
        QueryPool pool = buildPool();
        final int queryCount = 3;

        final AtomicBoolean isExecuted = new AtomicBoolean(false);
        final AtomicBoolean isParallelExclusive = new AtomicBoolean(false);

        Runnable function = buildSerialControlFunction(isExecuted, isParallelExclusive);

        pool.execute(component, new Query<SuccessResult>() {

            EditableResource<EmployeeEditable> empRes;

            @Override
            public void prepare(ResourceProvider resources) {
                empRes = resources.getEditableResource(EmployeeEditable.class);
            }

            @Override
            public SuccessResult execute(QueryTransaction transaction) throws PlatformException {
                function.run();
                return new SuccessResult();
            }
        })
                //TODO Ulitin V.
//                .thenApply(res)
;

        pool.execute(component, new Query<SuccessResult>() {

            @Override
            public void prepare(ResourceProvider resources) {
                resources.borrowAllDomainObjects(QueryPool.LockType.SHARED);
            }

            @Override
            public SuccessResult execute(QueryTransaction transaction) throws PlatformException {
                function.run();
                return new SuccessResult();
            }
        })
        //TODO Ulitin V.
//                .thenApply(res)
        ;

        pool.execute(component, new Query<SuccessResult>() {

            EditableResource<EmployeeEditable> empRes;

            @Override
            public void prepare(ResourceProvider resources) {
                empRes = resources.getEditableResource(EmployeeEditable.class);
            }

            @Override
            public SuccessResult execute(QueryTransaction transaction) throws PlatformException {
                function.run();
                return new SuccessResult();
            }
        })
//TODO Ulitin V.
//                .thenApply(res)
        ;

        pool.await();

        Assertions.assertTrue(res.isSuccess(queryCount), "Не все запросы выполнены успешно.");
        Assertions.assertFalse(isParallelExclusive.get());
    }

    @Test
    public void executeSerialQueryWithEditableResource() throws PlatformException, InterruptedException {
        ResultCallback res = new ResultCallback();
        QueryPool pool = buildPool();
        final int queryCount = 3;

        final AtomicBoolean isExecuted = new AtomicBoolean(false);
        final AtomicBoolean isParallelExclusive = new AtomicBoolean(false);

        Runnable function = buildSerialControlFunction(isExecuted, isParallelExclusive);

        pool.execute(component, new Query<SuccessResult>() {

            EditableResource<EmployeeEditable> empRes;

            @Override
            public void prepare(ResourceProvider resources) {
                empRes = resources.getEditableResource(EmployeeEditable.class);
            }

            @Override
            public SuccessResult execute(QueryTransaction transaction) throws PlatformException {
                function.run();
                return new SuccessResult();
            }
        })
//TODO Ulitin V.
//                .thenApply(res)
        ;

        pool.execute(component, new Query<SuccessResult>() {

            EditableResource<EmployeeEditable> empRes;

            @Override
            public void prepare(ResourceProvider resources) {
                empRes = resources.getEditableResource(EmployeeEditable.class);
            }

            @Override
            public SuccessResult execute(QueryTransaction transaction) throws PlatformException {
                function.run();
                return new SuccessResult();
            }
        })
//TODO Ulitin V.
//                .thenApply(res)
        ;

        pool.execute(component, new Query<SuccessResult>() {

            EditableResource<EmployeeEditable> empRes;

            @Override
            public void prepare(ResourceProvider resources) {
                empRes = resources.getEditableResource(EmployeeEditable.class);
            }

            @Override
            public SuccessResult execute(QueryTransaction transaction) throws PlatformException {
                function.run();
                return new SuccessResult();
            }
        })
//TODO Ulitin V.
//                .thenApply(res)
        ;

        pool.await();

        Assertions.assertTrue(res.isSuccess(queryCount), "Не все запросы выполнены успешно.");
        Assertions.assertFalse(isParallelExclusive.get());
    }

    private static Runnable buildSerialControlFunction(AtomicBoolean isExecuted, AtomicBoolean isParallelExclusive) {
        return () -> {
                if (isExecuted.get()) {
                    isParallelExclusive.set(true);
                }
                isExecuted.set(true);
                try {
                    Thread.sleep(QUERY_DURATION_MILLIS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                isExecuted.set(false);
            };
    }

    @Test
    public void executeSerialThanParalleledQuery1() throws PlatformException, InterruptedException {
        ResultCallback res = new ResultCallback();
        QueryPool pool = buildPool();

        final AtomicBoolean isExecutedExclusive = new AtomicBoolean(false);
        final AtomicBoolean isExecutedShared = new AtomicBoolean(false);
        final AtomicBoolean isParallelExclusive = new AtomicBoolean(false);
        final AtomicBoolean isParallelShared = new AtomicBoolean(true);
        final CountDownLatch countDownLatch = new CountDownLatch(2);

        Runnable exclusiveFunction = buildExclusiveControlFunction(isExecutedExclusive, isExecutedShared, isParallelExclusive);
        Runnable sharedFunction = buildSharedControlFunction(isExecutedExclusive, isExecutedShared, isParallelExclusive, isParallelShared, countDownLatch);

        pool.execute(component, createQuery(Object.class, QueryPool.LockType.EXCLUSIVE, exclusiveFunction))
//TODO Ulitin V.
//                .thenApply(res)
        ;
        pool.execute(component, createQuery(Object.class, QueryPool.LockType.SHARED, sharedFunction))
//TODO Ulitin V.
//                .thenApply(res)
        ;
        pool.execute(component, createQuery(Object.class, QueryPool.LockType.SHARED, sharedFunction))
//TODO Ulitin V.
//                .thenApply(res)
        ;
        pool.await();

        Assertions.assertTrue(res.isSuccess(3), "Не все запросы выполнены успешно.");
        Assertions.assertFalse(isParallelExclusive.get());
        Assertions.assertTrue(isParallelShared.get());
    }

    private static Runnable buildSharedControlFunction(AtomicBoolean isExecutedExclusive, AtomicBoolean isExecutedShared,
                                                       AtomicBoolean isParallelExclusive, AtomicBoolean isParallelShared,
                                                       CountDownLatch countDownLatch) {
        return () -> {
                if (isExecutedExclusive.get()) {
                    isParallelExclusive.set(true);
                }
                isExecutedShared.set(true);
                try {
                    Thread.sleep(QUERY_DURATION_MILLIS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                countDownLatch.countDown();
                try {
                    if (isParallelShared.get() && !countDownLatch.await(10, TimeUnit.SECONDS)) {
                        isParallelShared.set(false);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                isExecutedShared.set(false);
            };
    }

    private static Runnable buildExclusiveControlFunction(AtomicBoolean isExecutedExclusive,
                                                          AtomicBoolean isExecutedShared,
                                                          AtomicBoolean isParallelExclusive) {
        return () -> {
                if (isExecutedShared.get()) {
                    isParallelExclusive.set(true);
                }
                isExecutedExclusive.set(true);
                try {
                    Thread.sleep(QUERY_DURATION_MILLIS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                isExecutedExclusive.set(false);
            };
    }

    @Test
    public void executeSerialThanParalleledQuery2() throws PlatformException, InterruptedException {
        ResultCallback res = new ResultCallback();
        QueryPool pool = buildPool();

        final AtomicBoolean isExecutedExclusive = new AtomicBoolean(false);
        final AtomicBoolean isExecutedShared = new AtomicBoolean(false);
        final AtomicBoolean isParallelExclusive = new AtomicBoolean(false);
        final AtomicBoolean isParallelShared = new AtomicBoolean(true);
        final CountDownLatch countDownLatch = new CountDownLatch(2);

        Runnable exclusiveFunction = buildExclusiveControlFunction(isExecutedExclusive, isExecutedShared, isParallelExclusive);
        Runnable sharedFunction = buildSharedControlFunction(isExecutedExclusive, isExecutedShared, isParallelExclusive, isParallelShared, countDownLatch);

        pool.execute(component, createQuery(new HashMap<Class, QueryPool.LockType>() {{
            put(Object.class, QueryPool.LockType.EXCLUSIVE);
            put(Long.class, QueryPool.LockType.SHARED);
        }}, exclusiveFunction))
//TODO Ulitin V.
//                .thenApply(res)
        ;
        pool.execute(component, createQuery(new HashMap<Class, QueryPool.LockType>() {{
            put(Object.class, QueryPool.LockType.SHARED);
            put(Long.class, QueryPool.LockType.SHARED);
        }}, sharedFunction))
//TODO Ulitin V.
//                .thenApply(res)
        ;
        pool.execute(component, createQuery(new HashMap<Class, QueryPool.LockType>() {{
            put(Object.class, QueryPool.LockType.SHARED);
            put(Long.class, QueryPool.LockType.SHARED);
        }}, sharedFunction))
//TODO Ulitin V.
//                .thenApply(res)
        ;
        pool.await();

        Assertions.assertTrue(res.isSuccess(3), "Не все запросы выполнены успешно.");
        Assertions.assertFalse(isParallelExclusive.get());
        Assertions.assertTrue(isParallelShared.get());
    }

    @Test
    public void overloadWorkedQueue() throws PlatformException, InterruptedException {
        QueryPool pool = buildPool();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < QueryPool.MAX_WORKED_QUERY_COUNT + QueryPool.MAX_THREAD_COUNT; ++i) {
            pool.execute(component, createQuery(() -> {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }));
        }

        try {
            pool.execute(component, createQuery(null)).get();
            Assertions.fail();
        } catch (ExecutionException ex) {
            assertEquals(GeneralExceptionBuilder.buildServerOverloadedException(), ex.getCause());
        } finally {
            countDownLatch.countDown();
        }
    }

    @Test
    public void overloadWaitingHighQueue() throws PlatformException, InterruptedException {
        QueryPool pool = buildPool();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < QueryPool.MAX_WAITING_HIGH_QUERY_COUNT + 1; ++i) {
            pool.execute(component, createQuery(
                    Object.class, QueryPool.LockType.EXCLUSIVE, QueryPool.Priority.HIGH, () -> {
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }));
        }

        try {
            pool.execute(component, createQuery(
                    Object.class, QueryPool.LockType.EXCLUSIVE, QueryPool.Priority.HIGH, null)).get();
            Assertions.fail();
        } catch (ExecutionException ex) {
            assertEquals(GeneralExceptionBuilder.buildServerOverloadedException(), ex.getCause());
        } finally {
            countDownLatch.countDown();
        }
    }

    @Test
    public void overloadWaitingLowQueue() throws PlatformException, InterruptedException {
        QueryPool pool = buildPool();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < QueryPool.MAX_WAITING_LOW_QUERY_COUNT + 1; ++i) {
            pool.execute(component, createQuery(
                    Object.class, QueryPool.LockType.EXCLUSIVE, QueryPool.Priority.LOW, () -> {
                        try {
                            countDownLatch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }));
        }

        try {
            pool.execute(component, createQuery(
                    Object.class, QueryPool.LockType.EXCLUSIVE, QueryPool.Priority.LOW, null)).get();
            Assertions.fail();
        } catch (ExecutionException ex) {
            assertEquals(GeneralExceptionBuilder.buildServerOverloadedException(), ex.getCause());
        } finally {
            countDownLatch.countDown();
        }
    }

    @Test
    public void overloadMaintananceQueue() throws PlatformException, InterruptedException {
        final String marker = "loading";
        QueryPool pool = buildPool();

        CountDownLatch countDownLatch = new CountDownLatch(1);

        pool.execute(component, new Query<SuccessResult>() {
            @Override
            public String getMaintenanceMarker() {
                return marker;
            }

            @Override
            public void prepare(ResourceProvider resources) {
                resources.borrowResource(Object.class, QueryPool.LockType.EXCLUSIVE);
            }

            @Override
            public SuccessResult execute(QueryTransaction transaction) throws PlatformException {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return new SuccessResult();
            }
        });

        try {
            pool.execute(component, createQuery(Object.class, QueryPool.LockType.EXCLUSIVE, null)).get();
            Assertions.fail();
        } catch (ExecutionException ex) {
            assertEquals(GeneralExceptionBuilder.buildServerBusyException(marker), ex.getCause());
        } finally {
            countDownLatch.countDown();
        }
    }

    @Test
    public void executeExceptionalQuery() throws Exception {
        QueryPool pool = buildPool();
        pool.execute(component, new Query<Object>() {
            @Override
            public void prepare(ResourceProvider resources) {

            }

            @Override
            public Object execute(QueryTransaction transaction) throws PlatformException {
                throw GeneralExceptionBuilder.buildAccessDeniedException();
            }
        }).exceptionally(throwable -> {
            Assertions.assertEquals(throwable.getClass(), PlatformException.class);
            return null;
        }).get();

        pool.shutdownAwait();
    }

    @Test
    public void tryExecuteImmediately() throws Exception {
        QueryPool pool = buildPool();
        ReentrantLock lock = new ReentrantLock();
        lock.lock();

        QueryFuture<Void> fut1 = pool.tryExecuteImmediately(component, new Query<Void>() {
            @Override
            public void prepare(ResourceProvider resources) {
                resources.borrowResource(Object.class, QueryPool.LockType.EXCLUSIVE);
            }

            @Override
            public Void execute(QueryTransaction transaction) throws PlatformException {
                lock.lock();
                return null;
            }
        });
        Assertions.assertNotNull(fut1);

        QueryFuture fut2 = pool.tryExecuteImmediately(component, new Query<Void>() {
            @Override
            public void prepare(ResourceProvider resources) {
                resources.borrowResource(Object.class, QueryPool.LockType.SHARED);
            }

            @Override
            public Void execute(QueryTransaction transaction) throws PlatformException {
                return null;
            }
        });
        Assertions.assertNull(fut2);

        QueryFuture fut3 = pool.tryExecuteImmediately(component, new Query<Void>() {
            @Override
            public void prepare(ResourceProvider resources) {}

            @Override
            public Void execute(QueryTransaction transaction) throws PlatformException {
                return null;
            }
        });
        Assertions.assertNotNull(fut3);

        lock.unlock();
        fut1.get();
        fut3.get();

        pool.shutdownAwait();
    }

    @Test
    public void waitingQueryExists() throws Exception {
        QueryPool pool = buildPool();
        ReentrantLock lock = new ReentrantLock();
        lock.lock();

        pool.execute(component, new Query<Void>() {
            @Override
            public void prepare(ResourceProvider resources) {
                resources.borrowResource(Object.class, QueryPool.LockType.EXCLUSIVE);
            }

            @Override
            public Void execute(QueryTransaction transaction) throws PlatformException {
                lock.lock();
                return null;
            }
        });

        Assertions.assertFalse(pool.waitingQueryExists(QueryPool.Priority.LOW));
        Assertions.assertFalse(pool.waitingQueryExists(QueryPool.Priority.HIGH));

        QueryFuture fut1 = pool.execute(component, createQuery(
                Object.class, QueryPool.LockType.SHARED, QueryPool.Priority.HIGH, null));
        Assertions.assertFalse(pool.waitingQueryExists(QueryPool.Priority.LOW));
        Assertions.assertTrue(pool.waitingQueryExists(QueryPool.Priority.HIGH));

        QueryFuture fut2 = pool.execute(component, createQuery(
                Object.class, QueryPool.LockType.SHARED, QueryPool.Priority.LOW, null));
        Assertions.assertTrue(pool.waitingQueryExists(QueryPool.Priority.LOW));
        Assertions.assertTrue(pool.waitingQueryExists(QueryPool.Priority.HIGH));

        lock.unlock();
        fut1.get();
        fut2.get();

        Assertions.assertFalse(pool.waitingQueryExists(QueryPool.Priority.LOW));
        Assertions.assertFalse(pool.waitingQueryExists(QueryPool.Priority.HIGH));

        pool.shutdownAwait();
    }

    @Test
    public void check() throws Exception {
        QueryPool pool = buildPool();

        pool.execute(component, new Query<Void>() {

            private ResourceProvider provider;

            @Override
            public void prepare(ResourceProvider resources) {
                provider = resources;
            }

            @Override
            public Void execute(QueryTransaction transaction) {
                try {
                    provider.getReadableResource(EmployeeReadable.class);
                    Assertions.fail();
                } catch (ClosedObjectException e) {
                    Assertions.assertTrue(ResourceProvider.class.isAssignableFrom(e.getCauseClass()));
                }
                return null;
            }
        });

        pool.shutdownAwait();
    }

    @Test
    public void addEmptyReachedListner() throws Exception {
        {
            QueryPool pool = buildPool();
            AtomicInteger fireCount = new AtomicInteger(0);
            pool.addEmptyReachedListner(pool1 -> fireCount.incrementAndGet());

            pool.execute(component, createEmptyQuery());

            pool.await();
            Assertions.assertEquals(1, fireCount.intValue());
        }

        {
            QueryPool pool = buildPool();
            ResultCallback res = new ResultCallback();
            AtomicInteger fireCount = new AtomicInteger(0);
            pool.addEmptyReachedListner(pool1 -> fireCount.incrementAndGet());

            CompletableFuture<Void> fut = new CompletableFuture<>();
            pool.execute(component, createQuery(Object.class, QueryPool.LockType.EXCLUSIVE, () -> {
                pool.execute(component, createQuery(Object.class, QueryPool.LockType.EXCLUSIVE, null))
//TODO Ulitin V.
//                        .thenApply(res)
//                        .thenApply(fut::complete)
                ;
            }))
//TODO Ulitin V.
//                .thenApply(res)
            ;

            fut.get();
            pool.await();

            Assertions.assertTrue(res.isSuccess(2));
            Assertions.assertEquals(1, fireCount.intValue());
        }

        {
            QueryPool pool = buildPool();
            AtomicInteger fireCount1 = new AtomicInteger(0);
            pool.addEmptyReachedListner(pool1 -> fireCount1.incrementAndGet());

            AtomicInteger fireCount2 = new AtomicInteger(0);
            pool.addEmptyReachedListner(pool1 -> fireCount2.incrementAndGet());

            pool.execute(component, createEmptyQuery());

            pool.await();
            Assertions.assertEquals(1, fireCount1.intValue());
            Assertions.assertEquals(1, fireCount2.intValue());
        }
    }

    @Test
    public void removeEmptyReachedListner() throws Exception {
        final AtomicInteger fireCount = new AtomicInteger(0);
        final QueryPool.Callback callback = pool1 -> fireCount.incrementAndGet();

        {
            QueryPool pool = buildPool();
            pool.addEmptyReachedListner(callback);
            pool.removeEmptyReachedListner(callback);

            pool.execute(component, createEmptyQuery());

            pool.await();
            Assertions.assertEquals(0, fireCount.intValue());
        }

        {
            QueryPool pool = buildPool();
            pool.addEmptyReachedListner(callback);
            final AtomicInteger fireCount1 = new AtomicInteger(0);
            pool.addEmptyReachedListner(pool1 -> fireCount1.incrementAndGet());

            pool.removeEmptyReachedListner(callback);

            pool.execute(component, createEmptyQuery());

            pool.await();
            Assertions.assertEquals(0, fireCount.intValue());
            Assertions.assertEquals(1, fireCount1.intValue());
        }
    }

    @Test
    public void tryFireEmptyReachedListner() throws Exception {
        {
            QueryPool pool = buildPool();
            AtomicInteger fireCount = new AtomicInteger(0);
            QueryPool.Callback callback = pool1 -> fireCount.incrementAndGet();
            pool.addEmptyReachedListner(callback);

            pool.tryFireEmptyReachedListener();

            pool.await();
            Assertions.assertEquals(1, fireCount.intValue());
        }

        {
            QueryPool pool = buildPool();
            AtomicInteger fireCount = new AtomicInteger(0);
            QueryPool.Callback callback = pool1 -> fireCount.incrementAndGet();
            pool.addEmptyReachedListner(callback);

            ReentrantLock lock = new ReentrantLock();
            lock.lock();
            pool.execute(component, new Query<Object>() {
                @Override
                public void prepare(ResourceProvider resources) {
                    resources.borrowResource(Object.class, QueryPool.LockType.EXCLUSIVE);
                }

                @Override
                public Object execute(QueryTransaction transaction) throws PlatformException {
                    lock.lock();
                    return null;
                }
            });

            pool.tryFireEmptyReachedListener();
            lock.unlock();

            pool.await();
            Assertions.assertEquals(1, fireCount.intValue());
        }
    }

    @Test
    public void testUncaughtExceptionHandler() throws Throwable {
        testUncaughtExceptionHandler(transaction -> {
            throw new UncaughtException();
        }, new UncaughtException());

        testUncaughtExceptionHandler(transaction -> transaction.addCommitListener(() -> {
            throw new UncaughtException();
        }), new UncaughtException());

        testUncaughtExceptionHandler(transaction -> {
            transaction.addRollbackListener((e) -> {
                throw new UncaughtException();
            });

            throw GeneralExceptionBuilder.buildAccessDeniedException();
        }, new UncaughtException());

        try {
            testUncaughtExceptionHandler(transaction -> {
                throw GeneralExceptionBuilder.buildAccessDeniedException();
            }, null);
            Assertions.fail();
        } catch (PlatformException e) {
            // do nothing
        }
    }

    private void testUncaughtExceptionHandler(Consumer<QueryTransaction> execute, Throwable expected) throws Throwable {
        QueryPool pool = buildPool();

        ExecutionException ee = null;
        try {
            pool.execute(component, new Query<Object>() {
                @Override
                public void prepare(ResourceProvider resources) {
                }

                @Override
                public Object execute(QueryTransaction transaction) throws PlatformException {
                    execute.accept(transaction);
                    return null;
                }
            }).get();
        } catch (CancellationException ignore) {
        } catch (ExecutionException e) {
            ee = e;
        }

        pool.await();

        Assertions.assertEquals(uncaughtException, expected);
        uncaughtException = null;

        if (ee != null) {
            throw ee.getCause();
        }
    }

    private static Query<Object> createEmptyQuery() {
        return new Query<Object>() {
            @Override
            public void prepare(ResourceProvider resources) {
            }

            @Override
            public Object execute(QueryTransaction transaction) throws PlatformException {
                return null;
            }
        };}

    private static Query<SuccessResult> createQuery(Runnable function) {
        return createQuery(null, QueryPool.Priority.HIGH, function);
    }

    private static Query<SuccessResult> createQuery(
            Class resClass, QueryPool.LockType lockType, Runnable function) {
        return createQuery(resClass, lockType, QueryPool.Priority.HIGH, function);
    }

    private static Query<SuccessResult> createQuery(
            Class resClass, QueryPool.LockType lockType, QueryPool.Priority priority, Runnable function) {
        return createQuery(new HashMap<Class, QueryPool.LockType>() {{
            put(resClass, lockType);
        }}, priority, function);
    }

    private static Query<SuccessResult> createQuery(
            HashMap<Class, QueryPool.LockType> res, Runnable function) {
        return createQuery(res, QueryPool.Priority.HIGH, function);
    }

    private static Query<SuccessResult> createQuery(
            HashMap<Class, QueryPool.LockType> res, QueryPool.Priority priority, Runnable function) {
        return new Query<SuccessResult>() {
            private boolean prepareExecuted = false;
            private boolean executeExecuted = false;

            @Override
            public QueryPool.Priority getPriority() {
                return priority;
            }

            @Override
            public void prepare(ResourceProvider resources) {
                Assertions.assertTrue(!prepareExecuted, "Prepare executed twice");
                prepareExecuted = true;

                if (res != null) {
                    res.forEach(resources::borrowResource);
                }
            }

            @Override
            public SuccessResult execute(QueryTransaction transaction) throws PlatformException {
                Assertions.assertTrue(!executeExecuted, "Execute executed twice");
                executeExecuted = true;
                if (function != null) {
                    function.run();
                }
                return new SuccessResult();
            }
        };
    }

    private static void assertEquals(PlatformException expected, Throwable actual) {
        Assertions.assertTrue(PlatformException.equals(expected, (PlatformException) actual));
    }

    private static class UncaughtException extends RuntimeException {

        @Override
        public boolean equals(Object obj) {
            return obj instanceof UncaughtException;
        }
    }
}
