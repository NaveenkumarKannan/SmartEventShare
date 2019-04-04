package com.project.naveen.smarteventsshare;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class PostImageActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnTouchListener {
    ImageView viewImage;
    Button b, b1,b2,b3,b4,b5,b6,b7,b8,b9,b10,b11,b12,b13,b14,b15,b16,b17,b18;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;
    private float[] lastEvent = null;

    Bitmap postBitmap;
    String stringPostBitmap,userId,type,category;
    ProgressDialog loading;

    Spinner spinner;
    ArrayAdapter<CharSequence> adapter;
    EditText etComment;
    String comment;

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

        setContentView(R.layout.activity_post_image);

        b = (Button) findViewById(R.id.btnSelectPhoto);

        etComment = findViewById(R.id.etComment);
        viewImage = (ImageView) findViewById(R.id.viewImage);
        ImageView view = (ImageView) findViewById(R.id.viewImage);
        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        view.setOnTouchListener((View.OnTouchListener) this);

        b.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                selectImage();
            }

        });
        addListenerOnButton();

        spinner = (Spinner) findViewById(R.id.idSpinner);

        adapter = ArrayAdapter.createFromResource(this,R.array.Category,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = adapterView.getItemAtPosition(i).toString();

                //Toast.makeText(getContext(),BloodGroup,Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        startTracking();
    }
    public void addListenerOnButton() {
        b1 = (Button) findViewById(R.id.oo1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewImage.setBackgroundResource(R.drawable.oo1);
                Toast.makeText(getApplicationContext(), "OO001", Toast.LENGTH_SHORT).show();
            }
        });
        {
            b2 = (Button) findViewById(R.id.oo13);
            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage.setBackgroundResource(R.drawable.oo13);
                    Toast.makeText(getApplicationContext(),"OO013",Toast.LENGTH_SHORT).show();
                }
            });

        }
        {
            b3 = (Button) findViewById(R.id.oo14);
            b3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage.setBackgroundResource(R.drawable.oo14);
                    Toast.makeText(getApplicationContext(),"OO014",Toast.LENGTH_SHORT).show();
                }
            });

        }
        {
            b4 = (Button) findViewById(R.id.oo15);
            b4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage.setBackgroundResource(R.drawable.oo15);
                    Toast.makeText(getApplicationContext(),"OO015",Toast.LENGTH_SHORT).show();
                }
            });

        }
        {
            b5 = (Button) findViewById(R.id.oo11);
            b5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage.setBackgroundResource(R.drawable.oo11);
                    Toast.makeText(getApplicationContext(),"OO011",Toast.LENGTH_SHORT).show();
                }
            });

        }
        {
            b6 = (Button) findViewById(R.id.oo6);
            b6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage.setBackgroundResource(R.drawable.oo6);
                    Toast.makeText(getApplicationContext(),"OO006",Toast.LENGTH_SHORT).show();
                }
            });

        }
        {
            b7 = (Button) findViewById(R.id.oo7);
            b7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage.setBackgroundResource(R.drawable.oo7);
                    Toast.makeText(getApplicationContext(),"OO007",Toast.LENGTH_SHORT).show();
                }
            });

        }
        {
            b8 = (Button) findViewById(R.id.ff2);
            b8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage.setBackgroundResource(R.drawable.ff2);
                    Toast.makeText(getApplicationContext(),"OO008",Toast.LENGTH_SHORT).show();
                }
            });

        }
        {
            b9 = (Button) findViewById(R.id.ff6);
            b9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage.setBackgroundResource(R.drawable.ff6);
                    Toast.makeText(getApplicationContext(),"OO009",Toast.LENGTH_SHORT).show();
                }
            });

        }
        {
            b10 = (Button) findViewById(R.id.ff11);
            b10.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage.setBackgroundResource(R.drawable.ff11);
                    Toast.makeText(getApplicationContext(),"OO010",Toast.LENGTH_SHORT).show();
                }
            });

        }
        {
            b11 = (Button) findViewById(R.id.ff13);
            b11.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage.setBackgroundResource(R.drawable.ff13);
                    Toast.makeText(getApplicationContext(),"OO011",Toast.LENGTH_SHORT).show();
                }
            });

        }
        {
            b12 = (Button) findViewById(R.id.ro7);
            b12.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage.setBackgroundResource(R.drawable.ro7);
                    Toast.makeText(getApplicationContext(),"OO012",Toast.LENGTH_SHORT).show();
                }
            });

        }
        /*
        {
            b13 = (Button) findViewById(R.id.ro3);
            b13.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage.setBackgroundResource(R.drawable.ro3);
                    Toast.makeText(getApplicationContext(),"OO013",Toast.LENGTH_SHORT).show();
                }
            });

        }
        {
            b14 = (Button) findViewById(R.id.rc17);
            b14.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage.setBackgroundResource(R.drawable.rc17);
                    Toast.makeText(getApplicationContext(),"OO014",Toast.LENGTH_SHORT).show();
                }
            });

        }
        {
            b15 = (Button) findViewById(R.id.rc18);
            b15.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage.setBackgroundResource(R.drawable.rc18);
                    Toast.makeText(getApplicationContext(),"OO015",Toast.LENGTH_SHORT).show();
                }
            });

        }
        {
            b16 = (Button) findViewById(R.id.rc20);
            b16.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage.setBackgroundResource(R.drawable.rc20);
                    Toast.makeText(getApplicationContext(),"OO016",Toast.LENGTH_SHORT).show();
                }
            });

        }
        {
            b17 = (Button) findViewById(R.id.hf1);
            b17.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage.setBackgroundResource(R.drawable.hf1);
                    Toast.makeText(getApplicationContext(),"OO017",Toast.LENGTH_SHORT).show();
                }
            });

        }
        {
            b17 = (Button) findViewById(R.id.hf4);
            b17.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewImage.setBackgroundResource(R.drawable.hf4);
                    Toast.makeText(getApplicationContext(),"OO017",Toast.LENGTH_SHORT).show();
                }
            });

        }
        */


    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        // Dump touch event to log
        dumpEvent(event);

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: //first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.e(TAG, "mode=DRAG");
                mode = DRAG;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                lastEvent = new float[4];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);
                d = rotation(event);
                break;

            case MotionEvent.ACTION_UP: //first finger lifted
            case MotionEvent.ACTION_POINTER_UP: //second finger lifted
                mode = NONE;
                Log.e(TAG, "mode=NONE");
                break;


            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    // ...
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                            - start.y);
                } else if (mode == ZOOM && event.getPointerCount() == 2) {
                    float newDist = spacing(event);
                    matrix.set(savedMatrix);
                    if (newDist > 10f) {
                        scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                    if (lastEvent != null) {
                        newRot = rotation(event);
                        float r = newRot - d;
                        matrix.postRotate(r, view.getMeasuredWidth() / 2,
                                view.getMeasuredHeight() / 2);
                    }
                }
                break;

        }
        // Perform the transformation
        view.setImageMatrix(matrix);

        return true; // indicate event was handled

    }

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);

        return (float) Math.toDegrees(radians);
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);

    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);

    }

    /**
     * Show an event in the LogCat view, for debugging
     */

    private void dumpEvent(MotionEvent event) {
        String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(
                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");

        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())

                sb.append(";");
        }

        sb.append("]");
        Log.e(TAG, sb.toString());

    }

    private void selectImage() {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(PostImageActivity.this);

        builder.setTitle("Add Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo"))

                {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/JustTry";
                    File file = new File(path);
                    if(!file.exists()){
                        file.mkdirs();
                    }

                    File f = new File(file, "temp.jpg");

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

                    startActivityForResult(intent, 1);

                } else if (options[item].equals("Choose from Gallery"))

                {

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(intent, 2);


                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                String pathDir,file_name,externalPath;
                externalPath= Environment.getExternalStorageDirectory().getAbsolutePath();
                file_name = "/JustTry/temp.jpg";
                pathDir = externalPath+file_name;

                Bitmap bitmap = BitmapFactory.decodeFile(pathDir);
                FileOutputStream out = null;
                try {
                    File file;
                    file =new File(externalPath+"/JustTry","temp.jpg");
                    out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 25, out); // bmp is your Bitmap instance

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
                bitmap = BitmapFactory.decodeFile(pathDir);
                try {

                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                    viewImage.setImageDrawable(drawable);
                    String path = Environment

                            .getExternalStorageDirectory()

                            + File.separator

                            + "Phoenix" + File.separator + "default";

                    //f.delete();

                    OutputStream outFile = null;

                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");

                    try {

                        outFile = new FileOutputStream(file);

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);

                        outFile.flush();

                        outFile.close();

                    } catch (FileNotFoundException e) {

                        e.printStackTrace();

                    } catch (IOException e) {

                        e.printStackTrace();

                    } catch (Exception e) {

                        e.printStackTrace();

                    }

                } catch (Exception e) {

                    e.printStackTrace();

                }

            } else if (requestCode == 2) {


                Uri selectedImage = data.getData();

                String[] filePath = {MediaStore.Images.Media.DATA};

                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);

                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.e("path of image", picturePath + "");

                Drawable drawable = new BitmapDrawable(getResources(), thumbnail);
                viewImage.setImageDrawable(drawable);
            }
        }


    }

    public void saveImage(View view) {
        takeScreenshot();
    }
    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/JustTry";
            File file = new File(mPath);
            if(!file.exists()){
                file.mkdirs();
            }
            // create bitmap screen capture
            //View v1 = getWindow().getDecorView().getRootView();
            ImageView v1 = viewImage;
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(file,now + ".jpg");

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            //openScreenshot(imageFile);
            postImage(bitmap);
        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

    private void postImage(Bitmap bitmap) {
        if(district!=null){
            comment = etComment.getText().toString();
            postBitmap = bitmap;
            if(postBitmap!=null){
                SessionManager sessionManager = new SessionManager(this);
                // get user data from session
                HashMap<String, String> user = sessionManager.getUserDetails();
                // name
                userId = user.get(SessionManager.KEY_ID);

                if(category.equals("Select Category")){
                    Toast.makeText(getApplicationContext(),"Select Category", Toast.LENGTH_LONG).show();
                }else{
                    if(comment.trim().length() > 0){
                        loading = ProgressDialog.show(PostImageActivity.this, "Processing...","Please Wait...",true,true);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        postBitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream .toByteArray();

                        stringPostBitmap = Base64.encodeToString(byteArray, Base64.DEFAULT);
                        Log.e("stringPostBitmap",stringPostBitmap );

                        type = "post";
                        BackgroundWorker backgroundWorker = new BackgroundWorker();
                        backgroundWorker.execute();
                    }else {
                        Toast.makeText(getApplicationContext(),"Enter Caption", Toast.LENGTH_LONG).show();
                    }
                }

            }else {
                Toast.makeText(getApplicationContext(),"bitmap null", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"Wait for a minute... and Try again...", Toast.LENGTH_LONG).show();
        }
    }

    private void openScreenshot(File imageFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageFile);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    public class BackgroundWorker extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String post_data = null;
                String login_url = null;

                if(type.equals("post")) {
                    login_url = "http://nutzindia.com/SES_Wall/post.php";
                    post_data = URLEncoder.encode("user_name","UTF-8")+"="+ URLEncoder.encode(userId,"UTF-8")
                            +"&"+ URLEncoder.encode("post_image_string","UTF-8")+"="+ URLEncoder.encode(stringPostBitmap,"UTF-8")
                            +"&"+ URLEncoder.encode("comment","UTF-8")+"="+ URLEncoder.encode(comment,"UTF-8")
                            +"&"+ URLEncoder.encode("category","UTF-8")+"="+ URLEncoder.encode(category,"UTF-8")
                            +"&"+ URLEncoder.encode("district","UTF-8")+"="+ URLEncoder.encode(district,"UTF-8")
                    ;
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
            if(type.equals("post")) {
                if(result!=null){
                    loading.dismiss();
                    Toast.makeText(PostImageActivity.this, result, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(PostImageActivity.this, TrendingWall.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else
                    Toast.makeText(PostImageActivity.this, "result null", Toast.LENGTH_LONG).show();
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

            Geocoder geoCoder = new Geocoder(PostImageActivity.this, Locale.getDefault());
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