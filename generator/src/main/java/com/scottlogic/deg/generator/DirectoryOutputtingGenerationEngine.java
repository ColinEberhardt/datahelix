package com.scottlogic.deg.generator;

import com.google.inject.Inject;
import com.scottlogic.deg.generator.decisiontree.DecisionTreeFactory;
import com.scottlogic.deg.generator.generation.DataGenerator;
import com.scottlogic.deg.generator.outputs.targets.DirectoryOutputTarget;
import com.scottlogic.deg.generator.violations.ViolationFilter;

public class DirectoryOutputtingGenerationEngine extends GenerationEngine {
    @Inject
    public DirectoryOutputtingGenerationEngine(
        DirectoryOutputTarget outputter,
        DataGenerator dataGenerator,
        DecisionTreeFactory decisionTreeGenerator,
        ViolationFilter violationFilter) {
        super(outputter, dataGenerator, decisionTreeGenerator, violationFilter);
    }
}
