/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rxdelta.ldiff;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * change data type (insert, delete, update)
 * @author Mostafa Nazari rxdelta@gmail.com
 */
enum ChangeType { update, insert, delete }

/**
 * change data (insert, delete, update)
 * @author Mostafa Nazari rxdelta@gmail.com
 * @param <U> type of new data (for insert, update)
 */
public class Change<U> {
    int index;
    ChangeType type;
    U value;

    private Change(int index, ChangeType type, U value) {
        this.index = index;
        this.type = type;
        this.value = value;
    }
    
    /**
     * update change
     * @param <U> type of new item
     * @param index index of origin data (0 means first item, ...)
     * @param value value to overwrite the older
     * @return change object
     */
    public static <U> Change update(int index, U value) {
        return new Change(index, ChangeType.update, value);
    }
    
    /**
     * insert change
     * @param <U> type of new item
     * @param index index of origin data (0 means before the first)
     * @param value value to be inserted
     * @return change object
     */
    public static <U> Change insert(int index, U value) {
        return new Change(index, ChangeType.insert, value);
    }
    
    /**
     * delete change
     * @param index index of origin data (0 means the first)
     * @return change object
     */
    public static Change delete(int index) {
        return new Change(index, ChangeType.delete, null);
    }

    /**
     * chain of insert.
     * @param <U>
     * @param newValue values to be inserted from <code>skipped</code> item
     * @param skipped start insert from this number
     * @return <code>(newValue.size() - skipped) number of insertion of tail of newValue
     */
    public static <U> Collection<Change<U>> insertAll(List<? extends U> newValue, int skipped) {
        Collection<Change<U>> result = new LinkedList<>();
        for (int i = skipped; i < newValue.size(); i++) {
            result.add(Change.insert(skipped, newValue.get(i)));
        }
        return result;
    }

    /**
     * chain of removal
     * @param <T> type of origin data
     * @param <U> type of new item
     * @param origin origin data
     * @param skipped number of skipped item(s)
     * @return (origin.size() - skipped) number of delete
     */
    public static <T,U> Collection<Change<U>> deleteAll(Modifier<? extends T, ? extends U> origin, int skipped) {
        Collection<Change<U>> result = new LinkedList<>();
        while (origin.hasNext()) {
            origin.next();
            result.add(Change.delete(skipped));
            skipped++;
        }
        return result;
    }
    
    
}
