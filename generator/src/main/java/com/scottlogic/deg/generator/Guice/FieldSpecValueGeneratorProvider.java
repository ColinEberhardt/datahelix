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
    private final ReductivePinningFieldSpecValueGenerator reductivePinningGenerator;

    @Inject
    public FieldSpecValueGeneratorProvider(
        GenerationConfigSource configSource,
        StandardFieldSpecValueGenerator standardEvaluator,
        ReductivePinningFieldSpecValueGenerator reductivePinningGenerator) {

        this.configSource = configSource;
        this.standardEvaluator = standardEvaluator;
        this.reductivePinningGenerator = reductivePinningGenerator;
    }

    @Override
    public FieldSpecValueGenerator get() {
        if (configSource.getWalkerType() == GenerationConfig.TreeWalkerType.REDUCTIVE) {
            switch (configSource.getCombinationStrategyType()){
                case PINNING:
                    return reductivePinningGenerator;
                case EXHAUSTIVE:
                    return standardEvaluator;
                case MINIMAL:
                    //TODO: Update the documentation if this persists
                    throw new UnsupportedOperationException("Minimal isn't supported with the reductive mode yet");
            }
        }

        return standardEvaluator;
    }
}
