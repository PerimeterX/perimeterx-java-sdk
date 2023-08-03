package com.perimeterx.utils;

@FunctionalInterface
public interface PXCallable<T,E extends Throwable> {
    T apply() throws E;
}
