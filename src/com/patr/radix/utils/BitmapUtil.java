package com.patr.radix.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

/**
 * 
 * <图片公共类> <功能详细描述>
 * 
 * @author cyf
 * @version [版本号, 2013-9-17]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class BitmapUtil {

    /***
     * 根据资源文件获取Bitmap
     * 
     * @param context
     * @param drawableId
     * @return
     */
    public static Bitmap ReadBitmapById(Context context, int drawableId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Config.ARGB_8888;
        options.inInputShareable = true;
        options.inPurgeable = true;
        InputStream stream = context.getResources().openRawResource(drawableId);
        Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
        return bitmap;
    }

    /**
     * Bitmap 等比例压缩
     */
    public static Bitmap getBitmapScale(Bitmap bitmap, int screenWidth,
            int screenHight) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scale = (float) screenWidth / w;
        float scale2 = (float) screenHight / h;

        // scale = scale < scale2 ? scale : scale2;

        // 保证图片不变形.
        matrix.postScale(scale, scale);
        // w,h是原图的属性.
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * <图片按比例大小压缩方法（根据路径获取图片并压缩）>
     * 
     * @return 压缩后图片路径
     */
    public static String getImageScaleByPath(
            PicturePropertiesBean propertiesBean, Context context) {
        Bitmap bitmap = null;
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(propertiesBean.getSrcPath(), newOpts);
        int width = newOpts.outWidth;
        int height = newOpts.outHeight;
        // float minHeight = 800f;//设置为主流手机分辨率800*480

        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (height > width && width > propertiesBean.getWidth()) {
            // 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / propertiesBean.getWidth());
        } else if (width > height && height > propertiesBean.getHeight()) {
            // 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / propertiesBean.getHeight());
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        newOpts.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(propertiesBean.getSrcPath(), newOpts);
        return compressImage(bitmap, context, propertiesBean);// 压缩好比例大小后再进行质量压缩
    }

    /**
     * <质量压缩方法>
     * 
     * @return 压缩后图片路径
     */
    private static String compressImage(Bitmap image, Context context,
            PicturePropertiesBean propertiesBean) {
        File file = null;
        if (image != null) {
            try {
                int degree = getExifOrientation(propertiesBean.getSrcPath());
                if (degree > 0) {
                    Matrix matrix = new Matrix();
                    matrix.setRotate(degree);
                    Bitmap rotateBitmap = Bitmap.createBitmap(image, 0, 0,
                            image.getWidth(), image.getHeight(), matrix, true);
                    if (rotateBitmap != null) {
                        image.recycle();
                        image = rotateBitmap;
                    }
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                // 图片大于最大值,则压缩,否则不做任何操作
                if (baos.toByteArray().length > propertiesBean.getMaxSize()) {
                    baos.reset();
                    // 质量压缩方法，首先压缩options的压缩率
                    image.compress(Bitmap.CompressFormat.JPEG,
                            propertiesBean.getDefaultOption(), baos);
                    // 循环判断如果压缩后图片是否大于200kb,大于继续压缩
                    while (baos.toByteArray().length > propertiesBean
                            .getMaxSize()) {
                        baos.reset();// 重置baos即清空baos
                        // 这里压缩defaultOptions%，把压缩后的数据存放到baos中
                        propertiesBean.setDefaultOption(propertiesBean
                                .getDefaultOption()
                                - propertiesBean.getOptions());// 每次都减少option
                        image.compress(Bitmap.CompressFormat.JPEG,
                                propertiesBean.getDefaultOption(), baos);
                    }
                    while (baos.toByteArray().length < propertiesBean
                            .getMinSize()) {
                        baos.reset();// 重置baos即清空baos
                        // 这里压缩options%，把压缩后的数据存放到baos中
                        propertiesBean.setDefaultOption(propertiesBean
                                .getDefaultOption()
                                + propertiesBean.getOptions());// 每次都增加option
                        image.compress(Bitmap.CompressFormat.JPEG,
                                propertiesBean.getDefaultOption(), baos);
                    }
                }
                file = new File(propertiesBean.getDestPath());
                FileOutputStream stream = new FileOutputStream(file);
                if (baos != null) {
                    stream.write(baos.toByteArray());
                    stream.flush();
                }
                stream.close();
                baos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (image != null)
                    image.recycle();
            }
            return file.getPath();
        } else {
            return "";
        }
    }

    /**
     * 
     * <得到 图片旋转 的角度> <功能详细描述>
     * 
     * @param filepath
     * @return
     * @see [类、类#方法、类#成员]
     */
    private static int getExifOrientation(String filePath) {
        int degree = 0;
        try {
            ExifInterface exif = new ExifInterface(filePath);
            int result = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            switch (result) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;

            default:
                break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 根据图片的url路径获得Bitmap对象
     * @param url
     * @return
     */
    public static Bitmap returnBitmap(String url) {
        URL fileUrl = null;
        Bitmap bitmap = null;

        try {
            fileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) fileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;

    }
}