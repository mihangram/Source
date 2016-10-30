package org.telegram.messenger.exoplayer.extractor.ts;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.NalUnitUtil;
import org.telegram.messenger.exoplayer.util.NalUnitUtil.PpsData;
import org.telegram.messenger.exoplayer.util.NalUnitUtil.SpsData;
import org.telegram.messenger.exoplayer.util.ParsableBitArray;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

final class H264Reader
  extends ElementaryStreamReader
{
  private static final int NAL_UNIT_TYPE_PPS = 8;
  private static final int NAL_UNIT_TYPE_SEI = 6;
  private static final int NAL_UNIT_TYPE_SPS = 7;
  private boolean hasOutputFormat;
  private long pesTimeUs;
  private final NalUnitTargetBuffer pps;
  private final boolean[] prefixFlags;
  private final SampleReader sampleReader;
  private final NalUnitTargetBuffer sei;
  private final SeiReader seiReader;
  private final ParsableByteArray seiWrapper;
  private final NalUnitTargetBuffer sps;
  private long totalBytesWritten;
  
  public H264Reader(TrackOutput paramTrackOutput, SeiReader paramSeiReader, boolean paramBoolean1, boolean paramBoolean2)
  {
    super(paramTrackOutput);
    this.seiReader = paramSeiReader;
    this.prefixFlags = new boolean[3];
    this.sampleReader = new SampleReader(paramTrackOutput, paramBoolean1, paramBoolean2);
    this.sps = new NalUnitTargetBuffer(7, 128);
    this.pps = new NalUnitTargetBuffer(8, 128);
    this.sei = new NalUnitTargetBuffer(6, 128);
    this.seiWrapper = new ParsableByteArray();
  }
  
  private void endNalUnit(long paramLong1, int paramInt1, int paramInt2, long paramLong2)
  {
    Object localObject;
    if ((!this.hasOutputFormat) || (this.sampleReader.needsSpsPps()))
    {
      this.sps.endNalUnit(paramInt2);
      this.pps.endNalUnit(paramInt2);
      if (this.hasOutputFormat) {
        break label300;
      }
      if ((this.sps.isCompleted()) && (this.pps.isCompleted()))
      {
        localObject = new ArrayList();
        ((List)localObject).add(Arrays.copyOf(this.sps.nalData, this.sps.nalLength));
        ((List)localObject).add(Arrays.copyOf(this.pps.nalData, this.pps.nalLength));
        NalUnitUtil.SpsData localSpsData = NalUnitUtil.parseSpsNalUnit(unescape(this.sps));
        NalUnitUtil.PpsData localPpsData = NalUnitUtil.parsePpsNalUnit(unescape(this.pps));
        this.output.format(MediaFormat.createVideoFormat(null, "video/avc", -1, -1, -1L, localSpsData.width, localSpsData.height, (List)localObject, -1, localSpsData.pixelWidthAspectRatio));
        this.hasOutputFormat = true;
        this.sampleReader.putSps(localSpsData);
        this.sampleReader.putPps(localPpsData);
        this.sps.reset();
        this.pps.reset();
      }
    }
    for (;;)
    {
      if (this.sei.endNalUnit(paramInt2))
      {
        paramInt2 = NalUnitUtil.unescapeStream(this.sei.nalData, this.sei.nalLength);
        this.seiWrapper.reset(this.sei.nalData, paramInt2);
        this.seiWrapper.setPosition(4);
        this.seiReader.consume(paramLong2, this.seiWrapper);
      }
      this.sampleReader.endNalUnit(paramLong1, paramInt1);
      return;
      label300:
      if (this.sps.isCompleted())
      {
        localObject = NalUnitUtil.parseSpsNalUnit(unescape(this.sps));
        this.sampleReader.putSps((NalUnitUtil.SpsData)localObject);
        this.sps.reset();
      }
      else if (this.pps.isCompleted())
      {
        localObject = NalUnitUtil.parsePpsNalUnit(unescape(this.pps));
        this.sampleReader.putPps((NalUnitUtil.PpsData)localObject);
        this.pps.reset();
      }
    }
  }
  
  private void nalUnitData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if ((!this.hasOutputFormat) || (this.sampleReader.needsSpsPps()))
    {
      this.sps.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
      this.pps.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
    }
    this.sei.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
    this.sampleReader.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  private void startNalUnit(long paramLong1, int paramInt, long paramLong2)
  {
    if ((!this.hasOutputFormat) || (this.sampleReader.needsSpsPps()))
    {
      this.sps.startNalUnit(paramInt);
      this.pps.startNalUnit(paramInt);
    }
    this.sei.startNalUnit(paramInt);
    this.sampleReader.startNalUnit(paramLong1, paramInt, paramLong2);
  }
  
  private static ParsableBitArray unescape(NalUnitTargetBuffer paramNalUnitTargetBuffer)
  {
    int i = NalUnitUtil.unescapeStream(paramNalUnitTargetBuffer.nalData, paramNalUnitTargetBuffer.nalLength);
    paramNalUnitTargetBuffer = new ParsableBitArray(paramNalUnitTargetBuffer.nalData, i);
    paramNalUnitTargetBuffer.skipBits(32);
    return paramNalUnitTargetBuffer;
  }
  
  public void consume(ParsableByteArray paramParsableByteArray)
  {
    int j;
    byte[] arrayOfByte;
    int k;
    if (paramParsableByteArray.bytesLeft() > 0)
    {
      i = paramParsableByteArray.getPosition();
      j = paramParsableByteArray.limit();
      arrayOfByte = paramParsableByteArray.data;
      this.totalBytesWritten += paramParsableByteArray.bytesLeft();
      this.output.sampleData(paramParsableByteArray, paramParsableByteArray.bytesLeft());
      k = NalUnitUtil.findNalUnit(arrayOfByte, i, j, this.prefixFlags);
      if (k == j) {
        nalUnitData(arrayOfByte, i, j);
      }
    }
    else
    {
      return;
    }
    int m = NalUnitUtil.getNalUnitType(arrayOfByte, k);
    int i1 = k - i;
    if (i1 > 0) {
      nalUnitData(arrayOfByte, i, k);
    }
    int n = j - k;
    long l = this.totalBytesWritten - n;
    if (i1 < 0) {}
    for (int i = -i1;; i = 0)
    {
      endNalUnit(l, n, i, this.pesTimeUs);
      startNalUnit(l, m, this.pesTimeUs);
      i = k + 3;
      break;
    }
  }
  
  public void packetFinished() {}
  
  public void packetStarted(long paramLong, boolean paramBoolean)
  {
    this.pesTimeUs = paramLong;
  }
  
  public void seek()
  {
    NalUnitUtil.clearPrefixFlags(this.prefixFlags);
    this.sps.reset();
    this.pps.reset();
    this.sei.reset();
    this.sampleReader.reset();
    this.totalBytesWritten = 0L;
  }
  
  private static final class SampleReader
  {
    private static final int DEFAULT_BUFFER_SIZE = 128;
    private static final int NAL_UNIT_TYPE_AUD = 9;
    private static final int NAL_UNIT_TYPE_IDR = 5;
    private static final int NAL_UNIT_TYPE_NON_IDR = 1;
    private static final int NAL_UNIT_TYPE_PARTITION_A = 2;
    private final boolean allowNonIdrKeyframes;
    private byte[] buffer;
    private int bufferLength;
    private final boolean detectAccessUnits;
    private boolean isFilling;
    private long nalUnitStartPosition;
    private long nalUnitTimeUs;
    private int nalUnitType;
    private final TrackOutput output;
    private final SparseArray<NalUnitUtil.PpsData> pps;
    private SliceHeaderData previousSliceHeader;
    private boolean readingSample;
    private boolean sampleIsKeyframe;
    private long samplePosition;
    private long sampleTimeUs;
    private final ParsableBitArray scratch;
    private SliceHeaderData sliceHeader;
    private final SparseArray<NalUnitUtil.SpsData> sps;
    
    public SampleReader(TrackOutput paramTrackOutput, boolean paramBoolean1, boolean paramBoolean2)
    {
      this.output = paramTrackOutput;
      this.allowNonIdrKeyframes = paramBoolean1;
      this.detectAccessUnits = paramBoolean2;
      this.sps = new SparseArray();
      this.pps = new SparseArray();
      this.previousSliceHeader = new SliceHeaderData(null);
      this.sliceHeader = new SliceHeaderData(null);
      this.scratch = new ParsableBitArray();
      this.buffer = new byte[''];
      reset();
    }
    
    private void outputSample(int paramInt)
    {
      if (this.sampleIsKeyframe) {}
      for (int i = 1;; i = 0)
      {
        int j = (int)(this.nalUnitStartPosition - this.samplePosition);
        this.output.sampleMetadata(this.sampleTimeUs, i, j, paramInt, null);
        return;
      }
    }
    
    public void appendToNalUnit(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      if (!this.isFilling) {}
      int i3;
      int i4;
      int i5;
      NalUnitUtil.SpsData localSpsData;
      boolean bool2;
      int i6;
      boolean bool3;
      boolean bool1;
      boolean bool4;
      label403:
      int i1;
      int m;
      int i2;
      int n;
      do
      {
        do
        {
          do
          {
            do
            {
              boolean bool5;
              boolean bool6;
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      do
                      {
                        do
                        {
                          do
                          {
                            return;
                            paramInt2 -= paramInt1;
                            if (this.buffer.length < this.bufferLength + paramInt2) {
                              this.buffer = Arrays.copyOf(this.buffer, (this.bufferLength + paramInt2) * 2);
                            }
                            System.arraycopy(paramArrayOfByte, paramInt1, this.buffer, this.bufferLength, paramInt2);
                            this.bufferLength += paramInt2;
                            this.scratch.reset(this.buffer, this.bufferLength);
                          } while (this.scratch.bitsLeft() < 8);
                          this.scratch.skipBits(1);
                          i3 = this.scratch.readBits(2);
                          this.scratch.skipBits(5);
                        } while (!this.scratch.canReadExpGolombCodedNum());
                        this.scratch.readUnsignedExpGolombCodedInt();
                      } while (!this.scratch.canReadExpGolombCodedNum());
                      i4 = this.scratch.readUnsignedExpGolombCodedInt();
                      if (!this.detectAccessUnits)
                      {
                        this.isFilling = false;
                        this.sliceHeader.setSliceType(i4);
                        return;
                      }
                    } while (!this.scratch.canReadExpGolombCodedNum());
                    i5 = this.scratch.readUnsignedExpGolombCodedInt();
                    if (this.pps.indexOfKey(i5) < 0)
                    {
                      this.isFilling = false;
                      return;
                    }
                    paramArrayOfByte = (NalUnitUtil.PpsData)this.pps.get(i5);
                    localSpsData = (NalUnitUtil.SpsData)this.sps.get(paramArrayOfByte.seqParameterSetId);
                    if (!localSpsData.separateColorPlaneFlag) {
                      break;
                    }
                  } while (this.scratch.bitsLeft() < 2);
                  this.scratch.skipBits(2);
                } while (this.scratch.bitsLeft() < localSpsData.frameNumLength);
                bool2 = false;
                bool5 = false;
                bool6 = false;
                i6 = this.scratch.readBits(localSpsData.frameNumLength);
                bool3 = bool5;
                bool1 = bool6;
                if (localSpsData.frameMbsOnlyFlag) {
                  break;
                }
              } while (this.scratch.bitsLeft() < 1);
              bool4 = this.scratch.readBit();
              bool2 = bool4;
              bool3 = bool5;
              bool1 = bool6;
              if (!bool4) {
                break;
              }
            } while (this.scratch.bitsLeft() < 1);
            bool1 = this.scratch.readBit();
            bool3 = true;
            bool2 = bool4;
            if (this.nalUnitType != 5) {
              break label588;
            }
            bool4 = true;
            paramInt1 = 0;
            if (!bool4) {
              break;
            }
          } while (!this.scratch.canReadExpGolombCodedNum());
          paramInt1 = this.scratch.readUnsignedExpGolombCodedInt();
          i1 = 0;
          m = 0;
          i2 = 0;
          n = 0;
          if (localSpsData.picOrderCountType != 0) {
            break label594;
          }
        } while (this.scratch.bitsLeft() < localSpsData.picOrderCntLsbLength);
        i1 = this.scratch.readBits(localSpsData.picOrderCntLsbLength);
        i = i1;
        j = m;
        paramInt2 = i2;
        k = n;
        if (!paramArrayOfByte.bottomFieldPicOrderInFramePresentFlag) {
          break;
        }
        i = i1;
        j = m;
        paramInt2 = i2;
        k = n;
        if (bool2) {
          break;
        }
      } while (!this.scratch.canReadExpGolombCodedNum());
      int j = this.scratch.readSignedExpGolombCodedInt();
      int k = n;
      paramInt2 = i2;
      int i = i1;
      for (;;)
      {
        this.sliceHeader.setAll(localSpsData, i3, i4, i6, i5, bool2, bool3, bool1, bool4, paramInt1, i, j, paramInt2, k);
        this.isFilling = false;
        return;
        label588:
        bool4 = false;
        break label403;
        label594:
        i = i1;
        j = m;
        paramInt2 = i2;
        k = n;
        if (localSpsData.picOrderCountType == 1)
        {
          i = i1;
          j = m;
          paramInt2 = i2;
          k = n;
          if (!localSpsData.deltaPicOrderAlwaysZeroFlag)
          {
            if (!this.scratch.canReadExpGolombCodedNum()) {
              break;
            }
            i2 = this.scratch.readSignedExpGolombCodedInt();
            i = i1;
            j = m;
            paramInt2 = i2;
            k = n;
            if (paramArrayOfByte.bottomFieldPicOrderInFramePresentFlag)
            {
              i = i1;
              j = m;
              paramInt2 = i2;
              k = n;
              if (!bool2)
              {
                if (!this.scratch.canReadExpGolombCodedNum()) {
                  break;
                }
                k = this.scratch.readSignedExpGolombCodedInt();
                i = i1;
                j = m;
                paramInt2 = i2;
              }
            }
          }
        }
      }
    }
    
    public void endNalUnit(long paramLong, int paramInt)
    {
      int i = 0;
      if ((this.nalUnitType == 9) || ((this.detectAccessUnits) && (this.sliceHeader.isFirstVclNalUnitOfPicture(this.previousSliceHeader))))
      {
        if (this.readingSample) {
          outputSample(paramInt + (int)(paramLong - this.nalUnitStartPosition));
        }
        this.samplePosition = this.nalUnitStartPosition;
        this.sampleTimeUs = this.nalUnitTimeUs;
        this.sampleIsKeyframe = false;
        this.readingSample = true;
      }
      int j = this.sampleIsKeyframe;
      if (this.nalUnitType != 5)
      {
        paramInt = i;
        if (this.allowNonIdrKeyframes)
        {
          paramInt = i;
          if (this.nalUnitType == 1)
          {
            paramInt = i;
            if (!this.sliceHeader.isISlice()) {}
          }
        }
      }
      else
      {
        paramInt = 1;
      }
      this.sampleIsKeyframe = (paramInt | j);
    }
    
    public boolean needsSpsPps()
    {
      return this.detectAccessUnits;
    }
    
    public void putPps(NalUnitUtil.PpsData paramPpsData)
    {
      this.pps.append(paramPpsData.picParameterSetId, paramPpsData);
    }
    
    public void putSps(NalUnitUtil.SpsData paramSpsData)
    {
      this.sps.append(paramSpsData.seqParameterSetId, paramSpsData);
    }
    
    public void reset()
    {
      this.isFilling = false;
      this.readingSample = false;
      this.sliceHeader.clear();
    }
    
    public void startNalUnit(long paramLong1, int paramInt, long paramLong2)
    {
      this.nalUnitType = paramInt;
      this.nalUnitTimeUs = paramLong2;
      this.nalUnitStartPosition = paramLong1;
      if (((this.allowNonIdrKeyframes) && (this.nalUnitType == 1)) || ((this.detectAccessUnits) && ((this.nalUnitType == 5) || (this.nalUnitType == 1) || (this.nalUnitType == 2))))
      {
        SliceHeaderData localSliceHeaderData = this.previousSliceHeader;
        this.previousSliceHeader = this.sliceHeader;
        this.sliceHeader = localSliceHeaderData;
        this.sliceHeader.clear();
        this.bufferLength = 0;
        this.isFilling = true;
      }
    }
    
    private static final class SliceHeaderData
    {
      private static final int SLICE_TYPE_ALL_I = 7;
      private static final int SLICE_TYPE_I = 2;
      private boolean bottomFieldFlag;
      private boolean bottomFieldFlagPresent;
      private int deltaPicOrderCnt0;
      private int deltaPicOrderCnt1;
      private int deltaPicOrderCntBottom;
      private boolean fieldPicFlag;
      private int frameNum;
      private boolean hasSliceType;
      private boolean idrPicFlag;
      private int idrPicId;
      private boolean isComplete;
      private int nalRefIdc;
      private int picOrderCntLsb;
      private int picParameterSetId;
      private int sliceType;
      private NalUnitUtil.SpsData spsData;
      
      private boolean isFirstVclNalUnitOfPicture(SliceHeaderData paramSliceHeaderData)
      {
        return (this.isComplete) && ((!paramSliceHeaderData.isComplete) || (this.frameNum != paramSliceHeaderData.frameNum) || (this.picParameterSetId != paramSliceHeaderData.picParameterSetId) || (this.fieldPicFlag != paramSliceHeaderData.fieldPicFlag) || ((this.bottomFieldFlagPresent) && (paramSliceHeaderData.bottomFieldFlagPresent) && (this.bottomFieldFlag != paramSliceHeaderData.bottomFieldFlag)) || ((this.nalRefIdc != paramSliceHeaderData.nalRefIdc) && ((this.nalRefIdc == 0) || (paramSliceHeaderData.nalRefIdc == 0))) || ((this.spsData.picOrderCountType == 0) && (paramSliceHeaderData.spsData.picOrderCountType == 0) && ((this.picOrderCntLsb != paramSliceHeaderData.picOrderCntLsb) || (this.deltaPicOrderCntBottom != paramSliceHeaderData.deltaPicOrderCntBottom))) || ((this.spsData.picOrderCountType == 1) && (paramSliceHeaderData.spsData.picOrderCountType == 1) && ((this.deltaPicOrderCnt0 != paramSliceHeaderData.deltaPicOrderCnt0) || (this.deltaPicOrderCnt1 != paramSliceHeaderData.deltaPicOrderCnt1))) || (this.idrPicFlag != paramSliceHeaderData.idrPicFlag) || ((this.idrPicFlag) && (paramSliceHeaderData.idrPicFlag) && (this.idrPicId != paramSliceHeaderData.idrPicId)));
      }
      
      public void clear()
      {
        this.hasSliceType = false;
        this.isComplete = false;
      }
      
      public boolean isISlice()
      {
        return (this.hasSliceType) && ((this.sliceType == 7) || (this.sliceType == 2));
      }
      
      public void setAll(NalUnitUtil.SpsData paramSpsData, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9)
      {
        this.spsData = paramSpsData;
        this.nalRefIdc = paramInt1;
        this.sliceType = paramInt2;
        this.frameNum = paramInt3;
        this.picParameterSetId = paramInt4;
        this.fieldPicFlag = paramBoolean1;
        this.bottomFieldFlagPresent = paramBoolean2;
        this.bottomFieldFlag = paramBoolean3;
        this.idrPicFlag = paramBoolean4;
        this.idrPicId = paramInt5;
        this.picOrderCntLsb = paramInt6;
        this.deltaPicOrderCntBottom = paramInt7;
        this.deltaPicOrderCnt0 = paramInt8;
        this.deltaPicOrderCnt1 = paramInt9;
        this.isComplete = true;
        this.hasSliceType = true;
      }
      
      public void setSliceType(int paramInt)
      {
        this.sliceType = paramInt;
        this.hasSliceType = true;
      }
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\exoplayer\extractor\ts\H264Reader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */