package com.example.movielib;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AddActivity extends AppCompatActivity {
    EditText T,A,L,D;
    ImageView image;
    Button S,C,CH;
    Patterns patt;

    String uid;
    String aTitle;
    boolean u = false;
    String j;
    String k;
    Timer timer = new Timer();

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
            setContentView(R.layout.activity_add);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            uid = user.getUid();
            mStorageRef= FirebaseStorage.getInstance().getReference("Image");
            mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Movie");
        mDataba = FirebaseDatabase.getInstance().getReference().child("Users");

            T = findViewById(R.id.Titlle);
            A = findViewById(R.id.Authorr);
            L = findViewById(R.id.Link);
            D = findViewById(R.id.Date);
            S = findViewById(R.id.Save);
            C = findViewById(R.id.Cancel);
            CH = findViewById(R.id.choose);
            image = findViewById(R.id.imageView);
               TimerTask myTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    dRef.child("Movie").orderByChild("title").equalTo(T.getText().toString()).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            j = dataSnapshot.child("title").getValue().toString();

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
            }
        };

        timer.schedule(myTask, 1000, 1000);
            data= new Data();

                C.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AddActivity.this, HomeActivity.class);
                        startActivity(intent);
                    }
                });
            CH.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Filechooser();

                }
            });
        S.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(uploadTask != null && uploadTask.isInProgress()){
                        Toast.makeText(AddActivity.this,"Upload in progress",Toast.LENGTH_LONG).show();

                    }else {

                    Fileuploader();

                    }

                }
            });





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

    private void Fileuploader(){

        if (imageurl != null){
            String a = T.getText().toString().trim();
            String b = A.getText().toString().trim();
            String c = D.getText().toString().trim();
        if(patt.WEB_URL.matcher(L.getText().toString()).matches()){
            String imageid;
            imageid = System.currentTimeMillis()+"."+getExtention(imageurl);
            data.setTitle(T.getText().toString().trim());
            data.setAuthor(A.getText().toString().trim());
             k = data.getTitle();
        data.setLink(L.getText().toString().trim());
        data.setDate(D.getText().toString().trim());
            data.setLon(-0.4815);
            data.setLat(38.3452);
            try{
            if(!j.equals(T.getText().toString()) )  {
            final StorageReference Ref=mStorageRef.child(imageid);
              uploadTask=Ref.putFile(imageurl)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                     Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                         @Override
                         public void onSuccess(Uri uri) {
                             String img = String.valueOf(uri).toString();
                             data.setImg(img);

                             mDatabaseRef.push().setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void aVoid) {

                                     mDatabaseRef.orderByChild("title").equalTo(k).addChildEventListener(new ChildEventListener() {
                                         @Override
                                         public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                            mDataba.child(uid).child(dataSnapshot.getKey()).child("title").setValue(k).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(AddActivity.this, "Movie Uploaded Succesfully", Toast.LENGTH_LONG).show();
                                                    Intent intent = new Intent(AddActivity.this, HomeActivity.class);
                                                    startActivity(intent);
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


                                 }
                             });



                         }
                     });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });} else Toast.makeText( AddActivity.this,"This movie already exist",Toast.LENGTH_LONG).show();} catch (Exception e) {
                final StorageReference Ref=mStorageRef.child(imageid);
                uploadTask=Ref.putFile(imageurl)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String img = String.valueOf(uri).toString();
                                        data.setImg(img);

                                        mDatabaseRef.push().setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mDatabaseRef.orderByChild("title").equalTo(k).addChildEventListener(new ChildEventListener() {
                                                    @Override
                                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                        mDataba.child(uid).child(dataSnapshot.getKey()).child("title").setValue(k).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(AddActivity.this, "Movie Uploaded Succesfully", Toast.LENGTH_LONG).show();
                                                                Intent intent = new Intent(AddActivity.this, HomeActivity.class);
                                                                startActivity(intent);
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


                                            }
                                        });



                                    }
                                });


                            }
                        });

            }
        } else  Toast.makeText( AddActivity.this,"Add A Valid Link",Toast.LENGTH_LONG).show();
        } else{ Toast.makeText( AddActivity.this,"You have to add a picture",Toast.LENGTH_LONG).show(); }



    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }
}
