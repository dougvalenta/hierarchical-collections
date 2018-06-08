/*
 * Copyright 2018 Doug Valenta.
 * Licensed under the terms of the MIT License.
 */
package io.codecastle.util;

/**
 *
 * @author Doug Valenta
 */
public class MockKey {
	
	private final MockKey parent;
	
	public MockKey() {
		this(null);
	}
	
	public MockKey(final MockKey parent) {
		this.parent = parent;
	}
	
	public boolean isParentOf(final MockKey other) {
		if (other.parent == null) {
			return false;
		}
		if (other.parent == this) {
			return true;
		}
		return isParentOf(other.parent);
	}
	
}
