package com.dataservicios.systemauditor;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by usuario on 28/12/2014.
 */
public class Ubicacion extends FragmentActivity {
    private GoogleMap map;
    private static final LatLng MELBOURNE = new LatLng(-12.04513488,-77.02851775) ;
    private static final LatLng MELBOURNE2 = new LatLng(-11.917178, -77.041926);
    private static final LatLng MELBOURNE3 = new LatLng(-11.922590,-77.044282);
    private LatLngBounds AUSTRALIA;


    private LocationManager locManager;
    private LocationListener locListener;
    private double latitude ;
    private double longitude ;
    private Marker MarkerNow;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setWindowAnimations(2);
        setContentView(R.layout.ubicacion);

        //Comenzando la localización
        comenzarLocalizacion();

        map =((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        //Añade el marker al mapa
//        Marker melbourne = map.addMarker(new MarkerOptions()
//                .position(MELBOURNE)
//                .title("Melbourne")
//                .snippet("Population: 4,137,400")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));

        //Añade el marker al mapa
//        map.addMarker(new MarkerOptions()
//                .position(MELBOURNE2)
//                .title("Mi primer Market"));

//        Añade el marker al mapa
         MarkerNow = map.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("Melbourne")
                .snippet("Population: 4,137,400")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));

//        Mostrando información del Market ni bien carga el mapa
        MarkerNow.showInfoWindow();
//        Ajuste de la cámara en el mayor nivel de zoom es posible que incluya los límites
        //AUSTRALIA = new LatLngBounds(MELBOURNE, MELBOURNE3);
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
        map.setMyLocationEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(15).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(AUSTRALIA.getCenter(), 10));
    }

    private void comenzarLocalizacion()
    {
        //Obtenemos una referencia al LocationManager
        locManager =  (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        //Obtenemos la última posición conocida
        Location loc =   locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //Mostramos la última posición conocida
        mostrarPosicion(loc);

        //Nos registramos para recibir actualizaciones de la posición
        locListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                mostrarPosicion(location);
                if( MarkerNow != null){
                    MarkerNow.remove();
                }
                // Creating a LatLng object for the current location
                LatLng latLng = new LatLng(latitude, longitude);
                MarkerNow = map.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title("Melbourne")
                        .snippet("Population: 4,137,400")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_map)));
                // Showing the current location in Google Map
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                // Zoom in the Google Map
                map.animateCamera(CameraUpdateFactory.zoomTo(15));
                Log.i("Prueba", "Mostrando Posición " );
            }
            public void onProviderDisabled(String provider){
                //lblEstado.setText("Provider OFF");
                 Log.i("Prueba", "Provider OFF");
            }
            public void onProviderEnabled(String provider){
                //lblEstado.setText("Provider ON ");
                Log.i("Prueba", "Provider OFF");
            }
            public void onStatusChanged(String provider, int status, Bundle extras){
                Log.i("Prueba", "Provider Status: " + status);

                //lblEstado.setText("Provider Status: " + status);
            }
        };
        locManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 30000, 0, locListener);
    }


    private void mostrarPosicion(Location loc) {
        if(loc != null)
        {
            //lblLatitud.setText("Latitud: " + String.valueOf(loc.getLatitude()));
            //lblLongitud.setText("Longitud: " + String.valueOf(loc.getLongitude()));
            //lblPrecision.setText("Precision: " + String.valueOf(loc.getAccuracy()));
            latitude=loc.getLatitude();
            longitude= loc.getLongitude();

            Log.i("Posicion: ", String.valueOf(latitude + " - " + String.valueOf(longitude) + " - "));
        }
        else
        {
//            lblLatitud.setText("Latitud: (sin_datos)");
//            lblLongitud.setText("Longitud: (sin_datos)");
//            lblPrecision.setText("Precision: (sin_datos)");
            Log.i("SIN Data","No hay datos para mostrar");
        }
    }










    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ubicacion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_change_password) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
