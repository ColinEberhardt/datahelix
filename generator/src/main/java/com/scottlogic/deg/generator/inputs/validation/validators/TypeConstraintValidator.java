package com.scottlogic.deg.generator.inputs.validation.validators;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.constraints.atomic.IsOfTypeConstraint;
import com.scottlogic.deg.generator.inputs.validation.*;
import com.scottlogic.deg.generator.restrictions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypeConstraintValidator implements ConstraintValidatorAlerts {

    private final List<ValidationAlert> alerts;

    private TypeRestrictions currentRestrictions;


    public TypeConstraintValidator(){
        this.alerts = new ArrayList<>();
        this.currentRestrictions = new TypeRestrictions(Arrays.asList(IsOfTypeConstraint.Types.values()));
    }


    public void isOfType(Field field, IsOfTypeConstraint.Types type) {

        TypeRestrictions candidateRestrictions = new TypeRestrictions(Arrays.asList(type));
        TypeRestrictionsMerger merger = new TypeRestrictionsMerger();

        currentRestrictions = merger.merge(currentRestrictions, candidateRestrictions);
    }

    @Override
    public List<ValidationAlert> getAlerts() {
        return alerts;
    }
}
