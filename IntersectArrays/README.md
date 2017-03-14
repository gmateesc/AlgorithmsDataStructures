
# Application to intersect two arrays


## Table of Contents


- [The problem solved](#p0)


- [Description of the code](#p1)
  - [The alg package](#p11)
    - [IntersectArrays class](#p111)
    - [IntersectDriver class](#p112)
  - [The ui package](#p12)
    - [IntersectUI class](#p121)
    - [IntersectUtil class](#p122)


- [Building and Running the application](#p2)
  - [Building the application](#p21)
  - [Running the application](#p22)


- [The impact of choosing which array to put in Hash](#p3)
  - [Experiment](#p31)
  - [Explanation](#p32)




<a name="p0" id="p0"></a>
## The problem solved 


The problem solved is to intersect two arrays of integers, 
containing random integers in a specified range. 

The intersection is computed in linear time, by putting one 
of the arrays in a HashSet ot HashMap and then iterating through 
the other array and checking if its elements are in the Hash.

The application has a user interface, where the user specifies the 
size of each array and which array will be copied to a Hash. Then 
the user triggers the computation of the intersection in the UI, 
and when the computation is completed. the UI shows the number 
of elements in the array intersection and the time it took to 
compute it.



<a name="p1" id="p1"></a>
## Description of the code


The source code is under the src directory and has this layout
```
gabriel $ tree src
src
├── Makefile
├── manifest.txt
└── gabriel
    └── intersection
        ├── alg
        │   ├── ArrayFactory.java
        │   ├── IntersectArrays.java
        │   └── IntersectDriver.java
        └── ui
            ├── IntersectUI.java
            └── IntersectUtil.java

```


The algorithm to do the intersection of two arrays is implemented by the class **IntersectArrays**. 

The class IntersectDriver implements the workflow: creating the two integers 
arrays, invoking the intersectArrays method in IntersectArrays, and returning 
the result to the caller.

The ArrayFactory class is a helper class for IntersectDriver, taking care of 
creating the arrays.



Let's start with the alg package and its classes.



<a name="p11" id="p11"></a>
## The alg package


The methods *intersectArrays* and *intersectArrays_size* in the **IntersectArrays** class 
provides the algorithm for doing the intersection: *intersectArrays* computes the actual 
intersection, while *intersectArrays_size* finds only the size of the intersection:


<a name="p111" id="p111"></a>
### IntersectArrays class 

```
package gabriel.intersection.alg;

public class IntersectArrays<T extends Comparable<? super T> > {

    //...

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


}

```


The method *intersectArrays* puts the first array passed as parameter 
in a HashSet and builds another HashSet containing the intersection. 


The method *intersectArrays_size* puts the first array in a HashMap then 
scans the other array, keeping track of whether an element has already 
been scanned. 


Both methods take *O(m + n)* time, where m and n are the sizes of the arrays.
Computing the actual intersection uses two HashSets, while comouting the 
size of the intersection uses one HashMap.

 
The two methods are generic, and for the case of intersecting arrays of integers, 
it can be used as shown in the main method of the class


```
       // Find only the size of the intersection of the arrays
       int count = ia.intersectArrays_size(arr1, arr2);

       // Find the elements in the intersection of the arrays
       Set<Integer> result = ia.intersectArrays(arr1, arr2);

```




<a name="p112" id="p112"></a>
### IntersectDriver class 


The class **IntersectDriver** provides methods that: create the two arrays to be intersected; 
invoke the method *intersectArrays_size* so that it puts the first argument to the method 
the array that will be put in Hash; and compute the time taken by *intersectArrays_size* 


Here is the skeleton of the class **IntersectDriver** 

```
package gabriel.intersection.alg;

    // ...

    /**
     * Build the two arrays to intersect, filling them with random integers,
     * the intersect the arrays
     */
    public float buildAndIntesect(int[] sizes, boolean array_A) throws OutOfMemoryError {

        float time = 0.0F;

        checkMemory(sizes, array_A);

        try {

            ArrayFactory factory = ArrayFactory.getArrayFactory(random_range);
            Integer[] arr_a = factory.getArray(sizes[0]);
            Integer[] arr_b = factory.getArray(sizes[1]);

            //
            // Find the intersection
            //
            long start_time = System.currentTimeMillis();

            int n_res = doIntersection(arr_a, arr_b, array_A);
            sizes[2] = n_res;

            long end_time = System.currentTimeMillis();

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

        int size;

        if ( array_a_hash) {
            size = intersect.intersectArrays_size(arr_a, arr_b);
        }
        else {
            size = intersect.intersectArrays_size(arr_b, arr_a);
        }

        return size;

    }  // doIntersection

   // ...


} // IntersectDriver

```





<a name="p12" id="p12"></a>
## The ui package

There are two classes in this package: **IntersectUI*, the JavaFX application, 
and **IntersectUtil**, a class that provides helper methods for 
**IntersectUI**.


<a name="p121" id="p121"></a>
## IntersectUI class

The class **IntersectUI** is the entry point into the JavaFX application: 
it extends **Application** and overrides the *start()* method:


```
package gabriel.intersection.ui;

public class IntersectUI extends Application {


    // ...

    // Whether or not to put array A in HashSeet
    boolean array_A = true;


    /**
     * Launch the app
     */
    public static void main(String[] args) {

        launch(args);
    }



    /**
     * The start() method:
     *  the entry point into a JavaFX app
     */
    @Override
      public void start(Stage primaryStage) {


        IntersectUtil util = new IntersectUtil(width_scene, height_scene, width_text_field);

        GridPane grid = util.add_grid();

        int offset = 0;

        util.add_title_box(grid, "Find intersection of two arrays", 20, offset);

	offset += 2;
	util.add_title_box(grid, "Please fill in the size of arrays A and B", 16, offset);

        offset += 1;
        TextField array1 = util.add_text_field(grid, "Array A", offset);

        offset += 1;
	TextField array2 = util.add_text_field(grid, "Array B", offset);

        // ...
        Button start_btn = util.add_button(grid, "Find intersection", offset);

        start_btn.setOnAction(

          action ->
          {
              int[] sizes = {0, 0, 0};

              util.parse_validate_input(array1, array2, sizes);

              if ( (sizes[0] > 0) && (sizes[1] > 0) ) {

                  boolean ok = true;
                  float time = 0.0f;
                  IntersectDriver driver = new IntersectDriver(random_range);
                  try {
                      time = driver.buildAndIntesect(sizes, array_A);
                  }
                  catch (OutOfMemoryError ex) {
                      util.write_text_msg(2, time_stamp + "\n" + ex.getMessage() );
                      ok = false;
                  }
                  finally {
                      // Release heap resources
                      System.gc();
                  }

                  if  ( ok ) {
                      result_field.setText( String.valueOf(sizes[2]) );
                      time_field.setText( time + " sec");
                  }

              } // if ( (sizes[0] > 0) && (sizes[1] > 0) ) { ... }

          } // action

        ); // start_btn.setOnAction(  ... );


        //
        // Create scene, add it to the stage and show the stage
        //

        Scene scene = new Scene(grid, width_scene, height_scene);

        primaryStage.setTitle("Intersection of Arrays Application");
	primaryStage.setScene(scene);
        primaryStage.show();


      } // start


} // IntersectUI
    
```


The key functionality of this class is in the *start()* method, 
where a chain of actions occurs: 

  - a GridPane object is created and populated  with text fields, submit button, and radio-buttons; 

  - When the start button  is pressed, this triggers the parsing of the data entered in the UI (the sizes of the arrays, and which array will be put in a Hash) and the computation of the intersection.






<a name="p122" id="p122"></a>
## IntersectUtil class


This class provides methods needed by the **IntersecUI** class to 
create a GridPane add the UI widgets such as text fields, text boxes, 
buttons, radio-button groups, and to parse the system property random.range:


```
package gabriel.intersection.ui;


  /**
   * This class provides utility methods for the IntersectUI class
   */
  public class IntersectUtil {

    // Default window geometry
    private int width_scene = 650;
    private int height_scene = 520;
    private int width_text_field = 8;

    // Default selected box: none
    private Text box = null;

    //
    // Constructors
    //
    IntersectUtil(int width, int height, int width_text_field_) {
        width_scene      = width;
        height_scene     = height;
        width_text_field = width_text_field_;
    }

    IntersectUtil( ) {

        ; // Use default geometry
    }


    /**
     * Create the grid pane
     */
    protected GridPane add_grid() {

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        return grid;

    } // add_grid

    // ...


    /**
     * Add title box
     */
    protected void add_title_box(GridPane grid, String title, int font_size, int offset) {

        // Define title text, set text font
        Text scene_title = new Text(title);
        scene_title.setFont(Font.font("Tahoma", FontWeight.NORMAL, font_size));

        // Add text to the grid: last two param colspan=2, rowspan=1 are optional
        //grid.add(scene_title, 0, 0);
        grid.add(scene_title, 0, offset, 2, 1);

    } // add_title_box


    /**
     * Add a text field
     */
    protected TextField add_text_field(GridPane grid, String label_name, int offset) {

        // Label
        Label label = new Label(label_name);
        grid.add(label, 0, offset);

        // Input box
        TextField textField = new TextField();
        textField.setPrefWidth(width_text_field);

        grid.add(textField, 1, offset);

        return  textField;

    } // add_text_field



    /**
     * Add a text box for debug message written by the event handler
     */
    protected Text add_text_box(GridPane grid, int offset) {

        Text box = new Text();

	//
        // The text will span the first and second column
        //
        // GridPane.add(Node, colIndex, rowIndex [. colSpan [, rowSpan]] ):
        grid.add(box, 0, offset, 2, 1);

        return box;

    } // add_text_box


    /**
     * Add the Start Button
     */
    protected Button add_button(GridPane grid, String label, int offset) {

        // Create button
        Button btn = new Button(label);

        // Add button to hbox
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);

        // Add hbox to grid
        grid.add(hbBtn, 1, offset);

        return btn;

    } // add_button


    // ...


    /**
     * Get the value of the system property "random.range"
     */
    public int get_random_range() {

        int res = 0;

        try {

            String s = System.getProperty("random.range");
            // System.out.println(" random.range = " + s);

            if ( (s != null) && !( s.isEmpty() ) ) {
                res = Integer.valueOf(s);
            }
        }
        catch (Exception ex) {

            System.err.println("WARNING: Could not parse property random.range ");
        }

        return res;

    } // get_random_range


} // IntersectUtil

```


<a name="p2" id="p2"></a>
## Building and Running the application

<a name="p21" id="p21"></a>
## Building the application

There is a prebuild jar file in the app directory. 

If you want to compile the source and build the jar, there is a Makefile 
in the src directory:

```
  $ cd src
  $ make jar
```


<a name="p22" id="p22"></a>
## Running the application

After building or downloading the jar file provided in the app directory, 
you can run the application with

```
  $ java -jar IntersectArrays.jar
```

By default, the code fills in the arrays with random 
numbers in the range [0, 13000). To use a different range, 
set the random.range system property, e.g., 

```
  $ java -Drandom.range=20000 -jar app/IntersectArraysApp.jar 

```




<a name="p3" id="p3"></a>
## The impact of choosing which array to put in Hash


<a name="p31" id="p31"></a>
## Experiment


The next two images show the time to do the intersection when the big array, respectively the small array, 
is put in hash

<img src="https://github.com/gmateesc/AlgorithmsDataStructures/tree/master/IntersectArrays/images/put_array_big_in_hash.png" alt="blob" width="400">

versus:

<img src="https://github.com/gmateesc/AlgorithmsDataStructures/tree/master/IntersectArrays/images/put_array_small_in_hash.png" alt="blob" width="400">


Doing more runs, confirmes this pattern, which is explained next.




<a name="p32" id="p32"></a>
## Explanation



While the time complexity is in both cases *O(m + n)*, where *m* and *n* are the 
sizes of the two arrays, the time when the small array is in hash is smaller. 


This is due to several causes:  cache effects, rehashing and collisions: 

- cache effects: When the code checks if a sequence of (random) keys 
is in the Hash, the keys are spread all over the Hash (by the hashing 
function, and by the keys being random); the larger the Hash size, 
the larger the "jumps" through the memory address space, and thus 
the worse locality of cache;

- rehashing: as we insert elements in a hash, one the occupancy 
of the hash reaches the *load factor* all the entries are rehashed; 
the more entries we insert, the more rehashing occurs. This can be 
mitigated by creating a hash with a large enough initial capacity, 
but then we have the above cache effects;

- collisions: the more entries we insert in the hash, for a fixed 
random range, the higher the likelihood of collisions between 
the keys (the *birthday paradox*).




