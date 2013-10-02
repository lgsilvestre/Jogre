<%@include file="global/page_imports.jsp"   %>
<%@include file="global/page_html.jsp"      %>
<bean:message key="title.index.jsp"/>
<%@include file="global/page_header.jsp"    %>
<%@include file="global/page_title_with_logon.jsp" %>

<script language="JavaScript">

// Javascript function to playing a game.  It checks session username is set, other
// it asks the user to logon.
function openGameFrame (gameKey) {
    <% if (session.getAttribute("username") != null) {%>
    url = '/jogreweb/PlayGame.do?gameKey=' + gameKey + '&action=nw';
    window.open(url, '', 'resizable=no, scrollbars=no, width=590,height=475');   // Open game in seperate window
    <% } else { %>
    document.logonForm.submit();        // Logon    
    <%}%>
}

</script>
        <!--  declare logon form if not logged in -->
        <form name="logonForm" action="/jogreweb/Logon.do" method="post"></form>

<bean:define id="genre" name="gameForm" property="genre"/>
<%@include file="global/page_menu.jsp" %>

        <tr>
          <td>
            <table cellpadding="0" cellspacing="0" border="0" width="100%" bgcolor="#eeeeee">
              <tr height="20">
                <td colspan="3" background="images/grad_vl_light_gray_20.gif"><img src="images/1p.gif" height="1" width="1"/></td>
              </tr>
              <tr><td colspan="3" height="10"><img src="images/1p.gif" height="1" width="1"/></td></tr>

              <logic:present name="org.apache.struts.action.ERROR">
              <tr><td colspan="3" align="center"><font class="errors"><html:errors/></font></td></tr>
              </logic:present>

              <tr>
                <td width="10">&nbsp;</td>
                <td>

                  <!--
                  ================================================================================================
                  GAME SECTION
                  ================================================================================================
                  -->
                  <table cellpadding="3" cellspacing="1" border="0" bgcolor="#cccccc">
                    <tr height="30">
                      <td align="left" bgcolor="#55AA55" background="images/grad_vl_blue_32.gif">
                        <font face="Arial" size="3" color="#ffffff">
                          <b>JOGRE: <bean:write name="gameForm" property="gameName" /></b>
                        </font>
                      </td>
                    </tr>
                    <tr>
                      <td bgcolor="#ffffff">
                        <table cellpadding="0" cellspacing="10" border="0">
                          <tr valign="top">
                            <td align="center">
                              <table cellpadding="1" cellspacing="0" border="0" bgcolor="#ffffff">
                                <tr>
                                  <td nowrap bgcolor="#cccccc"><img src="images/play<bean:write name="gameForm" property="gameKey" />.gif" border="0" /></td>
                                </tr>
                              </table>
                              <font face="Arial" size="1" color="#999999"><bean:write name="gameForm" property="numOfUsers" /> <bean:message key="players" /></font>
                            </td>
                            <td width="10">&nbsp;</td>
                            <td>
                              <table cellpadding="0" cellspacing="0" border="0">
                                <tr>
                                  <td>
                                    <table cellpadding="0" cellspacing="1" border="0" height="25" width="100" bgcolor="#000000">
                                      <tr>
                                        <td nowrap="nowrap" background="images/grad_vl_green_32.gif" align="center" valign="center">
                                          <a href="/jogreweb/PlayGame.do?gameKey=<bean:write name="gameForm" property="gameKey" />" class="table"><bean:message key="play.now" /></a>
                                        </td>
                                      </tr>
                                    </table>
                                  </td>
                                  <td width="40">&nbsp;</td>
                                  <td>
                                    <table cellpadding="0" cellspacing="1" border="0" height="25" width="150" bgcolor="#000000">
                                      <tr>
                                        <td nowrap="nowrap" background="images/grad_vl_green_32.gif" align="center" valign="center">                                          
                                          <a href="javascript:openGameFrame('<bean:write name="gameForm" property="gameKey" />')" class="table"><bean:message key="play.new.window"/></a>
                                        </td>
                                      </tr>
                                    </table>
                                  </td>
                                </tr>
                              </table>
                              <font face="Arial" size="2" color="#999999">
							  <p><b><bean:message key="players.online"/>:</b> <bean:write name="gameForm" property="numOfUsers" /></p>
							  <p><b><bean:message key="tables"/>:</b> <bean:write name="gameForm" property="numOfTables" /></p>
                              <p><b><bean:message key="description"/>:</b><br/><bean:write name="gameForm" property="gameSynopsis" /></p>
                              <p><b><bean:message key="rules"/>:</b><br/><bean:write name="gameForm" property="gameRules" /></p>
                              </font>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>

                </td>
                <td width="10">&nbsp;</td>
              </tr>
              <tr><td colspan="3" height="20"><img src="images/1p.gif" height="1" width="1"/></td></tr>
            </table>
          </td>
        </tr>								  
<%@include file="global/page_footer.jsp"    %>