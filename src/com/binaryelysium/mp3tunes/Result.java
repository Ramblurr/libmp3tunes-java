package com.binaryelysium.mp3tunes;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class Result {
	public enum Status {
		OK, FAILED
	}

	final public static int FAILURE = -999; // Used when something irrecoverable
	// goes wrong with parsing
	final public static String NAMESPACE_OPENSEARCH = "http://a9.com/-/spec/opensearch/1.1/";
	private Status status;
	private String errorMessage = null;
	private int errorCode = -1;
	private int httpErrorCode = -1;

	private XmlPullParser xmlParser;

	public Result(XmlPullParser xpp) {
		try {
			xpp.nextTag();
			xpp.require(XmlPullParser.START_TAG, null, "mp3tunes"); // this will fail if something went wrong
			this.status = Status.OK;
			this.xmlParser = xpp;
		} catch (XmlPullParserException e) {
			this.errorCode = Result.FAILURE;
			this.errorMessage = e.getMessage();
		} catch (IOException e) {
			this.errorCode = Result.FAILURE;
			this.errorMessage = e.getMessage();
		}
	}

	public Result(String errorMessage) {
		this.status = Status.FAILED;
		this.errorMessage = errorMessage;
	}

	static Result createOkResult(XmlPullParser xpp) {
		return new Result(xpp);
	}

	static Result createHttpErrorResult(int httpErrorCode, String errorMessage) {
		Result r = new Result(errorMessage);
		r.httpErrorCode = httpErrorCode;
		return r;
	}

	static Result createRestErrorResult(int errorCode, String errorMessage) {
		Result r = new Result(errorMessage);
		r.errorCode = errorCode;
		return r;
	}

	/**
	 * Returns if the operation was successful. Same as
	 * <code>getStatus() == Status.OK</code>.
	 * 
	 * @return <code>true</code> if the operation was successful
	 */
	public boolean isSuccessful() {
		return status == Status.OK;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public int getHttpErrorCode() {
		return httpErrorCode;
	}

	public Status getStatus() {
		return status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	public XmlPullParser getParser() {
		return xmlParser;
	}
}
