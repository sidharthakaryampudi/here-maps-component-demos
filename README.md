HERE Maps Component Demos
=========================

This Java ME application is a MIDlet suite consisting of demos that show how to
use the HERE Maps API components in Nokia Asha applications. The examples have
been carefully built to accommodate to the device in use and they work in full
touch, touch and type, and non-touch devices. The various examples are
implemented as independent MIDlets.

The MIDlet suite contains examples of the following components:

 1. Basic Map Example: This MIDlet displays a map and demonstrates how to move
    the map center and change the map zoom level.
 2. Map Type Example: This MIDlet displays a map and demonstrates how to display
    and change between different map types.
 3. Map Language Example: This MIDlet displays the map in one of eight random
    languages.
 4. Address search: This MIDlet displays a map and demonstrates the geocoding
    and reverse geocoding functionality.
 5. Marker Example: This MIDlet displays a map and demonstrates how to place a
    marker on the map, and make it draggable using a MapComponent.
 6. Custom Marker Example: This MIDlet displays a map and demonstrates how to 
	place a marker with a custom icon image on the map.
 7. Routing Example: This MIDlet displays a map and demonstrates how to
    calculate a route between multiple waypoints that are selected on the
    displayed map.
 8. Cached Map Example: This MIDlet displays a map and demonstrates file based
    caching for the map tiles.
 9. Place Search: This MIDlet uses the place search from the Search manager to
    locate various places and displays these on the map.
10. Share My Map: This MIDlet displays a map and allows the map to be shared
    via a SMS with another device.
11. Tweets On My Map: This MIDlet allows you to search for Twitter messages
    containing a search term and displays these on the map.
12. KML Data Example: This MIDlet illustrates how KML data can be parsed and
    used to create objects on the map.
13. Adding an overlay: This MIDlet illustrates how to place an overlay over the
    the base map, by combining two maps of Berlin.
14. Basic Map components: This MIDlet illustrates the usage of the five standard
    map components and serves as an introduction to simple custom map
    components.
15. Locate the device: This MIDlet demonstrates how to make a request to the
    location API and display the location of the device on a map.
  
The following MIDlets demonstrate the use of custom components:

 1. Type Selector Component: This MIDlet demonstrates adding a map type selector
    button to the screen. This allows the user to switch between the five
    standard map types. The button is a simple toggle, the UI is delegated to a
    separate class.
 2. Scale Bar Component: This MIDlet demonstrates adding a scale bar to the map.
    The length of the scale bar will alter on zoom and pan according to the
    Normalized Mercator projection, and the legend will switch between Metric
    and Imperial Measurements.
 3. Positioning Component: This MIDlet demonstrates adding a positioning
    geolocator button to the map. When the button is pressed, the app will
    listen for GPS or Cell ID location events as appropriate, and update a
    marker to the center of the screen.
 4. Overview Component: This MIDlet demonstrates the use of the overview 
    (picture-in-picture) button; the small Picture-in-Picture will track the
    central location of the main map as it is panned and zoomed.
 5. Infobubble Component: This MIDlet demonstrates a component that replaces a
    registered marker when it is centered on the screen. The Infobubble itself
    is clickable.
 6. Tooltip Component: This MIDlet demonstrates adding a series of tooltips to
    map markers. When a Map Marker is centered on the screen, a tooltip appears
    below it.
 7. Restrict Map Component: This MIDlet demonstrates the use of the Area
    Restriction Component. This prevents the user from panning/zooming outside
    of a chosen area.
 8. Focal Observer Component: This MIDlet demonstrates the use of the focal
    observer component. This means that when a MapObject is moved to the center
    of the screen, an event is fired in the MapCanvas.
 9. Context Menu Component (touch only): This MIDlet demonstrates the use of
    context menus on the map. 
10. Centering Component (touch only): This MIDlet demonstrates the use of the
    centering component. This means that when a MapObject is pressed, the map
    state is altered to bring it to the center of the map.
11. Long Touch Component (requires gesture support): This MIDlet demonstrates a
    MapComponent responding to the Long Press event from the Gesture API.
12. Pinch Zoom Component (requires gesture support): This MIDlet demonstrates a
    GestureMapCanvas responding to the Pinch event by zooming the map. Use of
    the GestureMapCanvas requires the presence of the Gesture API.

Several other fully functional demos are also available which combine the use of
the various examples described with touch components to provide a complete
application.

This example is hosted in GitHub:
https://github.com/nokia-developer/here-maps-component-demos

More information is available at http://developer.here.com/java


1. Prerequisites
-------------------------------------------------------------------------------

- Java ME basics


3. Compatibility
-------------------------------------------------------------------------------

Nokia Asha software platform 1.0 and Series 40 full touch, touch and type, and
non-touch phones. Tested on Nokia Asha 311 and 501. Developed with Nokia Asha
SDK 1.0.

The API is compatible with all Java Technology (JSR) 139 Connected, Limited
Device Configuration (CLDC) 1.1 and Mobile Information Device Profile (MIDP)
2.0, JSR 118 devices.

The binary jars of the 1.3 API can be found as maps-api under the plug-in
directory of the Nokia Asha 1.0 SDK.

3.1 Known issues
----------------

- Some map objects, such as rectangles, are not drawn correctly around the
  International Date Line (+/-180 degrees longitude).
- On some KVMs it is not possible to download a map due to the name of the host
  that provides the default map tiles.
- HOV Lane and Stairs RouteFeatures are not supported with the route
  calculations.


3. Building, installing, and running the application
-------------------------------------------------------------------------------

The examples have been created with Nokia Asha SDK 1.0. To open the project in
the SDK, select File -> Import -> Existing Projects and browse to the folder
you have the project in. Click Finish. Installation via Netbeans is also
included.

The project is dependent on the HERE Maps libraries. The libraries are provided
with the SDK, but you still need to include them in your build:

 1. Open the project properties (right click the project name and select 
    Properties from the pop-up menu).
 2. From Properties, select Java Build Path.
 3. On Libraries tab, click Add External JARs.
 4. Navigate to the folder where the HERE Maps libaries are located. In the
    default SDK installation that is
    \Nokia\Devices\Nokia_Asha_SDK_1_0\plugins\maps api\lib
 5. For this example you need to add all the .jar files (you can select either
    maps-core.jar or maps-core-debug.jar depending on your needs).
 6. After you have added all the JARs, go to Order and Export tab and select
    the added libaries.

In order for the HERE Maps APIs to work, you need to obtain an application ID
and token. You can get these by registering at http://developer.here.com. After
you have obtained the ID and token, place them in Base.java located in package
com.nokia.maps.example.

Now you are ready to build and test the application. Since this example consists
of many MIDlets, launch the application in the emulator by selecting Run As ->
Emulated JAVA ME JAD. To create the .jad and .jar file, open Application
Descriptor in the IDE and in the Overview tab, select Create package.

You can install the application on a phone by transferring the .jar file in Mass
storage mode, with Nokia Suite or over Bluetooth. Locate the file with file
browser and tap to install. Then find the application icon in the application
menu and tap it to launch.


4. License
-------------------------------------------------------------------------------

See the license text file delivered with this project. The license file is also
available online at
https://github.com/nokia-developer/here-maps-component-demos/blob/master/Licence.txt


5. Version history
-------------------------------------------------------------------------------

1.0 Initial release.
