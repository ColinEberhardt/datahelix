package com.scottlogic.deg.profile.reader.atomic;

import com.google.inject.Inject;
import com.scottlogic.deg.common.ValidationException;
import com.scottlogic.deg.common.profile.Types;
import com.scottlogic.deg.common.profile.constraintdetail.AtomicConstraintType;
import com.scottlogic.deg.common.util.NumberUtils;
import com.scottlogic.deg.generator.fieldspecs.whitelist.DistributedList;
import com.scottlogic.deg.profile.dto.ConstraintDTO;
import com.scottlogic.deg.profile.reader.InvalidProfileException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AtomicConstraintValueReader {

    private final FromFileReader fromFileReader;

    @Inject
    public AtomicConstraintValueReader(FromFileReader fromFileReader) {
        this.fromFileReader = fromFileReader;
    }


    public Object getValue(ConstraintDTO dto, Types type){
        try {
            return tryGetValue(dto, type);
        } catch (IllegalArgumentException | ValidationException e){
            throw new InvalidProfileException(String.format("Field [%s]: %s", dto.field, e.getMessage()));
        }
    }

    private Object tryGetValue(ConstraintDTO dto, Types type){
        if (dto.values != null){
            return getSet(dto.values, type);
        }

        if (dto.file != null && dto.is.equals(AtomicConstraintType.IS_IN_SET.getText())){
            return fromFileReader.setFromFile(dto.file);
        }

        if (dto.file != null && dto.is.equals(AtomicConstraintType.IS_IN_MAP.getText())){
            return fromFileReader.listFromMapFile(dto.file, dto.key);
        }

        return getValue(dto.value, type);
    }

    private DistributedList getSet(Collection<Object> values, Types type) {
        List collect = values.stream()
            .map(val -> getValue(val, type))
            .distinct()
            .collect(Collectors.toList());
        return DistributedList.uniform(collect);
    }

    private Object getValue(Object value, Types type) {
        if (type == null) {
            return value;
        }
        switch (type) {
            case NUMERIC:
                return getBigDecimal(value);
            case DATETIME:
                if (!(value instanceof String) || value.equals("datetime") || value.equals("working days") || isTimeScale((String)value)) {
                    return value;
                }
                return ConstraintReaderHelpers.parseDate((String) value);
            default:
                return value;
        }
    }

    private static boolean isTimeScale(String value) {
        return Stream.of(ChronoUnit.values()).map(ChronoUnit::name).map(String::toLowerCase).anyMatch(value::equals);
    }

    private Object getBigDecimal(Object value) {
        BigDecimal bigDecimal = NumberUtils.coerceToBigDecimal(value);

        if (bigDecimal == null){
            return value;
        }

        return bigDecimal;
    }
}
