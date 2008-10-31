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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

public class Locker {
	Session mSession;
	String mPartnerToken;

	public enum UpdateType {
		locker, playlist, preferences
	};

	public Locker(String partnerToken, Session session) {
		if (mSession == null)
			throw (new LockerException("Invalid Session"));
		mSession = session;
		mPartnerToken = partnerToken;
	}

	public Locker(String partnerToken, String username, String password) {
		mSession = Authenticator.getSession(partnerToken,
				"unnamedrambler@gmail.com", "br34nn4");
		if (mSession == null)
			throw (new LockerException("Authentication failed"));
		mPartnerToken = partnerToken;
	}

	public long getLastUpdate(UpdateType type) throws LockerException {
		String m = "lastUpdate";
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", type.toString());
		Result result = Caller.getInstance().call(m, mSession, params);
		try {
			int event = result.getParser().nextTag();

			while (event != XmlPullParser.END_DOCUMENT) {
				String name = result.getParser().getName();
				switch (event) {
				case XmlPullParser.START_TAG:
					if (name.equals("status")) {
						String stat = result.getParser().nextText();
						if (!stat.equals("1"))
							throw (new LockerException(
									"Getting last update failed"));
					} else if (name.equals("timestamp")) {
						return Long.parseLong(result.getParser().nextText());
					}
					break;
				}
				event = result.getParser().next();
			}
		} catch (Exception e) {
			throw (new LockerException("Getting last update failed: "
					+ e.getMessage()));
		}
		return 0;
	}

	public Collection<Artist> getArtists() throws LockerException {
		String m = "lockerData";
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", "artist");
		Result result = Caller.getInstance().call(m, mSession, params);
		try {
			Collection<Artist> artists = new ArrayList<Artist>();
			int event = result.getParser().nextTag();
			boolean loop = true;
			while (loop && event != XmlPullParser.END_DOCUMENT) {
				String name = result.getParser().getName();
				switch (event) {
				case XmlPullParser.START_TAG:
					if (name.equals("item")) {
						Artist a = Artist.artistFromResult(result);
						if (a != null)
							artists.add(a);
					}
					break;
				case XmlPullParser.END_TAG:
					if (name.equals("artistList"))
						loop = false;
					break;
				}
				event = result.getParser().next();
			}
			return artists;
		} catch (Exception e) {
			throw (new LockerException("Getting all artists failed: "
					+ e.getMessage()));
		}

	}

	public Collection<Album> getAlbumsForArtist(int id) throws LockerException {
		return getAlbums(Integer.toString(id), "");
	}

	public Collection<Album> getAlbumsByToken(String token)
			throws LockerException {
		return getAlbums("", token);
	}

	public Collection<Album> getAlbums() throws LockerException {
		return getAlbums("", "");
	}

	private Collection<Album> getAlbums(String artistId, String token)
			throws LockerException {
		String m = "lockerData";
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", "album");
		if (artistId != "")
			params.put("artist_id", artistId);
		if (token != "")
			params.put("token", token);

		Result result = Caller.getInstance().call(m, mSession, params);
		try {
			Collection<Album> albums = new ArrayList<Album>();
			int event = result.getParser().nextTag();
			boolean loop = true;
			while (loop && event != XmlPullParser.END_DOCUMENT) {
				String name = result.getParser().getName();
				switch (event) {
				case XmlPullParser.START_TAG:
					if (name.equals("item")) {
						Album a = Album.albumFromResult(result);
						if (a != null)
							albums.add(a);
					}
					break;
				case XmlPullParser.END_TAG:
					if (name.equals("albumList"))
						loop = false;
					break;
				}
				event = result.getParser().next();
			}
			return albums;
		} catch (Exception e) {
			throw (new LockerException("Getting albums failed: "
					+ e.getMessage()));
		}

	}

	@SuppressWarnings("serial")
	public class LockerException extends RuntimeException {

		public LockerException() {
		}

		public LockerException(Throwable cause) {
			super(cause);
		}

		public LockerException(String message) {
			super(message);
		}

		public LockerException(String message, Throwable cause) {
			super(message, cause);
		}
	}
}
