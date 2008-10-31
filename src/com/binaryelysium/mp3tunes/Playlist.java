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
