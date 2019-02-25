package com.scottlogic.deg.generator.generation;

import com.scottlogic.deg.schemas.v3.AtomicConstraintType;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface GenerationConfigSource {
    /**
     * Gets a value signifying the data generation type, i.e. one of interesting, full sequential or random.
     * @return Enum value of the current generation types.
     */
    GenerationConfig.DataGenerationType getGenerationType();

    /**
     * Gets a value signifying the combination strategy, i.e. one of exhaustive, pinning or minimal
     * @return Enum value of the current combination strategy.
     */
    GenerationConfig.CombinationStrategyType getCombinationStrategyType();

    /**
     * Gets a value signifying the current tree walker type, i.e. the reductive walker.
     * @return Enum value of the current decision tree walker.
     */
    GenerationConfig.TreeWalkerType getWalkerType();
    GenerationConfig.MonitorType getMonitorType();
    List<AtomicConstraintType> getConstraintsToNotViolate();
    Optional<Long> getMaxRows();
    boolean getValidateProfile();
    boolean shouldDoPartitioning();
    boolean dontOptimise();
    Path getOutputPath();
    boolean isEnableTracing();
    File getProfileFile();
    boolean shouldViolate();
    boolean visualiseReductions();
    boolean overwriteOutputFiles();
}
