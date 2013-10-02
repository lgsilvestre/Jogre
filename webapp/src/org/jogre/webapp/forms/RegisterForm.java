/*
 * JOGRE (Java Online Gaming Real-time Engine) - Webapp
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
package org.jogre.webapp.forms;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.jogre.server.data.User;
import org.jogre.webapp.IJogreWeb;

/**
 * Interface which holds important JOGRE constants.
 * 
 * @author  Bob Marks
 * @version Beta 0.3
 */
public class RegisterForm extends AbstractJogreForm {
	
	private User user;		// user object
	
	// Extra fields used for input validation
	private String password2;
	private String securityAnswer2;
	private String securityQuestionText;
	
	/**
	 * Blank constructor.
	 */
	public RegisterForm () {
		this.user = new User ();
	}
	
	// Accessors / mutators
	
	public void setUser (User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
	
	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public String getSecurityAnswer2() {
		return securityAnswer2;
	}

	public void setSecurityAnswer2(String securityAnswer2) {
		this.securityAnswer2 = securityAnswer2;
	}

	public String getEmail() {
		return user.getEmail();
	}

	public String getPassword() {
		return user.getPassword();
	}

	public int getSecurityQuestion() {
		return user.getSecurityQuestion();
	}

	public String getSecurityQuestionText() {
		return securityQuestionText;
	}
	
	public String getSecurityAnswer() {
		return user.getSecurityAnswer();
	}

	public String getUsername() {
		return user.getUsername();
	}

	public String getYearOfBirth() {
		return user.getYearOfBirth();
	}

	public boolean isReceiveNewsletter() {
		return user.isReceiveNewsletter();
	}

	public void setEmail(String email) {
		user.setEmail(email);
	}

	public void setPassword(String password) {
		user.setPassword(password);
	}

	public void setReceiveNewsletter(boolean receiveNewsletter) {
		user.setReceiveNewsletter(receiveNewsletter);
	}

	public void setSecurityQuestion(int securityQuestion) {
		user.setSecurityQuestion(securityQuestion);
	}

	public void setSecurityQuestionText(String securityQuestionText) {
		this.securityQuestionText = securityQuestionText;
	}
	
	public void setSecurityAnswer(String securityAnswer) {
		user.setSecurityAnswer(securityAnswer);
	}

	public void setUsername(String username) {
		user.setUsername(username);
	}

	public void setYearOfBirth(String yearOfBirth) {
		user.setYearOfBirth(yearOfBirth);
	}

	/**
	 * Validate registering a new user.
	 * 
	 * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public ActionErrors validate (ActionMapping mapping, HttpServletRequest request)
    {
		// Create errors object
        ActionErrors errors = new ActionErrors();
        
        if (IJogreWeb.ACTION_SUBMIT.equals(action)) {			// validate user registering
        	int curYear = Calendar.getInstance().get(Calendar.YEAR);
        	
        	// Username validation
	        if(getUsername() == null || getUsername().equals(""))
	            errors.add("empty.username", new ActionMessage ("error.empty.username"));
	        else if(!validChars (getUsername()))        		
	        	errors.add("username.invalid.chars", new ActionMessage ("error.username.invalid.chars"));
	        else if (getUsername().length() < 6 || getUsername().length() > 20)
	        	errors.add("username.invalid.length", new ActionMessage ("error.username.invalid.length", "6", "20"));
	        
	        // Password validation
	        if(getPassword() == null || getPassword().equals("") || getPassword2() == null || getPassword2().equals(""))
	            errors.add("empty.passwords", new ActionMessage ("error.empty.passwords")); 
	        else if(!validChars (getPassword()))        		
	        	errors.add("password.invalid.chars", new ActionMessage ("error.password.invalid.chars"));
	        else if (getPassword().length() < 6 || getPassword().length() > 20)
	        	errors.add("username.password.length", new ActionMessage ("error.password.invalid.length", "6", "20"));
	        else if (!getPassword().equals(getPassword2()))
	        	errors.add("different.passwords", new ActionMessage ("error.different.passwords"));
	        
	        // Other field validation
	        if(getSecurityQuestion() == 0)
	            errors.add("security.question.not.selected", new ActionMessage ("error.security.question.not.selected"));
	        if(getSecurityAnswer() == null || getSecurityAnswer().equals("") || getSecurityAnswer2() == null || getSecurityAnswer2().equals(""))
	            errors.add("empty.security.answers", new ActionMessage ("error.empty.security.answers"));
	        else if(!getSecurityAnswer().equals(getSecurityAnswer2()))
	            errors.add("different.security.answers", new ActionMessage ("error.different.security.answers"));	        
	        try {
	        	int yearOfBirth = Integer.parseInt (getYearOfBirth());
	        	if (yearOfBirth < curYear - 150 || yearOfBirth > curYear)
	        		errors.add("invalid.year.of.birth", new ActionMessage ("error.invalid.year.of.birth"));
	        }
	        catch (NumberFormatException nfe) {
	        	errors.add("invalid.year.of.birth", new ActionMessage ("error.invalid.year.of.birth"));
	        }
	        if (getEmail().indexOf('@') == -1 || getEmail().indexOf('.') == -1)
	        	errors.add("invalid.email", new ActionMessage ("error.invalid.email"));
        }
        
        return errors;
    }
	
	/**
	 * Return true/false if this text is valid or not.
	 * 
	 * @param username
	 * @return
	 */
	private boolean validChars (String text) {
		// Check first char isn't a number
		if ("1234567890".indexOf(text.charAt(0)) != -1)
			return false;
		
		// Check for invalid characters
		String invalid = ";:\"<>*+=\\|?, )./[]{}#~@!£$%^&*-";
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (invalid.indexOf(c) != -1)
				return false;
		}
		return true;
	}
}