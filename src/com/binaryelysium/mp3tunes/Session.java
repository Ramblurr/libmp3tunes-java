/*
 * Copyright (C) 2008 Casey Link <unnamedrambler@gmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

package com.binaryelysium.mp3tunes;


import org.xmlpull.v1.XmlPullParser;

public class Session {
	String mUsername;
	String mSessionId;

	public Session(String user, String session) {
		mUsername = user;
		mSessionId = session;
	}

	public Session() {
	}
	
	public static Session sessionFromResult(Result result)
	{
		try {
			if (result.getParser() == null)
				return null;

			if (!result.getParser().getName().equals("mp3tunes"))
				return null;
			Session s = new Session();
			int event = result.getParser().nextTag();
			boolean loop = true;
			while (loop && event != XmlPullParser.END_DOCUMENT) {
				String name = result.getParser().getName();
				switch (event) {
				case XmlPullParser.START_TAG:
					if (name.equals("status")) {
						String status = result.getParser().nextText();
						if(status.equals("0")) // authentication failed
							return null;
					} else if (name.equals("username")) {
						s.mUsername = result.getParser().nextText();
					} else if (name.equals("session_id")) {
						s.mSessionId = result.getParser().nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					if(name.equals("mp3tunes")) {
						loop = false;
						continue;
					}
				}
				event = result.getParser().nextTag();
			}
			return s;
		} catch (Exception e) {
		}
		return null;
	}

	public String getUsername() {
		return mUsername;
	}

	public String getSessionId() {
		return mSessionId;
	}

	public void setUsername(String username) {
		mUsername = username;
	}

	public void setSessionId(String sessionId) {
		mSessionId = sessionId;
	}

}
