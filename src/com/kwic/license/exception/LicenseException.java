package com.kwic.license.exception;

public class LicenseException extends Exception {
	private static final long serialVersionUID = 1L;

	public LicenseException() {
		super();
	}

	public LicenseException(String message, Throwable cause) {
		super(message, cause);
	}

	public LicenseException(String message) {
		super(message);
	}

	public LicenseException(Throwable cause) {
		super(cause);
	}
}
