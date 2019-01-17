package com.scottlogic.deg.generator.violations;

import com.scottlogic.deg.generator.Rule;
import com.scottlogic.deg.generator.constraints.Constraint;
import com.scottlogic.deg.generator.constraints.atomic.IsStringShorterThanConstraint;

public class DontViolateStringMaxLengthFilter implements ViolationFilter {
    @Override
    public boolean canViolateRule(Rule rule) {
        return true;
    }

    @Override
    public boolean canViolateConstraint(Constraint constraint) {
        return !(constraint instanceof IsStringShorterThanConstraint);
    }
}

