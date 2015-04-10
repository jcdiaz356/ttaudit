package com.dataservicios.systemauditor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.dataservicios.Services.UploadService;

import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.content.ContentBody;
//import org.apache.http.entity.mime.content.FileBody;
//import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 06/02/2015.
 */
public class AndroidCustomGalleryActivity extends Activity {


    private static final int TAKE_PICTURE = 1;
    private String mCurrentPhotoPath;
    private static final String JPEG_FILE_PREFIX = "-Agente_foto_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private static final String url_insert_image = "http://redagentesyglobalnet.com/insertImagesAgent";
    private static final String url_upload_image = "http://redagentesyglobalnet.com/uploadImagesAgent";

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private ImageAdapter imageAdapter;
    ArrayList<String> f = new ArrayList<String>();// list of file paths
    File[] listFile;
    ArrayList<String> names = new ArrayList<String>();
    Activity MyActivity ;
    String idPDV,idPoll,tipo;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        MyActivity = (Activity) this;

        Bundle bundle = getIntent().getExtras();
        idPDV = bundle.getString("idPDV");
        idPoll = bundle.getString("idPoll");
        tipo = bundle.getString("tipo");


        getFromSdcard();

        final GridView imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
        imageAdapter = new ImageAdapter();
        imagegrid.setAdapter(imageAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }


        Button btn_photo = (Button)findViewById(R.id.take_photo);
        Button btn_upload = (Button)findViewById(R.id.save_images);
        // Register the onClick listener with the implementation above
        btn_photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                // create intent with ACTION_IMAGE_CAPTURE action
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


                Bundle bundle = getIntent().getExtras();
                String idPDV = bundle.getString("idPDV");

                // Create an image file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = String.format("%06d", Integer.parseInt(idPDV))+JPEG_FILE_PREFIX + timeStamp;
                File albumF = getAlbumDir();
                // to save picture remove comment
                File file = new File(albumF,imageFileName+JPEG_FILE_SUFFIX);

                Uri photoPath = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoPath);

                mCurrentPhotoPath = getAlbumDir().getAbsolutePath();

                // start camera activity
                startActivityForResult(intent, TAKE_PICTURE);

            }
        });


        btn_upload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {

//                Bundle bundle = getIntent().getExtras();
//                String idPDV = bundle.getString("idPDV");
//                String idPoll = bundle.getString("idPoll");
//                String tipo = bundle.getString("tipo");



                File file= new File(Environment.getExternalStorageDirectory().toString()+"/Pictures/CameraSample");

                if (file.isDirectory())
                {
                    listFile = file.listFiles();
                    int holder_counter = 1;
                    int contador=0;
                    //Verificando si se ha seleccionado alguna foto
                    if (listFile.length>0){
                        for (int i = 0; i < listFile.length; i++){
                            if (imageAdapter.getItem(holder_counter).checkbox.isChecked())
                            {
                                contador ++;
                            }
                            holder_counter++;
                        }
                        if (contador==0){
                            Toast toast;
                            toast = Toast.makeText(MyActivity , "Debe Seleccionar almenos una imagen", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                    } else{
                        Toast toast;
                        toast = Toast.makeText(MyActivity , "No hay ninguna foto en la galeria", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }

                    holder_counter=1;

                    for (int i = 0; i < listFile.length; i++)
                    {
                        if (  listFile[i].getName().substring(0,6).equals(String.format("%06d", Integer.parseInt(idPDV)) ))
                        {
                            if (imageAdapter.getItem(holder_counter).checkbox.isChecked())
                            {
                                String name = listFile[i].getName();
                                names.add(name);
                            }
                            holder_counter++;
                        }
                    }
                }
                //new ServerUpdate().execute(names);

                //String foto = names.get(0);
                //Uri uri = Uri.fromFile(new File(file +"/"+ foto) );

                Intent intent = new Intent(MyActivity, UploadService.class);
                //Log.i("FOO", uri.toString());
                Bundle argPDV = new Bundle();
                argPDV.putString("idPDV",idPDV );
                argPDV.putString("idPoll",idPoll );
                argPDV.putString("tipo",tipo);

                intent.putStringArrayListExtra("names", names);
//                intent.putExtra("idPDV", idPDV);
//               intent.putExtra("idPoll", idPoll);
//               intent.putExtra("tipo", tipo);
                intent.putExtras(argPDV);
                //intent.set(names);
                startService(intent);
                // getting values from selected ListItem
//                String aid = idPDV;
//                // Starting new intent
//                Intent i = new Intent( AndroidCustomGalleryActivity.this , AgenteDetailActivity.class);
//                Bundle bolsa = new Bundle();
//                bolsa.putString("id", aid);
//                i.putExtras(bolsa);
//                startActivity(i);
                finish();

            }
        });
    }


    //Enviar a AgenteDetailActivity


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bundle bundle = getIntent().getExtras();
        String idPDV = bundle.getString("idPDV");

        // getting values from selected ListItem
        String aid = idPDV;
        switch (item.getItemId()) {
            case android.R.id.home:
                // go to previous screen when app icon in action bar is clicked

                // app icon in action bar clicked; goto parent activity.
                onBackPressed();
                return true;
//                Intent intent = new Intent(this, AgenteDetailActivity.class);
//                Bundle bolsa = new Bundle();
//                bolsa.putString("id", aid);
//                intent.putExtras(bolsa);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }


//Comprimiendo imagen
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


    private boolean onInsert(String imag_name){
        Bundle bundle = getIntent().getExtras();
        String idPDV = bundle.getString("idPDV");
        HttpClient httpclient;
        List<NameValuePair> nameValuePairs;
        HttpPost httppost;
        httpclient=new DefaultHttpClient();
        httppost= new HttpPost(url_insert_image); // Url del Servidor
        //Añadimos nuestros datos
        nameValuePairs = new ArrayList<NameValuePair>(1);
        nameValuePairs.add(new BasicNameValuePair("imagen",imag_name));
        nameValuePairs.add(new BasicNameValuePair("idPDV",idPDV));

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            }
        }
    }


    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            galleryAddPic();
            mCurrentPhotoPath = null;
            Bundle bundle = getIntent().getExtras();
           // String idPDV = bundle.getString("idPDV");
            // getting values from selected ListItem
            //String idPDV = idPDV;
            // Starting new intent
            Intent i = new Intent( AndroidCustomGalleryActivity.this , AndroidCustomGalleryActivity.class);
            Bundle bolsa = new Bundle();
            bolsa.putString("idPDV", idPDV);
            bolsa.putString("idPDV", idPoll);
            bolsa.putString("idPDV", tipo);
            i.putExtras(bolsa);
            startActivity(i);
            finish();
        }

    }


    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

    }



    public void getFromSdcard()
    {
        Bundle bundle = getIntent().getExtras();
        String idPDV = bundle.getString("idPDV");

        File file= new File(Environment.getExternalStorageDirectory().toString()+"/Pictures/CameraSample");

        if (file.isDirectory())
        {
            listFile = file.listFiles();

            for (int i = 0; i < listFile.length; i++)
            {
                if (  listFile[i].getName().substring(0,6).equals(String.format("%06d", Integer.parseInt(idPDV)) ))
                {
                    f.add(listFile[i].getAbsolutePath());
                }

            }
        }
    }

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        ArrayList<ViewHolder> holders = new ArrayList<ViewHolder>();


        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return f.size();
        }

        public ViewHolder getItem(int position) {
            return holders.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(
                        R.layout.galleryitem, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.itemCheckBox);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap myBitmap = BitmapFactory.decodeFile(f.get(position), options);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap myBitmap1 =  Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
            holder.imageview.setImageBitmap(myBitmap1);
            holders.add(holder);
            return convertView;
        }

    }
    class ViewHolder {
        ImageView imageview;
        CheckBox checkbox;
    }

    public void onBackPressed() {
        super.onBackPressed();
        this.finish();

        overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
    }
}
