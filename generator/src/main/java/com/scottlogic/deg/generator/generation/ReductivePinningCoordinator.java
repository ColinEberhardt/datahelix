package com.scottlogic.deg.generator.generation;

import com.google.inject.Inject;
import com.scottlogic.deg.generator.ProfileFields;
import com.scottlogic.deg.generator.generation.databags.DataBag;

import java.util.Set;

public class ReductivePinningCoordinator {
    private final ReductivePinningFieldCache fieldCache;
    private ProfileFields fields;

    @Inject
    public ReductivePinningCoordinator(ReductivePinningFieldCache fieldCache) {
        this.fieldCache = fieldCache;
    }

    public void generationStarting(ProfileFields fields){
        this.fields = fields;
    }

    public void rowEmitted(DataBag row){
        fields.forEach(field -> {
            Object fieldValue = row.getValue(field);

            Set<Object> memoisedValuesForField = fieldCache.getSeenValues(field);
            ValueFilteringIterator<DataBag, Object> filteringIterator = fieldCache.getIterator(field);

            memoisedValuesForField.add(fieldValue);

            if (filteringIterator != null) {
                filteringIterator.valueEmitted(fieldValue);
            }
        });
    }
}
