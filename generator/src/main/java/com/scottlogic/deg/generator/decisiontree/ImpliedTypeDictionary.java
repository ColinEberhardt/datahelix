package com.scottlogic.deg.generator.decisiontree;

import com.scottlogic.deg.generator.constraints.atomic.*;
import com.sun.org.apache.regexp.internal.RE;

import java.util.Optional;

public class ImpliedTypeDictionary {

    public Optional<IsOfTypeConstraint> getImpliedTypeConstraint(AtomicConstraint constraint){
        Optional<IsOfTypeConstraint.Types> impliedType = getImpliedType(constraint);
        if (!impliedType.isPresent()){
            return Optional.empty();
        }

        return Optional.of(new IsOfTypeConstraint(constraint.getField(), impliedType.get(), constraint.getRules()));
    }

    public Optional<IsOfTypeConstraint.Types> getImpliedType(AtomicConstraint constraint){
        if (constraint instanceof NotConstraint){
            return getImpliedType(((NotConstraint) constraint).negatedConstraint);
        }

        if (constraint instanceof ViolatedAtomicConstraint) { //???
            return getImpliedType(((ViolatedAtomicConstraint) constraint).violatedConstraint);
        }

        if (constraint instanceof ContainsRegexConstraint ||
            constraint instanceof FormatConstraint ||
            constraint instanceof IsStringLongerThanConstraint ||
            constraint instanceof IsStringShorterThanConstraint ||
            constraint instanceof MatchesRegexConstraint ||
            constraint instanceof StringHasLengthConstraint
        ){
            return Optional.of(IsOfTypeConstraint.Types.STRING);
        }

        if (constraint instanceof IsAfterConstantDateTimeConstraint ||
            constraint instanceof IsAfterOrEqualToConstantDateTimeConstraint||
            constraint instanceof IsBeforeConstantDateTimeConstraint||
            constraint instanceof IsBeforeOrEqualToConstantDateTimeConstraint
        ){
            return Optional.of(IsOfTypeConstraint.Types.TEMPORAL);
        }

        if (constraint instanceof IsGranularToConstraint ||
            constraint instanceof IsGreaterThanConstantConstraint ||
            constraint instanceof IsLessThanConstantConstraint ||
            constraint instanceof IsLessThanOrEqualToConstantConstraint
        ){
            return Optional.of(IsOfTypeConstraint.Types.NUMERIC);
        }

        return Optional.empty();
    }
}
