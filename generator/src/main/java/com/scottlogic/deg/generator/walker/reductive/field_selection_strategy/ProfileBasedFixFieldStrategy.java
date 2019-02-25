package com.scottlogic.deg.generator.walker.reductive.field_selection_strategy;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.Profile;
import com.scottlogic.deg.generator.decisiontree.DecisionTree;
import com.scottlogic.deg.generator.decisiontree.reductive.ReductiveConstraintNode;
import com.scottlogic.deg.generator.walker.reductive.ReductiveState;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ProfileBasedFixFieldStrategy implements FixFieldStrategy {

    protected final DecisionTree tree;
    private List<Field> fieldsInFixingOrder;

    ProfileBasedFixFieldStrategy(DecisionTree tree) {
        this.tree = tree;
    }

    @Override
    public Field getNextFieldToFix(ReductiveState reductiveState, ReductiveConstraintNode rootNode) {
        return getFieldFixingPriorityList().stream()
            .filter(field -> !reductiveState.isFieldFixed(field) && reductiveState.getFields().stream().anyMatch(pf -> pf.equals(field)))
            .findFirst()
            .orElse(null);
    }

    List<Field> getFieldFixingPriorityList() {
        if (fieldsInFixingOrder == null) {
            fieldsInFixingOrder = Collections.unmodifiableList(tree.fields.stream()
                .sorted(getFieldOrderingStrategy())
                .collect(Collectors.toList()));
        }
        return fieldsInFixingOrder;
    }

    abstract Comparator<Field> getFieldOrderingStrategy();
}
