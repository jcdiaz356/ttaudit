package com.dataservicios.interbank;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.dataservicios.model.Audit;
import com.dataservicios.model.PollDetail;
import com.dataservicios.util.AuditUtil;
import com.dataservicios.util.GlobalConstant;
import com.dataservicios.util.SessionManager;
import com.dataservicios.systemauditor.AndroidCustomGalleryActivity;
import com.dataservicios.systemauditor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import app.AppController;
import com.dataservicios.model.Encuesta;

/**
 * Created by usuario on 30/03/2015.
 */
public class UsoIterbankAgente extends Activity {
    private static final String LOG_TAG = UsoIterbankAgente.class.getSimpleName();

    private ProgressDialog pDialog;
    private int idCompany, idPDV, idRuta, idAuditoria,idUser, idPoll ;
    private JSONObject params;
    private SessionManager session;
    private String email_user, name_user;
    private  int result;
    Activity MyActivity = (Activity) this;
    TextView pregunta ;
    Button guardar, btPhoto;
    RadioGroup rgTipo;
    RadioButton rbSi,rbNo;
    EditText comentario;

    LinearLayout ly_ChkSi ;
    CheckBox cb_A,cb_B,cb_C,cb_D,cb_E ;
    private String opciones="";

    private DatabaseHelper db;
    private PollDetail pollDetail;
    private Audit mAudit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.inter_uso_interbank_agente);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Uso de Interbank Agente");
        overridePendingTransition(R.anim.anim_slide_in_left,R.anim.anim_slide_out_left);

        db = new DatabaseHelper(getApplicationContext());

        pregunta = (TextView) findViewById(R.id.tvPregunta);
        guardar = (Button) findViewById(R.id.btGuardar);
        btPhoto =(Button) findViewById(R.id.btPhoto);
        rgTipo=(RadioGroup) findViewById(R.id.rgTipo);
        rbSi=(RadioButton) findViewById(R.id.rbSi);
        rbNo=(RadioButton) findViewById(R.id.rbNo);
        ly_ChkSi = (LinearLayout) findViewById(R.id.lyChkSi);
        cb_A = (CheckBox) findViewById(R.id.cbA);
        cb_B = (CheckBox) findViewById(R.id.cbB);
        cb_C = (CheckBox) findViewById(R.id.cbC);
        cb_D = (CheckBox) findViewById(R.id.cbD);
        cb_E = (CheckBox) findViewById(R.id.cbE);

        enabledControl(false);

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
        leerPreguntasEncuesta(params);

        btPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
            }
        });

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

                        String opcionA, opcionB , opcionC, opcionD, opcionE ;
                        opcionA ="";
                        opcionB ="";
                        opcionC = "";
                        opcionD = "";
                        opcionE = "";

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
                                if (cb_D.isChecked()){
                                    selected = true;
                                    opcionD =  idPoll + "d|";
                                }
                                if (cb_E.isChecked()){

                                    selected = true;
                                    opcionE =  idPoll + "e|";
                                }
                                opciones = opcionA + opcionB + opcionC + opcionD + opcionE ;
                                result = 1;
                                if (!selected){
                                    Toast toast;
                                    toast = Toast.makeText(MyActivity,"Debe marcar una opción" , Toast.LENGTH_LONG);
                                    toast.show();
                                    return;
                                }

                                // enabledControl(true);
                            } else if(id == rbNo.getId()){
                                result = 0;
                                // enabledControl(false);
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
                            //paramsData.put("tipo", "1");
                            paramsData.put("media", "1");
                            paramsData.put("coment", "0");
                            paramsData.put("result", result);
                            paramsData.put("opcion",opciones );
                            paramsData.put("status", "0");
                            paramsData.put("comentario", "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        insertaEncuesta(paramsData);





//                        //comentario = String.valueOf(etComent.getText()) ;
//                        pollDetail = new PollDetail();
//                        pollDetail.setPoll_id(idPoll);
//                        pollDetail.setStore_id(idPDV);
//                        pollDetail.setSino(1);
//                        pollDetail.setOptions(1);
//                        pollDetail.setLimits(0);
//                        pollDetail.setMedia(1);
//                        pollDetail.setComment(0);
//                        pollDetail.setResult(result);
//                        pollDetail.setLimite(0);
//                        pollDetail.setComentario("");
//                        pollDetail.setAuditor(idUser);
//                        pollDetail.setProduct_id(0);
//                        pollDetail.setCategory_product_id(0);
//                        pollDetail.setPublicity_id(0);
//                        pollDetail.setCompany_id(GlobalConstant.company_id);
//                        pollDetail.setCommentOptions(0);
//                        pollDetail.setSelectdOptions(opciones);
//                        pollDetail.setSelectedOtionsComment("");
//                        pollDetail.setPriority("0");
//
//                        new loadPoll().execute();


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

    private void leerPreguntasEncuesta(JSONObject  paramsData){
        showpDialog();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST , GlobalConstant.dominio + "/JsonGetQuestions" ,paramsData,
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
//                                        idPoll= Integer.valueOf(idPregunta);
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


    private void leerEncuesta() {
        if(db.getEncuestaCount()>0) {
            //Encuesta encuesta = db.getEncuesta(529);
            Encuesta encuesta = db.getEncuesta(GlobalConstant.poll_id[3]);
            //if (idPregunta.equals("2")  ){
            pregunta.setText(encuesta.getQuestion());
            pregunta.setTag(encuesta.getId());
            idPoll=encuesta.getId();
            //}
        }
    }

    private void insertaEncuesta(JSONObject paramsData) {
        showpDialog();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST ,  GlobalConstant.dominio + "/JsonInsertAuditPolls" ,paramsData,
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

                                Bundle argRuta = new Bundle();
                                argRuta.clear();
                                argRuta.putInt("company_id",idCompany);
                                argRuta.putInt("idPDV",idPDV);
                                argRuta.putInt("idRuta", idRuta );

                                argRuta.putInt("idAuditoria",idAuditoria);
                                Intent intent;
                                intent = new Intent("com.dataservicios.systemauditor.USOIBKSEGUNDO");
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

        Intent i = new Intent( MyActivity, AndroidCustomGalleryActivity.class);
        Bundle bolsa = new Bundle();
        bolsa.putString("idPDV",String.valueOf(idPDV));
        bolsa.putString("idPoll",String.valueOf(idPoll));
        bolsa.putString("tipo","1");


        i.putExtras(bolsa);
        startActivity(i);


    }

    class loadPoll extends AsyncTask<Void , Integer , Boolean> {
        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        boolean failure = false;
        @Override
        protected void onPreExecute() {
            //tvCargando.setText("Cargando Product...");
            pDialog.show();
            super.onPreExecute();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub


                if(!AuditUtil.insertPollDetail(pollDetail)) return false;


            return true;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(Boolean result) {
            // dismiss the dialog once product deleted

            if (result){
                // loadLoginActivity();

                Bundle argRuta = new Bundle();
                argRuta.clear();
                argRuta.putInt("company_id",idCompany);
                argRuta.putInt("idPDV",idPDV);
                argRuta.putInt("idRuta", idRuta );

                argRuta.putInt("idAuditoria",idAuditoria);
                Intent intent;
                //intent = new Intent("com.dataservicios.systemauditor.USOIBKSEGUNDO");
                intent = new Intent(MyActivity,UsoInterbankAgenteSegundo.class);
                intent.putExtras(argRuta);
                startActivity(intent);
                finish();



            } else {
                Toast.makeText(MyActivity , "No se pudo guardar la información intentelo nuevamente", Toast.LENGTH_LONG).show();
            }
            hidepDialog();
        }
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


    private void enabledControl(boolean state){
        if (state) {
            ly_ChkSi.setVisibility(View.VISIBLE);
            cb_A.setChecked(false);
            cb_B.setChecked(false);
            cb_C.setChecked(false);
            cb_D.setChecked(false);
            cb_E.setChecked(false);

        } else {
            ly_ChkSi.setVisibility(View.INVISIBLE);
            cb_A.setChecked(false);
            cb_B.setChecked(false);
            cb_C.setChecked(false);
            cb_D.setChecked(false);
            cb_E.setChecked(false);
        }

    }


}
