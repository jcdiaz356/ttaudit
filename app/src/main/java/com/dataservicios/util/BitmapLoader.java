package com.dataservicios.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.dataservicios.systemauditor.AlbumStorageDirFactory;
import com.dataservicios.systemauditor.BaseAlbumDirFactory;
import com.dataservicios.systemauditor.FroyoAlbumDirFactory;
import com.dataservicios.systemauditor.R;

import java.io.File;




/**
 * Created by Jaime on 21/09/2016.
 */
public class BitmapLoader {
    private static int getScale(int originalWidth, int originalHeight, final int requiredWidth, final int requiredHeight) {
        //a scale of 1 means the original dimensions
        //of the image are maintained
        int scale = 1;

        //calculate scale only if the height or width of
        //the image exceeds the required value.
        if ((originalWidth > requiredWidth) || (originalHeight > requiredHeight)) {
            //calculate scale with respect to
            //the smaller dimension
            if (originalWidth < originalHeight)
                scale = Math.round((float) originalWidth / requiredWidth);
            else
                scale = Math.round((float) originalHeight / requiredHeight);

        }

        return scale;
    }

    public static BitmapFactory.Options getOptions(String filePath, int requiredWidth, int requiredHeight) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        //setting inJustDecodeBounds to true
        //ensures that we are able to measure
        //the dimensions of the image,without
        //actually allocating it memory
        options.inJustDecodeBounds = true;

        //decode the file for measurement
        BitmapFactory.decodeFile(filePath, options);

        //obtain the inSampleSize for loading a
        //scaled down version of the image.
        //options.outWidth and options.outHeight
        //are the measured dimensions of the
        //original image
        options.inSampleSize = getScale(options.outWidth,
                options.outHeight, requiredWidth, requiredHeight);

        //set inJustDecodeBounds to false again
        //so that we can now actually allocate the
        //bitmap some memory
        options.inJustDecodeBounds = false;

        return options;

    }


    public static Bitmap loadBitmap(String filePath, int requiredWidth, int requiredHeight) {


        BitmapFactory.Options options = getOptions(filePath, requiredWidth, requiredHeight);

        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * Scale imagen
     * @param realImage
     * @param maxImageSize
     * @param filter
     * @return
     */
    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    /**
     * Rotate imagen
     * @param img
     * @param degree
     * @return
     */
    public static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }


    private static String getAlbunNameTemp(Context context){
        return  context.getString(R.string.album_name_temp);
    }

    public static File getAlbumDirTemp(Context context) {

        AlbumStorageDirFactory mAlbumStorageDirFactory = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }


        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbunNameTemp(context));

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d(getAlbunNameTemp(context), "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(String.valueOf(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }
}