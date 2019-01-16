package com.scottlogic.deg.generator.constraints;

import com.scottlogic.deg.generator.constraints.atomic.*;

import java.util.Collections;
import java.util.Set;

public class StringConstraintsCollection {
    private StringLength length;

    public StringConstraintsCollection(Set<AtomicConstraint> constraints) {
        length = constraints
            .stream()
            .filter(StringLengthConstraint.class::isInstance)
            .map(x -> ((StringLengthConstraint) x).getStringLength())
            .reduce(StringLength::mergeWith)
            .orElse(new StringLength());
    }

    public StringConstraintsCollection(StringLength length) {
        this.length = length;
    }

    public StringConstraintsCollection(AtomicConstraint constraint) {
        this(Collections.singleton(constraint));
    }

    public StringConstraintsCollection union(StringConstraintsCollection otherConstraint) {
        return new StringConstraintsCollection(length.mergeWith(otherConstraint.length));
    }

    public boolean isContradictory() {
        return length.isContradictory();
    }
}