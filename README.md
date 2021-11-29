# S4CProject
**S4CProject** is an Android app for managing Social Actions around the world. 
You can create new ones or edit/delete existing ones and the changes will be saved in Firestore (and caching in Room).
They will be showed in Google Map markers.

## Installation
The app could be launched in Android Studio.

Before start using it, you will need to write your Google Maps API Key in the "strings.xml" resources file :

```
<string name="maps_api_key">YOUR_GOOGLE_MAPS_API_KEY</string>
```

Make sure that your device has Google Play Services and Internet connection (for now, it will **crash** if not).

## Usage
Launch the app and a pop up message will ask you to give location permissions in order to center the map in the last known location.

Then, you will see a Google Map with the markers of the Social Actions saved in Firestore. Clicking in them will let you edit or delete that Social Action.

If you want to add a new one, click in the Floating Action Button on the bottom right corner.

## Authors
Helio Ojeda Reyes
