package org.telegram.messenger.volley;

public class VolleyError
  extends Exception
{
  public final NetworkResponse networkResponse;
  private long networkTimeMs;
  
  public VolleyError()
  {
    this.networkResponse = null;
  }
  
  public VolleyError(String paramString)
  {
    super(paramString);
    this.networkResponse = null;
  }
  
  public VolleyError(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    this.networkResponse = null;
  }
  
  public VolleyError(Throwable paramThrowable)
  {
    super(paramThrowable);
    this.networkResponse = null;
  }
  
  public VolleyError(NetworkResponse paramNetworkResponse)
  {
    this.networkResponse = paramNetworkResponse;
  }
  
  public long getNetworkTimeMs()
  {
    return this.networkTimeMs;
  }
  
  void setNetworkTimeMs(long paramLong)
  {
    this.networkTimeMs = paramLong;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\volley\VolleyError.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */