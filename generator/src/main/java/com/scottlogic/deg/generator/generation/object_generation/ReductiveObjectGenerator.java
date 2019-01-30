package com.scottlogic.deg.generator.generation.object_generation;

import com.google.inject.Inject;
import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.Profile;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.fieldspecs.RowSpec;
import com.scottlogic.deg.generator.generation.FieldSpecValueGenerator;
import com.scottlogic.deg.generator.generation.databags.DataBag;
import com.scottlogic.deg.generator.outputs.GeneratedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReductiveObjectGenerator implements ObjectGenerator {
    private final FieldSpecValueGenerator generator;

    @Inject
    public ReductiveObjectGenerator(FieldSpecValueGenerator generator){
        this.generator = generator;
    }

    @Override
    public Stream<GeneratedObject> generateObjectsFromRowSpecs(Profile profile, Stream<RowSpec> rowSpecs) {
        return rowSpecs.map(rowSpec -> generateObjectFromRowSpec(profile, rowSpec))
            .filter(Objects::nonNull);
    }

    private GeneratedObject generateObjectFromRowSpec(Profile profile, RowSpec rowSpec){
        List<DataBag> dataBagList = new ArrayList<>();

        for (Field field : rowSpec.getFields()) {
            FieldSpec specForField = rowSpec.getSpecForField(field);
            Optional<DataBag> dataValue = generator.generate(field, specForField).findFirst();
            if (!dataValue.isPresent()) { return null; }

            dataBagList.add(dataValue.get());
        }
        DataBag merged = DataBag.merge(dataBagList.toArray(new DataBag[dataBagList.size()]));

        return new GeneratedObject(
            profile.fields.stream()
                .map(merged::getValueAndFormat)
                .collect(Collectors.toList()),
            merged.getRowSource(profile.fields));
    }
}
