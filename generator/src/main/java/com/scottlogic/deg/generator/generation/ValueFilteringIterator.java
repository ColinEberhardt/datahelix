package com.scottlogic.deg.generator.generation;

import com.scottlogic.deg.generator.Field;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class ValueFilteringIterator<TSource, TValue> implements Iterator<TSource> {
    private final Set<TValue> memoisedValues;
    private final Iterator<TSource> iterator;
    private final Field field;
    private final Function<TSource, TValue> extractValue;
    private final ReductivePinningFieldCache pinningCache;
    private NullPermittingOptional<TSource> nextValue = null;
    private ValueBuildingIterator<TSource> valuesToEmitIfNoneAccepted = new ValueBuildingIterator<>(); //default to no-seen-value
    private boolean valueAcceptedByConsumer = false;
    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalAssignedToNull"})
    private Optional<Boolean> hasNext = null;

    ValueFilteringIterator(
        Field field,
        Set<TValue> memoisedValues,
        Iterator<TSource> iterator,
        Function<TSource, TValue> extractValue,
        ReductivePinningFieldCache pinningCache) {

        this.field = field;
        this.extractValue = extractValue;
        this.pinningCache = pinningCache;
        if (memoisedValues == null) {
            throw new IllegalArgumentException("memoisedValues must not be null");
        }
        if (iterator == null) {
            throw new IllegalArgumentException("iterator must not be null");
        }

        this.memoisedValues = memoisedValues;
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        //noinspection OptionalAssignedToNull
        if (hasNext != null) {
            //noinspection OptionalGetWithoutIsPresent
            return hasNext.get(); //caching, dont move to the next value in the iterator twice if calling hasNext() twice
        }

        nextValue = getNextValue();
        hasNext = Optional.of(nextValue.isPresent() || (!valueAcceptedByConsumer && valuesToEmitIfNoneAccepted.hasNext()));

        if (!hasNext.get()) {
            pinningCache.removeIterator(field, this);
        }

        return hasNext.orElse(false);
    }

    @Override
    public TSource next() {
        if (nextValue == null) {
            throw new IllegalStateException("next() called before hasNext()");
        }

        if (nextValue.isPresent()) {
            //valueAcceptedByConsumer = true;
            valuesToEmitIfNoneAccepted.remove(nextValue.get());
            //noinspection OptionalAssignedToNull
            hasNext = null;
            return nextValue.get();
        }

        if (valuesToEmitIfNoneAccepted.hasNext()) {
            //noinspection OptionalAssignedToNull
            hasNext = null;
            //valueAcceptedByConsumer = true;
            return valuesToEmitIfNoneAccepted.next();
        }

        throw new IllegalStateException("next() called when hasNext() returned false");
    }

    private NullPermittingOptional<TSource> getNextValue() {
        while (iterator.hasNext()) {
            TSource next = iterator.next();
            valuesToEmitIfNoneAccepted.add(next);

            TValue nextValue = extractValue.apply(next);
            if (!memoisedValues.contains(nextValue)) {
                return new NullPermittingOptional<>(next);
            }
        }

        return new NullPermittingOptional<>();
    }

    void valueEmitted(TValue value) {
        valuesToEmitIfNoneAccepted.clear();
        valueAcceptedByConsumer = true;
    }
}
