/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rxdelta.ldiff;

import java.util.List;
import java.util.ListIterator;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Implementation of some {@link Modifier}
 * 
 * @author Mostafa Nazari rxdelta@gmail.com
 */
public class Modifiers {
    
    /**
     * a Modifier on non-Immutable lists
     * @param <T> type of items
     * @param origin Modifiable List, the result would be affected on this list
     * @return Modifier linked to <code>origin</code>
     */
    public static <T> Modifier<T,T> fromList(List<T> origin) {
        return fromListIterator(() -> origin.listIterator());
    }
    
    /**
     * a Modifier on list iterator
     * @param <T> type of items
     * @param generator a method which create the iterator, during the algorithm it needs to re-generate the iterator when it wants to apply the changes
     * @return Modifier linked to <code>origin</code>
     */
    public static <T> Modifier<T,T> fromListIterator(Supplier<ListIterator<T>> generator) {
        
        return new Modifier<T,T>() {
            private ListIterator<T> l = generator.get();
            
            @Override
            public void reset() {
                l = generator.get();
            }

            @Override
            public void set(T t) {
                l.set(t);
            }

            @Override
            public void insert(T t) {
                l.add(t);
            }

            @Override
            public void remove() {
                l.remove();
            }

            @Override
            public boolean hasNext() {
                return l.hasNext();
            }

            @Override
            public T next() {
                return l.next();
            }
        };
    }    
    
    
    /**
     * a Modifier of List of Lists. this would update flat all the list and compare it to new value.
     * note that last insertion of previous list is preferred to first insertion of next list, when the insertion is happened between
     * @param <T> type of origin data
     * @param origin collection of input data
     * @return Modifier linked to <code>origin</code>
     */
    public static <T> Modifier<T,T> fromListOfList(List<List<T>> origin) {
        return fromListOfListIterator( () -> origin.stream().map(i -> i.listIterator()).collect(Collectors.toList()) );
    }
    /**
     * @see #fromListOfList(java.util.List) 
     * @param <T> type of items
     * @param generator a method which create the iterator, during the algorithm it needs to re-generate the iterator when it wants to apply the changes
     * @return Modifier linked to <code>origin</code>
     */
    public static <T> Modifier<T,T> fromListOfListIterator(Supplier<List<ListIterator<T>>> generator) {
        return new Modifier<T,T>() {
            
            List<ListIterator<T>> items = generator.get();
            int index = 0;
            
            @Override
            public void reset() {
                index = 0;
                items = generator.get();
            }

            @Override
            public void set(T t) {
                items.get(index).set(t);
            }

            @Override
            public void insert(T t) {
                items.get(index).add(t);
            }

            @Override
            public void remove() {
                items.get(index).remove();
            }
            

            @Override
            public boolean hasNext() {
                while (index < items.size()) {
                    if (items.get(index).hasNext()) return true;
                    index++;
                }
                return false;
            }

            @Override
            public T next() {
                if (hasNext()) return items.get(index).next();
                throw new IllegalStateException("next called of empty collection");
            }
        };
    }
    
    
}
