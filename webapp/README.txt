------------------------------------------------------
Running JOGRE WEB APPLICATION
------------------------------------------------------

Run the following steps: -

1) Database
     -> Ensure that your database server (e.g. HSQL / MySQL / etc) is running.
    
2) JOGRE Server
     -> Ensure that "server.xml" points to a live database connection and that
        JOGRE server is running OK.
     
3) Jogre Webapp
     - > Ensure that "jogreweb.war" WAR file is put into deploy directory of web container
     - > Ensure that webapp points to live database (edit "webapp.properties" as required)
     
4) JOGRE_WEBAPP environment variable
     - > Ensure that webcontainer (JBoss / Jetty / Tomcat) can read the JOGRE_WEBAPP 
         environment property (which points to the "webapp.properties" directory).  There
         are several ways to do this, I prefer to use the Java -D java option
         
         e.g. -DJOGRE_WEBAPP="c:\jogre\webapp" in the "run.bat" / "run.sh" of web container.
         
         e.g. java -DJOGRE_WEBAPP="c:\jogre\webapp" -jar start.jar		(Jetty)
     
5) Web Container
     - > Run your webcontainer (JBoss / Jetty / Tomcat) and load up the following URL
         
         http://localhost:80/jogreweb/Main.do                  or
         
         http://localhost:8080/jogreweb/Main.do
         

Hopefully you should see the main page of the JOGRE web application!