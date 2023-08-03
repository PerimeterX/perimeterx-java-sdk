package com.perimeterx.utils;

@FunctionalInterface
public interface PXCallableVoid<E extends Throwable> {
    void apply() throws E;
}
