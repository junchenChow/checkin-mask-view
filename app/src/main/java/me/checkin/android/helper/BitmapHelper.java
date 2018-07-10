package me.checkin.android.helper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhoujunchen
 * on 17/9/1.
 */

public class BitmapHelper {
    private static final String TAG = "BitmapUtil";

    /**
     * 通过资源id转化成Bitmap
     */
    public static Bitmap decodeResource(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_4444;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 缩放Bitmap满屏
     */
    public static Bitmap getBitmap(Bitmap bitmap, int screenWidth,
                                   int screenHight) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scale = (float) screenWidth / w;
        float scale2 = (float) screenHight / h;
        scale = scale < scale2 ? scale : scale2;
        matrix.postScale(scale, scale);
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        if (!bitmap.equals(bmp) && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return bmp;// Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * 按最大边按一定大小缩放图片
     */
    public static Bitmap scaleImage(byte[] buffer, float size) {
        // 获取原图宽度
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        options.inInputShareable = true;
        Bitmap bm;
        // 计算缩放比例
        float reSize = options.outWidth / size;
        if (options.outWidth < options.outHeight) {
            reSize = options.outHeight / size;
        }
        // 如果是小图则放大
        if (reSize <= 1) {
            int newWidth = 0;
            int newHeight = 0;
            if (options.outWidth > options.outHeight) {
                newWidth = (int) size;
                newHeight = options.outHeight * (int) size / options.outWidth;
            } else {
                newHeight = (int) size;
                newWidth = options.outWidth * (int) size / options.outHeight;
            }
            bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
            bm = scaleImage(bm, newWidth, newHeight);
            if (bm == null) {
                Log.e(TAG, "convertToThumb, decode fail:" + null);
                return null;
            }
            return bm;
        }
        // 缩放
        options.inJustDecodeBounds = false;
        options.inSampleSize = (int) reSize;
        bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
        if (bm == null) {
            Log.e(TAG, "convertToThumb, decode fail:" + null);
            return null;
        }
        return bm;
    }

    /**
     * 检查图片是否超过一定值，是则缩小
     */
    public static Bitmap convertToThumb(byte[] buffer, float size) {
        // 获取原图宽度
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inPurgeable = true;
        options.inInputShareable = true;
        Bitmap bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length,
                options);
        // 计算缩放比例
        float reSize = options.outWidth / size;
        if (options.outWidth > options.outHeight) {
            reSize = options.outHeight / size;
        }
        if (reSize <= 0) {
            reSize = 1;
        }
        Log.d(TAG, "convertToThumb, reSize:" + reSize);
        // 缩放
        options.inJustDecodeBounds = false;
        options.inSampleSize = (int) reSize;
        if (bm != null && !bm.isRecycled()) {
            bm.recycle();
            bm = null;
            Log.e(TAG, "convertToThumb, recyle");
        }
        bm = BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
        if (bm == null) {
            Log.e(TAG, "convertToThumb, decode fail:" + null);
            return null;
        }
        return bm;
    }

    /**
     * Bitmap --> byte[]
     */
    private static byte[] readBitmap(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, baos);
        try {
            baos.flush();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    /**
     * Bitmap --> byte[]
     */
    public static byte[] readBitmapFromBuffer(byte[] buffer, float size) {
        return readBitmap(convertToThumb(buffer, size));
    }

    /**
     * 以屏幕宽度为基准，显示图片
     */
    public static Bitmap decodeStream(Context context, Intent data, float size) {
        Bitmap image = null;
        try {
            Uri dataUri = data.getData();
            // 获取原图宽度
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPurgeable = true;
            options.inInputShareable = true;
            BitmapFactory.decodeStream(context.getContentResolver()
                    .openInputStream(dataUri), null, options);
            // 计算缩放比例
            float reSize = (int) (options.outWidth / size);
            if (reSize <= 0) {
                reSize = 1;
            }
            Log.d(TAG, "old-w:" + options.outWidth + ", llyt-w:" + size
                    + ", resize:" + reSize);
            // 缩放
            options.inJustDecodeBounds = false;
            options.inSampleSize = (int) reSize;
            image = BitmapFactory.decodeStream(context.getContentResolver()
                    .openInputStream(dataUri), null, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 按新的宽高缩放图片
     */
    public static Bitmap scaleImage(Bitmap bm, int newWidth, int newHeight) {
        if (bm == null || bm.getWidth() == 0 || bm.getHeight() == 0) {
            return null;
        }
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap finalBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        if (!bm.isRecycled()) {
            bm.recycle();
        }
        return finalBitmap;
    }


    /**
     * 根据指定的宽度平铺图像
     */
    public static Bitmap createRepeater(int width, Bitmap src) {
        int count = (width + src.getWidth() - 1) / src.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(width, src.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        for (int idx = 0; idx < count; ++idx) {
            canvas.drawBitmap(src, idx * src.getWidth(), 0, null);
        }
        return bitmap;
    }

    /**
     * 图片的质量压缩方法
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        if (baos != null) {
            try {
                baos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (isBm != null) {
            try {
                isBm.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (image != null && !image.isRecycled()) {
            image.recycle();
            image = null;
        }
        return bitmap;
    }

    /**
     * 图片按比例大小压缩方法(根据Bitmap图片压缩)
     */
    public static Bitmap getImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        if (isBm != null) {
            try {
                isBm.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (image != null && !image.isRecycled()) {
            image.recycle();
            image = null;
        }
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    /**
     * 通过资源id转化成Bitmap 全屏显示
     */
    public static Bitmap decodeResource(Context context, int drawableId,
                                        int screenWidth, int screenHight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inInputShareable = true;
        options.inPurgeable = true;
        InputStream stream = context.getResources().openRawResource(drawableId);
        Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
        return getBitmap(bitmap, screenWidth, screenHight);
    }

    /**
     * 设置新宽度 高度自适应缩放
     */
    public static Bitmap fitBitmap(Bitmap target, int newWidth) {
        int width = target.getWidth();
        int height = target.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        matrix.postScale(scaleWidth, scaleWidth);
        Bitmap bmp = Bitmap.createBitmap(target, 0, 0, width, height, matrix, true);
        if (!target.equals(bmp) && !target.isRecycled()) {
            target.recycle();
        }
        return bmp;
    }

    /**
     * 画水印图片
     */
    public static Bitmap addWatermarks(Bitmap src, Bitmap... watermark) {
        if (src == null || watermark == null) {
            Log.d(TAG, "src is null");
            return null;
        }
        int sWid = src.getWidth();
        int sHei = src.getHeight();
        if (sWid == 0 || sHei == 0) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(sWid, sHei, Bitmap.Config.ARGB_4444);
        try {
            Canvas cv = new Canvas(bitmap);
            cv.drawBitmap(src, 0, 0, null);
            for (Bitmap aWatermark : watermark) {
                cv.drawBitmap(aWatermark, 0, 0, null);
            }
            cv.save(Canvas.ALL_SAVE_FLAG);
            cv.restore();
        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }

    public static Bitmap getScaleBitmapByByte(Context context, int resId, int width, int height) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inMutable = true;
        InputStream is = context.getResources().openRawResource(resId);
        try {
            byte[] bytes = streamToByteArray(is);
            return scaleImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opt), width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] streamToByteArray(InputStream is) throws IOException {
        final byte[] buff = new byte[1024];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i;
        while ((i = is.read(buff, 0, buff.length)) > 0) {
            byteArrayOutputStream.write(buff, 0, i);
        }
        byte[] bytes = byteArrayOutputStream.toByteArray();
        is.close();
        return bytes;
    }
}
