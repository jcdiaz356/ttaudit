package com.dataservicios.interbank;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dataservicios.SQLite.DatabaseHelper;
import com.dataservicios.util.GlobalConstant;
import com.dataservicios.util.SessionManager;
import com.dataservicios.systemauditor.AndroidCustomGalleryActivity;
import com.dataservicios.systemauditor.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import app.AppController;
import com.dataservicios.model.Encuesta;

/**
 * Created by usuario on 07/04/2015.
 */
public class UsoInterbankAgenteSegundo extends Activity {
    private static final String LOG_TAG = UsoInterbankAgenteSegundo.class.getSimpleName();
    private ProgressDialog pDialog;
    private int idCompany, idPDV, idRuta, idAuditoria,idUser,idPoll ;
    private JSONObject params;
    private SessionManager session;
    private String email_user, name_user;
    private  int result;
    Activity MyActivity = (Activity) this;
    TextView pregunta ;
    Button guardar , btPhoto;
    RadioGroup rgTipo;
    RadioButton rbSi,rbNo;
    //EditText comentario;

    LinearLayout ly_ChkSi ;
    CheckBox cb_A,cb_B,cb_C ;
    private String opciones="";

    // Database Helper
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.inter_uso_interbank_agente_segundo);
       // getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Uso de Interbank Agente");
        overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);
        db = new DatabaseHelper(getApplicationContext());

        pregunta = (TextView) findViewById(R.id.tvPregunta);
        guardar = (Button) findViewById(R.id.btGuardar);
        rgTipo=(RadioGroup) findViewById(R.id.rgTipo);
        rbSi=(RadioButton) findViewById(R.id.rbSi);
        rbNo=(RadioButton) findViewById(R.id.rbNo);
        //comentario = (EditText) findViewById(R.id.etComentario) ;
        btPhoto =(Button) findViewById(R.id.btPhoto);

        ly_ChkSi = (LinearLayout) findViewById(R.id.lyChkSi);
        cb_A = (CheckBox) findViewById(R.id.cbA);
        cb_B = (CheckBox) findViewById(R.id.cbB);
        cb_C = (CheckBox) findViewById(R.id.cbC);


        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando...");
        pDialog.setCancelable(false);

        session = new SessionManager(MyActivity);
        HashMap<String, String> user = session.getUserDetails();
        // name
        name_user = user.get(SessionManager.KEY_NAME);
        // email
        email_user = user.get(SessionManager.KEY_EMAIL);
        // id
        idUser = Integer.valueOf(user.get(SessionManager.KEY_ID_USER)) ;
        params = new JSONObject();
        //Recogiendo paramentro del anterior Activity
        //Bundle bundle = savedInstanceState.getArguments();
        Bundle bundle = getIntent().getExtras();
        idCompany=bundle.getInt("company_id");
        idPDV= bundle.getInt("idPDV");
        idRuta= bundle.getInt("idRuta");
        idAuditoria= bundle.getInt("idAuditoria");
        try {
            params.put("idPDV", idPDV);
            //params.put("idRuta", idRuta);
            params.put("idAuditoria", idAuditoria);
            params.put("idCompany", idCompany);
            params.put("idUser", idUser);

            //params.put("id_pdv",idPDV);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        leerEncuesta();


        btPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

        enabledControl(false);
        rgTipo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == rbSi.getId()) {
                    enabledControl(true);
                }
                if(checkedId == rbNo.getId()) {
                    enabledControl(false);
                }

            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Encuesta");
                builder.setMessage("Está seguro de guardar todas las encuestas: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String opcionA, opcionB , opcionC ;
                        opcionA ="";
                        opcionB ="";
                        opcionC = "";
                        boolean selected = false ;

                        long id = rgTipo.getCheckedRadioButtonId();
                        if (id == -1){
                            //no item selected
                            //valor ="";
                            Toast toast;
                            toast = Toast.makeText(MyActivity,"Debe seleccionar una opción" , Toast.LENGTH_LONG);
                            toast.show();
                            return;
                        }
                        else{
                            if (id == rbSi.getId()){
                                //Do something with the button


                                if (cb_A.isChecked()){
                                    selected = true;
                                    opcionA =  idPoll + "a|";
                                }
                                if (cb_B.isChecked()){
                                    selected = true;
                                    opcionB =  idPoll + "b|";
                                }
                                if (cb_C.isChecked()){
                                    selected = true;
                                    opcionC =  idPoll + "c|";
                                }
                                opciones = opcionA + opcionB + opcionC ;
                                result = 1;

                                if (!selected){
                                    Toast toast;
                                    toast = Toast.makeText(MyActivity,"Debe marcar una opción" , Toast.LENGTH_LONG);
                                    toast.show();
                                    return;
                                }


                            } else if(id == rbNo.getId()){
                                result = 0;
                            }
                        }







                        JSONObject paramsData;
                        paramsData = new JSONObject();
                        try {
                            paramsData.put("poll_id", pregunta.getTag());
                            paramsData.put("user_id", String.valueOf(idUser));
                            paramsData.put("store_id", idPDV);
                            paramsData.put("idAuditoria", idAuditoria);
                            paramsData.put("idCompany", idCompany);
                            paramsData.put("idRuta", idRuta);
                            paramsData.put("sino", "1");
                            paramsData.put("options", "1");
                            paramsData.put("limits", "0");
                            paramsData.put("media", "1");
                            paramsData.put("tipo", "1");
                            paramsData.put("coment", "0");
                            paramsData.put("opcion",opciones );
                            paramsData.put("result", result);
                            paramsData.put("status", "0");
                            paramsData.put("comentario", "");
                            //params.put("id_pdv",idPDV);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        insertaEncuesta(paramsData);
                        dialog.dismiss();

                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                });

                builder.show();
                builder.setCancelable(false);

            }
        });

    }
    private void leerEncuesta() {
        if(db.getEncuestaCount()>0) {
            // Encuesta encuesta = db.getEncuesta(530);
            Encuesta encuesta = db.getEncuesta(GlobalConstant.poll_id[4]);
            //if (idPregunta.equals("2")  ){
            pregunta.setText(encuesta.getQuestion());
            pregunta.setTag(encuesta.getId());
            idPoll= encuesta.getId();
            //}
        }
    }




    private void insertaEncuesta(JSONObject paramsData) {
        showpDialog();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , GlobalConstant.dominio + "/JsonInsertAuditPolls" ,paramsData,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d(LOG_TAG, response.toString());
                        //adapter.notifyDataSetChanged();
                        try {
                            //String agente = response.getString("agentes");
                            int success =  response.getInt("success");
                            //idCompany =response.getInt("company");
                            if (success == 1) {

                                Toast toast;
                                toast = Toast.makeText(MyActivity, "Se guardo correctamente los datos", Toast.LENGTH_LONG);
                                toast.show();
                                //onBackPressed();

                                Bundle argRuta = new Bundle();
                                argRuta.clear();
                                argRuta.putInt("company_id",idCompany);
                                argRuta.putInt("idPDV",idPDV);
                                argRuta.putInt("idRuta", idRuta );

                                argRuta.putInt("idAuditoria",idAuditoria);
                                Intent intent;
                                intent = new Intent("com.dataservicios.systemauditor.USOIBKTERCERO");
                                intent.putExtras(argRuta);
                                startActivity(intent);
                                finish();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        hidepDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //VolleyLog.d(TAG, "Error: " + error.getMessage());
                        hidepDialog();
                    }
                }
        );

        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }
    // Camera
    private void takePhoto() {
        /*Intent intent = new Intent((MediaStore.ACTION_IMAGE_CAPTURE));
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());

        startActivityForResult(intent, 100);
        */

        //Bundle bundle = getIntent().getExtras();
        //String id_agente = bundle.getString(TAG_ID);

        // getting values from selected ListItem
        // String aid = id_agente;
        // Starting new intent
        Intent i = new Intent( MyActivity, AndroidCustomGalleryActivity.class);
        Bundle bolsa = new Bundle();
        bolsa.putString("idPDV",String.valueOf(idPDV));
        bolsa.putString("idPoll",String.valueOf(idPoll));
        bolsa.putString("tipo","1");

        i.putExtras(bolsa);
        startActivity(i);


    }
    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //return super.onOptionsItemSelected(item);
    }
        @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        this.finish();
//
//        overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
    }

    private void enabledControl(boolean state){
        if (state) {
            ly_ChkSi.setVisibility(View.VISIBLE);
            cb_A.setChecked(false);
            cb_B.setChecked(false);
            cb_C.setChecked(false);


        } else {
            ly_ChkSi.setVisibility(View.INVISIBLE);
            cb_A.setChecked(false);
            cb_B.setChecked(false);
            cb_C.setChecked(false);

        }

    }
}
