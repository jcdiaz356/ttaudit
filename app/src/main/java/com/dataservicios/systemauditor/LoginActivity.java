package com.dataservicios.systemauditor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dataservicios.SQLite.DatabaseHelper;
import com.dataservicios.util.GlobalConstant;
import com.dataservicios.util.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import com.dataservicios.model.User;
import com.dataservicios.util.JSONParserX;

/**
 * Created by usuario on 05/11/2014.
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    Button ingresar, btLlamar, btUbicar;
    EditText usuario,contrasena;
    private DatabaseHelper db;
    // Progress Dialog
    private ProgressDialog pDialog;
    // Session Manager Class
    SessionManager session;
    String username, password;

    // JSON parser class
    //JSONParserX jsonParser = new JSONParserX();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ingresar = (Button) findViewById(R.id.btIngresar);
        btLlamar = (Button) findViewById(R.id.btLlamar);
        btUbicar = (Button) findViewById(R.id.btUbicar);
        usuario =   (EditText) findViewById(R.id.etUsuario);
        contrasena = (EditText) findViewById(R.id.etContrasena);

        ingresar.setOnClickListener(this);
        btLlamar.setOnClickListener(this);
        btUbicar.setOnClickListener(this);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        db = new DatabaseHelper(getApplicationContext());
        if(db.getUserCount() > 0) {
            //User users = new User();
            List<User> usersList = db.getAllUser();
            if(usersList.size()>0) {
                User users = new User();
                users=usersList.get(0);
                usuario.setText(users.getNombre());
                //contrasena.setText(users.getPassword());
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btIngresar:
                //String nombre = usuario.getText().toString();
//                if (usuario.getText().toString().equals("jaime") && contrasena.getText().toString().equals("jaime"))
//                {
//                    Intent intent = new Intent("com.dataservicios.redagenteglobalapp.LISTGENTE");
//                    Bundle bolsa = new Bundle();
//                    bolsa.putString("NOMBRE", nombre);
//                    intent.putExtras(bolsa);
//                    startActivity(intent);
//                } else {
//                    Toast toast = Toast.makeText(this , "La contraseña o usario es incorrecto", Toast.LENGTH_SHORT );
//                    toast.show();
//                }
//
                if (usuario.getText().toString().trim().equals("") )
                {
                     Toast toast = Toast.makeText(this , "Ingrese un Usuario", Toast.LENGTH_SHORT );
                    toast.show();
                     usuario.requestFocus();
                }else if (contrasena.getText().toString().trim().equals("")) {
                    Toast toast = Toast.makeText(this , "Ingrese una Contraseña ", Toast.LENGTH_SHORT );
                    toast.show();
                    contrasena.requestFocus();
                }else {

                            username = usuario.getText().toString();
                            password = contrasena.getText().toString();
                            new AttemptLogin().execute();


                }
                break;
            case R.id.btLlamar:
                try {
                    Intent my_callIntent = new Intent(Intent.ACTION_CALL);
                    my_callIntent.setData(Uri.parse("tel:" + "948337893"));
                    //here the word 'tel' is important for making a call...

                    startActivity(my_callIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Error in your phone call"+e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btUbicar:
                Intent intent = new Intent("com.dataservicios.systemauditor.UBICACION");
                startActivity(intent);
                //finish();
                break;
        }
    }

    class AttemptLogin extends AsyncTask<String, String, String> {

        /**
         * Antes de comenzar en el hilo determinado, Mostrar progresión
         * */
        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Iniciando Sesión...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Comprobando si es exito
            int success;

            int id_user ;
            try {
                // Construyendo los parametros

                TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                // get IMEI
                tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                // get IMEI
                String imei = tm.getDeviceId();
                String imei1 = tm.getLine1Number();
                // get SimSerialNumber
                String simSerialNumber = tm.getSimSerialNumber();

//                List<NameValuePair> params = new ArrayList<NameValuePair>();
//                params.add(new BasicNameValuePair("username", username));
//                params.add(new BasicNameValuePair("password", password));
//                params.add(new BasicNameValuePair("imei", simSerialNumber));

                HashMap<String, String> params = new HashMap<>();

                params.put("username", String.valueOf(username));
                params.put("password", String.valueOf(password));
                params.put("imei", String.valueOf(simSerialNumber));

                Log.d("request!", "starting");
                // getting product details by making HTTP request
                //JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/loginUser" ,"POST", params);
                JSONParserX jsonParser = new JSONParserX();

                JSONObject json = jsonParser.makeHttpRequest(GlobalConstant.dominio + "/loginMovil" ,"POST", params);

                // check your log for json response
                Log.d("Login attempt", json.toString());

                // json success, tag que retorna el json
                success = json.getInt("success");
                id_user = json.getInt("id");
                if (success == 1) {
                    Log.d("Login Successful!", json.toString());
                    db.deleteAllUser();
                    User users = new User();
                    users.setId(id_user);
                    users.setNombre(username);
                    users.setPassword(password);
                    db.createUser(users);
                    // Creating user login session
                    // For testing i am stroing name, email as follow
                    // Use user real data
                    session.createLoginSession("Jaimito el Cartero", username, String.valueOf(id_user));
                    Intent i = new Intent(LoginActivity.this, PanelAdmin.class);
                    //Enviando los datos usando Bundle a otro activity
                    Bundle bolsa = new Bundle();
                    bolsa.putString("NOMBRE", username);
                    i.putExtras(bolsa);
                    finish();
                    startActivity(i);
                    return json.getString("message");
                }else{
                    Log.d("Login Failure!", json.getString("message"));
                    return json.getString("message");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(LoginActivity.this, file_url, Toast.LENGTH_LONG).show();
            }

        }

    }
}
