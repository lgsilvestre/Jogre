/*
 * JOGRE (Java Online Gaming Real-time Engine) - Server
 * Copyright (C) 2004  Bob Marks (marksie531@yahoo.com)
 * http://jogre.sourceforge.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.jogre.server.data.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Properties;

import org.dbunit.Assertion;
import org.dbunit.DBTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DefaultDataSet;
import org.dbunit.dataset.DefaultTable;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

/**
 * Class which wraps around the database test case which can be used to test the
 * JOGRE database connection.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class JogreDatabaseTestCase extends DBTestCase {

	private static final String DEFAULT_DBUNIT_DIR = "test/dbunit/";
	private static final String TEST_DATABASE_PROPS_FILENAME = "/org/jogre/server/data/db/test_database.properties";
	
    /** Use this connection to perform database setup */
    protected IDatabaseConnection dbunitConn;
    protected IBatis iBatis;
    
    private String filename = null; 
    private Properties properties = null;

    public JogreDatabaseTestCase() {
        super();
    }

    /**
     * Create new test case.
     * 
     * @param filename
     *                Passed in file name of XML data.
     */
    public JogreDatabaseTestCase(String filename) {
        super();
    }
    
    /**
     * Return the properties back to the user. 
     * 
     * @return
     */
    public Properties getProperties () throws IOException {
    	if (properties == null) {
    		// read properties file
            InputStream is = this.getClass().getResourceAsStream (TEST_DATABASE_PROPS_FILENAME);
            this.properties = new Properties ();
    		properties.load (is);
    	}
    	return this.properties;
    }

    /**
     * Set up the database connection.
     * 
     * @see org.dbunit.DatabaseTestCase#setUp()
     */
    protected void setUp() throws Exception {
        dbunitConn = getConnection();
        
        // Create new tables if they dont exist
        Properties properties = getProperties();
		String loadscript = properties.getProperty("loadscript");
		File scriptFile = new File (loadscript);

		// Load database tables.
		DBConnection.runScript (dbunitConn.getConnection(), scriptFile);
    }

    /**
     * Tear down the database connection.
     * 
     * @see org.dbunit.DatabaseTestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        dbunitConn.close();
    }

    /**
     * Create connection to the Intelliden oracle database. This is defined in a
     * properties file.
     * 
     * @see org.dbunit.DatabaseTestCase#getConnection()
     */
    protected IDatabaseConnection getConnection() throws Exception {
        
        Properties properties = getProperties();
		String driver   = properties.getProperty("driver");
		String url      = properties.getProperty("url");
		String username = properties.getProperty("username");
		String password = properties.getProperty("password");

        // Load DBUNIT database driver
        DriverManager.registerDriver((Driver) Class.forName(driver).newInstance());
        Connection conn = DriverManager.getConnection(url, username, password);
        IDatabaseConnection connection = new DatabaseConnection(conn);
        
        // Create iBatis connection (using same properties)
        this.iBatis = IBatis.getInstance(properties);
        
        return connection;
    }

    /**
     * Return data set.
     * 
     * @see org.dbunit.DatabaseTestCase#getDataSet()
     */
    protected IDataSet getDataSet() throws Exception {
        // Try and return the default data set by using the name of the test
        // case and trying to load that XML file. Other wise do nothing.
        if (filename != null)
            return getFlatXmlDataSet(filename);

        return null;
    }

    /**
     * Get flat XML data set.
     * 
     * @param filename
     * @return
     * @throws Exception
     */
    protected IDataSet getFlatXmlDataSet(String filename) throws Exception {
        File file = new File(DEFAULT_DBUNIT_DIR + filename);
        if (file.exists()) {
            FlatXmlDataSet dataSet = new FlatXmlDataSet(file);
            return dataSet;
        }

        throw new Exception("could not find file for " + file.getAbsolutePath());
    }

    /**
     * This method inserts the contents of a FlatXmlDataSet file into the
     * connection.
     */
    protected void insertFileIntoDb(String filename) throws Exception {
        DatabaseOperation.CLEAN_INSERT.execute(dbunitConn,
                getFlatXmlDataSet(filename));
    }

    /**
     * Empty a specific table.
     * 
     * @param tableName
     * @throws Exception
     */
    protected void emptyTable(String tableName) throws Exception {
        IDataSet dataSet = new DefaultDataSet(new DefaultTable(tableName));
        DatabaseOperation.DELETE_ALL.execute(dbunitConn, dataSet);
    }

    /**
     * Compare data in database to an XML file. Note, this only works on a
     * table-by-table bases.
     * 
     * @param actualQuery
     *                SQL which is the actual data in database.
     * @param expectedDataFilename
     *                Filename of XML file containing expected data.
     * @throws Exception
     */
    protected void compare(String actualDataSQL, String expectedDataFilename)
            throws Exception {
        // Load expected data from an XML dataset
        IDataSet expectedDataSet = getFlatXmlDataSet(expectedDataFilename);
        String[] tableNames = expectedDataSet.getTableNames();
        if (tableNames.length != 1)
            throw new Exception("Only expected data with 1 table is allowed!!!");

        // Only 1 table exists, retrieve its table name
        ITable expectedTable = expectedDataSet.getTable(tableNames[0]);
        ITable actualDataSet = dbunitConn.createQueryTable("Compare SQL",
                actualDataSQL);

        // Assert actual database table match expected table
        Assertion.assertEquals(expectedTable, actualDataSet);
    }

    /**
     * Empty all the tables from the database.
     * 
     * @throws Exception
     */
    protected void emptyAllTables() throws Exception {
        emptyTable("");
    }
    
    /**
     * Export everything to an "export.xml" file.
     * 
     * @throws Exception
     */
    protected void exportAll () throws Exception {
    	IDataSet dataset = dbunitConn.createDataSet();
    	FlatXmlDataSet.write(dataset, new FileOutputStream (new File ("export.xml")));
    }    	
}