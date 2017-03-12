
package gabriel.intersection.ui;

import javafx.stage.Stage;

import javafx.scene.Scene;
import javafx.scene.Group; 

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Toggle;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane; 
import javafx.scene.layout.HBox; 
import javafx.scene.layout.VBox; 

import javafx.scene.paint.Color;

import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import gabriel.intersection.alg.*;
import gabriel.intersection.ui.*;


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
     * Set the default text box uxed by the app
     */
    public void select_box (Text msg_box ) {

	box = msg_box;
	box.setText("");
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



    /**
     * Add a group of two radio buttons to the grid
     */
    protected ToggleGroup add_radio_choice(GridPane grid, String label1, String label2, int offset) {

	//
	// Create two radi buttons and group them
	//
	final ToggleGroup radio_group = new ToggleGroup();

	RadioButton rb1 = new RadioButton(label1);
	rb1.setSelected(true);
	rb1.setText(label1);
	rb1.requestFocus(); 
	rb1.setToggleGroup(radio_group);

	RadioButton rb2 = new RadioButton(label2);
	rb2.setSelected(false);
	rb2.setText(label2);
	rb2.setToggleGroup(radio_group);

	// Add the two radio buttons to an hbox
	int spacing = width_text_field/2;
	HBox hb_radio = new HBox(spacing);

	hb_radio.getChildren().addAll(rb1, rb2);

	grid.add(hb_radio, 1, offset);
       
	return radio_group;

    } // add_radio_choice



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



    /**
     * Write a message to a specified text box
     * @type: meesage type: 1 for info, 2 for error
     * @message; message to write
    */
    protected void write_text_msg(Text box, int type, String message) {

	if ( type == 1 ) {
	    box.setFill(Color.NAVY);
	}
	else {
	    box.setFill(Color.CRIMSON);
	}

	box.setText(message);

    } // write_text_msg



    /**
     * Write a message to the default box (set with select_box)
     */
    protected void write_text_msg(int type, String message) {

	// Make sure we have a vlaid text box
	if ( box == null ) {
	    return;
	}

	if ( type == 1 ) {
	    box.setFill(Color.NAVY);
	}
	else {
	    box.setFill(Color.CRIMSON);
	}

	box.setText(message);

    } // write_text_msg



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
	    
	    System.err.println("Could not parse property random.range "); 
	}

	return res;

    } // get_random_range



    /**
     * Parse the data in the text fields for array1 and array2 sizes
     * Write outcome to the text box. 
     */
    public void parse_validate_input(TextField array1, TextField array2, int[] sizes ) {

	String size_A = array1.getText();
	String size_B = array2.getText();  	

	try {
	    if ( (size_A != null) && !(size_A.isEmpty()) ) {
		sizes[0] = Integer.parseInt( array1.getText() ); 
	    }
	}
	catch(Exception ex) {
	}
	
	try {
	    if ( (array2.getText() != null) && !(array2.getText().isEmpty()) ) {
		sizes[1] = Integer.parseInt( array2.getText() ); 
	    }
	}
	catch(Exception ex) {
	}

	//
	// Write message to the default text box
	// the default is set with select_box()
	//
	if ( ! (sizes[0] > 0) && (box != null) ) {    
	    write_text_msg(box, 2, "Please set size of array A to a positive integer"); 
	} 
	else if ( ! (sizes[1] > 0) && (box != null) ) {   
	    write_text_msg(box, 2, "Please set size of array B to a positive integer"); 
	} 
	else {
	    write_text_msg(box, 1, " ");
	}

    } // parse_validate_input


} // IntersectUtil
