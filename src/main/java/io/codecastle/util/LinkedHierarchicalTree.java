/*
 * Copyright 2018 Doug Valenta.
 * Licensed under the terms of the MIT License.
 */
package io.codecastle.util;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Doug Valenta
 */
public class LinkedHierarchicalTree<K, V> implements HierarchicalTree<K, V> {
	
	private class Node implements HierarchicalTree.Node<K, V> {
		
		private K key;
		private V value;
		
		private Node next;
		private Node child;
		
		Node(final K key, final V value) {
			this.key = key;
			this.value = value;
		}
		
		void next(Node next) {
			if (this.next == null) {
				this.next = next;
			} else {
				this.next.next(next);
			}
		}
		
		void reparent(Node parent) {
			if (isAncestorFunction.apply(parent.key, key)) {
				if (next != null) {
					next.next(parent.next);
					parent.next = next;
					next.reparent(parent);
				}
				next = parent.child;
				parent.child = this;
			}
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(final V value) {
			if (value == null) throw new IllegalArgumentException("Null value");
			final V replaced = this.value;
			this.value = value;
			return replaced;
		}
		
		@Override
		public boolean equals(final Object other) {
			if (!(other instanceof Map.Entry)) {
				return false;
			}
			final Map.Entry<K, V> entry = (Map.Entry<K, V>) other;
			return (key.equals(entry.getKey()) && value.equals(entry.getValue()));
		}
		
		@Override
		public int hashCode() {
			return key.hashCode() ^ value.hashCode();
		}
		
	}
	
	private class NodeIterator implements Iterator<HierarchicalTree.Node<K, V>> {

		private Node current;
		private Node parent;
		private Node previous;
		private Deque<Node> stack = new LinkedList<>();
		
		@Override
		public boolean hasNext() {
			if (!stack.isEmpty()) return true;
			if (current == null) {
				return (parent != null || previous != null || root != null);
			} else {
				return (current.child != null || current.next != null);
			}
		}

		@Override
		public Node next() {
			if (current == null) {
				if (parent != null) {
					if (parent.child != null) {
						current = parent.child;
						return current;
					}
				} else if (previous != null) {
					if (previous.next != null) {
						current = previous.next;
						return current;
					}
				} else if (root != null) {
					current = root;
					return current;
				}
			} else if (current.child != null) {
				parent = current;
				previous = null;
				current = current.child;
				if (current.next != null) {
					stack.add(current);
				}
				return current;
			} else if (current.next != null) {
				parent = null;
				previous = current;
				current = current.next;
				return current;
			}
			if (!stack.isEmpty()) {
				parent = null;
				previous = stack.element();
				current = previous.next;
				return current;
			}
			throw new IllegalStateException("Iterator exhausted");
		}
		
		@Override
		public void remove() {
			if (current == null) {
				throw new IllegalStateException("No iteration");
			}
			if (current == root) {
				if (root.next != null) {
				if (root.child != null) {
					root.next.next(root.child);
				}
				root = root.next;
				} else {
					root = root.child;
				}
				current = null;
			} else if (parent != null) {
				if (current.next != null) {
					if (current.child != null) {
						current.next(current.child);
					}
					parent.child = current.next;
				} else {
					parent.child = current.child;
				}
				current = null;
			} else if (previous != null) {
				if (current.next != null) {
					if (current.child != null) {
						current.next(current.child);
					}
					previous.next = current.next;
				} else {
					previous.next = current.child;
				}
				current = null;
			}
			size--;
		}
		
	}

	private final ToBooleanBiFunction<K, K> isAncestorFunction;
	
	private Node root;
	private int size;
	
	public LinkedHierarchicalTree(final ToBooleanBiFunction<K, K> isAncestorFunction) {
		this.isAncestorFunction = isAncestorFunction;
	}
	
	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public void clear() {
		root = null;
		size = 0;
	}
	
	@Override
	public Optional<V> put(K key, V value) {
		if (key == null) throw new IllegalArgumentException("Null key");
		if (value == null) throw new IllegalArgumentException("Null value");
		if (root == null) {
			root = new Node(key, value);
			size++;
			return Optional.empty();
		}
		if (root.key.equals(key)) {
			V replaced = root.value;
			root.value = value;
			return Optional.of(replaced);
		}
		if (isAncestorFunction.apply(root.key, key)) {
			return putChild(key, value, root);
		}
		if (isAncestorFunction.apply(key, root.key)) {
			final Node newRoot = new Node(key, value);
			newRoot.child = root;
			newRoot.next = root.next;
			root.next = null;
			root = newRoot;
			if (root.next != null) {
				root.next.reparent(root);
			}
			size++;
			return Optional.empty();
		}
		return putNext(key, value, root);
	}
	
	private Optional<V> putChild(final K key, final V value, final Node node) {
		if (node.child == null) {
			node.child = new Node(key, value);
			size++;
			return Optional.empty();
		}
		if (node.child.key.equals(key)) {
			final V replaced = node.child.value;
			node.child.value = value;
			return Optional.of(replaced);
		}
		if (isAncestorFunction.apply(node.child.key, key)) {
			return putChild(key, value, node.child);
		}
		if (isAncestorFunction.apply(key, node.child.key)) {
			final Node newChild = new Node(key, value);
			newChild.child = node.child;
			newChild.next = node.child.next;
			node.child.next = null;
			node.child = newChild;
			if (newChild.next != null) {
				newChild.next.reparent(newChild);
			}
			size++;
			return Optional.empty();
		}
		return putNext(key, value, node.child);
	}
	
	private Optional<V> putNext(final K key, final V value, final Node node) {
		if (node.next == null) {
			node.next = new Node(key, value);
			size++;
			return Optional.empty();
		}
		if (node.next.key.equals(key)) {
			final V replaced = node.next.value;
			node.next.value = value;
			return Optional.of(replaced);
		}
		if (isAncestorFunction.apply(node.next.key, key)) {
			return putChild(key, value, node.next);
		}
		if (isAncestorFunction.apply(key, node.next.key)) {
			final Node newNext = new Node(key, value);
			newNext.child = node.next;
			newNext.next = node.next.next;
			node.next.next = null;
			node.next = newNext;
			if (newNext.next != null) {
				newNext.next.reparent(newNext);
			}
			size++;
			return Optional.empty();
		}
		return putNext(key, value, node.next);
	}
	
	@Override
	public boolean putIfNotPresent(K key, V value) {
		if (key == null) throw new IllegalArgumentException("Null key");
		if (value == null) throw new IllegalArgumentException("Null value");
		if (root == null) {
			root = new Node(key, value);
			size++;
			return true;
		}
		if (root.key.equals(key)) {
			return false;
		}
		if (isAncestorFunction.apply(root.key, key)) {
			return putChildIfNotPresent(key, value, root);
		}
		if (isAncestorFunction.apply(key, root.key)) {
			final Node newRoot = new Node(key, value);
			newRoot.child = root;
			newRoot.next = root.next;
			root.next = null;
			root = newRoot;
			if (root.next != null) {
				root.next.reparent(root);
			}
			size++;
			return true;
		}
		return putNextIfNotPresent(key, value, root);
	}
	
	private boolean putChildIfNotPresent(final K key, final V value, final Node node) {
		if (node.child == null) {
			node.child = new Node(key, value);
			size++;
			return true;
		}
		if (node.child.key.equals(key)) {
			return false;
		}
		if (isAncestorFunction.apply(node.child.key, key)) {
			return putChildIfNotPresent(key, value, node.child);
		}
		if (isAncestorFunction.apply(key, node.child.key)) {
			final Node newChild = new Node(key, value);
			newChild.child = node.child;
			newChild.next = node.child.next;
			node.child.next = null;
			node.child = newChild;
			if (newChild.next != null) {
				newChild.next.reparent(newChild);
			}
			size++;
			return true;
		}
		return putNextIfNotPresent(key, value, node.child);
	}
	
	private boolean putNextIfNotPresent(final K key, final V value, final Node node) {
		if (node.next == null) {
			node.next = new Node(key, value);
			size++;
			return true;
		}
		if (node.next.key.equals(key)) {
			return false;
		}
		if (isAncestorFunction.apply(node.next.key, key)) {
			return putChildIfNotPresent(key, value, node.next);
		}
		if (isAncestorFunction.apply(key, node.next.key)) {
			final Node newNext = new Node(key, value);
			newNext.child = node.next;
			newNext.next = node.next.next;
			node.next.next = null;
			node.next = newNext;
			if (newNext.next != null) {
				newNext.next.reparent(newNext);
			}
			size++;
			return true;
		}
		return putNextIfNotPresent(key, value, node.next);
	}
	
	@Override
	public Optional<V> remove(final K key) {
		if (key == null) throw new IllegalArgumentException("Null key");
		if (root == null) {
			return Optional.empty();
		}
		if (root.key.equals(key)) {
			final V removed = root.value;
			if (root.next != null) {
				if (root.child != null) {
					root.next.next(root.child);
				}
				root = root.next;
			} else {
				root = root.child;
			}
			size--;
			return Optional.of(removed);
		}
		if (isAncestorFunction.apply(root.key, key)) {
			return removeChild(key, root);
		}
		if (isAncestorFunction.apply(key, root.key)) {
			return Optional.empty();
		}
		return removeNext(key, root);
	}
	
	private Optional<V> removeChild(final K key, final Node node) {
		if (node.child == null) {
			return Optional.empty();
		}
		if (node.child.key.equals(key)) {
			final V removed = node.child.value;
			if (node.child.next != null) {
				if (node.child.child != null) {
					node.child.next(node.child.child);
				}
				node.child = node.child.next;
			} else {
				node.child = node.child.child;
			}
			size--;
			return Optional.of(removed);
		}
		if (isAncestorFunction.apply(node.child.key, key)) {
			return removeChild(key, node.child);
		}
		if (isAncestorFunction.apply(key, node.child.key)) {
			return Optional.empty();
		}
		return removeNext(key, node.child);
	}
	
	private Optional<V> removeNext(final K key, final Node node) {
		if (node.next == null) {
			return Optional.empty();
		}
		if (node.next.key.equals(key)) {
			final V removed = node.next.value;
			if (node.next.next != null) {
				if (node.next.child != null) {
					node.next.next(node.next.child);
				}
				node.next = node.next.next;
			} else {
				node.next = node.next.child;
			}
			size--;
			return Optional.of(removed);
		}
		if (isAncestorFunction.apply(node.next.key, key)) {
			return removeChild(key, node.next);
		}
		if (isAncestorFunction.apply(key, node.next.key)) {
			return Optional.empty();
		}
		return removeNext(key, node.next);
	}
	
	@Override
	public Optional<HierarchicalTree.Node<K, V>> get(final K key) {
		if (key == null) throw new IllegalArgumentException("Null key");
		return get(key, root);
	}
	
	private Optional<HierarchicalTree.Node<K, V>> get(final K key, final Node node) {
		if (node == null) return Optional.empty();
		if (node.key.equals(key)) return Optional.of(node);
		if (isAncestorFunction.apply(node.key, key)) return get(key, node.child);
		if (isAncestorFunction.apply(key, node.key)) return Optional.empty();
		return get(key, node.next);
	}
	
	@Override
	public Optional<HierarchicalTree.Node<K, V>> getNearest(final K key) {
		if (key == null) throw new IllegalArgumentException("Null key");
		return getNearest(key, root, null);
	}
	
	private Optional<HierarchicalTree.Node<K, V>> getNearest(final K key, final Node node, final Node candidate) {
		if (node == null) return Optional.ofNullable(candidate);
		if (node.key.equals(key)) return Optional.of(node);
		if (isAncestorFunction.apply(node.key, key)) return getNearest(key, node.child, node);
		if (isAncestorFunction.apply(key, node.key)) return Optional.ofNullable(candidate);
		return getNearest(key, node.next, candidate);
	}
	
	@Override
	public Iterator<HierarchicalTree.Node<K, V>> iterator() {
		return new NodeIterator();
	}
	
}
