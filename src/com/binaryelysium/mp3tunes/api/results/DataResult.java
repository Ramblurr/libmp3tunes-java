package com.binaryelysium.mp3tunes.api.results;

import java.util.Collection;


public class DataResult<E>
{
    protected String mType;
    protected int mTotalResults;
    protected Collection<E> mData;
    
    public DataResult(String type, int totalResults)
    {
        mType = type;
        mTotalResults = totalResults;
    }
    public DataResult()
    {
        
    }
    
    public String getType()
    {
        return mType;
    }
    
    public void setType( String type )
    {
        mType = type;
    }
    
    public int getTotalResults()
    {
        return mTotalResults;
    }
    
    public void setTotalResults( int totalResults )
    {
        mTotalResults = totalResults;
    }
    
    public Collection<E> getData()
    {
        return mData;
    }
    
    public void setData(Collection<E> data)
    {
        mData = data;
    }
}
