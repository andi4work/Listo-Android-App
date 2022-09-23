package com.listoit.location.ui.fragment;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.listoit.location.R;
import com.listoit.location.adapters.CustomGridAdapter;
import com.listoit.location.app.MainApplication;
import com.listoit.location.helpers.Constants;
import com.listoit.location.helpers.GPSTracker;
import com.listoit.location.helpers.InternetDetector;
import com.listoit.location.model.GooglePlacesAPIResponse;
import com.listoit.location.model.GooglePlacesResponseResults;
import com.listoit.location.ui.activity.DetailScrollingActivity;
import com.listoit.location.ui.activity.NearByActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.SEARCH_SERVICE;
import static android.graphics.Paint.ANTI_ALIAS_FLAG;


/**
 * Created by ${ChandraMohanReddy} on 1/12/2017.
 */
public class FragmentNearBy extends Fragment implements View.OnClickListener {
    private AdView mAdView;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 20;
    String[] web = null;
    int[] imageId;
    TextView dateOfBirth;
    RelativeLayout rl;
    CustomGridAdapter adapter;
    GridView grid;
    String type;
    List<String> list;
    ArrayList<HashMap<String, String>> contactList;
    ProgressBar progressbar;
    CardView header;
    LinearLayout people, professional, service, property, business;
    ImageView iconPoeple, iconProfessional, iconService, iconProperty, iconBusiness;
    TextView titlePeople, titleProfessional, titleService, titleProperty, titleBusiness;
    private List<GooglePlacesResponseResults> result = new ArrayList<>();
    String place_id;
    String currentLocationCoordinates;
    Typeface face, faceThin;
    Snackbar snackbar;
    RelativeLayout relativeLayout;
    private int visibleThreshold = 2;
    private boolean isLoading = true;
    InternetDetector id;
    Context mContext;
    SearchView searchView;
    TextView titleText;
    String title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_near_by, container, false);
        titleText = (TextView) rootView.findViewById(R.id.tvTitleText);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Bold.ttf");
        titleText.setTypeface(face);
        title = titleText.getText().toString();

        // set the ad unit ID
        mAdView = (AdView) rootView.findViewById(R.id.adView);

        // Create an ad request. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest1 = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest1);


        face = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/RobotoCondensed-Bold.ttf");

        progressbar = (ProgressBar) rootView.findViewById(R.id.progressbar);
        header = (CardView) rootView.findViewById(R.id.header_include);
        people = (LinearLayout) header.findViewById(R.id.llPeople);
        people.setOnClickListener(this);
        professional = (LinearLayout) header.findViewById(R.id.llprofessional);
        professional.setOnClickListener(this);
        service = (LinearLayout) header.findViewById(R.id.llService);
        service.setOnClickListener(this);
        property = (LinearLayout) header.findViewById(R.id.llProperty);
        property.setOnClickListener(this);
        business = (LinearLayout) header.findViewById(R.id.llBusiness);
        business.setOnClickListener(this);
        id = new InternetDetector(getActivity());

        iconPoeple = (ImageView) header.findViewById(R.id.iconPeople);
        iconPoeple.setImageResource(R.drawable.people_icon_selected);
        iconProfessional = (ImageView) header.findViewById(R.id.iconProfessional);
        iconProfessional.setImageResource(R.drawable.professional);
        iconService = (ImageView) header.findViewById(R.id.iconService);
        iconService.setImageResource(R.drawable.service_icon);

        iconProperty = (ImageView) header.findViewById(R.id.iconProperty);
        iconProperty.setImageResource(R.drawable.property_icon);
        iconBusiness = (ImageView) header.findViewById(R.id.iconBusiness);
        iconBusiness.setImageResource(R.drawable.business_icon);
        titlePeople = (TextView) header.findViewById(R.id.titlePeople);
        titleProfessional = (TextView) header.findViewById(R.id.titleProfessional);
        titleService = (TextView) header.findViewById(R.id.titleService);
        titleProperty = (TextView) header.findViewById(R.id.titleProperty);
        titleBusiness = (TextView) header.findViewById(R.id.titleBusiness);
        rl = (RelativeLayout) rootView.findViewById(R.id.rl);
        grid = (GridView) rootView.findViewById(R.id.gvNearBy);

        adapter = new CustomGridAdapter(getActivity(), result);
        grid.setAdapter(adapter);
        grid.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                int threshold = 1;
                int count = grid.getCount();

                if (isLoading && scrollState == SCROLL_STATE_IDLE) {
                    if (grid.getLastVisiblePosition() >= count
                            - threshold) {

                        if (!TextUtils.isEmpty(Constants.NEXT_PAGE_TOKEN)) {
                            googlePlacesService("", "", Constants.NEXT_PAGE_TOKEN);
                        } else {
                            Toast.makeText(getActivity(), "No more records", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });
        list = new ArrayList<String>();
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            googlePlacesService("restaurant", "", "");
        } else {
            showGPSDisabledAlertToUser();
        }


        return rootView;
    }


    private void googlePlacesService(String type, String keyword, String nextPageToken) {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }

        GPSTracker latlon = new GPSTracker(getActivity());
        String lat = String.valueOf(latlon.getLatitude());
        String lon = String.valueOf(latlon.getLongitude());
        //  currentLocationCoordinates = "12.971599,77.594563";
        currentLocationCoordinates = lat + "," + lon;
        String rankby = "distance";
        String key = Constants.API_KEY;
        if (id.isConnectingToInternet() && id.isOnline()) {
            progressbar.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getActivity(), "Check internet connection", Toast.LENGTH_LONG).show();
        }

        Call<GooglePlacesAPIResponse<List<GooglePlacesResponseResults>>> googlePlaces = MainApplication.getRestClient().getApiService().googlePlaces(currentLocationCoordinates, rankby, type, keyword, key, nextPageToken);
        googlePlaces.enqueue(new Callback<GooglePlacesAPIResponse<List<GooglePlacesResponseResults>>>() {
            @Override
            public void onResponse(Call<GooglePlacesAPIResponse<List<GooglePlacesResponseResults>>> call, final Response<GooglePlacesAPIResponse<List<GooglePlacesResponseResults>>> response) {

                if (response.code() == 200 && response.body().getResults().size() > 0 && response.body().getResults() != null) {

                    progressbar.setVisibility(View.GONE);
                    String nextPageToken = response.body().getNext_page_token();
                    Constants.NEXT_PAGE_TOKEN = nextPageToken;
                    adapter.refresh(response.body().getResults());

                    isLoading = true;
                    grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {

                            result = adapter.getResults();

                            Intent intent = new Intent(getContext(), DetailScrollingActivity.class);
                            if (result.get(position).getName() != null) {
                                intent.putExtra("title", result.get(position).getName());
                            }
                            if (result.get(position).getPlace_id() != null) {
                                intent.putExtra("place_id", result.get(position).getPlace_id());
                            }
                            if (result.get(position).getVicinity() != null) {
                                intent.putExtra("address", result.get(position).getVicinity());
                            }
                            if (result.get(position).getRating() != null)
                                intent.putExtra("rating", result.get(position).getRating());
                            intent.putExtra("current_lat_lon", currentLocationCoordinates);

                            if (result.get(position).getPhotos() != null && result.get(position).getPhotos().get(0).getPhotoReference() != null) {
                                intent.putExtra("photo_reference", result.get(position).getPhotos().get(0).getPhotoReference());
                            }
                            Double lat = result.get(position).getGeometry().getLocation().getLat();
                            Double lon = result.get(position).getGeometry().getLocation().getLng();

                            Location currentLocation = new Location("CurrentLocation");
                            Location apiLocation = new Location("apiLocation");

                            GPSTracker latlon = new GPSTracker(getActivity());
                            Double lat1 = latlon.getLatitude();
                            Double lon1 = latlon.getLongitude();
                            currentLocation.setLatitude(lat1);
                            currentLocation.setLongitude(lon1);
                            apiLocation.setLatitude(lat);
                            apiLocation.setLongitude(lon);

                            float distanceD = apiLocation.distanceTo(currentLocation) / 1000;
                            String s = String.valueOf(distanceD).substring(0, 4);
                            if (s != null) {
                                intent.putExtra("distance", s + " Kms");
                            }
                            if (lat != null) {
                                intent.putExtra("api_location_lat", lat);
                            }
                            if (lon != null) {
                                intent.putExtra("api_location_long", lon);
                            }
                            if (lat1 != null) {
                                intent.putExtra("current_location_lat", lat1);
                            }
                            if (lon1 != null) {
                                intent.putExtra("current_location_long", lon1);
                            }

                            startActivity(intent);
                        }
                    });
                } else {

                    progressbar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
                    isLoading = false;

                }
            }

            @Override
            public void onFailure(Call<GooglePlacesAPIResponse<List<GooglePlacesResponseResults>>> call, Throwable t) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llPeople:
                result.clear();
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "People");
                adapter = new CustomGridAdapter(getActivity(), result);
                grid.setAdapter(adapter);
                iconProfessional.setImageResource(R.drawable.professional);
                titleProfessional.setTextColor(getResources().getColor(R.color.text_unselected));
                iconPoeple.setImageResource(R.drawable.people_icon_selected);
                titlePeople.setTextColor(getResources().getColor(R.color.logoOrange));
                iconService.setImageResource(R.drawable.service_icon);
                titleService.setTextColor(getResources().getColor(R.color.text_unselected));
                iconProperty.setImageResource(R.drawable.property_icon);
                titleProperty.setTextColor(getResources().getColor(R.color.text_unselected));
                iconBusiness.setImageResource(R.drawable.business_icon);
                titleBusiness.setTextColor(getResources().getColor(R.color.text_unselected));
                googlePlacesService("restaurant", "", "");

                break;
            case R.id.llprofessional:

                AlertDialog.Builder professional = new AlertDialog.Builder(getActivity());
                professional.setTitle("Professionals");
                professional.setIcon(R.drawable.professional);
                professional.setItems(Constants.professionals, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int professional) {
                        adapter = new CustomGridAdapter(getActivity(), result);
                        grid.setAdapter(adapter);
                        titleProfessional.setTextColor(getResources().getColor(R.color.logoOrange));
                        iconPoeple.setImageResource(R.drawable.people_icon);
                        iconProfessional.setImageResource(R.drawable.professional_selected);
                        titlePeople.setTextColor(getResources().getColor(R.color.text_unselected));
                        iconService.setImageResource(R.drawable.service_icon);
                        titleService.setTextColor(getResources().getColor(R.color.text_unselected));
                        iconProperty.setImageResource(R.drawable.property_icon);
                        titleProperty.setTextColor(getResources().getColor(R.color.text_unselected));
                        iconBusiness.setImageResource(R.drawable.business_icon);
                        titleBusiness.setTextColor(getResources().getColor(R.color.text_unselected));
                        result.clear();
                        switch (professional) {
                            case 0:
                                type = "car_dealer";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Car Dealer");
                                googlePlacesService(type, "", "");
                                break;
                            case 1:
                                type = "dentist";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Dentist");
                                googlePlacesService(type, "", "");
                                break;
                            case 2:
                                type = "doctor";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Doctor");
                                googlePlacesService(type, "", "");
                                break;
                            case 3:
                                type = "lawyer";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Lawyer");
                                googlePlacesService(type, "", "");
                                break;
                            case 4:
                                type = "physiotherapist";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Physiotherapist");
                                googlePlacesService(type, "", "");
                                break;
                        }
                    }
                });
                AlertDialog proffessionals = professional.create();
                proffessionals.show();
                break;
            case R.id.llService:
                grid.setAdapter(adapter);
                AlertDialog.Builder service = new AlertDialog.Builder(getActivity());
                service.setTitle("Services");
                service.setIcon(R.drawable.service_icon);
                service.setItems(Constants.services, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int service) {
                        adapter = new CustomGridAdapter(getActivity(), result);
                        grid.setAdapter(adapter);
                        iconService.setImageResource(R.drawable.service_selected);
                        titleService.setTextColor(getResources().getColor(R.color.logoOrange));
                        iconProfessional.setImageResource(R.drawable.professional);
                        titleProfessional.setTextColor(getResources().getColor(R.color.text_unselected));
                        iconPoeple.setImageResource(R.drawable.people_icon);
                        titlePeople.setTextColor(getResources().getColor(R.color.text_unselected));
                        iconProperty.setImageResource(R.drawable.property_icon);
                        titleProperty.setTextColor(getResources().getColor(R.color.text_unselected));
                        iconBusiness.setImageResource(R.drawable.business_icon);
                        titleBusiness.setTextColor(getResources().getColor(R.color.text_unselected));

                        result.clear();
                        switch (service) {
                            case 0:
                                type = "atm";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "ATM");
                                googlePlacesService(type, "", "");
                                break;
                            case 1:
                                type = "bank";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Bank");
                                googlePlacesService(type, "", "");
                                break;
                            case 2:
                                type = "bowling_alley";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Bowling Alley");
                                googlePlacesService(type, "", "");
                                break;
                            case 3:
                                type = "bus_station";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Bus Station");
                                googlePlacesService(type, "", "");
                                break;
                            case 4:
                                type = "car_rental";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Car Rental");
                                googlePlacesService(type, "", "");
                                break;
                            case 5:
                                type = "car_repair";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Car Repair");
                                googlePlacesService(type, "", "");
                                break;
                            case 6:
                                type = "car_wash";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Car Wash");
                                googlePlacesService(type, "", "");
                                break;
                            case 7:
                                type = "electrician";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Electrician");
                                googlePlacesService(type, "", "");
                                break;
                            case 8:
                                type = "gas_station";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Gas Station");
                                googlePlacesService(type, "", "");
                                break;
                            case 9:
                                type = "library";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Library");
                                googlePlacesService(type, "", "");
                                break;
                            case 10:
                                type = "movie_theater";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Movie Theater");
                                googlePlacesService(type, "", "");
                                break;
                            case 11:
                                type = "night_club";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Night Club");
                                googlePlacesService(type, "", "");
                                break;
                            case 12:
                                type = "painter";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Painter");
                                googlePlacesService(type, "", "");
                                break;
                            case 13:
                                type = "parking";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Parking");
                                googlePlacesService(type, "", "");
                                break;
                            case 14:
                                type = "plumber";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Plumber");
                                googlePlacesService(type, "", "");
                                break;
                            case 15:
                                type = "police";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Police");
                                googlePlacesService(type, "", "");
                                break;
                            case 16:
                                type = "post_office";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Post Office");
                                googlePlacesService(type, "", "");
                                break;
                            case 17:
                                type = "spa";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Spa");
                                googlePlacesService(type, "", "");
                                break;
                            case 18:
                                type = "veterinary_care";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Veterinary Care");
                                googlePlacesService(type, "", "");
                                break;
                        }
                    }
                });
                AlertDialog services = service.create();
                services.show();
                break;
            case R.id.llProperty:

                grid.setAdapter(adapter);
                AlertDialog.Builder property = new AlertDialog.Builder(getActivity());
                property.setTitle("Properties");
                property.setIcon(R.drawable.property_icon);
                property.setItems(Constants.property, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int property) {
                        adapter = new CustomGridAdapter(getActivity(), result);
                        grid.setAdapter(adapter);
                        iconProfessional.setImageResource(R.drawable.professional);
                        titleProfessional.setTextColor(getResources().getColor(R.color.text_unselected));
                        iconPoeple.setImageResource(R.drawable.people_icon);
                        titlePeople.setTextColor(getResources().getColor(R.color.text_unselected));
                        iconService.setImageResource(R.drawable.service_icon);
                        titleService.setTextColor(getResources().getColor(R.color.text_unselected));
                        iconProperty.setImageResource(R.drawable.property_selected_icon);
                        titleProperty.setTextColor(getResources().getColor(R.color.logoOrange));
                        iconBusiness.setImageResource(R.drawable.business_icon);
                        titleBusiness.setTextColor(getResources().getColor(R.color.text_unselected));

                        result.clear();
                        switch (property) {
                            case 0:
                                type = "park";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Park");
                                googlePlacesService(type, "", "");
                                break;
                            case 1:
                                type = "real_estate_agency";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Real Estate Agency");
                                googlePlacesService(type, "", "");
                                break;
                            case 2:
                                type = "stadium";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Stadiium");
                                googlePlacesService(type, "", "");
                                break;
                        }
                    }
                });
                AlertDialog properties = property.create();
                properties.show();
                break;
            case R.id.llBusiness:

                grid.setAdapter(adapter);
                AlertDialog.Builder business = new AlertDialog.Builder(getActivity());
                business.setTitle("Business");
                business.setIcon(R.drawable.business_icon);
                business.setItems(Constants.business, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int business) {
                        adapter = new CustomGridAdapter(getActivity(), result);
                        grid.setAdapter(adapter);
                        iconProfessional.setImageResource(R.drawable.professional);
                        titleProfessional.setTextColor(getResources().getColor(R.color.text_unselected));
                        iconPoeple.setImageResource(R.drawable.people_icon);
                        titlePeople.setTextColor(getResources().getColor(R.color.text_unselected));
                        iconService.setImageResource(R.drawable.service_icon);
                        titleService.setTextColor(getResources().getColor(R.color.text_unselected));
                        iconProperty.setImageResource(R.drawable.property_icon);
                        titleProperty.setTextColor(getResources().getColor(R.color.text_unselected));
                        iconBusiness.setImageResource(R.drawable.business_icon_selected);
                        titleBusiness.setTextColor(getResources().getColor(R.color.logoOrange));

                        result.clear();
                        switch (business) {
                            case 0:
                                type = "bakery";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Bakery");
                                googlePlacesService(type, "", "");
                                break;
                            case 1:
                                type = "bar";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Bar");
                                googlePlacesService(type, "", "");
                                break;
                            case 2:
                                type = "beauty_salon";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Beauty Salon");
                                googlePlacesService(type, "", "");
                                break;
                            case 3:
                                type = "book_store";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Book Store");
                                googlePlacesService(type, "", "");
                                break;
                            case 4:
                                type = "cafe";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Cafe");
                                googlePlacesService(type, "", "");
                                break;
                            case 5:
                                type = "clothing_store";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Clothing Store");
                                googlePlacesService(type, "", "");
                                break;
                            case 6:
                                type = "convenience_store";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Convenience Store");
                                googlePlacesService(type, "", "");
                                break;
                            case 7:
                                type = "department_store";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Department Store");
                                googlePlacesService(type, "", "");
                                break;
                            case 8:
                                type = "electronics_store";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Electronics Store");
                                googlePlacesService(type, "", "");
                                break;
                            case 9:
                                type = "furniture_store";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Furniture Store");
                                googlePlacesService(type, "", "");
                                break;
                            case 10:
                                type = "florist";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Florist");
                                googlePlacesService(type, "", "");
                                break;
                            case 11:
                                type = "gym";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Gym");
                                googlePlacesService(type, "", "");
                                break;
                            case 12:
                                type = "hair_care";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Hair Care");
                                googlePlacesService(type, "", "");
                                break;
                            case 13:
                                type = "hardware_store";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Hardware Store");
                                googlePlacesService(type, "", "");
                                break;
                            case 14:
                                type = "home_goods_store";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Home Goods Store");
                                googlePlacesService(type, "", "");
                                break;
                            case 15:
                                type = "hospital";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Hospital");
                                googlePlacesService(type, "", "");
                                break;
                            case 16:
                                type = "insurance_agency";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Insurance Agency");
                                googlePlacesService(type, "", "");
                                break;
                            case 17:
                                type = "jewelry_store";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Jewelry Store");
                                googlePlacesService(type, "", "");
                                break;
                            case 18:
                                type = "laundry";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Laundry");
                                googlePlacesService(type, "", "");
                                break;
                            case 19:
                                type = "liquor_store";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Liquor Store");
                                googlePlacesService(type, "", "");
                                break;
                            case 20:
                                type = "lodging";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Lodging");
                                googlePlacesService(type, "", "");
                                break;
                            case 21:
                                type = "pet_store";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Pet Store");
                                googlePlacesService(type, "", "");
                                break;
                            case 22:
                                type = "pharmacy";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Pharmacy");
                                googlePlacesService(type, "", "");
                                break;
                            case 23:
                                type = "restaurant";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Restaurant");
                                googlePlacesService(type, "", "");
                                break;
                            case 24:
                                type = "school";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "School");
                                googlePlacesService(type, "", "");
                                break;
                            case 25:
                                type = "shoe_store";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Choe Store");
                                googlePlacesService(type, "", "");
                                break;
                            case 26:
                                type = "shopping_mall";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Shopping Mall");
                                googlePlacesService(type, "", "");
                                break;
                            case 27:
                                type = "travel_agency";
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "Travel Agency");
                                googlePlacesService(type, "", "");
                                break;
                        }
                    }
                });
                AlertDialog busines = business.create();
                busines.show();
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if (!queryTextFocused) {
                   /* MenuItemCompat.collapseActionView(menu.findItem(R.id.action_search));
                    searchView.setQuery("", false);*/
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String searchQuery) {
                iconPoeple.setImageResource(R.drawable.people_icon);
                titlePeople.setTextColor(getResources().getColor(R.color.text_unselected));
                iconProfessional.setImageResource(R.drawable.professional);
                titleProfessional.setTextColor(getResources().getColor(R.color.text_unselected));
                iconService.setImageResource(R.drawable.service_icon);
                titleService.setTextColor(getResources().getColor(R.color.text_unselected));
                iconProperty.setImageResource(R.drawable.property_icon);
                titleProperty.setTextColor(getResources().getColor(R.color.text_unselected));
                iconBusiness.setImageResource(R.drawable.business_icon);
                titleBusiness.setTextColor(getResources().getColor(R.color.text_unselected));

                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title + "" + searchQuery);
                result.clear();
                adapter.refresh(result);
                googlePlacesService("", searchQuery, "");
                searchView.onActionViewCollapsed();
                MenuItemCompat.collapseActionView(menu.findItem(R.id.action_search));

                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //REMOVE THIS to avoid gridview Filter
                adapter.getFilter().filter(s);
                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:


                break;
        }
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("LOGIN", MODE_PRIVATE).edit();
            editor.putInt("success", 0);
            editor.commit();
            startActivity(new Intent(getActivity(), NearByActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    //SET TEXT TO FLOATING ACTION BUTTON

    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    @Override
    public void onResume() {
        super.onResume();
        ((NearByActivity) getActivity()).getSupportActionBar().setTitle(title + "People");
    }
}
