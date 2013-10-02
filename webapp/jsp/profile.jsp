<%@include file="global/page_imports.jsp"   %>
<%@include file="global/page_html.jsp"      %>
<bean:message key="title.profile.jsp"/>
<%@include file="global/page_header.jsp"    %>
<%@include file="global/page_title_with_logon.jsp" %>

<bean:define id="genre" name="profileForm" property="genre"/>
<%@include file="global/page_menu.jsp" %>
        <tr>
          <td>
            <table cellpadding="0" cellspacing="0" border="0" width="100%" bgcolor="#eeeeee">
              <html:form action="/Profile" method="post">
              <html:hidden name="profileForm" property="action" value="submit"/>
              <tr height="20">
                <td colspan="5" background="images/grad_vl_light_gray_20.gif"><img src="images/1p.gif" height="1" width="1"/></td>
              </tr>
              <tr><td colspan="5" height="10"><img src="images/1p.gif" height="1" width="1"/></td></tr>

              <logic:present name="org.apache.struts.action.ERROR">
              <tr><td colspan="5" align="center"><font class="errors"><html:errors/></font></td></tr>
              </logic:present>

              <tr>

                <td width="10">&nbsp;</td>

                <!-- Profile info -->
                <td align="left" valign="top">
                  <table cellpadding="3" cellspacing="1" border="0" bgcolor="#cccccc">
                    <tr height="30">
                      <td align="center" bgcolor="#55AA55" background="images/grad_vl_blue_32.gif">
                        <font face="Arial" size="3" color="#ffffff">
                           <b><bean:message key="profile"/></b>
                        </font>
                      </td>
                    </tr>
                    <tr>
                      <td bgcolor="#ffffff">
                        <table cellpadding="0" cellspacing="10" border="0">
                          <logic:equal name="profileForm" property="action" value="success">
                          <tr height="25" valign="middle">
                            <td height="40" colspan="2" align="center">
                              <font face="arial" size="2" color="#777777">
                                <bean:message key="profile.updated.successfully"/>
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
                              <html:text name="profileForm" property="username" size="15" maxlength="20" />
                            </td>
                          </tr>
                          <tr height="25" valign="middle">
                            <td width="50%" align="right">
                              <font face="arial" size="2" color="#777777"><b>*</b> <bean:message key="password.6.15.chars"/>:</font>
                            </td>
                            <td width="50%">
                              <html:password name="profileForm" property="password" size="15" maxlength="20" />
                            </td>
                          </tr>
                          <tr height="25" valign="middle">
                            <td width="50%" align="right">
                              <font face="arial" size="2" color="#777777"><b>*</b><i> <bean:message key="re-type.your.password"/>:</i></font>
                            </td>
                            <td width="50%">
                              <html:password name="profileForm" property="password2" size="15" maxlength="20" />
                            </td>
                          </tr>
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
                              <html:select property="securityQuestion">
                                <html:option value="0" key="security.question.0"/>
                                <html:option value="1" key="security.question.1"/>
                                <html:option value="2" key="security.question.2"/>
                                <html:option value="3" key="security.question.3"/>
                                <html:option value="4" key="security.question.4"/>
                                <html:option value="5" key="security.question.5"/>
                              </html:select>
                            </td>
                          </tr>
                          <tr height="25" valign="middle">
                            <td width="50%" align="right">
                              <font face="arial" size="2" color="#777777"><b>*</b> <bean:message key="answer"/>:</font>
                            </td>
                            <td width="50%">
                              <html:text name="profileForm" property="securityAnswer" size="30" maxlength="50" />
                            </td>
                          </tr>
                          <tr height="25" valign="middle">
                            <td width="50%" align="right">
                              <font face="arial" size="2" color="#777777"><b>*</b> <bean:message key="re-type.your.answer"/>:</font>
                            </td>
                            <td width="50%">
                              <html:text name="profileForm" property="securityAnswer2" size="30" maxlength="50" />
                            </td>
                          </tr>
                          <tr height="25" valign="middle">
                            <td width="50%" align="right">
                              <font face="arial" size="2" color="#777777"><b>*</b> <bean:message key="year.of.birth.yyyy"/>:</font>
                            </td>
                            <td width="50%">
                              <html:text name="profileForm" property="yearOfBirth" maxlength="4" size="4"/>
                            </td>
                          </tr>
                          <tr height="25" valign="middle">
                            <td width="50%" align="right">
                              <font face="arial" size="2" color="#777777"><b>*</b> <bean:message key="email"/>:</font>
                            </td>
                            <td width="50%">
                              <html:text name="profileForm" property="email" maxlength="100" size="40"/>
                            </td>
                          </tr>
                          <tr height="25" valign="middle">
                            <td width="50%" align="right">
                              <font face="arial" size="2" color="#777777">
                                <i><bean:message key="newsletter"/></i>
                              </font>
                            </td>
                            <td width="50%">
                              <html:checkbox name="profileForm" property="receiveNewsletter" />
                            </td>
                          </tr>
                          <tr valign="middle">
                            <td height="80" colspan="2" align="center">                              
                              <html:submit><bean:message key="update"/></html:submit>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </td>

                <td width="10">&nbsp;</td>

                <!-- Games summaries -->
                <td align="right" valign="top" width="*">
                  <table cellpadding="1" cellspacing="1" border="0" bgcolor="#cccccc">
                    <tr height="30">
                      <td align="center" bgcolor="#55AA55" background="images/grad_vl_blue_32.gif">
                        <font face="Arial" size="3" color="#ffffff">
                          <b><bean:message key="game.summaries"/></b>
                        </font>
                      </td>
                    </tr>
                    <tr>
                      <td bgcolor="#ffffff">
                        <table cellpadding="0" cellspacing="10" border="0"><tr><td>
                          <table cellpadding="2" cellspacing="1" border="0" bgcolor="#777777">
                            <tr height="25" valign="middle" bgcolor="#eeeeee">
                              <td><font face="arial" size="2" color="#555555"><b><bean:message key="game"/></b></font></td>
                              <td><font face="arial" size="2" color="#555555"><b><bean:message key="rating"/></b></font></td>
                              <td><font face="arial" size="2" color="#555555"><b><bean:message key="streak"/></b></font></td>
                              <td><font face="arial" size="2" color="#555555"><b><bean:message key="wins"/></b></font></td>
                              <td><font face="arial" size="2" color="#555555"><b><bean:message key="loses"/></b></font></td>
                              <td><font face="arial" size="2" color="#555555"><b><bean:message key="draws"/></b></font></td>
                              <td><font face="arial" size="2" color="#555555"><b><bean:message key="streak"/></b></font></td>
                            </tr>
                            <logic:empty name="profileForm" property="gameSummaries">
                            <tr valign="bottom" bgcolor="#ffffff">
                              <td colspan="7"><font face="arial" size="2" color="#777777"><bean:message key="error.no.games.played"/></td>
                            </tr>
                            </logic:empty>
                            <logic:notEmpty name="profileForm" property="gameSummaries">
                            <logic:iterate name="profileForm" property="gameSummaries" id="game">
                            <tr valign="bottom" bgcolor="#ffffff">
                              <td><font face="arial" size="2" color="#777777"><a href="/jogreweb/Game.do?gameKey=<bean:write name="game" property="gameKey" />" class="game"><img src="images/<bean:write name="game" property="gameKey" />_icon.gif" border="0"/> <bean:message name="game" property="gameKey" /></a></font></td>
                              <td><font face="arial" size="2" color="#777777"><bean:write name="game" property="rating" /></font></td>
                              <td><font face="arial" size="2" color="#777777"><bean:write name="game" property="streak" /></font></td>
                              <td><font face="arial" size="2" color="#777777"><bean:write name="game" property="wins" /></font></td>
                              <td><font face="arial" size="2" color="#777777"><bean:write name="game" property="loses" /></font></td>
                              <td><font face="arial" size="2" color="#777777"><bean:write name="game" property="draws" /></font></td>
                              <td><font face="arial" size="2" color="#777777"><bean:write name="game" property="streak" /></font></td>
                            </tr>
                            </logic:iterate>
                            </logic:notEmpty>
                          </table>
                        </td></tr></table>
                      </td>
                    </tr>
                  </table>
                </td>

                <td width="10">&nbsp;</td>

              </tr>

              <tr><td colspan="5" height="20"><img src="images/1p.gif" height="1" width="1"/></td></tr>
            </html:form>
            </table>
          </td>
        </tr>
<%@include file="global/page_footer.jsp"    %>