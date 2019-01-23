package com.scottlogic.deg.generator.generation;

import com.scottlogic.deg.generator.FlatMappingSpliterator;
import com.scottlogic.deg.generator.generation.field_value_sources.FieldValueSource;
import com.scottlogic.deg.generator.utils.RandomNumberGenerator;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ValueLimitingFieldValueSource implements FieldValueSource {

    private final List<FieldValueSource> sources;
    private final int maxValues;

    ValueLimitingFieldValueSource(List<FieldValueSource> sources, int maxValues) {
        this.sources = sources;
        this.maxValues = maxValues;
    }

    @Override
    public boolean isFinite() {
        return true;
    }

    @Override
    public long getValueCount() {
        return maxValues;
    }

    @Override
    public Iterable<Object> generateInterestingValues() {
        return getValues(FieldValueSource::generateInterestingValues);
    }

    @Override
    public Iterable<Object> generateAllValues() {
        return getValues(FieldValueSource::generateAllValues);
    }

    @Override
    public Iterable<Object> generateRandomValues(RandomNumberGenerator randomNumberGenerator) {
        return getValues(source -> source.generateRandomValues(randomNumberGenerator));
    }

    private Iterable<Object> getValues(Function<FieldValueSource, Iterable<Object>> func){
        Stream<Object> stream = FlatMappingSpliterator.flatMap(
            sources.stream(),
            source -> toStream(func.apply(source)))
            .limit(maxValues);

        return stream::iterator;
    }

    private static Stream<Object> toStream(Iterable<Object> iterable){
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
