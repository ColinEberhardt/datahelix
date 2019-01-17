package com.scottlogic.deg.generator.violations;

import com.scottlogic.deg.generator.Rule;
import com.scottlogic.deg.generator.constraints.Constraint;

public class ViolateEverythingFilter implements ViolationFilter{

    @Override
    public boolean canViolateRule(Rule rule) {
        return true;
    }

    @Override
    public boolean canViolateConstraint(Constraint constraint) {
        return true;
    }
}
