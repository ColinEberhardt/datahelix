package com.scottlogic.deg.generator.constraints.atomic;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.constraints.StringLength;
import com.scottlogic.deg.generator.inputs.RuleInformation;

import java.util.Objects;
import java.util.Set;

public class NotStringHasLengthConstraint implements AtomicConstraint, StringLengthConstraint {
    public final Field field;
    public final int referenceValue;
    private final Set<RuleInformation> rules;

    public NotStringHasLengthConstraint(Field field, int referenceValue, Set<RuleInformation> rules) {
        if (referenceValue < 0){
            throw new IllegalArgumentException("Cannot create a NotStringHasLengthConstraint for field '" +
                field.name + "' with a negative length.");
        }

        this.rules = rules;
        this.referenceValue = referenceValue;
        this.field = field;
    }

    @Override
    public AtomicConstraint negate(){
        return new StringHasLengthConstraint(field, referenceValue + 1, rules);
    }

    @Override
    public StringLength getStringLength() {
        StringLength s = new StringLength();
        s.lengthIsNot.add(referenceValue);
        return s;
    }

    @Override
    public String toDotLabel() {
        return String.format("%s length = not %s", field.name, referenceValue);
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o instanceof ViolatedAtomicConstraint) {
            return o.equals(this);
        }
        if (o == null || getClass() != o.getClass()) return false;
        NotStringHasLengthConstraint constraint = (NotStringHasLengthConstraint) o;
        return Objects.equals(field, constraint.field) && Objects.equals(referenceValue, constraint.referenceValue);
    }

    @Override
    public int hashCode(){
        return Objects.hash(field, referenceValue);
    }

    @Override
    public String toString() { return String.format("`%s` length = not %s", field.name, referenceValue); }

    @Override
    public Set<RuleInformation> getRules() {
        return rules;
    }

    @Override
    public AtomicConstraint withRules(Set<RuleInformation> rules) {
        return new StringHasLengthConstraint(this.field, this.referenceValue, rules);
    }
}
