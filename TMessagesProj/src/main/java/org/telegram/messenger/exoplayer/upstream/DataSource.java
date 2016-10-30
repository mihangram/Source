package org.telegram.messenger.exoplayer.upstream;

import java.io.IOException;

public abstract interface DataSource
{
  public abstract void close()
    throws IOException;
  
  public abstract long open(DataSpec paramDataSpec)
    throws IOException;
  
  public abstract int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\upstream\DataSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */