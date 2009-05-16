package com.binaryelysium.mp3tunes.api;

@SuppressWarnings("serial")
public class LockerException extends RuntimeException {

	public LockerException() {
	}

	public LockerException(Throwable cause) {
		super(cause);
	}

	public LockerException(String message) {
		super(message);
	}

	public LockerException(String message, Throwable cause) {
		super(message, cause);
	}
}