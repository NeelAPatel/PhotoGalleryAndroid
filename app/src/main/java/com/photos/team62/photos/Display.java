package com.photos.team62.photos;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import objects.Album;
import objects.Photo;
import objects.User;


public class Display extends AppCompatActivity {
    //Fields upon creation

    private User myUser;
    private int currPhotoIndex;
    private int currAlbumIndex;

    //Fields derived
    private ArrayList<Album> arrAlbums;
    private Album currAlbum;
    private ArrayList<Photo> arrPhotos;
    private ArrayList<String> arrTagsPerson;
    private ArrayList<String> arrTagsLocation;
    private Photo p;

    //Elements
    private ImageView dispImage;
    private Button btnEditSave;
    private Button btnCancel;
    private EditText tfPersonTags, tfLocationTags;
    private TextView lblTagHeader;

    private String oriPerson;
    private String oriLocation;

    private boolean isStateEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_view);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent rcvIntent = getIntent();
        //myUser = new User();
        myUser = (User) rcvIntent.getExtras().getSerializable("my_user");
        currPhotoIndex = rcvIntent.getExtras().getInt("photo_index");
        currAlbumIndex = rcvIntent.getExtras().getInt("album_index");

        Log.d(">>>> ██PhotoAct76██ <<<<", "currAlbumIndex: " + currAlbumIndex);
        Log.d(">>>> ██PhotoAct77██ <<<<", "numOfAlbums: " + myUser.getMyAlbums().size());

        currAlbum = myUser.getMyAlbums().get(currAlbumIndex);

        // Initialize Values
        initializeElements();
        displayImage();
        populateTagsField();
        setButtonAction();
        btnVisibility();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
    private void initializeElements(){
        dispImage = (ImageView) findViewById(R.id.dispImageView);
        btnEditSave = (Button) findViewById(R.id.btnEditSave);
        tfPersonTags = (EditText) findViewById(R.id.tfDispPersonTags);
        tfLocationTags = (EditText) findViewById(R.id.tfDispLocationTags);
        lblTagHeader = (TextView) findViewById(R.id.lblTagHeader);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        isStateEdit = false;
    }
    private void displayImage() {
        p = currAlbum.getAllPhotos().get(currPhotoIndex);
        arrTagsPerson = p.getArrPersonTags();
        arrTagsLocation = p.getArrLocationTags();
        Uri temp =Uri.parse(p.getFilePath());
        setTitle(p.getFileName() + "");
        dispImage.setImageURI(temp);
        dispImage.setFocusedByDefault(true);
    }
    private void populateTagsField(){

        Log.d(">>>> ██Disp96██ <<<<", "persons: " + arrTagsPerson.size());
        Log.d(">>>> ██Disp96██ <<<<", "location: " + arrTagsLocation.size());
        tfPersonTags.setText("");
        tfLocationTags.setText("");

        String currStr = "";
        if (arrTagsPerson.size() > 0){

            for(String str: arrTagsPerson){
                currStr = currStr + str + ";";
                tfPersonTags.setText(currStr);
            }
        }
        currStr = "";
        if (arrTagsLocation.size() > 0){

            for(String str: arrTagsLocation){
                currStr = currStr + str + ";";
                tfLocationTags.setText(currStr);
            }
        }
    }
    private void setButtonAction(){
        btnEditSave.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(isStateEdit){
                    //Save changes
                    saveTags();
                    isStateEdit = false;

                }
                else
                {
                    // Enable
                    oriPerson = tfPersonTags.getText().toString();
                    oriLocation = tfLocationTags.getText().toString();
                    isStateEdit = true;
                }

                btnVisibility();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tfPersonTags.setText(oriPerson);
                tfLocationTags.setText(oriLocation);
                isStateEdit = false;
                btnVisibility();
            }

        });
    }
    private void btnVisibility(){
        if (isStateEdit){
            btnEditSave.setText("Save Changes");
            tfPersonTags.setEnabled(true);
            tfPersonTags.setHint("None - Add your tags");
            tfLocationTags.setEnabled(true);
            tfLocationTags.setHint("None - Add your tags");
            lblTagHeader.setText("Image Tags - Seperate tags using ;");
            btnCancel.setEnabled(true);
        }
        else
        {
            btnEditSave.setText("Edit Tags");
            tfPersonTags.setEnabled(false);
            tfPersonTags.setHint("None");
            tfLocationTags.setEnabled(false);
            tfLocationTags.setHint("None");
            lblTagHeader.setText("Image Tags");
            btnCancel.setEnabled(false);
        }
    }


    private void saveTags(){
        //Remove \n
        //Change "; " to ";"
        //tokenize
        //trim
        // remove duplicates
        // store
        // Serialize
        Log.d(">>>> ██======TAG SAVE======██ <<<<", "=======TAG SAVE=======");


        String oriPersonStr = tfPersonTags.getText() + "";
        String oriLocationStr = tfLocationTags.getText() + "";
        Log.d(">>>> ██=SAVE=██ <<<<", "person: " + oriPersonStr);
        Log.d(">>>> ██=SAVE=██ <<<<", "location: " + oriLocationStr);




        oriPersonStr = oriPersonStr.replace("\n", "").replace("\r","");
        oriLocationStr = oriLocationStr.replace("\n", "").replace("\r","");

        oriPersonStr = oriPersonStr.replace("; ", ";");
        oriLocationStr = oriLocationStr.replace("; ", ";");



        ArrayList<String> tempPersonTags = new ArrayList<String>(Arrays.asList(oriPersonStr.split(";")));
        ArrayList<String> tempLocationTags = new ArrayList<String>(Arrays.asList(oriLocationStr.split(";")));


        arrTagsPerson = new ArrayList<String>();
        arrTagsLocation = new ArrayList<String>();
        for (String x: tempPersonTags){

            if (x.trim().equals(";") || x.trim().equals("")){

            }
            else {
                if (!arrTagsPerson.contains(x.trim())) {
                    arrTagsPerson.add(x.trim());
                }
            }
        }


        for (String x: tempLocationTags){
            if (x.trim().equals(";")|| x.trim().equals("")){

            }
            else {
                if (!arrTagsLocation.contains(x.trim())) {
                    arrTagsLocation.add(x.trim());
                }
            }
        }

        if (arrTagsPerson.size() == 1 && (arrTagsPerson.get(0).equals(""))){
            arrTagsPerson.remove(0);
        }
        if (arrTagsLocation.size() == 1 && (arrTagsLocation.get(0).equals(""))){
            arrTagsLocation.remove(0);
        }



        p.setArrPersonTags(arrTagsPerson);
        p.setArrLocationTags(arrTagsLocation);
        populateTagsField();
        myUser.serializeMyData();
        Log.d(">>>> ██======TAG SAVE======██ <<<<", "=======TAG SAVE=======");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
