# Name Game (Android)#
Name Game is a project to learn the names of people using headshots of those people. The API for this project is located at  http://www.willowtreeapps.com/api/v1.0/profiles.

# Thoughts #
This project uses the baseline project from https://github.com/willowtreeapps/namegame_android. The project builds upon the existing framework fixing model objects and taking advantage of the ApplicationComponent to inject new activities. The Application Module contains a new method, providerNameGameManager to allow injection of a NameGameManager as a singleton. The NameGameManager contains information relevant to the name game, holding model objects, and performing statistic calculations. The NameGameManager also filters the Profiles object for 'bad' persons -- Person objects that have no headshots or contain a featured-image.

There exists two new activities, NameGameMenuActivity and NameGameResultsActivity, which are responsible for displaying a game menu and statistics for the NameGameActivity. These contain only an xml for portrait layout, which also works well when in landscape mode.

# Deploying #
There are two ways to run this application:
1. Download the APK file to your phone, install the APK file.
2. Build the project using gradle or gradlew and generate a signed APK file
