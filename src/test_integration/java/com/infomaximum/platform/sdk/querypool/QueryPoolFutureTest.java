package com.infomaximum.platform.sdk.querypool;

import com.infomaximum.platform.exception.PlatformException;
import com.infomaximum.platform.querypool.Query;
import com.infomaximum.platform.querypool.QueryPool;
import com.infomaximum.platform.querypool.QueryTransaction;
import com.infomaximum.platform.querypool.ResourceProvider;
import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.testcomponent.TestComponent;
import com.infomaximum.testcomponent.exception.TestExceptionBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public class QueryPoolFutureTest {

    private static Component component;

    private Throwable uncaughtException = null;

    @BeforeAll
    public static void initialize() throws PlatformException {
        component = new TestComponent();
        component.initialize();
    }


    @Test
    public void testChainThrowException() throws ExecutionException, InterruptedException {
        try {
            buildQueryPool().execute(
                    component,
                    new Query<Boolean>() {

                        @Override
                        public void prepare(ResourceProvider resources) {
                        }

                        @Override
                        public Boolean execute(QueryTransaction transaction) throws PlatformException {
                            throw TestExceptionBuilder.buildTrueAssetException();
                        }
                    })
                    .thenApply(aBoolean ->
                            new Query<Void>() {

                                @Override
                                public void prepare(ResourceProvider resources) throws PlatformException {
                                }

                                @Override
                                public Void execute(QueryTransaction transaction) throws PlatformException {
                                    return null;
                                }
                            })
                    .thenApply(aBoolean ->
                            new Query<Void>() {

                                @Override
                                public void prepare(ResourceProvider resources) throws PlatformException {
                                }

                                @Override
                                public Void execute(QueryTransaction transaction) throws PlatformException {
                                    return null;
                                }
                            }).get();
            Assertions.fail();
        } catch (ExecutionException ee) {
            if (ee.getCause() instanceof PlatformException) {
                PlatformException se = (PlatformException) ee.getCause();
                Assertions.assertEquals(TestExceptionBuilder.CODE_TRUE_ASSET, se.getCode());
            } else {
                throw ee;
            }
        }
    }

    @Test
    public void testChainThrowResult() throws ExecutionException, InterruptedException {
        int testResult = 15;
        buildQueryPool().execute(
                component,
                new Query<Integer>() {

                    @Override
                    public void prepare(ResourceProvider resources) {
                    }

                    @Override
                    public Integer execute(QueryTransaction transaction) throws PlatformException {
                        return testResult;
                    }
                })
                .thenApply(aInteger ->
                        new Query<Void>() {

                            @Override
                            public void prepare(ResourceProvider resources) throws PlatformException {
                                Assertions.assertEquals(testResult, (int)aInteger);
                            }

                            @Override
                            public Void execute(QueryTransaction transaction) throws PlatformException {
                                return null;
                            }
                        })
                .get();
    }

    private QueryPool buildQueryPool() {
        return new QueryPool((t, e) -> uncaughtException = e);
    }

    @AfterAll
    public static void destroying() {
        component.destroy();
    }
}
