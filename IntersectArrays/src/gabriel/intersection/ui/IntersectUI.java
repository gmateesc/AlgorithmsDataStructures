
package gabriel.intersection.ui;

import javafx.application.Application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.stage.Stage;
import javafx.scene.Scene;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Toggle;

import javafx.scene.layout.GridPane; 
import javafx.scene.text.Text;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import gabriel.intersection.alg.*;
import gabriel.intersection.ui.*;



public class IntersectUI extends Application {


    //
    // Window geometry parameters
    //
    final int width_scene      = 650; 
    final int height_scene     = 520;
    final int width_text_field = 8; 

    // Whether or not to put array A in HashSeet
    boolean array_A = true;


    //
    // Default range of the random numbers 
    // generated to fill in the arrays.
    //
    // Override with -Drandom.range=10000 
    //
    int random_range = 13000;


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


	//
	// 0. Create GridPane container
	//

	IntersectUtil util = new IntersectUtil(width_scene, height_scene, width_text_field);

        GridPane grid = util.add_grid();

	int range = util.get_random_range();
	if ( range > 0 ) {
	    random_range = range;
	}




	//
	// 1. Set title and subtitle
	//

	// 1.0 offset at which to place a text field or button
	int offset = 0; 

	// 1.1 Set title and font and add it to the grid (0)
	util.add_title_box(grid, "Find intersection of two arrays", 20, offset);

	// 1.2 Set title and font and add it to the grid (2)
	offset += 2;
	util.add_title_box(grid, "Please fill in the size of arrays A and B", 16, offset);


	//
	// 2. Set array A text field, and add it to the grid (3)
	//
 	offset += 1;
	TextField array1 = util.add_text_field(grid, "Array A", offset);


	//
	// 3. Set array B text field, and add it to the grid  (4)
	//
	offset += 1;
	TextField array2 = util.add_text_field(grid, "Array B", offset);


        //
        //  4. Add radio button group 
        //

	//
	// 4.1 Add radio button group selecting the array to be put in HashSet (5)
	//
        String msg = " to HashSet";
	offset += 1;
        ToggleGroup radio_group = util.add_radio_choice(grid, "Array A"+msg, "Array B"+msg, offset);


	//
	// 4.2 Set event handler for radio button group
	//
        ChangeListener<Toggle> cl = new ChangeListener<Toggle>() {

	    public void changed
  	    (
	      ObservableValue<? extends Toggle> ov,
	      Toggle toggle, 
	      Toggle toggle_new
	     ) 
	    {
		if (toggle_new != null) {
		    // Toogle the selected choicde
		    array_A = ! array_A;
		    //System.out.println("Putting array " + (array_A ? "A":"B") + " in hashSet");
		}
	    }
	};	
	radio_group.selectedToggleProperty().addListener(cl);
	


	//
	// 5. Set Start Button, and add it to the grid
	//
	offset += 3;
        Button start_btn = util.add_button(grid, "Find intersection", offset); // 8




	//
	// 6. Create text fields (into which the event handler inserts text)
	//    and add the fields to the grid
	//
	//       result_field: number of elements in the intersection 
	//       time_field:  the time to compute the intersection
	//

	offset += 2;  
	util.add_title_box(grid, "Result found", 16, offset++);    // 10
	TextField result_field = util.add_text_field(grid, "Intersection size", offset++); // 11
	TextField time_field   = util.add_text_field(grid, "Intersection time", offset++); // 12



	//
	// 7. Text boxes for status and done messages
	//
	offset += 2;  
	Text status_box = util.add_text_box(grid, offset); // 15



	//
	// 8. Set Exit Button, and add it to the grid
	//
	offset += 2;  
        Button exit_btn = util.add_button(grid, "Exit", offset); // 17
	exit_btn.setOnAction( 
          action -> 
	  {   
	      primaryStage.close();
	  }
	);



	//
	// 9. Handle Start Button Events
	//	
	start_btn.setOnAction( 

          action -> 
	  {	      

	      //
	      // 9.1 Clean up output fields
	      //

	      util.select_box(status_box);
	      util.write_text_msg(1, " ");
	      result_field.setText( " " );
	      time_field.setText( " " );


	      //
	      // 9.2 Validate data and call the intersect method
	      //

	      // 9.2.1 Create array that will hold the sizes of inputs and of the output
	      //   sizes[0] = size of array A
	      //   sizes[1] = size of array B
	      //   sizes[2] = size of intersection between A and B (computed below)
	      int[] sizes = {0, 0, 0};


	      // 9.2.2 Get sizes of arrays A and B into the "sizes" array
	      util.parse_validate_input(array1, array2, sizes);

	      String time_stamp = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);


	      //
	      // 9.3 If valid arrays sizes, perform the array intersection
	      //		  
	      if ( (sizes[0] > 0) && (sizes[1] > 0) ) {  

		  // 9.3.1 Emit time stamp 
		  util.write_text_msg(1, "Done: " + time_stamp  + 
                    "\nrandom_range=" + random_range + ". To change it use -Drandom.range=N");
		
		  // 9.3.2 Perform intersection and set size of the intersection in sizes[2]
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

		  // 9.3.3 Write the size of the result and the time taken to compute 
		  //       the result in the result field and time field, respectively
		  if  ( ok ) {
		      result_field.setText( String.valueOf(sizes[2]) );
		      time_field.setText( time + " sec");
		  }
		  
	      } // if ( (sizes[0] > 0) && (sizes[1] > 0) ) { ... }

	  } // action
	  
	); // start_btn.setOnAction(  ... );



	//
	// 10. Create scene, add it to the stage and show the stage
	//

	// 10.1 Create scene
	Scene scene = new Scene(grid, width_scene, height_scene);

	// 10.2 Set title of the stage
        primaryStage.setTitle("Intersection of Arrays Application");
  
	// 10.3 Add scene to stage
	primaryStage.setScene(scene);	      

	// 10.4 Show the stage
        primaryStage.show();


    } // start


} // IntersectUI


