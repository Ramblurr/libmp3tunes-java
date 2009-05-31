package com.binaryelysium.mp3tunes.api.results;


/**
 * In some instances, it may be desirable to limit the size of a result set due to 
 * limited availble memory and/or large locker sizes. It is now possible to retrieve 
 * lockerData results in multiple sets of a defined size.
 *
 * @param <E> The Data type (Artist, Album, Track)
 */
public class SetResult<E> extends DataResult<E>
{
    private int mSet;
    private int mCount;
    private int mTotalResultSets;
    
   
    public SetResult( String type, int totalResults, int set, int count, int totalResultSets )
    {
        super(type, totalResults);
        mSet = set;
        mCount = count;
        mTotalResultSets = totalResultSets;
    }
    
    public SetResult()
    {}

    
    public int getTotalResults()
    {
        return mTotalResults;
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

    
    public void setTotalResults( int totalResults )
    {
        mTotalResults = totalResults;
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
    
}
