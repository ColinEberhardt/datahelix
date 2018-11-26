package com.scottlogic.deg.generator.decisiontree.test_utils;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.scottlogic.deg.generator.constraints.IsGreaterThanConstantConstraint;
import com.scottlogic.deg.generator.decisiontree.tree_partitioning.test_utils.mapping.IConstraintMapper;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = IsInSetConstraintDto.class, name = "IsInSetConstraint"),
        @JsonSubTypes.Type(value = IsEqualToConstantConstraintDto.class, name = "IsEqualToConstantConstraint"),
        @JsonSubTypes.Type(value = IsStringShorterThanConstraintDto.class, name = "IsStringShorterThanConstraint"),
        @JsonSubTypes.Type(value = IsOfTypeConstraintDto.class, name = "IsOfTypeConstraint"),
        @JsonSubTypes.Type(value = NotConstraintDto.class, name = "NotConstraint"),
        @JsonSubTypes.Type(value = IsNullConstraintDto.class, name = "IsNullConstraint"),
        @JsonSubTypes.Type(value = IsLessThanConstantConstraintDto.class, name = "IsLessThanConstantConstraint"),
        @JsonSubTypes.Type(value = IsGreaterThanConstantConstraintDto.class, name = "IsGreaterThanConstantConstraint"),
        @JsonSubTypes.Type(value = IsGreaterThanOrEqualToConstantConstraintDto.class, name = "IsGreaterThanOrEqualToConstantConstraint")
})
public interface ConstraintDto extends IConstraintMapper {
}
