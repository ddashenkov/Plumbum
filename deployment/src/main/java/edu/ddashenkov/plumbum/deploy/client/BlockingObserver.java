package edu.ddashenkov.plumbum.deploy.client;

import io.grpc.stub.StreamObserver;
import io.spine.grpc.MemoizingObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static io.spine.grpc.StreamObservers.memoizingObserver;

final class BlockingObserver<T> implements StreamObserver<T> {

    private final CompletableFuture<Void> future = new CompletableFuture<>();
    private final MemoizingObserver<T> memoizingObserver = memoizingObserver();

    /**
     * The {@code private} constructor prevents direct instantiation.
     */
    private BlockingObserver() {}

    static <T> BlockingObserver<T> instance() {
        return new BlockingObserver<>();
    }

    @Override
    public void onNext(T value) {
        log().info("Received value {}", value.toString());
        memoizingObserver.onNext(value);
    }

    @Override
    public void onError(Throwable t) {
        log().error("Received error", t);
        memoizingObserver.onError(t);
        future.completeExceptionally(t);
    }

    @Override
    public void onCompleted() {
        log().info("Completed stream");
        memoizingObserver.onCompleted();
        future.complete(null);
    }

    T getSingleValue() {
        await();
        return memoizingObserver.firstResponse();
    }

    private void await() {
        future.join();
    }

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }

    private enum LogSingleton {
        INSTANCE;
        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final Logger value = LoggerFactory.getLogger(BlockingObserver.class);
    }
}
