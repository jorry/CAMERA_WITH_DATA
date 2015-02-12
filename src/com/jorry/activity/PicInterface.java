package com.jorry.activity;

import android.graphics.Bitmap;

public interface PicInterface {

  public void onSusse(Bitmap bit,String picPath);
  
  public void onError(int code);
  
}
