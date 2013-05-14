/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.routing;


import com.nokia.maps.routing.Maneuver;
import com.nokia.maps.routing.Route;

import com.nokia.maps.routing.RouteLeg;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;


/**
 * Displays each instruction turn-by-turn.
 */
public class TurnByTurnForm extends Form {

    /**
     * The BACK command for the Turn-by-Turn form.
     */
    public final static Command BACK = new Command("Ok", Command.BACK, 1);

    /**
     * Default constructor
     * @param route the route to displays instructions for.
     */
    public TurnByTurnForm(Route route) {
        super("");
        init(route);
    }

    /**
     * Initialising the route details.
     * @param route
     */
    private void init(Route route) {
        StringBuffer buf = new StringBuffer();
        RouteLeg[] legs = route.getLegs();

        for (int i = 0; i < legs.length; i++) {
            Maneuver[] manuevers = legs[i].getManuevers();
            for (int j = 0; j < manuevers.length; j++) {
                buf.append(manuevers[j].getInstruction()).append("\n\n");
            }
        }

        append(new StringItem("Instructions", buf.toString()));
        addCommand(BACK);
    }
}
