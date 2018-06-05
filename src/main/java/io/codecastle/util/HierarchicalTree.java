/*
 * Copyright 2018 Doug Valenta.
 * Licensed under the terms of the MIT License.
 */
package io.codecastle.util;

import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Doug Valenta
 */
public interface HierarchicalTree<K, V> extends Iterable<HierarchicalTree.Node<K, V>> {
	
	interface Node<K, V> extends Map.Entry<K, V> {}
	
	int size();
	
	boolean isEmpty();
	
	void clear();
	
	Optional<V> put(K key, V value);
	
	boolean putIfNotPresent(K key, V value);
	
	Optional<V> remove(K key);
	
	Optional<Node<K, V>> getNearest(K key);
	
	Optional<Node<K, V>> get(K key);
	
}
