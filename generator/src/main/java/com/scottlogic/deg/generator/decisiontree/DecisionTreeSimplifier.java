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

package com.scottlogic.deg.generator.decisiontree;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DecisionTreeSimplifier {
    public DecisionTree simplify(DecisionTree originalTree) {
        return new DecisionTree(
            simplify(originalTree.getRootNode()),
            originalTree.getFields());
    }

    public ConstraintNode simplify(ConstraintNode node) {
        if (node.getDecisions().isEmpty())
            return node;

        ConstraintNode transformedNode = this.simplifySingleOptionDecisions(node);
        Collection<DecisionNode> simplifiedDecisions = transformedNode.getDecisions().stream()
            .map(this::simplify)
            .collect(Collectors.toList());
        return transformedNode.setDecisions(simplifiedDecisions);
    }

    private DecisionNode simplify(DecisionNode decision) {
        List<ConstraintNode> newNodes = new ArrayList<>();

        for (ConstraintNode existingOption : decision.getOptions()) {
            ConstraintNode simplifiedNode = simplify(existingOption);

            // if an option contains no constraints and only one decision, then it can be replaced by the set of options within that decision.
            // this helps simplify the sorts of trees that come from eg A OR (B OR C)
            if (simplifiedNode.getAtomicConstraints().isEmpty() && simplifiedNode.getDecisions().size() == 1) {
                newNodes.addAll(
                    simplifiedNode.getDecisions()
                        .iterator().next() //get only member
                        .getOptions());
            } else {
                newNodes.add(simplifiedNode);
            }
        }

        return decision.setOptions(newNodes);
    }

    private ConstraintNode simplifySingleOptionDecisions(ConstraintNode node) {
        return node.getDecisions()
            .stream()
            .filter(decisionNode -> decisionNode.getOptions().size() == 1)
            .reduce(
                node,
                (parentConstraint, decisionNode) -> {
                    ConstraintNode firstOption = decisionNode.getOptions().iterator().next();
                    if (parentConstraint.getAtomicConstraints().stream().anyMatch(firstOption.getAtomicConstraints()::contains)) {
                        return parentConstraint.removeDecisions(Collections.singletonList(decisionNode));
                    } else {
                        return parentConstraint
                            .addAtomicConstraints(firstOption.getAtomicConstraints())
                            .addDecisions(firstOption.getDecisions())
                            .removeDecisions(Collections.singletonList(decisionNode));
                    }
                },
                (node1, node2) ->
                    new TreeConstraintNode(
                        Stream
                            .concat(node1.getAtomicConstraints().stream(), node2.getAtomicConstraints().stream())
                            .collect(Collectors.toList()),
                        Stream
                            .concat(node1.getDecisions().stream(), node2.getDecisions().stream())
                            .collect(Collectors.toList())
                    ));
    }
}
