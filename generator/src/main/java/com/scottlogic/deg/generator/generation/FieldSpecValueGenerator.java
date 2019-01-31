package com.scottlogic.deg.generator.generation;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.generation.databags.DataBag;

import java.util.stream.Stream;

public interface FieldSpecValueGenerator{
    Stream<DataBag> generate(Field field, FieldSpec spec);
}
