package com.scottlogic.deg.generator.Guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.scottlogic.deg.generator.generation.GenerationConfigSource;

import java.nio.file.Path;

public class ImplyTypesProvider implements Provider<Boolean> {
    final GenerationConfigSource configSource;

    @Inject
    public ImplyTypesProvider(GenerationConfigSource configSource){
        this.configSource = configSource;
    }

    @Override
    public Boolean get() {
        return configSource.implyTypes();
    }
}
