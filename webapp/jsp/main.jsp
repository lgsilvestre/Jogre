<%@include file="global/page_imports.jsp"   %>
<%@include file="global/page_html.jsp"      %>
<bean:message key="title.index.jsp"/>
<%@include file="global/page_header.jsp"    %>
<%@include file="global/page_title_with_logon.jsp" %>

<bean:define id="genre" name="mainForm" property="genre"/>
<%@include file="global/page_menu.jsp" %>

        <tr>
          <td>
            <table cellpadding="0" cellspacing="0" border="0" width="100%" bgcolor="#eeeeee">
              <tr height="20">
                <td colspan="5" background="images/grad_vl_light_gray_20.gif"><img src="images/1p.gif" height="1" width="1"/></td>
              </tr>
              <tr><td colspan="5" height="10"><img src="images/1p.gif" height="1" width="1"/></td></tr>
				
              <logic:present name="org.apache.struts.action.ERROR">
              <tr><td colspan="5" align="center"><font class="errors"><html:errors/></font></td></tr>
              </logic:present>
								
              <tr valign="top">
                <td width="10">&nbsp;</td>
                <td align="left">

                  <!--
                  ================================================================================================
                  TOP GAMES SECTION
                  ================================================================================================
                  -->
                  <table cellpadding="3" cellspacing="1" border="0" bgcolor="#cccccc">
                    <tr height="30">
                      <td colspan="2" align="center" bgcolor="#0099DD" background="images/grad_vl_blue_32.gif">
                        <font face="Arial" size="3" color="#ffffff">
                          <b><bean:message key="top.5.games"/>!</b>
                        </font>
                      </td>
                    </tr>
					
					<% int i = 1; %>
					<logic:iterate name="mainForm" property="topGames" id="game">
                    <tr height="24">
                      <td width="24" bgcolor="#000000" align="center" background="images/grad_vl_gray_24.gif">
                        <font face="Arial" size="2" color="#ffffff">
                          <b><%=i%></b>
                        </font>
                      </td>
                      <td width="*" align="left" bgcolor="#ffffff">
						  <a href="/jogreweb/Game.do?gameKey=<bean:write name="game" property="gameKey" />" class="game"><img src="images/<bean:write name="game" property="gameKey" />_icon.gif" border="0" width="16" height="16"/> <bean:message name="game" property="gameKey"/></a>
                          <font face="Arial" size="1" color="#999999">(<bean:write name="game" property="numOfUsers" />)</font> 
                      </td>	  
                    </tr>	
					<%i++;%>						
					</logic:iterate>
							
                  </table>
                </td>
                <td width="*">&nbsp;</td>
                <td align="right">

                  <!--
                  ================================================================================================
                  TOP GAMES SECTION
                  ================================================================================================
                  -->
                  <table cellpadding="3" cellspacing="1" border="0" bgcolor="#cccccc">
                    <tr height="30">
                      <td align="center" bgcolor="#55AA55" background="images/grad_vl_blue_32.gif">
                        <font face="Arial" size="3" color="#ffffff">
                          <b><bean:message key="new.games"/></b>
                        </font>
                      </td>
                    </tr>
                    <tr>
                      <td bgcolor="#ffffff">
                        <table cellpadding="0" cellspacing="15" border="0">
                          <tr valign="top">
							
							<logic:iterate name="mainForm" property="newGames" id="game">
                            <td align="center" width="130">
                              <table cellpadding="1" cellspacing="0" border="0" bgcolor="#ffffff">
                                <tr>
                                  <td bgcolor="#cccccc"><a href="/jogreweb/Game.do?gameKey=<bean:write name="game" property="gameKey" />"><img src="images/play<bean:write name="game" property="gameKey"/>.gif" border="0" width="100" height="100"/></a></td>
                                </tr>
                              </table>
                              <a href="/jogreweb/Game.do?gameKey=<bean:write name="game" property="gameKey" />" class="game"><bean:message name="game" property="gameKey"/></a>
                              <br/>
                              <font face="Arial" size="1" color="#aaaaaa"><bean:write name="game" property="numOfUsers" /> <bean:message key="players"/></font>
                              <p>
                                <font face="Arial" size="1" color="#999999">
                                  <bean:write name="game" property="gameSynopsis"/>
                                </font>
                              </p>
                            </td>								
							</logic:iterate>
									  
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </td>
                <td width="10">&nbsp;</td>
              </tr>
              <tr><td colspan="2" height="20"><img src="images/1p.gif" height="1" width="1"/></td></tr>
            </table>
          </td>			
        </tr>
<%@include file="global/page_footer.jsp"    %>