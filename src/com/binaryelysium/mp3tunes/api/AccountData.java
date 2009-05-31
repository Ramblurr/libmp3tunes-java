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

public class AccountData
{

    String mEmail;
    String mNickName;
    String mFirstName;
    String mLastName;
    long mMaxLockerSize;
    long mCurrentLockerSize;
    long mMaxFileSize;
    String mLockerType;
    boolean mExpired;
    Collection<Subscription> mSubscriptions = new ArrayList<Subscription>();

    public class Subscription
    {

        public String mName;
        public String mDescription;
        public String mActivateDate;
        public String mExpireDate;

        private Subscription()
        {

        }
    }

    private AccountData()
    {

    }

    private Subscription subscriptionFromResult( RestResult restResult )
    {

        try
        {
            if ( !restResult.getParser().getName().equals( "item" ) )
                return null;

            Subscription s = new Subscription();
            int event = restResult.getParser().nextTag();
            boolean loop = true;
            while ( loop )
            {
                String name = restResult.getParser().getName();
                switch ( event )
                {
                case XmlPullParser.START_TAG:
                    if ( name.equals( "name" ) )
                    {
                        s.mName = restResult.getParser().nextText();
                    }
                    else if ( name.equals( "expireDate" ) )
                    {
                        s.mExpireDate = restResult.getParser().nextText();
                    }
                    else if ( name.equals( "description" ) )
                    {
                        s.mDescription = restResult.getParser().nextText();
                    }
                    else if ( name.equals( "activateDate" ) )
                    {
                        s.mActivateDate = restResult.getParser().nextText();
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ( name.equals( "item" ) )
                        loop = false;
                    break;
                }
                event = restResult.getParser().next();
            }
            return s;
        }
        catch ( Exception e )
        {
        }
        return null;
    }

    public static AccountData accountDataFromResult( RestResult restResult )
    {

        try
        {
            restResult.getParser().nextTag();
            if ( !restResult.getParser().getName().equals( "user" ) )
                return null;

            AccountData d = new AccountData();
            int event = restResult.getParser().nextTag();
            boolean loop = true;
            while ( loop )
            {
                String name = restResult.getParser().getName();
                switch ( event )
                {
                case XmlPullParser.START_TAG:
                    if ( name.equals( "email" ) )
                    {
                        d.mEmail = restResult.getParser().nextText();
                    }
                    else if ( name.equals( "nickName" ) )
                    {
                        d.mNickName = restResult.getParser().nextText();
                    }
                    else if ( name.equals( "firstName" ) )
                    {
                        d.mFirstName = restResult.getParser().nextText();
                    }
                    else if ( name.equals( "lastName" ) )
                    {
                        d.mLastName = restResult.getParser().nextText();
                    }
                    else if ( name.equals( "maxLockerSize" ) )
                    {
                        d.mMaxLockerSize = Long.parseLong( restResult.getParser().nextText() );
                    }
                    else if ( name.equals( "currentLockerSize" ) )
                    {
                        d.mCurrentLockerSize = Long.parseLong( restResult.getParser().nextText() );
                    }
                    else if ( name.equals( "maxFileSize" ) )
                    {
                        d.mMaxFileSize = Long.parseLong( restResult.getParser().nextText() );
                    }
                    else if ( name.equals( "lockerType" ) )
                    {
                        d.mLockerType = restResult.getParser().nextText();
                    }
                    else if ( name.equals( "expired" ) )
                    {
                        d.mExpired = restResult.getParser().nextText().equals( "1" );
                    }
                    else if ( name.equals( "item" ) )
                    {
                        d.mSubscriptions.add( d.subscriptionFromResult( restResult ) );
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ( name.equals( "user" ) )
                        loop = false;
                    break;
                }
                event = restResult.getParser().next();
            }
            return d;
        }
        catch ( Exception e )
        {
        }
        return null;
    }

    public static AccountData getAccountData( Session session ) throws LockerException
    {

        String m = "accountData";
        Map<String, String> params = new HashMap<String, String>();
        try
        {
            RestResult restResult = Caller.getInstance().call( m, params );
            if ( !restResult.isSuccessful() )
                throw ( new LockerException( "Call Failed: " + restResult.getErrorMessage() ) );
            return AccountData.accountDataFromResult( restResult );
        }
        catch ( IOException e )
        {
            throw ( new LockerException( "connection issue" ) );
        }
    }
}
