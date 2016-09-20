package zj.chat.com.imchat.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.text.TextUtils;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.DisplayMetrics;

public class ImgBitmatUtils {
    /**
     * 创建需要水印的的图片
     *
     * @return
     */
    public static Bitmap createBitmap(Bitmap photo, List<String> lists) {

        int width = photo.getWidth(), hight = photo.getHeight();
        Bitmap icon = Bitmap.createBitmap(width, hight, Config.ARGB_8888); // 建立一个空的BItMap
        Canvas canvas = new Canvas(icon);// 初始化画布绘制的图像到icon上

        Paint photoPaint = new Paint(); // 建立画笔
        photoPaint.setDither(true); // 获取跟清晰的图像采样
        photoPaint.setFilterBitmap(true);// 过滤一些

        Rect src = new Rect(0, 0, photo.getWidth(), photo.getHeight());// 创建一个指定的新矩形的坐标
        Rect dst = new Rect(0, 0, width, hight);// 创建一个指定的新矩形的坐标
        canvas.drawBitmap(photo, src, dst, photoPaint);// 将photo 缩放或则扩大到
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
        textPaint.setTextSize(12.0f);// 字体大小
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);// 采用默认的宽度
        textPaint.setColor(Color.WHITE);// 采用的颜色 // dst使用的填充区photoPaint
        /// int bitmapWidth = photo.getWidth();
        int bitmapHeight = photo.getHeight();
        int chHeight = bitmapHeight - 10;
        // int chWidht = bitmapWidth - 150;
        // Paint paint = new Paint();
        // float size = paint.measureText(character.trim());
        for (String character : lists) {// 循环把字话上去
            if (character == null) {
                character = "字符串为空";
            }
            canvas.drawText(character, 5, chHeight, textPaint);// 绘制上去字，开始未知x,y采用那只笔绘制
            chHeight = chHeight - 15;
        }
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();

        return icon;
    }

    public static Bitmap getPic(Context context, String path) {
        byte[] bitmapArr = decodeBitmap(path);
        if (bitmapArr == null)
            return null;
        Bitmap bitmapOrg = BitmapFactory.decodeByteArray(bitmapArr, 0, bitmapArr.length);
        Bitmap resizedBitmap = scaleBitmap(bitmapOrg);
        return resizedBitmap;
    }

    public static byte[] decodeBitmap(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;// 设置成了true,不占用内存，只获取bitmap宽高
        BitmapFactory.decodeFile(path, opts);
        opts.inSampleSize = computeSampleSize(opts, -1, 1024 * 800);
        opts.inJustDecodeBounds = false;// 这里一定要将其设置回false，因为之前我们将其设置成了true
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inDither = false;
        opts.inPurgeable = true;
        opts.inTempStorage = new byte[16 * 1024];
        FileInputStream is = null;
        Bitmap bmp = null;
        ByteArrayOutputStream baos = null;
        try {
            is = new FileInputStream(path);
            bmp = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
            double scale = getScaling(opts.outWidth * opts.outHeight, 1024 * 600);
            Bitmap bmp2 = Bitmap.createScaledBitmap(bmp, (int) (opts.outWidth * scale), (int) (opts.outHeight * scale),
                    true);
            baos = new ByteArrayOutputStream();
            bmp2.compress(CompressFormat.JPEG, 100, baos);
            bmp2.recycle();
            bmp.recycle();
            return baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.gc();
        }
        if (baos == null)
            return null;
        return baos.toByteArray();
    }

    private static double getScaling(int src, int des) {
        /**
         * 48 目标尺寸÷原尺寸 sqrt开方，得出宽高百分比 49
         */
        double scale = Math.sqrt((double) des / (double) src);
        return scale;
    }

    public static Bitmap scaleBitmap(Bitmap bitmapOrg) {
        if (null == bitmapOrg)
            return null;
        // 获取这个图片的宽和高
        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();

        // 定义预转换成的图片的宽度和高度
        int newWidth = 0;
        int newHeight = 0;
        if (width < height) {
            int[] w_h = calWH(width, height);
            newWidth = w_h[0];
            newHeight = w_h[1];
        } else {
            int[] w_h = calWH(height, width);
            newWidth = w_h[1];
            newHeight = w_h[0];
        }

        // 计算缩放率，新尺寸除原始尺寸
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();

        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);

        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);
        return resizedBitmap;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128
                : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    private static int[] calWH(int ow, int oh) {
        int[] w_h = new int[2];
        int scaled = ow > oh ? oh / 220 : ow / 220;
        w_h[0] = ow / scaled;
        w_h[1] = oh / scaled;
        return w_h;
    }

    /**
     * 图片压缩
     *
     * @param bitmap
     * @param url
     * @param ration
     * @return
     */
    public static String compressImg(Bitmap bitmap, String url, int ration) {
        if (bitmap == null) {
            return url;
        }
        File file = new File(url);
        try {
            FileOutputStream os = new FileOutputStream(file);
            bitmap.compress(CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return url;
    }

    /*
     *
     * 释放bitmap
     */
    public static void releaseBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }

    }

    /**
     * 把图片切割成正方形
     *
     * @param bmp
     * @return
     */
    public static Bitmap cutBmp(Bitmap bmp) {
        Bitmap result;
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int nw;
        if (w > h) {

            nw = h;
            result = Bitmap.createBitmap(bmp, (w - nw) / 2, 0, nw, nw);
        } else {

            nw = w;
            result = Bitmap.createBitmap(bmp, 0, (h - nw) / 2, nw, nw);
        }
        return result;
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap 传入Bitmap对象
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            left = 0;
            top = 0;
            right = width;
            bottom = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);// 设置画笔无锯齿
        canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
        paint.setColor(color);

        // 以下有两种方法画圆,drawRounRect和drawCircle
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);//
        // 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
        canvas.drawCircle(roundPx, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
        canvas.drawBitmap(bitmap, src, dst, paint); // 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
        return output;
    }

    public static String compressImgSize(String srcPath, Activity mContext) {
        DisplayMetrics dm = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
        float hh = dm.heightPixels;
        float ww = dm.widthPixels;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, opts);
        opts.inJustDecodeBounds = false;
        int w = opts.outWidth;
        int h = opts.outHeight;
        int size = 0;
        if (w <= ww && h <= hh) {
            size = 1;
        } else {
            double scale = w >= h ? w / ww : h / hh;
            double log = Math.log(scale) / Math.log(2);
            double logCeil = Math.ceil(log);
            size = (int) Math.pow(2, logCeil);
        }
        opts.inSampleSize = size;
        bitmap = BitmapFactory.decodeFile(srcPath, opts);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(CompressFormat.JPEG, quality, baos);
        while (baos.toByteArray().length > 100 * 1024) {
            baos.reset();
            bitmap.compress(CompressFormat.JPEG, quality, baos);
            quality -= 5;
        }
        try {
            baos.writeTo(new FileOutputStream(srcPath));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return srcPath;
    }

    /**
     * 压缩图片
     *
     * @param srcPath
     */
    public static void compressImg(String srcPath) {
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(CompressFormat.JPEG, quality, baos);
        while (baos.toByteArray().length > 10 * 1024) {
            baos.reset();
            bitmap.compress(CompressFormat.JPEG, quality, baos);
            quality -= 5;
        }
        try {
            baos.writeTo(new FileOutputStream(srcPath));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 描述：将byte[]转换为Bitmap.
     *
     * @param b 图片格式的byte[]数组
     * @return bitmap 得到的Bitmap
     */
    public static Bitmap bytes2Bimap(byte[] b) {
        Bitmap bitmap = null;
        try {
            if (b.length != 0) {
                bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 镜像水平翻转
     *
     * @param //path    : 图片路径名称
     * @param //bitmap  : 位图
     * @param //format  : 图片格式
     * @param //quality : 图片质量
     */
    public static Bitmap convertBmp(Bitmap bitmap) {
        if (bitmap == null)
            return null;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1); // 镜像水平翻转
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * 保存位图到文件
     *
     * @param path    : 图片路径名称
     * @param bitmap  : 位图
     * @param format  : 图片格式
     * @param quality : 图片质量
     */
    public static boolean saveBitmapToFile(String path, Bitmap bitmap, CompressFormat format, int quality) {
        if (TextUtils.isEmpty(path) || bitmap == null || format == null) {
            return false;
        }

        // 如果质量小于0或大于100时，使用80值
        if (quality < 0 || quality > 100) {
            quality = 80;
        }

        BufferedOutputStream bos = null;
        try {
            File file = new File(path);
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(format, quality, bos);
            bos.flush();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                bos = null;
            }
        }
        return false;
    }

    /**
     * 描述：旋转Bitmap为一定的角度.
     *
     * @param bitmap  the bitmap
     * @param degrees the degrees
     * @return the bitmap
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
        Bitmap mBitmap = null;
        try {
            Matrix m = new Matrix();
            m.setRotate(degrees % 360);
            mBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mBitmap;
    }
}
