package com.binaryelysium.mp3tunes;

@SuppressWarnings("serial")
public class NoSuchEntryException extends LockerException {

	public NoSuchEntryException() {
	}

	public NoSuchEntryException(Throwable cause) {
		super(cause);
	}

	public NoSuchEntryException(String message) {
		super(message);
	}

	public NoSuchEntryException(String message, Throwable cause) {
		super(message, cause);
	}
}