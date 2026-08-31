package manager.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.concurrent.Callable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReactiveUtils {

    public static <T> Mono<T> fromBlocking(Scheduler scheduler, Callable<T> callable) {
        return Mono.fromCallable(callable)
                .subscribeOn(scheduler);
    }

}
