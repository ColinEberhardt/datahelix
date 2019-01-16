package com.scottlogic.deg.generator.constraints;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StringLength {
    public List<Integer> lengthIsNot = new ArrayList<>();
    public Integer longerThan = 0;
    public Integer shorterThan = Integer.MAX_VALUE;
    public Optional<Integer> lengthIs = Optional.empty();

    boolean contradictory;

    public boolean isContradictory() {
        return contradictory;
    }

    public StringLength mergeWith(StringLength other) {
        if (contradictory) {
            return this;
        }

        lengthIsNot.addAll(other.lengthIsNot);
        longerThan = Math.max(longerThan, other.longerThan);
        shorterThan = Math.min(shorterThan, other.shorterThan);

        if (!lengthIs.isPresent()) {
            lengthIs = other.lengthIs;
        }
        else {
            if (other.lengthIs.isPresent()){
                if (!lengthIs.equals(other.lengthIs)){
                    contradictory = true;
                    return this;
                }
            }
        }

        checkContradictions();
        return this;
    }

    public void checkContradictions(){
        if (contradictory) return;

        //maybe change to maxvalue and minvalue to avoid this being weird
        if (longerThan+1 > shorterThan-1) contradictory = true;

        //skipping lengthIsNot combined with longer than and shorter than range

        if (lengthIs.isPresent()) {
            int length = lengthIs.get();
            if (lengthIsNot.contains(length)) contradictory = true;

            if (length >= shorterThan) contradictory = true;

            if (length <= longerThan) contradictory = true;
        }
    }
}
