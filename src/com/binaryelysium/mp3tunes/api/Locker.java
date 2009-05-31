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

import com.binaryelysium.mp3tunes.api.results.DataResult;
import com.binaryelysium.mp3tunes.api.results.SetResult;

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
            RestResult restResult = Caller.getInstance().call( m, params );

            if ( !restResult.isSuccessful() )
                throw ( new LockerException( "Call Failed: " + restResult.getErrorMessage() ) );
            try
            {
                int event = restResult.getParser().nextTag();

                while ( event != XmlPullParser.END_DOCUMENT )
                {
                    String name = restResult.getParser().getName();
                    switch ( event )
                    {
                    case XmlPullParser.START_TAG:
                        if ( name.equals( "status" ) )
                        {
                            String stat = restResult.getParser().nextText();
                            if ( !stat.equals( "1" ) )
                                throw ( new LockerException( "Getting last update failed" ) );
                        }
                        else if ( name.equals( "timestamp" ) )
                        {
                            return Long.parseLong( restResult.getParser().nextText() );
                        }
                        break;
                    }
                    event = restResult.getParser().next();
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
        DataResult<Artist> res = fetchArtists( Integer.toString( id ), null );
        if ( res.getData().size() == 1 )
            return res.getData().iterator().next();
        else
            throw ( new NoSuchEntryException( "No such artist w/ id " + id ) );

    }

    public DataResult<Artist> getArtists() throws LockerException
    {

        return fetchArtists( "", null );
    }
    
    /**
     * Fetch a subset of artists 
     * 
     * @param count maximum number of results to be returned in a result set
     * @param set the result set number to retrieve, note that set numbers are 0 based
     * @return a list of count artists  
     * @throws LockerException
     */
    public SetResult<Artist> getArtistsSet(int count, int set) throws LockerException
    {

        return (SetResult<Artist>) fetchArtists( "", new SetQuery( count, set ) );
    }
    
    private static DataResult<Artist> fetchArtists( String artistId, SetQuery setQuery ) throws LockerException
    {
        String m = "lockerData";
        Map<String, String> params = new HashMap<String, String>();
        params.put( "type", "artist" );
        if ( artistId != "" )
            params.put( "artist_id", artistId );
        else if ( setQuery != null )
        {
            params.put( "count", setQuery.count );
            params.put( "set", setQuery.set );
        }
        try
        {
            System.out.println("Making GET ARTISTS call");
            RestResult restResult = Caller.getInstance().call( m, params );
            System.out.println("BACK FROM GET ARTISTS call");
            if ( !restResult.isSuccessful() )
                throw ( new LockerException( "Call Failed: " + restResult.getErrorMessage() ) );
            
            DataResult<Artist> results = null;
            if(setQuery != null )
                results = parseSetSummary(restResult);
            
            Collection<Artist> artists = parseArtists(restResult);
            if(results == null)
                results = new DataResult<Artist>("artist", artists.size());
            results.setData( artists );
            return results;
        }
        catch ( IOException e )
        {
            throw ( new LockerException( "connection issue" ) );
        }
    }

    private static Collection<Artist> parseArtists( RestResult restResult ) throws LockerException
    {
            try
            {
                Collection<Artist> artists = new ArrayList<Artist>();
                int event = restResult.getParser().nextTag();
                boolean loop = true;
                while ( loop && event != XmlPullParser.END_DOCUMENT )
                {
                    String name = restResult.getParser().getName();
                    switch ( event )
                    {
                    case XmlPullParser.START_TAG:
                        if ( name.equals( "item" ) )
                        {
                            Artist a = Artist.artistFromResult( restResult );
                            if ( a != null )
                                artists.add( a );
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ( name.equals( "artistList" ) )
                            loop = false;
                        break;
                    }
                    event = restResult.getParser().next();
                }
                return artists;
            }
            catch ( Exception e )
            {
                throw ( new LockerException( "Getting all artists failed: " + e.getMessage() ) );
            }


    }

    public Album getAlbum( int id ) throws NoSuchEntryException
    {

        Collection<Album> list = fetchAlbums( "", "", Integer.toString( id ), null ).getData();
        if ( list.size() == 1 )
            return list.iterator().next();
        else
            throw ( new NoSuchEntryException( "No such album w/ id " + id ) );
    }

    public DataResult<Album> getAlbumsForArtist( int id ) throws LockerException
    {

        return fetchAlbums( Integer.toString( id ), "", "", null );
    }

    public DataResult<Album> getAlbumsforToken( String token ) throws LockerException
    {

        return fetchAlbums( "", token, "", null );
    }
    
    public SetResult<Album> getAlbumsSet(int count, int set) throws LockerException
    {

        return (SetResult<Album>) fetchAlbums( "", "", "", new SetQuery( count, set ) );
    }

    public DataResult<Album> getAlbums() throws LockerException
    {

        return fetchAlbums( "", "", "", null );
    }
    

    protected static DataResult<Album> fetchAlbums( String artistId, String token, String albumId, SetQuery setQuery )
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
        if ( setQuery != null )
        {
            params.put( "count", setQuery.count );
            params.put( "set", setQuery.set );
        }
        try
        {
            RestResult restResult = Caller.getInstance().call( m, params );
            if ( !restResult.isSuccessful() )
                throw ( new LockerException( "Call Failed: " + restResult.getErrorMessage() ) );
            
            DataResult<Album> results = null;
            if(setQuery != null )
                results = parseSetSummary(restResult);
            
            Collection<Album> albums = parseAlbums(restResult);
            if(results == null)
                results = new DataResult<Album>("album", albums.size());
            results.setData( albums );
            return results;
        }
        catch ( IOException e )
        {
            throw ( new LockerException( "connection issue" ) );
        }

    }
    
    private static Collection<Album> parseAlbums( RestResult restResult ) throws LockerException
    {
        try
        {
            Collection<Album> albums = new ArrayList<Album>();
            int event = restResult.getParser().nextTag();
            boolean loop = true;
            while ( loop && event != XmlPullParser.END_DOCUMENT )
            {
                String name = restResult.getParser().getName();
                switch ( event )
                {
                case XmlPullParser.START_TAG:
                    if ( name.equals( "item" ) )
                    {
                        Album a = Album.albumFromResult( restResult );
                        if ( a != null )
                            albums.add( a );
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ( name.equals( "albumList" ) )
                        loop = false;
                    break;
                }
                event = restResult.getParser().next();
            }
            return albums;
        }
        catch ( Exception e )
        {
            throw ( new LockerException( "Getting albums failed: " + e.getMessage() ) );
        }
    }

    public DataResult<Track> getTracks() throws LockerException
    {

        return fetchTracks( "", "", "", "", null );
    }

    public DataResult<Track> getTracksForAlbum( int albumId ) throws LockerException
    {

        return fetchTracks( "", "", Integer.toString( albumId ), "", null );
    }

    public DataResult<Track> getTracksForArtist( int artistId ) throws LockerException
    {

        return fetchTracks( Integer.toString( artistId ), "", "", "", null );
    }

    public DataResult<Track> getTracksForToken( String token ) throws LockerException
    {

        return fetchTracks( "", token, "", "", null );
    }
    
    public DataResult<Track> getTracksForPlaylist( int playlistId) throws LockerException
    {

        return fetchTracks( "", "", "", Integer.toString(playlistId), null );
    }
    
    public SetResult<Track> getTracksSet( int count, int set )
    {
        return ( SetResult<Track> ) fetchTracks( "", "", "", "", new SetQuery( count, set ) );
    }

    protected static DataResult<Track> fetchTracks( String artistId, String token, String albumId, String playlistId, SetQuery setQuery )
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
        if ( playlistId != "")
            params.put( "playlist_id", playlistId);
        if ( setQuery != null )
        {
            params.put( "count", setQuery.count );
            params.put( "set", setQuery.set );
        }
        try
        {
            RestResult restResult = Caller.getInstance().call( m, params );
            if ( !restResult.isSuccessful() )
                throw ( new LockerException( "Call Failed: " + restResult.getErrorMessage() ) );
            
            //NOTE: when set and type=track are passed the tracklist comes BEFORE the set summary
            // Hence why this parseTracks call is before the parseSetSummary
            Collection<Track> tracks = parseTracks(restResult);
            
            DataResult<Track> results = null;
            if(setQuery != null )
                results = parseSetSummary(restResult);
            
            
            if(results == null)
                results = new DataResult<Track>("track", tracks.size());
            results.setData( tracks );
            return results;
        }
        catch ( IOException e )
        {
            throw ( new LockerException( "connection issue" ) );
        }

    }
    
    private static Collection<Track> parseTracks( RestResult restResult ) throws LockerException
    {
        try
        {
            Collection<Track> tracks = new ArrayList<Track>();
            int event = restResult.getParser().nextTag();
            boolean loop = true;
            while ( loop && event != XmlPullParser.END_DOCUMENT )
            {
                String name = restResult.getParser().getName();
                switch ( event )
                {
                case XmlPullParser.START_TAG:
                    if ( name.equals( "item" ) )
                    {
                        Track t = Track.trackFromResult( restResult, mPartnerToken );
                        if ( t != null )
                            tracks.add( t );
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ( name.equals( "trackList" ) )
                        loop = false;
                    break;
                }
                event = restResult.getParser().next();
            }
            return tracks;
        }
        catch ( Exception e )
        {
            throw ( new LockerException( "Getting albums failed: " + e.getMessage() ) );
        }
    }
    
    public Collection<Playlist> getPlaylists() throws LockerException
    {

        String m = "lockerData";
        Map<String, String> params = new HashMap<String, String>();
        params.put( "type", "playlist" );
        try
        {
            RestResult restResult = Caller.getInstance().call( m, params );
            if ( !restResult.isSuccessful() )
                throw ( new LockerException( "Call Failed: " + restResult.getErrorMessage() ) );
            try
            {
                Collection<Playlist> playlists = new ArrayList<Playlist>();
                int event = restResult.getParser().nextTag();
                boolean loop = true;
                while ( loop && event != XmlPullParser.END_DOCUMENT )
                {
                    String name = restResult.getParser().getName();
                    switch ( event )
                    {
                    case XmlPullParser.START_TAG:
                        if ( name.equals( "item" ) )
                        {
                            Playlist p = Playlist.playlistFromResult( restResult );
                            if ( p != null )
                                playlists.add( p );
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ( name.equals( "playlistList" ) )
                            loop = false;
                        break;
                    }
                    event = restResult.getParser().next();
                }
                return playlists;
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
    
    private static <E> SetResult<E> parseSetSummary(RestResult restResult)
    {
        try
        {
            SetResult<E> result = new SetResult<E>();
            int event = restResult.getParser().nextTag();
            boolean loop = true;
            while ( loop && event != XmlPullParser.END_DOCUMENT )
            {
                String name = restResult.getParser().getName();
                switch ( event )
                {
                case XmlPullParser.START_TAG:
                    if ( name.equals( "type" ) )
                    {
                        result.setType( restResult.getParser().nextText());
                    }
                    else if ( name.equals( "totalResults" ) )
                    {
                        result.setTotalResults( Integer.parseInt(restResult.getParser().nextText() ) );
                    }
                    else if ( name.equals( "set" ) )
                    {
                        result.setSet( Integer.parseInt(restResult.getParser().nextText() ) );
                    }
                    else if ( name.equals( "count" ) )
                    {
                        result.setCount( Integer.parseInt(restResult.getParser().nextText() ) );
                    }
                    else if ( name.equals( "totalResultSets" ) )
                    {
                        result.setTotalResultSets( Integer.parseInt(restResult.getParser().nextText() ) );
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ( name.equals( "summary" ) )
                        loop = false;
                    break;
                }
                event = restResult.getParser().next();
            }
            return result;
        }
        catch ( Exception e )
        {
            throw ( new LockerException( "Getting set summary failed: " + e.getMessage() ) );
        }
    }
    
    private class SetQuery {
        String count;
        String set;
        public SetQuery( int count, int set )
        {
            this.count = Integer.toString( count );
            this.set = Integer.toString( set );
        }
        
    }

}
