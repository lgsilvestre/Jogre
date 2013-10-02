filename=%game_id%/client.sh
#!/bin/sh
java -classpath .:classes:../../api/jogre.jar org.jogre.%game_id%.client.%Game_id%ClientFrame
