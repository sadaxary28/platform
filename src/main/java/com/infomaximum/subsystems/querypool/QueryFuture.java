package com.infomaximum.subsystems.querypool;

import com.infomaximum.platform.sdk.component.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class QueryFuture<T> {

	protected final QueryPool queryPool;
	protected final Component subsystem;
	private final CompletableFuture<T> future;

	public QueryFuture(QueryPool queryPool, Component subsystem, CompletableFuture<T> future) {
		this.queryPool = queryPool;
		this.subsystem = subsystem;
		this.future = future;
	}

	protected void complete(T result) {
		future.complete(result);
	}

	protected void completeExceptionally(Throwable ex) {
		future.completeExceptionally(ex);
	}

	protected void cancel(boolean mayInterruptIfRunning) {
		future.cancel(mayInterruptIfRunning);
	}

	public CompletableFuture<T> exceptionally(Function<Throwable, ? extends T> fn) {
		return future.exceptionally(fn);
	}

	public CompletableFuture<Void> thenRun(Runnable action) {
		return future.thenRun(action);
	}

	public <U> CompletableFuture<U> thenApplyCompletableFuture(Function<? super T, ? extends U> fn) {
		return future.thenApply(fn);
	}

	public CompletableFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
		return future.whenComplete(action);
	}

	public <U> QueryFuture<U> thenApply(Function<? super T, Query<? extends U>> fn) {
		return thenApply(fn, true);
	}

	public <U> QueryFuture<U> thenApply(Function<? super T, Query<? extends U>> fn, boolean failIfPoolBusy) {
		QueryFuture<U> queryFuture = new QueryFuture<U>(queryPool, subsystem, new CompletableFuture<>());
		future.thenApply(t -> {
			Query prev = fn.apply(t);
			queryPool.execute(queryFuture, prev, failIfPoolBusy);
			return null;
		}).exceptionally(throwable -> {
			queryFuture.future.completeExceptionally((Throwable) throwable);
			return null;
		});
		return queryFuture;
	}

	public T get() throws ExecutionException, InterruptedException {
		return future.get();
	}

	public T join() {
		return future.join();
	}
}
