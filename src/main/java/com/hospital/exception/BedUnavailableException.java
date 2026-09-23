package com.hospital.exception;

public class BedUnavailableException extends InvalidDataException {
    public BedUnavailableException(String message) {
        super(message);
    }
}
