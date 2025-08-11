package org.skriptlang.skript.api.nodes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.ParseContext;
import org.skriptlang.skript.api.entries.EntryStructureDefinition;
import org.skriptlang.skript.api.entries.StructureEntryNode;
import org.skriptlang.skript.api.types.SkriptType;

import java.util.*;

/**
 * Factory helpers for building concrete SyntaxNodeType implementations using a builder pattern.
 * <p>
 * This class exists to make addon code concise and type-safe when declaring new node types.
 * It provides three entry points: {@link #expression()}, {@link #effect()}, and {@link #structure()}.
 * Each returns a builder with specific to the respective node type.
 */
public class NodeTypeBuilders {

	/**
	 * Functional interface to create a node instance from parsed children and a matched pattern index.
	 * @param <T> Node type produced.
	 */
	@FunctionalInterface
	public interface CreateFunction<T> {
		T create(List<SyntaxNode> children, int matchedPattern);
	}

	/**
	 * Functional interface used to decide whether a node type can be parsed in a given context.
	 * Should be deterministic with respect to the provided inputs.
	 */
	@FunctionalInterface
	public interface ParsePredicateFunction {
		boolean canBeParsed(ParseContext context, int matchedPattern);
	}

	/**
	 * Functional interface to create a structure node, with optional entries.
	 * @param <T> Structure node type produced.
	 */
	@FunctionalInterface
	public interface StructureCreateFunction<T> {
		T create(List<SyntaxNode> children, int matchedPattern, @Nullable Map<String, StructureEntryNode> entries);
	}

	/**
	 * Entry point for building an ExpressionNodeType.
	 */
	public static <T extends ExpressionNode> ExpressionBuilder<T> expression() {
		return new ExprBuilder<>();
	}

	public static <T extends ExpressionNode> ExpressionBuilder<T> expression(Class<T> expressionClass) {
		return expression();
	}

	/**
	 * Entry point for building an EffectNodeType.
	 */
	public static <T extends EffectNode> EffectBuilder<T> effect() {
		return new EffBuilder<>();
	}

	public static <T extends EffectNode> EffectBuilder<T> effect(Class<T> effectClass) {
		return effect();
	}

	/**
	 * Entry point for building a StructureNodeType.
	 */
	public static <T extends StructureNode> StructureBuilder<T> structure() {
		return new StructBuilder<>();
	}

	public static <T extends StructureNode> StructureBuilder<T> structure(Class<T> structureClass) {
		return structure();
	}

	/**
	 * Builder pattern for ExpressionNodeType instances.
	 */
	public interface ExpressionBuilder<T extends ExpressionNode> {
		/** Replace the syntax list for this node type. */
		ExpressionBuilder<T> syntaxes(List<String> syntaxes);
		/** Replace the syntax list for this node type. */
		ExpressionBuilder<T> syntaxes(String... syntaxes);
		/** Provide the creation function for this node type (required). */
		ExpressionBuilder<T> create(CreateFunction<T> createFn);
		/** Optional parse predicate to further restrict when this syntax is considered. */
		ExpressionBuilder<T> canBeParsed(ParsePredicateFunction predicate);
		/** Optional filter of allowed parent node types. Empty means allowed anywhere. */
		ExpressionBuilder<T> allowedParents(SyntaxNodeType<?>... parents);
		/** Optional hint to allow this node type to be the parent of itself. */
		ExpressionBuilder<T> allowSelfAsParent();
		/** Optional hint of possible return types used by the parser to narrow candidates. */
		ExpressionBuilder<T> possibleReturnTypes(String... typeNames);
		/** Optional hint of possible return types used by the parser to narrow candidates. */
		ExpressionBuilder<T> possibleReturnTypes(SkriptType<?>... types);
		/** Build the ExpressionNodeType. */
		ExpressionNodeType<T> build();
	}

	/**
	 * Builder pattern for EffectNodeType instances.
	 */
	public interface EffectBuilder<T extends EffectNode> {
		/** Replace the syntax list for this node type. */
		EffectBuilder<T> syntaxes(List<String> syntaxes);
		/** Replace the syntax list for this node type. */
		EffectBuilder<T> syntaxes(String... syntaxes);
		/** Provide the creation function for this node type (required). */
		EffectBuilder<T> create(CreateFunction<T> createFn);
		/** Optional parse predicate to further restrict when this syntax is considered. */
		EffectBuilder<T> canBeParsed(ParsePredicateFunction predicate);
		/** Optional filter of allowed parent node types. Empty means allowed anywhere. */
		EffectBuilder<T> allowedParents(SyntaxNodeType<?>... parents);
		/** Optional hint to allow this node type to be the parent of itself. */
		EffectBuilder<T> allowSelfAsParent();
		/** Build the EffectNodeType. */
		EffectNodeType<T> build();
	}

	/**
	 * Builder pattern for StructureNodeType instances.
	 */
	public interface StructureBuilder<T extends StructureNode> {
		/** Replace the syntax list for this node type. */
		StructureBuilder<T> syntaxes(List<String> syntaxes);
		/** Replace the syntax list for this node type. */
		StructureBuilder<T> syntaxes(String... syntaxes);
		/** Optional entry structure definition (only used when syntax contains <entries>). */
		StructureBuilder<T> structure(@Nullable EntryStructureDefinition definition);
		/** Provide the creation function for this node type (required). */
		StructureBuilder<T> create(StructureCreateFunction<T> createFn);
		/** Optional parse predicate to further restrict when this syntax is considered. */
		StructureBuilder<T> canBeParsed(ParsePredicateFunction predicate);
		/** Optional filter of allowed parent node types. Empty means allowed anywhere. */
		StructureBuilder<T> allowedParents(SyntaxNodeType<?>... parents);
		/** Optional hint to allow this node type to be the parent of itself. */
		StructureBuilder<T> allowSelfAsParent();
		/** Build the StructureNodeType. */
		StructureNodeType<T> build();
	}

	/**
	 * Internal base to share common builder behavior.
	 */
	private static abstract class BaseBuilder<B extends BaseBuilder<B>> {
		/** In-order list of syntax patterns for the node type. */
		protected final List<String> syntaxes = new ArrayList<>();
		/** Optional parse predicate. */
		protected ParsePredicateFunction predicate;
		/** Optional filter of allowed parent node types. */
		protected SyntaxNodeType<?>[] allowedParents;
		protected boolean allowSelfAsParent;

		@SuppressWarnings("unchecked")
		private B self() { return (B) this; }

		/** Replace syntaxes with provided list. */
		public B syntaxes(List<String> syntaxes) {
			this.syntaxes.clear();
			this.syntaxes.addAll(syntaxes);
			return self();
		}

		/** Replace syntaxes with provided varargs. */
		public B syntaxes(String... syntaxes) {
			this.syntaxes.clear();
			Collections.addAll(this.syntaxes, syntaxes);
			return self();
		}

		/** Set optional parse predicate. */
		public B canBeParsed(ParsePredicateFunction predicate) {
			this.predicate = predicate;
			return self();
		}

		/** Set optional allowed parents filter. */
		public B allowedParents(SyntaxNodeType<?>... parents) {
			this.allowedParents = parents;
			return self();
		}

		public B allowSelfAsParent() {
			this.allowSelfAsParent = true;
			return self();
		}

		// Snapshots used during build() to capture current state into the anonymous type
		protected List<String> snapshotSyntaxes() { return List.copyOf(this.syntaxes); }
		protected ParsePredicateFunction snapshotPredicate() { return this.predicate; }
		protected SyntaxNodeType<?>[] snapshotAllowedParents() { return this.allowedParents; }
	}

	/** Concrete builder for ExpressionNodeType. */
	private static final class ExprBuilder<T extends ExpressionNode> extends BaseBuilder<ExprBuilder<T>> implements ExpressionBuilder<T> {
		private CreateFunction<T> createFn;
		private String[] possibleReturnTypes;

		@Override
		public ExpressionBuilder<T> create(CreateFunction<T> createFn) {
			this.createFn = createFn;
			return this;
		}

		@Override
		public ExpressionBuilder<T> possibleReturnTypes(String... typeNames) {
			this.possibleReturnTypes = typeNames;
			return this;
		}

		@Override
		public ExpressionBuilder<T> possibleReturnTypes(SkriptType<?>... types) {
			return possibleReturnTypes(Arrays.stream(types).map(SkriptType::typeName).toArray(String[]::new));
		}

		@Override
		public ExpressionNodeType<T> build() {
			final List<String> syntaxesCopy = snapshotSyntaxes();
			final CreateFunction<T> createFnLocal = this.createFn;
			final ParsePredicateFunction predicateLocal = snapshotPredicate();
			final SyntaxNodeType<?>[] allowedParentsLocal = snapshotAllowedParents();
			final String[] possibleReturnTypesLocal = this.possibleReturnTypes;
			final boolean allowSelfAsParentLocal = this.allowSelfAsParent;
			return new ExpressionNodeType<>() {
				@Override
				public List<String> getSyntaxes() { return syntaxesCopy; }

				@Override
				public @NotNull T create(List<SyntaxNode> children, int matchedPattern) {
					if (createFnLocal == null) throw new IllegalStateException("Create function not set for expression node type");
					return createFnLocal.create(children, matchedPattern);
				}

				@Override
				public boolean canBeParsed(ParseContext context, int matchedPattern) {
					return predicateLocal == null || predicateLocal.canBeParsed(context, matchedPattern);
				}

				@Override
				public SyntaxNodeType<?>[] allowedParents() {
					List<SyntaxNodeType<?>> parents = new LinkedList<>();
					if (allowedParentsLocal != null) {
						Collections.addAll(parents, allowedParentsLocal);
					}
					if (allowSelfAsParentLocal) {
						parents.add(this);
					}
					return parents.toArray(new SyntaxNodeType<?>[0]);
				}

				@Override
				public String[] possibleReturnTypes() {
					return (possibleReturnTypesLocal == null || possibleReturnTypesLocal.length == 0)
						? ExpressionNodeType.super.possibleReturnTypes()
						: possibleReturnTypesLocal;
				}
			};
		}
	}

	/** Concrete builder for EffectNodeType. */
	private static final class EffBuilder<T extends EffectNode> extends BaseBuilder<EffBuilder<T>> implements EffectBuilder<T> {
		private CreateFunction<T> createFn;

		@Override
		public EffectBuilder<T> create(CreateFunction<T> createFn) {
			this.createFn = createFn;
			return this;
		}

		@Override
		public EffectNodeType<T> build() {
			final List<String> syntaxesCopy = snapshotSyntaxes();
			final CreateFunction<T> createFnLocal = this.createFn;
			final ParsePredicateFunction predicateLocal = snapshotPredicate();
			final SyntaxNodeType<?>[] allowedParentsLocal = snapshotAllowedParents();
			return new EffectNodeType<>() {
				@Override
				public List<String> getSyntaxes() { return syntaxesCopy; }

				@Override
				public @NotNull T create(List<SyntaxNode> children, int matchedPattern) {
					if (createFnLocal == null) throw new IllegalStateException("Create function not set for effect node type");
					return createFnLocal.create(children, matchedPattern);
				}

				@Override
				public boolean canBeParsed(ParseContext context, int matchedPattern) {
					return predicateLocal == null || predicateLocal.canBeParsed(context, matchedPattern);
				}

				@Override
				public SyntaxNodeType<?>[] allowedParents() {
					return allowedParentsLocal == null ? new SyntaxNodeType<?>[0] : allowedParentsLocal;
				}
			};
		}
	}

	/** Concrete builder for StructureNodeType. */
	private static final class StructBuilder<T extends StructureNode> extends BaseBuilder<StructBuilder<T>> implements StructureBuilder<T> {
		private EntryStructureDefinition structureDef;
		private StructureCreateFunction<T> createFn;

		@Override
		public StructureBuilder<T> structure(@Nullable EntryStructureDefinition definition) {
			this.structureDef = definition;
			return this;
		}

		@Override
		public StructureBuilder<T> create(StructureCreateFunction<T> createFn) {
			this.createFn = createFn;
			return this;
		}

		@Override
		public StructureNodeType<T> build() {
			final List<String> syntaxesCopy = snapshotSyntaxes();
			final EntryStructureDefinition structureDefLocal = this.structureDef;
			final StructureCreateFunction<T> createFnLocal = this.createFn;
			final ParsePredicateFunction predicateLocal = snapshotPredicate();
			final SyntaxNodeType<?>[] allowedParentsLocal = snapshotAllowedParents();
			return new StructureNodeType<>() {
				@Override
				public List<String> getSyntaxes() { return syntaxesCopy; }

				@Override
				public @Nullable EntryStructureDefinition structure() { return structureDefLocal; }

				@Override
				protected @NotNull T create(@NotNull List<SyntaxNode> children, int matchedPattern, @Nullable Map<String, StructureEntryNode> entries) {
					if (createFnLocal == null) throw new IllegalStateException("Create function not set for structure node type");
					return createFnLocal.create(children, matchedPattern, entries);
				}

				@Override
				public boolean canBeParsed(ParseContext context, int matchedPattern) {
					return predicateLocal == null || predicateLocal.canBeParsed(context, matchedPattern);
				}

				@Override
				public SyntaxNodeType<?>[] allowedParents() {
					return allowedParentsLocal == null ? new SyntaxNodeType<?>[0] : allowedParentsLocal;
				}
			};
		}
	}
}
