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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import com.binaryelysium.mp3tunes.api.results.DataResult;
import com.binaryelysium.mp3tunes.api.results.SearchResult;
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
        if ( res.getData().length == 1 )
            return res.getData()[0];
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
            
            Artist[] artists = parseArtists(restResult);
            if(results == null)
                results = new DataResult<Artist>("artist", artists.length);
            results.setData( artists );
            return results;
        }
        catch ( IOException e )
        {
            throw ( new LockerException( "connection issue" ) );
        }
    }

    private static Artist[] parseArtists( RestResult restResult ) throws LockerException
    {
            try
            {
                List<Artist> artists = new ArrayList<Artist>();
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
                return artists.toArray(new Artist[artists.size()]);
            }
            catch ( Exception e )
            {
                throw ( new LockerException( "Getting all artists failed: " + e.getMessage() ) );
            }


    }

    public Album getAlbum( int id ) throws NoSuchEntryException
    {

        Album[] list = fetchAlbums( "", "", Integer.toString( id ), null ).getData();
        if ( list.length == 1 )
            return list[0];
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
            
            Album[] albums = parseAlbums(restResult);
            if(results == null)
                results = new DataResult<Album>("album", albums.length);
            results.setData( albums );
            return results;
        }
        catch ( IOException e )
        {
            throw ( new LockerException( "connection issue" ) );
        }

    }
    
    private static Album[] parseAlbums( RestResult restResult ) throws LockerException
    {
        try
        {
            List<Album> albums = new ArrayList<Album>();
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
            return albums.toArray(new Album[albums.size()]);
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
            Track[] tracks = parseTracks( restResult );
            
            DataResult<Track> results = null;
            if(setQuery != null )
                results = parseSetSummary( restResult );
            
            
            if(results == null)
                results = new DataResult<Track>( "track", tracks.length );
            results.setData( tracks );
            return results;
        }
        catch ( IOException e )
        {
            throw ( new LockerException( "connection issue" ) );
        }

    }
    
    private static Track[] parseTracks( RestResult restResult ) throws LockerException
    {
        try
        {
            List<Track> tracks = new ArrayList<Track>();
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
            return tracks.toArray(new Track[tracks.size()]);
        }
        catch ( Exception e )
        {
            throw ( new LockerException( "Getting albums failed: " + e.getMessage() ) );
        }
    }
    
    public DataResult<Playlist> getPlaylists() throws LockerException
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
                List<Playlist> playlists = new ArrayList<Playlist>();
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
                
                DataResult<Playlist> results = new DataResult<Playlist>( "playlist", playlists.size() );
                results.setData( playlists.toArray(new Playlist[playlists.size()]) );
                return results;
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
    
    public DataResult<Token> getArtistTokens() throws LockerException
    {
        return fetchTokens("artist");
    }
    
    public DataResult<Token> getAlbumTokens() throws LockerException
    {
        return fetchTokens("album");
    }
    
    public DataResult<Token> getTrackTokens() throws LockerException
    {
        return fetchTokens("track");
    }
    
    protected static DataResult<Token> fetchTokens( String type )throws LockerException
    {
        String m = "lockerData";
        Map<String, String> params = new HashMap<String, String>();
        params.put( "type", type+"_token" );
        try
        {
            RestResult restResult = Caller.getInstance().call( m, params );
            if ( !restResult.isSuccessful() )
                throw ( new LockerException( "Call Failed: " + restResult.getErrorMessage() ) );
            List<Token> tokens = new ArrayList<Token>();
            try
            {
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
                            Token t = Token.tokenFromResult( restResult );
                            if ( t != null )
                                tokens.add( t );
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ( name.equals( "tokenList" ) )
                            loop = false;
                        break;
                    }
                    event = restResult.getParser().next();
                }
            }
            catch ( Exception e )
            {
                throw ( new LockerException( "Getting albums failed: " + e.getMessage() ) );
            }
         
            
            DataResult<Token> results = new DataResult<Token>( type+"_token", tokens.size() );
            results.setData( tokens.toArray(new Token[tokens.size()]) );
            return results;
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
    
    public SearchResult search(String query, boolean artist, boolean album, boolean track, int count, int set )
    {
        if( !artist && !album && !track )
            return null;
        String m = "lockerSearch";
        Map<String, String> params = new HashMap<String, String>();
        
        params.put( "s", query );
        String type = "";
        if ( artist )
            type += "artist,";
        if ( album)
            type += "album,";
        if ( track )
            type += "track,";
        type = type.substring( 0, type.length()-1 ); // remove trailing comma
        params.put( "type", type );
        
        if ( count > 0 )
            params.put( "count", Integer.toString( count ) );
        if ( set >= 0 )
            params.put( "set", Integer.toString( set ) );

        try
        {
            RestResult restResult = Caller.getInstance().call( m, params );
            if ( !restResult.isSuccessful() )
                throw ( new LockerException( "Call Failed: " + restResult.getErrorMessage() ) );
            
            SearchResult result = parseSearchSummary( restResult );
            
            if( artist )
                result.setArtists( parseArtists( restResult ) );
            if( album )
                result.setAlbums( parseAlbums( restResult ) );
            if( track )
                result.setTracks( parseTracks( restResult ) );

            return result;
        }
        catch ( IOException e )
        {
            throw ( new LockerException( "connection issue" ) );
        }
    }
    
    private static SearchResult parseSearchSummary(RestResult restResult)
    {
        try
        {
            SearchResult result = new SearchResult();
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
                    else if ( name.equals( "artist" ) )
                    {
                        result.setTotalArtistResults( ( Integer.parseInt(restResult.getParser().nextText() ) ) );
                    }
                    else if ( name.equals( "album" ) )
                    {
                        result.setTotalAlbumResults( ( Integer.parseInt(restResult.getParser().nextText() ) ) );
                    }
                    else if ( name.equals( "track" ) )
                    {
                        result.setTotalTrackResults( ( Integer.parseInt(restResult.getParser().nextText() ) ) );
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
    

}
