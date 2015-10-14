package com.dataservicios.interbank;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.dataservicios.systemauditor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import app.AppController;
import model.Encuesta;

/**
 * Created by usuario on 08/04/2015.
 */
public class informacion extends Activity {

    private ProgressDialog pDialog;
    private int idCompany, idPDV, idRuta, idAuditoria,idUser ;
    private JSONObject params;
    private SessionManager session;
    private String email_user, name_user;
    private  int result;
    Activity MyActivity = (Activity) this;
    TextView pregunta ;
    Button guardar;
    //RadioGroup rgTipo;
    CheckBox cbA,cbB,cbC,cbD,cbE,cbF,cbG,cbH,cbI,cbJ,cbK;
    EditText comentario;
    LinearLayout lySi , lyNo;

    RadioGroup rgTipo;
    RadioButton rbSi,rbNo;


    int vA=0,vB=0,vC=0,vD=0,vE=0,vF=0,vG=0,vH=0,vI=0,vJ=0,vK=0;
    String oA="",oB="",oC="",oD="",oE="",oF="",oG="" ,oH="" ,oI="" , oJ="", oK="";
    String totalOption="";
    int totalValores ;
    String limite="";
    // Database Helper
    private DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.inter_informacion);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Información");
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

        db = new DatabaseHelper(getApplicationContext());

        pregunta = (TextView) findViewById(R.id.tvPregunta);
        guardar = (Button) findViewById(R.id.btGuardar);
        //rgTipo=(RadioGroup) findViewById(R.id.rgTipo);
        cbA=(CheckBox) findViewById(R.id.cbA);
        cbB=(CheckBox) findViewById(R.id.cbB);
        cbC=(CheckBox) findViewById(R.id.cbC);
        cbD=(CheckBox) findViewById(R.id.cbD);
        cbE=(CheckBox) findViewById(R.id.cbE);
        cbF=(CheckBox) findViewById(R.id.cbF);
        cbG=(CheckBox) findViewById(R.id.cbG);
        cbH=(CheckBox) findViewById(R.id.cbH);
        cbI=(CheckBox) findViewById(R.id.cbI);
        cbJ=(CheckBox) findViewById(R.id.cbJ);
        cbK=(CheckBox) findViewById(R.id.cbK);

        lySi=(LinearLayout) findViewById(R.id.lyChkSi);
        lyNo=(LinearLayout) findViewById(R.id.lyChkNo);
        //cbG=(CheckBox) findViewById(R.id.cbG);
        rgTipo=(RadioGroup) findViewById(R.id.rgTipo);
        rbSi=(RadioButton) findViewById(R.id.rbSi);
        rbNo=(RadioButton) findViewById(R.id.rbNo);
        comentario = (EditText) findViewById(R.id.etComentario) ;

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
        db.deleteAllEncuesta();
        cargarPreguntasEncuesta(params);
        rgTipo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                RadioButton rb=(RadioButton)findViewById(checkedId);
                if (rbSi.getId()==checkedId){
                    ViewGroup.LayoutParams paramsSi = lySi.getLayoutParams();
                    paramsSi.height = 480;
                    lySi.setLayoutParams(new LinearLayout.LayoutParams(paramsSi));
                    ViewGroup.LayoutParams paramsNo = lyNo.getLayoutParams();
                    paramsNo.height = 2;
                    lyNo.setLayoutParams(new LinearLayout.LayoutParams(paramsNo));
                    cbA.setChecked(false);
                    cbB.setChecked(false);
                    cbC.setChecked(false);
                    cbD.setChecked(false);
                    cbE.setChecked(false);
                    cbF.setChecked(false);
                    cbG.setChecked(false);
                    cbH.setChecked(false);
                    cbI.setChecked(false);
                    cbJ.setChecked(false);
                    cbK.setChecked(false);
                } else if (rbNo.getId()==checkedId) {
                    //lySi.setVisibility(View.INVISIBLE);
                  //  lyNo.setVisibility(View.VISIBLE);

                    ViewGroup.LayoutParams paramsNo = lyNo.getLayoutParams();
                    paramsNo.height = 480;
                    lyNo.setLayoutParams(new LinearLayout.LayoutParams(paramsNo));

                    ViewGroup.LayoutParams params = lySi.getLayoutParams();
                    params.height = 2;
                    lySi.setLayoutParams(new LinearLayout.LayoutParams(params));

                    cbA.setChecked(false);
                    cbB.setChecked(false);
                    cbC.setChecked(false);
                    cbD.setChecked(false);
                    cbE.setChecked(false);
                    cbF.setChecked(false);
                    cbG.setChecked(false);
                    cbH.setChecked(false);
                    cbI.setChecked(false);
                    cbJ.setChecked(false);
                    cbK.setChecked(false);

                }
                //textViewChoice.setText("You Selected "+rb.getText());
                //Toast.makeText(getApplicationContext(), rb.getText(), Toast.LENGTH_SHORT).show();

            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                        result = 1;
                    } else if(id == rbNo.getId()){
                        result = 0;
                    }
                }


                if (cbA.isChecked()){
                    vA=1;
                    oA= pregunta.getTag().toString() + "a";
                }
                if (cbB.isChecked()){
                    vB=1;
                    oB= pregunta.getTag().toString() + "b";
                }
                if (cbC.isChecked()){
                    vC=1;
                    oC= pregunta.getTag().toString() + "c";
                }
                if (cbD.isChecked()){
                    vD=1;
                    oD= pregunta.getTag().toString() + "d";
                }
                if (cbE.isChecked()){
                    vE=1;
                    oE= pregunta.getTag().toString() + "e";
                }
                if (cbF.isChecked()){
                    vF=1;
                    oF= pregunta.getTag().toString() + "f";
                }
                if (cbG.isChecked()){
                    vG=1;
                    oG= pregunta.getTag().toString() + "g";
                }
                if (cbH.isChecked()){
                    vH=1;
                    oH= pregunta.getTag().toString() + "h";
                }
                if (cbI.isChecked()){
                    vI=1;
                    oI= pregunta.getTag().toString() + "i";
                }
                if (cbJ.isChecked()){
                    vJ=1;
                    oJ= pregunta.getTag().toString() + "j";
                }
                if (cbK.isChecked()){
                    vK=1;
                    oK= pregunta.getTag().toString() + "k";
                }

                totalValores = vA + vB + vC + vD + vE + vF + vG + vH + vI + vJ + vK;

                totalOption = oA + "|" + oB + "|" + oC + "|" + oD + "|" + oE + "|" + oF + "|" + oG + "|" + oH  + "|" + oI + "|" + oJ + "|" + oK ;


                if(totalValores<=4){
                    limite="Debajo del estándar";
                }

                if(totalValores==5 ){
                    limite="Estándar";
                }

                if(totalValores>5 ){
                    limite="Superior";
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
                            paramsData.put("media", "0");
                            paramsData.put("coment", "0");
                            paramsData.put("coment_options", "1");
                            paramsData.put("result", result);
                            paramsData.put("status", "0");
                            paramsData.put("opcion", totalOption);
                            paramsData.put("limite", limite);
                            paramsData.put("comentario", "");
                            paramsData.put("comentario_options", comentario.getText());
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
            Encuesta encuesta = db.getEncuesta(60);
            //if (idPregunta.equals("2")  ){
            pregunta.setText(encuesta.getQuestion());
            pregunta.setTag(encuesta.getId());
            //}
        }
    }

    private void cargarPreguntasEncuesta(JSONObject  paramsData){
        showpDialog();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , GlobalConstant.dominio + "/JsonGetQuestions" ,paramsData,
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

                                    Encuesta encuesta = new Encuesta();
                                    encuesta.setId(Integer.valueOf(obj.getString("id")));
                                    encuesta.setQuestion(obj.getString("question"));
                                    db.createEncuesta(encuesta);
                                    // int status = obj.getInt("state");
//                                    if (idPregunta.equals("2")  ){
//                                        pregunta.setText(question);
//                                        pregunta.setTag(idPregunta);
//                                    }
                                }
                            }
                            leerEncuesta();

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
                                Bundle argRuta = new Bundle();
                                argRuta.clear();
                                argRuta.putInt("company_id",idCompany);
                                argRuta.putInt("idPDV",idPDV);
                                argRuta.putInt("idRuta", idRuta );
                                argRuta.putInt("idAuditoria",idAuditoria);

                                Intent intent;
                                intent = new Intent(MyActivity,informacionDos.class);
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
    //    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();

        overridePendingTransition(R.anim.anim_slide_in_right,R.anim.anim_slide_out_right);
    }
}
