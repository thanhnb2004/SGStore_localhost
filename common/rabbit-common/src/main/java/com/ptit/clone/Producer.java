package com.ptit.clone;

public interface Producer<T> {
    public void fire (T event);
}
