package org.telegram.messenger.exoplayer.upstream;

public final class Allocation
{
  public final byte[] data;
  private final int offset;
  
  public Allocation(byte[] paramArrayOfByte, int paramInt)
  {
    this.data = paramArrayOfByte;
    this.offset = paramInt;
  }
  
  public int translateOffset(int paramInt)
  {
    return this.offset + paramInt;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\upstream\Allocation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */