package org.jogre.server.data.xml;

import org.jogre.server.data.User;
import org.jogre.server.ServerProperties;
import org.jogre.server.data.ServerDataException;
import junit.framework.TestCase;

/* Tests para agregar/modificar usuarios al server usando la implementación de XML
 * Corrige bugs del original y muestra ejemplos de cómo manipular la clase
*/
public class ServerDataTest extends TestCase {
	private ServerDataXML data;
	private User u1, u2, u3;
	
	public void setUp() throws ServerDataException {
		ServerProperties.setUpFromFile();
		data = new ServerDataXML();
		u1 = new User("test1", "test1");
		u2 = new User("test2", "test2");
		u3 = new User("test3", "test3");
		data.newUser(u1);
		data.newUser(u2);
		data.newUser(u3);
	}
	
	public void testGetUsers() {
		assertTrue(data.containsUser("test1"));
		assertTrue(data.containsUser("test2"));
		assertFalse(data.containsUser("asdf"));
	}
	
	/* Para el login se debe definir el tipo de validacion de usuario:
	 * "password" pide que usuario y contraseña coincidan 
	 * "user" necesita solo el nombre del usuario para loguearse 
	 * "guest" no necesita ningún campo para poder entrar */
	public void testGetUserWithPass() {
		ServerProperties inst = ServerProperties.getInstance();
		inst.setUserValidation("password");
		assertTrue(data.containsUser("bob", "bob123"));
		assertFalse(data.containsUser("UnexistantUser", "UnexistantPass"));
		assertFalse(data.containsUser("bob", "UnexistantPass"));
		assertFalse(data.containsUser("UnexistantUser", "bob123"));
		assertFalse(data.containsUser("bob", "321bob"));
		
		inst.setUserValidation("guest");
		assertTrue(data.containsUser("guestUser", "asdf"));
		assertTrue(data.containsUser("bob", ""));
		
		inst.setUserValidation("user");
		assertTrue(data.containsUser("bob", "UnexistantPass"));
	}
	
	/* BUG: deleteUser no borraba el usuario, ya que no guardaba los cambios al XML */
	public void testDelete() throws ServerDataException {
		User test = new User("testUser", "asdf");
		data.newUser(test);
		assertTrue(data.containsUser("testUser"));
		data.deleteUser(test);
		assertFalse(data.containsUser("testUser")); /* bug */
	}
	
	public void tearDown() throws ServerDataException {
		data.deleteUser(u1);
		data.deleteUser(u2);
		data.deleteUser(u3);
	}
	
}
