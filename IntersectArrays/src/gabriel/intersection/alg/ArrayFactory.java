
package gabriel.intersection.alg;

import java.util.*;

import gabriel.intersection.alg.*;


/**
 * This is a factory of arrays populated with 
 * random data. For now, it produces arrays 
 * of int[]
 */
public class ArrayFactory {


    // Range of the the random numbers that 
    // populate the arrays created by the factory
    private int random_range = 1000;


    //
    // Costructors
    //
    public ArrayFactory (int n ) {

	if ( n > 0 ) {
	    random_range = n;
	}
    }

    public ArrayFactory () {
	
	; // Do not change randome_range
    }



    //
    // Create a factory instance
    //
    public static ArrayFactory getArrayFactory(int n ) {

	ArrayFactory af = new ArrayFactory(n);
	return af;

    }

    public static ArrayFactory getFactory( ) {

	ArrayFactory af = new ArrayFactory();
	return af;
    }



    //
    // Factory of array of integers
    //
    public Integer[] getArray(int len ) {

	Integer[] arr = new Integer[len];

	Random random_gen = new Random();

	for (int i = 0; i < len; i++ ) {
	    arr[i] = random_gen.nextInt(random_range);
	}

	return arr;

    } // getArray


} // ArrayFactory
