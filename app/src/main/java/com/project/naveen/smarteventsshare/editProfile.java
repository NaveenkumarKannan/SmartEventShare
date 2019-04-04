package com.project.naveen.smarteventsshare;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.plus.model.people.Person;
import com.project.naveen.smarteventsshare.socialintegration.constants.AppConstants;
import com.project.naveen.smarteventsshare.socialintegration.helpers.FbConnectHelper;
import com.project.naveen.smarteventsshare.socialintegration.helpers.GooglePlusSignInHelper;
import com.project.naveen.smarteventsshare.socialintegration.managers.SharedPreferenceManager;
import com.project.naveen.smarteventsshare.socialintegration.model.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
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
import java.util.HashMap;

import butterknife.ButterKnife;

public class editProfile extends AppCompatActivity{
    TextView tvDOB,tvUserId;
    String type;

    ProgressDialog loading;

    EditText etName,etPhNo,etEmail,etProStu,etShowWallCity,etNativePlace,etResidingCity,etCountry,etUserName;

    String user_id,name,phNo,emailId,proStu,showWallCity,nativePlace,residingCity,country,
            DOB,user_name;
    String profileImgUrl,profileImgPath,profileImgString;
    Bitmap profileImgBitmap;
    SimpleDraweeView profileImg;
    private int PICK_IMAGE = 100,RC_SIGN_IN = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etName= findViewById(R.id.etName);
        etPhNo= findViewById(R.id.etPhNo);
        etEmail= findViewById(R.id.etEmail);
        etProStu= findViewById(R.id.etProStu);
        etShowWallCity= findViewById(R.id.etShowWallCity);
        etNativePlace= findViewById(R.id.etNativePlace);
        etResidingCity= findViewById(R.id.etResidingCity);
        etCountry= findViewById(R.id.etCountry);
        etUserName = findViewById(R.id.etUserName);

        ButterKnife.bind(this);
        profileImg = ButterKnife.findById(this,R.id.user_imageview);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        tvUserId = findViewById(R.id.tvUserId);
        tvDOB= findViewById(R.id.tvDOB);
        tvDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int day, month, year;
                Calendar calendar = Calendar.getInstance();
                day = calendar.get(Calendar.DAY_OF_MONTH);
                month = calendar.get(Calendar.MONTH);
                year = calendar.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog = new DatePickerDialog(editProfile.this, new DatePickerDialog.OnDateSetListener() {
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

        SessionManager sessionManager = new SessionManager(this);
        // get user data from session
        HashMap<String, String> user = sessionManager.getUserDetails();
        // name
        user_name = user.get(SessionManager.KEY_ID);
        type = "getProfile";
        BackgroundWorkerJson backgroundWorker = new BackgroundWorkerJson();
        backgroundWorker.execute();
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    public void goBack(View view) {
        finish();
    }
    public void onEdit(View view) {
        name = etName.getText().toString();
        phNo = etPhNo.getText().toString();
        emailId = etEmail.getText().toString();
        proStu = etProStu.getText().toString();
        showWallCity = etShowWallCity.getText().toString();
        nativePlace = etNativePlace.getText().toString();
        residingCity = etResidingCity.getText().toString();
        country = etCountry.getText().toString();
        DOB = tvDOB.getText().toString();
        user_name = etUserName.getText().toString();

        if(name.trim().length() > 0 && phNo.trim().length() > 0
                && emailId.trim().length() > 0&& proStu.trim().length() > 0
                && showWallCity.trim().length() > 0&& nativePlace.trim().length() > 0
                && residingCity.trim().length() > 0&& country.trim().length() > 0
                && DOB.trim().length() > 0&& user_name.trim().length() > 0){
            loading = ProgressDialog.show(editProfile.this, "Processing...","Please Wait...",true,true);
            type = "editProfile";
            BackgroundWorkerJson backgroundWorker = new BackgroundWorkerJson();
            backgroundWorker.execute();
        }else
            Toast.makeText(this,"Enter all the details", Toast.LENGTH_LONG ).show();
    }
    public class BackgroundWorkerJson extends AsyncTask<String,Void,String> {
        String json_string;
        JSONArray jsonArray;
        JSONObject jsonObject;
        @Override
        protected String doInBackground(String... params) {

            try {
                String post_data = null;
                String websiteUrl = null;
                if(type.equals("getProfile")){
                    websiteUrl = "http://nutzindia.com/SES_Wall/getProfile.php";
                    Log.e(type,type );

                    post_data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(" ", "UTF-8")
                            +"&"+ URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8")
                    ;
                }
                if(type.equals("editProfile")){
                    websiteUrl = "http://nutzindia.com/SES_Wall/editProfile.php";
                    Log.e(type,type );

                    post_data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")
                            +"&"+ URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8")
                            +"&"+ URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(user_name, "UTF-8")
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

            if(type.equals("editProfile")){
                if(result !=null){
                    Log.e(type,result );
                    loading.dismiss();
                    Toast.makeText(getApplicationContext(),result, Toast.LENGTH_LONG ).show();
                    finish();
                    startActivity(getIntent());
                }
                else
                    Log.e(type,"null");
            }
            if(type.equals("getProfile")) {
                json_string = result;

                if(json_string != null)
                {
                    Log.e(type,result );
                    try {
                        jsonObject = new JSONObject(json_string);
                        jsonArray = jsonObject.getJSONArray("getProfile");

                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            String user_name,name,dob,ph_no,email_id,show_wall_city,native_place,residing_city,country,pro_r_stu,profileImgUrl;
                            user_id = jo.getString("user_id");
                            user_name = jo.getString("user_name");
                            name = jo.getString("name");
                            dob = jo.getString("dob");
                            profileImgUrl = jo.getString("profileImgUrl");
                            ph_no = jo.getString("ph_no");
                            email_id = jo.getString("email_id");
                            show_wall_city = jo.getString("show_wall_city");
                            native_place = jo.getString("native_place");
                            residing_city = jo.getString("residing_city");
                            country = jo.getString("country");
                            pro_r_stu = jo.getString("pro_r_stu");

                            tvUserId.setText(user_id);
                            etUserName.setText(user_name);
                            etName.setText(name);
                            tvDOB.setText(dob);
                            etPhNo.setText(ph_no);
                            etEmail.setText(email_id);
                            etShowWallCity.setText(show_wall_city);
                            etNativePlace.setText(native_place);
                            etResidingCity.setText(residing_city);
                            etCountry.setText(country);
                            etProStu.setText(pro_r_stu);
                            profileImg.setImageURI(Uri.parse(profileImgUrl));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
