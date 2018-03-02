package com.dlmol.collabgraph.exception;

public class CollabGraphException extends Exception {

    public CollabGraphException(String msg) {
        super(msg);
    }

    public CollabGraphException(String msg, Exception e) {
        super(msg, e);
    }
}
