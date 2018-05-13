package com.photos.team62.photos;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import objects.Album;
import objects.User;

public class MainActivity extends AppCompatActivity {
    /*
    Notes:
    - On almost all places where View object is displayed... I dont know why its there, but 90% of the time,
    things work because of it.

    - Avoid Lambdas when possible. If some code has lambda, its probably generated.
     */

    private int REQUEST_WRITE_STORAGE_REQUEST_CODE = 0;

    //Main Activity handles FAB action, Overflow menu in tab bar action, storage..?
    // ========== FIELDS
    private RecyclerView recView;
    public User myUser;

    // ==== Created from myUser
    private ArrayList<Album> arrAlbums;

    private AlbumsAdapter adapter; // Adapters handle the actions of the layout
    //private ArrayList<Album> albumList; //This is for the recycler view

    //private static final String TAG = "MainActivity";

    @Override
    // ON CREATION OF THIS PAGE...
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Calls superclass
        setContentView(R.layout.activity_main); // Attaches XML file
        setTitle("Albums");


        requestAppPermissions();


        // ===  Creates the appData.dat file if it doesn't exist
        Context context = this;
        File file = new File(context.getFilesDir(), "appData.dat");
        try{
            if (!file.exists()){
                file.createNewFile();
                myUser = new User();
            }
            else
            {
                // == Retrieve APPDATA from file.
                myUser = new User();
                myUser.retrieveMyData();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }


        // == Create Values from myUser:
        arrAlbums = myUser.getMyAlbums();

        // == SET UP GRAPHICS
        //Adds toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Add RecyclerView
        initializeRecyclerView();
        initializeFloatingActionButton();
        // User created to handle serialization
        adapter.notifyItemInserted(0);
        //Toast.makeText(context, "FirstStart #Albums: " + arrAlbums.size(), Toast.LENGTH_SHORT).show();
        //resetToStock();


    }

    //comes from floating action button
    private void createAlbum(String name) {
        Album a = new Album(name); // new Album is created
        arrAlbums.add(a);
        myUser.serializeMyData();


        recView.scrollToPosition(arrAlbums.size());
        adapter.notifyItemInserted(arrAlbums.size());

    }


    private void displayNewAlbumDialog(View view){
        //Only created this method so I don't get confused with extra stuff. plus acts like a template
        final View viewx = view;
        final EditText fieldNewAlbum = new EditText(MainActivity.this);
        fieldNewAlbum.setSingleLine(true);
        fieldNewAlbum.setHint("Album Name");
        AlertDialog dialogNewAlbum = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Add a new album")
                .setMessage("Provide a name for the new album")
                .setView(fieldNewAlbum)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = String.valueOf(fieldNewAlbum.getText());
                        if (name.length() > 0) {
                            createAlbum(name);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialogNewAlbum.show();
    }

    private void searchFunction(){


        if (myUser.getMyAlbums().size() > 0) {

            Intent intent = new Intent(this, SearchSelector.class);
            Bundle extras = new Bundle();
            extras.putSerializable("myUser", (Serializable) myUser);
            intent.putExtras(extras);
            this.startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Error: No albums to search from", Toast.LENGTH_SHORT).show();
        }

    }




    @Override
    protected void onRestart(){
        super.onRestart();
        //recView.setLayoutManager(myLayoutManager);
        //super.onCreate(mySavedInstanceState); // Calls superclass
        setContentView(R.layout.activity_main); // Attaches XML file
        setTitle("Albums");


        // TODO: Instead of re-displaying EVERYTHING, see if you can only change the values.
        // == Retrieve APPDATA from file.
        myUser = new User();
        myUser.retrieveMyData();


        // == Create Values from myUser:
        arrAlbums = myUser.getMyAlbums();

        // == SET UP GRAPHICS
        //Adds toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Add RecyclerView
        initializeRecyclerView();
        initializeFloatingActionButton();

        adapter.notifyItemInserted(0);
    }



    // ======= INITIALIZERS =========
    public void initializeRecyclerView(){
        adapter = new AlbumsAdapter(this, myUser); //Uses my array of albums to create the adapter

        //Initialize recyclerview
        recView = (RecyclerView) findViewById(R.id.rec_View);
        RecyclerView.LayoutManager myLayoutManager = new GridLayoutManager(this, 2); //Creates a layout manager and tells it to have 2 columns
        recView.setLayoutManager(myLayoutManager);
        recView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recView.setItemAnimator(new DefaultItemAnimator());
        recView.setAdapter(adapter);
    }

    //recyclerview helper methods
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

    public void initializeFloatingActionButton(){
        //Adds floating action buttton
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        // On click listeners listen for the element being tapped or long pressed or w/e
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // opens a dialog box to add a new album to
                displayNewAlbumDialog(view);
            }
        });
    }


    // ===== ACTION BAR BUTTON SETUP
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.search_button:
                searchFunction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // ==== PERMISSIONS
    private void requestAppPermissions() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (hasReadPermissions() && hasWritePermissions()) {
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, REQUEST_WRITE_STORAGE_REQUEST_CODE); // your request code
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }


}



