package org.telegram.SQLite;

public class SQLiteException
  extends Exception
{
  private static final long serialVersionUID = -2398298479089615621L;
  public final int errorCode;
  
  public SQLiteException()
  {
    this.errorCode = 0;
  }
  
  public SQLiteException(int paramInt, String paramString)
  {
    super(paramString);
    this.errorCode = paramInt;
  }
  
  public SQLiteException(String paramString)
  {
    this(0, paramString);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\SQLite\SQLiteException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */