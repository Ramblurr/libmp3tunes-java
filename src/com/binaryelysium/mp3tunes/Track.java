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

public class Track {
int mId;
String mTitle;
int mNumber;
double mDuration;
String mFileName;
String mFileKey;
int mFileSize;
String mDownloadUrl;
String mPlayUrl;

int mAlbumId;
String mAlbumTitle;
String mAlbumYear; //stored as a string cause we hardly use it as an int

int mArtistId;
String mArtistName;

String mAlbumArt;


private Track(){}

public static Track trackFromResult(Result result){
	try {
		Track t = new Track();
		int event = result.getParser().nextTag();
		boolean loop = true;
		while (loop) {
			String name = result.getParser().getName();
			switch (event) {
			case XmlPullParser.START_TAG:
				if (name.equals("trackId")) {
					t.mId = Integer.parseInt(result.getParser().nextText());
				} else if (name.equals("trackFileSize")) {
					t.mFileSize = Integer.parseInt(result.getParser().nextText());
				} else if (name.equals("trackTitle")) {
					t.mTitle = result.getParser().nextText();
				} else if (name.equals("trackFileName")) {
					t.mFileName= result.getParser().nextText();
				} else if (name.equals("trackFileKey")) {
					t.mFileKey = result.getParser().nextText();
				} else if (name.equals("trackNumber")) {
					t.mNumber = Integer.parseInt(result.getParser().nextText());
				} else if (name.equals("trackLength")) {
					t.mDuration = Double.parseDouble(result.getParser().nextText());
				} else if (name.equals("albumTitle")) {
					t.mAlbumTitle = result.getParser().nextText();
				} else if (name.equals("albumYear")) {
					t.mAlbumYear = result.getParser().nextText();
				} else if (name.equals("albumId")) {
					t.mAlbumId = Integer.parseInt(result.getParser().nextText());
				} else if (name.equals("artistId")) {
					t.mArtistId = Integer.parseInt(result.getParser().nextText());
				} else if (name.equals("artistName")) {
					t.mArtistName = result.getParser().nextText();
				} else if (name.equals("albumArtURL")) {
					t.mAlbumArt = result.getParser().nextText();
				} else if (name.equals("downloadURL")) {
					t.mDownloadUrl = result.getParser().nextText();
				} else if (name.equals("playURL")) {
					t.mPlayUrl = result.getParser().nextText();
				}
				break;
			case XmlPullParser.END_TAG:
				if (name.equals("item"))
					loop = false;
				break;
			}
			event = result.getParser().next();
		}
		return t;
	} catch (Exception e) {
	}
	return null;
}

public int getId() {
	return mId;
}

public String getTitle() {
	return mTitle;
}

public int getNumber() {
	return mNumber;
}

public Double getDuration() {
	return mDuration;
}

public String getFileName() {
	return mFileName;
}

public String getFileKey() {
	return mFileKey;
}

public int getFileSize() {
	return mFileSize;
}

public String getDownloadUrl() {
	return mDownloadUrl;
}

public String getPlayUrl() {
	return mPlayUrl;
}

public int getAlbumId() {
	return mAlbumId;
}

public String getAlbumTitle() {
	return mAlbumTitle;
}

public String getAlbumYear() {
	return mAlbumYear;
}

public int getArtistId() {
	return mArtistId;
}

public String getArtistName() {
	return mArtistName;
}

public String getAlbumArt() {
	return mAlbumArt;
}

}
