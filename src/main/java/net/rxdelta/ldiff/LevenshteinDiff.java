/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rxdelta.ldiff;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * 
 * @author Mostafa Nazari rxdelta@gmail.com
 */
public class LevenshteinDiff {
    
    /**
     * LevenshteinDiff function of the same type.
     * see {@link #ldiffAndApply(net.rxdelta.ldiff.Modifier, java.util.function.BiPredicate, java.util.function.Function, java.util.List) } for more info
     * @param <T> Type of item
     * @param origin data (you want modification to be applied to)
     * @param newValue List of item in form of {@link List}, because it is needed to randomly accessed
     * @return the number of modification applied on origin data
     * @see #ldiffAndApply(net.rxdelta.ldiff.Modifier, java.util.function.BiPredicate, java.util.function.Function, java.util.List) 
     */
    public static <T> int ldiffAndApply(Modifier<T> origin, List<? extends T> newValue) {
        return LevenshteinDiff.ldiffAndApply(origin, (t, u) -> t.equals(u), t -> t, newValue);
    }
    
    /**
     * Most Generic Levenshtein Diff function implementation in this library. 
     * it would calculate and apply minimum changes of two sequence of data, according to levenshtein algorithm, and also apply changes on origin data.<br/>
     * the algorithm is fast-forward if head and/or tail of items are equals, to improve performance  <br/>
     * each insertion, deletion or update assumed 1 change and
     * if there are equal situation between these, first priority is update, then delete, then insert ( see {@link LevenshteinMatrix#getChangeChain(int, int) } ).
     * to manage modifications, you can use {@link Modifiers#fromList(java.util.List) } for simple List processing, or you can implement your own {@link Modifier}
     * @param <T> Type of origin data
     * @param <U> Type of new data
     * @param origin an special Iterator on origin data, which also manages the changes
     * @param equals check if origin item and new value item are equals
     * @param generator create an object in form of origin data(T) from new data(U), this function calls on each insert/update
     * @param newValue a random-access flat collection of new values, if you have your input data in any other form, just flat them and convert to a read-only random accessed list
     * @return the number of modification applied on origin data
     * @see Modifiers#fromList(java.util.List) 
     * @see LevenshteinMatrix#getChangeChain(int, int) 
     */
    public static <T,U> int ldiffAndApply(Modifier<T> origin, BiPredicate<T,U> equals, Function<U,T> generator,  List<? extends U> newValue) {
        Collection<Change<T>> changes = ldiff(origin, equals, generator, newValue, 0);
        applyChanges(origin, changes);
        return changes.size();
    }

    /**
     * apply chain of changes on origin modifier
     * @param <T> type of input
     * @param origin origin modifier
     * @param changes chain of changes
     * @throws IllegalStateException
     */
    public static <T> void applyChanges(Modifier<T> origin, Collection<Change<T>> changes) throws IllegalStateException {
        if (!changes.isEmpty()) {
            origin.reset();
            int index = 0;
            for (Change<T> next : changes) {
                if (index < next.index) {
                    origin.skip(next.index - index);
                    index = next.index;
                }
                switch (next.type) {
                    case update:
                        origin.next();
                        origin.set(next.value);
                        index++;
                        break;
                    case insert:
                        origin.insert(next.value);
                        break;
                    case delete:
                        origin.next();
                        origin.remove();
                        index++;
                        break;
                    default:
                        throw new IllegalStateException("invalid state "+next.type);
                }
            }
        }
    }
    
    /**
     * Most Generic Levenshtein Diff function implementation in this library. 
     * it would calculate minimum changes of two sequence of data, according to levenshtein algorithm, and also apply changes on origin data. <br/>
     * the algorithm is fast-forward if head and/or tail of items are equals, to improve performance  <br/>
     * each insertion, deletion or update assumed 1 change and
     * if there are equal situation between these, first priority is update, then delete, then insert ( see {@link LevenshteinMatrix#getChangeChain(int, int) } ).
     * to manage modifications, you can use {@link Modifiers#fromList(java.util.List) } for simple List processing, or you can implement your own {@link Modifier}
     * @param <T> Type of origin data
     * @param <U> Type of new data
     * @param origin an special Iterator on origin data, which also manages the changes
     * @param equals check if origin item and new value item are equals
     * @param generator create an object in form of origin data(T) from new data(U), this function calls on each insert/update
     * @param newValue a random-access flat collection of new values, if you have your input data in any other form, just flat them and convert to a read-only random accessed list
     * @return the list of modification
     * @see Modifiers#fromList(java.util.List) 
     * @see LevenshteinMatrix#getChangeChain(int, int) 
     */
    public static <T,U> Collection<Change<T>> ldiff(Modifier<T> origin, BiPredicate<T,U> equals, Function<U,T> generator, List<? extends U> newValue) {
        return ldiff(origin, equals, generator, newValue, 0);
    }
    
    /**
     * @see #ldiff(net.rxdelta.ldiff.Modifier, java.util.function.BiPredicate, java.util.function.Function, java.util.List) 
     */
    private static <T,U> Collection<Change<T>> ldiff(Modifier<T> origin, BiPredicate<T,U> equals, Function<U,T> generator, List<? extends U> newValue, int skipped) {
        if (!origin.hasNext()) {
            return insertAll(newValue, generator, skipped);
        }
        
        if (skipped == newValue.size()) {
            return deleteAll(origin, skipped);
        }
        
        T ov = origin.next();
        U nv = newValue.get(skipped);
        
        if (equals.test(ov,nv)) {
            return ldiff(origin, equals, generator, newValue, skipped+1);
        } else {
            LevenshteinMatrix<T,U> m = new LevenshteinMatrix<>(origin, ov, equals, generator, newValue, skipped);
            return m.getChangeChain();
        }
    }

    private static <T,U> Collection<Change<T>> insertAll(List<? extends U> newValue, Function<U,T> generator, int skipped) {
        Collection<Change<T>> result = new LinkedList<>();
        for (int i = skipped; i < newValue.size(); i++) {
            result.add(Change.insert(skipped, generator.apply(newValue.get(i))));
        }
        return result;
    }

    private static <T> Collection<Change<T>> deleteAll(Modifier<? extends T> origin, int skipped) {
        Collection<Change<T>> result = new LinkedList<>();
        while (origin.hasNext()) {
            origin.next();
            result.add(Change.delete(skipped));
            skipped++;
        }
        return result;
    }
    
}
