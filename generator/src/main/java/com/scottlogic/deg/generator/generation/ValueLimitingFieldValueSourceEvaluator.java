package com.scottlogic.deg.generator.generation;

import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.fieldspecs.FieldSpecSource;
import com.scottlogic.deg.generator.generation.field_value_sources.CannedValuesFieldValueSource;
import com.scottlogic.deg.generator.generation.field_value_sources.FieldValueSource;
import com.scottlogic.deg.generator.restrictions.DataTypeRestrictions;

import java.util.Collections;
import java.util.List;

public class ValueLimitingFieldValueSourceEvaluator extends StandardFieldValueSourceEvaluator {
    private final Object valueForNonNullUnconstrainedField;

    public ValueLimitingFieldValueSourceEvaluator() {
        this.valueForNonNullUnconstrainedField = "";
    }

    @Override
    public List<FieldValueSource> getFieldValueSources(FieldSpec fieldSpec) {
        boolean fieldIsUnconstrained = fieldSpec.equals(FieldSpec.Empty);

        if (fieldIsUnconstrained){
            return Collections.singletonList(StandardFieldValueSourceEvaluator.nullOnlySource);
        }

        if (super.mustBeNull(fieldSpec)){
            return Collections.singletonList(StandardFieldValueSourceEvaluator.nullOnlySource);
        }

        boolean fieldIsUnconstraintedExceptForNull = treatAnyTypeAsUntyped(fieldSpecWithoutNullRestrictions(fieldSpec)).equals(FieldSpec.Empty);
        //does the field have any constraints except for its 'nullability'
        //the fieldSpec must define the field as being requiring a value, as otherwise it would have expressed MUST_BE_NULL and would have exited already (condition above)
        //fieldIsUnconstraintedExceptForNull = true == field can emit all possible values except for <missing/null>

        if (fieldIsUnconstraintedExceptForNull){
            if (fieldSpec.getMustContainRestriction() != null){
                return Collections.singletonList(
                    new ValueLimitingFieldValueSource(super.getMustContainRestrictionSources(fieldSpec, true), 1));
            }

            //one single value of a any type, picking a string for simplicity
            return Collections.singletonList(
                new CannedValuesFieldValueSource(
                    Collections.singletonList(valueForNonNullUnconstrainedField)));
        }

        return super.getFieldValueSources(fieldSpec);
    }

    private FieldSpec treatAnyTypeAsUntyped(FieldSpec fieldSpec) {
        if (fieldSpec.getTypeRestrictions() == DataTypeRestrictions.ALL_TYPES_PERMITTED){
            return fieldSpec.withTypeRestrictions(null, FieldSpecSource.Empty);
        }

        return fieldSpec;
    }

    private FieldSpec fieldSpecWithoutNullRestrictions(FieldSpec fieldSpec) {
        if (fieldSpec.getNullRestrictions() == null){
            return fieldSpec;
        }

        return fieldSpec
            .withNullRestrictions(null, FieldSpecSource.Empty)
            .withMustContainRestriction(null);
    }
}
