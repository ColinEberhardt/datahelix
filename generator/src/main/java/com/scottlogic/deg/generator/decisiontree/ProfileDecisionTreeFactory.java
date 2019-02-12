package com.scottlogic.deg.generator.decisiontree;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.scottlogic.deg.generator.FlatMappingSpliterator;
import com.scottlogic.deg.generator.Profile;
import com.scottlogic.deg.generator.Rule;
import com.scottlogic.deg.generator.constraints.*;
import com.scottlogic.deg.generator.constraints.atomic.AtomicConstraint;
import com.scottlogic.deg.generator.constraints.atomic.IsOfTypeConstraint;
import com.scottlogic.deg.generator.constraints.atomic.ViolatedAtomicConstraint;
import com.scottlogic.deg.generator.constraints.grammatical.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ProfileDecisionTreeFactory implements DecisionTreeFactory {
    private final DecisionTreeSimplifier decisionTreeSimplifier = new DecisionTreeSimplifier();

    private final Boolean implyTypes;
    ImpliedTypeDictionary dictionary;

    @Inject
    public ProfileDecisionTreeFactory(@Named("implyTypes") Boolean implyTypes, ImpliedTypeDictionary dictionary){
        this.implyTypes = implyTypes;
        this.dictionary = dictionary;
    }

    public ProfileDecisionTreeFactory(){
        this.implyTypes = false;
        dictionary = new ImpliedTypeDictionary();
    }

    private static Collection<Constraint> wrapEach(
        Collection<Constraint> constraints,
        Function<Constraint, Constraint> wrapFunc) {

        return constraints.stream()
            .map(wrapFunc)
            .collect(Collectors.toList());
    }

    private static Collection<Constraint> negateEach(Collection<Constraint> constraints) {
        return wrapEach(constraints, Constraint::negate);
    }

    private static Collection<Constraint> violateEach(Collection<Constraint> constraints) {
        return wrapEach(constraints, ViolateConstraint::new);
    }

    private static Constraint reduceConditionalConstraint(ConditionalConstraint constraint) {
        Constraint ifConstraint = constraint.condition;
        Constraint thenConstraint = constraint.whenConditionIsTrue;
        Constraint elseConstraint = constraint.whenConditionIsFalse;

        return new OrConstraint(
            ifConstraint.and(thenConstraint),
            elseConstraint != null
                ? ifConstraint.negate().and(elseConstraint)
                : ifConstraint.negate());
    }

    private static Collection<ConstraintNode> asConstraintNodeList(Collection<AtomicConstraint> constraints) {

        return Collections.singleton(
            new TreeConstraintNode(
                constraints,
                Collections.emptyList(),
                constraints.stream().anyMatch(ac -> ac instanceof ViolatedAtomicConstraint)
                    ? Collections.singleton(NodeMarking.VIOLATED)
                    : Collections.emptySet()
                ));
    }

    private Collection<ConstraintNode> asConstraintNodeList(AtomicConstraint constraint) {

        if (implyTypes){
            Optional<IsOfTypeConstraint> impliedTypeConstraint = dictionary.getImpliedTypeConstraint(constraint);
            if (impliedTypeConstraint.isPresent()) {
                return asConstraintNodeList(Arrays.asList(constraint, impliedTypeConstraint.get()));
            }
        }

        return asConstraintNodeList(Arrays.asList(constraint));
    }

    private static Collection<ConstraintNode> asConstraintNodeList(DecisionNode decision) {
        return Collections.singleton(
            new TreeConstraintNode(
                Collections.emptyList(),
                Collections.singleton(decision)));
    }

    @Override
    public DecisionTreeCollection analyse(Profile profile) {
        return new DecisionTreeCollection(
            profile.fields,
            profile.rules.stream()
                .map(rule -> new DecisionTree(convertRule(rule), profile.fields, profile.description))
                .map(decisionTreeSimplifier::simplify)
                .collect(Collectors.toList()));
    }

    private ConstraintNode convertRule(Rule rule) {
        Iterator<ConstraintNode> rootConstraintNodeFragments = FlatMappingSpliterator.flatMap(rule.constraints.stream(),
            c -> convertConstraint(c).stream())
            .iterator();

        return ConstraintNode.merge(rootConstraintNodeFragments);
    }

    private Collection<ConstraintNode> convertConstraint(ViolateConstraint constraintToConvert){
        Constraint violatedConstraint = constraintToConvert.violatedConstraint;

        // VIOLATE(AND(X, Y, Z)) reduces to
        //   OR(
        //     AND(VIOLATE(X), Y, Z),
        //     AND(X, VIOLATE(Y), Z),
        //     AND(X, Y, VIOLATE(Z)))
        if (violatedConstraint instanceof AndConstraint) {
            Collection<Constraint> subConstraints = ((AndConstraint) violatedConstraint).subConstraints;

            Collection<Constraint> possibleFulfilments =
                subConstraints.stream()
                    // for each subconstraint X, make a copy of the original list but with X replaced by VIOLATE(X)
                    .map(constraintToViolate ->
                        subConstraints.stream()
                            .map(c -> c == constraintToViolate
                                ? new ViolateConstraint(c)
                                : c)
                            .collect(Collectors.toList()))
                    // make an AndConstraint out of each of the new lists
                    .map(AndConstraint::new)
                    .collect(Collectors.toList());

            return convertConstraint(new OrConstraint(possibleFulfilments));
        }
        // VIOLATE(OR(X, Y, Z)) reduces to AND(VIOLATE(X), VIOLATE(Y), VIOLATE(Z))
        else if (violatedConstraint instanceof OrConstraint) {
            Collection<Constraint> subConstraints = ((OrConstraint) violatedConstraint).subConstraints;

            return convertConstraint(
                new AndConstraint(violateEach(subConstraints)));
        }
        // VIOLATE(IF(X, then: Y)) reduces to AND(X, VIOLATE(Y))
        // VIOLATE(IF(X, then: Y, else: Z)) reduces to OR(AND(X, VIOLATE(Y)), AND(VIOLATE(X), VIOLATE(Z)))
        else if (violatedConstraint instanceof ConditionalConstraint) {
            ConditionalConstraint conditional = ((ConditionalConstraint) violatedConstraint);

            Constraint positiveViolation = new AndConstraint(
                conditional.condition,
                new ViolateConstraint(conditional.whenConditionIsTrue));

            Constraint negativeViolation = conditional.whenConditionIsFalse == null
                ? null
                : new AndConstraint(
                new ViolateConstraint(conditional.condition),
                new ViolateConstraint(conditional.whenConditionIsFalse));

            return convertConstraint(
                negativeViolation != null
                    ? positiveViolation.or(negativeViolation)
                    : positiveViolation
            );
        }

        // we've got an atomic constraint
        AtomicConstraint violatedAtomicConstraint = (AtomicConstraint) violatedConstraint;
        return convertConstraint(new ViolatedAtomicConstraint(violatedAtomicConstraint.negate()));
    }

    private Collection<ConstraintNode> convertConstraint(Constraint constraintToConvert) {
        if (constraintToConvert instanceof ViolateConstraint) {
            return convertConstraint((ViolateConstraint)constraintToConvert);
        }

        if (constraintToConvert instanceof NegatedGrammaticalConstraint) {
            Constraint negatedConstraint = ((NegatedGrammaticalConstraint) constraintToConvert).negatedConstraint;

            // ¬AND(X, Y, Z) reduces to OR(¬X, ¬Y, ¬Z)
            if (negatedConstraint instanceof AndConstraint) {
                Collection<Constraint> subConstraints = ((AndConstraint) negatedConstraint).subConstraints;

                return convertConstraint(
                    new OrConstraint(negateEach(subConstraints)));
            }
            // ¬OR(X, Y, Z) reduces to AND(¬X, ¬Y, ¬Z)
            else if (negatedConstraint instanceof OrConstraint) {
                Collection<Constraint> subConstraints = ((OrConstraint) negatedConstraint).subConstraints;

                return convertConstraint(
                    new AndConstraint(negateEach(subConstraints)));
            }
            // ¬IF(X, then: Y) reduces to AND(X, ¬Y)
            // ¬IF(X, then: Y, else: Z) reduces to OR(AND(X, ¬Y), AND(¬X, ¬Z))
            else if (negatedConstraint instanceof ConditionalConstraint) {
                ConditionalConstraint conditional = (ConditionalConstraint) negatedConstraint;

                Constraint positiveNegation = new AndConstraint(
                    conditional.condition,
                    conditional.whenConditionIsTrue.negate());

                Constraint negativeNegation = conditional.whenConditionIsFalse == null
                    ? null
                    : new AndConstraint(
                    conditional.condition.negate(),
                    conditional.whenConditionIsFalse.negate());

                return convertConstraint(
                    negativeNegation != null
                        ? positiveNegation.or(negativeNegation)
                        : positiveNegation
                );
            }
            // if we got this far, it must be an atomic constraint
            else {
                AtomicConstraint atomicConstraint = (AtomicConstraint) constraintToConvert;
                return asConstraintNodeList(atomicConstraint);
            }
        }
        // AND(X, Y, Z) becomes a flattened list of constraint nodes
        else if (constraintToConvert instanceof AndConstraint) {
            Collection<Constraint> subConstraints = ((AndConstraint) constraintToConvert).subConstraints;

            return FlatMappingSpliterator.flatMap(subConstraints.stream(),
                c -> convertConstraint(c).stream())
                .collect(Collectors.toList());
        }
        // OR(X, Y, Z) becomes a decision node
        else if (constraintToConvert instanceof OrConstraint) {
            Collection<Constraint> subConstraints = ((OrConstraint) constraintToConvert).subConstraints;

            DecisionNode decisionPoint = new TreeDecisionNode(
                subConstraints.stream()
                    .map(c -> ConstraintNode.merge(convertConstraint(c).stream().iterator()))
                    .collect(Collectors.toList()));

            return asConstraintNodeList(decisionPoint);
        } else if (constraintToConvert instanceof ConditionalConstraint) {
            return convertConstraint(reduceConditionalConstraint((ConditionalConstraint) constraintToConvert));
        } else {
            AtomicConstraint atomicConstraint = (AtomicConstraint) constraintToConvert;
            return asConstraintNodeList(atomicConstraint);
        }
    }
}

