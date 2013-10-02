        <!-- Menu Bar -->
        <tr>
          <td>
            <table cellpadding="5" cellspacing="0" border="0" bgcolor="#ffffff" width="100%">
              <tr>
                <td align="left">
                  <logic:equal name="genre" value="home"><font class="menu_bold_selected"><bean:message key="HOME"/></font></logic:equal>
                  <logic:notEqual name="genre" value="home"><a href="/jogreweb/Main.do" class="menu_bold"><bean:message key="HOME"/></a></logic:notEqual>
                  &nbsp;&nbsp;|&nbsp;&nbsp;
                  <logic:equal name="genre" value="all_games"><font class="menu_bold_selected"><bean:message key="ALL.GAMES"/></font></logic:equal>
                  <logic:notEqual name="genre" value="all_games"><a href="/jogreweb/GameList.do?genre=all_games" class="menu_bold"><bean:message key="ALL.GAMES"/></a></logic:notEqual>
                  &nbsp;&nbsp;|&nbsp;&nbsp;
                  <logic:equal name="genre" value="arcade"><font class="menu_bold_selected"><bean:message key="ARCADE"/></font></logic:equal>
                  <logic:notEqual name="genre" value="arcade"><a href="/jogreweb/GameList.do?genre=arcade" class="menu_bold"><bean:message key="ARCADE"/></a></logic:notEqual>
                  &nbsp;&nbsp;|&nbsp;&nbsp;
                  <logic:equal name="genre" value="card"><font class="menu_bold_selected"><bean:message key="CARD"/></font></logic:equal>
                  <logic:notEqual name="genre" value="card"><a href="/jogreweb/GameList.do?genre=card" class="menu_bold"><bean:message key="CARD"/></a></logic:notEqual>
                  &nbsp;&nbsp;|&nbsp;&nbsp;
                  <logic:equal name="genre" value="board"><font class="menu_bold_selected"><bean:message key="BOARD"/></font></logic:equal>
                  <logic:notEqual name="genre" value="board"><a href="/jogreweb/GameList.do?genre=board" class="menu_bold"><bean:message key="BOARD"/></a></logic:notEqual>
                  &nbsp;&nbsp;|&nbsp;&nbsp;
                  <logic:equal name="genre" value="other"><font class="menu_bold_selected"><bean:message key="OTHER"/></font></logic:equal>
                  <logic:notEqual name="genre" value="other"><a href="/jogreweb/GameList.do?genre=other" class="menu_bold"><bean:message key="OTHER"/></a></logic:notEqual>
                  &nbsp;&nbsp;|&nbsp;&nbsp;
                </td>
                <td align="right">
                  <logic:equal name="genre" value="help"><font class="menu_plain"><bean:message key="help"/></font></logic:equal>
                  <logic:notEqual name="genre" value="help"><a href="/jogreweb/Help.do" class="menu_plain"><bean:message key="help"/></a></logic:notEqual>                  
                  &nbsp;
                </td>
              </tr>
            </table>
          </td>
        </tr>