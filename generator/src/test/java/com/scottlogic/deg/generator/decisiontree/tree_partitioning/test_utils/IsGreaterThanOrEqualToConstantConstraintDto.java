package com.scottlogic.deg.generator.decisiontree.tree_partitioning.test_utils;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.constraints.IConstraint;
import com.scottlogic.deg.generator.constraints.IsGreaterThanOrEqualToConstantConstraint;

public class IsGreaterThanOrEqualToConstantConstraintDto implements ConstraintDto {
    public FieldDto field;
    public Number referenceValue;

    @Override
    public IConstraint map() {
        return new IsGreaterThanOrEqualToConstantConstraint(new Field(field.name), referenceValue);
    }
}
