package com.scottlogic.deg.generator.generation;

import com.google.inject.Inject;
import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.generation.databags.DataBag;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ReductivePinningFieldSpecValueGenerator implements FieldSpecValueGenerator {
    private final FieldSpecValueGenerator underlyingGenerator;
    private final ReductivePinningFieldCache pinningCache;

    @Inject
    public ReductivePinningFieldSpecValueGenerator(StandardFieldSpecValueGenerator underlyingGenerator, ReductivePinningFieldCache pinningCache) {
        this.underlyingGenerator = underlyingGenerator;
        this.pinningCache = pinningCache;
    }

    @Override
    public Stream<DataBag> generate(Field field, FieldSpec spec) {
        Stream<DataBag> underlyingStream = underlyingGenerator.generate(field, spec);

        Set<Object> memoisedValues = pinningCache.getSeenValues(field);
        Iterator<DataBag> underlyingIterator = underlyingStream.iterator();
        ValueFilteringIterator<DataBag, Object> valueFilteringIterator = new ValueFilteringIterator<>(
            field,
            memoisedValues,
            underlyingIterator,
            db -> db.getValue(field),
            pinningCache);
        pinningCache.replaceIterator(field, valueFilteringIterator);

        if (!valueFilteringIterator.hasNext()){
            if (underlyingIterator.hasNext()) {
                throw new IllegalStateException("Underlying iterator has values, but the filtering iterator does not, something has gone wrong!");
            }

            throw new IllegalStateException("Something appears to be wrong, there are no values for the field.");
        }

        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(
                valueFilteringIterator,
                Spliterator.ORDERED),
            false);
    }
}
