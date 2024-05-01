package org.se13.utils;

@FunctionalInterface
public interface Subscriber<T> {
    void collect(T value);

    public static class EmptySubscriber<T> implements Subscriber<T> {

        @Override
        public void collect(T value) {

        }
    }
}
