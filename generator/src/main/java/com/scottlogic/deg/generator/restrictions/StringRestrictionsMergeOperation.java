package com.scottlogic.deg.generator.restrictions;

import com.scottlogic.deg.generator.constraints.IsOfTypeConstraint;

public class StringRestrictionsMergeOperation implements RestrictionMergeOperation {
    private static final StringRestrictionsMerger stringRestrictionsMerger = new StringRestrictionsMerger();

    @Override
    public boolean applyMergeOperation(FieldSpec left, FieldSpec right, FieldSpec merged) {
        MergeResult<StringRestrictions> mergeResult = stringRestrictionsMerger.merge(
            left.getStringRestrictions(), right.getStringRestrictions());

        if (!mergeResult.successful) {
            return false;
        }

        StringRestrictions stringRestrictions = mergeResult.restrictions;

        if (stringRestrictions != null) {
            ITypeRestrictions typeRestrictions = merged.getTypeRestrictions();
            if (typeRestrictions.isTypeAllowed(IsOfTypeConstraint.Types.String)) {
                merged.setTypeRestrictions(TypeRestrictions.createFromWhiteList(IsOfTypeConstraint.Types.String));
            } else {
                return false;
            }
        }

        merged.setStringRestrictions(mergeResult.restrictions);
        return true;
    }
}

