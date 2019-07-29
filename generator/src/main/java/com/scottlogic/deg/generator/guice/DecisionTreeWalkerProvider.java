/*
 * Copyright 2019 Scott Logic Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scottlogic.deg.generator.guice;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.scottlogic.deg.common.ValidationException;
import com.scottlogic.deg.generator.config.detail.DataGenerationType;
import com.scottlogic.deg.generator.generation.GenerationConfigSource;
import com.scottlogic.deg.generator.walker.*;
import com.scottlogic.deg.generator.walker.rowspec.RandomRowSpecDecisionTreeWalker;
import com.scottlogic.deg.generator.walker.rowspec.RowSpecDecisionTreeWalker;

public class DecisionTreeWalkerProvider implements Provider<DecisionTreeWalker> {
    private final ReductiveDecisionTreeWalker reductiveDecisionTreeWalker;
    private final RandomReductiveDecisionTreeWalker randomReductiveDecisionTreeWalker;
    private final RowSpecDecisionTreeWalker rowSpecDecisionTreeWalker;
    private final RandomRowSpecDecisionTreeWalker randomRowSpecDecisionTreeWalker;
    private final GenerationConfigSource configSource;

    @Inject
    public DecisionTreeWalkerProvider(
        ReductiveDecisionTreeWalker reductiveDecisionTreeWalker,
        RowSpecDecisionTreeWalker rowSpecDecisionTreeWalker,
        RandomReductiveDecisionTreeWalker randomReductiveDecisionTreeWalker,
        RandomRowSpecDecisionTreeWalker randomRowSpecDecisionTreeWalker,
        GenerationConfigSource configSource) {
        this.reductiveDecisionTreeWalker = reductiveDecisionTreeWalker;
        this.rowSpecDecisionTreeWalker = rowSpecDecisionTreeWalker;
        this.randomReductiveDecisionTreeWalker = randomReductiveDecisionTreeWalker;
        this.randomRowSpecDecisionTreeWalker = randomRowSpecDecisionTreeWalker;
        this.configSource = configSource;
    }

    @Override
    public DecisionTreeWalker get() {
          switch(configSource.getWalkerType()) {
              case CARTESIAN_PRODUCT:
              case DECISION_BASED:
                  if (configSource.getGenerationType() == DataGenerationType.RANDOM)
                      return randomRowSpecDecisionTreeWalker;
                  return rowSpecDecisionTreeWalker;

              case REDUCTIVE:
                  if (configSource.getGenerationType() == DataGenerationType.RANDOM)
                      return randomReductiveDecisionTreeWalker;

                  return reductiveDecisionTreeWalker;

              default:
                  throw new ValidationException("no WalkerType selected");
        }
    }
}
