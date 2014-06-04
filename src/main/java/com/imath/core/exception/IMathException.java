/* (C) 2013 iMath Research S.L. - All rights reserved.  */

package com.imath.core.exception;

/**
 * The IMathException Class, with predefined exceptions functionalities for iMath Cloud
 */
public class IMathException extends Exception {
	private static final long serialVersionUID = 1L;
	public static String ERR_NO_AVAILABLE_HOST = "No available host for job id:";
	public static enum IMATH_ERROR {
		OTHER             ("E0000", "[0]"),
		NO_AVAILABLE_HOST ("E0001", "No available host for job id: [0]"),
		NO_SOURCE_FILES   ("E0002", "No source files for job id: [0]"),
		FILE_NOT_FOUND    ("E0003", "File id: [0] not found."),
		ANY_AVAILABLE_HOST ("E0004", "Any available host."),
		NOT_USER_IN_DB ("E0005", "User: [0] is not in the iMath Cloud Database"),
		JOB_DOES_NOT_EXISTS("E0006", "Job id: [0] not found"),
		NO_AUTHORIZATION ("E0007", "No authorization to access de resource"),
		JOB_NOT_IN_OK_STATE("E0008", "Job id: [0] is not in the FINISHED OK state"),
		RECOVER_PROBLEM ("E0009", "Fatal error on erasing file [0]. Not recovered"),
		INVALID_PAGINATION ("E0010", "Invalid pagination parameter. It must be greater than 0");
		
		private final String code;
		private final String message;
		IMATH_ERROR(String code, String message) {
			this.code = code;
			this.message = message;
		}
		
		String getMessage() {
			return "[" + this.code + "] - " + this.message;
		}
		
		String getCode() {
			return this.code;
		}
	}
	
	private String message;
	private IMATH_ERROR iMathError;
	
	public IMathException(IMATH_ERROR iMathError, String [] args) {
		this.message = iMathError.getMessage();
		this.iMathError = iMathError;
		if (args != null) {
			for(int i=0;i<args.length;i++) {
				this.message = this.message.replaceAll("[" + i + "]", args[i]);
			}
		}
	}
	
	public IMathException(IMATH_ERROR iMathError) {
        this.message = iMathError.getMessage();
        this.iMathError = iMathError;
        
    }
	
	public IMathException(IMATH_ERROR iMathError, String arg) {
		this.message = iMathError.getMessage().replaceAll("\\[0\\]", arg);
		this.iMathError = iMathError;
		
	}
	
	public IMathException(IMATH_ERROR iMathError, String arg0, String arg1) {
		this.message = iMathError.getMessage().replaceAll("[0]", arg0);
		this.message = this.message.replaceAll("[1]", arg1);
		this.iMathError = iMathError;
	}

	public IMathException(IMATH_ERROR iMathError, String arg0, String arg1, String arg2) {
		this.message = iMathError.getMessage().replaceAll("[0]", arg0);
		this.message = this.message.replaceAll("[1]", arg1);
		this.message = this.message.replaceAll("[2]", arg2);
		this.iMathError = iMathError;
	}

	public String getMessage() {
		return this.message;
	}
	
	public IMATH_ERROR getIMATH_ERROR() {
		return this.iMathError; 
	}
}
