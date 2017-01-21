package adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.dataservicios.systemauditor.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jaime Eduardo on 15/10/2015.
 */
public class ImageAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Activity activity;
    ArrayList<ViewHolder> holders = new ArrayList<ViewHolder>();
    ArrayList<String> f = new ArrayList<String>();

    private boolean[] checkValue;

    public ImageAdapter(Activity activity, ArrayList<String> f) {
        this.activity = activity;
        this.f = f;
        mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
   // public ImageAdapter() {

   // }

    public int getCount() {
        return f.size();
    }

    public ViewHolder getItem(int position) {
        return holders.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.galleryitem, null);
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
        //Rotación imagen
        //matrix.postRotate(90);
        Bitmap myBitmap1 =  Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
        holder.imageview.setImageBitmap(myBitmap1);
        //holder.checkbox.setText(String.valueOf(position));
        holders.add(holder);

        //holder.checkbox.setId(position);
        String strPath = f.get(position).toString();
        holder.checkbox.setTag(strPath);
        //holder.imageview.setId(position);
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Toast toast;
                toast = Toast.makeText(activity , "Ha selecionado " + String.valueOf(position) + " imagenes", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        });




        return convertView;
    }

    public class  ViewHolder {
        public ImageView imageview;
        public CheckBox checkbox;
    }

}
