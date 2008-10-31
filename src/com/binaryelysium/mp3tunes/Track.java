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

public class Track {
int mId;
String mTitle;
int mNumber;
long mDuration;
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

public static Track trackFromXPP(){
	return new Track();
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

public long getDuration() {
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
