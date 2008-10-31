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
