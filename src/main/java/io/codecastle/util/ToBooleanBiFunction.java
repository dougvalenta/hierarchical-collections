/*
 * Copyright 2018 Doug Valenta.
 * Licensed under the terms of the MIT License.
 */
package io.codecastle.util;

/**
 *
 * @author Doug Valenta
 */
@FunctionalInterface
public interface ToBooleanBiFunction<T, U> {
	
	public boolean apply(T t, U u);
	
}
