package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_dialog;

public class DialogObject
{
  public static boolean isChannel(TLRPC.TL_dialog paramTL_dialog)
  {
    return (paramTL_dialog != null) && ((paramTL_dialog.flags & 0x1) != 0);
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\messenger\DialogObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */