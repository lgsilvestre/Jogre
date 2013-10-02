        <tr>
          <td>
            <!-- Top of screen / user logon and off -->
            <table cellpadding="0" cellspacing="5" border="0" width="100%" bgcolor="#ffffff">
              <tr>
                <td align="left" valign="bottom" width="*" rowspan="2">
                  <a href="/jogreweb/Main.do"><img src="images/title.gif" alt="<bean:message key="jogre.home"/>" width="264" height="70" border="0"/></a>
                </td>
                <td align="right" valign="top" height="50%">
                  <html:form action="Logon" method="post">
					<% 
                      // If user not logged on
					  Object username = session.getAttribute("username");
					  if (username == null) {  
					%>

				    <html:hidden name="mainForm" property="action" value="logon"/>
                    <font face="Arial" size="1" color="#777777">
                      <bean:message key="username"/>: <html:text name="mainForm" property="username" size="8"/>
                      <bean:message key="password"/>: <html:password name="mainForm" property="password" size="8"/>
                    </font>
                    <a href="javascript:document.mainForm.submit();" class="menu_plain"><bean:message key="logon"/></a> 
				
                    <% } else { %>

					<html:hidden name="mainForm" property="action" value="logoff"/>
                    <font face="Arial" size="2" color="#999999">
                      <bean:message key="hello"/>,
                    </font> 
                    <a href="/jogreweb/Profile.do?username=<%=username%>" class="menu_bold_selected"><%=username%></a> | 
				    <a href="javascript:document.mainForm.submit();" class="menu_plain"><bean:message key="sign.out"/></a>
                    
					<% } %>

                  </html:form>
                </td>
              </tr>
              <tr>
                <td align="right" valign="middle" height="50%">
				  &nbsp;
				  <% // Only show resgister if not logged on %>
                  
				  <font face="Arial" size="2" color="#777777">
					  <bean:message key="not.a.member"/> <bean:message key="click"/>
				  </font> 
			      <a href="/jogreweb/Register.do" class="menu_plain"><bean:message key="register"/></a>
				  
                </td>
              </tr>
            </table>
          </td>
        </tr>
