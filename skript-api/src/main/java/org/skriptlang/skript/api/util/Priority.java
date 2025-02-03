package org.skriptlang.skript.api.util;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Priorities are used for things like ordering syntax and loading structures in a specific order.
 * @author Patrick Miller
 */
public interface Priority extends Comparable<Priority> {

	/**
	 * @return A base priority for other priorities to build relationships off of.
	 */
	@Contract("-> new")
	static Priority base() {
		return new SimplePriority();
	}

	/**
	 * Constructs a new priority that is before <code>priority</code>.
	 * Note that this method will not make any changes to the {@link #after()} of <code>priority</code>.
	 * @param priority The priority that will be after the returned priority.
	 * @return A priority that is before <code>priority</code>.
	 */
	@Contract("_ -> new")
	static Priority before(Priority priority) {
		return new SimplePriority(priority, true);
	}

	/**
	 * Constructs a new priority that is after <code>priority</code>.
	 * Note that this method will not make any changes to the {@link #before()} of <code>priority</code>.
	 * @param priority The priority that will be before the returned priority.
	 * @return A priority that is after <code>priority</code>.
	 */
	@Contract("_ -> new")
	static Priority after(Priority priority) {
		return new SimplePriority(priority, false);
	}

	/**
	 * @return A collection of all priorities this priority is known to be after.
	 */
	@Unmodifiable Collection<Priority> after();

	/**
	 * @return A collection of all priorities this priority is known to be before.
	 */
	@Unmodifiable Collection<Priority> before();

	/**
	 * A simple implementation of {@link Priority} that is created by the static methods in {@link Priority}.
	 */
	class SimplePriority implements Priority {

		private final Set<Priority> after;

		private final Set<Priority> before;

		private SimplePriority() {
			this.after = ImmutableSet.of();
			this.before = ImmutableSet.of();
		}

		private SimplePriority(Priority priority, boolean isBefore) {
			Set<Priority> after = new HashSet<>();
			Set<Priority> before = new HashSet<>();
			if (isBefore) {
				before.add(priority);
			} else {
				after.add(priority);
			}
			after.addAll(priority.after());
			before.addAll(priority.before());

			this.after = ImmutableSet.copyOf(after);
			this.before = ImmutableSet.copyOf(before);
		}

		@Override
		public int compareTo(@NotNull Priority other) {
			if (this == other) {
				return 0;
			}

			Collection<Priority> ourBefore = this.before();
			Collection<Priority> otherAfter = other.after();

			// check whether this is known to be before other and whether other is known to be after this
			if (ourBefore.contains(other) || otherAfter.contains(this)) {
				return -1;
			}

			Collection<Priority> ourAfter = this.after();
			Collection<Priority> otherBefore = other.before();

			// check whether this is known to be after other and whether other is known to be before this
			if (ourAfter.contains(other) || otherBefore.contains(this)) {
				return 1;
			}

			// check whether the set of items we are before has common elements with the set of items other is after
			if (ourBefore.stream().anyMatch(otherAfter::contains)) {
				return -1;
			}

			// check whether the set of items we are after has common elements with the set of items other is before
			if (ourAfter.stream().anyMatch(otherBefore::contains)) {
				return 1;
			}

			// there is no meaningful relationship, we consider ourselves the same
			// however, in cases of a custom implementation, we defer to them to determine the relationship
			return (other instanceof SimplePriority) ? 0 : (other.compareTo(this) * -1);
		}

		@Override
		public Collection<Priority> after() {
			return after;
		}

		@Override
		public Collection<Priority> before() {
			return before;
		}

		@Override
		public int hashCode() {
			return Objects.hash(after, before);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Priority priority))
				return false;
			return compareTo(priority) == 0;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("after", after)
					.add("before", before)
					.toString();
		}

	}
}
