package com.jorry.activity;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

/**
 * 选择图片或相机
 * 
 */
public class SelectPhotMode {

  public static final int CAMERA_WITH_DATA = 0; // 拍照
  public static final int PHOTO_PICKED_WITH_DATA = CAMERA_WITH_DATA + 1; // 图库
  public static final int GALLERY_RESULT = PHOTO_PICKED_WITH_DATA + 1; // 照相结果
  public static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory()
      + "/tianyiCamera");// 图片的存储目录
  public static File mCurrentPhotoFile;

  // 拍照
  public static void getPicFromCapture(Activity context) {
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
      PHOTO_DIR.mkdir();
      mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName()); // 用当前时间给取得的图片命名
      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
      context.startActivityForResult(intent, CAMERA_WITH_DATA);
    } else {
      Toast.makeText(context, "没有sd卡", Toast.LENGTH_LONG).show();
    }
  }

  // 相册
  public static void getPicFromContent(Activity context) {
    try {
      Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
      context.startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
    } catch (Exception e) {
      Toast.makeText(context, "错误", Toast.LENGTH_LONG).show();
    }
  }

  // 用当前时间给取得的图片命名
  private static String getPhotoFileName() {
    Date date = new Date(System.currentTimeMillis());
    SimpleDateFormat dateFormat = new SimpleDateFormat("'edog'_yyyyMMdd_HHmmss");
    return dateFormat.format(date) + ".jpg";
  }

  public static String getCapUri(Context con,Intent data) {
    String photoPathString = "";
    Uri uri = data.getData();
    Cursor cursor = con.getContentResolver().query(uri, null, null, null, null);
    if (cursor == null) {
      String tempImageUrl = uri.toString();

      photoPathString = tempImageUrl.substring(7, tempImageUrl.length());
    } else {
      cursor.moveToFirst();
      photoPathString = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
      cursor.close();
    }
    return photoPathString;
  }


}
