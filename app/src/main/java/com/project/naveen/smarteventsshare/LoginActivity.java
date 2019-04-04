package com.project.naveen.smarteventsshare;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.model.people.Person;
import com.project.naveen.smarteventsshare.receiver.NetworkStateChangeReceiver;
import com.project.naveen.smarteventsshare.socialintegration.constants.AppConstants;
import com.project.naveen.smarteventsshare.socialintegration.helpers.FbConnectHelper;
import com.project.naveen.smarteventsshare.socialintegration.helpers.GooglePlusSignInHelper;
import com.project.naveen.smarteventsshare.socialintegration.managers.SharedPreferenceManager;
import com.project.naveen.smarteventsshare.socialintegration.model.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;

import static com.project.naveen.smarteventsshare.receiver.NetworkStateChangeReceiver.IS_NETWORK_AVAILABLE;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, FbConnectHelper.OnFbSignInListener, GooglePlusSignInHelper.OnGoogleSignInListener{
    EditText UserId, PasswordEt;
    String userId, password, type;

    // Session Manager Class
    SessionManager session;

    private static CheckBox show_hide_password;

    String TAG_PER = "Permissions";

    TextView forgot_password;
    Dialog myDialog;

    private FbConnectHelper fbConnectHelper;
    private GooglePlusSignInHelper gSignInHelper;

    //Location
    private static final String TAG = "PostImageLocation";
    private static final long INTERVAL = 1000;
    private static final long FASTEST_INTERVAL = 1000;

    private boolean currentlyProcessingLocation = false;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;

    Double latitude, longitude;
    String currentAddr,district;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        UserId = findViewById(R.id.etUserName);
        PasswordEt = findViewById(R.id.etPwd);
        forgot_password = findViewById(R.id.forgot_password);
        myDialog = new Dialog(this);
        if(checkAndRequestPermissions()) {
            init();
        }else
            Log.e(TAG_PER, "Some Permissions are not granted");
    }

    public static boolean isLocationEnabled(Context context)
    {
        int locationMode = 0;
        String locationProviders;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            try
            {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        }
        else
        {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }
    protected void getLocation() {
        if (isLocationEnabled(LoginActivity.this))
        {
            /*
            Log.w("TAG_PER", "Location is enabled");
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

            //You can still do this if you like, you might get lucky:
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                            ,10);
                }
                return;
            }
            Log.w("Provider", bestProvider);
            Location location = locationManager.getLastKnownLocation(bestProvider);
            locationManager.requestLocationUpdates(bestProvider, 0, 0, listener);
            if (location != null) {
                Log.w("TAG_PER", "GPS is on");
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                String time = DateFormat.getTimeInstance().format(location.getTime());
                String latlng =  "Latitude: " + latitude + "\nLongitude: " + longitude+"\nTime: "+time;
                Log.w("GEO Location", latlng);
            }
            else{
                Log.w("TAG_PER", "Location is null");
                //This is what you need:
                //locationManager.requestLocationUpdates(bestProvider, 1000, 0, listener);
            }
            */
        }
        else
        {
            //prompt user to enable location....
            android.app.AlertDialog.Builder notifyLocationServices = new android.app.AlertDialog.Builder(LoginActivity.this);
            notifyLocationServices.setTitle("Switch on Location Services");
            notifyLocationServices.setMessage("Location Services must be turned on to complete this action. Also please take note that if on a very weak network connection,  such as 'E' Mobile Data or 'Very weak Wifi-Connections' it may take even 15 mins to load. If on a very weak network connection as stated above, location returned to application may be null or nothing and cause the application to crash.");
            notifyLocationServices.setPositiveButton("Ok, Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent openLocationSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    LoginActivity.this.startActivity(openLocationSettings);
                    finish();
                }
            });
            notifyLocationServices.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            notifyLocationServices.show();
        }
        startTracking();
    }

    private void init() {
        Log.e(TAG_PER, "All Permissions are granted");
        // carry on the normal flow, as the case of  permissions  granted.


        IntentFilter intentFilter = new IntentFilter(NetworkStateChangeReceiver.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);
                String networkStatus = isNetworkAvailable ? "connected" : "disconnected";

                Snackbar.make(findViewById(R.id.LoginActivity), "Network Status: " + networkStatus, Snackbar.LENGTH_LONG).show();
            }
        }, intentFilter);

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
            connected = false;

        if(connected == false)
        {
            Intent intent = new Intent(LoginActivity.this,Internet_Connection.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            finish();
        }
        // Session Manager
        session = new SessionManager(getApplicationContext());

        //Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();

        if (session.isLoggedIn()==true){
            Intent intent = new Intent(LoginActivity.this,TrendingWall.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        show_hide_password = (CheckBox) findViewById(R.id.show_hide_password);
        show_hide_password.setTypeface(ResourcesCompat.getFont(this, R.font.dancing_script_bold));
        show_hide_password
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {

                        // If it is checkec then show password else hide
                        // password
                        if (isChecked) {
                            show_hide_password.setText(R.string.hide_pwd);// change
                            // checkbox
                            // text

                            PasswordEt.setInputType(InputType.TYPE_CLASS_TEXT);
                            PasswordEt.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                            PasswordEt.setTypeface(ResourcesCompat.getFont(LoginActivity.this, R.font.dancing_script_bold));
                        } else {
                            show_hide_password.setText(R.string.show_pwd);// change
                            // checkbox
                            // text

                            PasswordEt.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            PasswordEt.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password
                            PasswordEt.setTypeface(ResourcesCompat.getFont(LoginActivity.this, R.font.dancing_script_bold));
                        }

                    }
                });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowForgotPasswordPopup(R.layout.forgot_password_popup);
            }
        });

        ButterKnife.bind(this);

        setup();
        getLocation();
    }

    private void setup() {
        GooglePlusSignInHelper.setClientID(AppConstants.GOOGLE_CLIENT_ID);
        gSignInHelper = GooglePlusSignInHelper.getInstance();
        gSignInHelper.initialize(this, this);

        fbConnectHelper = new FbConnectHelper(this,this);
        //twitterConnectHelper = new TwitterConnectHelper(getActivity(), this);
    }
    //@OnClick(R.id.login_facebook)
    public void loginwithFacebook(View view) {
        fbConnectHelper.connect();
    }
    //@OnClick(R.id.login_google)
    public void loginwithGoogle(View view) {
        gSignInHelper.signIn(this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        fbConnectHelper.onActivityResult(requestCode, resultCode, data);
        gSignInHelper.onActivityResult(requestCode, resultCode, data);
        //twitterConnectHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void OnFbSuccess(GraphResponse graphResponse) {
        UserModel userModel = getUserModelFromGraphResponse(graphResponse);
        if(userModel!=null) {
            SharedPreferenceManager.getSharedInstance().saveUserModel(userModel);
            startHomeActivity(userModel);
        }
    }

    private UserModel getUserModelFromGraphResponse(GraphResponse graphResponse)
    {
        UserModel userModel = new UserModel();
        try {
            JSONObject jsonObject = graphResponse.getJSONObject();
            userModel.userName = jsonObject.getString("name");
            userModel.userEmail = jsonObject.getString("email");
            String id = jsonObject.getString("id");
            String profileImg = "http://graph.facebook.com/"+ id+ "/picture?type=large";
            userModel.profilePic = profileImg;
            Log.e("FB",profileImg);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return userModel;
    }

    @Override
    public void OnFbError(String errorMessage) {
        resetToDefaultView(errorMessage);
    }

    @Override
    public void OnGSignSuccess(GoogleSignInAccount acct, Person person) {
        UserModel userModel = new UserModel();
        userModel.userName = (acct.getDisplayName()==null)?"":acct.getDisplayName();
        userModel.userEmail = acct.getEmail();

        String TAG_PER = "OnGSignSuccess";
        Log.e(TAG_PER, "OnGSignSuccess: AccessToken "+ acct.getIdToken());

        if(person!=null) {
            int gender = person.getGender();

            if (gender == 0)
                userModel.gender = "MALE";
            else if (gender == 1)
                userModel.gender = "FEMALE";
            else
                userModel.gender = "OTHERS";

            Log.e(TAG_PER, "OnGSignSuccess: gender "+ userModel.gender);
        }

        Uri photoUrl = acct.getPhotoUrl();
        if(photoUrl!=null)
            userModel.profilePic = photoUrl.toString();
        else
            userModel.profilePic = "";
        Log.e(TAG_PER, acct.getIdToken());

        SharedPreferenceManager.getSharedInstance().saveUserModel(userModel);
        startHomeActivity(userModel);
    }

    @Override
    public void OnGSignError(GoogleSignInResult errorMessage) {
        resetToDefaultView("Google Sign in error@"+errorMessage);
    }

    private void startHomeActivity(UserModel userModel)
    {
        UserId.setText(userModel.userName);
    }

    private void resetToDefaultView(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    public void ShowForgotPasswordPopup(int view) {
        TextView txtclose;
        myDialog.setContentView(view);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final EditText etGmail,etUserId;

        Button btnConfirm;

        etGmail =(EditText) myDialog.findViewById(R.id.etGmail);
        etUserId =(EditText) myDialog.findViewById(R.id.etUserId);
        btnConfirm =(Button) myDialog.findViewById(R.id.btnConfirm);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Gmail;
                Gmail = etGmail.getText().toString();
                userId = etUserId.getText().toString();

                if(Gmail.trim().length()>0 ){
                    type = "gmail";
                    BackgroundWorker backgroundWorker = new BackgroundWorker();
                    //backgroundWorker.execute(Gmail);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Enter the Email id",Toast.LENGTH_LONG).show();
                }

            }
        });

        myDialog.show();

    }
    public void OnLogin(View view) {
        if(district!=null){
            userId = UserId.getText().toString();
            password = PasswordEt.getText().toString();
            if(userId.trim().length()>0 && password.trim().length()>0){
                type = "login";
                BackgroundWorker backgroundWorker = new BackgroundWorker();
                backgroundWorker.execute();
            }else{
                String f="Please enter username and password";
                Toast.makeText(LoginActivity.this,f ,Toast.LENGTH_LONG ).show();
            }
        }else {
            Toast.makeText(LoginActivity.this,"Wait for a minute... and Try again...", Toast.LENGTH_LONG).show();
        }
    }

    public void onSignUp(View view) {
        Intent intent = new Intent(LoginActivity.this,SignUp.class);
        startActivity(intent);
    }

    private Boolean exit = false;
    @Override
    public void onBackPressed() {

        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }
    public class BackgroundWorker extends AsyncTask<String,Void,String> {

        android.app.AlertDialog alertDialog;

        @Override
        protected String doInBackground(String... params) {



            try {
                String post_data = null;
                String login_url = null;

                if(type.equals("login")) {
                    login_url = "http://nutzindia.com/SES_Wall/login.php";
                    post_data = URLEncoder.encode("uid","UTF-8")+"="+ URLEncoder.encode(userId,"UTF-8")+"&"
                            + URLEncoder.encode("passwd","UTF-8")+"="+ URLEncoder.encode(password,"UTF-8");
                }else if(type.equals("gmail")) {
                    String gmail;
                    gmail = params[0];
                    login_url = "http://nutzindia.com/SES_Wall/forgotPassword.php";
                    post_data = URLEncoder.encode("user_id","UTF-8")+"="+ URLEncoder.encode(userId,"UTF-8")+"&"
                            + URLEncoder.encode("gmail","UTF-8")+"="+ URLEncoder.encode(gmail,"UTF-8");
                }

                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line="";
                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String result) {

            String f = result;
            Log.e("Login", result);
            final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
            if(type.equals("login")){

                //builder.setTitle("Login Status");

                if(userId.trim().length() > 0 && password.trim().length() > 0){

                    if(result.charAt(0)=='L')
                    {
                        //      f ="Login Success! Welcome!!!";

                        SessionManager session;
                        session = new SessionManager(LoginActivity.this);
                        session.createLoginSession(userId,password,district);

                        Intent intent = new Intent(LoginActivity.this,SplashActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Bundle extras = new Bundle();
                        extras.putString("userId",userId);
                        intent.putExtras(extras);
                        startActivity(intent);

                        Toast.makeText(LoginActivity.this,"You have successfully logged in" ,Toast.LENGTH_LONG ).show();
                        /*
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(LoginActivity.this,SplashActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                Bundle extras = new Bundle();
                                extras.putString("userId",userId);
                                intent.putExtras(extras);
                                startActivity(intent);

                            }
                        });
                        */
                    }else if(result.charAt(0)=='Y'){
                        Toast.makeText(LoginActivity.this,"You are already logged in other device" ,Toast.LENGTH_LONG ).show();
                    }
                    else {
                        Toast.makeText(LoginActivity.this,"User Id or Password is Incorrect" ,Toast.LENGTH_LONG ).show();
                        /*
                        f = "User Id or Password is Incorrect";
                        builder.setNeutralButton("Try Again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                android.support.v7.app.AlertDialog alert1 = builder.create();
                                alert1.cancel();
                            }
                        });
                        */
                    }

                }
                else{
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    f="Please enter username and password";
                    Toast.makeText(LoginActivity.this,f ,Toast.LENGTH_LONG ).show();
                    /*
                    builder.setNeutralButton("Try Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            android.support.v7.app.AlertDialog alert1 = builder.create();
                            alert1.cancel();
                        }
                    });
                    */
                }

            }else if(type.equals("gmail")) {
                if(result.charAt(0)=='c'){
                    f = "Your password is sent to your mail";
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            android.support.v7.app.AlertDialog alert1 = builder.create();
                            alert1.cancel();
                            myDialog.cancel();
                        }
                    });
                }else if (result.charAt(0)=='w'){
                    f = "Your mail id is incorrect";
                    builder.setNeutralButton("Try Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            android.support.v7.app.AlertDialog alert1 = builder.create();
                            alert1.cancel();
                        }
                    });
                }
                builder.setMessage(f);
                android.support.v7.app.AlertDialog alert1 = builder.create();
                alert1.show();
                // To close the AlertDialog
                // alert1.cancel();
            }

        }


    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private  boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int storage2= ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        int loc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        /*
        int record_audio = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO);
        int process_outgoing_calls = ContextCompat.checkSelfPermission(this, android.Manifest.permission.PROCESS_OUTGOING_CALLS);
        int read_phone_state = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE);
        */

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (storage2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        /*
        if (record_audio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.RECORD_AUDIO);
        }
        if (read_phone_state != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (process_outgoing_calls != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.PROCESS_OUTGOING_CALLS);
        }
        */
        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.e(TAG_PER, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                /*
                perms.put(Manifest.permission.PROCESS_OUTGOING_CALLS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);
                */
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (/*perms.get(Manifest.permission.PROCESS_OUTGOING_CALLS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                            &&perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

                            &&*/
                            perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                    && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                    && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                    && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                                    && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        Log.e(TAG_PER, "All permissions are granted");
                        init();
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.e(TAG_PER, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (/*ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.PROCESS_OUTGOING_CALLS)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)

                                || */
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                                ) {
                            showDialogOK("Storage, Camera and Location Permissions are required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    //Location
    private void startTracking() {
        Log.w(TAG, "startTracking");

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            Log.w(TAG, "GooglePlayServicesAvailable");
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                Log.w(TAG, "googleApiClient.connect();");
                googleApiClient.connect();
            }
            //googleApiClient.connect();
        } else {
            Log.e(TAG, "unable to connect to google play services.");
        }
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.w(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());

            Geocoder geoCoder = new Geocoder(LoginActivity.this, Locale.getDefault());
            StringBuilder builder = new StringBuilder();
            try {
                Double latitude,longitude;
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                List<Address> address = geoCoder.getFromLocation(latitude, longitude, 1);
                int maxLines = address.get(0).getMaxAddressLineIndex();
                for (int i=0; i<maxLines; i++) {
                    String addressStr = address.get(0).getAddressLine(i);
                    builder.append(addressStr);
                    builder.append(" ");
                }

                String finalAddress;
                //finalAddress= builder.toString(); //This is the complete address.
                finalAddress = address.get(0).getAddressLine(0)
                //        +"\n"+ address.get(0).getLocality()+"\n"+
                //      address.get(0).getAdminArea()+"\n"+
                //    address.get(0).getCountryName()+"\n"+
                //  address.get(0).getPostalCode()
                ;
                Log.w("Location", "Latitude: " + latitude + "\nLongitude: " + longitude+"\nFinal Address: "+finalAddress);

                //t.setText("Latitude: " + latitude + "\nLongitude: " + longitude+"\nFinal Address: "+finalAddress); //This will display the final address.

                this.latitude = latitude;
                this.longitude = longitude;
                currentAddr = finalAddress;

                district = address.get(0).getLocality();
                Log.w("Location", "district: "+district);
                stopLocationUpdates();
            } catch (IOException e) {
                // Handle IOException
            } catch (NullPointerException e) {
                // Handle NullPointerException
            }

        } else {
            Log.w(TAG, "NO POSITION FOUND");
        }
    }

    private void stopLocationUpdates() {
        Log.w(TAG, "stopLocationUpdates");
        googleApiClient.disconnect();
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        Log.w(TAG, "onConnected");

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(INTERVAL); // milliseconds
        locationRequest.setFastestInterval(FASTEST_INTERVAL); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

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
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed");

        stopLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "GoogleApiClient connection has been suspend");
    }
}
