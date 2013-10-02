package org.jogre.common.comm;

import nanoxml.XMLElement;

/**
 * Test connection communication object.
 * 
 * @author Bob Marks
 */
public class CommAdminTestConnection extends CommGameMessage {
	
	// Fields
	private String driver = null;
	private String url = null;
	private String password = null;

	// XML constants
	public static final String XML_ATT_DRIVER   = "driver";
	public static final String XML_ATT_URL      = "url";
	public static final String XML_ATT_PASSWORD = "password";
	
	/**
	 * Connstructor which takes database connection details consisting of
	 * id, driver, url, username and password.
	 * 
	 * @param driver    Database connection driver.
	 * @param url       Database connection URL.
	 * @param username  Database connection username.
	 * @param password  Database connection password.
	 */
	public CommAdminTestConnection (String driver, 
			                   String url, 
			                   String username, 
			                   String password) 
	{
		super (username);
		
		this.driver   = driver;
		this.url      = url;
		this.password = password;
	} 
	
	/**
	 * Constructor which takes a status attribute. 
	 * 
	 * @param status
	 */
	public CommAdminTestConnection (int status) {
		super (status);
	}
	
	/**
	 * Connection which takes an XMLElement message and regenerates fields.
	 * 
	 * @param message
	 */
	public CommAdminTestConnection (XMLElement message) {
		super (message);
		
		this.driver   = message.getStringAttribute (XML_ATT_DRIVER);
		this.url      = message.getStringAttribute (XML_ATT_URL);
		this.password = message.getStringAttribute (XML_ATT_PASSWORD);
	}
		
	/**
	 * Return database connection driver.
	 * 
	 * @return
	 */
	public String getDriver () {
		return driver;
	}

	/**
	 * Return database connection URL.
	 * 
	 * @return
	 */
	public String getUrl () {
		return url;
	}

	/**
	 * Return database connection password.
	 * 
	 * @return
	 */
	public String getPassword () {
		return password;
	}

	/**
	 * Flatten the object.
	 * 
	 * @see org.jogre.common.comm.ITransmittable#flatten()
	 */
	public XMLElement flatten() {
		XMLElement message = super.flatten(Comm.ADMIN_TEST_CONNECTION);
		
		if (driver != null)   message.setAttribute (XML_ATT_DRIVER,   driver);
		if (url != null)      message.setAttribute (XML_ATT_URL,      url);
		if (password != null) message.setAttribute (XML_ATT_PASSWORD, password);
		
		return message;
	}
}