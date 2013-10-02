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

import java.awt.image.DataBufferShort;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.jogre.common.IError;
import org.jogre.server.ServerLabels;
import org.jogre.server.ServerProperties;

/**
 * Class for retrieving a connection to a JOGRE database.
 * 
 * @author Bob Marks
 * @version Beta 0.3
 */
public class DBConnection {
   
    /**
     * Returns a connection to the user from the details
     * specified in the "database.properties" file.
     * 
     * @return  Database connection.
     */
    public synchronized static Connection getConn () {   
    	ServerProperties serverProperties = ServerProperties.getInstance();
        String driver   = serverProperties.getDBDriver();
        String connURL  = serverProperties.getDBConnURL();
        String username = serverProperties.getDBUsername();
        String password = serverProperties.getDBPassword();
                
        try {
            Class.forName (driver).newInstance();       // try and load driver            
            Connection connection = DriverManager.getConnection (connURL, username, password);
            
            return connection;
        }
        catch (InstantiationException iEx) {
        	System.err.println ("Failed to load JDBC/ODBC driver (" + driver + ")");
        	iEx.printStackTrace();
        	System.exit(-1);	// fatal error
        }
        catch (ClassNotFoundException cnfEx) {
        	System.err.println ("Failed to load JDBC/ODBC driver (" + driver + ")");
        	cnfEx.printStackTrace();
        	System.exit(-1);	// fatal error
        }
        catch (IllegalAccessException iaEx) {
        	System.err.println ("Failed to load JDBC/ODBC driver (" + driver + ")");
        	iaEx.printStackTrace();
        	System.exit(-1);	// fatal error
        }
        catch (SQLException sqlEx) {
        	System.err.println ("SQL exception (" + connURL + ")");
        	sqlEx.printStackTrace();
        	System.exit(-1);	// fatal error
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Test database connection.  Returns the following:
     * 
     * 0 - Success!!
     * 7 - Failed to load JDBC/ODBC driver.
     * 8 - Unable to connect to database.
     * 
     * @param driver
     * @param connURL
     * @param username
     * @param password
     * @return
     */
    public static int testConnection (String driver, String connURL, String username, String password, boolean testMetaData) {
    	try {
    		int error = IError.NO_ERROR;
    		
            Class.forName (driver).newInstance();       // try and load driver
            
            Connection connection = DriverManager.getConnection (connURL, username, password);
            
            if (testMetaData) 
            	error = DBConnection.testDatabaseMetaData(connection, driver);
            
            try {connection.close();}catch (Exception e) {}
            
            return error;
        }
        catch (ClassNotFoundException exClassNotFound) {
            return IError.JDBC_DRIVER_LOAD_ERROR;
        }
        catch (Exception exNoConn) {
            return IError.DATABASE_CONNECTION_ERROR;
        }
    }
    
    /**
     * Test database meta data.
     * 
     * @param connection
     * @return
     */
    public static int testDatabaseMetaData (Connection connection, String driver) {
    	try {
    		// Output fact that database connection was successful
    		ServerLabels labels = ServerLabels.getInstance();
    		System.out.println ("\t" + labels.get("database.connection.successful"));
    		
    		// Retrieve database meta data and check to see if table exists or not
			DatabaseMetaData dmd = connection.getMetaData();
			ResultSet rs = dmd.getTables(null, null, "SNAP_SHOT", null);	// FIXME - dont like SNAP_SHOT hardcoded
			if (!rs.next()) {
				System.out.println ("\t" + labels.get("no.tables.exist.in.database"));
				
				// Fix this when more drivers are added / supported
				File loadScript = null;
				if ("org.hsqldb.jdbcDriver".equals(driver))
					loadScript = new File ("data/database/create_db_hsqldb.sql");
				else if ("com.mysql.jdbc.Driver".equals(driver))
					loadScript = new File ("data/database/create_db_mysql.sql");
				
				// If driver has database load script then load it
				if (loadScript != null) {
					// Run load script
					System.out.println ("\t" + labels.get("running.database.load.script", new String [] {loadScript.getName()}));
					
					DBConnection.runScript (connection, loadScript);
					
					System.out.println ("\t" + labels.get("database.load.successful"));
				}
				else {
					System.out.println ("\t" + labels.get("no.database.load.script.for.driver", new String [] {driver}));
					return IError.LOAD_ERROR;
				}
			}
		} catch (SQLException sqlEx) {
			return IError.SQL_ERROR;
		} catch (IOException ioEx) {
			return IError.IO_ERROR;
		} catch (Exception genEx) {
			return IError.GENERAL_ERROR;
		}
    	
    	return IError.NO_ERROR;
    }
    
    /**
     * Run database script.
     * 
     * @param conn
     * @param script
     * @throws Exception
     */
    public static void runScript(Connection conn, File script) throws Exception {

        BufferedReader in = new BufferedReader(new FileReader(script));
        if (!in.ready())
            throw new IOException();
        String sql;
        while ((sql = in.readLine()) != null) {
        	
            // Strip last ';' character out
            sql = sql.trim();
            if (sql.endsWith(";") && !sql.startsWith("--")) {
                sql = sql.substring(0, sql.lastIndexOf(";"));
                
                // Create new statement and run script
                java.sql.PreparedStatement st = conn.prepareStatement(sql);
                st.execute();
            } 
        }
        in.close();
    }
}