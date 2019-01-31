package com.scottlogic.deg.generator.walker.reductive;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.generation.ReductiveDataGeneratorMonitor;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.restrictions.NullRestrictions;
import com.scottlogic.deg.generator.restrictions.Nullness;
import com.scottlogic.deg.generator.restrictions.SetRestrictions;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class FixedField {
    private static final Object NOT_ITERATED = new NotIterated();

    public final Field field;

    private final Stream<Object> values;
    private final FieldSpec valuesFieldSpec;
    private final ReductiveDataGeneratorMonitor monitor;
    final ReductiveState reductiveState;

    private Object current = NOT_ITERATED;
    private FieldSpec fieldSpec;

    FixedField(
        Field field,
        Stream<Object> values,
        FieldSpec valuesFieldSpec,
        ReductiveDataGeneratorMonitor monitor,
        ReductiveState reductiveState) {
        this.field = field;
        this.values = values;
        this.valuesFieldSpec = valuesFieldSpec;
        this.monitor = monitor;
        this.reductiveState = reductiveState;
    }

    public Stream<Object> getStream() {
        Iterator<Object> iterator = this.values.iterator();
        MonitoringIterator<Object> monitoringIterator = new MonitoringIterator<>(field, iterator, monitor, (value) -> {
            current = value;
            fieldSpec = null;
        }, reductiveState);

        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(monitoringIterator, Spliterator.ORDERED),
            false);
    }

    public FieldSpec getFieldSpecForValues(){ //TODO: need to check if this instance is ever used to produce values, if so then it needs to know about the filter
        return this.valuesFieldSpec;
    }

    @Override
    public String toString() {
        return this.current == NOT_ITERATED
            ? this.field.name
            : String.format("[%s] = %s", this.field.name, this.current);
    }

    FieldSpec getFieldSpecForCurrentValue(){
        if (this.fieldSpec != null) {
            return this.fieldSpec;
        }

        return this.fieldSpec = current == null
            ? getNullRequiredFieldSpec()
            : getFieldSpecForCurrentValue(current);
    }

    private FieldSpec getFieldSpecForCurrentValue(Object currentValue) {
        if (currentValue == NOT_ITERATED){
            throw new UnsupportedOperationException("FixedField has not iterated yet");
        }

        return FieldSpec.Empty.withSetRestrictions(
            new SetRestrictions(new HashSet<>(Collections.singletonList(currentValue)), null),
            this.valuesFieldSpec.getFieldSpecSource()
        ).withNullRestrictions(
            new NullRestrictions(Nullness.MUST_NOT_BE_NULL),
            this.valuesFieldSpec.getFieldSpecSource()
        );
    }

    private FieldSpec getNullRequiredFieldSpec() {
        return FieldSpec.Empty
        .withNullRestrictions(
            new NullRestrictions(Nullness.MUST_BE_NULL),
            this.valuesFieldSpec.getFieldSpecSource()
        );
    }

    public Object getCurrentValue() {
        if (this.current == NOT_ITERATED){
            throw new UnsupportedOperationException("FixedField has not iterated yet");
        }

        return this.current;
    }

    private static class NotIterated { }

    class MonitoringIterator<T> implements Iterator<T> {
        private final Field field;
        private final Iterator<T> underlyingIterator;
        private final ReductiveDataGeneratorMonitor monitor;
        private final Consumer<T> valueChanged;
        private ReductiveState reductiveState;

        MonitoringIterator(Field field, Iterator<T> underlyingIterator, ReductiveDataGeneratorMonitor monitor, Consumer<T> valueChanged, ReductiveState reductiveState) {
            this.field = field;
            this.underlyingIterator = underlyingIterator;
            this.monitor = monitor;
            this.valueChanged = valueChanged;
            this.reductiveState = reductiveState;
        }

        @Override
        public boolean hasNext() {
            boolean hasNext = underlyingIterator.hasNext();
            if (!hasNext){
                monitor.atEndOfField(field, reductiveState);
            }

            return hasNext;
        }

        @Override
        public T next() {
            T value = underlyingIterator.next();
            monitor.fieldFixedToValue(field, value);
            valueChanged.accept(value);
            return value;
        }
    }
}
