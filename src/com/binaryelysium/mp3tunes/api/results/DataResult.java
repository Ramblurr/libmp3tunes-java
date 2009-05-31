package com.binaryelysium.mp3tunes.api.results;


public class DataResult<E>
{
    protected String mType;
    protected int mTotalResults;
    protected E[] mData;
    
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
    
    public E[] getData()
    {
        return mData;
    }
    
    public void setData(E[] data)
    {
        mData = data;
    }
}
