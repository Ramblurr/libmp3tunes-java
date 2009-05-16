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

import java.util.ArrayList;
import java.util.Collection;

import org.xmlpull.v1.XmlPullParser;

public class Album {
	int mId;
	String mName;
	String mYear;
	int mTrackCount;
	int mSize;
	String mReleaseDate;
	String mPurhaseDate;
	int mHasArt;
	int mArtistId;
	String mArtistName;
	Collection<Track> mTracks;

	private Album() {
	}

	public int getId() {
		return mId;
	}

	public String getName() {
		return mName;
	}

	public String getYear() {
		return mYear;
	}

	public int getTrackCount() {
		return mTrackCount;
	}

	public int getSize() {
		return mSize;
	}

	public String getReleaseDate() {
		return mReleaseDate;
	}

	public String getPurhaseDate() {
		return mPurhaseDate;
	}

	public int getHasArt() {
		return mHasArt;
	}

	public int getArtistId() {
		return mArtistId;
	}

	public String getArtistName() {
		return mArtistName;
	}

	public Collection<Track> getTracks() {
		if (mTracks == null) { // we need to fetch the tracks
			try {
				mTracks = Locker
						.fetchTracks("", "", Integer.toString(this.mId));
			} catch (LockerException e) {
				mTracks = new ArrayList<Track>();
			}
		}
		return mTracks;
	}

	public static Album albumFromResult(Result result) {
		try {
			Album a = new Album();
			int event = result.getParser().nextTag();
			boolean loop = true;
			while (loop) {
				String name = result.getParser().getName();
				switch (event) {
				case XmlPullParser.START_TAG:
					if (name.equals("albumId")) {
						a.mId = Integer.parseInt(result.getParser().nextText());
					} else if (name.equals("albumSize")) {
						a.mSize = Integer.parseInt(result.getParser()
								.nextText());
					} else if (name.equals("albumTitle")) {
						a.mName = result.getParser().nextText();
					} else if (name.equals("artistId")) {
						a.mArtistId = Integer.parseInt(result.getParser()
								.nextText());
					} else if (name.equals("trackCount")) {
						a.mTrackCount = Integer.parseInt(result.getParser()
								.nextText());
					} else if (name.equals("artistName")) {
						a.mArtistName = result.getParser().nextText();
					} else if (name.equals("hasArt")) {
						a.mHasArt = Integer.parseInt(result.getParser()
								.nextText());
					} else if (name.equals("purchaseDate")) {
						a.mPurhaseDate = result.getParser().nextText();
					} else if (name.equals("releaseDate")) {
						a.mReleaseDate = result.getParser().nextText();
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
