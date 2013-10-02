<%@include file="global/page_imports.jsp"   %>
<%@include file="global/page_html.jsp"      %>
<bean:message key="title.index.jsp"/>
<%@include file="global/page_header.jsp"    %>
<%@include file="global/page_title.jsp"     %>

<bean:define id="genre" name="mainForm" property="genre"/>
<%@include file="global/page_menu.jsp" %>

        <tr>
          <td>
            <table cellpadding="0" cellspacing="0" border="0" width="100%" bgcolor="#eeeeee">
              <tr height="20">
                <td colspan="5" background="images/grad_vl_light_gray_20.gif"><img src="images/1p.gif" height="1" width="1"/></td>
              </tr>
              <tr><td colspan="5" height="10" align="center"><img src="images/1p.gif" height="1" width="1"/></td></tr>
			  
              <logic:present name="org.apache.struts.action.ERROR">
              <tr><td colspan="5" align="center"><font class="errors"><html:errors/></font></td></tr>
              </logic:present>
				
              <tr valign="top">
                <td width="10%">&nbsp;</td>
                <td width="35%">

                  <!--
                  ================================================================================================
                  EXISTING USER
                  ================================================================================================
                  -->
                  <html:form action="/Logon" method="post">
					<html:hidden name="mainForm" property="action" value="logon"/>
                    <table cellpadding="0" cellspacing="1" border="0" bgcolor="#cccccc" width="100%">
                      <tr height="30">
                        <td align="center" bgcolor="#0099DD" background="images/grad_vl_blue_32.gif">
                          <font face="Arial" size="3" color="#ffffff">
                            <b><bean:message key="existing.user"/></b>
                          </font>
                        </td>
                      </tr>
                      <tr>
                        <td bgcolor="#ffffff">
                          <table cellpadding="0" cellspacing="15" border="0" width="100%">
                            <tr>
                              <td colspan="2" align="center" bgcolor="#ffffff">
                                <font face="Arial" size="2" color="#999999">
                                  <bean:message key="enter.username.and.password"/>
                                </font>
                              </td>
                            </tr>
                            <tr>
                              <td align="right" bgcolor="#ffffff">
                                <font face="Arial" size="2" color="#999999"><bean:message key="username"/>:</font>
                              </td>
                              <td align="left" bgcolor="#ffffff">
                                <html:text name="mainForm" property="username" size="16"/>
                              </td>
                            </tr>
                            <tr>
                              <td align="right" bgcolor="#ffffff">
                                <font face="Arial" size="2" color="#999999"><bean:message key="password"/>:</font>
                              </td>
                              <td align="left" bgcolor="#ffffff">
								<html:password name="mainForm" property="password" size="16"/>
                              </td>
                            </tr>
                            <tr>
                              <td colspan="2" align="center" bgcolor="#ffffff">
                                <a href="javascript:document.mainForm.submit();" class="menu_plain"><bean:message key="logon"/></a>
                              </td>
                            </tr>
                            <tr><td colspan="2">&nbsp;</td></tr>
                            <tr>
                              <td colspan="2" align="center" bgcolor="#ffffff">
                                <font face="Arial" size="2" color="#999999">
                                  <bean:message key="forgotten.your.password"/> <bean:message key="click"/> <a href="main.html" class="menu_plain"><bean:message key="here"/></a>
                                </font>
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </html:form>
                </td>
                <td width="10%">&nbsp;</td>
                <td width="35%">

                  <!--
                  ================================================================================================
                  NEW USER
                  ================================================================================================
                  -->

                  <table cellpadding="0" cellspacing="1" border="0" bgcolor="#cccccc" width="100%">
                    <tr height="30">
                      <td align="center" bgcolor="#0099DD" background="images/grad_vl_blue_32.gif">
                        <font face="Arial" size="3" color="#ffffff">
                          <b><bean:message key="new.user"/></b>
                        </font>
                      </td>
                    </tr>
                    <tr>
                      <td bgcolor="#ffffff">
                        <table cellpadding="0" cellspacing="10" border="0" width="100%">
                          <tr>
                            <tr><td>&nbsp;</td></tr>
                            <td align="center" bgcolor="#ffffff">
                              <font face="Arial" size="2" color="#777777">
                                <bean:message key="not.a.member"/> <bean:message key="click"/>
                              </font> 
                              <a href="/jogreweb/Register.do" class="menu_plain"><bean:message key="register"/></a>							
                            </td>
                            <tr><td>&nbsp;</td></tr>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>

                </td>
                <td width="10%">&nbsp;</td>
              </tr>
              <tr><td colspan="5" height="20"><img src="images/1p.gif" height="1" width="1"/></td></tr>
            </table>
          </td>			
        </tr>			  
<%@include file="global/page_footer.jsp"    %>