<%@include file="global/page_imports.jsp"   %>

<html>
<head>
<title>JOGRE <bean:write name="playGameForm" property="gameName" /></title>
</head>
<body leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">

<applet archive="applets/applet_<bean:write name="playGameForm" property="gameKey" />.jar" 
        code="<bean:write name="playGameForm" property="applet" />" 
        width="100%" 
        height="100%">
    <param name="username"   value="<bean:write name="playGameForm" property="username" />"/>
    <param name="password"   value="<bean:write name="playGameForm" property="password" />"/>
    <param name="serverhost" value="<bean:write name="playGameForm" property="serverHost" />"/>
    <param name="serverport" value="<bean:write name="playGameForm" property="serverPort" />"/>
    <param name="language"   value="<bean:write name="playGameForm" property="language" />"/>
</applet>

</body>
</html>