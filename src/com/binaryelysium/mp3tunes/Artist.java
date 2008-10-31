package com.binaryelysium.mp3tunes;

import org.xmlpull.v1.XmlPullParser;

public class Artist {
	int mId;
	String mName;
	int mTrackCount;
	int mAlbumCount;
	int mSize;

	public int getId() {
		return mId;
	}

	public String getName() {
		return mName;
	}

	public int getTrackCount() {
		return mTrackCount;
	}

	public int getAlbumCount() {
		return mAlbumCount;
	}

	public int getSize() {
		return mSize;
	}

	private Artist() {
	}

	public static Artist artistFromResult(Result result) {
		try {
			Artist a = new Artist();
			int event = result.getParser().nextTag();
			boolean loop = true;
			while (loop) {
				String name = result.getParser().getName();
				switch (event) {
				case XmlPullParser.START_TAG:
					if (name.equals("artistId")) {
						a.mId = Integer.parseInt(result.getParser().nextText());
					} else if (name.equals("artistSize")) {
						a.mSize = Integer.parseInt(result.getParser().nextText());
					} else if (name.equals("artistName")) {
						a.mName = result.getParser().nextText();
					} else if (name.equals("albumCount")) {
						a.mAlbumCount = Integer.parseInt(result.getParser().nextText());
					} else if (name.equals("trackCount")) {
						a.mTrackCount = Integer.parseInt(result.getParser().nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if (name.equals("item"))
						loop = false;
					break;
				}
				event = result.getParser().next();
			}
			return a;
		} catch (Exception e) {
		}
		return null;
	}

}
