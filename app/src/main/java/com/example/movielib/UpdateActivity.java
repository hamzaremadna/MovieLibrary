package com.example.movielib;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateActivity extends AppCompatActivity {
    EditText T,A,L,D;
    ImageView image;
    Button C,CH,U;
    Patterns patt;
    String uid;
    String aTitle;
    boolean u = false;
    String j;
    String k;

    Data data;
    StorageReference mStorageRef;
    public Uri imageurl;
    private StorageTask uploadTask;
    DatabaseReference mDatabaseRef;
    DatabaseReference dRef;
    DatabaseReference mDataba;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        mStorageRef= FirebaseStorage.getInstance().getReference("Image");
        dRef = FirebaseDatabase.getInstance().getReference();


        T = findViewById(R.id.Titlle);
        A = findViewById(R.id.Authorr);
        L = findViewById(R.id.Link);
        D = findViewById(R.id.Date);
        C = findViewById(R.id.Cancel);
        CH = findViewById(R.id.choose);
        U = findViewById(R.id.Update);
        image = findViewById(R.id.imageView);
        Intent intent = getIntent();
        final String pic = intent.getStringExtra("image");
        aTitle = intent.getStringExtra("title");
        final String author = intent.getStringExtra("author");
        final String link = intent.getStringExtra("link");
        final String date = intent.getStringExtra("date");
        T.setText(aTitle);
        A.setText(author);
        L.setText(link);
        D.setText(date);
        if(pic != null){
            Picasso.with(UpdateActivity.this).load(pic.toString()).into(image);}


        data= new Data();

        C.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        CH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Filechooser();

            }
        });
        U.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String imageid;
                if(!T.getText().toString().trim().equals("") && !A.getText().toString().trim().equals("") && !D.getText().toString().trim().equals("")) {
                    if(patt.WEB_URL.matcher(L.getText().toString()).matches()){
                        if (imageurl != null) {
                            imageid = System.currentTimeMillis() + "." + getExtention(imageurl);
                            data.setTitle(T.getText().toString().trim());
                            data.setAuthor(A.getText().toString().trim());

                            data.setLink(L.getText().toString().trim());
                            data.setDate(D.getText().toString().trim());
                            data.setLon(-0.4815);
                            data.setLat(38.3452);

                            dRef.child("Movie").orderByChild("title").equalTo(aTitle).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
                                    final StorageReference Ref = mStorageRef.child(imageid);
                                    uploadTask = Ref.putFile(imageurl)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                    Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            String img = String.valueOf(uri).toString();
                                                            data.setImg(img);
                                                            dRef.child("Movie").child(dataSnapshot.getKey()).setValue(data);
                                                            dRef.child("Users").child(uid).child(dataSnapshot.getKey()).child("title").setValue(data.getTitle()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(UpdateActivity.this, "Movie Updated Succesfully", Toast.LENGTH_LONG).show();
                                                                    Intent intent = new Intent(UpdateActivity.this, HomeActivity.class);
                                                                    startActivity(intent);

                                                                }
                                                            });


                                                        }
                                                    });


                                                }
                                            });


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


                        } else {
                            data.setTitle(T.getText().toString().trim());
                            data.setAuthor(A.getText().toString().trim());

                            data.setLink(L.getText().toString().trim());
                            data.setDate(D.getText().toString().trim());
                            data.setLon(-0.4815);
                            data.setLat(38.3452);
                            dRef.child("Movie").orderByChild("title").equalTo(aTitle).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    data.setImg(pic);
                                    try {
                                        dRef.child("Users").child(uid).child(dataSnapshot.getKey()).child("title").setValue(data.getTitle());
                                        dRef.child("Movie").child(dataSnapshot.getKey()).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Toast.makeText(UpdateActivity.this, "Movie Updated Succesfully", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(UpdateActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                                dRef = null;

                                            }
                                        });


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
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
                            }); }


                    } else  Toast.makeText( UpdateActivity.this,"Add A Valid Link",Toast.LENGTH_LONG).show();}
                else Toast.makeText( UpdateActivity.this,"Fill all the gaps",Toast.LENGTH_LONG).show();


            }}
        );

    }
    private void Filechooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            imageurl=data.getData();
            image.setImageURI(imageurl);
        }


    }
    private String getExtention(Uri uri){
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }


}
