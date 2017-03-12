package optravis.patternsearch.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import optravis.patternsearch.dao.DataItem;


/**
 * <p>
 * This class supports complex querie againt the entries in a map
 * Map<K, V> map, which is passed to the c;ass constructor. 
 * 
 * The insertData() method loads the map into a Derby database;
 * Then complex queries can be performed by using JDBC to 
 * access the data loaded in the Derby database. 
 * </p> 
 * <p> 
 * In our implementation, Derby runs in an embedded framework, 
 * which implies that the application and Derby run in the same JVM, 
 * and the application starts up and shuts down the Derby engine.
 * </p>
 */


public class 
    PatternSearchMapServiceJavaDB<K extends Comparable<? super K>, V> implements PatternSearchMapService<K,V> 
{

    // Generic version of map
    private Map<K, V> map;

    // arrays of keys
    private ArrayList<K> keys = null;

    // Derby DB
    private String protocol  = "jdbc:derby:";

    // the default framework is embedded 
    private String framework = "embedded";

    // The name of the database
    private String dbName    = "derbyDB"; 

    // The name of the table
    private String tableName = "map_table";

    // The name of this class
    private String className = null;

    // Cutoff value for printing rows in insertData() and selectData()
    private static final int MAX_SET_SIZE = 32;



    //
    // Constructor
    //

    public PatternSearchMapServiceJavaDB( Map<K, V> map_ ) {

	init(map_);

    } 
    

    //
    // Initialize the fields map, keys and className
    //

    void init ( Map<K, V> map_ ) {
	
        map = map_;

	className = this.getClass().getCanonicalName();

	//
	// Extract the keys into the keys ArrayList
	//
	keys = new ArrayList<K>(map.size());
	for ( K key: map.keySet() ) {	
	    keys.add(key);
	}


    } // init() 





    /**
     * Implement the searchForpattern() method using a DerbyDB and JDBC
     */
    @Override 
      public Collection<String> searchForPattern(String searchPattern) {


	//
	// 1. Insert the entries in smap into the Derby DB
	//
	insertData(null);



	//
	// 2. Look up the entries that match the searchPattern, 
	//    doing case-insensitive matching
	//

	// 2.1 Get start time
	long start_time = System.currentTimeMillis();

	// 2.2 Sey search pattern and search
	String filter = "LOWER(val) LIKE '%" + searchPattern.toLowerCase() + "%'";
	ArrayList<String> res = selectData(filter);

	// 2.3 Compute time taken by the search
	long end_time = System.currentTimeMillis();
	double time = new Double(end_time - start_time);

	// 2.4 Show time taken
	System.out.println("The search took " + time/1000 + " seconds");


	//
	// 3. Return the list of UIDs that match the search
	//
	return res;


    } // searchForPattern





    /**
     * Sort the keys array
     */
    public void sortKeys ( ) {

	//
	// Sort the keys
	//

	//
	// For Map<K,V>
	//
	Collections.sort(keys);


	//
	// Show array
	//
	System.out.println( "\nTraverse map after sort: " );
	for ( K key: keys ) {	    
	    System.out.println( "  " + key + " => " + map.get(key) );
	}


    } // sort()





    
    /**
     * <p>
     * Connect to Derby (automatically loading the driver), and 
     * create a database if it does not exist, then create a table 
     * in the database, and insert and updating and retrieving
     * some data. Finally, the database is shut down.
     * See also the <code>selectData()</code> method.
     * </p>
     * 
     * @param limit  limit on the number of rows to insert
     *
     * @see #selectData()  #getConnection(String)
     */
    public void insertData(Integer limit )
    {

        ///System.out.println("\nStarting the DB in " + framework + " mode");

        /* We will executing SQL queries using Statement and PreparedStatement 
         * objects. These objects, as well as Connections and ResultSets,
         * are resources that should be released explicitly after use, hence
         * the try-catch-finally pattern used below.
         * 
         * We are storing the Statement and Prepared statement object references
         * in an array list for keeping track of them. 
         */
	
        Connection conn = null;
        Statement s;
        PreparedStatement ps;

	List<Statement> statements = new ArrayList<Statement>(); // list of Statements, PreparedStatements

        ResultSet rs = null;

        try
        {

	    //
	    // 0. Connect to the DB
	    //
	    conn = getConnection(dbName);
            ///System.out.println("Connected to and created database " + dbName);

	    // Disable autocommit 
            conn.setAutoCommit(false);


	    //
	    // 1. Create table tableName and clean it
	    //

            // 1.0 Create a statement object that we can use for running various
            //     SQL statements commands against the database.     
            s = conn.createStatement();
            statements.add(s);


	    //
	    // 1.1 Create table
	    //
	    try {
		s.execute("create table " + tableName + "(id varchar(32), val varchar(128) )");

	    }
	    catch ( SQLException ex ) {

		//
		// If table already exists, it is not really an exception
		//

		//String sql_state = ex.getSQLState();
		//System.out.println("SQL state = " + sql_state);
		if( ! ex.getSQLState().equals("X0Y32") ) {
		    throw ex;
		}
	    }
	    ///System.out.println("\nCreated table " + tableName);



	    //
	    // 1.2 Clear table
	    //
            s.execute("delete from " + tableName);
            conn.commit();




	    //
            // 2. Insert the rows into the table
	    //


	    //
	    // 2.0 The insert statement
	    //

            // parameter 1 is id(string), parameter 2 is val(varchar)
            ps = conn.prepareStatement
		(
		 "insert into " + tableName + " values (?, ?)"
		 );
            statements.add(ps);





	    //
	    // 2.1 Insert num_rows rows
	    //

	    int first_row = 0;

	    if ( (limit == null) || (limit > keys.size() ) ) {
		limit = keys.size();
	    }
	    int num_inserts = limit;

	    if ( num_inserts <= MAX_SET_SIZE ) { 
		System.out.println("\nInserting rows:");
	    }

	    
	    for ( int count = 0; count < num_inserts; count++) {

		int row_num = first_row + count;


		//
		// 2.1.1 First column in record is the map key
		//
		
		//
		// (a) Get the key to insert in first column
		//		
		String key = keys.get(row_num).toString();
		

		//
		// (b) Set first column to key
		//		
		ps.setString(1, key);
		


		//
		// 2.1.2 Second column in record is the map value
		//
		
		//
		// (a) Get map value for key
		//
		V val = map.get(key);
		String value = val.toString();
		if ( val instanceof DataItem ) {
		    DataItem item = (DataItem) val; 
		    value  = String.join(",", item.getKeywords());
		}


		//
		// (b) Set second column to map value
		//
		ps.setString(2, value);




		//
		// 2.1.3 Execute the INSERT query
		//

		ps.executeUpdate();

		if ( num_inserts <= MAX_SET_SIZE ) { 
		    //System.out.println("## key = " + key);
		    //System.out.println("## val = " + value);
		    System.out.println("  row " + count + ": " + key + ", '" + value + "'");
		}


	    } // for ( int count = 0; count < num_inserts; count++) { ... }





            //
            // 3. Commit the transaction. Any changes will be persisted to
            //    the database now.
             
            conn.commit();
            //System.out.println("\nCommitted the transaction");



	    //
	    // 4. Shut down DB and Derby engine
	    //
	    shutDown();

        }
        catch (SQLException sqle)
        {
            printSQLException(sqle);
        } 
	finally {

	    //
	    // 5. Clean up resources
	    //
	    cleanUp(conn, statements, rs);
        }


    } // insertData () { ... }





    /**
     * <p>
     * Connect to Derby database dbName, retrieve from tableName
     * the data (previously inserted by #insertData()) while 
     * applying the filter passed as input patameter.
     * Finally, shut down the database. 
     * See also the <code>insertData()</code> method.
     * </p>
     * @return  the list of keys matching the filter
     * @see #insertData()  #getConnection(String)
     */
    public ArrayList<String> selectData(String filter)
    {

	ArrayList<String> col_one = new ArrayList<String>();
	ArrayList<String> col_two = new ArrayList<String>();

        ///System.out.println("Class " + className + " started");

        ///System.out.println("\nStarting the DB in " + framework + " mode");

        Connection conn = null;
        Statement s;
        PreparedStatement ps;

	// list of Statements and/or PreparedStatements
	List<Statement> statements = new ArrayList<Statement>(); 

        ResultSet rs = null;

        try
        {

	    //
	    // 0. Connect to the DB
	    //
	    conn = getConnection(dbName);
            ///System.out.println("Connected to and created database " + dbName);

	    // Disable autocommit 
            conn.setAutoCommit(false);


	    //
            // 1. Create a statement object that we will use for running 
            //     SQL statements commands on the database. 
	    //
            s = conn.createStatement();
            statements.add(s);




            //
            // 2. Select the rows according to filter;
	    //    if verify flag set, then verify the results.
            //


	    //
	    // 2.1 Execure SELECT query
	    //

	    String where_clause = (filter != null)? "WHERE " + filter + " " : " ";
	    String query = 
		"SELECT id, val " + 
		"FROM " + tableName + " " + 
		where_clause + 
		"ORDER BY id";
	    ///System.out.println("\nExecute query: " + query );
            rs = s.executeQuery(query);
	    

	    //
	    // 2.2 Process the result set
	    //

	    //
	    // Extract row fields for each row in the result set
	    //
            String col_1;
	    String col_2;  
	    int count;
	    for(count = 0; rs.next(); count++ ) {
		col_1 = rs.getString(1);
		col_2 = rs.getString(2);
		col_one.add(col_1);
		col_two.add(col_2);

	    } // for(count = 0; rs.next(); count++ ) { ... }


	    //
	    // 2.3 Show result set
	    //
            /*
	    for ( int idx = 0; (idx < count) && (count <= MAX_SET_SIZE ); idx++ ) {
		System.out.println("  row " + idx + ": " + col_one.get(idx) + ", " + col_two.get(idx)); 
	    }
            */

            //
            // 3. Commit the transaction. Any changes will be persisted to
            //    the database now.
             
            conn.commit();
            //System.out.println("\nCommitted the transaction");




	    //
	    // 4. Shut down DB and Derby engine
	    //
	    shutDown();

        }
        catch (SQLException sqle)
        {
            printSQLException(sqle);
        } 
	finally {

	    //
	    // 5. Clean up resources
	    //
	    cleanUp(conn, statements, rs);
        }


        //System.out.println("Class " + className + " finished");

	return col_one;

    } // selectData () { ... }






      	

    /**
     * Get connection to the derby server
     */
    public Connection getConnection (String dbName)  throws SQLException {

	Connection conn = null;	
    
	//
	// If no user name is given, the schema APP will be used. 
	// providing a user name and password is optional. 
	//
	Properties props = new Properties(); // connection properties
	
	
	/*
	 * This connection specifies create=true in the connection URL to
	 * cause the database to be created when connecting for the first
	 * time. To remove the database, remove the directory derbyDB (the
	 * same as the database name) and its contents.
	 *
	 * The directory dbName will be created under the directory that
	 * the system property derby.system.home points to, or the current
	 * directory (user.dir) if derby.system.home is not set.
	 */
	conn = DriverManager.getConnection
	    (
	     protocol + dbName + ";create=true", 
	     props
	     );
	
	
	return conn;
	
    } // getConnection()






    /**
     * <p>
     * Shut down Derby.
     * </p>
     * <p>
     * In embedded mode, the application should shut down the database.
     * It is also possible to shut down the Derby system/engine, which
     * automatically shuts down all booted databases.
     *
     * Explicitly shutting down the database or the Derby engine with
     * the connection URL is preferred. This style of shutdown will
     * always throw an SQLException.
     *
     */
    public void shutDown() {
	
	if (framework.equals("embedded")) {
	    try {
		
		//
		// Method 1: Shut down Derby engine
		//           use the shutdown=true attribute
		//
		//DriverManager.getConnection("jdbc:derby:;shutdown=true");
		
		//
		// Method 2: Shut down a specific database only, but keep the
		//           engine running 
		DriverManager.getConnection("jdbc:derby:" + dbName + ";shutdown=true");
	    }
	    catch (SQLException se) {

		//
		// Expected exception for Derby engine shutdown
		//
		if ( ( se.getErrorCode() == 50000)  && 
		     ( "XJ015".equals(se.getSQLState()) )
		     ) {
		    ; ///System.out.println("\nDerby engine shut down normally");
		} 
		//
		// Expected exception for single DB shutdown
		//
		else if ( ( se.getErrorCode() == 45000)  && 
		     ( "08006".equals(se.getSQLState()) )
		     ) {
		    ; ///System.out.println("\nDerby shut down normally");
		} 
		else {
		    // If the error code or SQLState is different, we 
		    // have an unexpected exception (shutdown failed)
		    System.err.println("\nDerby did not shut down normally");
		    printSQLException(se);
		}
	    }
	}

    } // shutDown()

	


    /** 
     * Release all open resources to avoid unnecessary memory usage. 
     * So free resources: connection, statements and result set. 
     * If shurDown() is called, cleanUo() should be called after shutDown().
     */
    void cleanUp (Connection conn, List<Statement> statements, ResultSet rs) {

	//
	// 1. Release ResultSet
	//
	try {
	    if (rs != null) {
		rs.close();
		rs = null;
	    }
	} 
	catch (SQLException sqle) {
	    printSQLException(sqle);
	}
	
	//
	// 2. Release Statements and PreparedStatements
	//
	int i = 0;
	while (!statements.isEmpty()) {
	    // PreparedStatement extend Statement
	    Statement st = (Statement)statements.remove(i);
	    try {
		if (st != null) {
		    st.close();
		    st = null;
		}
	    } 
	    catch (SQLException sqle) {
		printSQLException(sqle);
	    }
	}
	
	//
	// 3. Release Connection
	//
	try {
	    if (conn != null) {
		conn.close();
		conn = null;
	    }
	} 
	catch (SQLException sqle) {
	    printSQLException(sqle);
	}
	
    } // cleanUp()

    



    

    /**
     * <p>
     * The main() method shows a sample usage of this class; 
     * The mettod creates a new instance of this class and runs 
     * the methods for storing a map in Derby and searching for a 
     * pattern in the map entries by searching the Derby database.
     * </p>
     *      
     * @param args - optional argument specifying which JDBC driver 
     *        to use to connect to Derby. Default is embedded JDBC driver.
     */
    public static void main(String[] args)
    {

	// Sample data used in the main() method
	Map<String, String> sample_data = new HashMap<String, String>()
	    {
		{
		    put("0180",  "Grand Ave.");      // 0  first row,  second value
		    put("0300",  "Lakeshore Ave.");  // 1  first row,  third & final value
		    put("1910",  "Union St.");       // 2  second row, first & final value
		    put("1956",  "Webster St.");     // 3  first row,  first value
		}
	    };


        PatternSearchMapServiceJavaDB<String,String> inst = 
	    new PatternSearchMapServiceJavaDB<String,String>(sample_data);


        ///System.out.println("Class " + inst.className + " started");

	//
	// 1. Preprocessing
	//
	inst.sortKeys();


	//
	// 2. Insert (k,v) pairs from map as rows into the table
	//

        inst.insertData(null);	


	//
	// 3. Get data inserted in the table using filter 
	//    and optionally verify against expected data
	//

	//
	// 4.1 Filter
	//

	String filter = "LOWER(val) LIKE '%ave.%' OR LOWER(val) LIKE '%st.%' ";


	//
	// 4.2 Invoke selectData() to fetch rows from table
	//
	inst.selectData(filter);

        ///System.out.println("Class " + inst.className + " finished");


    } // main()







    /**
     * Reports a data verification failure to System.err with the given message.
     *
     * @param message A message describing what failed.
     */
    private void reportFailure(String message) {
        System.err.println("\nERROR: Data verification failed: " + message);
    }



    /**
     * Prints details of an SQLException chain to <code>System.err</code>.
     * Details included are SQL State, Error code, Exception message.
     *
     * @param e the SQLException from which to print details.
     */
    public static void printSQLException(SQLException e)
    {
        // Unwraps the entire exception chain to unveil the real cause of the
        // Exception.
        while (e != null)
        {
            System.err.println("\n----- SQLException -----");
            System.err.println("  SQL State:  " + e.getSQLState());
            System.err.println("  Error Code: " + e.getErrorCode());
            System.err.println("  Message:    " + e.getMessage());
            // for stack traces, refer to derby.log or uncomment this:
            //e.printStackTrace(System.err);
            e = e.getNextException();
        }
    }



} // class PatternSearchMapServiceJavaDB


