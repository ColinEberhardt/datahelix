package com.scottlogic.deg.generator.constraints.atomic;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.constraints.StringLength;
import com.scottlogic.deg.generator.inputs.validation.ProfileVisitor;
import com.scottlogic.deg.generator.inputs.validation.VisitableProfileElement;
import com.scottlogic.deg.generator.inputs.RuleInformation;

import java.util.Objects;
import java.util.Set;

public class IsStringShorterThanConstraint implements AtomicConstraint, VisitableProfileElement, StringLengthConstraint {
    public final Field field;
    private final Set<RuleInformation> rules;
    public final int referenceValue;

    public IsStringShorterThanConstraint(Field field, int referenceValue, Set<RuleInformation> rules) {
        if (referenceValue < 1){
            throw new IllegalArgumentException("Cannot create an IsStringShorterThanConstraint for field '" +
                field.name + "' with a negative length.");
        }

        this.referenceValue = referenceValue;
        this.field = field;
        this.rules = rules;
    }

    @Override
    public AtomicConstraint negate(){
        return new IsStringLongerThanConstraint(field, referenceValue - 1, rules);
    }

    @Override
    public StringLength getStringLength() {
       StringLength s = new StringLength();
        s.shorterThan = referenceValue;
        return s;
    }

    @Override
    public String toDotLabel(){
        return String.format("%s length < %s", field.name, referenceValue);
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
        IsStringShorterThanConstraint constraint = (IsStringShorterThanConstraint) o;
        return Objects.equals(field, constraint.field) && Objects.equals(referenceValue, constraint.referenceValue);
    }

    @Override
    public int hashCode(){
        return Objects.hash(field, referenceValue);
    }

    @Override
    public String toString() { return String.format("`%s` length < %d", field.name, referenceValue); }

    @Override
    public void accept(ProfileVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Set<RuleInformation> getRules() {
        return rules;
    }

    @Override
    public AtomicConstraint withRules(Set<RuleInformation> rules) {
        return new IsStringShorterThanConstraint(this.field, this.referenceValue, rules);
    }
}
