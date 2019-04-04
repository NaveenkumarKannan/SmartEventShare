package com.project.naveen.smarteventsshare;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.GraphResponse;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.model.people.Person;
import com.project.naveen.smarteventsshare.socialintegration.constants.AppConstants;
import com.project.naveen.smarteventsshare.socialintegration.helpers.FbConnectHelper;
import com.project.naveen.smarteventsshare.socialintegration.helpers.GooglePlusSignInHelper;
import com.project.naveen.smarteventsshare.socialintegration.managers.SharedPreferenceManager;
import com.project.naveen.smarteventsshare.socialintegration.model.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;

public class SignUp extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, FbConnectHelper.OnFbSignInListener, GooglePlusSignInHelper.OnGoogleSignInListener{
    TextView tvDOB;
    String type;

    ProgressDialog loading;

    EditText etName,etPhNo,etEmail,etProStu,etShowWallCity,etNativePlace,etResidingCity,etCountry,etPwd,etConfirmPwd,etUserName;

    String name,phNo,emailId,proStu,showWallCity,nativePlace,residingCity,country,
            DOB,pwd,confirmPwd,user_name;
    String profileImgUrl,profileImgPath,profileImgString;
    Bitmap profileImgBitmap;
    SimpleDraweeView profileImg;
    private FbConnectHelper fbConnectHelper;
    private GooglePlusSignInHelper gSignInHelper;
    private int PICK_IMAGE = 100,RC_SIGN_IN = 101;

    //Location
    private static final String TAG = "SignUpLocation";
    private static final long INTERVAL = 1000;
    private static final long FASTEST_INTERVAL = 1000;

    private boolean currentlyProcessingLocation = false;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;

    Double latitude, longitude;
    String currentAddr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etName= findViewById(R.id.etName);
        etPhNo= findViewById(R.id.etPhNo);
        etEmail= findViewById(R.id.etEmail);
        etProStu= findViewById(R.id.etProStu);
        etShowWallCity= findViewById(R.id.etShowWallCity);
        etNativePlace= findViewById(R.id.etNativePlace);
        etResidingCity= findViewById(R.id.etResidingCity);
        etCountry= findViewById(R.id.etCountry);
        etPwd= findViewById(R.id.etPwd);
        etConfirmPwd = findViewById(R.id.etConfirmPwd);
        etUserName = findViewById(R.id.etUserName);

        ButterKnife.bind(this);
        profileImg = ButterKnife.findById(this,R.id.user_imageview);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
                tvDOB= findViewById(R.id.tvDOB);
        tvDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int day, month, year;
                Calendar calendar = Calendar.getInstance();
                day = calendar.get(Calendar.DAY_OF_MONTH);
                month = calendar.get(Calendar.MONTH);
                year = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(SignUp.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        NumberFormat numberFormat = new DecimalFormat("00");
                        DOB = String.valueOf(numberFormat.format(i1+1))+"/"+String.valueOf(numberFormat.format(i2))+"/"+String.valueOf(i);
                        tvDOB.setText(DOB);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });
        setup();
        startTracking();
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION| Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE);
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
        if(requestCode == RC_SIGN_IN){
            fbConnectHelper.onActivityResult(requestCode, resultCode, data);
            gSignInHelper.onActivityResult(requestCode, resultCode, data);
        }
        //twitterConnectHelper.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            profileImgUrl = null;
            //found mistake=> if(requestCode == RESULT_OK && requestCode == PICK_IMAGE)
            Uri imageUri = data.getData();
            // try1=>fail - Because of mistake
            String[] projection={MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(imageUri,projection,null,null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(projection[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            profileImgPath = filePath;
            profileImgBitmap = BitmapFactory.decodeFile(filePath);

            /*
            String path,externalPath;
            externalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            profileImgPath = "/JusTry/profileImgPath.jpg";
            path = externalPath + profileImgPath;

            FileOutputStream out = null;
            try {
                File file;
                file =new File(externalPath+"/JustTry","profileImgPath.jpg");
                out = new FileOutputStream(file);
                profileImgBitmap.compress(Bitmap.CompressFormat.JPEG, 25, out); // bmp is your Bitmap instance

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            profileImgPath = path;
            profileImgBitmap = BitmapFactory.decodeFile(profileImgPath);
            Log.e("path",path );
            */

            Log.e("filePath",filePath );
            if(profileImgBitmap!=null){
                profileImg.setImageURI(imageUri);
                Log.e("Galary","not null" );
            }
            else
                Log.e("Galery"," null" );

            profileImgBitmap = BitmapFactory.decodeFile(profileImgPath);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            profileImgBitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();

            profileImgString = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.e("stringHeadPhoto",profileImgString);
        }


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
            Log.e("FB", graphResponse.getRawResponse());
            JSONObject jsonObject = graphResponse.getJSONObject();
            userModel.userName = jsonObject.getString("name");
            userModel.userEmail = jsonObject.getString("email");
            String id,location,birthday,first_name;
            id = jsonObject.getString("id");
            location = jsonObject.getJSONObject("location").getString("name");
            birthday = jsonObject.getString("birthday");
            first_name = jsonObject.getString("first_name");

            etName.setText(first_name);
            etNativePlace.setText(location);
            etResidingCity.setText(location);
            tvDOB.setText(birthday);
            String profileImg = "http://graph.facebook.com/"+ id+ "/picture?type=large";
            userModel.profilePic = profileImg;
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

        String TAG = "OnGSignSuccess";
        Log.e(TAG, "OnGSignSuccess: AccessToken "+ acct.getIdToken());

        if(person!=null) {
            int gender = person.getGender();

            if (gender == 0)
                userModel.gender = "MALE";
            else if (gender == 1)
                userModel.gender = "FEMALE";
            else
                userModel.gender = "OTHERS";

            Log.e(TAG, "OnGSignSuccess: gender "+ userModel.gender);
        }

        Uri photoUrl = acct.getPhotoUrl();
        if(photoUrl!=null)
            userModel.profilePic = photoUrl.toString();
        else
            userModel.profilePic = "";
        Log.e(TAG, acct.getIdToken());

        SharedPreferenceManager.getSharedInstance().saveUserModel(userModel);
        startHomeActivity(userModel);
    }

    @Override
    public void OnGSignError(GoogleSignInResult errorMessage) {
        resetToDefaultView("Google Sign in error@"+errorMessage);
    }
    private void startHomeActivity(UserModel userModel)
    {
        profileImgUrl = userModel.profilePic;
        Log.e("profileImgUrl",profileImgUrl);
        profileImg.setImageURI(Uri.parse(profileImgUrl));
        etUserName.setText(userModel.userName);
        etEmail.setText(userModel.userEmail);
    }

    private void resetToDefaultView(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    public void goBack(View view) {
        finish();
    }
    public void onSignUp(View view) {
        name = etName.getText().toString();
        phNo = etPhNo.getText().toString();
        emailId = etEmail.getText().toString();
        proStu = etProStu.getText().toString();
        showWallCity = etShowWallCity.getText().toString();
        nativePlace = etNativePlace.getText().toString();
        residingCity = etResidingCity.getText().toString();
        country = etCountry.getText().toString();
        DOB = tvDOB.getText().toString();
        pwd = etPwd.getText().toString();
        confirmPwd = etConfirmPwd.getText().toString();
        user_name = etUserName.getText().toString();

        if(name.trim().length() > 0 && phNo.trim().length() > 0
                && emailId.trim().length() > 0&& proStu.trim().length() > 0
                && showWallCity.trim().length() > 0&& nativePlace.trim().length() > 0
                && residingCity.trim().length() > 0&& country.trim().length() > 0
                && DOB.trim().length() > 0&& pwd.trim().length() > 0
                && confirmPwd.trim().length() > 0&& user_name.trim().length() > 0){
            if(confirmPwd.equals(pwd)){
                loading = ProgressDialog.show(SignUp.this, "Processing...","Please Wait...",true,true);
                if(profileImgUrl!=null){
                    type = "sign_up_social";
                }else {
                    type = "sign_up";
                }
                BackgroundWorkerJson backgroundWorker = new BackgroundWorkerJson();
                backgroundWorker.execute();
            }else
                Toast.makeText(this,"Password does not match", Toast.LENGTH_LONG ).show();
        }else
            Toast.makeText(this,"Enter all the details", Toast.LENGTH_LONG ).show();
    }
    public class BackgroundWorkerJson extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                String post_data = null;
                String websiteUrl = null;
                if(type.equals("sign_up_social")){
                    websiteUrl = "http://nutzindia.com/SES_Wall/sign_up.php";
                    Log.e(type,type );

                    post_data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")
                            +"&"+ URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8")
                            +"&"+ URLEncoder.encode("pwd", "UTF-8") + "=" + URLEncoder.encode(pwd, "UTF-8")
                            +"&"+ URLEncoder.encode("dob", "UTF-8") + "=" + URLEncoder.encode(DOB, "UTF-8")
                            +"&"+ URLEncoder.encode("ph_no", "UTF-8") + "=" + URLEncoder.encode(phNo, "UTF-8")
                            +"&"+ URLEncoder.encode("email_id", "UTF-8") + "=" + URLEncoder.encode(emailId, "UTF-8")
                            +"&"+ URLEncoder.encode("show_wall_city", "UTF-8") + "=" + URLEncoder.encode(showWallCity, "UTF-8")
                            +"&"+ URLEncoder.encode("native_place", "UTF-8") + "=" + URLEncoder.encode(nativePlace, "UTF-8")
                            +"&"+ URLEncoder.encode("residing_city", "UTF-8") + "=" + URLEncoder.encode(residingCity, "UTF-8")
                            +"&"+ URLEncoder.encode("country", "UTF-8") + "=" + URLEncoder.encode(country, "UTF-8")
                            +"&"+ URLEncoder.encode("pro_r_stu", "UTF-8") + "=" + URLEncoder.encode(proStu, "UTF-8")
                            +"&"+ URLEncoder.encode("profileImgUrl", "UTF-8") + "=" + URLEncoder.encode(profileImgUrl, "UTF-8")
                    ;
                }
                if(type.equals("sign_up")){
                    websiteUrl = "http://nutzindia.com/SES_Wall/sign_up.php";
                    Log.e(type,type );

                    post_data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")
                            +"&"+ URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8")
                            +"&"+ URLEncoder.encode("pwd", "UTF-8") + "=" + URLEncoder.encode(pwd, "UTF-8")
                            +"&"+ URLEncoder.encode("dob", "UTF-8") + "=" + URLEncoder.encode(DOB, "UTF-8")
                            +"&"+ URLEncoder.encode("ph_no", "UTF-8") + "=" + URLEncoder.encode(phNo, "UTF-8")
                            +"&"+ URLEncoder.encode("email_id", "UTF-8") + "=" + URLEncoder.encode(emailId, "UTF-8")
                            +"&"+ URLEncoder.encode("show_wall_city", "UTF-8") + "=" + URLEncoder.encode(showWallCity, "UTF-8")
                            +"&"+ URLEncoder.encode("native_place", "UTF-8") + "=" + URLEncoder.encode(nativePlace, "UTF-8")
                            +"&"+ URLEncoder.encode("residing_city", "UTF-8") + "=" + URLEncoder.encode(residingCity, "UTF-8")
                            +"&"+ URLEncoder.encode("country", "UTF-8") + "=" + URLEncoder.encode(country, "UTF-8")
                            +"&"+ URLEncoder.encode("pro_r_stu", "UTF-8") + "=" + URLEncoder.encode(proStu, "UTF-8")
                            +"&"+ URLEncoder.encode("profileImgString", "UTF-8") + "=" + URLEncoder.encode(profileImgString, "UTF-8")
                    ;
                }

                URL url = new URL(websiteUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

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
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {

            if(type.equals("sign_up_social")||type.equals("sign_up")){
                if(result !=null){
                    Log.e(type,result );
                    loading.dismiss();
                    Toast.makeText(getApplicationContext(),result, Toast.LENGTH_LONG ).show();
                    Intent intent = new Intent(SignUp.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else
                    Log.e(type,"null");
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

            Geocoder geoCoder = new Geocoder(SignUp.this, Locale.getDefault());
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

                etNativePlace.setText(address.get(0).getLocality());
                etCountry.setText(address.get(0).getCountryName());
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
