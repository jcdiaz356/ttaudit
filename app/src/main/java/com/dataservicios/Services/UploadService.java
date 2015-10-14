package com.dataservicios.Services;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;
import com.dataservicios.librerias.AndroidMultiPartEntity;
import com.dataservicios.librerias.GlobalConstant;
import com.dataservicios.systemauditor.AlbumStorageDirFactory;
import com.dataservicios.systemauditor.BaseAlbumDirFactory;
import com.dataservicios.systemauditor.FroyoAlbumDirFactory;
import com.dataservicios.systemauditor.R;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;




//Subida de Archivos
public class UploadService extends IntentService{
    long totalSize = 0;
    private NotificationManager notificationManager;
    private Notification notification;
    Context context = this;
    public UploadService(String name) {
        super(name);
    }
    public UploadService(){
        super("UploadService");
    }
    ArrayList<String> names = new ArrayList<String>();
    private static final String url_upload_image = GlobalConstant.dominio + "/uploadImagesAudit";
    private static final String url_insert_image = GlobalConstant.dominio + "/insertImagesPoll";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    String idPDV,idPoll,tipo;
    @Override
    protected void onHandleIntent(Intent intent) {
       // notificationManager = (NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        //Uri uri  = intent.getData();
        names=intent.getStringArrayListExtra("names");
        idPDV=intent.getStringExtra("idPDV");
        idPoll=intent.getStringExtra("idPoll");
        tipo=intent.getStringExtra("tipo");
        //Log.i("FOO", uri.toString());
        new ServerUpdate().execute(names);
    }
    class ServerUpdate extends AsyncTask<ArrayList<String>,String,String> {
        //ProgressDialog pDialog;
        @Override
        protected String doInBackground(ArrayList<String>... arg0) {
            Uri uri;
            int lastPercent = 0;
            //uri=arg0[0];
            for (int i = 0; i < arg0[0].size(); i++) {
                String foto = arg0[0].get(i);
                if (uploadFoto(getAlbumDir().getAbsolutePath() + "/" + foto) && onInsert(foto)) {
                    File file = new File(getAlbumDir().getAbsolutePath() + "/" + foto);
                    file.delete();
                }

//                if (uploadFoto(getAlbumDir().getAbsolutePath() + "/" + foto) ) {
//                    File file = new File(getAlbumDir().getAbsolutePath() + "/" + foto);
//                    file.delete();
//                }
            }
           return null;


//            final HttpResponse resp;
//            final HttpClient httpClient = new DefaultHttpClient();
//            HttpPost httppost = new HttpPost("http://192.168.1.45/file/upload.php");
//            File file = new File(uri.getPath());
//            Bitmap bbicon;
//            bbicon= BitmapFactory.decodeFile(String.valueOf(file));
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            bbicon.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//            InputStream in = new ByteArrayInputStream(bos.toByteArray());
//            httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
//            //MultipartEntity mpEntity = new MultipartEntity(new ProgressListener(){});
//            AndroidMultiPartEntity mpEntity = new AndroidMultiPartEntity(new AndroidMultiPartEntity.ProgressListener() {
//                @Override
//                public void transferred(long num) {
//                    notification.contentView.setProgressBar(R.id.progressBar1, 100,(int) ((num / (float) totalSize) * 100), true);
//                    notificationManager.notify(1, notification);
//                }
//            });
//            //ContentBody foto = new FileBody(file, "image/jpeg");
//            ContentBody foto = new InputStreamBody(in, "image/jpeg", file.getName());
//            totalSize =  mpEntity.getContentLength();
//            mpEntity.addPart("fotoUp", foto);
//            httppost.setEntity(mpEntity);
//            // } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            //    e.printStackTrace();
//            // }
//            try {
//                Log.i("FOO", "About to call httpClient.execute");
//                resp = httpClient.execute(httppost);
//                if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//                    notification.setLatestEventInfo(context, "Uploading Workout", "All Done", contentIntent);
//                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
//                    notificationManager.notify(1, notification);
//                    Log.i("FOO", "All done");
//                } else {
//                    Log.i("FOO", "Screw up with http - " + resp.getStatusLine().getStatusCode());
//                }
//                resp.getEntity().consumeContent();
//            } catch (ClientProtocolException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String s) {

        }
    }


    //Metodo que escala la imágen
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
    private boolean uploadFoto(String imag){
        File file = new File(imag);
        Bitmap bbicon;
         HttpResponse resp;

        HttpClient httpClient = new DefaultHttpClient();
       // Intent notificationIntent = new Intent();
        //PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
       // notification = new Notification(R.drawable.ic_salir, "TTAudit", System.currentTimeMillis());
       // notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
      //  notification.contentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.upload_progress_bar);
      //  notification.contentIntent = contentIntent;
      //  notification.contentView.setProgressBar(R.id.progressBar1, 100,0, true);
     //   notificationManager.notify(1, notification);
        Log.i("FOO", "Notification started");
        bbicon=BitmapFactory.decodeFile(String.valueOf(file));
        Bitmap scaledBitmap = scaleDown(bbicon, 450 , true);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        //bbicon.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        InputStream in = new ByteArrayInputStream(bos.toByteArray());
        // InputStream in = new ByteArrayInputStream(bos.toByteArray());
        //If you are stuck with HTTPClient 4.0, use InputStreamBody instead:
        //ContentBody foto = new InputStreamBody(in, "image/jpeg", file.getName());
        //ContentBody foto = new FileBody(file, "image/jpeg");
        //Use a ByteArrayBody instead (available since HTTPClient 4.1), despite its name it takes a file name, too:
        ContentBody foto = new ByteArrayBody(bos.toByteArray(), file.getName());
        HttpClient httpclient = new DefaultHttpClient();

        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpPost httppost = new HttpPost(url_upload_image);
        //MultipartEntity mpEntity = new MultipartEntity();
        AndroidMultiPartEntity mpEntity = new AndroidMultiPartEntity(new AndroidMultiPartEntity.ProgressListener() {
            @Override
            public void transferred(long num) {
                //notification.contentView.setProgressBar(R.id.progressBar1, 100,(int) ((num / (float) totalSize) * 100), true);
               // notificationManager.notify(1, notification);
            }
        });

        totalSize =  mpEntity.getContentLength();
        mpEntity.addPart("fotoUp", foto);
        httppost.setEntity(mpEntity);

        try {
            Log.i("FOO", "About to call httpClient.execute");
            resp = httpClient.execute(httppost);
            if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
               // notification.setLatestEventInfo(context, "TTAudit", "Se subió correctamente la imágen", contentIntent);
               // notification.flags |= Notification.FLAG_AUTO_CANCEL;
              //  notificationManager.notify(1, notification);
                Log.i("FOO", "All done");
            } else {
              //  notification.setLatestEventInfo(context, "TTAudit", "Ocurrió un error, no se pudo subir el archivo", contentIntent);
               // notification.flags |= Notification.FLAG_AUTO_CANCEL;
              //  notificationManager.notify(1, notification);
                Log.i("FOO", "Screw up with http - " + resp.getStatusLine().getStatusCode());
            }
            resp.getEntity().consumeContent();
            return true;
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_name);
    }
    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }
    private boolean onInsert(String imag_name){
        HttpClient httpclient;
        List<NameValuePair> nameValuePairs;
        HttpPost httppost;
        httpclient=new DefaultHttpClient();
        httppost= new HttpPost(url_insert_image); // Url del Servidor
        //Añadimos nuestros datos
        nameValuePairs = new ArrayList<NameValuePair>(1);



        nameValuePairs.add(new BasicNameValuePair("archivo",imag_name));
        nameValuePairs.add(new BasicNameValuePair("store_id",idPDV));
        nameValuePairs.add(new BasicNameValuePair("poll_id",idPoll));
        nameValuePairs.add(new BasicNameValuePair("tipo",tipo));
        //idPDV

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpclient.execute(httppost);
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
