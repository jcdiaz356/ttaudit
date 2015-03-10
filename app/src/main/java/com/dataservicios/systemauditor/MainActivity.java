package com.dataservicios.systemauditor;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ProgressBar;
public class MainActivity extends Activity {
    private int splashTime = 3000;
    private Thread thread;
    private ProgressBar mSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSpinner = (ProgressBar) findViewById(R.id.Splash_ProgressBar);
        mSpinner.setIndeterminate(true);
        thread = new Thread(runable);
        thread.start();
    }

    public Runnable runable = new Runnable() {
        public void run() {
            try {
                Thread.sleep(splashTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                //startActivity(new Intent(MainActivity.this,LoginActivity.class));
                //Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                Intent intent = new Intent("com.dataservicios.systemauditor.LOGIN");

                startActivity(intent);
                finish();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    };


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
