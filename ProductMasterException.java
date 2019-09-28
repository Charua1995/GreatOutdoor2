/**
 * 
 * Exception Raised due to Product Master Operation
 */


package com.capgemini.go.exception;

public class ProductMasterException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3310833736396981163L;

	public ProductMasterException() {
	}

	public ProductMasterException(String message) {
		super(message);
	}

	public ProductMasterException(Throwable cause) {
		super(cause);
	}

	public ProductMasterException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProductMasterException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
