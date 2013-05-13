package com.nokia.maps.example.sharing;


import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;


/**
 * class for configuration of phone number and message details
 */
public class SharingConfig extends Form {

    final static Command SEND = new Command("Send", Command.OK, 10);
    final static Command CANCEL = new Command("Cancel", Command.CANCEL, 20);
    private TextField phoneNumber = new TextField("Number", "", 40,
            TextField.NUMERIC);
    private TextField message = new TextField("Message", "", 160, TextField.ANY);

    /**
     * Default constructor.
     */
    public SharingConfig() {
        super("Settings");
        append(phoneNumber);
        append(message);
        addCommand(CANCEL);
        addCommand(SEND);
    }

    /**
     * Gets the phone number.
     * @return the phone number
     */
    public String getPhoneNumber() {
        return phoneNumber.getString();
    }

    /**
     * Sets the phone number
     * @param number the phone number to set.
     */
    public void setPhoneNumber(String number) {
        phoneNumber.setString(number);
    }

    /**
     * Sets the SMS message
     * @param message the message to set.
     */
    public void setMessage(String message) {
        this.message.setString(message);
    }

    /**
     * Gets the SMS message to send.
     * @return the SMS message to send.
     */
    public String getMessage() {
        return message.getString();
    }
}
