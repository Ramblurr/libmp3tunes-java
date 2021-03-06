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
import java.util.HashMap;
import java.util.Map;


public class Authenticator {
	private Authenticator() {
	}
	/**
	 * Fetch a session key for a user.
	 *
	 * @param token the mp3tunes partner token
	 * @param user the username
	 * @param the user's password
	 * @return a Session instance
	 * @see Session
	 */
	public static Session getSession(String token, String user, String password) throws LockerException, IOException {
		String m = "login";
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", user);
		params.put("partner_token", token);
		params.put("password", password);
		Caller.getInstance().setApiRootUrl(Caller.API_LOGIN);
		RestResult restResult = Caller.getInstance().call(m, params);
		Caller.getInstance().setApiRootUrl(Caller.API_GENERAL);
		return Session.sessionFromResult(restResult);
	}
}
