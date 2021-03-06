/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.twitter.json;


import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;


/**
 * Parses JSONObject from InputStream or URL
 */
public class JSONParser {
    // constant for internal state returned from next()

    private final static String VALUE = "value";
    private final static String END = "end";
    private final static String COMMA = "comma";
    // values end to one of these characters
    private static final byte[] VALUE_END = { ',', '\n', '}', ']', ':'};
    private static final byte[] CONTROL = { ']', '}', ','};

    /**
     * Parses JSONObject from InputStream
     *
     * @param stream the input stream to parse
     * @return a JSON object holding the data parsed from the input stream.
     * @throws IOException if the input stream can't be found.
     */
    public static JSONObject parse(InputStream stream) throws IOException {
        JSONReader reader = new JSONReader(stream);

        return (JSONObject) next(reader);
    }

    /**
     * Parses JSONObject from url
     *
     * @param url The url to load the data from
     * @return a JSON object holding the data parsed from the input stream.
     * @throws IOException if the resource is not found.
     */
    public static JSONObject parse(String url) throws IOException {
        JSONObject object = null;
        InputStream stream = null;

        try {
            stream = openStream(url);
            object = parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return object;
    }

    /**
     * Opens InputStream for reading from the given URL
     * @param url the url to read
     * @return  an input stream of data
     * @throws IOException if connection fails.
     */
    private static InputStream openStream(String url) throws IOException {
        InputStream input = null;
        HttpConnection connection = null;

        try {
            connection = (HttpConnection) Connector.open(url);
            int rc = connection.getResponseCode();

            if (rc != HttpConnection.HTTP_OK) {
                throw new IOException("HTTP error: " + rc + " url:" + url);
            }
            input = connection.openInputStream();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
        return input;
    }

    /**
     * Reads next token from reader
     * @param reader reader for JSON stream
     * @return the next data object.
     * @throws IOException
     */
    private static Object next(JSONReader reader) throws IOException {
        reader.next();
        Object n = null;

        if (reader.current() == '[') {
            n = createArray(reader);
        } else if (reader.current() == '{') {
            n = createObject(reader);
        } else if (reader.current() == '"') {
            n = reader.readJSONString();
        } else if (reader.current() == ':') {
            n = VALUE;
        } else if (reader.current() == ',') {
            n = COMMA;
        } else if (reader.current() == '}' || reader.current() == ']') {
            n = END;
        } else {
            n = reader.current() + reader.readUntil(VALUE_END);
            if (reader.contains(reader.current(), CONTROL)) {
                // value ended with control character, need to return it from next read
                reader.moveBack();
            }
        }
        return n;
    }

    /**
     * creates JSONArray using the given reader
     * @param reader
     * @return an array holding parsed JSON data.
     * @throws IOException
     */
    private static Object createArray(JSONReader reader) throws IOException {
        JSONArray array = new JSONArray();
        Object next = next(reader);

        while (next != END) {
            if (next == COMMA) {
                next = next(reader);
            }
            array.addElement(next);
            next = next(reader); // comma or end
            assertTrue(next == COMMA || next == END);
        }
        return array;
    }

    /**
     * creates JSONObject using the given reader
     * @param reader reader for JSON stream
     * @return an object holding JSON data.
     * @throws IOException
     */
    private static Object createObject(JSONReader reader) throws IOException {
        JSONObject object = new JSONObject();
        Object next = next(reader);

        while (next != END) {
            if (next == COMMA) {
                next = next(reader);
            }
            // object must be 'name':'value'
            String name = (String) next;

            assertTrue(next(reader) == VALUE);
            object.put(name, next(reader));
            next = next(reader); // comma or end
            assertTrue(next == COMMA || next == END);
        }
        return object;
    }

    /**
     * Throws exception if b is false. Used to assert JSON syntax
     * @param b the value to check.
     */
    private static void assertTrue(boolean b) {
        if (!b) {
            throw new IllegalArgumentException("Invalid JSON");
        }
    }
}
