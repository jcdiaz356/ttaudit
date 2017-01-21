package com.dataservicios.interbank;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dataservicios.SQLite.DatabaseHelper;
import com.dataservicios.util.GlobalConstant;
import com.dataservicios.util.SessionManager;
import com.dataservicios.systemauditor.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import app.AppController;
import com.dataservicios.model.Encuesta;

/**
 * Created by usuario on 08/04/2015.
 */
public class EvaluacionTratoTres extends Activity {
    private static final String LOG_TAG = EvaluacionTratoTres.class.getSimpleName();
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
    CheckBox cbA,cbB,cbC,cbD,cbE,cbF,cbG;
    EditText comentario;

    int vA=0,vB=0,vC=0,vD=0,vE=0,vF=0,vG=0;
    String oA="",oB="",oC="",oD="",oE="",oF="",oG="";
    String totalOption="";
    int totalValores ;
    String limite="";
    // Database Helper
    private DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.inter_evaluacion_trato_tres);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Evaluación de Trato");
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

        db = new DatabaseHelper(getApplicationContext());

        pregunta = (TextView) findViewById(R.id.tvPregunta);
        guardar = (Button) findViewById(R.id.btGuardar);
        //rgTipo=(RadioGroup) findViewById(R.id.rgTipo);
        cbA=(CheckBox) findViewById(R.id.cbA);
        cbB=(CheckBox) findViewById(R.id.cbB);
        cbC=(CheckBox) findViewById(R.id.cbC);
       // cbD=(CheckBox) findViewById(R.id.cbD);
//        cbE=(CheckBox) findViewById(R.id.cbE);
//        cbF=(CheckBox) findViewById(R.id.cbF);
//        cbG=(CheckBox) findViewById(R.id.cbG);

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
        //db.deleteAllEncuesta();
        //cargarPreguntasEncuesta(params);
        leerEncuesta();

        //

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //long id = rgTipo.getCheckedRadioButtonId();

                if (cbA.isChecked()){
                    vA=1;
                    oA= pregunta.getTag() + "a";
                }
                if (cbB.isChecked()){
                    vB=1;
                    oB= pregunta.getTag() + "b";
                }
                if (cbC.isChecked()){
                    vC=1;
                    oC= pregunta.getTag() + "c";
                }
//                if (cbD.isChecked()){
//                    vD=1;
//                    oD= pregunta.getText() + "d";
//                }
//                if (cbE.isChecked()){
//                    vE=1;
//                    oE= pregunta.getText() + "e";
//                }
//                if (cbF.isChecked()){
//                    vF=1;
//                    oF= pregunta.getText() + "f";
//                }
//                if (cbG.isChecked()){
//                    vG=1;
//                    oG= pregunta.getText() + "g";
//                }

                totalValores = vA + vB + vC + vD + vE + vF + vG;

                totalOption = oA + "|" + oB + "|" + oC; //+ "|" + oD ;// + oE + "|" + oF + "|" + oG ;


                if(totalValores<=1){
                    limite=" Debajo del estándar";
                }

                if(totalValores==2 ){
                    limite="Estándar";
                }

                if(totalValores>2 ){
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
                            paramsData.put("user_id", String.valueOf(idUser));
                            paramsData.put("store_id", idPDV);
                            paramsData.put("idAuditoria", idAuditoria);
                            paramsData.put("idCompany", idCompany);
                            paramsData.put("idRuta", idRuta);
                            paramsData.put("sino", "0");
                            paramsData.put("options", "1");
                            paramsData.put("limits", "1");
                            paramsData.put("media", "0");
                            paramsData.put("coment", "0");
                            paramsData.put("result", result);
                            paramsData.put("status", "1");
                            paramsData.put("opcion", totalOption);
                            paramsData.put("limite", limite);
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
            //Encuesta encuesta = db.getEncuesta(551);
            Encuesta encuesta = db.getEncuesta(GlobalConstant.poll_id[25]);
            //if (idPregunta.equals("2")  ){
            pregunta.setText(encuesta.getQuestion());
            pregunta.setTag(encuesta.getId());
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
                                // onBackPressed();
//                                Bundle argRuta = new Bundle();
//                                argRuta.clear();
//                                argRuta.putInt("company_id",idCompany);
//                                argRuta.putInt("idPDV",idPDV);
//                                argRuta.putInt("idRuta", idRuta );
//                                argRuta.putInt("idAuditoria",idAuditoria);
//
//                                Intent intent;
//                                intent = new Intent(MyActivity,EvaluacionTratoTres.class);
//                                intent.putExtras(argRuta);
//                                startActivity(intent);
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
}
