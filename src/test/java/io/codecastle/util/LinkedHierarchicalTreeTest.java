/*
 * Copyright 2018 Doug Valenta.
 * Licensed under the terms of the MIT License.
 */
package io.codecastle.util;

import java.util.Iterator;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Doug Valenta
 */
public class LinkedHierarchicalTreeTest {
	
	@Test
	public void testEmptyTree() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		Assert.assertEquals(0, tree.size());
		Assert.assertTrue(tree.isEmpty());
	}
	
	@Test
	public void testGetFromEmptyTree() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		verifyEmpty(tree.get(Object.class));
	}
	
	@Test
	public void testGetNearestFromEmptyTree() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		verifyEmpty(tree.getNearest(Object.class));
	}
	
	@Test
	public void testRemoveFromEmptyTree() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		verifyEmpty(tree.remove(Object.class));
	}
	
	@Test
	public void testIteratorFromEmptyTree() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Iterator<HierarchicalTree.Node<Class<?>, Object>> iterator = tree.iterator();
		Assert.assertNotNull(iterator);
		Assert.assertFalse(iterator.hasNext());
	}
	
	@Test(expected=IllegalStateException.class)
	public void testNextFromEmptyIterator() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Iterator<HierarchicalTree.Node<Class<?>, Object>> iterator = tree.iterator();
		iterator.next();
	}
	
	@Test(expected=IllegalStateException.class)
	public void testRemoveFromEmptyIterator() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Iterator<HierarchicalTree.Node<Class<?>, Object>> iterator = tree.iterator();
		iterator.remove();
	}
	
	@Test
	public void testPutIfNotPresentToEmptyTree() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final boolean wasPut = tree.putIfNotPresent(Object.class, new Object());
		Assert.assertTrue(wasPut);
		Assert.assertEquals(1, tree.size());
		Assert.assertFalse(tree.isEmpty());
	}
	
	@Test
	public void testPutToEmptyTree() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		verifyEmpty(tree.put(Object.class, new Object()));
		Assert.assertEquals(1, tree.size());
		Assert.assertFalse(tree.isEmpty());
	}
	
	@Test
	public void testGetFromSingleNodeTree() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		tree.put(Object.class, value1);
		HierarchicalTree.Node<Class<?>, Object> node = verifyOptionalNode(tree.get(Object.class), Object.class, value1);
		final Object value2 = new Object();
		node.setValue(value2);
		verifyOptionalNode(tree.get(Object.class), Object.class, value2);
	}
	
	@Test
	public void testGetNearestFromSingleNodeTreeWithExact() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		tree.put(Object.class, value1);
		verifyOptionalNode(tree.getNearest(Object.class), Object.class, value1);
	}
	
	@Test
	public void testGetNearestFromSingleNodeTreeWithDescendant() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		tree.put(Object.class, value1);
		verifyOptionalNode(tree.getNearest(String.class), Object.class, value1);
	}
	
	@Test
	public void testGetNearestFromSingleNodeTreeWithAncestor() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		tree.put(CharSequence.class, value1);
		verifyEmpty(tree.getNearest(Object.class));
	}
	
	@Test
	public void testGetNearestFromSingleNodeTreeWithSibling() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		tree.put(CharSequence.class, value1);
		verifyEmpty(tree.getNearest(Iterator.class));
	}
	
	@Test
	public void testRemoveFromSingleNodeTree() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		tree.put(Object.class, value1);
		verifyPresent(tree.remove(Object.class), value1);
		Assert.assertEquals(0, tree.size());
		Assert.assertTrue(tree.isEmpty());
	}
	
	@Test
	public void testRemoveFromSingleNodeTreeWithNotPresentDescendant() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		tree.put(CharSequence.class, value1);
		verifyEmpty(tree.remove(String.class));
		Assert.assertEquals(1, tree.size());
	}
	
	@Test
	public void testRemoveFromSingleNodeTreeWithNotPresentSibling() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		tree.put(CharSequence.class, value1);
		verifyEmpty(tree.remove(Iterator.class));
		Assert.assertEquals(1, tree.size());
	}
	
	@Test
	public void testRemoveFromSingleNodeTreeWithNotPresentAncestor() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		tree.put(CharSequence.class, value1);
		verifyEmpty(tree.remove(Object.class));
		Assert.assertEquals(1, tree.size());
	}
	
	@Test
	public void testPutToSingleNodeTreeWithAncestor() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		final Object value2 = new Object();
		tree.put(String.class, value1);
		verifyEmpty(tree.put(Object.class, value2));
		Assert.assertEquals(2, tree.size());
		verifyOptionalNode(tree.get(String.class), String.class, value1);
		verifyOptionalNode(tree.get(Object.class), Object.class, value2);
	}
	
	@Test
	public void testPutToSingleNodeTreeWithDescendant() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		final Object value2 = new Object();
		tree.put(Object.class, value1);
		verifyEmpty(tree.put(String.class, value2));
		Assert.assertEquals(2, tree.size());
		verifyOptionalNode(tree.get(Object.class), Object.class, value1);
		verifyOptionalNode(tree.get(String.class), String.class, value2);
	}
	
	@Test
	public void testPutToSingleNodeTreeWithSibling() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		final Object value2 = new Object();
		tree.put(Iterator.class, value1);
		verifyEmpty(tree.put(String.class, value2));
		Assert.assertEquals(2, tree.size());
		verifyOptionalNode(tree.get(Iterator.class), Iterator.class, value1);
		verifyOptionalNode(tree.get(String.class), String.class, value2);
	}
	
	@Test
	public void testPutToSingleNodeTreeWithExact() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		final Object value2 = new Object();
		tree.put(Iterator.class, value1);
		verifyPresent(tree.put(Iterator.class, value2), value1);
		Assert.assertEquals(1, tree.size());
		verifyOptionalNode(tree.get(Iterator.class), Iterator.class, value2);
	}
	
	@Test
	public void testPutIfNotPresentToSingleNodeTreeWithAncestor() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		final Object value2 = new Object();
		tree.put(String.class, value1);
		Assert.assertTrue(tree.putIfNotPresent(Object.class, value2));
		Assert.assertEquals(2, tree.size());
		verifyOptionalNode(tree.get(String.class), String.class, value1);
		verifyOptionalNode(tree.get(Object.class), Object.class, value2);
	}
	
	@Test
	public void testPutIfNotPresentToSingleNodeTreeWithDescendant() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		final Object value2 = new Object();
		tree.put(Object.class, value1);
		Assert.assertTrue(tree.putIfNotPresent(String.class, value2));
		Assert.assertEquals(2, tree.size());
		verifyOptionalNode(tree.get(Object.class), Object.class, value1);
		verifyOptionalNode(tree.get(String.class), String.class, value2);
	}
	
	@Test
	public void testPutIfNotPresentToSingleNodeTreeWithSibling() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		final Object value2 = new Object();
		tree.put(Iterator.class, value1);
		Assert.assertTrue(tree.putIfNotPresent(String.class, value2));
		Assert.assertEquals(2, tree.size());
		verifyOptionalNode(tree.get(Iterator.class), Iterator.class, value1);
		verifyOptionalNode(tree.get(String.class), String.class, value2);
	}
	
	@Test
	public void testPutIfNotPresentToSingleNodeTreeWithExact() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		final Object value2 = new Object();
		tree.put(Iterator.class, value1);
		Assert.assertFalse(tree.putIfNotPresent(Iterator.class, value2));
		Assert.assertEquals(1, tree.size());
		verifyOptionalNode(tree.get(Iterator.class), Iterator.class, value1);
	}
	
	@Test
	public void testIteratorFromSingleNodeTree() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		tree.put(Object.class, value1);
		final Iterator<HierarchicalTree.Node<Class<?>, Object>> iterator = tree.iterator();
		Assert.assertNotNull(iterator);
		Assert.assertTrue(iterator.hasNext());
		final HierarchicalTree.Node<Class<?>, Object> node = iterator.next();
		Assert.assertNotNull(node);
		Assert.assertEquals(Object.class, node.getKey());
		Assert.assertEquals(value1, node.getValue());
		Assert.assertFalse(iterator.hasNext());
	}
	
	@Test(expected=IllegalStateException.class)
	public void testIteratorFromSingleNodeTreeWhenExhausted() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		tree.put(Object.class, value1);
		final Iterator<HierarchicalTree.Node<Class<?>, Object>> iterator = tree.iterator();
		Assert.assertNotNull(iterator);
		Assert.assertTrue(iterator.hasNext());
		Assert.assertNotNull(iterator.next());
		Assert.assertFalse(iterator.hasNext());
		iterator.next();
	}
	
	@Test(expected=IllegalStateException.class)
	public void testRemoveFromIteratorFromSingleNodeTreeWhenNotIterated() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		tree.put(Object.class, value1);
		final Iterator<HierarchicalTree.Node<Class<?>, Object>> iterator = tree.iterator();
		Assert.assertNotNull(iterator);
		iterator.remove();
	}
	
	@Test
	public void testRemoveFromIteratorFromSingleNodeTree() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		tree.put(Object.class, value1);
		final Iterator<HierarchicalTree.Node<Class<?>, Object>> iterator = tree.iterator();
		Assert.assertNotNull(iterator);
		Assert.assertTrue(iterator.hasNext());
		Assert.assertNotNull(iterator.next());
		Assert.assertFalse(iterator.hasNext());
		iterator.remove();
		Assert.assertEquals(0, tree.size());
		Assert.assertTrue(tree.isEmpty());
		Assert.assertFalse(iterator.hasNext());
	}
	
	@Test(expected=IllegalStateException.class)
	public void testNextAfterRemoveFromIteratorFromSingleNodeTree() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		tree.put(Object.class, value1);
		final Iterator<HierarchicalTree.Node<Class<?>, Object>> iterator = tree.iterator();
		Assert.assertNotNull(iterator);
		Assert.assertTrue(iterator.hasNext());
		Assert.assertNotNull(iterator.next());
		Assert.assertFalse(iterator.hasNext());
		iterator.remove();
		iterator.next();
	}
	
	@Test(expected=IllegalStateException.class)
	public void testRemoveAfterRemoveFromIteratorFromSingleNodeTree() {
		final HierarchicalTree<Class<?>, Object> tree = new LinkedHierarchicalTree<>(Class::isAssignableFrom);
		final Object value1 = new Object();
		tree.put(Object.class, value1);
		final Iterator<HierarchicalTree.Node<Class<?>, Object>> iterator = tree.iterator();
		Assert.assertNotNull(iterator);
		Assert.assertTrue(iterator.hasNext());
		Assert.assertNotNull(iterator.next());
		Assert.assertFalse(iterator.hasNext());
		iterator.remove();
		iterator.remove();
	}
	
	private static <K, V> HierarchicalTree.Node<K, V> verifyOptionalNode(Optional<HierarchicalTree.Node<K, V>> optionalNode, K expectedKey, V expectedValue) {
		Assert.assertNotNull(optionalNode);
		Assert.assertTrue(optionalNode.isPresent());
		final HierarchicalTree.Node<K, V> node = optionalNode.get();
		Assert.assertEquals(expectedKey, node.getKey());
		Assert.assertEquals(expectedValue, node.getValue());
		return node;
	}
	
	private static void verifyEmpty(Optional<?> optional) {
		Assert.assertNotNull(optional);
		Assert.assertFalse(optional.isPresent());
	}
	
	private static <V> void verifyPresent(Optional<V> optional, final V expectedValue) {
		Assert.assertNotNull(optional);
		Assert.assertTrue(optional.isPresent());
		Assert.assertEquals(expectedValue, optional.get());
	}
	
}
