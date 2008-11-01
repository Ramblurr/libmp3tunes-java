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

import org.xmlpull.v1.XmlPullParser;

public class Artist {
	int mId;
	String mName;
	int mTrackCount;
	int mAlbumCount;
	int mSize;
	Collection<Album> mAlbums;

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
	
	public Collection<Album> getAlbums() {
		if (mAlbums == null) { // we need to fetch the tracks
			try {
				mAlbums = Locker
						.fetchAlbums(Integer.toString(this.mId), "", "");
			} catch (LockerException e) {
				mAlbums = new ArrayList<Album>();
			}
		}
		return mAlbums;
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
