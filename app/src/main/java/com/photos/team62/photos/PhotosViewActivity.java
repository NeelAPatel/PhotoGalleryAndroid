package com.photos.team62.photos;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import objects.Album;
import objects.Photo;
import objects.User;
import android.widget.AdapterView.OnItemClickListener;

public class PhotosViewActivity extends AppCompatActivity {

    private RecyclerView recPhotoView;
    private PhotosAdapter photosAdapter; // Adapters handle the actions of the layout

    //From Intent
    private User myUser;
    private int currAlbumIndex;

    //Created From Intent
    private Album currAlbum; //get by position
    private ArrayList<Album> arrAlbums;
    private ArrayList<Photo> arrCurrAlbumPhotos;


    //BLEHHH

    private static final int REQUEST_OPEN_RESULT_CODE = 0;
    //private ArrayList<Photo> photoList;

    @Override
    // WHEN PHOTOS VIEW IS CREATED...
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos_view);

        //== Graphics Setup
        //Adds toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //Retrieve from Intent
        Intent rcvIntent = getIntent();
        myUser = new User();
        myUser = (User) rcvIntent.getExtras().getSerializable("my_user");
        currAlbumIndex = rcvIntent.getExtras().getInt("album_pos");
        Log.d(">>>> ██PhotoAct76██ <<<<", "currAlbumIndex: " + currAlbumIndex);
        Log.d(">>>> ██PhotoAct77██ <<<<", "numOfAlbums: " + myUser.getMyAlbums().size());

        //Create from intent
        arrAlbums = myUser.getMyAlbums();
        currAlbum = myUser.getMyAlbums().get(currAlbumIndex);
        arrCurrAlbumPhotos = currAlbum.getAllPhotos();


        //Initialize Layout
        setTitle(currAlbum.getAlbumName() + "");
        initializeRecyclerView();
        initializeFloatingActionButton();
        photosAdapter.notifyItemInserted(0);


        /*
        for(Photo p: currAlbum.getAllPhotos()){
            photoList.add(p);
        }
        photosAdapter.notifyItemInserted(0);
        */
    }

    //INITIALIZATION
    public void initializeRecyclerView() {

        photosAdapter = new PhotosAdapter(this, myUser, currAlbumIndex, arrCurrAlbumPhotos);
        //Initialize recyclerview
        recPhotoView = (RecyclerView) findViewById(R.id.recPhotoView);
        RecyclerView.LayoutManager myLayoutManager = new GridLayoutManager(this, 2); //Creates a layout manager and tells it to have 2 columns
        recPhotoView.setLayoutManager(myLayoutManager);
        recPhotoView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recPhotoView.setItemAnimator(new DefaultItemAnimator());
        recPhotoView.setAdapter(photosAdapter);

    }



    private void initializeFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestImage(); //Native Android Image Picker
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.btnSlideshow:
                openSlideshow();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void openSlideshow(){

        if (currAlbum.getAllPhotos().size()> 0) {
            Context context = this;
            Intent intentSlide = new Intent(context, SlideshowActivity.class);

            Bundle extras = new Bundle();
            extras.putSerializable("photo_arr", (Serializable) arrCurrAlbumPhotos);
            intentSlide.putExtras(extras);

            this.startActivity(intentSlide);
        }
        else
        {
            Toast.makeText(this, "Cannot open slideshow. This Album has no pictures.", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // NATIVE ANDROID IMAGE THING
    // =========== HELPER METHODS ========================


    private void requestImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_OPEN_RESULT_CODE);
    }

    @Override // part of request image
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == REQUEST_OPEN_RESULT_CODE && resultCode == RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    Bitmap bitmap = getBitmapFromUri(uri);
                    insertImageToRecView(bitmap, uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return bitmap;
    }

    private void insertImageToRecView(Bitmap bitmap, Uri uri) {
        String fileName = getFileNameFromURI(uri);
        Photo p = new Photo(fileName, getTitle() + "", uri);
        arrCurrAlbumPhotos.add(p);
       // photosAdapter.notifyItemInserted(photoList.size());
        currAlbum.updatePhotosList(arrCurrAlbumPhotos);
        myUser.updateAlbumList(arrAlbums);
        myUser.serializeMyData();
        photosAdapter.notifyItemInserted(arrCurrAlbumPhotos.size()-1);
    }
    public String getFileNameFromURI(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    // ======= GRAPHICS SET UP METHODS =========

    // ==== RECYCLER VIEW RELATED METHODS


    /**
     * Gives equal margin around grid item
     */
    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        myUser.retrieveMyData();
    }
}
