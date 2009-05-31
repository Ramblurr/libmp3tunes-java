/***************************************************************************
 *   Copyright 2008 Casey Link <unnamedrambler@gmail.com>                  *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 3 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU General Public License     *
 *   along with this program; if not, write to the                         *
 *   Free Software Foundation, Inc.,                                       *
 *   51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.         *
 ***************************************************************************/

package com.binaryelysium.mp3tunes.api;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * 
 * Represents the results of an HTTP call to the mp3tunes REST webservice.
 *
 */
public class RestResult {
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

	public RestResult(XmlPullParser xpp) {
		try {
			xpp.nextTag();
			xpp.require(XmlPullParser.START_TAG, null, "mp3tunes"); // this will fail if something went wrong
			this.status = Status.OK;
			this.xmlParser = xpp;
		} catch (XmlPullParserException e) {
			this.errorCode = RestResult.FAILURE;
			this.errorMessage = e.getMessage();
		} catch (IOException e) {
			this.errorCode = RestResult.FAILURE;
			this.errorMessage = e.getMessage();
		}
	}

	public RestResult(String errorMessage) {
		this.status = Status.FAILED;
		this.errorMessage = errorMessage;
	}

	static RestResult createOkResult(XmlPullParser xpp) {
		return new RestResult(xpp);
	}

	static RestResult createHttpErrorResult(int httpErrorCode, String errorMessage) {
		RestResult r = new RestResult(errorMessage);
		r.httpErrorCode = httpErrorCode;
		return r;
	}

	static RestResult createRestErrorResult(int errorCode, String errorMessage) {
		RestResult r = new RestResult(errorMessage);
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
