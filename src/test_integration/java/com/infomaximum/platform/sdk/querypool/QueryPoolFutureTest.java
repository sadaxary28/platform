package com.infomaximum.platform.sdk.querypool;

import com.infomaximum.platform.sdk.component.Component;
import com.infomaximum.subsystems.exception.SubsystemException;
import com.infomaximum.subsystems.querypool.Query;
import com.infomaximum.subsystems.querypool.QueryPool;
import com.infomaximum.subsystems.querypool.QueryTransaction;
import com.infomaximum.subsystems.querypool.ResourceProvider;
import com.infomaximum.testcomponent.TestComponent;
import com.infomaximum.testcomponent.exception.TestExceptionBuilder;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

public class QueryPoolFutureTest {

    private static Component component;

    private Throwable uncaughtException = null;

    @BeforeClass
    public static void initialize() {
        component = new TestComponent(null);
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
                        public Boolean execute(QueryTransaction transaction) throws SubsystemException {
                            throw TestExceptionBuilder.buildTrueAssetException();
                        }
                    })
                    .thenApply(aBoolean ->
                            new Query<Void>() {

                                @Override
                                public void prepare(ResourceProvider resources) throws SubsystemException {
                                }

                                @Override
                                public Void execute(QueryTransaction transaction) throws SubsystemException {
                                    return null;
                                }
                            })
                    .thenApply(aBoolean ->
                            new Query<Void>() {

                                @Override
                                public void prepare(ResourceProvider resources) throws SubsystemException {
                                }

                                @Override
                                public Void execute(QueryTransaction transaction) throws SubsystemException {
                                    return null;
                                }
                            }).get();
            Assert.fail();
        } catch (ExecutionException ee) {
            if (ee.getCause() instanceof SubsystemException) {
                SubsystemException se = (SubsystemException) ee.getCause();
                Assert.assertEquals(TestExceptionBuilder.CODE_TRUE_ASSET, se.getCode());
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
                    public Integer execute(QueryTransaction transaction) throws SubsystemException {
                        return testResult;
                    }
                })
                .thenApply(aInteger ->
                        new Query<Void>() {

                            @Override
                            public void prepare(ResourceProvider resources) throws SubsystemException {
                                Assert.assertEquals(testResult, (int)aInteger);
                            }

                            @Override
                            public Void execute(QueryTransaction transaction) throws SubsystemException {
                                return null;
                            }
                        })
                .get();
    }

    private QueryPool buildQueryPool() {
        return new QueryPool((t, e) -> uncaughtException = e);
    }

    @AfterClass
    public static void destroying() {
        component.destroying();
    }
}
