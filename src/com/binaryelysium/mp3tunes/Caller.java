package com.binaryelysium.mp3tunes;

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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.binaryelysium.util.StringUtilities;

public class Caller {
	
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
	private static final String PARAM_PARTNER_TOKEN = "partner_token";
	private static final String PARAM_OUTPUT_METHOD = "output";
	private static final String PARAM_OUTPUT_TYPE = "xml";
	private static final Caller instance = new Caller();

	private String mUserAgent = "libmp3tunes-java";
	private String mApiRootUrl = API_GENERAL;
	
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

	public void setApiRootUrl(String url)
	{
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

	public Result call(String method, String... params)
			throws CallerException {
		return call(method, StringUtilities.map(params));
	}

	public Result call(String method, Map<String, String> params)
			throws CallerException {
		return call(method, params, null);
	}

	public Result call(String method, Session session, String... params) {
		return call(method, StringUtilities.map(params), session);
	}

	public Result call(String method, Session session,
			Map<String, String> params) {
		return call(method, params, session);
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
	private Result call(String method, Map<String, String> params, Session session) {
		
		// create new Map in case params is an immutable Map
		params = new HashMap<String, String>(params); 
		params.put(PARAM_OUTPUT_METHOD, PARAM_OUTPUT_TYPE);
		if (session != null) {
			params.put("sid", session.getSessionId());
		}
		try {
			String get = buildParameterQueue(method, params);
			if (mDebugMode) {
				System.out.println("get string: " + mApiRootUrl + get);
			}
			HttpURLConnection urlConnection = openConnection(mApiRootUrl + get);
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			OutputStream outputStream = urlConnection.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					outputStream));
			
			writer.write(get);
			writer.close();
			int responseCode = urlConnection.getResponseCode();
			InputStream httpInput;
			if (responseCode == HttpURLConnection.HTTP_FORBIDDEN
					|| responseCode == HttpURLConnection.HTTP_BAD_REQUEST) {
				httpInput = urlConnection.getErrorStream();
			} else if (responseCode != HttpURLConnection.HTTP_OK) {
				return Result.createHttpErrorResult(responseCode, urlConnection
						.getResponseMessage());
			} else {
				httpInput = urlConnection.getInputStream();
			}
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			if (mDebugMode) {
				String all = "";
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(httpInput));
				String response = reader.readLine();
				
				while (null != response) {
					all = all.concat(response + "\n");
					response = reader.readLine();
				}
				System.out.println(all);
				xpp.setInput(new StringReader(all));
			} else {
				xpp.setInput(httpInput, "utf-8");	
			}
			return Result.createOkResult(xpp);
		} catch (IOException e) {
			return Result.createRestErrorResult(Result.FAILURE, e.getMessage());
		} catch (XmlPullParserException e) {
			return Result.createRestErrorResult(Result.FAILURE, e.getMessage());
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
		/*if (proxy != null)
			urlConnection = (HttpURLConnection) u.openConnection(proxy);
		else*/
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
			builder.append(count % 2 == 0 ? string : StringUtilities.encode(string));
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

