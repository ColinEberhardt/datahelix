package com.scottlogic.deg.generator.generation;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.generation.databags.DataBag;

import java.util.*;

public class ReductivePinningFieldCache{
    private final Map<Field, Set<Object>> memoisedValuesPerField = new HashMap<>();
    private final Map<Field, ValueFilteringIterator<DataBag, Object>> filteringIteratorsPerField = new HashMap<>();

    Set<Object> getSeenValues(Field field) {
        return putIfAbsent(memoisedValuesPerField, field, new HashSet<>());
    }

    void replaceIterator(Field field, ValueFilteringIterator<DataBag, Object> iterator) {
        filteringIteratorsPerField.put(field, iterator);
    }

    ValueFilteringIterator<DataBag, Object> getIterator(Field field) {
        return filteringIteratorsPerField.get(field);
    }

    void removeIterator(Field field, ValueFilteringIterator expectedIterator) {
        ValueFilteringIterator<DataBag, Object> foundIterator = filteringIteratorsPerField.get(field);
        if (foundIterator == null){
            throw new IllegalStateException("Expected an iterator but none was found");
        }

        if (foundIterator == expectedIterator){
            filteringIteratorsPerField.remove(field);
            return;
        }

        throw new IllegalStateException("Found an unexpected iterator");
    }

    private static <T> T putIfAbsent(Map<Field, T> map, Field key, T value){
        //NOTE: This looks on the face-of-it that it could be replaced with putIfAbsent() however
        // there is a bug with this method which can be seen in the put() call, it implies it can return
        // the value that has been put, but in fact always returns null, as such putIfAbsent() returns null

        if (map.containsKey(key)) {
            return map.get(key);
        }

        map.put(key, value);
        return value;
    }
}
