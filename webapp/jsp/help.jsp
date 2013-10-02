<%@include file="global/page_imports.jsp"   %>
<%@include file="global/page_html.jsp"      %>
<bean:message key="title.register.jsp"/>
<%@include file="global/page_header.jsp"    %>
<%@include file="global/page_title_with_logon.jsp" %>
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
                           <b><bean:message key="help"/></b>
                        </font>
                      </td>
                    </tr>
                    <tr>
                      <td bgcolor="#ffffff">
                        <table cellpadding="0" cellspacing="10" border="0">                          
                          <tr height="25" valign="middle">
                            <td height="40" colspan="2" align="center">
                              <font face="arial" size="2" color="#777777">
                                !!! TODO !!!
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