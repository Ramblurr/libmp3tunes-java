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

import java.util.Collection;

import org.xmlpull.v1.XmlPullParser;

public class Playlist {
    String mId;
	String mName;
	String mFileName;
	int mCount;
	String mDateModified;
	int mSize;
	Collection<Track> mTracks;
	
	public final static String RANDOM_TRACKS = "RANDOM_TRACKS";
	
    public final static String NEWEST_TRACKS = "NEWEST_TRACKS";
    
    public final static String RECENTLY_PLAYED = "RECENTLY_PLAYED";
    
    public final static String INBOX = "INBOX";


	private Playlist() {}
	
	public Playlist( String id, String name, String fileName, int count, String dateModified, int size )
    {
        mId = id;
        mName = name;
        mFileName = fileName;
        mCount = count;
        mDateModified = dateModified;
        mSize = size;
    }

	public static Playlist playlistFromResult( RestResult restResult )
    {
        try
        {
            Playlist p = new Playlist();
            int event = restResult.getParser().nextTag();
            boolean loop = true;
            while ( loop )
            {
                String name = restResult.getParser().getName();
                switch ( event )
                {
                case XmlPullParser.START_TAG:
                    if ( name.equals( "playlistId" ) )
                    {
                        p.mId  = restResult.getParser().nextText();
//                        System.out.println("PLAYLIST: " + id);
//                        if( id.equals( RANDOM_TRACKS ) )
//                            p.mId = RANDOM_TRACKS_ID;
//                        else if (id.equals( RECENTLY_PLAYED ) )
//                        {
//                            p.mId = RECENTLY_PLAYED_ID;
//                            System.out.println("GOT RECENTLY_PLAYED");
//                        }
//                        else if ( id.equals( NEWEST_TRACKS ))
//                            p.mId = NEWEST_TRACKS_ID;
//                        else
//                            p.mId = Integer.parseInt( id );
                    }
                    else if ( name.equals( "playlistTitle" ) )
                    {
                        p.mName = restResult.getParser().nextText();
                    }
                    else if ( name.equals( "fileName" ) )
                    {
                        p.mFileName = restResult.getParser().nextText();
                    }
                    else if ( name.equals( "fileCount" ) )
                    {
                        p.mCount = Integer.parseInt( restResult.getParser().nextText() );
                    }
//                    else if ( name.equals( "playlistSize" ) )
//                    {
//                        p.mSize = Integer.parseInt( restResult.getParser().nextText() );
//                    }
//                    else if ( name.equals( "dateModified" ) )
//                    {
//                        p.mDateModified = result.getParser().nextText();
//                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ( name.equals( "item" ) )
                        loop = false;
                    break;
                }
                event = restResult.getParser().next();
            }
            return p;
        }
        catch ( Exception e )
        {}
        return null;
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
	
	public String getId() {
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
