package com.scottlogic.deg.generator.restrictions;

import java.util.stream.Collectors;

public class TypeRestrictionsMerger {
    public TypeRestrictions merge(TypeRestrictions left, TypeRestrictions right) {
        if (left == null && right == null)
            return TypeRestrictions.ALL_TYPES_PERMITTED;
        if (left == null)
            return right;
        if (right == null)
            return left;

        return new TypeRestrictions(
            left.getAllowedTypes().stream()
                .filter(right.getAllowedTypes()::contains)
                .collect(Collectors.toList()));
    }
}
