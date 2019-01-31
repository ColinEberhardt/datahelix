package com.scottlogic.deg.generator.generation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ValueBuildingIterator<T> implements Iterator<T> {
    private final List<T> values = new ArrayList<>();
    private int pointer = -1;
    private boolean finishedIterating;

    public void add(T value){
        if (finishedIterating){
            throw new UnsupportedOperationException("Unable to add a value once iteration has finished, it'll never be read");
        }

        values.add(value);
    }

    public int size(){
        return values.size();
    }

    @Override
    public boolean hasNext() {
        return finishedIterating = pointer < values.size() -1;
    }

    @Override
    public T next() {
        pointer++;
        return values.get(pointer);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public void clear(){
        values.clear();
    }

    public void remove(T value){
        if (values.indexOf(value) <= pointer){
            pointer--; //make sure we dont skip an item if we're iterating
        }

        values.remove(value);
    }
}
