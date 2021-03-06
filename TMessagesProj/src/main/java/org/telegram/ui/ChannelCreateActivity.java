package org.telegram.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_channels_checkUsername;
import org.telegram.tgnet.TLRPC.TL_channels_exportInvite;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputChannelEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.ContactsAdapter;
import org.telegram.ui.Adapters.SearchAdapter;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChipSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LetterSectionsListView;
import org.telegram.ui.Supergram.Theming.MihanTheme;

public class ChannelCreateActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, AvatarUpdater.AvatarUpdaterDelegate
{
  private static final int done_button = 1;
  private ArrayList<ChipSpan> allSpans = new ArrayList();
  private TLRPC.FileLocation avatar;
  private AvatarDrawable avatarDrawable;
  private BackupImageView avatarImage;
  private AvatarUpdater avatarUpdater;
  private int beforeChangeIndex;
  private boolean canCreatePublic = true;
  private CharSequence changeString;
  private int chatId;
  private int checkReqId = 0;
  private Runnable checkRunnable = null;
  private TextView checkTextView;
  private boolean createAfterUpload;
  private int currentStep;
  private EditText descriptionTextView;
  private View doneButton;
  private boolean donePressed;
  private TextView emptyTextView;
  private HeaderCell headerCell;
  private boolean ignoreChange;
  private TLRPC.ExportedChatInvite invite;
  private boolean isPrivate = false;
  private String lastCheckName = null;
  private boolean lastNameAvailable = false;
  private LinearLayout linkContainer;
  private LetterSectionsListView listView;
  private ContactsAdapter listViewAdapter;
  private boolean loadingInvite;
  private EditText nameTextView;
  private String nameToSet = null;
  private TextBlockCell privateContainer;
  private ProgressDialog progressDialog = null;
  private LinearLayout publicContainer;
  private RadioButtonCell radioButtonCell1;
  private RadioButtonCell radioButtonCell2;
  private SearchAdapter searchListViewAdapter;
  private boolean searchWas;
  private boolean searching;
  private HashMap<Integer, ChipSpan> selectedContacts = new HashMap();
  private TextInfoPrivacyCell typeInfoCell;
  private TLRPC.InputFile uploadedAvatar;
  
  public ChannelCreateActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.currentStep = paramBundle.getInt("step", 0);
    if (this.currentStep == 0)
    {
      this.avatarDrawable = new AvatarDrawable();
      this.avatarUpdater = new AvatarUpdater();
      paramBundle = new TLRPC.TL_channels_checkUsername();
      paramBundle.username = "1";
      paramBundle.channel = new TLRPC.TL_inputChannelEmpty();
      ConnectionsManager.getInstance().sendRequest(paramBundle, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              ChannelCreateActivity localChannelCreateActivity = ChannelCreateActivity.this;
              if ((paramAnonymousTL_error == null) || (!paramAnonymousTL_error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH"))) {}
              for (boolean bool = true;; bool = false)
              {
                ChannelCreateActivity.access$002(localChannelCreateActivity, bool);
                return;
              }
            }
          });
        }
      });
      return;
    }
    if (this.currentStep == 1)
    {
      this.canCreatePublic = paramBundle.getBoolean("canCreatePublic", true);
      if (this.canCreatePublic) {
        break label194;
      }
    }
    for (;;)
    {
      this.isPrivate = bool;
      this.chatId = paramBundle.getInt("chat_id", 0);
      return;
      label194:
      bool = false;
    }
  }
  
  private boolean checkUserName(final String paramString, boolean paramBoolean)
  {
    if ((paramString != null) && (paramString.length() > 0))
    {
      this.checkTextView.setVisibility(0);
      if ((!paramBoolean) || (paramString.length() != 0)) {
        break label44;
      }
    }
    label44:
    do
    {
      return true;
      this.checkTextView.setVisibility(8);
      break;
      if (this.checkRunnable != null)
      {
        AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
        this.checkRunnable = null;
        this.lastCheckName = null;
        if (this.checkReqId != 0) {
          ConnectionsManager.getInstance().cancelRequest(this.checkReqId, true);
        }
      }
      this.lastNameAvailable = false;
      if (paramString != null)
      {
        if ((paramString.startsWith("_")) || (paramString.endsWith("_")))
        {
          this.checkTextView.setText(LocaleController.getString("LinkInvalid", 2131165897));
          this.checkTextView.setTextColor(-3198928);
          return false;
        }
        int i = 0;
        while (i < paramString.length())
        {
          int j = paramString.charAt(i);
          if ((i == 0) && (j >= 48) && (j <= 57))
          {
            if (paramBoolean) {
              showErrorAlert(LocaleController.getString("LinkInvalidStartNumber", 2131165901));
            }
            for (;;)
            {
              return false;
              this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", 2131165901));
              this.checkTextView.setTextColor(-3198928);
            }
          }
          if (((j < 48) || (j > 57)) && ((j < 97) || (j > 122)) && ((j < 65) || (j > 90)) && (j != 95))
          {
            if (paramBoolean) {
              showErrorAlert(LocaleController.getString("LinkInvalid", 2131165897));
            }
            for (;;)
            {
              return false;
              this.checkTextView.setText(LocaleController.getString("LinkInvalid", 2131165897));
              this.checkTextView.setTextColor(-3198928);
            }
          }
          i += 1;
        }
      }
      if ((paramString == null) || (paramString.length() < 5))
      {
        if (paramBoolean) {
          showErrorAlert(LocaleController.getString("LinkInvalidShort", 2131165899));
        }
        for (;;)
        {
          return false;
          this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", 2131165899));
          this.checkTextView.setTextColor(-3198928);
        }
      }
      if (paramString.length() > 32)
      {
        if (paramBoolean) {
          showErrorAlert(LocaleController.getString("LinkInvalidLong", 2131165898));
        }
        for (;;)
        {
          return false;
          this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", 2131165898));
          this.checkTextView.setTextColor(-3198928);
        }
      }
    } while (paramBoolean);
    this.checkTextView.setText(LocaleController.getString("LinkChecking", 2131165893));
    this.checkTextView.setTextColor(-9605774);
    this.lastCheckName = paramString;
    this.checkRunnable = new Runnable()
    {
      public void run()
      {
        TLRPC.TL_channels_checkUsername localTL_channels_checkUsername = new TLRPC.TL_channels_checkUsername();
        localTL_channels_checkUsername.username = paramString;
        localTL_channels_checkUsername.channel = MessagesController.getInputChannel(ChannelCreateActivity.this.chatId);
        ChannelCreateActivity.access$3702(ChannelCreateActivity.this, ConnectionsManager.getInstance().sendRequest(localTL_channels_checkUsername, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymous2TLObject, final TLRPC.TL_error paramAnonymous2TL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                ChannelCreateActivity.access$3702(ChannelCreateActivity.this, 0);
                if ((ChannelCreateActivity.this.lastCheckName != null) && (ChannelCreateActivity.this.lastCheckName.equals(ChannelCreateActivity.18.this.val$name)))
                {
                  if ((paramAnonymous2TL_error == null) && ((paramAnonymous2TLObject instanceof TLRPC.TL_boolTrue)))
                  {
                    ChannelCreateActivity.this.checkTextView.setText(LocaleController.formatString("LinkAvailable", 2131165892, new Object[] { ChannelCreateActivity.18.this.val$name }));
                    ChannelCreateActivity.this.checkTextView.setTextColor(-14248148);
                    ChannelCreateActivity.access$902(ChannelCreateActivity.this, true);
                  }
                }
                else {
                  return;
                }
                if ((paramAnonymous2TL_error != null) && (paramAnonymous2TL_error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH"))) {
                  ChannelCreateActivity.this.checkTextView.setText(LocaleController.getString("ChannelPublicLimitReached", 2131165511));
                }
                for (;;)
                {
                  ChannelCreateActivity.this.checkTextView.setTextColor(-3198928);
                  ChannelCreateActivity.access$902(ChannelCreateActivity.this, false);
                  return;
                  ChannelCreateActivity.this.checkTextView.setText(LocaleController.getString("LinkInUse", 2131165895));
                }
              }
            });
          }
        }, 2));
      }
    };
    AndroidUtilities.runOnUIThread(this.checkRunnable, 300L);
    return true;
  }
  
  private ChipSpan createAndPutChipForUser(TLRPC.User paramUser)
  {
    Object localObject2;
    Object localObject1;
    try
    {
      Object localObject3 = ((LayoutInflater)ApplicationLoader.applicationContext.getSystemService("layout_inflater")).inflate(2130903081, null);
      TextView localTextView = (TextView)((View)localObject3).findViewById(2131624131);
      localObject2 = UserObject.getUserName(paramUser);
      localObject1 = localObject2;
      if (((String)localObject2).length() == 0)
      {
        localObject1 = localObject2;
        if (paramUser.phone != null)
        {
          localObject1 = localObject2;
          if (paramUser.phone.length() != 0) {
            localObject1 = PhoneFormat.getInstance().format("+" + paramUser.phone);
          }
        }
      }
      localTextView.setText((String)localObject1 + ", ");
      int i = View.MeasureSpec.makeMeasureSpec(0, 0);
      ((View)localObject3).measure(i, i);
      ((View)localObject3).layout(0, 0, ((View)localObject3).getMeasuredWidth(), ((View)localObject3).getMeasuredHeight());
      localObject1 = Bitmap.createBitmap(((View)localObject3).getWidth(), ((View)localObject3).getHeight(), Bitmap.Config.ARGB_8888);
      localObject2 = new Canvas((Bitmap)localObject1);
      ((Canvas)localObject2).translate(-((View)localObject3).getScrollX(), -((View)localObject3).getScrollY());
      ((View)localObject3).draw((Canvas)localObject2);
      ((View)localObject3).setDrawingCacheEnabled(true);
      ((View)localObject3).getDrawingCache().copy(Bitmap.Config.ARGB_8888, true);
      ((View)localObject3).destroyDrawingCache();
      localObject2 = new BitmapDrawable((Bitmap)localObject1);
      ((BitmapDrawable)localObject2).setBounds(0, 0, ((Bitmap)localObject1).getWidth(), ((Bitmap)localObject1).getHeight());
      localObject1 = new SpannableStringBuilder("");
      localObject2 = new ChipSpan((Drawable)localObject2, 1);
      this.allSpans.add(localObject2);
      this.selectedContacts.put(Integer.valueOf(paramUser.id), localObject2);
      paramUser = this.allSpans.iterator();
      while (paramUser.hasNext())
      {
        localObject3 = (ImageSpan)paramUser.next();
        ((SpannableStringBuilder)localObject1).append("<<");
        ((SpannableStringBuilder)localObject1).setSpan(localObject3, ((SpannableStringBuilder)localObject1).length() - 2, ((SpannableStringBuilder)localObject1).length(), 33);
      }
      this.nameTextView.setText((CharSequence)localObject1);
    }
    catch (Exception paramUser)
    {
      FileLog.e("tmessages", paramUser);
      return null;
    }
    this.nameTextView.setSelection(((SpannableStringBuilder)localObject1).length());
    return (ChipSpan)localObject2;
  }
  
  private void generateLink()
  {
    if ((this.loadingInvite) || (this.invite != null)) {
      return;
    }
    this.loadingInvite = true;
    TLRPC.TL_channels_exportInvite localTL_channels_exportInvite = new TLRPC.TL_channels_exportInvite();
    localTL_channels_exportInvite.channel = MessagesController.getInputChannel(this.chatId);
    ConnectionsManager.getInstance().sendRequest(localTL_channels_exportInvite, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if (paramAnonymousTL_error == null) {
              ChannelCreateActivity.access$2102(ChannelCreateActivity.this, (TLRPC.ExportedChatInvite)paramAnonymousTLObject);
            }
            ChannelCreateActivity.access$3502(ChannelCreateActivity.this, false);
            TextBlockCell localTextBlockCell = ChannelCreateActivity.this.privateContainer;
            if (ChannelCreateActivity.this.invite != null) {}
            for (String str = ChannelCreateActivity.this.invite.link;; str = LocaleController.getString("Loading", 2131165905))
            {
              localTextBlockCell.setText(str, false);
              return;
            }
          }
        });
      }
    });
  }
  
  private void showErrorAlert(String paramString)
  {
    if (getParentActivity() == null) {
      return;
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165338));
    int i = -1;
    switch (paramString.hashCode())
    {
    default: 
      switch (i)
      {
      default: 
        localBuilder.setMessage(LocaleController.getString("ErrorOccurred", 2131165672));
      }
      break;
    }
    for (;;)
    {
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166111), null);
      showDialog(localBuilder.create());
      return;
      if (!paramString.equals("USERNAME_INVALID")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("USERNAME_OCCUPIED")) {
        break;
      }
      i = 1;
      break;
      if (!paramString.equals("USERNAMES_UNAVAILABLE")) {
        break;
      }
      i = 2;
      break;
      localBuilder.setMessage(LocaleController.getString("LinkInvalid", 2131165897));
      continue;
      localBuilder.setMessage(LocaleController.getString("LinkInUse", 2131165895));
      continue;
      localBuilder.setMessage(LocaleController.getString("FeatureUnavailable", 2131165685));
    }
  }
  
  private void updatePrivatePublic()
  {
    int j = 0;
    Object localObject1 = this.radioButtonCell1;
    boolean bool;
    if (!this.isPrivate)
    {
      bool = true;
      ((RadioButtonCell)localObject1).setChecked(bool, true);
      this.radioButtonCell2.setChecked(this.isPrivate, true);
      Object localObject2 = this.typeInfoCell;
      if (!this.isPrivate) {
        break label246;
      }
      localObject1 = LocaleController.getString("ChannelPrivateLinkHelp", 2131165507);
      label60:
      ((TextInfoPrivacyCell)localObject2).setText((CharSequence)localObject1);
      localObject2 = this.headerCell;
      if (!this.isPrivate) {
        break label260;
      }
      localObject1 = LocaleController.getString("ChannelInviteLinkTitle", 2131165466);
      label91:
      ((HeaderCell)localObject2).setText((String)localObject1);
      localObject1 = this.publicContainer;
      if (!this.isPrivate) {
        break label274;
      }
      i = 8;
      label114:
      ((LinearLayout)localObject1).setVisibility(i);
      localObject1 = this.privateContainer;
      if (!this.isPrivate) {
        break label279;
      }
      i = 0;
      label135:
      ((TextBlockCell)localObject1).setVisibility(i);
      localObject1 = this.linkContainer;
      if (!this.isPrivate) {
        break label285;
      }
      i = 0;
      label156:
      ((LinearLayout)localObject1).setPadding(0, 0, 0, i);
      localObject2 = this.privateContainer;
      if (this.invite == null) {
        break label295;
      }
      localObject1 = this.invite.link;
      label187:
      ((TextBlockCell)localObject2).setText((String)localObject1, false);
      this.nameTextView.clearFocus();
      localObject1 = this.checkTextView;
      if ((this.isPrivate) || (this.checkTextView.length() == 0)) {
        break label309;
      }
    }
    label246:
    label260:
    label274:
    label279:
    label285:
    label295:
    label309:
    for (int i = j;; i = 8)
    {
      ((TextView)localObject1).setVisibility(i);
      AndroidUtilities.hideKeyboard(this.nameTextView);
      return;
      bool = false;
      break;
      localObject1 = LocaleController.getString("ChannelUsernameHelp", 2131165528);
      break label60;
      localObject1 = LocaleController.getString("ChannelLinkTitle", 2131165474);
      break label91;
      i = 0;
      break label114;
      i = 8;
      break label135;
      i = AndroidUtilities.dp(7.0F);
      break label156;
      localObject1 = LocaleController.getString("Loading", 2131165905);
      break label187;
    }
  }
  
  private void updateVisibleRows(int paramInt)
  {
    if (this.listView == null) {}
    for (;;)
    {
      return;
      int j = this.listView.getChildCount();
      int i = 0;
      while (i < j)
      {
        View localView = this.listView.getChildAt(i);
        if ((localView instanceof UserCell)) {
          ((UserCell)localView).update(paramInt);
        }
        i += 1;
      }
    }
  }
  
  public View createView(Context paramContext)
  {
    this.searching = false;
    this.searchWas = false;
    this.actionBar.setBackButtonImage(2130837810);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(final int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          ChannelCreateActivity.this.finishFragment();
        }
        do
        {
          do
          {
            return;
          } while (paramAnonymousInt != 1);
          if (ChannelCreateActivity.this.currentStep != 0) {
            break;
          }
        } while (ChannelCreateActivity.this.donePressed);
        if (ChannelCreateActivity.this.nameTextView.length() == 0)
        {
          localObject1 = (Vibrator)ChannelCreateActivity.this.getParentActivity().getSystemService("vibrator");
          if (localObject1 != null) {
            ((Vibrator)localObject1).vibrate(200L);
          }
          AndroidUtilities.shakeView(ChannelCreateActivity.this.nameTextView, 2.0F, 0);
          return;
        }
        ChannelCreateActivity.access$202(ChannelCreateActivity.this, true);
        if (ChannelCreateActivity.this.avatarUpdater.uploadingAvatar != null)
        {
          ChannelCreateActivity.access$502(ChannelCreateActivity.this, true);
          ChannelCreateActivity.access$602(ChannelCreateActivity.this, new ProgressDialog(ChannelCreateActivity.this.getParentActivity()));
          ChannelCreateActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", 2131165905));
          ChannelCreateActivity.this.progressDialog.setCanceledOnTouchOutside(false);
          ChannelCreateActivity.this.progressDialog.setCancelable(false);
          ChannelCreateActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", 2131165426), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
            {
              ChannelCreateActivity.access$502(ChannelCreateActivity.this, false);
              ChannelCreateActivity.access$602(ChannelCreateActivity.this, null);
              ChannelCreateActivity.access$202(ChannelCreateActivity.this, false);
              try
              {
                paramAnonymous2DialogInterface.dismiss();
                return;
              }
              catch (Exception paramAnonymous2DialogInterface)
              {
                FileLog.e("tmessages", paramAnonymous2DialogInterface);
              }
            }
          });
          ChannelCreateActivity.this.progressDialog.show();
          return;
        }
        paramAnonymousInt = MessagesController.getInstance().createChat(ChannelCreateActivity.this.nameTextView.getText().toString(), new ArrayList(), ChannelCreateActivity.this.descriptionTextView.getText().toString(), 2, ChannelCreateActivity.this);
        ChannelCreateActivity.access$602(ChannelCreateActivity.this, new ProgressDialog(ChannelCreateActivity.this.getParentActivity()));
        ChannelCreateActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", 2131165905));
        ChannelCreateActivity.this.progressDialog.setCanceledOnTouchOutside(false);
        ChannelCreateActivity.this.progressDialog.setCancelable(false);
        ChannelCreateActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", 2131165426), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
          {
            ConnectionsManager.getInstance().cancelRequest(paramAnonymousInt, true);
            ChannelCreateActivity.access$202(ChannelCreateActivity.this, false);
            try
            {
              paramAnonymous2DialogInterface.dismiss();
              return;
            }
            catch (Exception paramAnonymous2DialogInterface)
            {
              FileLog.e("tmessages", paramAnonymous2DialogInterface);
            }
          }
        });
        ChannelCreateActivity.this.progressDialog.show();
        return;
        if (ChannelCreateActivity.this.currentStep == 1)
        {
          if (!ChannelCreateActivity.this.isPrivate)
          {
            if (ChannelCreateActivity.this.nameTextView.length() == 0)
            {
              localObject1 = new AlertDialog.Builder(ChannelCreateActivity.this.getParentActivity());
              ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", 2131165338));
              ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("ChannelPublicEmptyUsername", 2131165509));
              ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("Close", 2131165551), null);
              ChannelCreateActivity.this.showDialog(((AlertDialog.Builder)localObject1).create());
              return;
            }
            if (!ChannelCreateActivity.this.lastNameAvailable)
            {
              localObject1 = (Vibrator)ChannelCreateActivity.this.getParentActivity().getSystemService("vibrator");
              if (localObject1 != null) {
                ((Vibrator)localObject1).vibrate(200L);
              }
              AndroidUtilities.shakeView(ChannelCreateActivity.this.checkTextView, 2.0F, 0);
              return;
            }
            MessagesController.getInstance().updateChannelUserName(ChannelCreateActivity.this.chatId, ChannelCreateActivity.this.lastCheckName);
          }
          localObject1 = new Bundle();
          ((Bundle)localObject1).putInt("step", 2);
          ((Bundle)localObject1).putInt("chat_id", ChannelCreateActivity.this.chatId);
          ChannelCreateActivity.this.presentFragment(new ChannelCreateActivity((Bundle)localObject1), true);
          return;
        }
        Object localObject1 = new ArrayList();
        Iterator localIterator = ChannelCreateActivity.this.selectedContacts.keySet().iterator();
        while (localIterator.hasNext())
        {
          Object localObject2 = (Integer)localIterator.next();
          localObject2 = MessagesController.getInputUser(MessagesController.getInstance().getUser((Integer)localObject2));
          if (localObject2 != null) {
            ((ArrayList)localObject1).add(localObject2);
          }
        }
        MessagesController.getInstance().addUsersToChannel(ChannelCreateActivity.this.chatId, (ArrayList)localObject1, null);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        localObject1 = new Bundle();
        ((Bundle)localObject1).putInt("chat_id", ChannelCreateActivity.this.chatId);
        ChannelCreateActivity.this.presentFragment(new ChatActivity((Bundle)localObject1), true);
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837844, AndroidUtilities.dp(56.0F));
    LinearLayout localLinearLayout;
    label259:
    float f1;
    label267:
    float f2;
    if (this.currentStep != 2)
    {
      this.fragmentView = new ScrollView(paramContext);
      localObject1 = (ScrollView)this.fragmentView;
      ((ScrollView)localObject1).setFillViewport(true);
      localLinearLayout = new LinearLayout(paramContext);
      ((ScrollView)localObject1).addView(localLinearLayout, new FrameLayout.LayoutParams(-1, -2));
      localLinearLayout.setOrientation(1);
      if (this.currentStep != 0) {
        break label982;
      }
      this.actionBar.setTitle(LocaleController.getString("NewChannel", 2131165988));
      this.fragmentView.setBackgroundColor(-1);
      localObject1 = new FrameLayout(paramContext);
      localLinearLayout.addView((View)localObject1, LayoutHelper.createLinear(-1, -2));
      this.avatarImage = new BackupImageView(paramContext);
      this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0F));
      this.avatarDrawable.setInfo(5, null, null, false);
      this.avatarDrawable.setDrawPhoto(true);
      this.avatarImage.setImageDrawable(this.avatarDrawable);
      localObject2 = this.avatarImage;
      if (!LocaleController.isRTL) {
        break label926;
      }
      i = 5;
      if (!LocaleController.isRTL) {
        break label932;
      }
      f1 = 0.0F;
      if (!LocaleController.isRTL) {
        break label939;
      }
      f2 = 16.0F;
      label277:
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(64, 64.0F, i | 0x30, f1, 12.0F, f2, 12.0F));
      this.avatarImage.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (ChannelCreateActivity.this.getParentActivity() == null) {
            return;
          }
          AlertDialog.Builder localBuilder = new AlertDialog.Builder(ChannelCreateActivity.this.getParentActivity());
          if (ChannelCreateActivity.this.avatar != null)
          {
            paramAnonymousView = new CharSequence[3];
            paramAnonymousView[0] = LocaleController.getString("FromCamera", 2131165763);
            paramAnonymousView[1] = LocaleController.getString("FromGalley", 2131165770);
            paramAnonymousView[2] = LocaleController.getString("DeletePhoto", 2131165617);
          }
          for (;;)
          {
            localBuilder.setItems(paramAnonymousView, new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
              {
                if (paramAnonymous2Int == 0) {
                  ChannelCreateActivity.this.avatarUpdater.openCamera();
                }
                do
                {
                  return;
                  if (paramAnonymous2Int == 1)
                  {
                    ChannelCreateActivity.this.avatarUpdater.openGallery();
                    return;
                  }
                } while (paramAnonymous2Int != 2);
                ChannelCreateActivity.access$1402(ChannelCreateActivity.this, null);
                ChannelCreateActivity.access$1502(ChannelCreateActivity.this, null);
                ChannelCreateActivity.this.avatarImage.setImage(ChannelCreateActivity.this.avatar, "50_50", ChannelCreateActivity.this.avatarDrawable);
              }
            });
            ChannelCreateActivity.this.showDialog(localBuilder.create());
            return;
            paramAnonymousView = new CharSequence[2];
            paramAnonymousView[0] = LocaleController.getString("FromCamera", 2131165763);
            paramAnonymousView[1] = LocaleController.getString("FromGalley", 2131165770);
          }
        }
      });
      this.nameTextView = new EditText(paramContext);
      this.nameTextView.setTypeface(MihanTheme.getMihanTypeFace());
      this.nameTextView.setHint(LocaleController.getString("EnterChannelName", 2131165665));
      if (this.nameToSet != null)
      {
        this.nameTextView.setText(this.nameToSet);
        this.nameToSet = null;
      }
      this.nameTextView.setMaxLines(4);
      localObject2 = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label944;
      }
      i = 5;
      label404:
      ((EditText)localObject2).setGravity(i | 0x10);
      this.nameTextView.setTextSize(1, 16.0F);
      this.nameTextView.setHintTextColor(-6842473);
      this.nameTextView.setImeOptions(268435456);
      this.nameTextView.setInputType(16385);
      localObject2 = new InputFilter.LengthFilter(100);
      this.nameTextView.setFilters(new InputFilter[] { localObject2 });
      this.nameTextView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0F));
      AndroidUtilities.clearCursorDrawable(this.nameTextView);
      this.nameTextView.setTextColor(-14606047);
      localObject2 = this.nameTextView;
      if (!LocaleController.isRTL) {
        break label950;
      }
      f1 = 16.0F;
      label531:
      if (!LocaleController.isRTL) {
        break label957;
      }
      f2 = 96.0F;
      label541:
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, -2.0F, 16, f1, 0.0F, f2, 0.0F));
      this.nameTextView.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramAnonymousEditable)
        {
          AvatarDrawable localAvatarDrawable = ChannelCreateActivity.this.avatarDrawable;
          if (ChannelCreateActivity.this.nameTextView.length() > 0) {}
          for (paramAnonymousEditable = ChannelCreateActivity.this.nameTextView.getText().toString();; paramAnonymousEditable = null)
          {
            localAvatarDrawable.setInfo(5, paramAnonymousEditable, null, false);
            ChannelCreateActivity.this.avatarImage.invalidate();
            return;
          }
        }
        
        public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      });
      this.descriptionTextView = new EditText(paramContext);
      this.descriptionTextView.setTypeface(MihanTheme.getMihanTypeFace());
      this.descriptionTextView.setTextSize(1, 18.0F);
      this.descriptionTextView.setHintTextColor(-6842473);
      this.descriptionTextView.setTextColor(-14606047);
      this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0F));
      localObject1 = this.descriptionTextView;
      if (!LocaleController.isRTL) {
        break label964;
      }
      i = 5;
      label660:
      ((EditText)localObject1).setGravity(i);
      this.descriptionTextView.setInputType(180225);
      this.descriptionTextView.setImeOptions(6);
      localObject1 = new InputFilter.LengthFilter(120);
      this.descriptionTextView.setFilters(new InputFilter[] { localObject1 });
      this.descriptionTextView.setHint(LocaleController.getString("DescriptionPlaceholder", 2131165624));
      AndroidUtilities.clearCursorDrawable(this.descriptionTextView);
      localLinearLayout.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 24.0F, 18.0F, 24.0F, 0.0F));
      this.descriptionTextView.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
        {
          if ((paramAnonymousInt == 6) && (ChannelCreateActivity.this.doneButton != null))
          {
            ChannelCreateActivity.this.doneButton.performClick();
            return true;
          }
          return false;
        }
      });
      this.descriptionTextView.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramAnonymousEditable) {}
        
        public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      });
      paramContext = new TextView(paramContext);
      paramContext.setTypeface(MihanTheme.getMihanTypeFace());
      paramContext.setTextSize(1, 15.0F);
      paramContext.setTextColor(-9605774);
      if (!LocaleController.isRTL) {
        break label970;
      }
      i = 5;
      label831:
      paramContext.setGravity(i);
      paramContext.setText(LocaleController.getString("DescriptionInfo", 2131165621));
      if (!LocaleController.isRTL) {
        break label976;
      }
      i = 5;
      label859:
      localLinearLayout.addView(paramContext, LayoutHelper.createLinear(-2, -2, i, 24, 10, 24, 20));
    }
    label926:
    label932:
    label939:
    label944:
    label950:
    label957:
    label964:
    label970:
    label976:
    label982:
    label1111:
    label1726:
    label1836:
    label1842:
    do
    {
      return this.fragmentView;
      this.fragmentView = new LinearLayout(paramContext);
      this.fragmentView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      localLinearLayout = (LinearLayout)this.fragmentView;
      break;
      i = 3;
      break label259;
      f1 = 16.0F;
      break label267;
      f2 = 0.0F;
      break label277;
      i = 3;
      break label404;
      f1 = 96.0F;
      break label531;
      f2 = 16.0F;
      break label541;
      i = 3;
      break label660;
      i = 3;
      break label831;
      i = 3;
      break label859;
      if (this.currentStep == 1)
      {
        this.actionBar.setTitle(LocaleController.getString("ChannelSettings", 2131165514));
        this.fragmentView.setBackgroundColor(-986896);
        localObject1 = new LinearLayout(paramContext);
        ((LinearLayout)localObject1).setOrientation(1);
        ((LinearLayout)localObject1).setBackgroundColor(-1);
        localLinearLayout.addView((View)localObject1, LayoutHelper.createLinear(-1, -2));
        this.radioButtonCell1 = new RadioButtonCell(paramContext);
        this.radioButtonCell1.setBackgroundResource(2130837932);
        localObject2 = this.radioButtonCell1;
        String str1 = LocaleController.getString("ChannelPublic", 2131165508);
        String str2 = LocaleController.getString("ChannelPublicInfo", 2131165510);
        boolean bool;
        if (!this.isPrivate)
        {
          bool = true;
          ((RadioButtonCell)localObject2).setTextAndValue(str1, str2, bool, false);
          ((LinearLayout)localObject1).addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
          this.radioButtonCell1.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              if (!ChannelCreateActivity.this.canCreatePublic)
              {
                paramAnonymousView = new AlertDialog.Builder(ChannelCreateActivity.this.getParentActivity());
                paramAnonymousView.setTitle(LocaleController.getString("AppName", 2131165338));
                paramAnonymousView.setMessage(LocaleController.getString("ChannelPublicLimitReached", 2131165511));
                paramAnonymousView.setPositiveButton(LocaleController.getString("OK", 2131166111), null);
                ChannelCreateActivity.this.showDialog(paramAnonymousView.create());
              }
              while (!ChannelCreateActivity.this.isPrivate) {
                return;
              }
              ChannelCreateActivity.access$802(ChannelCreateActivity.this, false);
              ChannelCreateActivity.this.updatePrivatePublic();
            }
          });
          this.radioButtonCell2 = new RadioButtonCell(paramContext);
          this.radioButtonCell2.setBackgroundResource(2130837932);
          this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", 2131165505), LocaleController.getString("ChannelPrivateInfo", 2131165506), this.isPrivate, false);
          ((LinearLayout)localObject1).addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
          this.radioButtonCell2.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              if (ChannelCreateActivity.this.isPrivate) {
                return;
              }
              ChannelCreateActivity.access$802(ChannelCreateActivity.this, true);
              ChannelCreateActivity.this.updatePrivatePublic();
            }
          });
          localLinearLayout.addView(new ShadowSectionCell(paramContext), LayoutHelper.createLinear(-1, -2));
          this.linkContainer = new LinearLayout(paramContext);
          this.linkContainer.setOrientation(1);
          this.linkContainer.setBackgroundColor(-1);
          localLinearLayout.addView(this.linkContainer, LayoutHelper.createLinear(-1, -2));
          this.headerCell = new HeaderCell(paramContext);
          this.linkContainer.addView(this.headerCell);
          this.publicContainer = new LinearLayout(paramContext);
          this.publicContainer.setOrientation(0);
          this.linkContainer.addView(this.publicContainer, LayoutHelper.createLinear(-1, 36, 17.0F, 7.0F, 17.0F, 0.0F));
          localObject1 = new EditText(paramContext);
          ((EditText)localObject1).setText("telegram.me/");
          ((EditText)localObject1).setTextSize(1, 18.0F);
          ((EditText)localObject1).setHintTextColor(-6842473);
          ((EditText)localObject1).setTextColor(-14606047);
          ((EditText)localObject1).setMaxLines(1);
          ((EditText)localObject1).setLines(1);
          ((EditText)localObject1).setEnabled(false);
          ((EditText)localObject1).setBackgroundDrawable(null);
          ((EditText)localObject1).setPadding(0, 0, 0, 0);
          ((EditText)localObject1).setSingleLine(true);
          ((EditText)localObject1).setInputType(163840);
          ((EditText)localObject1).setImeOptions(6);
          this.publicContainer.addView((View)localObject1, LayoutHelper.createLinear(-2, 36));
          this.nameTextView = new EditText(paramContext);
          this.nameTextView.setTextSize(1, 18.0F);
          this.nameTextView.setHintTextColor(-6842473);
          this.nameTextView.setTextColor(-14606047);
          this.nameTextView.setMaxLines(1);
          this.nameTextView.setLines(1);
          this.nameTextView.setBackgroundDrawable(null);
          this.nameTextView.setPadding(0, 0, 0, 0);
          this.nameTextView.setSingleLine(true);
          this.nameTextView.setInputType(163872);
          this.nameTextView.setImeOptions(6);
          this.nameTextView.setHint(LocaleController.getString("ChannelUsernamePlaceholder", 2131165529));
          AndroidUtilities.clearCursorDrawable(this.nameTextView);
          this.publicContainer.addView(this.nameTextView, LayoutHelper.createLinear(-1, 36));
          this.nameTextView.addTextChangedListener(new TextWatcher()
          {
            public void afterTextChanged(Editable paramAnonymousEditable) {}
            
            public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
            
            public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
            {
              ChannelCreateActivity.this.checkUserName(ChannelCreateActivity.this.nameTextView.getText().toString(), false);
            }
          });
          this.privateContainer = new TextBlockCell(paramContext);
          this.privateContainer.setBackgroundResource(2130837932);
          this.linkContainer.addView(this.privateContainer);
          this.privateContainer.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              if (ChannelCreateActivity.this.invite == null) {
                return;
              }
              try
              {
                ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", ChannelCreateActivity.this.invite.link));
                Toast.makeText(ChannelCreateActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", 2131165894), 0).show();
                return;
              }
              catch (Exception paramAnonymousView)
              {
                FileLog.e("tmessages", paramAnonymousView);
              }
            }
          });
          this.checkTextView = new TextView(paramContext);
          this.checkTextView.setTextSize(1, 15.0F);
          localObject1 = this.checkTextView;
          if (!LocaleController.isRTL) {
            break label1836;
          }
          i = 5;
          ((TextView)localObject1).setGravity(i);
          this.checkTextView.setVisibility(8);
          localObject1 = this.linkContainer;
          localObject2 = this.checkTextView;
          if (!LocaleController.isRTL) {
            break label1842;
          }
        }
        for (i = 5;; i = 3)
        {
          ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(-2, -2, i, 17, 3, 17, 7));
          this.typeInfoCell = new TextInfoPrivacyCell(paramContext);
          this.typeInfoCell.setBackgroundResource(2130837800);
          localLinearLayout.addView(this.typeInfoCell, LayoutHelper.createLinear(-1, -2));
          updatePrivatePublic();
          break;
          bool = false;
          break label1111;
          i = 3;
          break label1726;
        }
      }
    } while (this.currentStep != 2);
    this.actionBar.setTitle(LocaleController.getString("ChannelAddMembers", 2131165441));
    this.actionBar.setSubtitle(LocaleController.formatPluralString("Members", this.selectedContacts.size()));
    this.searchListViewAdapter = new SearchAdapter(paramContext, null, false, false, false, false);
    this.searchListViewAdapter.setCheckedMap(this.selectedContacts);
    this.searchListViewAdapter.setUseUserCell(true);
    this.listViewAdapter = new ContactsAdapter(paramContext, 1, false, null, false);
    this.listViewAdapter.setCheckedMap(this.selectedContacts);
    Object localObject1 = new FrameLayout(paramContext);
    localLinearLayout.addView((View)localObject1, LayoutHelper.createLinear(-1, -2));
    this.nameTextView = new EditText(paramContext);
    this.nameTextView.setTextSize(1, 16.0F);
    this.nameTextView.setHintTextColor(-6842473);
    this.nameTextView.setTextColor(-14606047);
    this.nameTextView.setInputType(655536);
    this.nameTextView.setMinimumHeight(AndroidUtilities.dp(54.0F));
    this.nameTextView.setSingleLine(false);
    this.nameTextView.setLines(2);
    this.nameTextView.setMaxLines(2);
    this.nameTextView.setVerticalScrollBarEnabled(true);
    this.nameTextView.setHorizontalScrollBarEnabled(false);
    this.nameTextView.setPadding(0, 0, 0, 0);
    this.nameTextView.setHint(LocaleController.getString("AddMutual", 2131165300));
    this.nameTextView.setTextIsSelectable(false);
    this.nameTextView.setImeOptions(268435462);
    Object localObject2 = this.nameTextView;
    if (LocaleController.isRTL)
    {
      i = 5;
      label2144:
      ((EditText)localObject2).setGravity(i | 0x10);
      AndroidUtilities.clearCursorDrawable(this.nameTextView);
      ((FrameLayout)localObject1).addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0F, 51, 10.0F, 0.0F, 10.0F, 0.0F));
      this.nameTextView.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramAnonymousEditable)
        {
          int i;
          if (!ChannelCreateActivity.this.ignoreChange)
          {
            int j = 0;
            i = ChannelCreateActivity.this.nameTextView.getSelectionEnd();
            if (paramAnonymousEditable.toString().length() >= ChannelCreateActivity.this.changeString.toString().length()) {
              break label417;
            }
            paramAnonymousEditable = "";
            try
            {
              localObject = ChannelCreateActivity.this.changeString.toString().substring(i, ChannelCreateActivity.this.beforeChangeIndex);
              paramAnonymousEditable = (Editable)localObject;
            }
            catch (Exception localException)
            {
              for (;;)
              {
                Object localObject;
                FileLog.e("tmessages", localException);
              }
              ChannelCreateActivity.this.actionBar.setSubtitle(LocaleController.formatPluralString("Members", ChannelCreateActivity.this.selectedContacts.size()));
              ChannelCreateActivity.this.listView.invalidateViews();
            }
            if (paramAnonymousEditable.length() <= 0) {
              break label412;
            }
            i = j;
            if (ChannelCreateActivity.this.searching)
            {
              i = j;
              if (ChannelCreateActivity.this.searchWas) {
                i = 1;
              }
            }
            paramAnonymousEditable = ChannelCreateActivity.this.nameTextView.getText();
            j = 0;
            while (j < ChannelCreateActivity.this.allSpans.size())
            {
              localObject = (ChipSpan)ChannelCreateActivity.this.allSpans.get(j);
              if (paramAnonymousEditable.getSpanStart(localObject) == -1)
              {
                ChannelCreateActivity.this.allSpans.remove(localObject);
                ChannelCreateActivity.this.selectedContacts.remove(Integer.valueOf(((ChipSpan)localObject).uid));
              }
              j += 1;
            }
          }
          for (;;)
          {
            if (i != 0)
            {
              paramAnonymousEditable = ChannelCreateActivity.this.nameTextView.getText().toString().replace("<", "");
              if (paramAnonymousEditable.length() == 0) {
                break;
              }
              ChannelCreateActivity.access$2502(ChannelCreateActivity.this, true);
              ChannelCreateActivity.access$2602(ChannelCreateActivity.this, true);
              if (ChannelCreateActivity.this.listView != null)
              {
                ChannelCreateActivity.this.listView.setAdapter(ChannelCreateActivity.this.searchListViewAdapter);
                ChannelCreateActivity.this.searchListViewAdapter.notifyDataSetChanged();
                ChannelCreateActivity.this.listView.setFastScrollAlwaysVisible(false);
                ChannelCreateActivity.this.listView.setFastScrollEnabled(false);
                ChannelCreateActivity.this.listView.setVerticalScrollBarEnabled(true);
              }
              if (ChannelCreateActivity.this.emptyTextView != null) {
                ChannelCreateActivity.this.emptyTextView.setText(LocaleController.getString("NoResult", 2131166020));
              }
              ChannelCreateActivity.this.searchListViewAdapter.searchDialogs(paramAnonymousEditable);
            }
            return;
            label412:
            i = 1;
            continue;
            label417:
            i = 1;
          }
          ChannelCreateActivity.this.searchListViewAdapter.searchDialogs(null);
          ChannelCreateActivity.access$2502(ChannelCreateActivity.this, false);
          ChannelCreateActivity.access$2602(ChannelCreateActivity.this, false);
          ChannelCreateActivity.this.listView.setAdapter(ChannelCreateActivity.this.listViewAdapter);
          ChannelCreateActivity.this.listViewAdapter.notifyDataSetChanged();
          ChannelCreateActivity.this.listView.setFastScrollAlwaysVisible(true);
          ChannelCreateActivity.this.listView.setFastScrollEnabled(true);
          ChannelCreateActivity.this.listView.setVerticalScrollBarEnabled(false);
          ChannelCreateActivity.this.emptyTextView.setText(LocaleController.getString("NoContacts", 2131166004));
        }
        
        public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
        {
          if (!ChannelCreateActivity.this.ignoreChange)
          {
            ChannelCreateActivity.access$2302(ChannelCreateActivity.this, ChannelCreateActivity.this.nameTextView.getSelectionStart());
            ChannelCreateActivity.access$2402(ChannelCreateActivity.this, new SpannableString(paramAnonymousCharSequence));
          }
        }
        
        public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      });
      localObject1 = new LinearLayout(paramContext);
      ((LinearLayout)localObject1).setVisibility(4);
      ((LinearLayout)localObject1).setOrientation(1);
      localLinearLayout.addView((View)localObject1, LayoutHelper.createLinear(-1, -1));
      ((LinearLayout)localObject1).setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      this.emptyTextView = new TextView(paramContext);
      this.emptyTextView.setTextColor(-8355712);
      this.emptyTextView.setTextSize(20.0F);
      this.emptyTextView.setGravity(17);
      this.emptyTextView.setText(LocaleController.getString("NoContacts", 2131166004));
      ((LinearLayout)localObject1).addView(this.emptyTextView, LayoutHelper.createLinear(-1, -1, 0.5F));
      ((LinearLayout)localObject1).addView(new FrameLayout(paramContext), LayoutHelper.createLinear(-1, -1, 0.5F));
      this.listView = new LetterSectionsListView(paramContext);
      this.listView.setEmptyView((View)localObject1);
      this.listView.setVerticalScrollBarEnabled(false);
      this.listView.setDivider(null);
      this.listView.setDividerHeight(0);
      this.listView.setFastScrollEnabled(true);
      this.listView.setScrollBarStyle(33554432);
      this.listView.setAdapter(this.listViewAdapter);
      this.listView.setFastScrollAlwaysVisible(true);
      paramContext = this.listView;
      if (!LocaleController.isRTL) {
        break label2499;
      }
    }
    label2499:
    for (int i = 1;; i = 2)
    {
      paramContext.setVerticalScrollbarPosition(i);
      localLinearLayout.addView(this.listView, LayoutHelper.createLinear(-1, -1));
      this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          if ((ChannelCreateActivity.this.searching) && (ChannelCreateActivity.this.searchWas))
          {
            paramAnonymousAdapterView = (TLRPC.User)ChannelCreateActivity.this.searchListViewAdapter.getItem(paramAnonymousInt);
            if (paramAnonymousAdapterView != null) {
              break label94;
            }
          }
          label94:
          boolean bool;
          label573:
          do
          {
            int i;
            do
            {
              return;
              i = ChannelCreateActivity.this.listViewAdapter.getSectionForPosition(paramAnonymousInt);
              paramAnonymousInt = ChannelCreateActivity.this.listViewAdapter.getPositionInSectionForPosition(paramAnonymousInt);
            } while ((paramAnonymousInt < 0) || (i < 0));
            paramAnonymousAdapterView = (TLRPC.User)ChannelCreateActivity.this.listViewAdapter.getItem(i, paramAnonymousInt);
            break;
            bool = true;
            if (ChannelCreateActivity.this.selectedContacts.containsKey(Integer.valueOf(paramAnonymousAdapterView.id))) {
              bool = false;
            }
            Object localObject;
            do
            {
              try
              {
                localObject = (ChipSpan)ChannelCreateActivity.this.selectedContacts.get(Integer.valueOf(paramAnonymousAdapterView.id));
                ChannelCreateActivity.this.selectedContacts.remove(Integer.valueOf(paramAnonymousAdapterView.id));
                paramAnonymousAdapterView = new SpannableStringBuilder(ChannelCreateActivity.this.nameTextView.getText());
                paramAnonymousAdapterView.delete(paramAnonymousAdapterView.getSpanStart(localObject), paramAnonymousAdapterView.getSpanEnd(localObject));
                ChannelCreateActivity.this.allSpans.remove(localObject);
                ChannelCreateActivity.access$2202(ChannelCreateActivity.this, true);
                ChannelCreateActivity.this.nameTextView.setText(paramAnonymousAdapterView);
                ChannelCreateActivity.this.nameTextView.setSelection(paramAnonymousAdapterView.length());
                ChannelCreateActivity.access$2202(ChannelCreateActivity.this, false);
                ChannelCreateActivity.this.actionBar.setSubtitle(LocaleController.formatPluralString("Members", ChannelCreateActivity.this.selectedContacts.size()));
                if ((!ChannelCreateActivity.this.searching) && (!ChannelCreateActivity.this.searchWas)) {
                  break label573;
                }
                ChannelCreateActivity.access$2202(ChannelCreateActivity.this, true);
                paramAnonymousAdapterView = new SpannableStringBuilder("");
                paramAnonymousView = ChannelCreateActivity.this.allSpans.iterator();
                while (paramAnonymousView.hasNext())
                {
                  localObject = (ImageSpan)paramAnonymousView.next();
                  paramAnonymousAdapterView.append("<<");
                  paramAnonymousAdapterView.setSpan(localObject, paramAnonymousAdapterView.length() - 2, paramAnonymousAdapterView.length(), 33);
                }
              }
              catch (Exception paramAnonymousAdapterView)
              {
                for (;;)
                {
                  FileLog.e("tmessages", paramAnonymousAdapterView);
                }
              }
              ChannelCreateActivity.access$2202(ChannelCreateActivity.this, true);
              localObject = ChannelCreateActivity.this.createAndPutChipForUser(paramAnonymousAdapterView);
              if (localObject != null) {
                ((ChipSpan)localObject).uid = paramAnonymousAdapterView.id;
              }
              ChannelCreateActivity.access$2202(ChannelCreateActivity.this, false);
            } while (localObject != null);
            return;
            ChannelCreateActivity.this.nameTextView.setText(paramAnonymousAdapterView);
            ChannelCreateActivity.this.nameTextView.setSelection(paramAnonymousAdapterView.length());
            ChannelCreateActivity.access$2202(ChannelCreateActivity.this, false);
            ChannelCreateActivity.this.searchListViewAdapter.searchDialogs(null);
            ChannelCreateActivity.access$2502(ChannelCreateActivity.this, false);
            ChannelCreateActivity.access$2602(ChannelCreateActivity.this, false);
            ChannelCreateActivity.this.listView.setAdapter(ChannelCreateActivity.this.listViewAdapter);
            ChannelCreateActivity.this.listViewAdapter.notifyDataSetChanged();
            ChannelCreateActivity.this.listView.setFastScrollAlwaysVisible(true);
            ChannelCreateActivity.this.listView.setFastScrollEnabled(true);
            ChannelCreateActivity.this.listView.setVerticalScrollBarEnabled(false);
            ChannelCreateActivity.this.emptyTextView.setText(LocaleController.getString("NoContacts", 2131166004));
            return;
          } while (!(paramAnonymousView instanceof UserCell));
          ((UserCell)paramAnonymousView).setChecked(bool, true);
        }
      });
      this.listView.setOnScrollListener(new AbsListView.OnScrollListener()
      {
        public void onScroll(AbsListView paramAnonymousAbsListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
        {
          if (paramAnonymousAbsListView.isFastScrollEnabled()) {
            AndroidUtilities.clearDrawableAnimation(paramAnonymousAbsListView);
          }
        }
        
        public void onScrollStateChanged(AbsListView paramAnonymousAbsListView, int paramAnonymousInt)
        {
          boolean bool = true;
          if (paramAnonymousInt == 1) {
            AndroidUtilities.hideKeyboard(ChannelCreateActivity.this.nameTextView);
          }
          if (ChannelCreateActivity.this.listViewAdapter != null)
          {
            paramAnonymousAbsListView = ChannelCreateActivity.this.listViewAdapter;
            if (paramAnonymousInt == 0) {
              break label45;
            }
          }
          for (;;)
          {
            paramAnonymousAbsListView.setIsScrolling(bool);
            return;
            label45:
            bool = false;
          }
        }
      });
      break;
      i = 3;
      break label2144;
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.updateInterfaces)
    {
      paramInt = ((Integer)paramVarArgs[0]).intValue();
      if (((paramInt & 0x2) != 0) || ((paramInt & 0x1) != 0) || ((paramInt & 0x4) != 0)) {
        updateVisibleRows(paramInt);
      }
    }
    do
    {
      return;
      if (paramInt == NotificationCenter.chatDidFailCreate)
      {
        if (this.progressDialog != null) {}
        try
        {
          this.progressDialog.dismiss();
          this.donePressed = false;
          return;
        }
        catch (Exception paramVarArgs)
        {
          for (;;)
          {
            FileLog.e("tmessages", paramVarArgs);
          }
        }
      }
      if (paramInt == NotificationCenter.chatDidCreated)
      {
        if (this.progressDialog != null) {}
        try
        {
          this.progressDialog.dismiss();
          paramInt = ((Integer)paramVarArgs[0]).intValue();
          paramVarArgs = new Bundle();
          paramVarArgs.putInt("step", 1);
          paramVarArgs.putInt("chat_id", paramInt);
          paramVarArgs.putBoolean("canCreatePublic", this.canCreatePublic);
          if (this.uploadedAvatar != null) {
            MessagesController.getInstance().changeChatAvatar(paramInt, this.uploadedAvatar);
          }
          presentFragment(new ChannelCreateActivity(paramVarArgs), true);
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e("tmessages", localException);
          }
        }
      }
    } while ((paramInt != NotificationCenter.contactsDidLoaded) || (this.listViewAdapter == null));
    this.listViewAdapter.notifyDataSetChanged();
  }
  
  public void didUploadedPhoto(final TLRPC.InputFile paramInputFile, final TLRPC.PhotoSize paramPhotoSize1, TLRPC.PhotoSize paramPhotoSize2)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        ChannelCreateActivity.access$1502(ChannelCreateActivity.this, paramInputFile);
        ChannelCreateActivity.access$1402(ChannelCreateActivity.this, paramPhotoSize1.location);
        ChannelCreateActivity.this.avatarImage.setImage(ChannelCreateActivity.this.avatar, "50_50", ChannelCreateActivity.this.avatarDrawable);
        if (ChannelCreateActivity.this.createAfterUpload) {}
        try
        {
          if ((ChannelCreateActivity.this.progressDialog != null) && (ChannelCreateActivity.this.progressDialog.isShowing()))
          {
            ChannelCreateActivity.this.progressDialog.dismiss();
            ChannelCreateActivity.access$602(ChannelCreateActivity.this, null);
          }
          ChannelCreateActivity.this.doneButton.performClick();
          return;
        }
        catch (Exception localException)
        {
          for (;;)
          {
            FileLog.e("tmessages", localException);
          }
        }
      }
    });
  }
  
  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.avatarUpdater.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  public boolean onFragmentCreate()
  {
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidCreated);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidFailCreate);
    if (this.currentStep == 2) {
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
    }
    for (;;)
    {
      if (this.avatarUpdater != null)
      {
        this.avatarUpdater.parentFragment = this;
        this.avatarUpdater.delegate = this;
      }
      return super.onFragmentCreate();
      if (this.currentStep == 1) {
        generateLink();
      }
    }
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidCreated);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidFailCreate);
    if (this.currentStep == 2) {
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
    }
    if (this.avatarUpdater != null) {
      this.avatarUpdater.clear();
    }
    AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
  }
  
  public void onResume()
  {
    super.onResume();
    AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
  }
  
  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (this.currentStep != 1))
    {
      this.nameTextView.requestFocus();
      AndroidUtilities.showKeyboard(this.nameTextView);
    }
  }
  
  public void restoreSelfArgs(Bundle paramBundle)
  {
    if (this.currentStep == 0)
    {
      if (this.avatarUpdater != null) {
        this.avatarUpdater.currentPicturePath = paramBundle.getString("path");
      }
      paramBundle = paramBundle.getString("nameTextView");
      if (paramBundle != null)
      {
        if (this.nameTextView == null) {
          break label56;
        }
        this.nameTextView.setText(paramBundle);
      }
    }
    return;
    label56:
    this.nameToSet = paramBundle;
  }
  
  public void saveSelfArgs(Bundle paramBundle)
  {
    if (this.currentStep == 0)
    {
      if ((this.avatarUpdater != null) && (this.avatarUpdater.currentPicturePath != null)) {
        paramBundle.putString("path", this.avatarUpdater.currentPicturePath);
      }
      if (this.nameTextView != null)
      {
        String str = this.nameTextView.getText().toString();
        if ((str != null) && (str.length() != 0)) {
          paramBundle.putString("nameTextView", str);
        }
      }
    }
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\ChannelCreateActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */