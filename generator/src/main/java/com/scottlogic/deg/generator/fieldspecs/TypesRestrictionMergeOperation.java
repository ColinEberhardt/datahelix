package com.scottlogic.deg.generator.fieldspecs;

import com.scottlogic.deg.generator.restrictions.TypeRestrictions;
import com.scottlogic.deg.generator.restrictions.TypeRestrictionsMerger;

import java.util.Optional;

public class TypesRestrictionMergeOperation implements RestrictionMergeOperation {
    private static final TypeRestrictionsMerger typeRestrictionsMerger = new TypeRestrictionsMerger();

    @Override
    public Optional<FieldSpec> applyMergeOperation(FieldSpec left, FieldSpec right, FieldSpec merging) {
        TypeRestrictions mergeResult = typeRestrictionsMerger.merge(
            left.getTypeRestrictions(),
            right.getTypeRestrictions());

        return Optional.of(merging.withTypeRestrictions(
            mergeResult,
            FieldSpecSource.fromFieldSpecs(left, right)));
    }
}

