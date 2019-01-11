package com.scottlogic.deg.generator.generation;

import com.scottlogic.deg.generator.constraints.atomic.IsOfTypeConstraint;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.generation.field_value_sources.*;
import com.scottlogic.deg.generator.restrictions.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StandardFieldValueSourceEvaluator implements FieldValueSourceEvaluator {
    private final MustContainRestrictionReducer mustContainRestrictionReducer = new MustContainRestrictionReducer();

    public Set<FieldValueSource> getFieldValueSources(FieldSpec fieldSpec, GenerationConfig generationConfig) {
        Set<FieldValueSource> validSources = new HashSet<>();
        MustContainRestriction mustContainRestriction = fieldSpec.getMustContainRestriction();
        boolean violating = generationConfig.getIsViolating();

        // check nullability...
        if (!violating && shouldHaltDueToNullRestrictions(fieldSpec)) {
            validSources.add(getNullabilityValueSource(fieldSpec));
            return validSources;
        }

        // if there's a whitelist, we can just output that
        if (fieldSpec.getSetRestrictions() != null && fieldSpec.getSetRestrictions().getWhitelist() != null) {
            Set<Object> whitelist = fieldSpec.getSetRestrictions().getWhitelist();

            // If we have values that must be included we need to check that those values are included in the whitelist
            if (mustContainRestriction != null) {
                whitelist = Stream.concat(whitelist.stream(),
                getNotNullSetRestrictionFilterOnMustContainRestriction(mustContainRestriction)
                    .flatMap(o -> o.getSetRestrictions().getWhitelist().stream())).collect(Collectors.toSet());
            }

            if (violating && fieldSpec.getNullRestrictions() != null) {
                validSources.add(getNullabilityValueSource(fieldSpec));
            }

            Stream<Object> validSourcesValues = validSources
                .stream()
                .flatMap(valueSource -> StreamSupport.stream(valueSource.generateAllValues().spliterator(), false));
            List<Object> whitelistAndValidSourcesValues = Stream
                .concat(whitelist.stream(), validSourcesValues)
                .collect(Collectors.toList());

            if (whitelistAndValidSourcesValues.isEmpty()){
                System.out.println(String.format(
                    "No values available for field %s (%s)",
                    fieldSpec.getFieldSpecSource().getConstraints().iterator().next().getField().name,
                    fieldSpec.toString()));
            }

            return Collections.singleton(
                new CannedValuesFieldValueSource(whitelistAndValidSourcesValues)
            );
        }

        if (mustContainRestriction != null) {
            applyMustConstrainRestrictionToValidSources(validSources, fieldSpec, generationConfig);
        }

        TypeRestrictions typeRestrictions = fieldSpec.getTypeRestrictions() != null
            ? fieldSpec.getTypeRestrictions()
            : DataTypeRestrictions.ALL_TYPES_PERMITTED;

        if (typeRestrictions.isTypeAllowed(IsOfTypeConstraint.Types.NUMERIC)) {
            NumericRestrictions restrictions = fieldSpec.getNumericRestrictions() == null
                ? new NumericRestrictions()
                : fieldSpec.getNumericRestrictions();

            int numericScale = fieldSpec.getGranularityRestrictions() != null
                ? fieldSpec.getGranularityRestrictions().getNumericScale()
                : 0;

            if (numericScale == 0) {
                validSources.add(
                    new IntegerFieldValueSource(
                        restrictions,
                        getBlacklist(fieldSpec)));
            } else {
                validSources.add(
                    new RealNumberFieldValueSource(
                        restrictions,
                        getBlacklist(fieldSpec),
                        numericScale));
            }
        }

        if (typeRestrictions.isTypeAllowed(IsOfTypeConstraint.Types.STRING)) {

            StringRestrictions stringRestrictions = fieldSpec.getStringRestrictions();
            if (stringRestrictions != null && (stringRestrictions.stringGenerator != null)) {
                Set<Object> blacklist = getBlacklist(fieldSpec);

                final StringGenerator generator;
                if (blacklist.size() > 0) {
                    RegexStringGenerator blacklistGenerator = RegexStringGenerator.createFromBlacklist(blacklist);

                    generator = stringRestrictions.stringGenerator.intersect(blacklistGenerator);
                } else {
                    generator = stringRestrictions.stringGenerator;
                }

                validSources.add(generator.asFieldValueSource());

            } else {
                // todo: move default interesting values into the string field value source
                validSources.add(CannedValuesFieldValueSource.of("Lorem Ipsum"));
            }
        }

        if (typeRestrictions.isTypeAllowed(IsOfTypeConstraint.Types.TEMPORAL)) {

            DateTimeRestrictions restrictions = fieldSpec.getDateTimeRestrictions();
            validSources.add(new TemporalFieldValueSource(
                restrictions != null ? restrictions : new DateTimeRestrictions(),
                getBlacklist(fieldSpec)));
        }

        if (violating) {
            validSources.add(getNullabilityValueSource(fieldSpec));
        }

        return validSources;
    }

    private boolean shouldHaltDueToNullRestrictions(FieldSpec fieldSpec) {
        if (fieldSpec.getNullRestrictions() != null) {
            // if *always* null, add a null-only source and signal that no other sources are needed
            return fieldSpec.getNullRestrictions().nullness == Nullness.MUST_BE_NULL;
        }

        return false;
    }

    private FieldValueSource getNullabilityValueSource(FieldSpec fieldSpec) {
        FieldValueSource nullOnlySource = new CannedValuesFieldValueSource(Collections.singletonList(null));
        if (fieldSpec.getNullRestrictions() != null) {
            if (fieldSpec.getNullRestrictions().nullness == Nullness.MUST_BE_NULL) {
                // if *always* null, add a null-only source and signal that no other sources are needed
                return nullOnlySource;
            } else if (fieldSpec.getNullRestrictions().nullness == Nullness.MUST_NOT_BE_NULL) {
                // if *never* null, add nothing and signal that source generation should continue
                return new CannedValuesFieldValueSource(Collections.emptyList());
            }
        }
        // if none of the above, the field is nullable
        return nullOnlySource;
    }

    private Set<Object> getBlacklist(FieldSpec fieldSpec) {
        if (fieldSpec.getSetRestrictions() == null)
            return Collections.emptySet();

        return new HashSet<>(fieldSpec.getSetRestrictions().getBlacklist());
    }

    private Stream<FieldSpec> getNotNullSetRestrictionFilterOnMustContainRestriction(MustContainRestriction restriction) {
        return restriction.getRequiredObjects()
            .stream()
            .filter(o -> o.getSetRestrictions() != null);
    }

    private void applyMustConstrainRestrictionToValidSources(
        Set<FieldValueSource> validSources,
        FieldSpec fieldSpec,
        GenerationConfig generationConfig) {
        Set<FieldSpec> mustContainRestrictionFieldSpecs = fieldSpec.getMustContainRestriction().getRequiredObjects();
        if (mustContainRestrictionFieldSpecs.size() > 1) {
            mustContainRestrictionFieldSpecs = mustContainRestrictionReducer.getReducedMustContainRestriction(fieldSpec);
        }

        mustContainRestrictionFieldSpecs.forEach(fs -> validSources.addAll(getFieldValueSources(fs, generationConfig)));
    }
}
