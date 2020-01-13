# PurdueHCR-Android
Open Source repository for the PurdueHCR app

To run the app, you will need to download this repository and open it in android studio. You will also need the google-services.json file for the firebase project, which you can find on the google drive. Put that file under the app/ subfolder, and the app will run.


How to upload the app to the Google Play Store.

1. Run through all of the tests to ensure the app does what you want it to do.
2. Go to Utils/HttpNetworking/APIHelper.java and uncomment the domain that has the link the Production and comment the link to the test database
3. Go to Models/SystemPreferences and update the version number variable
4. Go to build.grade (Module: app) and increment the versionCode and version name

To upload the app to Google Play Store, you will need to go to the top bar menu of Android Studio -> Click Build -> Generate Signed Bundle -> Choose Android App Bundle -> Enter Passwords -> Choose Release.

After that runs, go the the Google Play Console and sign in. Choose Release Management in the side bar, then app releases. Click the Manage button under Production, then click Create Release.
Finally, upload the build, change the comment, save, review, submit.
