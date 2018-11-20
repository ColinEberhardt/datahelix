package com.scottlogic.deg.generator.decisiontree.tree_partitioning.test_utils;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.constraints.IConstraint;
import com.scottlogic.deg.generator.constraints.IsStringLongerThanConstraint;

public class IsStringLongerThanConstraintDto implements ConstraintDto {
    public FieldDto field;
    public int referenceValue;

    @Override
    public IConstraint map() {
        return new IsStringLongerThanConstraint(new Field(field.name), referenceValue);
    }
}
