package com.scottlogic.deg.generator.Guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.scottlogic.deg.generator.CommandLine.GenerateCommandLine;
import com.scottlogic.deg.generator.decisiontree.tree_partitioning.NoopTreePartitioner;
import com.scottlogic.deg.generator.decisiontree.tree_partitioning.RelatedFieldTreePartitioner;
import com.scottlogic.deg.generator.decisiontree.tree_partitioning.TreePartitioner;
import com.scottlogic.deg.generator.generation.GenerationConfigSource;

public class TreePartitioningProvider implements Provider<TreePartitioner> {
    private final GenerationConfigSource commandLine;

    @Inject
    public TreePartitioningProvider(GenerationConfigSource commandLine) {
        this.commandLine = commandLine;
    }

    @Override
    public TreePartitioner get() {
        if (this.commandLine.shouldDoPartitioning()){
            return new RelatedFieldTreePartitioner();
        }
        return new NoopTreePartitioner();
    }
}
