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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

public class Locker
{

    Session mSession;
    static String mPartnerToken;

    public enum UpdateType
    {
        locker, playlist, preferences
    };

    public Locker( String partnerToken, Session session )
    {

        if ( mSession == null )
            throw ( new LockerException( "Invalid Session" ) );
        mSession = session;
        mPartnerToken = partnerToken;
    }

    public Locker( String partnerToken, String username, String password ) throws LockerException
    {

        try
        {
            mSession = Authenticator.getSession( partnerToken, username, password );
        }
        catch ( IOException e )
        {
            throw ( new LockerException( "connection issue" ) );
        }
        Caller.getInstance().setSession( mSession );
        if ( mSession == null )
            throw ( new LockerException( "connection issue" ) );
        mPartnerToken = partnerToken;
    }

    public long getLastUpdate( UpdateType type ) throws LockerException
    {

        String m = "lastUpdate";
        Map<String, String> params = new HashMap<String, String>();
        params.put( "type", type.toString() );
        try
        {
            Result result = Caller.getInstance().call( m, params );

            if ( !result.isSuccessful() )
                throw ( new LockerException( "Call Failed: " + result.getErrorMessage() ) );
            try
            {
                int event = result.getParser().nextTag();

                while ( event != XmlPullParser.END_DOCUMENT )
                {
                    String name = result.getParser().getName();
                    switch ( event )
                    {
                    case XmlPullParser.START_TAG:
                        if ( name.equals( "status" ) )
                        {
                            String stat = result.getParser().nextText();
                            if ( !stat.equals( "1" ) )
                                throw ( new LockerException( "Getting last update failed" ) );
                        }
                        else if ( name.equals( "timestamp" ) )
                        {
                            return Long.parseLong( result.getParser().nextText() );
                        }
                        break;
                    }
                    event = result.getParser().next();
                }
            }
            catch ( Exception e )
            {
                throw ( new LockerException( "Getting last update failed: " + e.getMessage() ) );
            }
        }
        catch ( IOException e )
        {
            throw ( new LockerException( "connection issue" ) );
        }
        return 0;
    }

    public Artist getArtist( int id ) throws NoSuchEntryException
    {

        Collection<Artist> list = fetchArtists( Integer.toString( id ) );
        if ( list.size() == 1 )
            return list.iterator().next();
        else
            throw ( new NoSuchEntryException( "No such artist w/ id " + id ) );

    }

    public Collection<Artist> getArtists() throws LockerException
    {

        return fetchArtists( "" );
    }

    private Collection<Artist> fetchArtists( String artistId ) throws LockerException
    {

        String m = "lockerData";
        Map<String, String> params = new HashMap<String, String>();
        params.put( "type", "artist" );
        if ( artistId != "" )
            params.put( "artist_id", artistId );
        try
        {
            System.out.println("Making GET ARTISTS call");
            Result result = Caller.getInstance().call( m, params );
            System.out.println("BACK FROM GET ARTISTS call");
            if ( !result.isSuccessful() )
                throw ( new LockerException( "Call Failed: " + result.getErrorMessage() ) );
            try
            {
                Collection<Artist> artists = new ArrayList<Artist>();
                int event = result.getParser().nextTag();
                boolean loop = true;
                while ( loop && event != XmlPullParser.END_DOCUMENT )
                {
                    System.out.println( "Looping for artists..." );
                    String name = result.getParser().getName();
                    switch ( event )
                    {
                    case XmlPullParser.START_TAG:
                        if ( name.equals( "item" ) )
                        {
                            Artist a = Artist.artistFromResult( result );
                            if ( a != null )
                                artists.add( a );
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ( name.equals( "artistList" ) )
                            loop = false;
                        break;
                    }
                    event = result.getParser().next();
                }
                return artists;
            }
            catch ( Exception e )
            {
                throw ( new LockerException( "Getting all artists failed: " + e.getMessage() ) );
            }
        }
        catch ( IOException e )
        {
            throw ( new LockerException( "connection issue" ) );
        }

    }

    public Album getAlbum( int id ) throws NoSuchEntryException
    {

        Collection<Album> list = fetchAlbums( "", "", Integer.toString( id ) );
        if ( list.size() == 1 )
            return list.iterator().next();
        else
            throw ( new NoSuchEntryException( "No such album w/ id " + id ) );
    }

    public Collection<Album> getAlbumsForArtist( int id ) throws LockerException
    {

        return fetchAlbums( Integer.toString( id ), "", "" );
    }

    public Collection<Album> getAlbumsforToken( String token ) throws LockerException
    {

        return fetchAlbums( "", token, "" );
    }

    public Collection<Album> getAlbums() throws LockerException
    {

        return fetchAlbums( "", "", "" );
    }

    protected static Collection<Album> fetchAlbums( String artistId, String token, String albumId )
            throws LockerException
    {

        String m = "lockerData";
        Map<String, String> params = new HashMap<String, String>();
        params.put( "type", "album" );
        if ( artistId != "" )
            params.put( "artist_id", artistId );
        if ( token != "" )
            params.put( "token", token );
        if ( albumId != "" )
            params.put( "album_id", albumId );
        try
        {
            Result result = Caller.getInstance().call( m, params );
            if ( !result.isSuccessful() )
                throw ( new LockerException( "Call Failed: " + result.getErrorMessage() ) );
            try
            {
                Collection<Album> albums = new ArrayList<Album>();
                int event = result.getParser().nextTag();
                boolean loop = true;
                while ( loop && event != XmlPullParser.END_DOCUMENT )
                {
                    String name = result.getParser().getName();
                    switch ( event )
                    {
                    case XmlPullParser.START_TAG:
                        if ( name.equals( "item" ) )
                        {
                            Album a = Album.albumFromResult( result );
                            if ( a != null )
                                albums.add( a );
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ( name.equals( "albumList" ) )
                            loop = false;
                        break;
                    }
                    event = result.getParser().next();
                }
                return albums;
            }
            catch ( Exception e )
            {
                throw ( new LockerException( "Getting albums failed: " + e.getMessage() ) );
            }
        }
        catch ( IOException e )
        {
            throw ( new LockerException( "connection issue" ) );
        }

    }

    public Collection<Track> getTracks() throws LockerException
    {

        return fetchTracks( "", "", "" );
    }

    public Collection<Track> getTracksForAlbum( int albumId ) throws LockerException
    {

        return fetchTracks( "", "", Integer.toString( albumId ) );
    }

    public Collection<Track> getTracksForArtist( int artistId ) throws LockerException
    {

        return fetchTracks( Integer.toString( artistId ), "", "" );
    }

    public Collection<Track> getTracksForToken( String token ) throws LockerException
    {

        return fetchTracks( "", token, "" );
    }

    protected static Collection<Track> fetchTracks( String artistId, String token, String albumId )
            throws LockerException
    {

        String m = "lockerData";
        Map<String, String> params = new HashMap<String, String>();
        params.put( "type", "track" );
        if ( artistId != "" )
            params.put( "artist_id", artistId );
        if ( token != "" )
            params.put( "token", token );
        if ( albumId != "" )
            params.put( "album_id", albumId );
        try
        {
            Result result = Caller.getInstance().call( m, params );
            if ( !result.isSuccessful() )
                throw ( new LockerException( "Call Failed: " + result.getErrorMessage() ) );
            try
            {
                Collection<Track> tracks = new ArrayList<Track>();
                int event = result.getParser().nextTag();
                boolean loop = true;
                while ( loop && event != XmlPullParser.END_DOCUMENT )
                {
                    String name = result.getParser().getName();
                    switch ( event )
                    {
                    case XmlPullParser.START_TAG:
                        if ( name.equals( "item" ) )
                        {
                            Track t = Track.trackFromResult( result, mPartnerToken );
                            if ( t != null )
                                tracks.add( t );
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ( name.equals( "trackList" ) )
                            loop = false;
                        break;
                    }
                    event = result.getParser().next();
                }
                return tracks;
            }
            catch ( Exception e )
            {
                throw ( new LockerException( "Getting albums failed: " + e.getMessage() ) );
            }
        }
        catch ( IOException e )
        {
            throw ( new LockerException( "connection issue" ) );
        }

    }
}
