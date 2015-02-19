package ch.unibe.scg.nullfinder.classification;

import ch.unibe.scg.nullfinder.NullCheck;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;

public class MethodCallNullCheckClassification extends
		AbstractNullCheckClassification {

	public MethodCallNullCheckClassification(NullCheck check) {
		super(check);
	}

	@Override
	public boolean accepts(NullCheck check) {
		BinaryExpr expression = (BinaryExpr) check.getNode().getParentNode();
		return expression.getLeft() instanceof MethodCallExpr
				|| expression.getRight() instanceof MethodCallExpr;
	}

}
