package com.binaryelysium.mp3tunes.api;

import org.xmlpull.v1.XmlPullParser;

public class Token
{
    private String mToken;
    private int mCount;
    
    public Token( String token, int count )
    {
        mToken = token;
        mCount = count;
    }
    
    private Token()
    {
    }

    
    public String getToken()
    {
        return mToken;
    }

    
    public int getCount()
    {
        return mCount;
    }

    
    public void setToken( String token )
    {
        mToken = token;
    }

    
    public void setCount( int count )
    {
        mCount = count;
    }
    
    public static Token tokenFromResult( RestResult restResult )
    {
        try
        {
            Token t = new Token();
            int event = restResult.getParser().nextTag();
            boolean loop = true;
            while ( loop )
            {
                String name = restResult.getParser().getName();
                switch ( event )
                {
                case XmlPullParser.START_TAG:
                    if ( name.equals( "count" ) )
                    {
                        t.mCount = Integer.parseInt( restResult.getParser().nextText() );
                    }
                    else if ( name.equals( "token" ) )
                    {
                        t.mToken = restResult.getParser().nextText();
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

}
