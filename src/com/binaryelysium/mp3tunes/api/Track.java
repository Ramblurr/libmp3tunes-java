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

public class Track
{

    private int mId;
    private String mTitle;
    int mNumber;
    double mDuration;
    String mFileName;
    String mFileKey;
    int mFileSize;
    String mDownloadUrl;
    String mPlayUrl;

    int mAlbumId;
    String mAlbumTitle;
    String mAlbumYear; // stored as a string cause we hardly use it as an int

    int mArtistId;
    String mArtistName;

    String mAlbumArt;

    private Track()
    {}

    public Track( int id, String play_url, String download_url, String title, int track,
            int artist_id, String artist_name, int album_id, String album_name, String cover_url )
    {
        mId = id;
        mPlayUrl = play_url;
        mDownloadUrl = download_url;
        mTitle = title;
        mNumber = track;
        mArtistId = artist_id;
        mArtistName = artist_name;
        mAlbumId = album_id;
        mAlbumTitle  = album_name;
        mAlbumArt = cover_url;
    }

    public static Track trackFromResult( RestResult restResult, String partner_token )
    {
        try
        {
            Track t = new Track();
            int event = restResult.getParser().nextTag();
            boolean loop = true;
            while ( loop )
            {
                String name = restResult.getParser().getName();
                switch ( event )
                {
                case XmlPullParser.START_TAG:
                    if ( name.equals( "trackId" ) )
                    {
                        t.mId = Integer.parseInt( restResult.getParser().nextText() );
                    }
                    else if ( name.equals( "trackFileSize" ) )
                    {
                        t.mFileSize = Integer.parseInt( restResult.getParser().nextText() );
                    }
                    else if ( name.equals( "trackTitle" ) )
                    {
                        t.mTitle = restResult.getParser().nextText();
                    }
                    else if ( name.equals( "trackFileName" ) )
                    {
                        t.mFileName = restResult.getParser().nextText();
                    }
                    else if ( name.equals( "trackFileKey" ) )
                    {
                        t.mFileKey = restResult.getParser().nextText();
                    }
                    else if ( name.equals( "trackNumber" ) )
                    {
                        t.mNumber = Integer.parseInt( restResult.getParser().nextText() );
                    }
                    else if ( name.equals( "trackLength" ) )
                    {
                        t.mDuration = Double.parseDouble( restResult.getParser().nextText() );
                    }
                    else if ( name.equals( "albumTitle" ) )
                    {
                        t.mAlbumTitle = restResult.getParser().nextText();
                    }
                    else if ( name.equals( "albumYear" ) )
                    {
                        t.mAlbumYear = restResult.getParser().nextText();
                    }
                    else if ( name.equals( "albumId" ) )
                    {
                        t.mAlbumId = Integer.parseInt( restResult.getParser().nextText() );
                    }
                    else if ( name.equals( "artistId" ) )
                    {
                        t.mArtistId = Integer.parseInt( restResult.getParser().nextText() );
                    }
                    else if ( name.equals( "artistName" ) )
                    {
                        t.mArtistName = restResult.getParser().nextText();
                    }
                    else if ( name.equals( "albumArtURL" ) )
                    {
                        t.mAlbumArt = restResult.getParser().nextText();
                    }
                    else if ( name.equals( "downloadURL" ) )
                    {
                        t.mDownloadUrl = restResult.getParser().nextText();
                        t.mDownloadUrl += partner_token;
                    }
                    else if ( name.equals( "playURL" ) )
                    {
                        t.mPlayUrl = restResult.getParser().nextText();
                        t.mPlayUrl += partner_token;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ( name.equals( "item" ) )
                        loop = false;
                    break;
                }
                event = restResult.getParser().next();
            }
            return t;
        }
        catch ( Exception e )
        {}
        return null;
    }

    public int getId()
    {
        return mId;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public int getNumber()
    {
        return mNumber;
    }

    public Double getDuration()
    {
        return mDuration;
    }

    public String getFileName()
    {
        return mFileName;
    }

    public String getFileKey()
    {
        return mFileKey;
    }

    public int getFileSize()
    {
        return mFileSize;
    }

    public String getDownloadUrl()
    {
        return mDownloadUrl;
    }

    public String getPlayUrl()
    {
        return mPlayUrl;
    }

    public int getAlbumId()
    {
        return mAlbumId;
    }

    public String getAlbumTitle()
    {
        return mAlbumTitle;
    }

    public String getAlbumYear()
    {
        return mAlbumYear;
    }

    public int getArtistId()
    {
        return mArtistId;
    }

    public String getArtistName()
    {
        return mArtistName;
    }

    public String getAlbumArt()
    {
        return mAlbumArt;
    }

}
