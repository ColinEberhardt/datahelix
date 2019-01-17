package com.scottlogic.deg.generator;

import com.google.inject.Inject;
import com.scottlogic.deg.generator.constraints.Constraint;
import com.scottlogic.deg.generator.constraints.grammatical.AndConstraint;
import com.scottlogic.deg.generator.constraints.grammatical.ViolateConstraint;
import com.scottlogic.deg.generator.decisiontree.DecisionTreeCollection;
import com.scottlogic.deg.generator.decisiontree.DecisionTreeFactory;
import com.scottlogic.deg.generator.generation.DataGenerator;
import com.scottlogic.deg.generator.generation.GenerationConfig;
import com.scottlogic.deg.generator.outputs.GeneratedObject;
import com.scottlogic.deg.generator.outputs.TestCaseDataSet;
import com.scottlogic.deg.generator.outputs.TestCaseGenerationResult;
import com.scottlogic.deg.generator.outputs.targets.DirectoryOutputTarget;
import com.scottlogic.deg.generator.outputs.targets.FileOutputTarget;
import com.scottlogic.deg.generator.outputs.targets.OutputTarget;
import com.scottlogic.deg.generator.violations.ViolationFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class GenerationEngine {
    private final DecisionTreeFactory decisionTreeGenerator;
    private final ViolationFilter violationFilter;
    private final DataGenerator dataGenerator;
    private final OutputTarget outputter;

    @Inject
    protected GenerationEngine(
        OutputTarget outputter,
        DataGenerator dataGenerator,
        DecisionTreeFactory decisionTreeGenerator,
        ViolationFilter violationFilter) {
        this.outputter = outputter;
        this.dataGenerator = dataGenerator;
        this.decisionTreeGenerator = decisionTreeGenerator;
        this.violationFilter = violationFilter;
    }

    void generateDataSet(Profile profile, GenerationConfig config) throws IOException {
        final Stream<GeneratedObject> generatedDataItems = generate(profile, config);

        this.outputter.outputDataset(generatedDataItems, profile.fields);
    }

    public void generateTestCases(Profile profile, GenerationConfig config) throws IOException {
        final Stream<TestCaseDataSet> violatingCases = profile.rules
            .stream()
            .filter(this.violationFilter::canViolateRule)
            .map(rule -> getViolationForRuleTestCaseDataSet(profile, config, rule));

        final TestCaseGenerationResult generationResult = new TestCaseGenerationResult(
            profile,
            violatingCases.collect(Collectors.toList()));

        this.outputter.outputTestCases(generationResult);
    }

    private TestCaseDataSet getViolationForRuleTestCaseDataSet(Profile profile, GenerationConfig config, Rule rule) {
        Collection<Rule> violatedRule = profile.rules.stream()
            .map(r -> r == rule
                ? violateRule(rule)
                : r)
            .collect(Collectors.toList());

        Profile violatingProfile = new Profile(
            profile.fields,
            violatedRule,
            String.format("%s -- Violating: %s", profile.description, rule.rule.getDescription()));

        return new TestCaseDataSet(
            rule.rule,
            generate(
                violatingProfile,
                config));
    }

    private Stream<GeneratedObject> generate(Profile profile, GenerationConfig config) {
        final DecisionTreeCollection analysedProfile = this.decisionTreeGenerator.analyse(profile);

        return this.dataGenerator.generateData(
            profile,
            analysedProfile.getMergedTree(),
            config);
    }

    private Rule violateRule(Rule rule) {
        Set<Constraint> constraintsToViolate = rule.constraints
            .stream()
            .filter(this.violationFilter::canViolateConstraint)
            .collect(Collectors.toSet());

        Set<Constraint> constraintstoMaintain = rule.constraints
            .stream()
            .filter(c -> !this.violationFilter.canViolateConstraint(c))
            .collect(Collectors.toSet());

        if (constraintsToViolate.isEmpty()){
            return rule; //nothing can be violated
        }

        //This will in effect produce the following constraint: // VIOLATE(AND(X, Y, Z)) reduces to
        //   OR(
        //     AND(VIOLATE(X), Y, Z),
        //     AND(X, VIOLATE(Y), Z),
        //     AND(X, Y, VIOLATE(Z)))
        // See ProfileDecisionTreeFactory.convertConstraint(ViolateConstraint)
        ViolateConstraint violatedConstraints = new ViolateConstraint(constraintsToViolate.size() == 1
            ? constraintsToViolate.iterator().next()
            : new AndConstraint(constraintsToViolate));

        if (constraintstoMaintain.isEmpty()){
            return new Rule(rule.rule, Collections.singleton(violatedConstraints));
        }

        Constraint maintainedConstraints = constraintstoMaintain.size() == 1
            ? constraintstoMaintain.iterator().next()
            : new AndConstraint(constraintstoMaintain);

        AndConstraint unviolatedAndViolatedConstraints = new AndConstraint(
            Arrays.asList(
                maintainedConstraints,
                violatedConstraints));
        return new Rule(rule.rule, Collections.singleton(unviolatedAndViolatedConstraints));
    }
}
