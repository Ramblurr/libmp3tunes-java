package com.binaryelysium.mp3tunes.api.results;

import com.binaryelysium.mp3tunes.api.Album;
import com.binaryelysium.mp3tunes.api.Artist;
import com.binaryelysium.mp3tunes.api.Track;


public class SearchResult
{
    private String mType;
    
    private Artist[] mArtists;
    private Album[] mAlbums;
    private Track[] mTracks;
    
    private int mSet;
    private int mCount;
    private int mTotalResultSets;
    
    private int mTotalArtistResults;
    private int mTotalAlbumResults;
    private int mTotalTrackResults;

    public SearchResult()
    {
    }
    
    public String getType()
    {
        return mType;
    }
    
    public void setType( String type )
    {
        type = mType;
    }
    
    public int getSet()
    {
        return mSet;
    }

    
    public int getCount()
    {
        return mCount;
    }

    
    public int getTotalResultSets()
    {
        return mTotalResultSets;
    }
    
    public void setSet( int set )
    {
        mSet = set;
    }

    
    public void setCount( int count )
    {
        mCount = count;
    }

    
    public void setTotalResultSets( int totalResultSets )
    {
        mTotalResultSets = totalResultSets;
    }
    
    public Artist[] getArtists()
    {
        return mArtists;
    }
    
    public Album[] getAlbums()
    {
        return mAlbums;
    }
    
    public Track[] getTracks()
    {
        return mTracks;
    }
    
    public void setArtists( Artist[] artists )
    {
        mArtists = artists;
    }
    
    public void setAlbums( Album[] albums )
    {
        mAlbums = albums;
    }
    
    public void setTracks( Track[] track )
    {
        mTracks = track;
    }
    

    
    public int getTotalTrackResults()
    {
        return mTotalTrackResults;
    }

    
    public void setTotalTrackResults( int totalTrackResults )
    {
        mTotalTrackResults = totalTrackResults;
    }

    
    public int getTotalArtistResults()
    {
        return mTotalArtistResults;
    }

    
    public int getTotalAlbumResults()
    {
        return mTotalAlbumResults;
    }

    
    public void setTotalArtistResults( int totalArtistResults )
    {
        mTotalArtistResults = totalArtistResults;
    }

    
    public void setTotalAlbumResults( int totalAlbumResults )
    {
        mTotalAlbumResults = totalAlbumResults;
    }
}
