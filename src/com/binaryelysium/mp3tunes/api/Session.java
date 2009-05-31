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
	
	public static Session sessionFromResult(RestResult restResult) throws LockerException
	{
		try {
			if (restResult.getParser() == null)
				return null;

			if (!restResult.getParser().getName().equals("mp3tunes"))
				return null;
			Session s = new Session();
			int event = restResult.getParser().nextTag();
			boolean loop = true;
			while (loop && event != XmlPullParser.END_DOCUMENT) {
				String name = restResult.getParser().getName();
				switch (event) {
				case XmlPullParser.START_TAG:
					if (name.equals("status")) {
						String status = restResult.getParser().nextText();
						if(status.equals("0")) // authentication failed
						    throw (new LockerException("auth failure"));
					} else if (name.equals("username")) {
						s.mUsername = restResult.getParser().nextText();
					} else if (name.equals("session_id")) {
						s.mSessionId = restResult.getParser().nextText();
					}
					break;
				case XmlPullParser.END_TAG:
					if(name.equals("mp3tunes")) {
						loop = false;
						continue;
					}
				}
				event = restResult.getParser().nextTag();
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
