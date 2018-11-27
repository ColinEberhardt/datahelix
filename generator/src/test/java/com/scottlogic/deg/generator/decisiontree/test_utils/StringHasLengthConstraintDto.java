package com.scottlogic.deg.generator.decisiontree.test_utils;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.constraints.IConstraint;
import com.scottlogic.deg.generator.constraints.StringHasLengthConstraint;

public class StringHasLengthConstraintDto implements ConstraintDto {
    public FieldDto field;
    public Number referenceValue;

    @Override
    public IConstraint map() {
        return new StringHasLengthConstraint(new Field(field.name), referenceValue);
    }
}
