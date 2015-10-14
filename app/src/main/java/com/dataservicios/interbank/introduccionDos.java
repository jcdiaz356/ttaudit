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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.dataservicios.librerias.GlobalConstant;
import com.dataservicios.librerias.SessionManager;
import com.dataservicios.systemauditor.AndroidCustomGalleryActivity;
import com.dataservicios.systemauditor.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import app.AppController;
import model.Encuesta;

/**
 * Created by usuario on 09/04/2015.
 */
public class introduccionDos extends Activity {
    private ProgressDialog pDialog;
    private int idCompany, idPDV, idRuta, idAuditoria,idUser,idPoll ;
    private JSONObject params;
    private SessionManager session;
    private String email_user, name_user , opciones;
    private  int result;
    Activity MyActivity = (Activity) this;
    TextView pregunta ;
    Button guardar, btPhoto;

    RadioGroup rgTipo, rg_Options;
    RadioButton rbSi,rbNo , rb_A, rb_B, rb_C;
   // EditText comentario;
    TextView tvComentario;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.inter_introduccion_dos);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Introducción");
        overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);

        db = new DatabaseHelper(getApplicationContext());

        pregunta = (TextView) findViewById(R.id.tvPregunta);
        guardar = (Button) findViewById(R.id.btGuardar);
        btPhoto =(Button) findViewById(R.id.btPhoto);
        rgTipo=(RadioGroup) findViewById(R.id.rgTipo);
        rg_Options = (RadioGroup) findViewById(R.id.rgOptions);
        rb_A = (RadioButton) findViewById(R.id.rbA);
        rb_B = (RadioButton) findViewById(R.id.rbB);
        rb_C = (RadioButton) findViewById(R.id.rbC);
        rbSi=(RadioButton) findViewById(R.id.rbSi);
        rbNo=(RadioButton) findViewById(R.id.rbNo);

        //tvComentario = (TextView) findViewById(R.id.tvComentario);
        opciones ="";

       // comentario = (EditText) findViewById(R.id.etComentario) ;
//        pregunta.setText("Hoa");
//        pregunta.setTag("145");

        Toast toast;
        toast = Toast.makeText(MyActivity, pregunta.getText() + ": " +pregunta.getTag() , Toast.LENGTH_LONG);
        toast.show();

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

        enabledControl(false);
        rgTipo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton rb=(RadioButton)findViewById(checkedId);
                if (rbSi.getId()==checkedId){

                    enabledControl(false);

                } else if (rbNo.getId()==checkedId) {

                    //tvComentario.setVisibility(View.VISIBLE);
                    enabledControl(true);
//
                }


            }
        });

       rg_Options.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(RadioGroup group, int checkedId) {
               RadioButton rb=(RadioButton)findViewById(checkedId);

               if (rb_A.getId()==checkedId){
                  opciones = String.valueOf(idPoll)  + "A";
               }
               if (rb_B.getId()==checkedId){
                   opciones = String.valueOf(idPoll)  + "B";
               }
               if (rb_B.getId()==checkedId){
                   opciones = String.valueOf(idPoll)  + "C";
               }
           }
       });

        btPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int id = rgTipo.getCheckedRadioButtonId();
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
                        result = 1;

                    } else if(id == rbNo.getId()){
                        result = 0;

                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity);
                builder.setTitle("Guardar Encuesta");
                builder.setMessage("Está seguro de guardar todas las encuestas: ");
                builder.setPositiveButton("Si", new DialogInterface.OnClickListener()

                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        JSONObject paramsData;
                        paramsData = new JSONObject();
                        try {
                            paramsData.put("poll_id", pregunta.getTag());
                            paramsData.put("store_id", idPDV);
                            paramsData.put("idAuditoria", idAuditoria);
                            paramsData.put("idCompany", idCompany);
                            paramsData.put("idRuta", idRuta);
                            paramsData.put("sino", "1");
                            paramsData.put("options", "1");
                            paramsData.put("limits", "0");
                            paramsData.put("media", "1");
                            paramsData.put("coment", "0");
                            paramsData.put("comentario", "");
                            paramsData.put("opcion", opciones);
                            paramsData.put("result", result);
                            if(result==0){
                                paramsData.put("status", "1");
                                GlobalConstant.global_close_audit=1;
                            } else if(result==1){
                                paramsData.put("status", "0");
                            }


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

    private void insertaEncuesta(JSONObject paramsData) {
        showpDialog();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , GlobalConstant.dominio + "/JsonInsertAuditPolls" ,paramsData,
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
                            //idCompany =response.getInt("company");
                            if (success == 1) {

                                Toast toast;
                                toast = Toast.makeText(MyActivity, "Se guardo correctamente los datos", Toast.LENGTH_LONG);
                                toast.show();
                               // onBackPressed();

                                if(result==0){
                                    GlobalConstant.global_close_audit=1;
                                    finish();
                                } else if(result==1){
                                    Bundle argRuta = new Bundle();
                                    argRuta.clear();
                                    argRuta.putInt("company_id",idCompany);
                                    argRuta.putInt("idPDV",idPDV);
                                    argRuta.putInt("idRuta", idRuta );

                                    argRuta.putInt("idAuditoria",idAuditoria);
                                    Intent intent;
                                    intent = new Intent(MyActivity,introduccionTres.class);
                                    intent.putExtras(argRuta);
                                    startActivity(intent);
                                    finish();
                                }

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
    public void onBackPressed() {
//        super.onBackPressed();
//        this.finish();
//
//
//        overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
    }
    private void leerEncuesta() {
        if(db.getEncuestaCount()>0) {
            Encuesta encuesta = db.getEncuesta(67);
            //if (idPregunta.equals("2")  ){
            pregunta.setText(encuesta.getQuestion());
            pregunta.setTag(encuesta.getId());
            idPoll=encuesta.getId();
            //}
        }
    }

    //Lee las preguntas del servidor para mostralo en la inteface
    private void leerPreguntasEncuesta(JSONObject  params){
        showpDialog();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , GlobalConstant.dominio + "/JsonGetQuestions" ,params,
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
                            //idCompany =response.getInt("company");
                            if (success == 1) {
                                JSONArray agentesObjJson;
                                agentesObjJson = response.getJSONArray("questions");
                                // looping through All Products
                                for (int i = 0; i < agentesObjJson.length(); i++) {
                                    JSONObject obj = agentesObjJson.getJSONObject(i);
                                    // Storing each json item in variable
                                    String idPregunta = obj.getString("id");
                                    String question = obj.getString("question");
                                    // int status = obj.getInt("state");
                                    if (idPregunta.equals("27")  ){
                                        pregunta.setText(question);
                                        pregunta.setTag(idPregunta);
                                    }

                                }
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



    private void enabledControl(boolean state){
        if (state) {
            rg_Options.setVisibility(View.VISIBLE);
            rg_Options.setEnabled(true);
            rb_A.setChecked(false);
            rb_B.setChecked(false);
            rb_C.setChecked(false);

        } else {
            rg_Options.setVisibility(View.INVISIBLE);
            rg_Options.setEnabled(false);
            rb_A.setChecked(false);
            rb_B.setChecked(false);
            rb_C.setChecked(false);

        }

    }

}
