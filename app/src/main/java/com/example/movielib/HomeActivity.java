package com.example.movielib;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;


public class HomeActivity extends AppCompatActivity {

    int PERMISSION_ID = 44;
    ListView listView;
    DatabaseReference dRef,db,ab;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "HomeActivity";
    double M,S;
    int K=0;
    FirebaseListAdapter adapter;
    final String[] a = new String[100];
    final String[] b = new String[100];
    final String[] c = new String[100];
    final String[] d = new String[100];
    final String[] s = new String[100];
    final double[] f = new double[100];
    final double[] g = new double[100];
    final boolean[] x = new boolean[100];
    final int[] child = new int[100];
    final ImageView[] img = new ImageView[100];
    int u=0;
    Button A;
    int q = 0;
    String token;
    Timer timer = new Timer();

    String uid;
    private FusedLocationProviderClient mFusedLocationClient;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        getLastLocation();


        A = findViewById(R.id.plus);


        dRef = FirebaseDatabase.getInstance().getReference();
        ab = FirebaseDatabase.getInstance().getReference().child("Movie");
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();



        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("", "getInstanceId failed", task.getException());
                            return;
                        }
                        token = task.getResult().getToken();
                        dRef.child("Users").child(uid).child("token").setValue(token);
                        Log.d(TAG, "Profesor, el ID del dispositivo es: "+ token,task.getException());
                    }
                });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        listView = findViewById(R.id.listView);

        final Query query = FirebaseDatabase.getInstance().getReference().child("Movie");
        FirebaseListOptions<Data> options = new FirebaseListOptions.Builder<Data>().setLayout(R.layout.row).setQuery(query, Data.class).build();
        adapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(View v, Object model, int position) {
                TextView Title = (TextView)v.findViewById(R.id.Title);
                TextView Author = (TextView)v.findViewById(R.id.Author);
                ImageView image = v.findViewById(R.id.image);
                img[position] = image;
                 Data mv = (Data) model;
                Title.setText(mv.getTitle().toString());
                Author.setText(mv.getAuthor().toString());
                Picasso.with(HomeActivity.this).load(mv.getImg().toString()).into(image);
                    a[position] = mv.getTitle().toString();
                    b[position] = mv.getAuthor().toString();
                    c[position] = mv.getImg().toString();
                    d[position] = mv.getLink().toString();
                   s[position] = mv.getDate().toString();
                   f[position] = mv.getLat();
                     g[position] = mv.getLon();
                     x[position] = mv.isChecked();
                     K++;

            }
        };



        A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddActivity.class);
                startActivity(intent);

            }
        });


        TimerTask myTask = new TimerTask() {
            @Override
                public void run () {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    for (int i = 0; i < K; i++) {
                        try {
                            double a = distance(M, S, f[i], g[i]);
                            if (a < 1) {
                                ab.orderByChild("lat").equalTo(f[i]).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        db.child(uid).child(dataSnapshot.getKey()).child("near").setValue(true);

                                    }


                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });

                            }else{
                                ab.orderByChild("lat").equalTo(f[i]).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        db.child(uid).child(dataSnapshot.getKey()).child("near").setValue(false);

                                    }


                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    }   });   }
              };
        timer.schedule(myTask, 3000, 3000);


        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                    intent.putExtra("title", a[position]);
                    intent.putExtra("author", b[position]);
                    intent.putExtra("image", c[position]);
                    intent.putExtra("link", d[position]);
                intent.putExtra("date", s[position]);
                     intent.putExtra("latitude", f[position]);
                     intent.putExtra("longitude", g[position]);
                    startActivity(intent);
                }});
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, final int position, long id, boolean checked) {

                if (!checked){
                    try {
                    listView.getChildAt(position).setBackgroundColor(Color.parseColor("#FFFFFF"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                }
                if(checked){
                    try {
                        listView.getChildAt(position).setBackgroundColor(Color.parseColor("#B2291F"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try{child[u]=position;
                    u++;} catch (Exception e) {
                    e.printStackTrace();
                }
                q = position;
                adapter.notifyDataSetChanged();
                Query k = adapter.getRef(q).getParent();
                k.orderByChild("title").equalTo(a[q]).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable  String s) {
                        dataSnapshot.getRef().child("checked").setValue(true);
                        db.child(uid).child(dataSnapshot.getKey()).child("title").setValue(a[q]);

                    }
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }});
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.delete_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.addgeo:
                        Intent intent = new Intent(HomeActivity.this, MapsActivity.class);
                        intent.putExtra("title",a[q]);

                        startActivity(intent);
                        break;
                    case R.id.delete:

                        ab.orderByChild("title").equalTo(a[q]).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                               dataSnapshot.getRef().removeValue();
                                db.child(uid).child(dataSnapshot.getKey()).child("title").removeValue();


                            }
                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {



                            }});
                             break;
                        case R.id.share:
                            String msg = "I love this movie "+a[q];

                            Uri bmpUri = getLocalBitmapUri(img[q]);
                            Intent intentz = new Intent();
                            intentz.setAction(Intent.ACTION_SEND);
                            intentz.putExtra(Intent.EXTRA_TEXT, msg);
                            intentz.setType("text/plain");
                            intentz.putExtra(Intent.EXTRA_STREAM, bmpUri);
                            intentz.setType("image/jpeg");
                            intentz.setPackage("com.twitter.android");
                            startActivity(intentz);



                                break;
                            case R.id.delgeo:
                            Query k = adapter.getRef(q).getParent();
                             k.orderByChild("title").equalTo(a[q]).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                dataSnapshot.getRef().child("lat").setValue(38.3452);
                                dataSnapshot.getRef().child("lon").setValue(-0.4815);
                                Toast.makeText(HomeActivity.this,"geofencing eliminado",Toast.LENGTH_SHORT).show();


                            }


                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) { }}); }

                return false; }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
               while(u>=0){
                  try {
                      listView.getChildAt(child[u]).setBackgroundColor(Color.WHITE);
                   } catch (Exception e) {
                    e.printStackTrace();
                    }
                   u--;}
                      u=0;

                   try{
                  Query k = adapter.getRef(q).getParent();
                  k.orderByChild("checked").addChildEventListener(new ChildEventListener() {
                      @Override
                      public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                          dataSnapshot.getRef().child("checked").setValue(false);
                      }

                      @Override
                      public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                      }

                      @Override
                      public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                      }

                      @Override
                      public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError databaseError) {

                      }
                  });
              } catch (Exception e) {
                  e.printStackTrace();
              }

                adapter.notifyDataSetChanged();

            }
        });
    }
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // This way, you don't need to request external read/write permission.
            File file =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {

            e.printStackTrace();
        }
        return bmpUri;
    }



    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        K = 1;
        timer.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater(). inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    M = location.getLatitude();
                                    S = location.getLongitude();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            M = mLastLocation.getLatitude();
            S = mLastLocation.getLongitude();
        }
    };
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.about:
                Intent intent1 = new Intent(HomeActivity.this, HelpActivity.class);
                startActivity(intent1);
                break;
            case R.id.dec:
                mGoogleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(HomeActivity.this,"You are Logged Out",Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent2);

                    }
                });
                break;
            case R.id.desc:
                mGoogleSignInClient.revokeAccess().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(HomeActivity.this,"Your account has been disconnected",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent);

                    }
                });
        }
        return super.onOptionsItemSelected(item);
    }

}
