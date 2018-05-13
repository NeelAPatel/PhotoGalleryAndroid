package com.photos.team62.photos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import objects.Photo;

public class SlideshowActivity extends AppCompatActivity {

    //Fields from Intent
    private ArrayList<Photo> arrPhotos;


    private ImageView imgView;
    private Button btnLeft, btnRight;
    private TextView lblPhotoCount;
    int currIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        //== Graphics Setup
        //Adds toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Slideshow");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Retrieve from Intent
        Intent rcv = getIntent();
        arrPhotos = (ArrayList<Photo>) rcv.getExtras().getSerializable("photo_arr");

        //InitializeElements
        initializeElements();

        runSlideshow();

        buttonVisibility();

    }

    private void buttonVisibility() {
        if (arrPhotos.size() <= 1){
            btnLeft.setEnabled(false);
            btnRight.setEnabled(false);
        }
        else if (currIndex == arrPhotos.size()-1){
            btnLeft.setEnabled(true);
            btnRight.setEnabled(false);
        }
        else if (currIndex == 0){
            btnLeft.setEnabled(false);
            btnRight.setEnabled(true);
        }
        else {
            btnLeft.setEnabled(true);
            btnRight.setEnabled(true);
        }

    }

    private void runSlideshow() {
        //Set Label
        lblPhotoCount.setText((currIndex +1) + "/" + arrPhotos.size());

        //SetFirstImage
        imgView.setImageURI(Uri.parse(arrPhotos.get(currIndex).getFilePath()));

        //Add Left button onClick
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if  (currIndex == 0){
                    currIndex = arrPhotos.size()-1;
                }else{
                    currIndex--;}

                imgView.setImageURI(Uri.parse(arrPhotos.get(currIndex).getFilePath()));
                lblPhotoCount.setText((currIndex +1) + "/" + arrPhotos.size());
                buttonVisibility();
            }
        });


        //Add Right button onClick
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if  (currIndex == arrPhotos.size()-1){
                    currIndex = 0;
                }else{
                    currIndex++;}
                imgView.setImageURI(Uri.parse(arrPhotos.get(currIndex).getFilePath()));
                lblPhotoCount.setText((currIndex +1) + "/" + arrPhotos.size());
                buttonVisibility();
            }
        });

    }

    private void initializeElements(){
        lblPhotoCount = (TextView) findViewById(R.id.lblPhotoCount);
        imgView = (ImageView) findViewById(R.id.photoFull);
        btnRight = (Button) findViewById(R.id.btnRight);
        btnLeft = (Button) findViewById(R.id.btnLeft);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
