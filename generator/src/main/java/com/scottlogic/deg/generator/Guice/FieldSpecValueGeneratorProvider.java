package com.scottlogic.deg.generator.Guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.scottlogic.deg.generator.generation.FieldSpecValueGenerator;
import com.scottlogic.deg.generator.generation.GenerationConfig;
import com.scottlogic.deg.generator.generation.GenerationConfigSource;
import com.scottlogic.deg.generator.generation.StandardFieldSpecValueGenerator;
import com.scottlogic.deg.generator.generation.ReductivePinningFieldSpecValueGenerator;

public class FieldSpecValueGeneratorProvider implements Provider<FieldSpecValueGenerator> {

    private final GenerationConfigSource configSource;
    private final StandardFieldSpecValueGenerator standardEvaluator;

    @Inject
    public FieldSpecValueGeneratorProvider(GenerationConfigSource configSource, StandardFieldSpecValueGenerator standardEvaluator) {
        this.configSource = configSource;
        this.standardEvaluator = standardEvaluator;
    }

    @Override
    public FieldSpecValueGenerator get() {
        if (configSource.getWalkerType() == GenerationConfig.TreeWalkerType.REDUCTIVE) {
            switch (configSource.getCombinationStrategyType()){
                case PINNING:
                    throw new UnsupportedOperationException("Pinning isn't supported with the reductive mode yet");
                case EXHAUSTIVE:
                    return standardEvaluator;
                case MINIMAL:
                    throw new UnsupportedOperationException("Minimal isn't supported with the reductive mode yet");
            }
        }

        return standardEvaluator;
    }
}
