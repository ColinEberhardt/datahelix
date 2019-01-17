package com.scottlogic.deg.generator.violations;

import com.scottlogic.deg.generator.Rule;
import com.scottlogic.deg.generator.constraints.Constraint;

public interface ViolationFilter {
    boolean canViolateRule(Rule rule);
    boolean canViolateConstraint(Constraint constraint);
}

