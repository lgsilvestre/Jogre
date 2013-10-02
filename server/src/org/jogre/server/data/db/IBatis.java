/**
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

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.jogre.server.ServerProperties;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 * Thin wrapper class for populating objects using Ibatis.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class IBatis {

	// Declare constants
	private static final String DEFAULT_SQLMAP = "org/jogre/server/data/db/SQLMapConf.xml";
	
	// Singleton instance
	private static IBatis instance = null;
	
	private SqlMapClient sqlMap;
		
	/**
	 * Private singleton constructor which creates the sqlMap using the 
	 * default values i.e. this is for use of connecting to a standard JOGRE Server 
	 * database using the connection values in the server properties ("server.xml").
	 */
	private IBatis () throws IOException {
		// Set up default SQL map		
		this (IBatis.getJogreIbatisProperites ());
	}	
	
	/**
	 * Additional constructor - this can be used for testing purposes or retrieving
	 * a different connection to what is defined inside the server properties.
	 * 
	 * @param properties    Properties containing 4 properties: -
	 *                      "driver", "url", "username" and "password".
	 * @throws IOException
	 */
	private IBatis (Properties properties) throws IOException {
		// Set up default SQL map		
		Reader reader = Resources.getResourceAsReader(DEFAULT_SQLMAP);
		        
		this.sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader, properties);
	}
	
	/**
	 * Return the ibatis properties necessary to get a database connection using values
	 * from the server properties (server.xml) file. 
	 * 
	 * @return
	 */
	private static Properties getJogreIbatisProperites () {
		// Read database connection from "server.xml" and put into Properties instance
		ServerProperties serverProperties = ServerProperties.getInstance();
		
		Properties ibatisProperties = new Properties ();
		ibatisProperties.put("driver",   serverProperties.getDBDriver());
		ibatisProperties.put("url",      serverProperties.getDBConnURL());
		ibatisProperties.put("username", serverProperties.getDBUsername());
		ibatisProperties.put("password", serverProperties.getDBPassword());
		
		return ibatisProperties;
	}
	
	/**
	 * Get instance of IBatis wrapper.
	 * 
	 * @return
	 */
	public static IBatis getInstance () throws IOException {
		if (instance == null)
			instance = new IBatis ();
		return instance;
	}
	
	/**
	 * Get instance of IBatis wrapper (this is not part of the singleton).
	 * 
	 * @return
	 */
	public static IBatis getInstance (Properties props) throws IOException {
		return new IBatis (props);
	}
	
	/**
	 * Return a single object with supplied parameter object.
	 * 
	 * @param id
	 * @param parameterObject
	 * @return
	 */
	public Object getObject (String id, Object parameterObject) throws SQLException {
		Object obj = sqlMap.queryForObject (id, parameterObject);		
		return obj;
	}
		
	/**
	 * Return object.
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Object getObject (String id) throws SQLException {
		Object obj = getObject (id, null);
		return obj;
	}

	/**
     * Return a long value from an SQL map.  Useful for getting primary IDs.
     * 
     * @param id                  Statement ID.
     */
    public long getLong (String id) throws SQLException {
    	// Read long from object.
    	Long autoId = (Long)getObject(id);
    	if (autoId != null)	
    		return autoId.longValue();
    	else
    		return 0;
    }
	
	/**
	 * Return list of object with parameter object.
	 * 
	 * @param id
	 * @param parameterObject
	 * @return
	 */
	public List getList (String id, Object parameterObject) throws SQLException {
		List list = sqlMap.queryForList(id, parameterObject);		
		return list;
	}
	
	/**
	 * Return list of object with no parameter object.
	 * 
	 * @param id
	 * @return
	 */
	public List getList (String id) throws SQLException {
		List list = getList (id, null);
		return list;
	}

    /**
     * Update object using parameter object.
     * 
     * @param id                  Statement ID.
     * @param parameterObject     Parameter object.
     */
    public void update (String id, Object parameterObject) throws SQLException {
    	sqlMap.update (id, parameterObject);
    }
    
    /**
     * Update database with no parameter object.
     * 
     * @param id
     * @throws SQLException
     */
    public void update (String id) throws SQLException {
    	sqlMap.update(id);
    }
}