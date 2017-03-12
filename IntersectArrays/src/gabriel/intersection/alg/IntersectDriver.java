package gabriel.intersection.alg;

import java.util.*;

import gabriel.intersection.alg.*;

public class IntersectDriver {

    private int random_range = 1000;


    //
    // Constructors
    //

    public IntersectDriver (int n) {
	if ( n > 0 ) {
	    random_range = n;
	}
    }


    public IntersectDriver( ) {

	; // Do not change randome_range
    }




    /** 
     * Build the two arrays to intersect, filling them with random integers, 
     * the intersect the arrays
     */
    public float buildAndIntesect(int[] sizes, boolean array_A) throws OutOfMemoryError {

	float time = 0.0F;

	//
	// 1. Check memory
	//
	checkMemory(sizes, array_A);

	try {

	    //
	    // 2. Allocate and initialize the arrays to intersect
	    //
	    ArrayFactory factory = ArrayFactory.getArrayFactory(random_range);
	    Integer[] arr_a = factory.getArray(sizes[0]);
	    Integer[] arr_b = factory.getArray(sizes[1]);
	    
	
	    //
	    // 3. Find the intersection
	    // 
	    
	    // 3.1 Set start time
	    long start_time = System.currentTimeMillis();
	    
	    // 3.2 Perform intersection
	    int n_res = doIntersection(arr_a, arr_b, array_A);
	    sizes[2] = n_res;
	    //System.out.println("# doIntersect() returned " + n_res);
	    

	    // 3.3 Set end time
	    long end_time = System.currentTimeMillis();

	    // 3.4 Find time taken by doIntersect
	    time = (end_time - start_time) / 1000F;
	}

	catch ( OutOfMemoryError err) {
	    throw new OutOfMemoryError("Not enough memory for arrays A and B and HashSet");
	}
	    
	return time;


    } // buildAndIntesect()



    /**
     * Intersect two arrays, when the arrays are already filled in
     */
    public int doIntersection (Integer[] arr_a, Integer[] arr_b, boolean array_a_hash) {

	IntersectArrays<Integer> intersect = new IntersectArrays<Integer>();

        //Set<Integer> s;
        int size;

	// To find the elements of the intersection, invoke intersectArrays()
	if ( array_a_hash) {
	    //s = intersect.intersectArrays(arr_a, arr_b);
	    size = intersect.intersectArrays_size(arr_a, arr_b);
	}
	else {
	    //s = intersect.intersectArrays(arr_b, arr_a);
	    size = intersect.intersectArrays_size(arr_b, arr_a);
	}

	//return s.size();
	return size;

    }  // doIntersection



    /**
     * Check enough memory: before actually allocating the 
     * two arrays and the HashSet, check ifthere is enough 
     * space and throw OutOfMemoryError id there is not.
     */
    private void checkMemory(int[] sizes, boolean array_A) throws OutOfMemoryError {

	long free = Runtime.getRuntime().freeMemory();

	//System.out.println("\nsize_a =  " + sizes[0]);
	//System.out.println("size_b =  " + sizes[1]);

	//
	// Compute needed mem
	//
	//  one integer takes 32 bits, i.e., 4 Bytes
	//  there is an overhead of 16 bytes per array (e.g., for array length)
	//
	long mem_a = 16L + sizes[0] * 4L; 
	long mem_b = 16L + sizes[1] * 4L;

	long mem_hash_a = (long) (1.2 * mem_a);
        long mem_hash_b = (long) (1.2 * mem_b);

	long mem_hash_min = Math.min(mem_hash_a, mem_hash_b);

	long mem_hash = mem_hash_b;
	if (array_A) {
	    mem_hash = mem_hash_a;
	}

        long mem_total  = mem_a + mem_b + mem_hash;
	//System.out.println("\nArrays + HashSet = " + mem_total);

	if ( mem_total > free ) {

	    if ( mem_a + mem_b + mem_hash_min < free ) {
		throw new OutOfMemoryError
		    (
		       "Not enough memory for the hashSet; \n" + 
		       "Consider using HashSet for array " + ((array_A)? "B":"A") 
		     );
	    }
	    else {
		throw new OutOfMemoryError("Not enough memory for arrays A and B and HashSet");
	    }
	}


    } // checkMemory()


} // IntersectDriver

