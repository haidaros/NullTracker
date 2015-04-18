package ch.unibe.scg.nullfinder.feature.extractor.level1;

import java.util.Collections;
import java.util.List;

import ch.unibe.scg.nullfinder.feature.extractor.AbstractVariableComparandDependentExtractor;
import ch.unibe.scg.nullfinder.jpa.entity.Feature;
import ch.unibe.scg.nullfinder.jpa.entity.Node;
import ch.unibe.scg.nullfinder.jpa.entity.NullCheck;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

public class MemberVariableComparandExtractor extends
		AbstractVariableComparandDependentExtractor {

	public MemberVariableComparandExtractor() {
		super(1);
	}

	@Override
	protected List<Feature> safeExtract(NullCheck nullCheck,
			List<Feature> features) {
		// TODO there is some dirty stuff going on here...
		Feature variableFeature = this.extractVariableFeature(nullCheck,
				features);
		Node variableNode = this.extractVariableNode(nullCheck, features);
		com.github.javaparser.ast.Node current = nullCheck.getNode()
				.getJavaParserNode().getParentNode();
		while (current != null) {
			if (current instanceof ClassOrInterfaceDeclaration) {
				ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) current;
				try {
					VariableDeclarator variableDeclarator = this
							.findDeclaration(clazz.getMembers(), variableNode);
					return this
							.getFeatures(this
									.getFeatureBuilder(
											nullCheck,
											ClassOrInterfaceDeclaration.class
													.getName())
									.addNodeReason(variableDeclarator)
									.addFeatureReason(variableFeature)
									.getEntity());
				} catch (DeclarationNotFoundException exception) {
					// noop
				}
			} else if (current instanceof ObjectCreationExpr) {
				ObjectCreationExpr objectCreation = (ObjectCreationExpr) current;
				if (objectCreation.getAnonymousClassBody() != null) {
					try {
						VariableDeclarator variableDeclarator = this
								.findDeclaration(
										objectCreation.getAnonymousClassBody(),
										variableNode);
						return this.getFeatures(this
								.getFeatureBuilder(nullCheck,
										ObjectCreationExpr.class.getName())
								.addNodeReason(variableDeclarator)
								.addFeatureReason(variableFeature).getEntity());
					} catch (DeclarationNotFoundException exception) {
						// noop
					}
				}
			}
			current = current.getParentNode();
		}
		return Collections.emptyList();
	}

	protected VariableDeclarator findDeclaration(List<BodyDeclaration> bodies,
			Node variableExtractorNode) throws DeclarationNotFoundException {
		for (BodyDeclaration body : bodies) {
			if (body instanceof FieldDeclaration) {
				FieldDeclaration field = (FieldDeclaration) body;
				for (VariableDeclarator variable : field.getVariables()) {
					if (this.getVariableName(variableExtractorNode).equals(
							variable.getId().getName())) {
						return variable;
					}
				}
			}
		}
		throw new DeclarationNotFoundException(variableExtractorNode);
	}

}