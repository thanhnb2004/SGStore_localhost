package com.ptit.clone;

public interface Handle<K,V>{
    V handle (K event);
}
