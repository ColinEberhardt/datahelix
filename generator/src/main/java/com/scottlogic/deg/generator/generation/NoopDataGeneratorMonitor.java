package com.scottlogic.deg.generator.generation;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.outputs.GeneratedObject;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.fieldspecs.RowSpec;
import com.scottlogic.deg.generator.walker.reductive.ReductiveState;
import com.scottlogic.deg.generator.walker.reductive.FixedField;

import java.util.Map;

public class NoopDataGeneratorMonitor implements ReductiveDataGeneratorMonitor {

    @Override
    public void generationStarting(GenerationConfig generationConfig) { }

    @Override
    public void rowEmitted(GeneratedObject row) { }

    @Override
    public void rowSpecEmitted(FixedField lastFixedField, FieldSpec fieldSpecForValuesInLastFixedField, RowSpec rowSpecWithAllValuesForLastFixedField) { }

    @Override
    public void fieldFixedToValue(Field field, Object current) { }

    @Override
    public void unableToStepFurther(ReductiveState reductiveState) { }

    @Override
    public void noValuesForField(ReductiveState reductiveState) { }

    @Override
    public void unableToEmitRowAsSomeFieldSpecsAreEmpty(ReductiveState reductiveState, Map<Field, FieldSpec> fieldSpecsPerField) { }

    @Override
    public void atEndOfField(Field field, ReductiveState reductiveState) { }
}
