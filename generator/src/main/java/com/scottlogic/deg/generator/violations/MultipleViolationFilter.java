package com.scottlogic.deg.generator.violations;

import com.scottlogic.deg.generator.Rule;
import com.scottlogic.deg.generator.constraints.Constraint;

import java.util.Arrays;

public class MultipleViolationFilter implements ViolationFilter {
    private final ViolationFilter[] filters;

    public MultipleViolationFilter(ViolationFilter... filters) {
        this.filters = filters;
    }

    @Override
    public boolean canViolateRule(Rule rule) {
        return Arrays.stream(filters).allMatch(f -> f.canViolateRule(rule));
    }

    @Override
    public boolean canViolateConstraint(Constraint constraint) {
        return Arrays.stream(filters).allMatch(f -> f.canViolateConstraint(constraint));
    }
}
