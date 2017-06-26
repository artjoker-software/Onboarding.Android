package artjoker.com.onboarding;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class RxBus<T> {
    private final PublishSubject<T> bus = PublishSubject.create();

    public void send(T o) {
        bus.onNext(o);
    }

    public Observable<T> toObservable() {
        return bus;
    }
}
