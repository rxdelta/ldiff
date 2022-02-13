/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rxdelta.ldiff;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * methods need to modify origin data.
 * modifiers is work like {@link Iterator}, means they traverse input data by next.
 * whenever it needs to modify the source, either one of {@link #set(java.lang.Object)}, {@link #insert(java.lang.Object)} or {@link #remove() } is called
 * see {@link ListIterator} for more info about update condition
 * @author Mostafa Nazari rxdelta@gmail.com
 * @param <T> type of origin data
 * @param <U> type of new data (for insert/update)
 */
public interface Modifier<T,U> extends Iterator<T> {
    
    /**
     * reset the Iterator.
     * after first traverse (for reading) and before modification start, this method would be executed
     */
    public void reset();
    
    /**
     * bypass <code>skip</code> number of items, this function is equivalent to call {@link #next() } for <code>skip</code> times.
     * @param skip number of items need to be skipped
     */
    default void skip(int skip) {
        for (int i = 0; i < skip; i++) {
            next();
        }
    }
    
    /**
     * update the value of item which is recently calculated by {@link #next()}.
     * @param t new value, you should convert it <code>T</code> if it is required
     * @see ListIterator#set(java.lang.Object) 
     * @see LevenshteinDiff#ldiff(net.rxdelta.ldiff.Modifier, java.util.function.BiPredicate, java.util.function.Function, java.util.List) for generator
     * 
     */
    public void set(U t);
    
    /**
     * insert new item right after the item which is recently calculated by {@link #next()}.
     * @param t new value, you should convert it <code>T</code> and then insert it
     * @see ListIterator#add(java.lang.Object) for more info about the place where item would be added
     * @see LevenshteinDiff#ldiff(net.rxdelta.ldiff.Modifier, java.util.function.BiPredicate, java.util.function.Function, java.util.List) for generator
     * 
     */
    public void insert(U t);

    /**
     * remove the item which is recently calculated by {@link #next()} from collection.
     * @see ListIterator#remove()
     * 
     */
    @Override
    public void remove();

}
