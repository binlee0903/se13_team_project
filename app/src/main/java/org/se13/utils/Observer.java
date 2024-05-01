package org.se13.utils;

public class Observer<T> {
    private T value;

    private Subscriber<T> subscriber = new Subscriber.EmptySubscriber<>();

    public void setValue(T value) {
        this.value = value;
        subscriber.collect(this.value);
    }

    public T getValue() {
        return value;
    }

    public void subscribe(Subscriber<T> subscriber) {
        this.subscriber = subscriber;
    }
}
