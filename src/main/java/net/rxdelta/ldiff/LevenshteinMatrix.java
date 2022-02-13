/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rxdelta.ldiff;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * @param <T> type of origin
 * @param <U> type of new items (for insert/update)
 * @author Mostafa Nazari rxdelta@gmail.com
 */
class LevenshteinMatrix<T,U> {

    /**
     * equals function
     */
    BiPredicate<T,U> equals;
    
    /**
     * number of items skipped from both origin and new items, this is also indicates the first-equal number of items
     */
    int skipped;
    
    /**
     * the rest of items in new items
     */
    List<? extends U> values;
    
    /**
     * the rest of items in origin
     */
    List<T> origin;
    
    /**
     * [n(origin)+1] * [n(values)+1] cache memory (levenshteint matrix)
     * 
     */
    LinkedList<Change<U>>[][] cache;

    /**
     * create a levenshtein matrix
     * @param origin  origin data (head-equal items are skipped
     * @param first first item of origin data which is skipped
     * @param equals comparator between T and U
     * @param newValue list of new items (including skipped)
     * @param skipped number of skipped item, means they were heads of collection and equals in both origin and new item
     */
    public LevenshteinMatrix(Iterator<T> origin, T first, BiPredicate<T, U> equals, List<? extends U> newValue, int skipped) {
        this.equals = equals;
        this.skipped = skipped;
        this.values = newValue.subList(skipped, newValue.size());
        this.origin = new ArrayList<>();
        if (first != null) this.origin.add(first);
        while (origin.hasNext()) {
            this.origin.add(origin.next());
        }
        
        cache = new LinkedList[this.origin.size()+1][this.values.size()+1];
    }
    
    /**
     * get levenshtein distance(i,j).
     * <code>
     * f(0,0) = [] <br/>
     * f(k,0) = [ delete * k times ] <br/>
     * f(0,k) = [ insert * k times ] <br/>
     * f(i,j) = {  <br/>
     * &nbsp; f(i-1,j-1) if origin(i) == values(i) <br/>
     * &nbsp; else: one of these items with minumum length : // if some have minimum length, first one have upper priority <br/>
     * &nbsp; &nbsp; f(i-1,j-1).append(update(i,j)) <br/>
     * &nbsp; &nbsp; f(i-1,j).append(delete(i,j)) <br/>
     * &nbsp; &nbsp; f(i,j-1).append(insert(i,j)) <br/>
     * </code>
     * @param i range: [0 .. n(origin)] both inclusive
     * @param j range: [0 .. n(values)] both inclusive
     * @return chain of minimum changes required to make origin the same as new items. 
     */
    private LinkedList<Change<U>> getChangeChain(int i, int j) {
        LinkedList<Change<U>> result = cache[i][j];
        if (result == null) {
            
            if (i == 0) {
                
                result = new LinkedList<>();
                for (int k = 0; k < j; k++) {
                    result.add(Change.insert(skipped, values.get(k)));
                }
            } else if (j == 0) {
                
                result = new LinkedList<>();
                for (int k = 0; k < i; k++) {
                    result.add(Change.delete(skipped+k));
                }
            } else {
                if (equals.test(origin.get(i-1), values.get(j-1))) {
                    result = getChangeChain(i-1, j-1);
                } else {
                    LinkedList<Change<U>> left = getChangeChain(i, j-1);
                    LinkedList<Change<U>> top = getChangeChain(i-1, j);
                    LinkedList<Change<U>> corner = getChangeChain(i-1, j-1);
                    
                    if (corner.size() <= top.size() && corner.size() <= left.size()) {
                        result = new LinkedList<>(corner);
                        result.add(Change.update(skipped+i-1, values.get(j-1)));
                    } else if (left.size() <= top.size()) {
                        result = new LinkedList<>(left);
                        result.add(Change.insert(skipped+i, values.get(j-1)));
                    } else {
                        result = new LinkedList<>(top);
                        result.add(Change.delete(skipped+i-1));
                    }
                }
            }
            cache[i][j] = result;
        }
        return result;
    }

    /**
     * @return minimum list of changes
     * @see #getChangeChain(int, int) 
     */
    public LinkedList<Change<U>> getChangeChain() {
        return this.getChangeChain(origin.size(), values.size());
    }
    
}
