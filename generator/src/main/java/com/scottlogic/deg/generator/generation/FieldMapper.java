package com.scottlogic.deg.generator.generation;

import com.scottlogic.deg.generator.Field;
import com.scottlogic.deg.generator.decisiontree.ConstraintNode;
import com.scottlogic.deg.generator.decisiontree.DecisionTreeProfile;
import com.scottlogic.deg.generator.reducer.ConstraintFieldSniffer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Given a rule or list of rules it finds which rules act on which fields and returns a map of rules to fields
 */
public class FieldMapper {

    private class ObjectFields {
        public Object object;
        public Stream<Field> fields;

        public ObjectFields(Object object, Stream<Field> fields) {
            this.object = object;
            this.fields = fields;
        }
    }

    private final ConstraintFieldSniffer constraintSniffer = new ConstraintFieldSniffer();

    private Stream<ObjectFields> mapConstraintToFields(ConstraintNode node) {
        return Stream.concat(
            node.getAtomicConstraints()
                .stream()
                .map(constraint -> new ObjectFields(constraint, Stream.of(constraintSniffer.detectField(constraint)))),
            node.getDecisions()
                .stream()
                .flatMap(decision -> decision.getOptions()
                    .stream()
                    .flatMap(this::mapConstraintToFields)
                    .flatMap(map -> Stream.of(
                        map, // this part is technically not used, but no reason not to keep it
                        new ObjectFields(decision, map.fields)
                    ))
        ));
    }

//    private Stream<Field> mapConstraintToFields(ConstraintNode node) {
//        return Stream.concat(
//            node.getAtomicConstraints()
//                .stream()
//                .map(constraintSniffer::detectField),
//            node.getDecisions()
//                .stream()
//                .flatMap(decision -> decision.getOptions()
//                    .stream()
//                    .flatMap(this::mapConstraintToFields)));
//    }

    public Map<Object, List<Field>> mapRulesToFields(DecisionTreeProfile profile){
        return mapConstraintToFields(profile.getRootNode())
            .collect(Collectors.toMap(
                map -> map.object,
                map -> map.fields.collect(Collectors.toList())
            ));
    }
}
