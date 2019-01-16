package com.scottlogic.deg.generator.constraints;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StringLength {
    public Optional<Integer> lengthIs = Optional.empty();
    public List<Integer> lengthIsNot = new ArrayList<>();
    public Integer longerThan = 0;
    public Integer shorterThan = Integer.MAX_VALUE;
}
