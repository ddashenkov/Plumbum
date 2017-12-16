package edu.ddashenkov.plumbum.webadapter;

import io.grpc.stub.StreamObserver;
import io.spine.grpc.MemoizingObserver;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.spine.grpc.StreamObservers.memoizingObserver;

/**
 * @author Dmytro Dashenkov
 */
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
        memoizingObserver.onNext(value);
    }

    @Override
    public void onError(Throwable t) {
        memoizingObserver.onError(t);
        future.completeExceptionally(t);
    }

    @Override
    public void onCompleted() {
        memoizingObserver.onCompleted();
        future.complete(null);
    }

    T getSingleValue() {
        await();
        return memoizingObserver.firstResponse();
    }

    List<T> getValues() {
        await();
        return memoizingObserver.responses();
    }

    private void await() {
        future.join();
    }
}
