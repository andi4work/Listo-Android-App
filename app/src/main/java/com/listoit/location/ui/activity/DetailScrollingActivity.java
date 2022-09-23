package com.listoit.location.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.listoit.location.R;
import com.listoit.location.helpers.Constants;
import com.listoit.location.helpers.JSONParser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.listoit.location.R.id.map;

public class DetailScrollingActivity extends AppCompatActivity implements OnMapReadyCallback {
    TextView address, distnace;
    String title, ratingS, photo, place_id, addressS, distanceS, currentLocationCoordinates, apiLocationCoordinates;

    Double lat, lon, latCL, lonCL;
    Context mContext;
    ImageView backgroundImage;
    TextView rating;
    RatingBar ratingB;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrolling_activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        address = (TextView) findViewById(R.id.tvAddress);
        distnace = (TextView) findViewById(R.id.tvDistancee);
        rating = (TextView) findViewById(R.id.tvRating);
        ratingB = (RatingBar) findViewById(R.id.ratingbar);
        ratingB.setStepSize((float) 0.1);

        backgroundImage = (ImageView) findViewById(R.id.ivPLaceLocation);
        backgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DetailsImageActivity.class);
                int[] screenLocation = new int[2];
                backgroundImage.getLocationOnScreen(screenLocation);

                //Pass the image title and url to DetailsImageActivity
                intent.putExtra("left", screenLocation[0]).
                        putExtra("top", screenLocation[1]).
                        putExtra("width", backgroundImage.getWidth()).
                        putExtra("height", backgroundImage.getHeight()).
                        putExtra("image", "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1400&key=" + Constants.API_KEY + "&photoreference=" + photo);
                //Start details activity
                startActivity(intent);
            }
        });
        title = getIntent().getStringExtra("title");
        place_id = getIntent().getStringExtra("place_id");
        ratingS = getIntent().getStringExtra("rating");
        DetailScrollingActivity.this.setTitle(title);
        addressS = getIntent().getStringExtra("address");
        distanceS = getIntent().getStringExtra("distance");
        distnace.setText(distanceS);
        photo = getIntent().getStringExtra("photo_reference");
        lat = getIntent().getDoubleExtra("api_location_lat", 12.9716);
        lon = getIntent().getDoubleExtra("api_location_long", 77.5946);
        latCL = getIntent().getDoubleExtra("current_location_lat", 12.9716);
        lonCL = getIntent().getDoubleExtra("current_location_long", 77.5946);
        makeURL(latCL, lonCL, lat, lon);
        currentLocationCoordinates = latCL + "," + lonCL;

        apiLocationCoordinates = lat + "," + lon;

        rating.setText(ratingS);
        if (ratingS != null) {
            ratingB.setRating(Float.parseFloat(ratingS));
        } else {
            ratingB.setRating(0);
        }
        if (photo != null) {
            Picasso.with(mContext).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=600&key=" + Constants.API_KEY + "&photoreference=" + photo).into(backgroundImage);
        } else {
            backgroundImage.setImageResource(R.drawable.no_preview);
        }
        address.setText(addressS);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + currentLocationCoordinates + "+&daddr=" + apiLocationCoordinates));
                startActivity(intent);
              /*  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            case android.R.id.home:
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
                finish(); // close this activity and return to preview activity (if there is any)


        }

        return super.onOptionsItemSelected(item);
    }

    //DRAW POLYLINE ON MAP >>>>>START
    public String makeURL(double sourcelat, double sourcelog, double destlat, double destlog) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=" + Constants.API_KEY);
        new connectAsyncTask(urlString.toString()).execute();
        return urlString.toString();
    }

    public void drawPath(String result) {
        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            PolylineOptions options = new PolylineOptions().width(16).color(Color.BLUE).geodesic(true);
            for (int z = 0; z < list.size(); z++) {
                LatLng point = list.get(z);
                options.add(point);
            }
            Polyline line = mMap.addPolyline(options);

        } catch (JSONException e) {

        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private class connectAsyncTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        String url;

        connectAsyncTask(String urlPass) {
            url = urlPass;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(DetailScrollingActivity.this);
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            String json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            if (result != null) {
                drawPath(result);
            }
        }
    }

    //DRAW POLYLINE ON MAP >>>>>>END
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng google_api_lat_lon = new LatLng(lat, lon);
        LatLng current_lat_lon = new LatLng(latCL, lonCL);
        float zoomLevel = 16.0f;

        Marker markerAPILocation = mMap.addMarker(new MarkerOptions().position(google_api_lat_lon).title(title).icon(BitmapDescriptorFactory.fromResource(R.drawable.saved_locations)));
        markerAPILocation.showInfoWindow();
        mMap.setMyLocationEnabled(true);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(google_api_lat_lon);
        builder.include(current_lat_lon);
        final LatLngBounds bounds = builder.build();

        // mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(google_api_lat_lon, zoomLevel));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
            }
        });

    }

}
