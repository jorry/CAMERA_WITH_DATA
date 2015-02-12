package com.jorry.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

public class BitmapTools {

  /**
   * 宽高等比缩放：
   * 
   * @param imagePath 图片路径
   * @param width 缩放的大小 比如： w = 1280
   * @param maxSize 最大内存数
   * @return
   */
  public static void getThumbUploadPath(String imagePath, int toWidth, long maxSizeData,
      PicInterface picInterfaace) {
    try {
      BitmapFactory.Options newOpts = new BitmapFactory.Options();
      newOpts.inJustDecodeBounds = true;

      Bitmap bitmap = BitmapFactory.decodeFile(imagePath, newOpts);// 此时返回bm为空

      newOpts.inSampleSize = getSimpleSize(newOpts, toWidth, 0);// 设置缩放比例
      newOpts.inJustDecodeBounds = false;
      // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
      bitmap = BitmapFactory.decodeFile(imagePath, newOpts);

      Bitmap compressBit = compressImage(bitmap, maxSizeData); // options 后

      saveBitmap(compressBit, SelectPhotMode.PHOTO_DIR.getAbsolutePath(),
          new File(imagePath).getName(), toWidth, 0, picInterfaace);// 压缩好比例大小后再进行质量压缩
    } catch (Exception e) {
      e.printStackTrace();
      picInterfaace.onError(-1);
    }
  }



  /**
   * 制定 宽高缩放
   * 
   * @param imagePath 图片路径
   * @param toWidth 缩放的大小
   * @param toHeight 缩放的大小
   * @param maxSize 最大内存数
   * @return
   */
  public static void getThumbUploadPath(String imagePath, int toWidth, int toHeight,
      long maxSizeData, PicInterface picInterfaace) {
    try {
      BitmapFactory.Options newOpts = new BitmapFactory.Options();
      newOpts.inJustDecodeBounds = true;

      Bitmap bitmap = BitmapFactory.decodeFile(imagePath, newOpts);// 此时返回bm为空

      newOpts.inSampleSize = getSimpleSize(newOpts, toWidth, toHeight);// 设置缩放比例
      newOpts.inJustDecodeBounds = false;
      // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
      bitmap = BitmapFactory.decodeFile(imagePath, newOpts);

      Bitmap compressBit = compressImage(bitmap, maxSizeData); // options 后

      saveBitmap(compressBit, SelectPhotMode.PHOTO_DIR.getAbsolutePath(),
          new File(imagePath).getName(), toWidth, toHeight, picInterfaace);// 压缩好比例大小后再进行质量压缩
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 
   * @param options
   * 
   * @param viewWidth 要求缩放的图片
   * @return
   */
  private static int getSimpleSize(BitmapFactory.Options options, int toWidth, int toHeight) {
    int simpleSize = 1;
    int imgHeight = options.outHeight;
    int imgWidth = options.outWidth;
    if (imgWidth <= toWidth) {
      return simpleSize;
    }
    int widthRatio = (int) Math.ceil(imgWidth / toWidth);
    int heightRatio = (int) Math.ceil(imgHeight / (toHeight == 0 ? toWidth : toHeight));
    if (widthRatio > 1) {
      if (widthRatio > heightRatio) {
        simpleSize = widthRatio;
      } else {
        simpleSize = heightRatio;
      }
    }
    return simpleSize;
  }


  private static void saveBitmap(Bitmap bm, String savePath, String saveFileName, int toW, int toH,
      PicInterface picInterfaace) {
    String imageP = "";
    System.out.println(bm.getWidth() + "=====saveBitmap====  " + bm.getHeight());
    try {
      File oF = new File(savePath);//
      if (!oF.exists()) {
        oF.mkdirs();
      }
      File f = new File(savePath, saveFileName);
      imageP = f.getAbsolutePath();
      if (f.exists()) {
        f.delete();
      }
      FileOutputStream out = new FileOutputStream(f);

      int bmpWidth = bm.getWidth();

      int bmpHeight = bm.getHeight();

      Bitmap resizeBitmap = null;
      if (bmpWidth > toW) {
        // 缩放图片的尺寸
        float scaleWidth = (float) toW / bmpWidth; // 按固定大小缩放 sWidth 写多大就多大

        float scaleHeight = (float) toH / bmpHeight; //

        Matrix matrix = new Matrix();
        if (scaleHeight <= 0) {
          matrix.postScale(scaleWidth, scaleWidth);// 产生缩放后的Bitmap对象
        } else {
          matrix.postScale(scaleWidth, scaleHeight);// 产生缩放后的Bitmap对象
        }
        resizeBitmap = Bitmap.createBitmap(bm, 0, 0, bmpWidth, bmpHeight, matrix, false);
        bm.recycle();

        Log.i("UI", "bmpWidth---->" + bmpWidth + "bmpHeight = " + bmpHeight + "------>"
            + resizeBitmap.getWidth() + " Matrix 后  " + resizeBitmap.getHeight());

        resizeBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);

      } else {
        bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
        Log.i("UI",
            "bmpWidth---->" + bmpWidth + "bmpHeight = " + bmpHeight + "------>" + bm.getWidth()
                + " Matrix 后  " + bm.getHeight());
      }

      out.flush();
      out.close();
      picInterfaace.onSusse(resizeBitmap != null ? resizeBitmap : bm, f.getAbsolutePath());
    } catch (OutOfMemoryError e) {
      imageP = savePath;
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      imageP = savePath;
      e.printStackTrace();
    } catch (Exception e) {
      imageP = savePath;
      e.printStackTrace();
    }
  }

  public static Bitmap compressImage(Bitmap image, long maxSize) throws Exception {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
    int options = 100;
    while (baos.toByteArray().length / 1024 > maxSize) { // 循环判断如果压缩后图片是否大于300kb,大于继续压缩
      options -= 5;// 每次都减少10
      baos.reset();// 重置baos即清空baos
      image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
    }
    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
    Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
    return bitmap;
  }

}
