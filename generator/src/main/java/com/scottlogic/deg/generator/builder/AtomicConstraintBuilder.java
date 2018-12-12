package com.scottlogic.deg.generator.builder;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.constraints.atomic.IsEqualToConstantConstraint;
import com.scottlogic.deg.generator.constraints.atomic.IsInSetConstraint;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AtomicConstraintBuilder {
    private ConstraintNodeBuilder constraintNodeBuilder;
    private Field field;

    protected AtomicConstraintBuilder(ConstraintNodeBuilder constraintNodeBuilder, Field field){
        this.constraintNodeBuilder = constraintNodeBuilder;
        this.field = field;
    }

    public ConstraintNodeBuilder isEqualTo(Object requiredValue){
        IsEqualToConstantConstraint constraint = new IsEqualToConstantConstraint(field, requiredValue);
        constraintNodeBuilder.constraints.add(constraint);
        return constraintNodeBuilder;
    }

    public ConstraintNodeBuilder isInSet(Object... legalValues){
        Set values = new HashSet();
        Collections.addAll(values, legalValues);
        IsInSetConstraint isInSetConstraint = new IsInSetConstraint(field, values);
        constraintNodeBuilder.constraints.add(isInSetConstraint);
        return constraintNodeBuilder;
    }
    ///TODO others
}
