/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rxdelta.ldiff;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Mostafa Nazari rxdelta@gmail.com
 */
public class Sample {
    
    public static void main(String[] args) {
        sample1();
        System.out.println("-------------------------");
        sample2();
    }

    private static void sample1() {
        
        //simple sample
        
        ArrayList<Integer> origin = new ArrayList<>(List.of(1,2,3,4,5,6,7,8));
        List<Integer> target = List.of(1,2,13,4,40,5,7,8);
        System.out.println("old version: "+origin);
        
        int diff = LevenshteinDiff.ldiffAndApply(Modifiers.fromList(origin), target );
        
        System.out.println("new version: "+origin);
        System.out.println("number of changes: "+diff); // 3
    }

    private static void sample2() {
        
        //deep inspect example
        
        ArrayList<Optional<Integer>> origin = 
                new ArrayList<>(List.of(1,2,3,4,5,6,7,8,9)
                .stream()
                .map(o->Optional.of(o))
                .collect(Collectors.toList())
        ); //convert to list of optionals
        
        List<Integer> target = List.of(1,2,13,4,40,5,6,7,8);
        
        System.out.println("old version: "+origin);
        
        int diff = LevenshteinDiff.ldiffAndApply(new Modifier<Optional<Integer>, Integer>() {
            
            ListIterator<Optional<Integer>> it = origin.listIterator();
            Optional<Integer> oldv;
            
            @Override
            public void reset() {
                System.out.println("# reset items");
                it = origin.listIterator();
            }

            @Override
            public void set(Integer t) {
                System.out.println("# update item: "+oldv+" -> "+t);
                it.set(Optional.of(t));
            }

            @Override
            public void insert(Integer t) {
                System.out.println("# insert item: "+t+" (after "+oldv);
                it.add(Optional.of(t));
            }

            @Override
            public void remove() {
                System.out.println("# removed item: "+oldv);
                it.remove();
            }

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public Optional<Integer> next() {
                oldv = it.next();
                return oldv;
            }
        }, (t,u) -> t.get().equals(u), target);
        
        System.out.println("new version: "+origin);
        System.out.println("number of changes: "+diff);
    }
}
