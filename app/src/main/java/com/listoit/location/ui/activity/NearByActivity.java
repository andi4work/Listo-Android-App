package com.listoit.location.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;
import com.listoit.location.R;
import com.listoit.location.helpers.ConnectivityReceiver;
import com.listoit.location.ui.fragment.FragmentNearBy;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by ${ChandraMohanReddy} on 1/12/2017.
 */

public class NearByActivity extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ViewPagerAdapter adapter;
    LinearLayout footer;
    ActionBarDrawerToggle toggle;
    ImageView emergencyIV, eatenIV, commuteIV, frtIV, cargoIV, gatedCommIV;
    LinearLayout emergency, eaten, commute, frt, cargo, gatedComm;
    TextView emergencyTV, eatenTV, commuteTV, frtTV, cargoTV, gatedCommTV;
    TextView logoutTV;
    ImageView location, editProfile;
    protected DrawerLayout mDrawerLayout;
    TextView legal, aboutListo, settings, logout;
    TextView tvProgressOne, tvProgressTwo, tvProgressThree, tvProgressFour;
    // Show if locationStatus = 1
    int locationStatus = 1;
    InterstitialAd mInterstitialAd;
    Toolbar toolbar;
    TextView title, userName, mobileNo;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;
    Uri outPutfileUri;
    CircleImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        title = new TextView(getApplicationContext());
        title.setText("<b>Nearby - </b>");
     /*   mInterstitialAd = new InterstitialAd(this);

        mInterstitialAd.setAdUnitId(getString(R.string.FULL_SCREEN_AD_UNIT_ID));

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);

        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });
*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //  toolbar.setTitle(Html.fromHtml(title.getText().toString() + "People"));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

/*
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                SharedPreferences prefs = getSharedPreferences("LISTO", MODE_PRIVATE);
                String name = prefs.getString("profile_name", "");
                String dob = prefs.getString("profile_date_of_birth", "");
                String address = prefs.getString("profile_address", "");
                String contactNo = prefs.getString("profile_emergency_contact_number", "");

                if (name.length() > 0) {
                    userName.setText(name);
                    tvProgressOne.setBackgroundColor(getResources().getColor(R.color.profile_completed));
                }
                if (dob.length() > 0) {
                    tvProgressTwo.setBackgroundColor(getResources().getColor(R.color.profile_completed));
                }
                if (address.length() > 0) {
                    tvProgressThree.setBackgroundColor(getResources().getColor(R.color.profile_completed));
                }
                if (contactNo.length() > 0) {
                    mobileNo.setText(contactNo);
                    tvProgressFour.setBackgroundColor(getResources().getColor(R.color.profile_completed));
                }
                String encodedImage = prefs.getString("camera_image_data", "");

                if (!encodedImage.equalsIgnoreCase("")) {
                    byte[] b = Base64.decode(encodedImage, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                    profilePic.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }


            @Override
            public void onDrawerStateChanged(int newState) {
                SharedPreferences prefs = getSharedPreferences("LISTO", MODE_PRIVATE);
                String name = prefs.getString("profile_name", "");
                String dob = prefs.getString("profile_date_of_birth", "");
                String address = prefs.getString("profile_address", "");
                String contactNo = prefs.getString("profile_emergency_contact_number", "");

                if (name.length() > 0) {
                    userName.setText(name);
                    tvProgressOne.setBackgroundColor(getResources().getColor(R.color.profile_completed));
                }
                if (dob.length() > 0) {
                    tvProgressTwo.setBackgroundColor(getResources().getColor(R.color.profile_completed));
                }
                if (address.length() > 0) {
                    tvProgressThree.setBackgroundColor(getResources().getColor(R.color.profile_completed));
                }
                if (contactNo.length() > 0) {
                    mobileNo.setText(contactNo);
                    tvProgressFour.setBackgroundColor(getResources().getColor(R.color.profile_completed));
                }
                String encodedImage = prefs.getString("camera_image_data", "");

                if (!encodedImage.equalsIgnoreCase("")) {
                    byte[] b = Base64.decode(encodedImage, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                    profilePic.setImageBitmap(bitmap);
                }
            }
        });
*/

       /* toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                SharedPreferences prefs = getSharedPreferences("LISTO", MODE_PRIVATE);
                String name = prefs.getString("profile_name", "");
                String dob = prefs.getString("profile_date_of_birth", "");
                String address = prefs.getString("profile_address", "");
                String contactNo = prefs.getString("profile_emergency_contact_number", "");

                if (name.length() > 0) {
                    userName.setText(name);
                    tvProgressOne.setBackgroundColor(getResources().getColor(R.color.profile_completed));
                }
                if (dob.length() > 0) {
                    tvProgressTwo.setBackgroundColor(getResources().getColor(R.color.profile_completed));
                }
                if (address.length() > 0) {
                    tvProgressThree.setBackgroundColor(getResources().getColor(R.color.profile_completed));
                }
                if (contactNo.length() > 0) {
                    mobileNo.setText(contactNo);
                    tvProgressFour.setBackgroundColor(getResources().getColor(R.color.profile_completed));
                }
                String encodedImage = prefs.getString("camera_image_data", "");

                if (!encodedImage.equalsIgnoreCase("")) {
                    byte[] b = Base64.decode(encodedImage, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                    profilePic.setImageBitmap(bitmap);
                }
            }
        };
        toggle.syncState();
*/

      /*  NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.inflateHeaderView(R.layout.navigation_header);
        location = (ImageView) hView.findViewById(R.id.ivLocation);
        userName = (TextView) hView.findViewById(R.id.tvUserName);
        mobileNo = (TextView) hView.findViewById(R.id.tvUserMobileNumber);
        profilePic = (CircleImageView) hView.findViewById(R.id.ivProfilePic);
        tvProgressOne = (TextView) hView.findViewById(R.id.tvProgressOne);
        tvProgressTwo = (TextView) hView.findViewById(R.id.tvProgressTwo);
        tvProgressThree = (TextView) hView.findViewById(R.id.tvProgressThird);
        tvProgressFour = (TextView) hView.findViewById(R.id.tvProgressForth);

        SharedPreferences prefs = getSharedPreferences("LISTO", MODE_PRIVATE);
        String name = prefs.getString("profile_name", "");
        String dob = prefs.getString("profile_date_of_birth", "");
        String address = prefs.getString("profile_address", "");
        String contactNo = prefs.getString("profile_emergency_contact_number", "");

        if (name.length() > 0) {
            userName.setText(name);
            tvProgressOne.setBackgroundColor(getResources().getColor(R.color.profile_completed));
        }
        if (dob.length() > 0) {
            tvProgressTwo.setBackgroundColor(getResources().getColor(R.color.profile_completed));
        }
        if (address.length() > 0) {
            tvProgressThree.setBackgroundColor(getResources().getColor(R.color.profile_completed));
        }
        if (contactNo.length() > 0) {
            mobileNo.setText(contactNo);
            tvProgressFour.setBackgroundColor(getResources().getColor(R.color.profile_completed));
        }
        String encodedImage = prefs.getString("camera_image_data", "");

        if (!encodedImage.equalsIgnoreCase("")) {
            byte[] b = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            profilePic.setImageBitmap(bitmap);
        }
        location.setTag(1);
        location.setOnClickListener(this);
        editProfile = (ImageView) hView.findViewById(R.id.ivEditProfile);
        editProfile.setOnClickListener(this);
        CircleImageView changeDP = (CircleImageView) hView.findViewById(R.id.ivCameraChangeProfilePic);
        changeDP.setOnClickListener(this);
*/
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        emergencyIV = (ImageView) findViewById(R.id.ivEmergency);
        eatenIV = (ImageView) findViewById(R.id.ivEaten);
        commuteIV = (ImageView) findViewById(R.id.ivCommute);
        frtIV = (ImageView) findViewById(R.id.ivFrt);
        cargoIV = (ImageView) findViewById(R.id.ivCargo);
        gatedCommIV = (ImageView) findViewById(R.id.ivGatedComm);
        emergencyIV.setImageResource(R.drawable.emergency);
        eatenIV.setImageResource(R.drawable.eaten_unselected);
        commuteIV.setImageResource(R.drawable.commute_unselected);
        frtIV.setImageResource(R.drawable.versha_group_icon);
        cargoIV.setImageResource(R.drawable.cargo_unselected);
        gatedCommIV.setImageResource(R.drawable.gatepass_unselected);
        emergencyTV = (TextView) findViewById(R.id.tvEmergency);

        eatenTV = (TextView) findViewById(R.id.tvEaten);
        commuteTV = (TextView) findViewById(R.id.tvCommute);
        frtTV = (TextView) findViewById(R.id.tvFrt);
        frtTV.setText("VERSHA");
        cargoTV = (TextView) findViewById(R.id.tvCargo);
        gatedCommTV = (TextView) findViewById(R.id.tvGatedComm);

        emergencyTV.setTextColor(getResources().getColor(R.color.white));
        eatenTV.setTextColor(getResources().getColor(R.color.colorPrimary));
        commuteTV.setTextColor(getResources().getColor(R.color.colorPrimary));
        frtTV.setTextColor(getResources().getColor(R.color.colorPrimary));
        cargoTV.setTextColor(getResources().getColor(R.color.colorPrimary));
        gatedCommTV.setTextColor(getResources().getColor(R.color.colorPrimary));


        footer = (LinearLayout) findViewById(R.id.footer);
        emergency = (LinearLayout) findViewById(R.id.llEmergency);
        eaten = (LinearLayout) findViewById(R.id.llEaten);
        commute = (LinearLayout) findViewById(R.id.llCommute);
        frt = (LinearLayout) findViewById(R.id.llFRT);
        cargo = (LinearLayout) findViewById(R.id.llCargo);
        gatedComm = (LinearLayout) findViewById(R.id.llGatedComm);
        emergency.setOnClickListener(this);
        eaten.setOnClickListener(this);
        commute.setOnClickListener(this);
        frt.setOnClickListener(this);
        cargo.setOnClickListener(this);
        gatedComm.setOnClickListener(this);


        if (ContextCompat.checkSelfPermission(NearByActivity.this,
                android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NearByActivity.this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
        viewPager.setAdapter(adapter);


        //TAB CUSTOM COLOR & FONT START  >>>>>
        for (int i = 0; i < tabLayout.getTabCount(); i++) {

            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {

                TextView tabTextView = new TextView(this);
                tab.setCustomView(tabTextView);
                tabTextView.setTextColor(getResources().getColor(R.color.tab_unselected));
                tabTextView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                tabTextView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                Typeface face = Typeface.createFromAsset(getAssets(),
                        "fonts/Roboto-Light.ttf");
                tabTextView.setTypeface(face);

                tabTextView.setText(tab.getText());

                // First tab is the selected tab, so if i==0 then set BOLD typeface
                if (i == 0) {
                    tabTextView.setTextColor(getResources().getColor(R.color.white));
                    Typeface faceer = Typeface.createFromAsset(getAssets(),
                            "fonts/Roboto-Bold.ttf");
                    tabTextView.setTypeface(faceer);
                }

            }

        }

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                TextView text = (TextView) tab.getCustomView();
                String subTitle = text.getText().toString().trim();
                text.setTextColor(getResources().getColor(R.color.white));
                Typeface face = Typeface.createFromAsset(getAssets(),
                        "fonts/Roboto-Bold.ttf");
                text.setTypeface(face);

                if (subTitle.equals("NEAR BY")) {
                    subTitle = "People";
                } else if (subTitle.equals("FRIENDS")) {
                    subTitle = "Friends";
                } else if (subTitle.equals("FAMILY")) {
                    subTitle = "Family";
                }
                toolbar.setTitle("Nearby - " + subTitle);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView text = (TextView) tab.getCustomView();
                text.setTextColor(getResources().getColor(R.color.tab_unselected));
                Typeface face = Typeface.createFromAsset(getAssets(),
                        "fonts/Roboto-Light.ttf");
                text.setTypeface(face);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });
        //TAB CUSTOM COLOR & FONT END  <<<<<<
    }

    private void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentNearBy(), "NEAR BY");
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    viewPager.setAdapter(adapter);
                } else {
                    finish();
                }
                return;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivCameraChangeProfilePic:
              /*  startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                Toast.makeText(getApplicationContext(), "Change profile picture", Toast.LENGTH_LONG).show();
*/
                selectImage();

                break;

            case R.id.ivLocation:
                if (locationStatus == 0) {
                    location.setImageResource(R.drawable.location_show);
                    Toast.makeText(getApplicationContext(), "Show my location to others", Toast.LENGTH_LONG).show();
                    locationStatus = 1;
                } else if (locationStatus == 1) {
                    location.setImageResource(R.drawable.location_hide);
                    Toast.makeText(getApplicationContext(), "Hide my location  to others", Toast.LENGTH_LONG).show();
                    locationStatus = 0;
                }
                break;

            case R.id.llEaten:

                Toast.makeText(getApplicationContext(), "Eaten", Toast.LENGTH_SHORT).show();

                break;
            case R.id.llCommute:
                Toast.makeText(getApplicationContext(), "Commute", Toast.LENGTH_SHORT).show();

                break;
            case R.id.llCargo:


                Toast.makeText(getApplicationContext(), "Cargo", Toast.LENGTH_SHORT).show();

                break;
            case R.id.llGatedComm:

                Toast.makeText(getApplicationContext(), "Gated Community", Toast.LENGTH_SHORT).show();
                break;

        }
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.WHITE;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {

        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_close_scale, R.anim.activity_open_scale);

            finish();

            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;

            }
        }, 2000);
    }

    //UPLOADING IMAGE
//UPLOADING IMAGE
    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(NearByActivity.this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(),
                "ListoDP.jpg");
        outPutfileUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
        CropImage.activity(outPutfileUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);

        startActivityForResult(intent, REQUEST_CAMERA);
    }

    Uri resultUri;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    resultUri = result.getUri();
                    onCaptureImageResult(data);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }

    }


    Bitmap bitmap = null;

    private void onCaptureImageResult(Intent data) {

        String uri = resultUri.toString();
        Log.e("uri-:", uri);
        //  Toast.makeText(this, outPutfileUri.toString(), Toast.LENGTH_LONG).show();

        //Bitmap myBitmap = BitmapFactory.decodeFile(uri);
        // mImageView.setImageURI(Uri.parse(uri));   OR drawable make image strechable so try bleow also

        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
            Drawable d = new BitmapDrawable(getResources(), bitmap);
            profilePic.setImageDrawable(d);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

            SharedPreferences.Editor editor = getSharedPreferences("LISTO", MODE_PRIVATE).edit();
            editor.putString("camera_image_data", encodedImage);
            editor.commit();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {


        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();
                String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                SharedPreferences.Editor editor = getSharedPreferences("LISTO", MODE_PRIVATE).edit();
                editor.putString("camera_image_data", encodedImage);
                editor.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        profilePic.setImageBitmap(bitmap);
    }


}