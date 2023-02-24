package com.example.movielib;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {
    Button A,B,C,D;
    ImageView image;
    TextView title, description,date;
    DatabaseReference dRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
         dRef = FirebaseDatabase.getInstance().getReference();
        A = findViewById(R.id.button3);

        B = findViewById(R.id.button);
        C = findViewById(R.id.button2);
        C.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
        image = findViewById(R.id.another_imageView);
        title = findViewById(R.id.titleText);
        description = findViewById(R.id.descriptionText);
        date = findViewById(R.id.date);

        Intent intent = getIntent();

        final String pic = intent.getStringExtra("image");
        final String aTitle = intent.getStringExtra("title");
        final String author = intent.getStringExtra("author");
        final String ate = intent.getStringExtra("date");
        final double latitude = intent.getDoubleExtra("latitude",38.3452);
        final double longitude = intent.getDoubleExtra("longitude",-0.4815);
        final String link = intent.getStringExtra("link");

        A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UpdateActivity.class);
                intent.putExtra("title", aTitle);
                intent.putExtra("author", author);
                intent.putExtra("image", pic);
                intent.putExtra("link",link);
                intent.putExtra("date",ate);
                startActivity(intent);
            }
        });
        B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(browserIntent);            }
        });
      Picasso.with(DetailsActivity.this).load(pic.toString()).into(image);
        title.setText(aTitle);
        description.setText(author);
        date.setText(ate);

        D = findViewById(R.id.button7);
        if (latitude == 38.3452){D.setVisibility(View.GONE);}

        D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity2.class);
                intent.putExtra("title",aTitle);
                intent.putExtra("author", author);
                intent.putExtra("date",ate);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
            }
        });

    }


}
