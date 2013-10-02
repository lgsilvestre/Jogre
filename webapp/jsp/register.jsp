<%@include file="global/page_imports.jsp"   %>
<%@include file="global/page_html.jsp"      %>
<bean:message key="title.register.jsp"/>
<%@include file="global/page_header.jsp"    %>
<%@include file="global/page_title_with_logon.jsp" %>

<bean:define id="genre" name="registerForm" property="genre"/>
<%@include file="global/page_menu.jsp" %>
        <tr>
          <td>
            <table cellpadding="0" cellspacing="0" border="0" width="100%" bgcolor="#eeeeee">
              <html:form action="/Register" method="post">
              <html:hidden name="registerForm" property="action" value="submit"/>
              <tr height="20">
                <td background="images/grad_vl_light_gray_20.gif"><img src="images/1p.gif" height="1" width="1"/></td>
              </tr>
              <tr><td height="10"><img src="images/1p.gif" height="1" width="1"/></td></tr>

              <logic:present name="org.apache.struts.action.ERROR">
              <tr><td colspan="5" align="center"><font class="errors"><html:errors/></font></td></tr>
              </logic:present>

              <tr>
                <td align="center">
                  <table cellpadding="3" cellspacing="1" border="0" bgcolor="#cccccc">
                    <tr height="30">
                      <td align="center" bgcolor="#55AA55" background="images/grad_vl_blue_32.gif">
                        <font face="Arial" size="3" color="#ffffff">
                           <logic:notEqual name="registerForm" property="action" value="success">
                           <b><bean:message key="register"/></b>
                           </logic:notEqual>
                           <logic:equal name="registerForm" property="action" value="success">
                           <b><bean:message key="registration.successful"/></b>
                           </logic:equal>
                        </font>
                      </td>
                    </tr>
                    <tr>
                      <td bgcolor="#ffffff">
                        <table cellpadding="0" cellspacing="10" border="0">
                          <logic:equal name="registerForm" property="action" value="success">
                          <tr height="25" valign="middle">
                            <td height="40" colspan="2" align="center">
                              <font face="arial" size="2" color="#777777">
                                <bean:message key="registration.was.successful.you.can.now.log.on"/>
                              </font>
                            </td>
                          </tr>                         
                          </logic:equal>
                          <tr height="25" valign="middle" bgcolor="#ffffff">
                            <td height="40" colspan="2" align="center">
                              <font face="arial" size="3" color="#666666">
                                <b><bean:message key="logon.information"/></b>
                              </font>
                            </td>
                          </tr>
                          <tr height="25" valign="middle" bgcolor="#ffffff">
                            <td width="50%" align="right">
                              <font face="arial" size="2" color="#777777"><b>*</b> <bean:message key="username.6.to.15.chars"/>:</font>
                            </td>
                            <td width="50%">
                              <logic:notEqual name="registerForm" property="action" value="success">
                              <html:text name="registerForm" property="username" size="15" maxlength="20" />
                              </logic:notEqual>
                              <logic:equal name="registerForm" property="action" value="success">
                              <font face="arial" size="2" color="#777777"><bean:write name="registerForm" property="username" /></font>
                              </logic:equal>
                            </td>
                          </tr>
                          <tr height="25" valign="middle">
                            <td width="50%" align="right">
                              <font face="arial" size="2" color="#777777"><b>*</b> <bean:message key="password.6.15.chars"/>:</font>
                            </td>
                            <td width="50%">
                              <logic:notEqual name="registerForm" property="action" value="success">
                              <html:password name="registerForm" property="password" size="15" maxlength="20" />
                              </logic:notEqual>
                              <logic:equal name="registerForm" property="action" value="success">
                              <font face="arial" size="2" color="#777777">********</font>
                              </logic:equal>
                            </td>
                          </tr>
                          <logic:notEqual name="registerForm" property="action" value="success">
                          <tr height="25" valign="middle">
                            <td width="50%" align="right">
                              <font face="arial" size="2" color="#777777"><b>*</b><i> <bean:message key="re-type.your.password"/>:</i></font>
                            </td>
                            <td width="50%">
                              <html:password name="registerForm" property="password2" size="15" maxlength="20" />
                            </td>
                          </tr>
                          </logic:notEqual>
                          <tr height="25" valign="middle">
                            <td height="40" colspan="2" align="center">
                              <font face="arial" size="3" color="#666666"><b><bean:message key="forget.password.section"/></b></font>
                            </td>
                          </tr>
                          <tr height="25" valign="middle">
                            <td width="50%" align="right">
                              <font face="arial" size="2" color="#777777"><b>*</b> <bean:message key="security.question"/>:</font>
                            </td>
                            <td width="50%">
                              <logic:notEqual name="registerForm" property="action" value="success">
                              <html:select property="securityQuestion">
                                <html:option value="0" key="security.question.0"/>
                                <html:option value="1" key="security.question.1"/>
                                <html:option value="2" key="security.question.2"/>
                                <html:option value="3" key="security.question.3"/>
                                <html:option value="4" key="security.question.4"/>
                                <html:option value="5" key="security.question.5"/>
                              </html:select>
                              </logic:notEqual>
                              <logic:equal name="registerForm" property="action" value="success">
                              <font face="arial" size="2" color="#777777"><bean:write name="registerForm" property="securityQuestionText" /></font>
                              </logic:equal>
                            </td>
                          </tr>
                          <tr height="25" valign="middle">
                            <td width="50%" align="right">
                              <font face="arial" size="2" color="#777777"><b>*</b> <bean:message key="answer"/>:</font>
                            </td>
                            <td width="50%">
                              <logic:notEqual name="registerForm" property="action" value="success">
                              <html:text name="registerForm" property="securityAnswer" size="30" maxlength="50" />
                              </logic:notEqual>
                              <logic:equal name="registerForm" property="action" value="success">
                              <font face="arial" size="2" color="#777777"><bean:write name="registerForm" property="securityAnswer" /></font>
                              </logic:equal>
                            </td>
                          </tr>
                          <logic:notEqual name="registerForm" property="action" value="success">
                          <tr height="25" valign="middle">
                            <td width="50%" align="right">
                              <font face="arial" size="2" color="#777777"><b>*</b> <bean:message key="re-type.your.answer"/>:</font>
                            </td>
                            <td width="50%">
                              <html:text name="registerForm" property="securityAnswer2" size="30" maxlength="50" />
                            </td>
                          </tr>
                          </logic:notEqual>
                          <tr height="25" valign="middle">
                            <td width="50%" align="right">
                              <font face="arial" size="2" color="#777777"><b>*</b> <bean:message key="year.of.birth.yyyy"/>:</font>
                            </td>
                            <td width="50%">
                              <logic:notEqual name="registerForm" property="action" value="success">
                              <html:text name="registerForm" property="yearOfBirth" maxlength="4" size="4"/>
                              </logic:notEqual>
                              <logic:equal name="registerForm" property="action" value="success">
                              <font face="arial" size="2" color="#777777"><bean:write name="registerForm" property="yearOfBirth" /></font>
                              </logic:equal>
                            </td>
                          </tr>
                          <tr height="25" valign="middle">
                            <td width="50%" align="right">
                              <font face="arial" size="2" color="#777777"><b>*</b> <bean:message key="email"/>:</font>
                            </td>
                            <td width="50%">
                              <logic:notEqual name="registerForm" property="action" value="success">
                              <html:text name="registerForm" property="email" maxlength="100" size="40"/>
                              </logic:notEqual>
                              <logic:equal name="registerForm" property="action" value="success">
                              <font face="arial" size="2" color="#777777"><bean:write name="registerForm" property="email" /></font>
                              </logic:equal>
                            </td>
                          </tr>
                          <tr height="25" valign="middle">
                            <td width="50%" align="right">
                              <font face="arial" size="2" color="#777777">
                                <i><bean:message key="click.checkbox.to.receive.newsletter"/></i>
                              </font>
                            </td>
                            <td width="50%">
                              <logic:notEqual name="registerForm" property="action" value="success">
                              <html:checkbox name="registerForm" property="receiveNewsletter" />
                              </logic:notEqual>
                              <logic:equal name="registerForm" property="action" value="success">
                              <font face="arial" size="2" color="#777777"><bean:write name="registerForm" property="receiveNewsletter" /></font>
                              </logic:equal>
                            </td>
                          </tr>
                          <logic:notEqual name="registerForm" property="action" value="success">
                          <tr valign="middle">
                            <td height="80" colspan="2" align="center">                              
                              <html:submit><bean:message key="submit"/></html:submit>
                            </td>
                          </tr>                           
						  </logic:notEqual>
                          <tr height="25" valign="middle">
                            <td height="40" colspan="2" align="center">
                              <font face="arial" size="2" color="#777777">
                                <b><bean:message key="note"/>: </b><bean:message key="submitted.data.will.not.be.passed"/>
                              </font>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>

              <tr><td height="20"><img src="images/1p.gif" height="1" width="1"/></td></tr>
            </html:form>
            </table>
          </td>
        </tr>
<%@include file="global/page_footer.jsp"    %>