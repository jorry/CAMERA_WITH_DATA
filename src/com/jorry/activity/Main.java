package com.jorry.activity;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class Main extends Activity {

  ArrayList<ContactsBean> mAllContacts = new ArrayList<ContactsBean>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.camera).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        SelectPhotMode.getPicFromCapture(Main.this);
      }
    });

    findViewById(R.id.photo).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        SelectPhotMode.getPicFromContent(Main.this);
      }
    });
    findViewById(R.id.contact).setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        ContentResolver contentResolver = Main.this.getContentResolver();
        String email = "";
        Cursor c =
            getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        c.moveToPosition(-1);
        while (c.moveToNext()) {
          int tempId =
              c.getInt(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
          String tempName =
              c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
          String tempNumber =
              c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
          float tempMatcher = 0;
          ContactsBean contactsBean = new ContactsBean(tempId, tempName, tempNumber, tempMatcher);

          // Fetch email
          Cursor emailCursor =
              contentResolver.query(
                  android.provider.ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                  android.provider.ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + tempId,
                  null, null);
          while (emailCursor.moveToNext()) {
            email =
                emailCursor.getString(emailCursor
                    .getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Email.DATA));
            contactsBean.setEmai(email);
            Log.i("vi", contactsBean.getName()+"联系人  邮件= " + email);
          }

          mAllContacts.add(contactsBean);
        }

        for (int i = 0; i < mAllContacts.size(); i++) {
          Log.i("vi", "联系人 = " + mAllContacts.get(i).getName() + " "
              + mAllContacts.get(i).getNumber()+" email"+mAllContacts.get(i).getEmai());
        }

      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case SelectPhotMode.PHOTO_PICKED_WITH_DATA: // 图片库
        if (data == null || data.getData() == null) {
          return;
        }
        // 获取图片。
        try {
          String fileUri = SelectPhotMode.getCapUri(Main.this, data);
          setBitmap(fileUri);
        } catch (Exception e) {
          System.gc();
          System.out.println("手机获取图库异常,改为默认图片。");
        }
        break;
      case SelectPhotMode.CAMERA_WITH_DATA:// 相机
        String fileUri = SelectPhotMode.mCurrentPhotoFile.getAbsolutePath();
        setBitmap(fileUri);
        break;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void setBitmap(String fileUri) {
    if (TextUtils.isEmpty(fileUri)) {
      return;
    }
    BitmapTools.getThumbUploadPath(fileUri, 1080, 500, new PicInterface() {

      @Override
      public void onSusse(Bitmap bit, String picPath) {
        Log.i("v", "成功" + picPath);
      }

      @Override
      public void onError(int code) {
        Log.i("v", "失败");
      }
    });

  }
}
