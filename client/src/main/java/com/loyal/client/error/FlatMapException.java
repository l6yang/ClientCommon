package com.loyal.client.error;

public class FlatMapException extends Exception {
    /**
     * Constructs a {@code ParseException} with no detail message.
     */
    public FlatMapException() {
        super();
    }

    /**
     * Constructs a {@code ParseException} with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public FlatMapException(String s) {
        super(s);
    }

    public FlatMapException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlatMapException(Throwable cause) {
        super(cause);
    }

}
