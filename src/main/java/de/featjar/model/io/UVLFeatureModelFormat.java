/* -----------------------------------------------------------------------------
 * uvl - Universal Variability Language
 * Copyright (C) 2022 Elias Kuiter
 * 
 * This file is part of uvl.
 * 
 * uvl is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 * 
 * uvl is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with uvl. If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/FeatJAR/uvl> for further information.
 * -----------------------------------------------------------------------------
 */
package de.featjar.model.io;

import de.featjar.util.io.format.Format;
import de.featjar.util.io.format.ParseException;
import de.featjar.util.io.format.ParseProblem;
import de.neominik.uvl.UVLParser;
import de.neominik.uvl.ast.ParseError;
import de.neominik.uvl.ast.UVLModel;
import de.featjar.model.FeatureModel;
import de.featjar.model.util.Identifier;
import de.featjar.util.data.Problem;
import de.featjar.util.data.Result;
import de.featjar.util.io.InputMapper;
import de.featjar.util.io.format.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Parses and writes feature models from and to UVL files.
 *
 * @author Dominik Engelhardt
 * @author Elias Kuiter
 */
public class UVLFeatureModelFormat implements Format<FeatureModel> {
	protected FeatureModel featureModel;
	protected List<Problem> parseProblems = new ArrayList<>();

	@Override
	public UVLFeatureModelFormat getInstance() {
		return new UVLFeatureModelFormat();
	}

	@Override
	public String getFileExtension() {
		return "uvl";
	}

	@Override
	public String getName() {
		return "UVL";
	}

	@Override
	public boolean supportsParse() {
		return true;
	}

	@Override
	public boolean supportsSerialize() {
		return false;
	}

	@Override
	public Result<FeatureModel> parse(InputMapper inputMapper, Supplier<FeatureModel> supplier) {
		featureModel = supplier.get();
		return parse(inputMapper);
	}

	@Override
	public Result<FeatureModel> parse(InputMapper inputMapper) {
		if (featureModel != null)
			featureModel = new FeatureModel(Identifier.newCounter());
		parseProblems.clear();
		try {
			final Object result = UVLParser.parse(inputMapper.get().readText().orElseThrow()); // todo
			if (result instanceof UVLModel) {
				constructFeatureModel((UVLModel) result);
			} else if (result instanceof ParseError) {
				parseProblems.add(new ParseProblem(result.toString(), ((ParseError) result).getLine(),
					Problem.Severity.ERROR));
			}
			return Result.of(featureModel, parseProblems);
		} catch (final ParseException e) {
			return Result.empty(new ParseProblem(e.getMessage(), e.getLineNumber(), Problem.Severity.ERROR));
		} catch (final Exception e) {
			return Result.empty(new Problem(e));
		}
	}

	private void constructFeatureModel(UVLModel uvlModel) throws ParseException {
//		Arrays.stream(uvlModel.getImports()).forEach(i -> parseImport(fm, i));
//		IFeature root;
//		if (uvlModel.getRootFeatures().length == 1) {
//			final Feature f = uvlModel.getRootFeatures()[0];
//			root = parseFeature(fm, null, f, uvlModel);
//		} else {
//			String rootName = MULTI_ROOT_PREFIX + 0;
//			for (int i = 1; uvlModel.getAllFeatures().keySet().contains(rootName); i++) {
//				rootName = MULTI_ROOT_PREFIX + i;
//			}
//			root = factory.createFeature(fm, rootName);
//			root.getStructure().setAbstract(true);
//			root.getProperty().setImplicit(true);
//			fm.addFeature(root);
//			Arrays.stream(uvlModel.getRootFeatures()).forEachOrdered(f -> parseFeature(fm, root, f, uvlModel));
//			root.getStructure().getChildren().forEach(fs -> fs.setMandatory(true));
//		}
//		fm.getStructure().setRoot(root.getStructure());
//		final List<Object> ownConstraints = Arrays.asList(uvlModel.getOwnConstraints());
//		Arrays.stream(uvlModel.getConstraints()).filter(c -> !ownConstraints.contains(c)).forEach(c -> parseConstraint(fm, c));
//		ownConstraints.forEach(c -> parseOwnConstraint(fm, c));
//		fm.addAttribute(NS_ATTRIBUTE_FEATURE, NS_ATTRIBUTE_NAME, uvlModel.getNamespace());
	}

//	private static final String NS_ATTRIBUTE_NAME = "namespace";
//	private static final String NS_ATTRIBUTE_FEATURE = "_synthetic_ns_feature";
//
//	protected static final String EXTENDED_ATTRIBUTE_NAME = "extended__";
//	private static final String MULTI_ROOT_PREFIX = "Abstract_";
//
//	private UVLModel rootModel;
//	protected ProblemList pl;
//	private IFeatureModel fm;
//	protected MultiFeatureModelFactory factory;
//
//	@Override
//	public ProblemList read(IFeatureModel fm, CharSequence source) {
//		if (fm.getSourceFile() != null) {
//			return read(fm, source, fm.getSourceFile().toAbsolutePath());
//		}
//		System.err.println("No path set for model. Can't load imported models.");
//		return read(fm, source, new File("./.").toPath());
//	}
//
//
//
//	private IFeature parseFeature(MultiFeatureModel fm, IFeature root, Feature f, UVLModel submodel) {
//		final Feature resolved = UVLParser.resolve(f, rootModel);
//
//		boolean duplicateFeature = false;
//		// Add error in case of a duplicate feature name
//		if (fm.getFeatures().stream().anyMatch(feature -> feature.getName().equals(resolved.getName()))) {
//			pl.add(new Problem("Duplicate feature name " + resolved.getName(), 0, Severity.ERROR));
//			duplicateFeature = true;
//		}
//
//		// Validate imported feature
//		if ((root == null ? -1 : root.getName().lastIndexOf('.')) < resolved.getName().lastIndexOf('.')) {
//			// Update current submodel or add an error if the feature does not exist
//			boolean invalid = true;
//			Optional<UVLModel> sub;
//			// Find submodel declaring the current feature, iterate in case a submodel has an imported root feature
//			while ((sub = Arrays.stream(submodel.getSubmodels())
//					.filter(m -> Arrays.stream(m.getRootFeatures()).map(Feature::getName).anyMatch(resolved.getName()::equals)).findFirst()).isPresent()) {
//				submodel = sub.get();
//				invalid = false;
//			}
//			if (invalid) {
//				pl.add(new Problem("Feature " + resolved.getName() + " does not exist", 0, Severity.ERROR));
//			}
//
//			// Check for invalid attributes and child groups
//			if (!f.getAttributes().isEmpty()) {
//				pl.add(new Problem("Invalid attribute of imported feature " + f.getName(), 0, Severity.ERROR));
//			}
//			if (f.getGroups().length != 0) {
//				pl.add(new Problem("Invalid group of imported feature " + f.getName(), 0, Severity.ERROR));
//			}
//		}
//		final UVLModel finalSubmodel = submodel;
//
//		final MultiFeature feature = factory.createFeature(fm, resolved.getName());
//
//		if (resolved.getName().contains(".")) {
//			feature.setType(MultiFeature.TYPE_INTERFACE);
//		}
//		fm.addFeature(feature);
//		if (root != null) {
//			root.getStructure().addChild(feature.getStructure());
//		}
//		feature.getStructure().setAbstract(isAbstract(resolved));
//
//		if (!duplicateFeature) { // Don't process groups for duplicate feature names, as this can cause infinite recursion
//			Arrays.stream(resolved.getGroups()).forEach(g -> parseGroup(fm, feature, g, finalSubmodel));
//		}
//		parseAttributes(fm, feature, resolved);
//
//		return feature;
//	}
//
//	private void parseGroup(MultiFeatureModel fm, IFeature root, Group g, UVLModel submodel) {
//		if ("cardinality".equals(g.getType())) {
//			if ((g.getLower() == 1) && (g.getUpper() == -1)) {
//				g.setType("or");
//			} else if ((g.getLower() == 1) && (g.getUpper() == 1)) {
//				g.setType("alternative");
//			} else if ((g.getLower() == 0) && (g.getUpper() == -1)) {
//				g.setType("optional");
//			} else if ((g.getLower() == g.getUpper()) && (g.getUpper() == g.getChildren().length)) {
//				g.setType("mandatory");
//			} else {
//				g.setType("optional");
//				pl.add(new Problem(
//						String.format("Failed to convert cardinality [%d..%d] to known group type at feature %s.", g.getLower(), g.getUpper(), root.getName()),
//						0, Severity.WARNING));
//			}
//		}
//		final List<IFeature> children = Stream.of(g.getChildren()).map(f -> parseFeature(fm, root, (Feature) f, submodel)).collect(Collectors.toList());
//		switch (g.getType()) {
//			case "or":
//				root.getStructure().setOr();
//				break;
//			case "alternative":
//				root.getStructure().setAlternative();
//				break;
//			case "optional":
//				break;
//			case "mandatory":
//				children.forEach(f -> f.getStructure().setMandatory(true));
//				break;
//		}
//	}
//
//	private boolean isAbstract(Feature f) {
//		return Objects.equals(true, f.getAttributes().get("abstract"));
//	}
//
//	private void parseAttributes(MultiFeatureModel fm, MultiFeature feature, Feature f) {
//		UVLParser.getAttributes(f).entrySet().stream().forEachOrdered(e -> parseAttribute(fm, feature, e.getKey(), e.getValue()));
//	}
//
//	/**
//	 * This method parses an attribute that is contained in UVL under a feature to an attribute/constraint in the feature model.
//	 *
//	 * @param fm the featuremodel that is parsed from UVL
//	 * @param feature the feature that contains the attribute that is parsed
//	 * @param attributeKey the name of the attribute that is parsed
//	 * @param attributeValue the value of the attribute that is parsed
//	 */
//	protected void parseAttribute(MultiFeatureModel fm, MultiFeature feature, String attributeKey, Object attributeValue) {
//		if (attributeKey.equals("constraint") || attributeKey.equals("constraints")) {
//			if (attributeValue instanceof List<?>) {
//				((List<?>) attributeValue).forEach(v -> parseConstraint(fm, v));
//			} else {
//				parseConstraint(fm, attributeValue);
//			}
//		}
//	}
//
//	private void parseConstraint(MultiFeatureModel fm, Object c) {
//		parseConstraint(fm, c, false);
//	}
//
//	private void parseOwnConstraint(MultiFeatureModel fm, Object c) {
//		parseConstraint(fm, c, true);
//	}
//
//	private void parseConstraint(MultiFeatureModel fm, Object c, boolean own) {
//		try {
//			final Node constraint = parseConstraint(c);
//			if (constraint != null) {
//				final MultiConstraint newConstraint = factory.createConstraint(fm, constraint);
//				if (own) {
//					fm.addOwnConstraint(newConstraint);
//				} else {
//					newConstraint.setType(MultiFeature.TYPE_INTERFACE);
//					fm.addConstraint(newConstraint);
//				}
//			}
//		} catch (final RuntimeException e) {
//			// Contained invalid reference. Already added to problem list
//		}
//	}
//
//	private Node parseConstraint(Object c) {
//		if (c instanceof String) {
//			final String name = (String) c;
//			checkReferenceValid(name);
//			return new Literal((String) c);
//		} else if (c instanceof Not) {
//			return new org.prop4j.Not(parseConstraint(((Not) c).getChild()));
//		} else if (c instanceof And) {
//			return new org.prop4j.And(parseConstraint(((And) c).getLeft()), parseConstraint(((And) c).getRight()));
//		} else if (c instanceof Or) {
//			return new org.prop4j.Or(parseConstraint(((Or) c).getLeft()), parseConstraint(((Or) c).getRight()));
//		} else if (c instanceof Impl) {
//			return new Implies(parseConstraint(((Impl) c).getLeft()), parseConstraint(((Impl) c).getRight()));
//		} else if (c instanceof Equiv) {
//			return new Equals(parseConstraint(((Equiv) c).getLeft()), parseConstraint(((Equiv) c).getRight()));
//		}
//		return null;
//	}
//
//	private void checkReferenceValid(String name) {
//		final IFeature f = fm.getFeature(name);
//		if ((f == null) || f.getProperty().isImplicit()) {
//			pl.add(new Problem("Invalid reference: Feature " + name + " doesn't exist", 0, Severity.ERROR));
//			throw new RuntimeException("Invalid reference");
//		}
//	}
//
//	private void parseImport(MultiFeatureModel fm, Import i) {
//		fm.addInstance(i.getNamespace(), i.getAlias());
//	}
//
//	/**
//	 * @param error a {@link ParseError}
//	 * @return a {@link Problem}
//	 */
//	private Problem toProblem(ParseError error) {
//		return new Problem(error.toString(), error.getLine(), Severity.ERROR);
//	}
//
//	@Override
//	public String write(IFeatureModel fm) {
//		return deconstructFeatureModel(fm).toString();
//	}
//
//	private UVLModel deconstructFeatureModel(IFeatureModel fm) {
//		final UVLModel model = new UVLModel();
//		String namespace = fm.getStructure().getRoot().getFeature().getName();
//		List<IConstraint> constraints = fm.getConstraints();
//		if (fm instanceof MultiFeatureModel) {
//			final MultiFeatureModel mfm = (MultiFeatureModel) fm;
//			final FeatureAttribute<String> nsAttribute = mfm.getStringAttributes().getAttribute(NS_ATTRIBUTE_FEATURE, NS_ATTRIBUTE_NAME);
//			if (nsAttribute != null) {
//				namespace = nsAttribute.getValue();
//			}
//			model.setImports(mfm.getExternalModels().values().stream().map(um -> new Import(um.getModelName(), um.getVarName())).toArray(Import[]::new));
//			if (mfm.isMultiProductLineModel()) {
//				constraints = mfm.getOwnConstraints();
//			}
//		} else {
//			model.setImports(new Import[0]);
//		}
//		model.setNamespace(namespace);
//		final IFeatureStructure root = fm.getStructure().getRoot();
//		if (root.getFeature().getProperty().isImplicit() && root.isAnd() && root.hasChildren()
//				&& root.getChildren().stream().allMatch(IFeatureStructure::isMandatory) && root.getRelevantConstraints().isEmpty()) {
//			// Remove implicit root feature, use children as root features
//			model.setRootFeatures(root.getChildren().stream().map(child -> printFeature(child.getFeature())).toArray(Feature[]::new));
//		} else {
//			// Use single root feature
//			model.setRootFeatures(new Feature[] { printFeature(root.getFeature()) });
//		}
//		model.setConstraints(constraints.stream().map(this::printConstraint).toArray());
//		return model;
//	}
//
//	private Feature printFeature(IFeature feature) {
//		final Feature f = new Feature();
//		f.setName(feature.getName());
//		if (!f.getName().contains(".")) { // exclude references
//			f.setAttributes(printAttributes(feature));
//			f.setGroups(printGroups(feature));
//		}
//		return f;
//	}
//
//	/**
//	 * This method writes all attributes of the specified feature to a map that can be converted to UVL.
//	 *
//	 * @param feature the feature whose attributes are written
//	 * @return a map containing the attributes
//	 */
//	protected Map<String, Object> printAttributes(IFeature feature) {
//		final Map<String, Object> attributes = new TreeMap<>();
//		if (feature.getStructure().isAbstract()) {
//			attributes.put("abstract", true);
//		}
//		return attributes;
//	}
//
//	private Group constructGroup(IFeatureStructure fs, String type, Predicate<IFeatureStructure> pred) {
//		return new Group(type, 0, 0, fs.getChildren().stream().filter(pred).map(f -> printFeature(f.getFeature())).toArray(Feature[]::new));
//	}
//
//	private Group[] printGroups(IFeature feature) {
//		final IFeatureStructure fs = feature.getStructure();
//		if (!fs.hasChildren()) {
//			return new Group[] {};
//		}
//		if (fs.isAnd()) {
//			final List<Group> groups = new LinkedList<Group>();
//			for (int i = 0; i < fs.getChildrenCount(); i++) {
//				final Group group = getGroup(fs.getChildren().get(i), i);
//				groups.add(group);
//				i = (i + group.getChildren().length) - 1;
//			}
//			final Group[] groupArray = new Group[groups.size()];
//			for (int i = 0; i < groups.size(); i++) {
//				groupArray[i] = groups.get(i);
//			}
//			return groupArray;
//		} else if (fs.isOr()) {
//			return new Group[] { constructGroup(fs, "or", x -> true) };
//		} else if (fs.isAlternative()) {
//			return new Group[] { constructGroup(fs, "alternative", x -> true) };
//		}
//		return new Group[] {};
//	}
//
//	/**
//	 * a method to create a group for uvl starting with a feature that is either mandatory or optional and adding all features with the same property until a
//	 * feature with a different property comes in order
//	 *
//	 * @param feat the first feature of a new group
//	 * @param pos the position of the feature in the list of children of the parent feature
//	 * @return the new group with the given feature as start feature
//	 */
//	private Group getGroup(IFeatureStructure feat, int pos) {
//		final List<IFeatureStructure> featuresInGroup = new LinkedList<IFeatureStructure>();
//		featuresInGroup.add(feat);
//		for (int i = pos + 1; i < feat.getParent().getChildrenCount(); i++) {
//			if (feat.getParent().getChildren().get(i).isMandatory() == feat.isMandatory()) {
//				featuresInGroup.add(feat.getParent().getChildren().get(i));
//			} else {
//				break;
//			}
//		}
//		if (feat.isMandatory()) {
//			return constructGroup(feat.getParent(), "mandatory", c -> featuresInGroup.contains(c));
//		} else {
//			return constructGroup(feat.getParent(), "optional", c -> featuresInGroup.contains(c));
//		}
//	}
//
//	private Object printConstraint(IConstraint constraint) {
//		return printConstraint(constraint.getNode());
//	}
//
//	private Object printConstraint(Node n) {
//		if (n instanceof Literal) {
//			return ((Literal) n).var;
//		} else if (n instanceof org.prop4j.Not) {
//			return new Not(printConstraint(n.getChildren()[0]));
//		} else if (n instanceof org.prop4j.And) {
//			return printMultiArity(And::new, n.getChildren());
//		} else if (n instanceof org.prop4j.Or) {
//			return printMultiArity(Or::new, n.getChildren());
//		} else if (n instanceof Implies) {
//			return new Impl(printConstraint(n.getChildren()[0]), printConstraint(n.getChildren()[1]));
//		} else if (n instanceof Equals) {
//			return new Equiv(printConstraint(n.getChildren()[0]), printConstraint(n.getChildren()[1]));
//		}
//		return null;
//	}
//
//	private Object printMultiArity(BiFunction<Object, Object, Object> constructor, Node[] args) {
//		switch (args.length) {
//			case 0:
//				return null;
//			case 1:
//				return printConstraint(args[0]);
//			case 2:
//				return constructor.apply(printConstraint(args[0]), printConstraint(args[1]));
//			default:
//				return constructor.apply(printConstraint(args[0]), printMultiArity(constructor, Arrays.copyOfRange(args, 1, args.length)));
//		}
//	}
//
//	@Override
//	public boolean supportsContent(CharSequence content) {
//		return !content.toString().contains(EXTENDED_ATTRIBUTE_NAME);
//	}
//
//	@Override
//	public boolean supportsContent(LazyReader reader) {
//		return supportsContent((CharSequence) reader);
//	}
//
//	@Override
//	public boolean isValidFeatureName(String featureName) {
//		return featureName.matches("[^\\\"\\.\\n\\r]*");
//	}
//
//	@Override
//	public String getErrorMessage() {
//		return "The characters  \" and . are not allowed and the feature name has to be non-empty.";
//	}
}