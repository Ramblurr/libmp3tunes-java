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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.binaryelysium.util.StringUtilities;

public class Caller {

	@SuppressWarnings("serial")
    public class CallerException extends RuntimeException {

		public CallerException() {
		}

		public CallerException(Throwable cause) {
			super(cause);
		}

		public CallerException(String message) {
			super(message);
		}

		public CallerException(String message, Throwable cause) {
			super(message, cause);
		}
	}

	public static final String API_GENERAL = "http://ws.mp3tunes.com/api/v1/";
	public static final String API_STORAGE = "http://ws.mp3tunes.com/api/v1/";
	public static final String API_LOGIN = "https://shop.mp3tunes.com/api/v1/";
	private static final String PARAM_OUTPUT_METHOD = "output";
	private static final String PARAM_OUTPUT_TYPE = "xml";
	private static final Caller instance = new Caller();

	private String mUserAgent = "libmp3tunes-java";
	private String mApiRootUrl = API_GENERAL;

	private Session mSession;

	private boolean mDebugMode = true;

	private Caller() {
	}

	/**
	 * Returns the single instance of the <code>Caller</code> class.
	 * 
	 * @return a <code>Caller</code>
	 */
	public static Caller getInstance() {
		return instance;
	}

	public void setApiRootUrl(String url) {
		mApiRootUrl = url;
	}

	/**
	 * Sets a User Agent this Caller will use for all upcoming HTTP requests. If
	 * you distribute your application use an identifiable User-Agent.
	 * 
	 * @param userAgent
	 *            a User-Agent string
	 */
	public void setUserAgent(String userAgent) {
		this.mUserAgent = userAgent;
	}

	public void setSession(Session session) {
		mSession = session;
	}

	public RestResult call(String method, String... params) throws IOException {
		return call(method, StringUtilities.map(params));
	}

	/**
	 * Performs the web-service call. If the <code>session</code> parameter is
	 * <code>non-null</code> then an authenticated call is made. If it's
	 * <code>null</code> then an unauthenticated call is made.<br/>
	 * The <code>apiKey</code> parameter is always required, even when a valid
	 * session is passed to this method.
	 * 
	 * @param method
	 *            The method to call
	 * @param params
	 *            Parameters
	 * @param session
	 *            A Session instance or <code>null</code>
	 * @return the result of the operation
	 * @throws XmlPullParserException
	 */
	public RestResult call(String method, Map<String, String> params) throws IOException {

		// create new Map in case params is an immutable Map
		params = new HashMap<String, String>(params);
		params.put(PARAM_OUTPUT_METHOD, PARAM_OUTPUT_TYPE);
		if (mSession != null) {
			params.put("sid", mSession.getSessionId());
		}
		try {
			String get = buildParameterQueue(method, params);
			if (mDebugMode) {
				System.out.println("get string: " + mApiRootUrl + get);
			}
			OutputStream outputStream; 
			URL url = new URL(mApiRootUrl + get);
			HttpURLConnection urlConnection;
			if(mApiRootUrl.startsWith( "https" ))
			{
			    //We are going to force the Hostname verification, because the android sdk's default
			    // Verifier is broken
			    HttpsURLConnection urlsConnection = (HttpsURLConnection) url.openConnection();
			    urlsConnection.setHostnameVerifier ( new HostnameVerifier() {
			        public boolean verify ( String hostname, SSLSession session) {
			            return true;
			        }
			    });
			    urlsConnection.setRequestMethod("GET");
                urlsConnection.setDoOutput(true);
                urlConnection = urlsConnection;
                outputStream = urlConnection.getOutputStream();
			} else {
    			urlConnection = openConnection(mApiRootUrl + get);
    			
    			urlConnection.setRequestMethod("GET");
    			urlConnection.setDoOutput(true);
    			outputStream = urlConnection.getOutputStream();
			}
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					outputStream));

			writer.write(get);
			writer.close();
			int responseCode = urlConnection.getResponseCode();
			InputStream httpInput;
			String errorHeader = urlConnection.getHeaderField("X-MP3tunes-ErrorNo");
			if (responseCode == HttpURLConnection.HTTP_FORBIDDEN
					|| responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
				httpInput = urlConnection.getErrorStream();
			} else if (responseCode != HttpURLConnection.HTTP_OK) {
				return RestResult.createHttpErrorResult(responseCode, urlConnection
						.getResponseMessage());
			} else if (errorHeader != null) {
				String errorMsg = urlConnection.getHeaderField("X-MP3tunes-ErrorString");
				return RestResult.createRestErrorResult(Integer.parseInt(errorHeader), errorMsg);
			} else {
				httpInput = urlConnection.getInputStream();
			}
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			if (mDebugMode && false) {
				String all = "";
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(httpInput));
				String response = reader.readLine();

				System.out.println("While.. loop");
				
				while (null != response) {
					all = all.concat(response + "\n");
					response = reader.readLine();
				}
				System.out.println(all);
				xpp.setInput(new StringReader(all));
			} else {
				xpp.setInput(httpInput, "utf-8");
			}
			return RestResult.createOkResult(xpp);
		} catch (XmlPullParserException e) {
			return RestResult.createRestErrorResult(RestResult.FAILURE, e.getMessage());
		}
	}

	/**
	 * Creates a new {@link HttpURLConnection}, sets the proxy, if available,
	 * and sets the User-Agent property.
	 * 
	 * @param url
	 *            URL to connect to
	 * @return a new connection.
	 * @throws IOException
	 *             if an I/O exception occurs.
	 */
	public HttpURLConnection openConnection(String url) throws IOException {
		if (mDebugMode)
			System.out.println("open: " + url);
		URL u = new URL(url);
		HttpURLConnection urlConnection;
		/*
		 * if (proxy != null) urlConnection = (HttpURLConnection)
		 * u.openConnection(proxy); else
		 */
		urlConnection = (HttpURLConnection) u.openConnection();
		urlConnection.setRequestProperty("User-Agent", mUserAgent);
		return urlConnection;
	}

	private String buildParameterQueue(String method,
			Map<String, String> params, String... strings) {
		StringBuilder builder = new StringBuilder(100);
		builder.append(method);
		builder.append('?');
		for (Iterator<Entry<String, String>> it = params.entrySet().iterator(); it
				.hasNext();) {
			Entry<String, String> entry = it.next();
			builder.append(entry.getKey());
			builder.append('=');
			builder.append(StringUtilities.encode(entry.getValue()));
			if (it.hasNext() || strings.length > 0)
				builder.append('&');
		}
		int count = 0;
		for (String string : strings) {
			builder.append(count % 2 == 0 ? string : StringUtilities
					.encode(string));
			count++;
			if (count != strings.length) {
				if (count % 2 == 0) {
					builder.append('&');
				} else {
					builder.append('=');
				}
			}
		}
		return builder.toString();
	}
}
