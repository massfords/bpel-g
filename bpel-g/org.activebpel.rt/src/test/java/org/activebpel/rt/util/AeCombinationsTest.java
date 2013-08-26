package org.activebpel.rt.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class AeCombinationsTest {

    @Test
    public void threeStrings() throws Exception {
        String[][] expected = {
                {"a", "b", "c"},
                {"a", "b"},
                {"a", "c"},
                {"b", "c"},
                {"a"},
                {"b"},
                {"c"}
        };
        Iterator<List<String>> it = AeCombinations.createAllCombinations(Arrays.asList(expected[0]));
        int i = 0;
        while (it.hasNext()) {
            List<String> combo = it.next();
            assertEquals(Arrays.asList(expected[i++]), combo);
        }
    }
}
