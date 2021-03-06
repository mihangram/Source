package org.telegram.ui.Components;

import android.text.TextPaint;
import org.telegram.messenger.AndroidUtilities;

public class URLSpanNoUnderlineBold
  extends URLSpanNoUnderline
{
  public URLSpanNoUnderlineBold(String paramString)
  {
    super(paramString);
  }
  
  public void updateDrawState(TextPaint paramTextPaint)
  {
    super.updateDrawState(paramTextPaint);
    paramTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    paramTextPaint.setUnderlineText(false);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Components\URLSpanNoUnderlineBold.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */