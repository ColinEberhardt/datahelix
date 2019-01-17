package com.scottlogic.deg.generator.violations;

import com.scottlogic.deg.generator.Rule;
import com.scottlogic.deg.generator.constraints.Constraint;
import com.scottlogic.deg.generator.constraints.atomic.IsNullConstraint;
import com.scottlogic.deg.generator.constraints.atomic.NotConstraint;

public class DontViolateNullabilityFilter implements ViolationFilter {
    @Override
    public boolean canViolateRule(Rule rule) {
        return true;
    }

    @Override
    public boolean canViolateConstraint(Constraint constraint) {
        if (constraint instanceof IsNullConstraint){
            return false;
        }

        if (constraint instanceof NotConstraint) {
            return !(((NotConstraint) constraint).negatedConstraint instanceof IsNullConstraint);
        }

        return true;
    }
}
