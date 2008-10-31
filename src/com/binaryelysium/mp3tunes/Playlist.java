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

import java.util.Collection;

public class Playlist {
	int mId;
	String mName;
	String mFileName;
	int mCount;
	String mDateModified;
	int mSize;
	Collection<Track> mTracks;

	private Playlist() {}
	
	public static Playlist playlistFromXPP(){
		return new Playlist();
	}
	
	public static Playlist randomTracks()
	{
		return new Playlist();
	}
	
	public static Playlist newestTracks()
	{
		return new Playlist();
	}
	
	public static Playlist recentlyPlayed()
	{
		return new Playlist();
	}
	
	public int getId() {
		return mId;
	}

	public String getName() {
		return mName;
	}

	public String getFileName() {
		return mFileName;
	}

	public int getCount() {
		return mCount;
	}

	public String getDateModified() {
		return mDateModified;
	}

	public int getSize() {
		return mSize;
	}

	public Collection<Track> getTracks() {
		return mTracks;
	}

}
