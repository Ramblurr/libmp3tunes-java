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
