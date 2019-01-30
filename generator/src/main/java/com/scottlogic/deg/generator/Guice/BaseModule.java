package com.scottlogic.deg.generator.Guice;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.scottlogic.deg.generator.CommandLine.GenerateCommandLine;
import com.scottlogic.deg.generator.Profile;
import com.scottlogic.deg.generator.GenerationEngine;
import com.scottlogic.deg.generator.decisiontree.DecisionTreeFactory;
import com.scottlogic.deg.generator.decisiontree.DecisionTreeOptimiser;
import com.scottlogic.deg.generator.decisiontree.ProfileDecisionTreeFactory;
import com.scottlogic.deg.generator.generation.*;
import com.scottlogic.deg.generator.generation.object_generation.DataBagObjectGenerator;
import com.scottlogic.deg.generator.generation.object_generation.ObjectGenerator;
import com.scottlogic.deg.generator.inputs.JsonProfileReader;
import com.scottlogic.deg.generator.inputs.ProfileReader;
import com.scottlogic.deg.generator.inputs.validation.ProfileValidator;
import com.scottlogic.deg.generator.inputs.validation.reporters.ProfileValidationReporter;
import com.scottlogic.deg.generator.inputs.validation.reporters.SystemOutProfileValidationReporter;
import com.scottlogic.deg.generator.outputs.dataset_writers.DataSetWriter;
import com.scottlogic.deg.generator.outputs.targets.FileOutputTarget;
import com.scottlogic.deg.generator.outputs.targets.OutputTarget;
import com.scottlogic.deg.generator.violations.filters.ViolationFilter;
import com.scottlogic.deg.generator.walker.*;
import com.scottlogic.deg.generator.walker.reductive.IterationVisualiser;
import com.scottlogic.deg.generator.walker.reductive.NoOpIterationVisualiser;
import com.scottlogic.deg.generator.walker.reductive.field_selection_strategy.FixFieldStrategy;
import com.scottlogic.deg.generator.walker.reductive.field_selection_strategy.HierarchicalDependencyFixFieldStrategy;
import com.scottlogic.deg.generator.walker.routes.ExhaustiveProducer;
import com.scottlogic.deg.generator.walker.routes.RowSpecRouteProducer;

import java.nio.file.Path;
import java.util.List;

/**
 * Class to define default bindings for Guice injection. Utilises the generation config source to determine which
 * 'generate' classes should be bound for this execution run.
 */
public class BaseModule extends AbstractModule {
    private final GenerationConfigSource configSource;

    public BaseModule(GenerationConfigSource configSource) {
        this.configSource = configSource;
    }

    @Override
    protected void configure() {
        // Bind command line to correct implementation
        bindAllCommandLineTypes();

        // Bind providers - used to retrieve implementations based on user input
        bind(DecisionTreeOptimiser.class).toProvider(DecisionTreeOptimiserProvider.class);
        bind(Profile.class).toProvider(ProfileProvider.class);
        bind(DataSetWriter.class).toProvider(DataSetWriterProvider.class);
        bind(DecisionTreeWalker.class).toProvider(DecisionTreeWalkerProvider.class);
        bind(ProfileValidator.class).toProvider(ProfileValidatorProvider.class);
        bind(GenerationEngine.class).toProvider(GenerationEngineProvider.class);

        // Bind known implementations - no user input required
        bind(DataGeneratorMonitor.class).to(ReductiveDataGeneratorMonitor.class);
        bind(ReductiveDataGeneratorMonitor.class).to(NoopDataGeneratorMonitor.class);
        bind(IterationVisualiser.class).to(NoOpIterationVisualiser.class);
        bind(FixFieldStrategy.class).to(HierarchicalDependencyFixFieldStrategy.class);
        bind(DataGenerator.class).to(DecisionTreeDataGenerator.class);
        bind(DecisionTreeFactory.class).to(ProfileDecisionTreeFactory.class);
        bind(ProfileValidationReporter.class).to(SystemOutProfileValidationReporter.class);
        bind(RowSpecRouteProducer.class).to(ExhaustiveProducer.class);
        bind(ObjectGenerator.class).to(DataBagObjectGenerator.class);
        bind(ProfileReader.class).to(JsonProfileReader.class);
        bind(OutputTarget.class).to(FileOutputTarget.class);

        bind(new TypeLiteral<List<ViolationFilter>>(){}).toProvider(ViolationFiltersProvider.class);

        bind(DecisionTreeWalker.class).annotatedWith(Names.named("cartesian")).to(CartesianProductDecisionTreeWalker.class);
        bind(DecisionTreeWalker.class).annotatedWith(Names.named("reductive")).to(ReductiveDecisionTreeWalker.class);
        bind(DecisionTreeWalker.class).annotatedWith(Names.named("routed")).to(DecisionTreeRoutesTreeWalker.class);

        bind(Path.class).annotatedWith(Names.named("outputPath")).toProvider(OutputPathProvider.class);
    }

    private void bindAllCommandLineTypes() {
        if (this.configSource instanceof GenerateCommandLine) {
            bind(GenerateCommandLine.class).toInstance((GenerateCommandLine) this.configSource);
            bind(GenerationConfigSource.class).to(GenerateCommandLine.class);
        }
    }
}
