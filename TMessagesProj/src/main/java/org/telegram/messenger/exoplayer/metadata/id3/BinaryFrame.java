package org.telegram.messenger.exoplayer.metadata.id3;

public final class BinaryFrame
  extends Id3Frame
{
  public final byte[] data;
  
  public BinaryFrame(String paramString, byte[] paramArrayOfByte)
  {
    super(paramString);
    this.data = paramArrayOfByte;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\metadata\id3\BinaryFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */