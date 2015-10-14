package com.dataservicios.systemauditor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dataservicios.librerias.GPSTracker;
import com.dataservicios.librerias.GlobalConstant;
import com.dataservicios.librerias.SessionManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import adapter.CustomListAdapter;
import adapter.PdvsAdapter;
import app.AppController;
import model.Movie;
import model.Pdv;
import model.Ruta;

/**
 * Created by usuario on 06/01/2015.
 */
public class PuntosVenta extends Activity {

    // Log tag
    //private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG = PuntoVenta.class.getSimpleName();

    EditText pdvs1,pdvsAuditados1,porcentajeAvance1;
    private TextView tvPDVSdelDía;

    // Movies json url
    private static final String url = "http://www.dataservicios.com/webservice/pdvspordia.php";
    private ProgressDialog pDialog;
    private List<Pdv> pdvList = new ArrayList<Pdv>();
    private ListView listView;
    private PdvsAdapter adapter;
    private int IdRuta ;
    private String fechaRuta;
    private Button bt;

    Activity MyActivity ;
    private JSONObject params;
    private SessionManager session;
    private String email_user, id_user, name_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puntos_venta);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("PDVs");
        overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);


        final Activity MyActivity = (Activity) this;

        pdvs1 = (EditText) findViewById(R.id.etPDVS);
        pdvsAuditados1 = (EditText) findViewById(R.id.etPDVSAuditados);
        porcentajeAvance1 = (EditText) findViewById(R.id.etPorcentajeAvance);
        tvPDVSdelDía = (TextView) findViewById(R.id.tvPDVSdelDía);

        Bundle bundle = getIntent().getExtras();
        IdRuta= bundle.getInt("idRuta");
        fechaRuta = bundle.getString("fechaRuta");

        tvPDVSdelDía.setText(fechaRuta);


        session = new SessionManager(MyActivity);
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        // name
        name_user = user.get(SessionManager.KEY_NAME);
        // email
        email_user = user.get(SessionManager.KEY_EMAIL);
        // id
        id_user = user.get(SessionManager.KEY_ID_USER);
        //Añadiendo parametros para pasar al Json por metodo POST
        params = new JSONObject();
        try {
            params.put("id", IdRuta);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        bt = (Button) findViewById(R.id.button1);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle argRuta = new Bundle();
                argRuta.putInt("id",IdRuta);
                Intent intent = new Intent("com.dataservicios.systemauditor.MAPARUTAS");
                intent.putExtras(argRuta);
                startActivity(intent);
            }
        });
        listView = (ListView) findViewById(R.id.list);
        adapter = new PdvsAdapter(this, pdvList);

        listView.setAdapter(adapter);
        // Click event for single list row
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Obteniendo fecha y hora
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String strDate = sdf.format(c.getTime());
                GlobalConstant.inicio = strDate;
                Log.i("FECHA",strDate);

                //Obteniendo Ubicacion
                GPSTracker gps = new GPSTracker(MyActivity);

                // Verificar si GPS esta habilitado
                if(gps.canGetLocation()){
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    GlobalConstant.latitude_open = latitude;
                    GlobalConstant.longitude_open = longitude;
                    //Toast toast = Toast.makeText(getApplicationContext(), "Lat: " + String.valueOf(latitude) + "Long: " + String.valueOf(longitude), Toast.LENGTH_SHORT);
                    //toast.show();
                }else{
                    // Indicar al Usuario que Habilite su GPS
                    gps.showSettingsAlert();
                }

                // selected item
                String selected =((TextView) view.findViewById(R.id.tvId)).getText().toString();
                Toast toast = Toast.makeText(getApplicationContext(), selected, Toast.LENGTH_SHORT);
                toast.show();
                Bundle argPDV = new Bundle();
                argPDV.putInt("idPDV", Integer.valueOf(selected) );
                argPDV.putInt("idRuta", Integer.valueOf(IdRuta) );
                argPDV.putString("fechaRuta",fechaRuta);
                Intent intent = new Intent("com.dataservicios.systemauditor.DETALLEPDV");
                intent.putExtras(argPDV);
                startActivity(intent);
            }
        });
        listView.setAdapter(adapter);
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();


//        JsonArrayRequest movieReq = new JsonArrayRequest(url+"?idruta="+IdRuta,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        Log.d(TAG, response.toString());
//                        hidePDialog();
//                        // Parsing json
//                        for (int i = 0; i < response.length(); i++) {
//                            try {
//                                JSONObject obj = response.getJSONObject(i);
//                                Pdv pdv = new Pdv();
//                                pdv.setId(obj.getInt("id"));
//                                pdv.setPdv(obj.getString("pdv"));
//                                pdv.setThumbnailUrl(obj.getString("image"));
//                                pdv.setDireccion(obj.getString("direccion"));
//                                pdv.setDistrito(obj.getString("distrito"));
//                                pdv.setStatus(obj.getInt("status"));
//                                // adding movie to movies array
//                                pdvList.add(pdv);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        // notifying list adapter about data changes
//                        // so that it renders the list view with updated data
//                        adapter.notifyDataSetChanged();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                hidePDialog();
//            }
//        });
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(movieReq);


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , GlobalConstant.dominio + "/JsonRoadsDetail" ,params,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("DATAAAA", response.toString());
                        //adapter.notifyDataSetChanged();
                        try {
                            //String agente = response.getString("agentes");
                            int success =  response.getInt("success");
                            float contadorPDVS =0 ;
                            float auditadosPDV =0;
                            if (success == 1) {
//
                                JSONArray ObjJson;
                                ObjJson = response.getJSONArray("roadsDetail");
                                // looping through All Products
                                if(ObjJson.length() > 0) {

                                    contadorPDVS = contadorPDVS + Integer.valueOf(response.getString("pdvs"));
                                    auditadosPDV =  auditadosPDV + Integer.valueOf(response.getString("auditados"));

                                    for (int i = 0; i < ObjJson.length(); i++) {
//                                    JSONObject obj = agentesObjJson.getJSONObject(i);
//                                    // Storing each json item in variable
//                                    String idAuditoria = obj.getString("id");
//                                    String auditoria = obj.getString("auditoria");
//                                    int status = obj.getInt("status");
                                        try {

//                                            JSONObject obj = agentesObjJson.getJSONObject(i);
//                                            contadorPDVS = contadorPDVS + Integer.valueOf(obj.getString("pdvs"));
//                                            auditadosPDV =  auditadosPDV + Integer.valueOf(obj.getString("auditados"));
//                                            Ruta ruta = new Ruta();
//                                            ruta.setId(obj.getInt("id"));
//                                            ruta.setRutaDia(obj.getString("fullname"));
//                                            ruta.setPdvs(Integer.valueOf(obj.getString("pdvs")) );
//                                            ruta.setPorcentajeAvance(Integer.valueOf(obj.getString("auditados")));
//                                            // adding movie to movies array
//                                            rutaList.add(ruta);
                                            JSONObject obj = ObjJson.getJSONObject(i);
                                            Pdv pdv = new Pdv();
                                            pdv.setId(Integer.valueOf(obj.getString("id")));
                                            pdv.setPdv(obj.getString("fullname"));
                                            //pdv.setThumbnailUrl(obj.getString("image"));
                                            pdv.setDireccion(obj.getString("address"));
                                            pdv.setDistrito(obj.getString("district"));
                                            pdv.setStatus(obj.getInt("status"));


//                                            int idpuntoventa=Integer.valueOf(obj.getString("id"));
//                                            if(idpuntoventa==29){
//                                                pdv.setStatus(1);
//                                            } else if(idpuntoventa==34){
//                                                pdv.setStatus(1);
//                                            } else{
//                                                pdv.setStatus(0);
//                                            }
                                            // adding movie to movies array
                                            pdvList.add(pdv);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }



                                    pdvs1.setText(String.valueOf(contadorPDVS)) ;
                                    pdvsAuditados1.setText(String.valueOf(auditadosPDV));

                                    float porcentajeAvance=(auditadosPDV / contadorPDVS) *100;
                                    BigDecimal big = new BigDecimal(porcentajeAvance);
                                    big = big.setScale(2, RoundingMode.HALF_UP);
                                    porcentajeAvance1.setText( String.valueOf(big ) + " % ");
                                }





                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        adapter.notifyDataSetChanged();
                        hidePDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //VolleyLog.d(TAG, "Error: " + error.getMessage());
                        hidePDialog();
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
//        Intent a = new Intent(this,PanelAdmin.class);
//        //a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(a);
        overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
//                this.finish();
//                Intent a = new Intent(this,PanelAdmin.class);
//                //a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(a);
//                overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        finish();
        startActivity(getIntent());
    }
}

