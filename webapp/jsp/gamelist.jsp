<%@include file="global/page_imports.jsp"   %>
<%@include file="global/page_html.jsp"      %>
<bean:message key="title.index.jsp"/>
<%@include file="global/page_header.jsp"    %>
<%@include file="global/page_title_with_logon.jsp" %>

<script language="JavaScript">

// for browsing by SEARCH.
function openGameFrame () {
    window.open('/jogreweb/Game.do','','resizable=no, scrollbars=no, width=590,height=475');
}

</script>

<bean:define id="genre" name="gameListForm" property="genre"/>
<%@include file="global/page_menu.jsp" %>

        <tr>
          <td>
            <table cellpadding="0" cellspacing="0" border="0" width="100%" bgcolor="#eeeeee">
              <tr height="20">
                <td colspan="3" background="images/grad_vl_light_gray_20.gif"><img src="images/1p.gif" height="1" width="1"/></td>
              </tr>
              <tr><td colspan="3" height="10"><img src="images/1p.gif" height="1" width="1"/></td></tr>
              <tr>
                <td width="10">&nbsp;</td>
                <td>
                  <%
                  // Table header is messy at min - lots of repeated HTML everywhere 
                  // Could we use custom TLD (tag library)? 
                  %>
                  <table cellpadding="3" cellspacing="1" border="0" bgcolor="#cccccc">                    
                    <tr height="30">
                      <td colspan="2" align="center" bgcolor="#55AA55" background="images/grad_vl_blue_32.gif">
                        <logic:equal name="gameListForm" property="col" value="game">
                          <a class="table" href="/jogreweb/GameList.do?col=game&dir=<bean:write name="gameListForm" property="dir" />&n=y&genre=<bean:write name="gameListForm" property="genre" />"><bean:message key="game"/></a>
                          <a class="table" href="/jogreweb/GameList.do?col=game&dir=<bean:write name="gameListForm" property="dir" />&n=y&genre=<bean:write name="gameListForm" property="genre" />"><img src="images/arrow_<bean:write name="gameListForm" property="dirStr" />.gif" border="0" width="10" height="11"/></a>
                        </logic:equal>
                        <logic:notEqual name="gameListForm" property="col" value="game">
                          <a class="table" href="/jogreweb/GameList.do?col=game&dir=<bean:write name="gameListForm" property="dir" />&genre=<bean:write name="gameListForm" property="genre" />"><bean:message key="game"/></a>
                          <img src="images/1p.gif" width="10" height="11"/>
                        </logic:notEqual>
                      </td>
                      <td align="center" bgcolor="#55AA55" width="100" background="images/grad_vl_blue_32.gif">
                        <logic:equal name="gameListForm" property="col" value="genre">
                          <a class="table" href="/jogreweb/GameList.do?col=genre&dir=<bean:write name="gameListForm" property="dir" />&n=y&genre=<bean:write name="gameListForm" property="genre" />"><bean:message key="genre"/></a>
                          <a class="table" href="/jogreweb/GameList.do?col=genre&dir=<bean:write name="gameListForm" property="dir" />&n=y&genre=<bean:write name="gameListForm" property="genre" />"><img src="images/arrow_<bean:write name="gameListForm" property="dirStr" />.gif" border="0" width="10" height="11"/></a>
                        </logic:equal>
                        <logic:notEqual name="gameListForm" property="col" value="genre">
                          <a class="table" href="/jogreweb/GameList.do?col=genre&dir=<bean:write name="gameListForm" property="dir" />&genre=<bean:write name="gameListForm" property="genre" />"><bean:message key="genre"/></a>
                          <img src="images/1p.gif" width="10" height="11"/>
                        </logic:notEqual>
                      </td>
                      <td align="center" bgcolor="#55AA55" width="150" background="images/grad_vl_blue_32.gif">
                        <logic:equal name="gameListForm" property="col" value="players">
                          <a class="table" href="/jogreweb/GameList.do?col=players&dir=<bean:write name="gameListForm" property="dir" />&n=y&genre=<bean:write name="gameListForm" property="genre" />"><bean:message key="players.online"/></a>
                          <a class="table" href="/jogreweb/GameList.do?col=players&dir=<bean:write name="gameListForm" property="dir" />&n=y&genre=<bean:write name="gameListForm" property="genre" />"><img src="images/arrow_<bean:write name="gameListForm" property="dirStr" />.gif" border="0" width="10" height="11"/></a>
                        </logic:equal>
                        <logic:notEqual name="gameListForm" property="col" value="players">
                          <a class="table" href="/jogreweb/GameList.do?col=players&dir=<bean:write name="gameListForm" property="dir" />&genre=<bean:write name="gameListForm" property="genre" />"><bean:message key="players.online"/></a>
                          <img src="images/1p.gif" width="10" height="11"/>
                        </logic:notEqual>
                      </td>
                      <td align="center" bgcolor="#55AA55" width="*" background="images/grad_vl_blue_32.gif">
                        <logic:equal name="gameListForm" property="col" value="synopsis">
                          <a class="table" href="/jogreweb/GameList.do?col=synopsis&dir=<bean:write name="gameListForm" property="dir" />&n=y&genre=<bean:write name="gameListForm" property="genre" />"><bean:message key="synopsis"/></a>
                          <a class="table" href="/jogreweb/GameList.do?col=synopsis&dir=<bean:write name="gameListForm" property="dir"/>&n=y&genre=<bean:write name="gameListForm" property="genre" />"><img src="images/arrow_<bean:write name="gameListForm" property="dirStr" />.gif" border="0" width="10" height="11"/></a>
                        </logic:equal>
                        <logic:notEqual name="gameListForm" property="col" value="synopsis">
                          <a class="table" href="/jogreweb/GameList.do?col=synopsis&dir=<bean:write name="gameListForm" property="dir" />&genre=<bean:write name="gameListForm" property="genre" />"><bean:message key="synopsis"/></a>
                          <img src="images/1p.gif" width="10" height="11"/>
                        </logic:notEqual>
                      </td>
                    </tr>
                    <logic:iterate name="gameListForm" property="games" id="game">
                    <tr bgcolor="#ffffff" align="center">
                      <td>
                        <table cellpadding="1" cellspacing="0" border="0" bgcolor="#ffffff">
                          <tr>
                            <td bgcolor="#cccccc"><a class="game" href="/jogreweb/Game.do?gameKey=<bean:write name="game" property="gameKey"/>"><img src="images/play<bean:write name="game" property="gameKey" />.gif" border="0"/></a></td>
                          </tr>
                        </table>
                      </td>
                      <td><a class="game" href="/jogreweb/Game.do?gameKey=<bean:write name="game" property="gameKey"/>"><bean:write name="game" property="gameName" /></a></td>
                      <td><font face="Arial" size="2" color="#777777"><bean:write name="game" property="gameGenre" /></font></td>
                      <td><font face="Arial" size="2" color="#777777"><bean:write name="game" property="numOfUsers" /></font></td>
                      <td>
                        <p><font face="Arial" size="2" color="#777777"><bean:write name="game" property="gameSynopsis" /></font></p>
                      </td>
                    </tr>
                    </logic:iterate>
                  </table>

                </td>
                <td width="10">&nbsp;</td>
              </tr>
              <tr><td height="20"><img src="images/1p.gif" height="1" width="1"/></td></tr>
            </table>
          </td>
        </tr>								  
<%@include file="global/page_footer.jsp"    %>