package com.scottlogic.deg.generator.walker;

import com.scottlogic.deg.generator.decisiontree.DecisionTree;
import com.scottlogic.deg.generator.fieldspecs.RowSpec;
import com.scottlogic.deg.generator.walker.reductive.field_selection_strategy.FixFieldStrategy;

import java.util.stream.Stream;

public interface DecisionTreeWalker {
    Stream<RowSpec> walk(DecisionTree tree, FixFieldStrategy fixFieldStrategy);
}
