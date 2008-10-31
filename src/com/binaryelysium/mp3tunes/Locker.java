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
		locker,
		playlist,
		preferences
	};

	public Locker(String partnerToken, Session session )
	{
		if( mSession == null )
			throw ( new LockerException("Invalid Session"));
		mSession = session;
		mPartnerToken = partnerToken;
	}
	
	public Locker(String partnerToken, String username, String password )
	{
		mSession = Authenticator.getSession(partnerToken, "unnamedrambler@gmail.com", "br34nn4");
		if( mSession == null )
			throw ( new LockerException("Authentication failed"));
		mPartnerToken = partnerToken;
	}
	
	public long getLastUpdate(UpdateType type) throws LockerException
	{
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
						if(!stat.equals("1"))
							throw ( new LockerException("Getting last update failed"));
					} else if (name.equals("timestamp")) {
						return Long.parseLong(result.getParser().nextText());
					}
					break;
				}
				event = result.getParser().next();
			}
		} catch (Exception e) {
			throw ( new LockerException("Getting last update failed: " + e.getMessage()));
		}
		return 0;
	}
	
	public Collection<Artist> getArtists() throws LockerException
	{
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
						if( a!= null )
							artists.add(a);
					}
					break;
				case XmlPullParser.END_TAG:
					if(name.equals("artistList"))
						loop = false;
					break;
				}
				event = result.getParser().next();
			}
			return artists;
		} catch (Exception e) {
			throw ( new LockerException("Getting all artists failed: " + e.getMessage()));
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
