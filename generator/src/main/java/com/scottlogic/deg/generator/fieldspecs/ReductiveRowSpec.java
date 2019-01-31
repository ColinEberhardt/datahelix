package com.scottlogic.deg.generator.fieldspecs;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.ProfileFields;

import java.util.Map;

public class ReductiveRowSpec extends RowSpec {
    public final Field lastFixedField;

    public ReductiveRowSpec(ProfileFields fields, Map<Field, FieldSpec> fieldToFieldSpec, Field lastFixedField) {
        super(fields, fieldToFieldSpec);
        this.lastFixedField = lastFixedField;
    }
}
