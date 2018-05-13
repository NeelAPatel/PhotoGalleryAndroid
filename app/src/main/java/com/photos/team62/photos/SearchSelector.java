package com.photos.team62.photos;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;

import objects.User;

public class SearchSelector extends AppCompatActivity {

    private User myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_selector);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Select Search type");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent rcv = getIntent();
        myUser = (User) rcv.getExtras().getSerializable("myUser");


        Button btnPersonSearch = findViewById(R.id.btnPersonSearch);
        btnPersonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),PersonSearch.class );
                Bundle extras = new Bundle();
                extras.putSerializable("myUser", (Serializable) myUser);
                intent.putExtras(extras);
                v.getContext().startActivity(intent);
            }
        });


        Button btnLocationSearch = findViewById(R.id.btnLocationSearch);
        btnLocationSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),LocationSearch.class );
                Bundle extras = new Bundle();
                extras.putSerializable("myUser", (Serializable) myUser);
                intent.putExtras(extras);
                v.getContext().startActivity(intent);
            }
        });

        Button btnSearchBoth = findViewById(R.id.btnSearchBoth);
        btnSearchBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),BothSearch.class );
                Bundle extras = new Bundle();
                extras.putSerializable("myUser", (Serializable) myUser);
                intent.putExtras(extras);
                v.getContext().startActivity(intent);
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
