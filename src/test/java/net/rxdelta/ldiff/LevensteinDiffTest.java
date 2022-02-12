/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rxdelta.ldiff;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author mostafa
 */
public class LevensteinDiffTest {
    
    public LevensteinDiffTest() {
    }

    /**
     * Test of ldiffAndApply method, of class LevensteinDiff.
     */
    @Test
    public void testLdiffModifierList() {
        
        //case 0 - equals
        testList(
            List.of(1,2,3),
            List.of(1,2,3),
            0
        );
        
        //case 1 - tail insertion
        testList(
            List.of(1,2,3),
            List.of(1,2,3,4,5,6),
            3
        );
        
        //case 2 - tail removal
        testList(
            List.of(1,2,3,4,5,6),
            List.of(1,2,3),
            3
        );
        
        //case 3 - single update
        testList(
            List.of(1,2,3,4),
            List.of(1,3,3,4),
            1
        );
        
        //case 4 - rotation
        testList(
            List.of(1,2,3,4),
            List.of(2,3,4,5),
            2
        );
        
        testList(
            List.of(1,2,3,4),
            List.of(0,1,2,3),
            2
        );
        
        //case 5 - several remove
        testList(
            List.of(1,2,3,4,5,6),
            List.of(2,3,5),
            3
        );
        
        //case 6 - several insertion
        testList(
            List.of(2,3,5),
            List.of(1,2,3,4,5),
            2
        );
        
        //case 6 - mixed
        testList(
            List.of(1,2,3,4,5,6),
            List.of(1,1,2,3,5,7),
            3
        );
        
        //case 7 - head insertion
        testList(
            List.of(4,5,6),
            List.of(1,2,3,4,5,6),
            3
        );
        
        //case 8 - head removal
        testList(
            List.of(1,2,3,4,5,6),
            List.of(4,5,6),
            3
        );
    }
    
    @Test
    public void testLdiffModifierListOfList() {
        
        // case 0 - equals
        testListOfList(
                List.of(
                        List.of(1,2,3)
                )
                , List.of(
                        List.of(1,2,3)
                )
                , 0
        );
        
        // case 1 - last single insertion
        testListOfList(
                List.of(
                        List.of(1,2,3),
                        List.of(4,5,6)
                )
                , List.of(
                        List.of(1,2,3,10,11,12),
                        List.of(4,5,6)
                )
                , 3
        );
        
        // case 2 - mixed modification
        testListOfList(
                List.of(
                        List.of(1,2,3,4),
                        List.of(4,5,6),
                        List.of(7,8,9),
                        List.of(10,11,12)
                        
                )
                , List.of(
                        List.of(1,1,2,3,4),
                        List.of(4,6),
                        List.of(7,18,9),
                        List.of(10,11,12)
                )
                , 3
        );
        
        
    }

    private <T> void testList(List<T> origin, List<T> target, int diff) {
        ArrayList<T> o = new ArrayList<>(origin);
        
        int ndiff = LevenshteinDiff.ldiffAndApply(Modifiers.fromList(o), target);
        
        Assertions.assertEquals(diff, ndiff);
        Assertions.assertEquals(target, o);
        
    }

    private <T> void testListOfList(List<List<T>> origin, List<List<T>> target, int diff) {
        List<List<T>> o = origin.stream().map( l->new ArrayList<>(l)).collect(Collectors.toList());
        
        int ndiff = LevenshteinDiff.ldiffAndApply(Modifiers.fromListOfList(o), target.stream().flatMap(l->l.stream()).collect(Collectors.toList()));
        
        Assertions.assertEquals(diff, ndiff);
        Assertions.assertEquals(target, o);
        
    }

    
}
