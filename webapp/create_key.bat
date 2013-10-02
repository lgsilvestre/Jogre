@echo off

echo -------------------------------------------------
echo - Test keytool script
echo -------------------------------------------------
echo -
echo - NOTE: ensure PATH points to the bin directory 
echo -       of your Java Development Kit (JDK).
echo -
echo -------------------------------------------------
echo - Examples values ...
echo -
echo -   Password:                 password
echo -   First and last name:      www.jogre.org
echo -   Organizational unit:      webapp
echo -   Organization:             jogre
echo -   City or Locality:         Belfast
echo -   State or Province:        Antrim
echo -   Two-letter country code:  UK
echo ------------------------------------------------

keytool -genkey -alias jogre -keyalg RSA -validity 265 -keystore jogre.keystore 