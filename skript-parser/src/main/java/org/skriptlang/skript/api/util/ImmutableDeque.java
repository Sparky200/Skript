package org.skriptlang.skript.api.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * An unmodifiable view of a stack as a list.
 * @param <T>
 */
public class ImmutableDeque<T> implements Deque<T> {
	private final Deque<T> deque;

	public ImmutableDeque(Deque<T> deque) {
		this.deque = deque;
	}

	@Override
	public int size() {
		return deque.size();
	}

	@Override
	public boolean isEmpty() {
		return deque.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return deque.contains(o);
	}

	@Override
	public @NotNull Iterator<T> iterator() {
		return deque.iterator();
	}

	@Override
	public @NotNull Iterator<T> descendingIterator() {
		return deque.descendingIterator();
	}

	@Override
	public @NotNull Object @NotNull [] toArray() {
		return deque.toArray();
	}

	@Override
	public @NotNull <T1> T1 @NotNull [] toArray(@NotNull T1 @NotNull [] a) {
		return deque.toArray(a);
	}

	@Override
	public void addFirst(T t) {
		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}

	@Override
	public void addLast(T t) {
		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}

	@Override
	public boolean offerFirst(T t) {
		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}

	@Override
	public boolean offerLast(T t) {
		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}

	@Override
	public T removeFirst() {
		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}

	@Override
	public T removeLast() {
		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}

	@Override
	public T pollFirst() {
		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}

	@Override
	public T pollLast() {
		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}

	@Override
	public T getFirst() {
		return deque.getFirst();
	}

	@Override
	public T getLast() {
		return deque.getLast();
	}

	@Override
	public T peekFirst() {
		return deque.peekFirst();
	}

	@Override
	public T peekLast() {
		return deque.peekLast();
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}

	@Override
	public boolean add(T t) {
		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}

	@Override
	public boolean offer(T t) {
		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}

	@Override
	public T remove() {
		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}

	@Override
	public T poll() {
		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}

	@Override
	public T element() {
		return deque.element();
	}

	@Override
	public T peek() {
		return deque.peek();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}

	@Override
	public boolean containsAll(@NotNull Collection<?> c) {
		return deque.containsAll(c);
	}

	@Override
	public boolean addAll(@NotNull Collection<? extends T> c) {
		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}

	@Override
	public void push(T t) {
 		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}

	@Override
	public T pop() {
		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}

	@Override
	public boolean removeAll(@NotNull Collection<?> c) {
		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}

	@Override
	public boolean retainAll(@NotNull Collection<?> c) {
		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("Stack views are unmodifiable");
	}
}
