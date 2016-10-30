package org.telegram.ui.ActionBar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.Mihangram.Theming.MihanTheme;

public class BackDrawable
  extends Drawable
{
  String activity;
  private boolean alwaysClose;
  private boolean animationInProgress;
  private int currentAnimationTime;
  private float currentRotation;
  private float finalRotation;
  private DecelerateInterpolator interpolator = new DecelerateInterpolator();
  private long lastFrameTime;
  private Paint paint = new Paint(1);
  private boolean reverseAngle = false;
  SharedPreferences themePreferences;
  
  public BackDrawable(boolean paramBoolean)
  {
    this.paint.setColor(-1);
    this.paint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.alwaysClose = paramBoolean;
    this.themePreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    this.activity = null;
  }
  
  public BackDrawable(boolean paramBoolean, String paramString)
  {
    this.paint.setColor(-1);
    this.paint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.alwaysClose = paramBoolean;
    this.themePreferences = ApplicationLoader.applicationContext.getSharedPreferences("Mihantheme", 0);
    this.activity = paramString;
  }
  
  public void draw(Canvas paramCanvas)
  {
    label148:
    float f2;
    if (this.currentRotation != this.finalRotation)
    {
      if (this.lastFrameTime != 0L)
      {
        long l1 = System.currentTimeMillis();
        long l2 = this.lastFrameTime;
        this.currentAnimationTime = ((int)(this.currentAnimationTime + (l1 - l2)));
        if (this.currentAnimationTime >= 300) {
          this.currentRotation = this.finalRotation;
        }
      }
      else
      {
        this.lastFrameTime = System.currentTimeMillis();
        invalidateSelf();
      }
    }
    else
    {
      i = (int)(-138.0F * this.currentRotation);
      i = Color.rgb(i + 255, i + 255, i + 255);
      this.paint.setColor(i);
      if (this.finalRotation != 1.0F) {
        break label396;
      }
      this.paint.setColor(this.themePreferences.getInt("theme_chat_action_amicolor", -9276814));
      paramCanvas.save();
      paramCanvas.translate(getIntrinsicWidth() / 2, getIntrinsicHeight() / 2);
      f1 = this.currentRotation;
      if (this.alwaysClose) {
        break label457;
      }
      f2 = this.currentRotation;
      if (!this.reverseAngle) {
        break label449;
      }
    }
    label396:
    label449:
    for (int i = 65311;; i = 135)
    {
      paramCanvas.rotate(i * f2);
      paramCanvas.drawLine(-AndroidUtilities.dp(7.0F) - AndroidUtilities.dp(1.0F) * f1, 0.0F, AndroidUtilities.dp(8.0F), 0.0F, this.paint);
      f2 = -AndroidUtilities.dp(0.5F);
      float f3 = AndroidUtilities.dp(7.0F) + AndroidUtilities.dp(1.0F) * f1;
      float f4 = -AndroidUtilities.dp(7.0F) + AndroidUtilities.dp(7.0F) * f1;
      f1 = AndroidUtilities.dp(0.5F) - AndroidUtilities.dp(0.5F) * f1;
      paramCanvas.drawLine(f4, -f2, f1, -f3, this.paint);
      paramCanvas.drawLine(f4, f2, f1, f3, this.paint);
      paramCanvas.restore();
      return;
      if (this.currentRotation < this.finalRotation)
      {
        this.currentRotation = (this.interpolator.getInterpolation(this.currentAnimationTime / 300.0F) * this.finalRotation);
        break;
      }
      this.currentRotation = (1.0F - this.interpolator.getInterpolation(this.currentAnimationTime / 300.0F));
      break;
      if (this.activity == "chat")
      {
        this.paint.setColor(this.themePreferences.getInt("theme_chat_action_icolor", MihanTheme.getActionBarIconColor(this.themePreferences)));
        break label148;
      }
      this.paint.setColor(MihanTheme.contrastColor(MihanTheme.getThemeColor()));
      break label148;
    }
    label457:
    float f1 = this.currentRotation;
    if (this.reverseAngle) {}
    for (i = 65356;; i = 180)
    {
      paramCanvas.rotate(i * f1 + 135.0F);
      f1 = 1.0F;
      break;
    }
  }
  
  public int getIntrinsicHeight()
  {
    return AndroidUtilities.dp(24.0F);
  }
  
  public int getIntrinsicWidth()
  {
    return AndroidUtilities.dp(24.0F);
  }
  
  public int getOpacity()
  {
    return -2;
  }
  
  public void setAlpha(int paramInt) {}
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
  
  public void setRotation(float paramFloat, boolean paramBoolean)
  {
    this.lastFrameTime = 0L;
    if (this.currentRotation == 1.0F)
    {
      this.reverseAngle = true;
      this.lastFrameTime = 0L;
      if (!paramBoolean) {
        break label100;
      }
      if (this.currentRotation >= paramFloat) {
        break label83;
      }
      this.currentAnimationTime = ((int)(this.currentRotation * 300.0F));
      label49:
      this.lastFrameTime = System.currentTimeMillis();
    }
    for (this.finalRotation = paramFloat;; this.finalRotation = paramFloat)
    {
      invalidateSelf();
      return;
      if (this.currentRotation != 0.0F) {
        break;
      }
      this.reverseAngle = false;
      break;
      label83:
      this.currentAnimationTime = ((int)((1.0F - this.currentRotation) * 300.0F));
      break label49;
      label100:
      this.currentRotation = paramFloat;
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\ActionBar\BackDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */