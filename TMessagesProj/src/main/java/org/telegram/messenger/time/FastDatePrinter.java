package org.telegram.messenger.time;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.Mihangram.SolarCalendar;

public class FastDatePrinter
  implements DatePrinter, Serializable
{
  public static final int FULL = 0;
  public static final int LONG = 1;
  public static final int MEDIUM = 2;
  public static final int SHORT = 3;
  private static final ConcurrentMap<TimeZoneDisplayKey, String> cTimeZoneDisplayCache = new ConcurrentHashMap(7);
  private static final long serialVersionUID = 1L;
  private final Locale mLocale;
  private transient int mMaxLengthEstimate;
  private final String mPattern;
  private transient Rule[] mRules;
  private final TimeZone mTimeZone;
  
  protected FastDatePrinter(String paramString, TimeZone paramTimeZone, Locale paramLocale)
  {
    this.mPattern = paramString;
    this.mTimeZone = paramTimeZone;
    this.mLocale = paramLocale;
    init();
  }
  
  private String applyRulesToString(Calendar paramCalendar)
  {
    return applyRules(paramCalendar, new StringBuffer(this.mMaxLengthEstimate)).toString();
  }
  
  static String getTimeZoneDisplay(TimeZone paramTimeZone, boolean paramBoolean, int paramInt, Locale paramLocale)
  {
    TimeZoneDisplayKey localTimeZoneDisplayKey = new TimeZoneDisplayKey(paramTimeZone, paramBoolean, paramInt, paramLocale);
    String str = (String)cTimeZoneDisplayCache.get(localTimeZoneDisplayKey);
    Object localObject = str;
    if (str == null)
    {
      localObject = paramTimeZone.getDisplayName(paramBoolean, paramInt, paramLocale);
      paramTimeZone = (String)cTimeZoneDisplayCache.putIfAbsent(localTimeZoneDisplayKey, localObject);
      if (paramTimeZone != null) {
        localObject = paramTimeZone;
      }
    }
    return (String)localObject;
  }
  
  private void init()
  {
    List localList = parsePattern();
    this.mRules = ((Rule[])localList.toArray(new Rule[localList.size()]));
    int i = 0;
    int j = this.mRules.length;
    for (;;)
    {
      j -= 1;
      if (j < 0) {
        break;
      }
      i += this.mRules[j].estimateLength();
    }
    this.mMaxLengthEstimate = i;
  }
  
  private GregorianCalendar newCalendar()
  {
    return new GregorianCalendar(this.mTimeZone, this.mLocale);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    paramObjectInputStream.defaultReadObject();
    init();
  }
  
  public String MihanFormat(long paramLong)
  {
    GregorianCalendar localGregorianCalendar = newCalendar();
    localGregorianCalendar.setTimeInMillis(paramLong);
    if (ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getBoolean("persian_date", false)) {
      return new SolarCalendar(localGregorianCalendar).getDesMonthYear();
    }
    return applyRulesToString(localGregorianCalendar);
  }
  
  protected StringBuffer applyRules(Calendar paramCalendar, StringBuffer paramStringBuffer)
  {
    Rule[] arrayOfRule = this.mRules;
    int j = arrayOfRule.length;
    int i = 0;
    while (i < j)
    {
      arrayOfRule[i].appendTo(paramStringBuffer, paramCalendar);
      i += 1;
    }
    return paramStringBuffer;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof FastDatePrinter)) {}
    do
    {
      return false;
      paramObject = (FastDatePrinter)paramObject;
    } while ((!this.mPattern.equals(((FastDatePrinter)paramObject).mPattern)) || (!this.mTimeZone.equals(((FastDatePrinter)paramObject).mTimeZone)) || (!this.mLocale.equals(((FastDatePrinter)paramObject).mLocale)));
    return true;
  }
  
  public String format(long paramLong)
  {
    GregorianCalendar localGregorianCalendar = newCalendar();
    localGregorianCalendar.setTimeInMillis(paramLong);
    if (ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getBoolean("persian_date", false)) {
      return new SolarCalendar(localGregorianCalendar).getTime();
    }
    return applyRulesToString(localGregorianCalendar);
  }
  
  public String format(Calendar paramCalendar)
  {
    return format(paramCalendar, new StringBuffer(this.mMaxLengthEstimate)).toString();
  }
  
  public String format(Date paramDate)
  {
    GregorianCalendar localGregorianCalendar = newCalendar();
    localGregorianCalendar.setTime(paramDate);
    if (ApplicationLoader.applicationContext.getSharedPreferences("Mihanconfig", 0).getBoolean("persian_date", false))
    {
      int i = localGregorianCalendar.get(6);
      int j = localGregorianCalendar.get(1);
      paramDate = Calendar.getInstance();
      int k = paramDate.get(6);
      int m = paramDate.get(1);
      if ((k == i) && (j == m)) {
        return new SolarCalendar(localGregorianCalendar).getTime();
      }
      if (j == m) {
        return new SolarCalendar(localGregorianCalendar).getShortDesDate();
      }
      return new SolarCalendar(localGregorianCalendar).getNumDate();
    }
    return applyRulesToString(localGregorianCalendar);
  }
  
  public StringBuffer format(long paramLong, StringBuffer paramStringBuffer)
  {
    return format(new Date(paramLong), paramStringBuffer);
  }
  
  public StringBuffer format(Object paramObject, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition)
  {
    if ((paramObject instanceof Date)) {
      return format((Date)paramObject, paramStringBuffer);
    }
    if ((paramObject instanceof Calendar)) {
      return format((Calendar)paramObject, paramStringBuffer);
    }
    if ((paramObject instanceof Long)) {
      return format(((Long)paramObject).longValue(), paramStringBuffer);
    }
    paramStringBuffer = new StringBuilder().append("Unknown class: ");
    if (paramObject == null) {}
    for (paramObject = "<null>";; paramObject = paramObject.getClass().getName()) {
      throw new IllegalArgumentException((String)paramObject);
    }
  }
  
  public StringBuffer format(Calendar paramCalendar, StringBuffer paramStringBuffer)
  {
    return applyRules(paramCalendar, paramStringBuffer);
  }
  
  public StringBuffer format(Date paramDate, StringBuffer paramStringBuffer)
  {
    GregorianCalendar localGregorianCalendar = newCalendar();
    localGregorianCalendar.setTime(paramDate);
    return applyRules(localGregorianCalendar, paramStringBuffer);
  }
  
  public Locale getLocale()
  {
    return this.mLocale;
  }
  
  public int getMaxLengthEstimate()
  {
    return this.mMaxLengthEstimate;
  }
  
  public String getPattern()
  {
    return this.mPattern;
  }
  
  public TimeZone getTimeZone()
  {
    return this.mTimeZone;
  }
  
  public int hashCode()
  {
    return this.mPattern.hashCode() + (this.mTimeZone.hashCode() + this.mLocale.hashCode() * 13) * 13;
  }
  
  protected List<Rule> parsePattern()
  {
    Object localObject = new DateFormatSymbols(this.mLocale);
    ArrayList localArrayList = new ArrayList();
    String[] arrayOfString3 = ((DateFormatSymbols)localObject).getEras();
    String[] arrayOfString4 = ((DateFormatSymbols)localObject).getMonths();
    String[] arrayOfString5 = ((DateFormatSymbols)localObject).getShortMonths();
    String[] arrayOfString1 = ((DateFormatSymbols)localObject).getWeekdays();
    String[] arrayOfString2 = ((DateFormatSymbols)localObject).getShortWeekdays();
    String[] arrayOfString6 = ((DateFormatSymbols)localObject).getAmPmStrings();
    int k = this.mPattern.length();
    int[] arrayOfInt = new int[1];
    int i = 0;
    int m;
    int j;
    if (i < k)
    {
      arrayOfInt[0] = i;
      localObject = parseToken(this.mPattern, arrayOfInt);
      m = arrayOfInt[0];
      j = ((String)localObject).length();
      if (j != 0) {}
    }
    else
    {
      return localArrayList;
    }
    switch (((String)localObject).charAt(0))
    {
    default: 
      throw new IllegalArgumentException("Illegal pattern component: " + (String)localObject);
    case 'G': 
      localObject = new TextField(0, arrayOfString3);
    }
    for (;;)
    {
      localArrayList.add(localObject);
      i = m + 1;
      break;
      if (j == 2)
      {
        localObject = TwoDigitYearField.INSTANCE;
      }
      else
      {
        i = j;
        if (j < 4) {
          i = 4;
        }
        localObject = selectNumberRule(1, i);
        continue;
        if (j >= 4)
        {
          localObject = new TextField(2, arrayOfString4);
        }
        else if (j == 3)
        {
          localObject = new TextField(2, arrayOfString5);
        }
        else if (j == 2)
        {
          localObject = TwoDigitMonthField.INSTANCE;
        }
        else
        {
          localObject = UnpaddedMonthField.INSTANCE;
          continue;
          localObject = selectNumberRule(5, j);
          continue;
          localObject = new TwelveHourField(selectNumberRule(10, j));
          continue;
          localObject = selectNumberRule(11, j);
          continue;
          localObject = selectNumberRule(12, j);
          continue;
          localObject = selectNumberRule(13, j);
          continue;
          localObject = selectNumberRule(14, j);
          continue;
          if (j < 4) {}
          for (localObject = arrayOfString2;; localObject = arrayOfString1)
          {
            localObject = new TextField(7, (String[])localObject);
            break;
          }
          localObject = selectNumberRule(6, j);
          continue;
          localObject = selectNumberRule(8, j);
          continue;
          localObject = selectNumberRule(3, j);
          continue;
          localObject = selectNumberRule(4, j);
          continue;
          localObject = new TextField(9, arrayOfString6);
          continue;
          localObject = new TwentyFourHourField(selectNumberRule(11, j));
          continue;
          localObject = selectNumberRule(10, j);
          continue;
          if (j >= 4)
          {
            localObject = new TimeZoneNameRule(this.mTimeZone, this.mLocale, 1);
          }
          else
          {
            localObject = new TimeZoneNameRule(this.mTimeZone, this.mLocale, 0);
            continue;
            if (j == 1)
            {
              localObject = TimeZoneNumberRule.INSTANCE_NO_COLON;
            }
            else
            {
              localObject = TimeZoneNumberRule.INSTANCE_COLON;
              continue;
              localObject = ((String)localObject).substring(1);
              if (((String)localObject).length() == 1) {
                localObject = new CharacterLiteral(((String)localObject).charAt(0));
              } else {
                localObject = new StringLiteral((String)localObject);
              }
            }
          }
        }
      }
    }
  }
  
  protected String parseToken(String paramString, int[] paramArrayOfInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = paramArrayOfInt[0];
    int m = paramString.length();
    char c = paramString.charAt(i);
    if (((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z')))
    {
      localStringBuilder.append(c);
      for (;;)
      {
        j = i;
        if (i + 1 >= m) {
          break;
        }
        j = i;
        if (paramString.charAt(i + 1) != c) {
          break;
        }
        localStringBuilder.append(c);
        i += 1;
      }
    }
    localStringBuilder.append('\'');
    int k = 0;
    int j = i;
    if (i < m)
    {
      c = paramString.charAt(i);
      if (c == '\'') {
        if ((i + 1 < m) && (paramString.charAt(i + 1) == '\''))
        {
          i += 1;
          localStringBuilder.append(c);
          j = k;
        }
      }
    }
    for (;;)
    {
      i += 1;
      k = j;
      break;
      if (k == 0) {}
      for (j = 1;; j = 0) {
        break;
      }
      if ((k == 0) && (((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z'))))
      {
        j = i - 1;
        paramArrayOfInt[0] = j;
        return localStringBuilder.toString();
      }
      localStringBuilder.append(c);
      j = k;
    }
  }
  
  protected NumberRule selectNumberRule(int paramInt1, int paramInt2)
  {
    switch (paramInt2)
    {
    default: 
      return new PaddedNumberField(paramInt1, paramInt2);
    case 1: 
      return new UnpaddedNumberField(paramInt1);
    }
    return new TwoDigitNumberField(paramInt1);
  }
  
  public String toString()
  {
    return "FastDatePrinter[" + this.mPattern + "," + this.mLocale + "," + this.mTimeZone.getID() + "]";
  }
  
  private static class CharacterLiteral
    implements FastDatePrinter.Rule
  {
    private final char mValue;
    
    CharacterLiteral(char paramChar)
    {
      this.mValue = paramChar;
    }
    
    public void appendTo(StringBuffer paramStringBuffer, Calendar paramCalendar)
    {
      paramStringBuffer.append(this.mValue);
    }
    
    public int estimateLength()
    {
      return 1;
    }
  }
  
  private static abstract interface NumberRule
    extends FastDatePrinter.Rule
  {
    public abstract void appendTo(StringBuffer paramStringBuffer, int paramInt);
  }
  
  private static class PaddedNumberField
    implements FastDatePrinter.NumberRule
  {
    private final int mField;
    private final int mSize;
    
    PaddedNumberField(int paramInt1, int paramInt2)
    {
      if (paramInt2 < 3) {
        throw new IllegalArgumentException();
      }
      this.mField = paramInt1;
      this.mSize = paramInt2;
    }
    
    public final void appendTo(StringBuffer paramStringBuffer, int paramInt)
    {
      if (paramInt < 100)
      {
        i = this.mSize;
        for (;;)
        {
          i -= 1;
          if (i < 2) {
            break;
          }
          paramStringBuffer.append('0');
        }
        paramStringBuffer.append((char)(paramInt / 10 + 48));
        paramStringBuffer.append((char)(paramInt % 10 + 48));
        return;
      }
      if (paramInt < 1000) {}
      for (int i = 3;; i = Integer.toString(paramInt).length())
      {
        int j = this.mSize;
        for (;;)
        {
          j -= 1;
          if (j < i) {
            break;
          }
          paramStringBuffer.append('0');
        }
      }
      paramStringBuffer.append(Integer.toString(paramInt));
    }
    
    public void appendTo(StringBuffer paramStringBuffer, Calendar paramCalendar)
    {
      appendTo(paramStringBuffer, paramCalendar.get(this.mField));
    }
    
    public int estimateLength()
    {
      return 4;
    }
  }
  
  private static abstract interface Rule
  {
    public abstract void appendTo(StringBuffer paramStringBuffer, Calendar paramCalendar);
    
    public abstract int estimateLength();
  }
  
  private static class StringLiteral
    implements FastDatePrinter.Rule
  {
    private final String mValue;
    
    StringLiteral(String paramString)
    {
      this.mValue = paramString;
    }
    
    public void appendTo(StringBuffer paramStringBuffer, Calendar paramCalendar)
    {
      paramStringBuffer.append(this.mValue);
    }
    
    public int estimateLength()
    {
      return this.mValue.length();
    }
  }
  
  private static class TextField
    implements FastDatePrinter.Rule
  {
    private final int mField;
    private final String[] mValues;
    
    TextField(int paramInt, String[] paramArrayOfString)
    {
      this.mField = paramInt;
      this.mValues = paramArrayOfString;
    }
    
    public void appendTo(StringBuffer paramStringBuffer, Calendar paramCalendar)
    {
      paramStringBuffer.append(this.mValues[paramCalendar.get(this.mField)]);
    }
    
    public int estimateLength()
    {
      int j = 0;
      int i = this.mValues.length;
      for (;;)
      {
        int k = i - 1;
        if (k < 0) {
          break;
        }
        int m = this.mValues[k].length();
        i = k;
        if (m > j)
        {
          j = m;
          i = k;
        }
      }
      return j;
    }
  }
  
  private static class TimeZoneDisplayKey
  {
    private final Locale mLocale;
    private final int mStyle;
    private final TimeZone mTimeZone;
    
    TimeZoneDisplayKey(TimeZone paramTimeZone, boolean paramBoolean, int paramInt, Locale paramLocale)
    {
      this.mTimeZone = paramTimeZone;
      if (paramBoolean) {}
      for (this.mStyle = (0x80000000 | paramInt);; this.mStyle = paramInt)
      {
        this.mLocale = paramLocale;
        return;
      }
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {}
      do
      {
        return true;
        if (!(paramObject instanceof TimeZoneDisplayKey)) {
          break;
        }
        paramObject = (TimeZoneDisplayKey)paramObject;
      } while ((this.mTimeZone.equals(((TimeZoneDisplayKey)paramObject).mTimeZone)) && (this.mStyle == ((TimeZoneDisplayKey)paramObject).mStyle) && (this.mLocale.equals(((TimeZoneDisplayKey)paramObject).mLocale)));
      return false;
      return false;
    }
    
    public int hashCode()
    {
      return (this.mStyle * 31 + this.mLocale.hashCode()) * 31 + this.mTimeZone.hashCode();
    }
  }
  
  private static class TimeZoneNameRule
    implements FastDatePrinter.Rule
  {
    private final String mDaylight;
    private final Locale mLocale;
    private final String mStandard;
    private final int mStyle;
    
    TimeZoneNameRule(TimeZone paramTimeZone, Locale paramLocale, int paramInt)
    {
      this.mLocale = paramLocale;
      this.mStyle = paramInt;
      this.mStandard = FastDatePrinter.getTimeZoneDisplay(paramTimeZone, false, paramInt, paramLocale);
      this.mDaylight = FastDatePrinter.getTimeZoneDisplay(paramTimeZone, true, paramInt, paramLocale);
    }
    
    public void appendTo(StringBuffer paramStringBuffer, Calendar paramCalendar)
    {
      TimeZone localTimeZone = paramCalendar.getTimeZone();
      if ((localTimeZone.useDaylightTime()) && (paramCalendar.get(16) != 0))
      {
        paramStringBuffer.append(FastDatePrinter.getTimeZoneDisplay(localTimeZone, true, this.mStyle, this.mLocale));
        return;
      }
      paramStringBuffer.append(FastDatePrinter.getTimeZoneDisplay(localTimeZone, false, this.mStyle, this.mLocale));
    }
    
    public int estimateLength()
    {
      return Math.max(this.mStandard.length(), this.mDaylight.length());
    }
  }
  
  private static class TimeZoneNumberRule
    implements FastDatePrinter.Rule
  {
    static final TimeZoneNumberRule INSTANCE_COLON = new TimeZoneNumberRule(true);
    static final TimeZoneNumberRule INSTANCE_NO_COLON = new TimeZoneNumberRule(false);
    final boolean mColon;
    
    TimeZoneNumberRule(boolean paramBoolean)
    {
      this.mColon = paramBoolean;
    }
    
    public void appendTo(StringBuffer paramStringBuffer, Calendar paramCalendar)
    {
      int i = paramCalendar.get(15) + paramCalendar.get(16);
      if (i < 0)
      {
        paramStringBuffer.append('-');
        i = -i;
      }
      for (;;)
      {
        int j = i / 3600000;
        paramStringBuffer.append((char)(j / 10 + 48));
        paramStringBuffer.append((char)(j % 10 + 48));
        if (this.mColon) {
          paramStringBuffer.append(':');
        }
        i = i / 60000 - j * 60;
        paramStringBuffer.append((char)(i / 10 + 48));
        paramStringBuffer.append((char)(i % 10 + 48));
        return;
        paramStringBuffer.append('+');
      }
    }
    
    public int estimateLength()
    {
      return 5;
    }
  }
  
  private static class TwelveHourField
    implements FastDatePrinter.NumberRule
  {
    private final FastDatePrinter.NumberRule mRule;
    
    TwelveHourField(FastDatePrinter.NumberRule paramNumberRule)
    {
      this.mRule = paramNumberRule;
    }
    
    public void appendTo(StringBuffer paramStringBuffer, int paramInt)
    {
      this.mRule.appendTo(paramStringBuffer, paramInt);
    }
    
    public void appendTo(StringBuffer paramStringBuffer, Calendar paramCalendar)
    {
      int j = paramCalendar.get(10);
      int i = j;
      if (j == 0) {
        i = paramCalendar.getLeastMaximum(10) + 1;
      }
      this.mRule.appendTo(paramStringBuffer, i);
    }
    
    public int estimateLength()
    {
      return this.mRule.estimateLength();
    }
  }
  
  private static class TwentyFourHourField
    implements FastDatePrinter.NumberRule
  {
    private final FastDatePrinter.NumberRule mRule;
    
    TwentyFourHourField(FastDatePrinter.NumberRule paramNumberRule)
    {
      this.mRule = paramNumberRule;
    }
    
    public void appendTo(StringBuffer paramStringBuffer, int paramInt)
    {
      this.mRule.appendTo(paramStringBuffer, paramInt);
    }
    
    public void appendTo(StringBuffer paramStringBuffer, Calendar paramCalendar)
    {
      int j = paramCalendar.get(11);
      int i = j;
      if (j == 0) {
        i = paramCalendar.getMaximum(11) + 1;
      }
      this.mRule.appendTo(paramStringBuffer, i);
    }
    
    public int estimateLength()
    {
      return this.mRule.estimateLength();
    }
  }
  
  private static class TwoDigitMonthField
    implements FastDatePrinter.NumberRule
  {
    static final TwoDigitMonthField INSTANCE = new TwoDigitMonthField();
    
    public final void appendTo(StringBuffer paramStringBuffer, int paramInt)
    {
      paramStringBuffer.append((char)(paramInt / 10 + 48));
      paramStringBuffer.append((char)(paramInt % 10 + 48));
    }
    
    public void appendTo(StringBuffer paramStringBuffer, Calendar paramCalendar)
    {
      appendTo(paramStringBuffer, paramCalendar.get(2) + 1);
    }
    
    public int estimateLength()
    {
      return 2;
    }
  }
  
  private static class TwoDigitNumberField
    implements FastDatePrinter.NumberRule
  {
    private final int mField;
    
    TwoDigitNumberField(int paramInt)
    {
      this.mField = paramInt;
    }
    
    public final void appendTo(StringBuffer paramStringBuffer, int paramInt)
    {
      if (paramInt < 100)
      {
        paramStringBuffer.append((char)(paramInt / 10 + 48));
        paramStringBuffer.append((char)(paramInt % 10 + 48));
        return;
      }
      paramStringBuffer.append(Integer.toString(paramInt));
    }
    
    public void appendTo(StringBuffer paramStringBuffer, Calendar paramCalendar)
    {
      appendTo(paramStringBuffer, paramCalendar.get(this.mField));
    }
    
    public int estimateLength()
    {
      return 2;
    }
  }
  
  private static class TwoDigitYearField
    implements FastDatePrinter.NumberRule
  {
    static final TwoDigitYearField INSTANCE = new TwoDigitYearField();
    
    public final void appendTo(StringBuffer paramStringBuffer, int paramInt)
    {
      paramStringBuffer.append((char)(paramInt / 10 + 48));
      paramStringBuffer.append((char)(paramInt % 10 + 48));
    }
    
    public void appendTo(StringBuffer paramStringBuffer, Calendar paramCalendar)
    {
      appendTo(paramStringBuffer, paramCalendar.get(1) % 100);
    }
    
    public int estimateLength()
    {
      return 2;
    }
  }
  
  private static class UnpaddedMonthField
    implements FastDatePrinter.NumberRule
  {
    static final UnpaddedMonthField INSTANCE = new UnpaddedMonthField();
    
    public final void appendTo(StringBuffer paramStringBuffer, int paramInt)
    {
      if (paramInt < 10)
      {
        paramStringBuffer.append((char)(paramInt + 48));
        return;
      }
      paramStringBuffer.append((char)(paramInt / 10 + 48));
      paramStringBuffer.append((char)(paramInt % 10 + 48));
    }
    
    public void appendTo(StringBuffer paramStringBuffer, Calendar paramCalendar)
    {
      appendTo(paramStringBuffer, paramCalendar.get(2) + 1);
    }
    
    public int estimateLength()
    {
      return 2;
    }
  }
  
  private static class UnpaddedNumberField
    implements FastDatePrinter.NumberRule
  {
    private final int mField;
    
    UnpaddedNumberField(int paramInt)
    {
      this.mField = paramInt;
    }
    
    public final void appendTo(StringBuffer paramStringBuffer, int paramInt)
    {
      if (paramInt < 10)
      {
        paramStringBuffer.append((char)(paramInt + 48));
        return;
      }
      if (paramInt < 100)
      {
        paramStringBuffer.append((char)(paramInt / 10 + 48));
        paramStringBuffer.append((char)(paramInt % 10 + 48));
        return;
      }
      paramStringBuffer.append(Integer.toString(paramInt));
    }
    
    public void appendTo(StringBuffer paramStringBuffer, Calendar paramCalendar)
    {
      appendTo(paramStringBuffer, paramCalendar.get(this.mField));
    }
    
    public int estimateLength()
    {
      return 4;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\time\FastDatePrinter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */