package com.scottlogic.deg.generator.restrictions;

import com.scottlogic.deg.generator.constraints.atomic.IsOfTypeConstraint;

import java.util.Set;

public interface TypeRestrictions {
    TypeRestrictions except(IsOfTypeConstraint.Types... types);

    boolean isTypeAllowed(IsOfTypeConstraint.Types type);

    Set<IsOfTypeConstraint.Types> getAllowedTypes();
}
