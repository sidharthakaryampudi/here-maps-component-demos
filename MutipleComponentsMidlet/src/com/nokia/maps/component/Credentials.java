package com.nokia.maps.component;


import com.nokia.maps.common.ApplicationContext;


public class Credentials {
	
    public static void InitialiseAuth() {

        // You must get your own app_id and token by registering at
        // https://api.developer.nokia.com/ovi-api/ui/registration
        // Insert your own AppId and Token, as obtained from the above
        // URL into the two methods below.

    	ApplicationContext.getInstance().setAppID("...");
        ApplicationContext.getInstance().setToken("...");

        // Due to an issue with the hostnames that are used it is not possible to use
        // international maps at this stage on the WTK emulators. The devices and Nokia
        // emulators do not suffer from this limitation.
        if ("SunMicrosystems_wtk".equals(
                System.getProperty("microedition.platform"))) {
            ApplicationContext.getInstance().setChina(true);
        }
        
         // Check if virtual keyboard class is available 
        try {
        	Class.forName("com.nokia.mid.ui.VirtualKeyboard"); 
        	ApplicationContext.getInstance().enableDirectUtils();
        } catch (ClassNotFoundException e) {
        	// Class not available: running app on Java Runtime < 2.0.0 phone. 
        	// Do not enable direct utils.
        }
    }

}
