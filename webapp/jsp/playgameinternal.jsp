<%@include file="global/page_imports.jsp"   %>
<%@include file="global/page_html.jsp"      %>
<bean:message key="title.index.jsp"/>
<%@include file="global/page_header.jsp"    %>
<%@include file="global/page_title_with_logon.jsp" %>
<bean:define id="genre" name="playGameForm" property="genre"/>
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
                          <b>JOGRE: <bean:write name="playGameForm" property="gameName" /></b>
                        </font>
                      </td>
                    </tr>
                    <tr>
                      <td bgcolor="#ffffff"><applet archive="applets/applet_<bean:write name="playGameForm" property="gameKey" />.jar" 
                                code="<bean:write name="playGameForm" property="applet" />" 
                                width="640" 
                                height="480">
                            <param name="username"   value="<bean:write name="playGameForm" property="username" />"/>
                            <param name="password"   value="<bean:write name="playGameForm" property="password" />"/>
                            <param name="serverhost" value="<bean:write name="playGameForm" property="serverHost" />"/>
                            <param name="serverport" value="<bean:write name="playGameForm" property="serverPort" />"/>
                            <param name="language"   value="<bean:write name="playGameForm" property="language" />"/>
                        </applet></td>
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