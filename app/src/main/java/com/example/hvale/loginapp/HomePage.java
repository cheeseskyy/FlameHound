package com.example.hvale.loginapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HomePage extends AppCompatActivity
        implements OnMapReadyCallback, OnMyLocationButtonClickListener,
        OnMyLocationClickListener, OnRequestPermissionsResultCallback {

    private static final int MY_LOCATION_REQUEST_CODE = 99;
    private GoogleMap map;
    private FusedLocationProviderClient Client;
    private ArrayList<LatLng> latlngs = new ArrayList<>();
    private ImageView Occorrences;
    private ImageView Trending;
    private ImageView Draw;
    private FrameLayout fram_map;
    private boolean canMove = false;
    private int counter;
    private Projection projection;
    private double lat, log;
    private ArrayList<LatLng> valuesToDraw;
    private static final String url = "https://my-first-project-196314.appspot.com/rest/";
    RequestQueue requestQueue;
    JSONArray finalResponse = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Cache cache = new DiskBasedCache(getCacheDir(), 1024*1024);
        Network network = new BasicNetwork( new HurlStack());

        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
        doInBackGround();

        valuesToDraw = new ArrayList<>();
        latlngs.add(new LatLng(37.4517, -122.184));

        MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

        Client = LocationServices.getFusedLocationProviderClient(this);

        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Occorrences =  findViewById(R.id.myOccorrences) ;
        Trending =  findViewById(R.id.trending);
        Draw =  findViewById(R.id.free_Draw);

        fram_map =  findViewById(R.id.fram_map);

        fram_map.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();

                int x_co = Math.round(x);
                int y_co = Math.round(y);

                projection = map.getProjection();
                Point x_y_points = new Point(x_co, y_co);
                LatLng latLng = map.getProjection().fromScreenLocation(x_y_points);
                lat = latLng.latitude;
                log = latLng.longitude;

                int eventaction = event.getAction();
                switch (eventaction) {
                    case MotionEvent.ACTION_DOWN:
                        valuesToDraw.add(new LatLng(lat, log));
                        break;

                    case MotionEvent.ACTION_MOVE:
                        valuesToDraw.add(new LatLng(lat, log));
                        break;
                    case MotionEvent.ACTION_UP:
                        drawMap();
                        break;
                }


                return canMove;
            }
        });

        Draw.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v){
                    canMove = !canMove;
                }
        });

        Occorrences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(HomePage.this, OccurrenceListActivity.class);
                startActivity(it);
            }
        });
        Trending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(HomePage.this, OccurrenceListActivity.class);
                startActivity(it);
            }
        });

        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(HomePage.this, RegistOccurrence.class);
                startActivity(it);
            }
        });

        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        */




    }

    private void drawMap() {
        int TRANSPARENT = 0x330000FF;
        PolygonOptions rectOptions = new PolygonOptions();
       rectOptions.addAll(valuesToDraw);
       rectOptions.strokeColor(TRANSPARENT);
       rectOptions.strokeWidth(5);
       rectOptions.fillColor(TRANSPARENT);
       Polygon polygon = map.addPolygon(rectOptions);
       canMove = false;
       valuesToDraw.clear();

    }

    /*
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.minhasOcorrencias) {

        } /*else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.contacts) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
    */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(HomePage.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_LOCATION_REQUEST_CODE);
        }
        else {
            if(counter < 1) {
                initMapInDeviceCoord();
                counter++;
            }
            else {

            }
        }

        for (int i = 0; i < latlngs.size(); i++) {

            LatLng newMarker = latlngs.get(i);
            MarkerOptions initialMarkerOptions = new MarkerOptions();
            initialMarkerOptions.position(newMarker);
            initialMarkerOptions.title("Mockup Marker");
       //     map.animateCamera(CameraUpdateFactory.newLatLngZoom(newMarker, 10));
       //     map.addMarker(initialMarkerOptions); FAKE MARKER

        }
        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if(!canMove) {
                    Log.d("latLng", point.latitude + ":" + point.longitude);
                    // Creating a marker
                    MarkerOptions markerOptions = new MarkerOptions();

                    // Setting the position for the marker
                    markerOptions.position(point);

                    // Setting the title for the marker.
                    // This will be displayed on taping the marker
                    String x = point.latitude + ":" + point.longitude;
                    markerOptions.title(x);

                    // Clears the previously touched position
                    //map.clear();
                    // Animating to the touched position
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 10));

                    // Placing a marker on the touched position
                    map.addMarker(markerOptions);

                    latlngs.add(point);


                    //falta o if se foi criada uma ocorrência (confirmação via Rest adicionar o marker;

                    Intent it = new Intent(HomePage.this, RegistOccurrence.class);
                    it.putExtra("Loc", x);
                    startActivityForResult(it, 1);
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RegistOccurrence.RESULT_OK){
                String[] result = data.getStringArrayExtra("result");
            }
            if (resultCode == RegistOccurrence.RESULT_CANCELED) {
                System.out.println("erro");
                //TODO
            }
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "Going to your location", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if ((permissions.length == 1) &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                    permissions[1].equals(Manifest.permission.ACCESS_COARSE_LOCATION) &&
                    (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                initMapInDeviceCoord();
            } else {
                // Permission was denied. Display an error message.
            }
        }
    }

    protected void initMapInDeviceCoord() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(HomePage.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_LOCATION_REQUEST_CODE);
        }
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);

        Client.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
                        }
                    }
                });
    }

    private void doInBackGround() {

        final JSONObject loginInfo = LogOutSingleton.getInstance(getApplicationContext()).getSessionId();
        JSONArray ocurence = new JSONArray();
        ocurence.put(loginInfo);
        setProgressBarVisibility(true);
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.POST,url + "occurrency/getOccurrencyAndroid/all",ocurence, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                finalResponse = response;
                onPostExecute(finalResponse);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                onCancelled(error);
            }
        });

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonRequest);
        setProgressBarVisibility(false);
    }

    private void onPostExecute(JSONArray finalResponse) {

        for(int i=0; i<finalResponse.length(); i++) {
            try {
                JSONObject jsonObject = finalResponse.getJSONObject(i);
                String[] coord = jsonObject.getString("location").split(",");
                LatLng current = new LatLng(Double.parseDouble(coord[0]), Double.parseDouble(coord[1]));
                latlngs.add(current);
            } catch (JSONException e) {
                onCancelled(e);
            }
        }
    }

    private void onCancelled(Exception e) {

         if (e instanceof ParseError) {
            System.out.println("Erro sv");
        }
    }

}
