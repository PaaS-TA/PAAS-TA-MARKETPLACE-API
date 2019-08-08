package org.openpaas.paasta.marketplace.api.exception;

public class PlatformException extends Exception {

    private static final long serialVersionUID = 1L;

    public PlatformException() {
        super();
    }

    public PlatformException(String message) {
        super(message);
    }

    public PlatformException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlatformException(Throwable cause) {
        super(cause);
    }

}
