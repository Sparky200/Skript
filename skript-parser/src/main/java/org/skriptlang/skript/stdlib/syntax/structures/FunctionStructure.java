package org.skriptlang.skript.stdlib.syntax.structures;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.api.entries.StructureEntryNode;
import org.skriptlang.skript.api.nodes.*;
import org.skriptlang.skript.api.runtime.ExecuteContext;
import org.skriptlang.skript.api.util.ExecuteResult;
import org.skriptlang.skript.api.util.Priority;

import java.util.List;
import java.util.Map;

public class FunctionStructure implements StructureNode {

	public static final StructureNodeType<FunctionStructure> TYPE = new StructureNodeType<>() {
		@Override
		public List<String> getSyntaxes() {
			return List.of("[local] function <token::identifier>\\([{<token::identifier>: <token::identifier>,}]\\) [returns <token::identifier>] : <section>");
		}

		@Override
		public @NotNull FunctionStructure create(List<SyntaxNode> children, int matchedPattern, @Nullable Map<String, StructureEntryNode> entries) {
			List<SyntaxNode> tokenNodesInParams = children.subList(1, children.size() - 2);
			// TODO: params

			return new FunctionStructure(
				((TokenNode) children.getFirst()).tokenContents(),
				(SectionNode) children.getLast(),
				((TokenNode) children.get(children.size() - 2)).tokenContents()
			);
		}
	};

	private final String name;
	private final SectionNode body;
	private final String returnTypeName;
	// TODO: params

	public FunctionStructure(String name, SectionNode body, String returnTypeName) {
		this.name = name;
		this.body = body;
		this.returnTypeName = returnTypeName;
	}

	@Override
	public Priority priority() {
		return StructureNode.FUNCTION;
	}

	@Override
	public @NotNull ExecuteResult load(@NotNull ExecuteContext context) {
		ExecuteContext functionBaseContext = context.fork();
		context.setFunction(name, () -> {
			ExecuteContext functionContext = functionBaseContext.fork();
			return execute(functionContext);
		});
		return ExecuteResult.success();
	}

	private @NotNull ExecuteResult execute(@NotNull ExecuteContext context) {
		ExecuteContext functionContext = context.fork();
		for (StatementNode node : body.children()) {

			if (node instanceof EffectNode effect) {
				ExecuteResult result = effect.execute(functionContext);
				if (result != ExecuteResult.SUCCESS) {
					return result;
				}
			}
		}
		return ExecuteResult.success();
	}
}
