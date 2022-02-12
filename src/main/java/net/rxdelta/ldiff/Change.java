/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.rxdelta.ldiff;

/**
 * change data type (insert, delete, update)
 * @author Mostafa Nazari rxdelta@gmail.com
 */
enum ChangeType { update, insert, delete }

/**
 * change data (insert, delete, update)
 * @author Mostafa Nazari rxdelta@gmail.com
 */
public class Change<T> {
    int index;
    ChangeType type;
    T value;

    private Change(int index, ChangeType type, T value) {
        this.index = index;
        this.type = type;
        this.value = value;
    }
    
    /**
     * update change
     * @param <T> type of origin data
     * @param index index of origin data (0 means first item, ...)
     * @param value value to overwrite the older
     * @return change object
     */
    public static <T> Change update(int index, T value) {
        return new Change(index, ChangeType.update, value);
    }
    
    /**
     * insert change
     * @param <T> type of origin data
     * @param index index of origin data (0 means before the first)
     * @param value value to be inserted
     * @return change object
     */
    public static <T> Change insert(int index, T value) {
        return new Change(index, ChangeType.insert, value);
    }
    
    /**
     * delete change
     * @param <T> type of origin data
     * @param index index of origin data (0 means the first)
     * @return change object
     */
    public static <T> Change delete(int index) {
        return new Change(index, ChangeType.delete, null);
    }
    
}
