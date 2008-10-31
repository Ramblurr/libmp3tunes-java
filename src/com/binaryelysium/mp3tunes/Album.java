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

public class Album {
	int mId;
	String mTitle;
	String mYear; 
	int mTrackCount;
	int mSize;
	String mReleaseDate;
	String mPurhaseDate;
	int mHasArt;
	int mArtistId;
	String mArtistName;
	
	private Album(){}
	
	public static Album albumFromXPP()
	{
		return new Album();
	}
	
	public int getId() {
		return mId;
	}
	public String getTitle() {
		return mTitle;
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
	
}
