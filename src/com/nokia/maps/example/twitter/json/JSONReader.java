/**
* Copyright (c) 2013 Nokia Corporation.
*/
package com.nokia.maps.example.twitter.json;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


/**
 * Reader for JSON stream
 */
class JSONReader {
    // white space

    private static final byte[] IGNORE = { ' ', '\n', '\r', '\t'};
    private InputStreamReader reader;
    private int current;
    private int before = -1; // needed to move back one character

    /**
     * Creates new JSONReader for the given stream
     *
     * @param stream The data stream to read.
     */
    public JSONReader(InputStream stream) {
        try {
            reader = new InputStreamReader(stream, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // use only ascii
            reader = new InputStreamReader(stream);
        }
    }

    /**
     * Reads next 'non empty' characted from stream.
     * @throws IOException if the stream cannot be read.
     */
    public void next() throws IOException {
        current = readerRead();
        while (contains(current, IGNORE)) {
            current = readerRead();
        }
    }

    /**
     * Current character from stream.
     *
     * @return current character.
     */
    public char current() {
        return (char) current;
    }

    /**
     * Sets current character to be returned from next read
     */
    public void moveBack() {
        before = current;
    }

    /**
     * Reads streams until one of the characters is found.
     *
     * @param end the end byte(s) to look for.
     * @return A string holding all the chars up to the end byte(s)
     * @throws IOException if the stream cannot be read.
     */
    public String readUntil(byte[] end) throws IOException {
        StringBuffer buf = new StringBuffer();

        current = readerRead();
        while (!contains(current, end)) {
            buf.append((char) current);
            current = readerRead();
        }
        return buf.toString();
    }

    /**
     * Reads until " using JSON string escape handling
     *  @return A string holding all the chars up to the next "
     * @throws IOException if the stream cannot be read.
     */
    public String readJSONString() throws IOException {
        StringBuffer buf = new StringBuffer();

        current = readerRead();
        while (current != '"') {
            if (current == '\\') {
                current = readerRead();
                if (current == 'u') {
                    StringBuffer sb = new StringBuffer();

                    sb.append((char) readerRead());
                    sb.append((char) readerRead());
                    sb.append((char) readerRead());
                    sb.append((char) readerRead());
                    buf.append((char) Integer.parseInt(sb.toString(), 16));
                } else if (current == 'b') {
                    buf.append('\b');
                } else if (current == 'f') {
                    buf.append('\f');
                } else if (current == 'n') {
                    buf.append('\n');
                } else if (current == 'r') {
                    buf.append('\r');
                } else if (current == 't') {
                    buf.append('\t');
                } else {
                    buf.append((char) current);
                }
            } else {
                buf.append((char) current);
            }
            current = readerRead();
        }
        return buf.toString();
    }

    /**
     * Check if the given array contains the given character
     *
     * @param c the ASCII char to look for.
     * @param end the array to check.
     * @return <code>true</code> if the character is found, <code>false</code>
     * otherwise.
     */
    public boolean contains(int c, byte[] end) {
        for (int i = 0; i < end.length; i++) {
            if (end[i] == c) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reads next value from the stream
     */
    private int readerRead() throws IOException {
        if (before != -1) { // move back one character
            int ret = before;

            before = -1;
            return ret;
        }
        int r = reader.read();

        if (r == -1) {
            throw new IOException("Stream end.");
        }
        return r;
    }
}
