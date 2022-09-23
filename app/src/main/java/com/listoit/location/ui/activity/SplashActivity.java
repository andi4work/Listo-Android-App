package com.listoit.location.ui.activity;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.listoit.location.R;
import com.listoit.location.app.Prefs;
import com.listoit.location.helpers.Constants;
import com.listoit.location.helpers.InternetDetector;
import com.listoit.location.model.DeviceDetail;
import com.listoit.location.retrofit.services.HttpPost_Request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

import io.realm.Realm;

import static com.listoit.location.helpers.Constants.ANDROID_ID;
import static com.listoit.location.helpers.Constants.CREATION_DATE;
import static com.listoit.location.helpers.Constants.DEVICE_MANUFACTURER;
import static com.listoit.location.helpers.Constants.DEVICE_MODEL;
import static com.listoit.location.helpers.Constants.DEVICE_WIDTH;
import static com.listoit.location.helpers.Constants.IMEI;
import static com.listoit.location.helpers.Constants.IMSI;
import static com.listoit.location.helpers.Constants.NETWORK_NAME;
import static com.listoit.location.helpers.Constants.NETWORK_TYPE;
import static com.listoit.location.helpers.Constants.OS_VERSION;
import static com.listoit.location.helpers.Constants.REGISTERED_EMAIL_ID;
import static com.listoit.location.helpers.Constants.SIM_SERIAL_NUMBER;

/**
 * Created by ${ChandraMohanReddy} on 1/11/2017.
 */

public class SplashActivity extends Activity {
    private static final int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    String TAG = "Device INFO";
    String ENCODED_JSON_STRING = "ENCODED_JSON_STRING";
    String base64EncodedJsonData, base64DecodedJsonData;
    String jsonStr;
    final Context context = SplashActivity.this;
    byte[] encodedString = null;
    byte[] decodedString = null;
    String message = null;
    String statusCode = "0";
    int device_oID;
    String registrationToken;
    Context mContext;
    private Realm realm;
    ImageView splashLogo, ivRefresh;
    InternetDetector internetDetector;
    Animation rotation;
    TextView greetings, tvInternet, tvRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        tvInternet = (TextView) findViewById(R.id.tvInternetDetector);

        ivRefresh = (ImageView) findViewById(R.id.ivRefresh);
        tvRefresh = (TextView) findViewById(R.id.tvRefresh);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivRefresh.startAnimation(rotation);
                tvInternet.setText("Checking internet connection...");
                if (internetDetector.isConnectingToInternet() && internetDetector.isOnline()) {
                    new MyAsyncTask().execute();
                } else {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            ivRefresh.clearAnimation();
                            tvInternet.setText("Internet not available");
                        }
                    }, 4500);
                }
            }
        });
        splashLogo = (ImageView) findViewById(R.id.ivSplashLogo);
        rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        rotation.setFillAfter(true);
        splashLogo.startAnimation(rotation);
        greetings = (TextView) findViewById(R.id.tvGreetings);
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            greetings.setText("Good Morning");
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            greetings.setText("Good Afternoon");
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            greetings.setText("Good Evening");
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            greetings.setText("Hi...");
        }
        displayVersionName();

        // This solution will leak memory!  Don't use!!!
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Check();
            }
        }, 4600);

        internetDetector = new InternetDetector(getApplicationContext());

    }

    /*   AccountManager accountManager = AccountManager.get(this);
       Account account = new Account("Listo", "com.listoit.listo.account.LISTOACCOUNT");
       boolean success = accountManager.addAccountExplicitly(account, "password", null);
       if (success) {
           Log.d(TAG, "Account created");
       } else {
           Log.d(TAG, "Account creation failed. Look at previous logs to investigate");
       }*/
    public void Check() {
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else if (internetDetector.isConnectingToInternet() && internetDetector.isConnectingToInternet()) {
            new MyAsyncTask().execute();
        } else {
            splashLogo.clearAnimation();
            tvInternet.setVisibility(View.VISIBLE);
            ivRefresh.setVisibility(View.VISIBLE);
            tvRefresh.setVisibility(View.VISIBLE);

        }
    }

    public void deviceInfo() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = tm.getDeviceId();
        IMSI = tm.getSubscriberId();
        ANDROID_ID = Settings.System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        DEVICE_MANUFACTURER = android.os.Build.MANUFACTURER;
        DEVICE_MODEL = android.os.Build.MODEL;
        OS_VERSION = Build.VERSION.RELEASE;
        NETWORK_TYPE = tm.getNetworkOperator();
        NETWORK_NAME = tm.getNetworkOperatorName();
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        registrationToken = FirebaseInstanceId.getInstance().getToken();
        Constants.FCM_TOKEN = registrationToken;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Account[] accounts =
                AccountManager.get(this).getAccountsByType("com.google");
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                REGISTERED_EMAIL_ID = account.name;
            }
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        CREATION_DATE = df.format(c.getTime());

        Display mDisplay = this.getWindowManager().getDefaultDisplay();
        int width = mDisplay.getWidth();
        DEVICE_WIDTH = String.valueOf(width);
        SIM_SERIAL_NUMBER = tm.getSimSerialNumber();
        Log.d(TAG, "IMEI : " + IMEI + "\nIMSI : " + IMSI + "\nANDROID_ID : " + ANDROID_ID + "\nDEVICE_MANUFACTURER : " + DEVICE_MANUFACTURER + "\nDEVICE_MODEL : " + DEVICE_MODEL + "\nOS_VERSION : " + OS_VERSION + "\nNETWORK_TYPE : " + NETWORK_TYPE + "\nNETWORK_NAME : " + NETWORK_NAME + "\nREGISTERED_EMAIL_ID : " + REGISTERED_EMAIL_ID + "\nCREATION_DATE : " + CREATION_DATE + "\nDEVICE_WIDTH : " + DEVICE_WIDTH + "\nSIM_SERIAL_NO : " + SIM_SERIAL_NUMBER);
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
                new MyAsyncTask().execute();

            }

        }

        return true;
    }


    private void setDeviceDetails() {
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        DeviceDetail dev_detail = realm.createObject(DeviceDetail.class, 1 + System.currentTimeMillis());
        dev_detail.setDevice_oid(Constants.DEVICE_OID);
        dev_detail.setReg_email_id(Constants.REGISTERED_EMAIL_ID);
        dev_detail.setGcm_code(Constants.FCM_TOKEN);
        dev_detail.setReg_datatime(Constants.CREATION_DATE);
        realm.copyToRealmOrUpdate(dev_detail);
        realm.commitTransaction();
        Prefs.with(this).setPreLoad(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    new MyAsyncTask().execute();

                } else {
                    finish();
                }
                return;

            }
        }
    }

    public void JsonConvertion() {
        JSONObject register = new JSONObject();
        try {
            register.put("imei", IMEI);
            register.put("imsi", IMSI);
            register.put("android_id", ANDROID_ID);
            register.put("device_make", DEVICE_MANUFACTURER);
            register.put("device_model", DEVICE_MODEL);
            register.put("os_version", OS_VERSION);
            register.put("network_type", NETWORK_TYPE);
            register.put("network_name", NETWORK_NAME);
            register.put("registered_email_id", REGISTERED_EMAIL_ID);
            register.put("creation_date", CREATION_DATE);
            register.put("device_width", DEVICE_WIDTH);
            register.put("sim_serial_number", SIM_SERIAL_NUMBER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonStr = register.toString();


//JSON TO BASE64 CONVERTING

        try {
            encodedString = jsonStr.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        base64EncodedJsonData = Base64.encodeToString(encodedString, Base64.DEFAULT);
        Log.d("MESSAGE", "" + base64EncodedJsonData);


        Log.d(ENCODED_JSON_STRING, base64EncodedJsonData);
    }

    private class MyAsyncTask extends AsyncTask<String, Void, String> {
        String result = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            deviceInfo();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                JsonConvertion();
                result = HttpPost_Request.DeviceRegistration(base64EncodedJsonData);

            } catch (Exception ex) {
                Log.d("Data fetch Failed...! ", ex.toString());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("encoded_response", "" + s);
            try {
                decodedString = Base64.decode(s, Base64.DEFAULT);
                base64DecodedJsonData = new String(decodedString, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.d("decoded_response", "" + base64DecodedJsonData);
            try {
                JSONArray itemArray = new JSONArray(base64DecodedJsonData);
                for (int i = 0; i < itemArray.length(); i++) {
                    statusCode = itemArray.getString(0);
                    message = itemArray.getString(1);
                    device_oID = itemArray.getInt(2);
                    Log.e("Status Code:", statusCode);
                    Log.e("Status Message:", message);
                    Log.e("Device device_oID:", String.valueOf(device_oID));
                    // Log.isLoggable("Device device_oID:", device_oID);
                    registrationToken = FirebaseInstanceId.getInstance().getToken();
                    Constants.FCM_TOKEN = registrationToken;
                    Constants.DEVICE_OID = String.valueOf(device_oID);
                    SharedPreferences.Editor editor = getSharedPreferences("LISTO", MODE_PRIVATE).edit();
                    editor.putString("statusCode", statusCode);
                    editor.putString("message", message);
                    editor.putInt("deviceOID", device_oID);
                    editor.putString("fcmRegistrationToken", registrationToken);
                    editor.putString("emailID", REGISTERED_EMAIL_ID);
                    editor.putString("fcmRegistrationToken", registrationToken);
                    editor.putString("emailID", REGISTERED_EMAIL_ID);
                    editor.putString("date", CREATION_DATE);
                    editor.putString("date", CREATION_DATE);
                    editor.commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(ENCODED_JSON_STRING, base64EncodedJsonData);
            Log.d("MESSAGEEEEE", "" + message);
            Realm.init(context);
            realm = Realm.getDefaultInstance();
            if (!Prefs.with(context).getPreLoad()) {
                setDeviceDetails();
            } else {
                final DeviceDetail dd = realm.where(DeviceDetail.class).findFirst();
            }
            if (statusCode.equals("0")) {
                Toast.makeText(getApplicationContext(), "Error while connecting to server ", Toast.LENGTH_SHORT).show();
                tvInternet.setVisibility(View.VISIBLE);
                ivRefresh.setVisibility(View.VISIBLE);
            } else if (statusCode.equals("L104")) {
                SharedPreferences prefs = getSharedPreferences("LOGIN", MODE_PRIVATE);
                int restoredText = prefs.getInt("success", 0);
                if (restoredText == 1) {
                    Intent intent = new Intent(getApplicationContext(), NearByActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
                    overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), NearByActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("EXIT", true);
                    startActivity(intent);
                    finish();
                }
            } else if (statusCode.equals("L100")) {
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), NearByActivity.class));

                finish();
            }
        }
    }

    private void displayVersionName() {
        String versionName = "";
        PackageInfo packageInfo;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = "v " + packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        TextView tv = (TextView) findViewById(R.id.tvVersionName);
        tv.setText(versionName);
    }
}