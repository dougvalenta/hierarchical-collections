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
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		Assert.assertEquals(0, tree.size());
		Assert.assertTrue(tree.isEmpty());
		Iterator<HierarchicalTree.Node<MockKey, Object>> iterator = tree.iterator();
		Assert.assertNotNull(iterator);
		Assert.assertFalse(iterator.hasNext());
		verifyEmpty(tree.get(new MockKey()));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testPutWithNullKey() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		tree.put(null, new Object());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testPutWithNullValue() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		tree.put(new MockKey(), null);
	}
	
	@Test
	public void testPutRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key = new MockKey();
		final Object value = new Object();
		verifyEmpty(tree.put(key, value));
		Assert.assertEquals(1, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key), key, value);
	}
	
	@Test
	public void testPutRootDirectReplace() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key = new MockKey();
		final Object value1 = new Object();
		final Object value2 = new Object();
		tree.put(key, value1);
		verifyPresent(tree.put(key, value2), value1);
		Assert.assertEquals(1, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key), key, value2);
	}
	
	@Test
	public void testPutChildOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		tree.put(key1, value1);
		verifyEmpty(tree.put(key2, value2));
		Assert.assertEquals(2, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
	}
	
	@Test
	public void testPutChildOfRootDirectReplace() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		verifyPresent(tree.put(key2, value3), value2);
		Assert.assertEquals(2, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value3);
	}
	
	@Test
	public void testPutChildOfChildOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		final MockKey key3 = new MockKey(key2);
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		verifyEmpty(tree.put(key3, value3));
		Assert.assertEquals(3, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyOptionalNode(tree.get(key3), key3, value3);
	}
	
	@Test
	public void testPutParentOfChildOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		final MockKey key3 = new MockKey(key2);
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key3, value3);
		verifyEmpty(tree.put(key2, value2));
		Assert.assertEquals(3, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyOptionalNode(tree.get(key3), key3, value3);
	}
	
	@Test
	public void testPutNextOfChildOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		final MockKey key3 = new MockKey(key1);
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		verifyEmpty(tree.put(key3, value3));
		Assert.assertEquals(3, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyOptionalNode(tree.get(key3), key3, value3);
	}
	
	@Test
	public void testPutParentOfChildOfRootWithNext() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		final MockKey key3 = new MockKey(key2);
		final Object value3 = new Object();
		final MockKey key4 = new MockKey(key2);
		final Object value4 = new Object();
		tree.put(key1, value1);
		tree.put(key3, value3);
		tree.put(key4, value4);
		verifyEmpty(tree.put(key2, value2));
		Assert.assertEquals(4, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyOptionalNode(tree.get(key3), key3, value3);
		verifyOptionalNode(tree.get(key4), key4, value4);
	}
	
	@Test
	public void testPutParentOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		tree.put(key2, value2);
		verifyEmpty(tree.put(key1, value1));
		Assert.assertEquals(2, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
	}
	
	@Test
	public void testPutNextOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		final Object value2 = new Object();
		tree.put(key1, value1);
		verifyEmpty(tree.put(key2, value2));
		Assert.assertEquals(2, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
	}
	
	@Test
	public void testPutParentOfRootWithNext() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		final MockKey key3 = new MockKey();
		final Object value3 = new Object();
		tree.put(key2, value2);
		tree.put(key3, value3);
		verifyEmpty(tree.put(key1, value1));
		Assert.assertEquals(3, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyOptionalNode(tree.get(key3), key3, value3);
	}
	
	@Test
	public void testPutNextOfRootDirectReplace() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		final Object value2 = new Object();
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		verifyPresent(tree.put(key2, value3), value2);
		Assert.assertEquals(2, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value3);
	}
	
	@Test
	public void testPutChildOfNextOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		final Object value2 = new Object();
		final MockKey key3 = new MockKey(key2);
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		verifyEmpty(tree.put(key3, value3));
		Assert.assertEquals(3, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyOptionalNode(tree.get(key3), key3, value3);
	}
	
	@Test
	public void testPutParentOfNextOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		final Object value2 = new Object();
		final MockKey key3 = new MockKey(key2);
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key3, value3);
		verifyEmpty(tree.put(key2, value2));
		Assert.assertEquals(3, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyOptionalNode(tree.get(key3), key3, value3);
	}
	
	@Test
	public void testPutNextOfNextOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		final Object value2 = new Object();
		final MockKey key3 = new MockKey();
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		verifyEmpty(tree.put(key3, value3));
		Assert.assertEquals(3, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyOptionalNode(tree.get(key3), key3, value3);
	}
	
	@Test
	public void testPutParentOfNextOfRootWithNext() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		final Object value2 = new Object();
		final MockKey key3 = new MockKey();
		final Object value3 = new Object();
		final MockKey key4 = new MockKey(key2);
		final Object value4 = new Object();
		tree.put(key1, value1);
		tree.put(key4, value4);
		tree.put(key3, value3);
		verifyEmpty(tree.put(key2, value2));
		Assert.assertEquals(4, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyOptionalNode(tree.get(key3), key3, value3);
		verifyOptionalNode(tree.get(key4), key4, value4);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testPutIfNotPresentWithNullKey() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		tree.putIfNotPresent(null, new Object());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testPutIfNotPresentWithNullValue() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		tree.putIfNotPresent(new MockKey(), null);
	}
	
	@Test
	public void testPutIfNotPresentRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key = new MockKey();
		final Object value = new Object();
		Assert.assertTrue(tree.putIfNotPresent(key, value));
		Assert.assertEquals(1, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key), key, value);
	}
	
	@Test
	public void testPutIfNotPresentRootDirectReplace() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key = new MockKey();
		final Object value1 = new Object();
		final Object value2 = new Object();
		tree.put(key, value1);
		Assert.assertFalse(tree.putIfNotPresent(key, value2));
		Assert.assertEquals(1, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key), key, value1);
	}
	
	@Test
	public void testPutIfNotPresentChildOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		tree.put(key1, value1);
		Assert.assertTrue(tree.putIfNotPresent(key2, value2));
		Assert.assertEquals(2, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
	}
	
	@Test
	public void testPutIfNotPresentChildOfRootDirectReplace() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		Assert.assertFalse(tree.putIfNotPresent(key2, value3));
		Assert.assertEquals(2, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
	}
	
	@Test
	public void testPutIfNotPresentChildOfChildOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		final MockKey key3 = new MockKey(key2);
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		Assert.assertTrue(tree.putIfNotPresent(key3, value3));
		Assert.assertEquals(3, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyOptionalNode(tree.get(key3), key3, value3);
	}
	
	@Test
	public void testPutIfNotPresentParentOfChildOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		final MockKey key3 = new MockKey(key2);
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key3, value3);
		Assert.assertTrue(tree.putIfNotPresent(key2, value2));
		Assert.assertEquals(3, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyOptionalNode(tree.get(key3), key3, value3);
	}
	
	@Test
	public void testPutIfNotPresentNextOfChildOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		final MockKey key3 = new MockKey(key1);
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		Assert.assertTrue(tree.putIfNotPresent(key3, value3));
		Assert.assertEquals(3, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyOptionalNode(tree.get(key3), key3, value3);
	}
	
	@Test
	public void testPutIfNotPresentParentOfChildOfRootWithNext() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		final MockKey key3 = new MockKey(key2);
		final Object value3 = new Object();
		final MockKey key4 = new MockKey(key2);
		final Object value4 = new Object();
		tree.put(key1, value1);
		tree.put(key3, value3);
		tree.put(key4, value4);
		Assert.assertTrue(tree.putIfNotPresent(key2, value2));
		Assert.assertEquals(4, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyOptionalNode(tree.get(key3), key3, value3);
		verifyOptionalNode(tree.get(key4), key4, value4);
	}
	
	@Test
	public void testPutIfNotPresentParentOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		tree.put(key2, value2);
		Assert.assertTrue(tree.putIfNotPresent(key1, value1));
		Assert.assertEquals(2, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
	}
	
	@Test
	public void testPutIfNotPresentNextOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		final Object value2 = new Object();
		tree.put(key1, value1);
		Assert.assertTrue(tree.putIfNotPresent(key2, value2));
		Assert.assertEquals(2, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
	}
	
	@Test
	public void testPutIfNotPresentParentOfRootWithNext() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		final MockKey key3 = new MockKey();
		final Object value3 = new Object();
		tree.put(key2, value2);
		tree.put(key3, value3);
		Assert.assertTrue(tree.putIfNotPresent(key1, value1));
		Assert.assertEquals(3, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyOptionalNode(tree.get(key3), key3, value3);
	}
	
	@Test
	public void testPutIfNotPresentNextOfRootDirectReplace() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		final Object value2 = new Object();
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		Assert.assertFalse(tree.putIfNotPresent(key2, value3));
		Assert.assertEquals(2, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
	}
	
	@Test
	public void testPutIfNotPresentChildOfNextOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		final Object value2 = new Object();
		final MockKey key3 = new MockKey(key2);
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		Assert.assertTrue(tree.putIfNotPresent(key3, value3));
		Assert.assertEquals(3, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyOptionalNode(tree.get(key3), key3, value3);
	}
	
	@Test
	public void testPutIfNotPresentParentOfNextOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		final Object value2 = new Object();
		final MockKey key3 = new MockKey(key2);
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key3, value3);
		Assert.assertTrue(tree.putIfNotPresent(key2, value2));
		Assert.assertEquals(3, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyOptionalNode(tree.get(key3), key3, value3);
	}
	
	@Test
	public void testPutIfNotPresentNextOfNextOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		final Object value2 = new Object();
		final MockKey key3 = new MockKey();
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		Assert.assertTrue(tree.putIfNotPresent(key3, value3));
		Assert.assertEquals(3, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyOptionalNode(tree.get(key3), key3, value3);
	}
	
	@Test
	public void testPutIfNotPresentParentOfNextOfRootWithNext() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		final Object value2 = new Object();
		final MockKey key3 = new MockKey();
		final Object value3 = new Object();
		final MockKey key4 = new MockKey(key2);
		final Object value4 = new Object();
		tree.put(key1, value1);
		tree.put(key4, value4);
		tree.put(key3, value3);
		Assert.assertTrue(tree.putIfNotPresent(key2, value2));
		Assert.assertEquals(4, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyOptionalNode(tree.get(key3), key3, value3);
		verifyOptionalNode(tree.get(key4), key4, value4);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testRemoveWithNullKey() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		tree.remove(null);
	}
	
	@Test
	public void testRemoveFromEmpty() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		tree.remove(new MockKey());
	}
	
	@Test
	public void testRemoveRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key = new MockKey();
		final Object value = new Object();
		tree.put(key, value);
		verifyPresent(tree.remove(key), value);
		Assert.assertEquals(0, tree.size());
		Assert.assertTrue(tree.isEmpty());
		verifyEmpty(tree.get(key));
	}
	
	@Test
	public void testRemoveRootWithNext() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		final Object value2 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		verifyPresent(tree.remove(key1), value1);
		Assert.assertEquals(1, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyEmpty(tree.get(key1));
		verifyOptionalNode(tree.get(key2), key2, value2);
	}
	
	@Test
	public void testRemoveRootWithChildAndNext() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		final Object value2 = new Object();
		final MockKey key3 = new MockKey(key1);
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		tree.put(key3, value3);
		verifyPresent(tree.remove(key1), value1);
		Assert.assertEquals(2, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyEmpty(tree.get(key1));
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyOptionalNode(tree.get(key3), key3, value3);
	}
	
	@Test
	public void testRemoveChildOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		verifyPresent(tree.remove(key2), value2);
		Assert.assertEquals(1, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyEmpty(tree.get(key2));
	}
	
	@Test
	public void testRemoveParentOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		tree.put(key2, value2);
		verifyEmpty(tree.remove(key1));
		Assert.assertEquals(1, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyEmpty(tree.get(key1));
		verifyOptionalNode(tree.get(key2), key2, value2);
	}
	
	@Test
	public void testRemoveNextOfRoot() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		final Object value2 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		verifyPresent(tree.remove(key2), value2);
		Assert.assertEquals(1, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyEmpty(tree.get(key2));
		verifyOptionalNode(tree.get(key1), key1, value1);
	}
	
	@Test
	public void testRemoveChildWhenNotPresent() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		tree.put(key1, value1);
		verifyEmpty(tree.remove(key2));
		Assert.assertEquals(1, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyEmpty(tree.get(key2));
	}
	
	@Test
	public void testRemoveChildWithNext() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		final MockKey key3 = new MockKey(key1);
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		tree.put(key3, value3);
		verifyPresent(tree.remove(key2), value2);
		Assert.assertEquals(2, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyEmpty(tree.get(key2));
		verifyOptionalNode(tree.get(key3), key3, value3);
	}
	
	@Test
	public void testRemoveChildWithNextAndChild() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		final MockKey key3 = new MockKey(key1);
		final Object value3 = new Object();
		final MockKey key4 = new MockKey(key2);
		final Object value4 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		tree.put(key3, value3);
		tree.put(key4, value4);
		verifyPresent(tree.remove(key2), value2);
		Assert.assertEquals(3, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyEmpty(tree.get(key2));
		verifyOptionalNode(tree.get(key3), key3, value3);
		verifyOptionalNode(tree.get(key4), key4, value4);
	}
	
	@Test
	public void testRemoveChildOfChild() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		final MockKey key3 = new MockKey(key2);
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		tree.put(key3, value3);
		verifyPresent(tree.remove(key3), value3);
		Assert.assertEquals(2, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyEmpty(tree.get(key3));
	}
	
	@Test
	public void testRemoveParentOfChildWhenNotPresent() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final MockKey key3 = new MockKey(key2);
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key3, value3);
		verifyEmpty(tree.remove(key2));
		Assert.assertEquals(2, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key3), key3, value3);
		verifyEmpty(tree.get(key2));
	}
	
	@Test
	public void testRemoveNextOfChild() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey(key1);
		final Object value2 = new Object();
		final MockKey key3 = new MockKey(key1);
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		tree.put(key3, value3);
		verifyPresent(tree.remove(key3), value3);
		Assert.assertEquals(2, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyEmpty(tree.get(key3));
	}
	
	@Test
	public void testRemoveNextNotPresent() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		tree.put(key1, value1);
		verifyEmpty(tree.remove(key2));
		Assert.assertEquals(1, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyEmpty(tree.get(key2));
	}
	
	@Test
	public void testRemoveNextWithNext() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		final Object value2 = new Object();
		final MockKey key3 = new MockKey();
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		tree.put(key3, value3);
		verifyPresent(tree.remove(key2), value2);
		Assert.assertEquals(2, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key3), key3, value3);
		verifyEmpty(tree.get(key2));
	}
	
	@Test
	public void testRemoveNextWithNextAndChild() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		final Object value2 = new Object();
		final MockKey key3 = new MockKey();
		final Object value3 = new Object();
		final MockKey key4 = new MockKey(key2);
		final Object value4 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		tree.put(key3, value3);
		tree.put(key4, value4);
		verifyPresent(tree.remove(key2), value2);
		Assert.assertEquals(3, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key3), key3, value3);
		verifyOptionalNode(tree.get(key4), key4, value4);
		verifyEmpty(tree.get(key2));
	}
	
	@Test
	public void testRemoveChildOfNext() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		final Object value2 = new Object();
		final MockKey key3 = new MockKey(key2);
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		tree.put(key3, value3);
		verifyPresent(tree.remove(key3), value3);
		Assert.assertEquals(2, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyEmpty(tree.get(key3));
	}
	
	@Test
	public void testRemoveParentOfNextWhenNotPresent() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		final Object value2 = new Object();
		final MockKey key3 = new MockKey(key2);
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key3, value3);
		verifyEmpty(tree.remove(key2));
		Assert.assertEquals(2, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key3), key3, value3);
		verifyEmpty(tree.get(key2));
	}
	
	@Test
	public void testRemoveNextOfNext() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final Object value1 = new Object();
		final MockKey key2 = new MockKey();
		final Object value2 = new Object();
		final MockKey key3 = new MockKey();
		final Object value3 = new Object();
		tree.put(key1, value1);
		tree.put(key2, value2);
		tree.put(key3, value3);
		verifyPresent(tree.remove(key3), value3);
		Assert.assertEquals(2, tree.size());
		Assert.assertFalse(tree.isEmpty());
		verifyOptionalNode(tree.get(key1), key1, value1);
		verifyOptionalNode(tree.get(key2), key2, value2);
		verifyEmpty(tree.get(key3));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetWithNullKey() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		tree.get(null);
	}
	
	@Test
	public void testGetNotPresentParent() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		final MockKey key2 = new MockKey(key1);
		tree.put(key2, new Object());
		verifyEmpty(tree.get(key1));
	}
	
	@Test
	public void testClear() {
		HierarchicalTree<MockKey, Object> tree = new LinkedHierarchicalTree<>(MockKey::isParentOf);
		final MockKey key1 = new MockKey();
		tree.put(key1, new Object());
		tree.clear();
		Assert.assertEquals(0, tree.size());
		Assert.assertTrue(tree.isEmpty());
		verifyEmpty(tree.get(key1));
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
