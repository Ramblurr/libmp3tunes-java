package com.binaryelysium.mp3tunes;

import java.util.HashMap;
import java.util.Map;


public class Authenticator {
	private Authenticator() {
	}
	/**
	 * Fetch a session key for a user.
	 *
	 * @param token A token returned by {@link #getToken(String)}
	 * @param apiKey A last.fm API key
	 * @param secret Your last.fm API secret
	 * @return a Session instance
	 * @see Session
	 */
	public static Session getSession(String token, String user, String password) {
		String m = "login";
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", user);
		params.put("partner_token", token);
		params.put("password", password);
		Caller.getInstance().setApiRootUrl(Caller.API_LOGIN);
		Result result = Caller.getInstance().call(m, params);
		Caller.getInstance().setApiRootUrl(Caller.API_GENERAL);
		return Session.sessionFromResult(result);
	}
}
