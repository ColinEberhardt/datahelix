package com.scottlogic.deg.generator.restrictions;

import com.scottlogic.deg.generator.constraints.atomic.IsOfTypeConstraint;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import javax.print.DocFlavor;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class DataTypeRestrictionsTests {

    @Test
    void equals_whenOtherObjectIsNull_returnsFalse() {
        Collection<IsOfTypeConstraint.Types> type = new HashSet();

        type.add(IsOfTypeConstraint.Types.STRING);

        DataTypeRestrictions restriction = new DataTypeRestrictions(type);

        boolean result = restriction.equals(null);

        Assert.assertFalse(result);
    }

    @Test
    void equals_whenObjectsAreEqual_returnsTrue() {
        Collection<IsOfTypeConstraint.Types> type1 = new HashSet<>();
        Collection<IsOfTypeConstraint.Types> type2 = new HashSet<>();

        type1.add(IsOfTypeConstraint.Types.STRING);
        type2.add(IsOfTypeConstraint.Types.STRING);

        DataTypeRestrictions restriction1 = new DataTypeRestrictions(type1);
        DataTypeRestrictions restriction2 = new DataTypeRestrictions(type2);

        boolean result = restriction1.equals(restriction2);

        Assert.assertTrue(result);
    }

    @Test
    void equals_whenObjectsAreGivenTheSameTypeValue_returnsTrue() {
        Collection<IsOfTypeConstraint.Types> type1 = new HashSet<>();
        Collection<IsOfTypeConstraint.Types> type2 = new HashSet<>();

        type1.add(IsOfTypeConstraint.Types.valueOf("TEMPORAL"));
        type2.add(IsOfTypeConstraint.Types.valueOf("TEMPORAL"));

        DataTypeRestrictions restriction1 = new DataTypeRestrictions(type1);
        DataTypeRestrictions restriction2 = new DataTypeRestrictions(type2);

        boolean result = restriction1.equals(restriction2);

        Assert.assertTrue(result);
    }

    @Test
    void equals_whenObjectsAreEqualAndOneIsGivenTypeAsAString_returnsTrue() {
        Collection<IsOfTypeConstraint.Types> type1 = new HashSet<>();
        Collection<IsOfTypeConstraint.Types> type2 = new HashSet<>();

        type1.add(IsOfTypeConstraint.Types.NUMERIC);
        type2.add(IsOfTypeConstraint.Types.valueOf("NUMERIC"));

        DataTypeRestrictions restriction1 = new DataTypeRestrictions(type1);
        DataTypeRestrictions restriction2 = new DataTypeRestrictions(type2);

        boolean result = restriction1.equals(restriction2);

        Assert.assertTrue(result);
    }

    @Test
    void equals_whenObjectsAreEqualAndAreGivenSameTypesInDifferentOrder_returnsTrue() {
        Collection<IsOfTypeConstraint.Types> type1 = new HashSet<>();
        Collection<IsOfTypeConstraint.Types> type2 = new HashSet<>();

        type1.add(IsOfTypeConstraint.Types.NUMERIC);
        type1.add(IsOfTypeConstraint.Types.STRING);
        type2.add(IsOfTypeConstraint.Types.STRING);
        type2.add(IsOfTypeConstraint.Types.NUMERIC);

        DataTypeRestrictions restriction1 = new DataTypeRestrictions(type1);
        DataTypeRestrictions restriction2 = new DataTypeRestrictions(type2);

        boolean result = restriction1.equals(restriction2);

        Assert.assertTrue(result);
    }

    @Test
    void equals_whenObjectsAreEqualAndAreGivenSameTypesInSameOrder_returnsTrue() {
        Collection<IsOfTypeConstraint.Types> type1 = new HashSet<>();
        Collection<IsOfTypeConstraint.Types> type2 = new HashSet<>();

        type1.add(IsOfTypeConstraint.Types.TEMPORAL);
        type1.add(IsOfTypeConstraint.Types.NUMERIC);
        type2.add(IsOfTypeConstraint.Types.TEMPORAL);
        type2.add(IsOfTypeConstraint.Types.NUMERIC);

        DataTypeRestrictions restriction1 = new DataTypeRestrictions(type1);
        DataTypeRestrictions restriction2 = new DataTypeRestrictions(type2);

        boolean result = restriction1.equals(restriction2);

        Assert.assertTrue(result);
    }

    @Test
    void equals_whenObjectsAreNotEqual_returnsFalse() {
        Collection<IsOfTypeConstraint.Types> type1 = new HashSet<>();
        Collection<IsOfTypeConstraint.Types> type2 = new HashSet<>();

        type1.add(IsOfTypeConstraint.Types.NUMERIC);
        type2.add(IsOfTypeConstraint.Types.STRING);

        DataTypeRestrictions restriction1 = new DataTypeRestrictions(type1);
        DataTypeRestrictions restriction2 = new DataTypeRestrictions(type2);

        boolean result = restriction1.equals(restriction2);

        Assert.assertFalse(result);
    }

    @Test
    void equals_whenObjectsAreNotEqualAndAreGivenTypesAsStrings_returnsFalse() {
        Collection<IsOfTypeConstraint.Types> type1 = new HashSet<>();
        Collection<IsOfTypeConstraint.Types> type2 = new HashSet<>();

        type1.add(IsOfTypeConstraint.Types.valueOf("TEMPORAL"));
        type2.add(IsOfTypeConstraint.Types.valueOf("NUMERIC"));

        DataTypeRestrictions restriction1 = new DataTypeRestrictions(type1);
        DataTypeRestrictions restriction2 = new DataTypeRestrictions(type2);

        boolean result = restriction1.equals(restriction2);

        Assert.assertFalse(result);
    }

    @Test
    void equals_whenObjectsAreNotEqualAndOneIsGivenTypeAsString_returnsFalse() {
        Collection<IsOfTypeConstraint.Types> type1 = new HashSet<>();
        Collection<IsOfTypeConstraint.Types> type2 = new HashSet<>();

        type1.add(IsOfTypeConstraint.Types.valueOf("TEMPORAL"));
        type2.add(IsOfTypeConstraint.Types.STRING);

        DataTypeRestrictions restriction1 = new DataTypeRestrictions(type1);
        DataTypeRestrictions restriction2 = new DataTypeRestrictions(type2);

        boolean result = restriction1.equals(restriction2);

        Assert.assertFalse(result);
    }

    @Test
    void hashCode_whenObjectsAreEqual_returnsTrue() {
        Collection<IsOfTypeConstraint.Types> type1 = new HashSet<>();
        Collection<IsOfTypeConstraint.Types> type2 = new HashSet<>();

        type1.add(IsOfTypeConstraint.Types.STRING);
        type2.add(IsOfTypeConstraint.Types.STRING);

        DataTypeRestrictions restriction1 = new DataTypeRestrictions(type1);
        DataTypeRestrictions restriction2 = new DataTypeRestrictions(type2);

        int hashCode1 = restriction1.hashCode();
        int hashCode2 = restriction2.hashCode();

        Assert.assertEquals(hashCode1, hashCode2);
    }

    @Test
    void hashCode_whenObjectsAreGivenTheSameTypeValue_returnsTrue() {
        Collection<IsOfTypeConstraint.Types> type1 = new HashSet<>();
        Collection<IsOfTypeConstraint.Types> type2 = new HashSet<>();

        type1.add(IsOfTypeConstraint.Types.valueOf("TEMPORAL"));
        type2.add(IsOfTypeConstraint.Types.valueOf("TEMPORAL"));

        DataTypeRestrictions restriction1 = new DataTypeRestrictions(type1);
        DataTypeRestrictions restriction2 = new DataTypeRestrictions(type2);

        int hashCode1 = restriction1.hashCode();
        int hashCode2 = restriction2.hashCode();

        Assert.assertEquals(hashCode1, hashCode2);
    }

    @Test
    void hashCode_whenObjectsAreEqualAndOneIsGivenTypeAsAString_returnsTrue() {
        Collection<IsOfTypeConstraint.Types> type1 = new HashSet<>();
        Collection<IsOfTypeConstraint.Types> type2 = new HashSet<>();

        type1.add(IsOfTypeConstraint.Types.NUMERIC);
        type2.add(IsOfTypeConstraint.Types.valueOf("NUMERIC"));

        DataTypeRestrictions restriction1 = new DataTypeRestrictions(type1);
        DataTypeRestrictions restriction2 = new DataTypeRestrictions(type2);

        int hashCode1 = restriction1.hashCode();
        int hashCode2 = restriction2.hashCode();

        Assert.assertEquals(hashCode1, hashCode2);
    }

    @Test
    void hashCode_whenObjectsAreEqualAndAreGivenSameTypesInDifferentOrder_returnsTrue() {
        Collection<IsOfTypeConstraint.Types> type1 = new HashSet<>();
        Collection<IsOfTypeConstraint.Types> type2 = new HashSet<>();

        type1.add(IsOfTypeConstraint.Types.NUMERIC);
        type1.add(IsOfTypeConstraint.Types.STRING);
        type2.add(IsOfTypeConstraint.Types.STRING);
        type2.add(IsOfTypeConstraint.Types.NUMERIC);

        DataTypeRestrictions restriction1 = new DataTypeRestrictions(type1);
        DataTypeRestrictions restriction2 = new DataTypeRestrictions(type2);

        int hashCode1 = restriction1.hashCode();
        int hashCode2 = restriction2.hashCode();

        Assert.assertEquals(hashCode1, hashCode2);
    }

    @Test
    void hashCode_whenObjectsAreEqualAndAreGivenSameTypesInSameOrder_returnsTrue() {
        Collection<IsOfTypeConstraint.Types> type1 = new HashSet<>();
        Collection<IsOfTypeConstraint.Types> type2 = new HashSet<>();

        type1.add(IsOfTypeConstraint.Types.TEMPORAL);
        type1.add(IsOfTypeConstraint.Types.NUMERIC);
        type2.add(IsOfTypeConstraint.Types.TEMPORAL);
        type2.add(IsOfTypeConstraint.Types.NUMERIC);

        DataTypeRestrictions restriction1 = new DataTypeRestrictions(type1);
        DataTypeRestrictions restriction2 = new DataTypeRestrictions(type2);

        int hashCode1 = restriction1.hashCode();
        int hashCode2 = restriction2.hashCode();

        Assert.assertEquals(hashCode1, hashCode2);
    }

    @Test
    void hashCode_whenObjectsAreNotEqual_returnsFalse() {
        Collection<IsOfTypeConstraint.Types> type1 = new HashSet<>();
        Collection<IsOfTypeConstraint.Types> type2 = new HashSet<>();

        type1.add(IsOfTypeConstraint.Types.NUMERIC);
        type2.add(IsOfTypeConstraint.Types.STRING);

        DataTypeRestrictions restriction1 = new DataTypeRestrictions(type1);
        DataTypeRestrictions restriction2 = new DataTypeRestrictions(type2);

        int hashCode1 = restriction1.hashCode();
        int hashCode2 = restriction2.hashCode();

        Assert.assertNotEquals(hashCode1, hashCode2);
    }

    @Test
    void hashCode_whenObjectsAreNotEqualAndAreGivenTypesAsStrings_returnsFalse() {
        Collection<IsOfTypeConstraint.Types> type1 = new HashSet<>();
        Collection<IsOfTypeConstraint.Types> type2 = new HashSet<>();

        type1.add(IsOfTypeConstraint.Types.valueOf("TEMPORAL"));
        type2.add(IsOfTypeConstraint.Types.valueOf("NUMERIC"));

        DataTypeRestrictions restriction1 = new DataTypeRestrictions(type1);
        DataTypeRestrictions restriction2 = new DataTypeRestrictions(type2);

        int hashCode1 = restriction1.hashCode();
        int hashCode2 = restriction2.hashCode();

        Assert.assertNotEquals(hashCode1, hashCode2);
    }

    @Test
    void hashCode_whenObjectsAreNotEqualAndOneIsGivenTypeAsString_returnsFalse() {
        Collection<IsOfTypeConstraint.Types> type1 = new HashSet<>();
        Collection<IsOfTypeConstraint.Types> type2 = new HashSet<>();

        type1.add(IsOfTypeConstraint.Types.valueOf("TEMPORAL"));
        type2.add(IsOfTypeConstraint.Types.STRING);

        DataTypeRestrictions restriction1 = new DataTypeRestrictions(type1);
        DataTypeRestrictions restriction2 = new DataTypeRestrictions(type2);

        int hashCode1 = restriction1.hashCode();
        int hashCode2 = restriction2.hashCode();

        Assert.assertNotEquals(hashCode1, hashCode2);
    }

}
