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

public class Artist {
	int mId;
	String mName;
	int mTrackCount;
	int mAlbumCount;
	int mSize;
	Album[] mAlbums;

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
	
	public Album[] getAlbums() {
		if (mAlbums == null) { // we need to fetch the tracks
			try {
				mAlbums = Locker
						.fetchAlbums(Integer.toString(this.mId), "", "", null).getData();
			} catch (LockerException e) {
				mAlbums = new Album[0];
			}
		}
		return mAlbums;
	}

	public static Artist artistFromResult(RestResult restResult) {
		try {
			Artist a = new Artist();
			int event = restResult.getParser().nextTag();
			boolean loop = true;
			while (loop) {
				String name = restResult.getParser().getName();
				switch (event) {
				case XmlPullParser.START_TAG:
					if (name.equals("artistId")) {
						a.mId = Integer.parseInt(restResult.getParser().nextText());
					} else if (name.equals("artistSize")) {
						a.mSize = Integer.parseInt(restResult.getParser().nextText());
					} else if (name.equals("artistName")) {
						a.mName = restResult.getParser().nextText();
					} else if (name.equals("albumCount")) {
						a.mAlbumCount = Integer.parseInt(restResult.getParser().nextText());
					} else if (name.equals("trackCount")) {
						a.mTrackCount = Integer.parseInt(restResult.getParser().nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if (name.equals("item"))
						loop = false;
					break;
				}
				event = restResult.getParser().next();
			}
			return a;
		} catch (Exception e) {
		}
		return null;
	}

}
