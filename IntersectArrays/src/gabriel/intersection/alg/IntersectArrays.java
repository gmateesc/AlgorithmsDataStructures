package gabriel.intersection.alg;

import java.util.Collection;
import java.util.Arrays;
import java.lang.reflect.Array;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;


/**
 *  Generic class to intersect two arrays
 *
 */
public class IntersectArrays<T extends Comparable<? super T> > {


    public static void main(String[] args) {
	
	int len = 0;


	//
	// Example: Intersect array of Integers
	//

	Integer[] arr1 = {1, 2, 6};
	Integer[] arr2 = {10, 2, 5, 1};


	IntersectArrays<Integer> ia = new IntersectArrays<Integer>(); 

	System.out.print("\n array 1: ");
	ia.printArr(arr1);

	System.out.print(" array 2: ");
	ia.printArr(arr2);


	// Find only the size of the intersection of the arrays
        int count = ia.intersectArrays_size(arr1, arr2);
	System.out.println(" intersection has size " + count );


	// Find the elements in the intersection of the arrays
        Set<Integer> result = ia.intersectArrays(arr1, arr2);
	System.out.print(" intersection: ");
	ia.printSet(result);


    } // main()



    /**
    * Generic code to intersect two arrays using a HashSet 
    */
    public Set<T> intersectArrays(T[] a, T[] b) {

	Set<T> aSet = new HashSet<T>();

	Set<T> result = new HashSet<T>();

	// Convert array "a" to a hash set
	for (T elem : a) {
	    aSet.add(elem);
	}

	// Iterate over "b", and pass the element into the result, if it is also in aSet
	for (T elem : b) {
	    if (aSet.contains(elem)) {
		result.add(elem);
	    }
	}
	return result;

    } // intersectArrays



    /**
    * Simplified version of intersectArrays(), which computes only 
    * the size of the intersection, not the intersection itself
    */
    public int intersectArrays_size(T[] a, T[] b) {

	Map<T,Integer> aMap = new HashMap<T,Integer>();

	int result = 0;

	// Convert array "a" to a hash map
	for (T elem : a) {
	    aMap.put(elem, 1);
	}

	// Iterate over "b", and count the element, if it is also in aMap
	// and has not been seen already
	for (T elem : b) {
	    if ( aMap.containsKey(elem) ) {

		if ( aMap.get(elem) == 1 ) {
		    result++;
		}

		Integer ct = aMap.get(elem);
		aMap.put(elem, ct+1);
	    }
	}

	return result;

    } // intersectArrays_size





    // 
    // Utilities
    //

    public void printArr(T[] arr) {

        int n = arr.length;

	if ( n > 0 ) {
	    System.out.print("[ ");
	}

	for (int i = 0; i < n - 1; i++ ) {
	    System.out.print(arr[i] + ", ");
	}

	System.out.println( arr[n-1] + " ]");

    } // printArr


    public void printSet(Set<T> set) {

	System.out.print("[ ");
	for (T item :set) {
	    System.out.print(item + ", ");
	}
	System.out.println("]");

    } // printSet


} // IntersectArrays


