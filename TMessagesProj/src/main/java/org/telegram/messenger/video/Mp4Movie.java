package org.telegram.messenger.video;

import android.annotation.TargetApi;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import com.googlecode.mp4parser.util.Matrix;
import java.io.File;
import java.util.ArrayList;

@TargetApi(16)
public class Mp4Movie
{
  private File cacheFile;
  private int height;
  private Matrix matrix = Matrix.ROTATE_0;
  private ArrayList<Track> tracks = new ArrayList();
  private int width;
  
  public void addSample(int paramInt, long paramLong, MediaCodec.BufferInfo paramBufferInfo)
    throws Exception
  {
    if ((paramInt < 0) || (paramInt >= this.tracks.size())) {
      return;
    }
    ((Track)this.tracks.get(paramInt)).addSample(paramLong, paramBufferInfo);
  }
  
  public int addTrack(MediaFormat paramMediaFormat, boolean paramBoolean)
    throws Exception
  {
    this.tracks.add(new Track(this.tracks.size(), paramMediaFormat, paramBoolean));
    return this.tracks.size() - 1;
  }
  
  public File getCacheFile()
  {
    return this.cacheFile;
  }
  
  public int getHeight()
  {
    return this.height;
  }
  
  public Matrix getMatrix()
  {
    return this.matrix;
  }
  
  public ArrayList<Track> getTracks()
  {
    return this.tracks;
  }
  
  public int getWidth()
  {
    return this.width;
  }
  
  public void setCacheFile(File paramFile)
  {
    this.cacheFile = paramFile;
  }
  
  public void setRotation(int paramInt)
  {
    if (paramInt == 0) {
      this.matrix = Matrix.ROTATE_0;
    }
    do
    {
      return;
      if (paramInt == 90)
      {
        this.matrix = Matrix.ROTATE_90;
        return;
      }
      if (paramInt == 180)
      {
        this.matrix = Matrix.ROTATE_180;
        return;
      }
    } while (paramInt != 270);
    this.matrix = Matrix.ROTATE_270;
  }
  
  public void setSize(int paramInt1, int paramInt2)
  {
    this.width = paramInt1;
    this.height = paramInt2;
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\video\Mp4Movie.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */