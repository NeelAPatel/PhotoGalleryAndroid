package com.photos.team62.photos;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import objects.Album;
import objects.Photo;
import objects.User;

public class LocationSearch extends AppCompatActivity{

    private User myUser;
    private ArrayList<Photo> arrAllPhotos;
    private RecyclerView recLocationSearch;
    private LocationSearchAdapter mAdapter;
    private String searchQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent rcv = getIntent();
        myUser = new User();
        //myUser = (User) rcv.getExtras().getSerializable("myUser");

        myUser.retrieveMyData();
        arrAllPhotos = new ArrayList<Photo>();
        for(Album a: myUser.getMyAlbums()){
            for(Photo p: a.getAllPhotos()) {
                arrAllPhotos.add(p);
            }
        }

        initializeRecyclerView();
    }

    private void initializeRecyclerView(){


        mAdapter = new LocationSearchAdapter(this, arrAllPhotos);
        recLocationSearch = findViewById(R.id.recLocationSearch);
        RecyclerView.LayoutManager myLayoutManager = new GridLayoutManager(this,2);
        recLocationSearch.setLayoutManager(myLayoutManager);
        recLocationSearch.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recLocationSearch.setItemAnimator(new DefaultItemAnimator());
        recLocationSearch.setAdapter(mAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_locationsearch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.btnReset:
                resetProcess();
                return true;
            case R.id.action_location_search:
                resetProcess();
                searchProcess();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void resetProcess() {
        mAdapter = new LocationSearchAdapter(this, arrAllPhotos);
        recLocationSearch.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void searchProcess(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(LocationSearch.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_singlesearch,null);

        final EditText editTag = (EditText) mView.findViewById(R.id.editTag);
        mBuilder.setTitle("Search");
        mBuilder.setMessage("Enter a tag to search all location tags in the library.");
        mBuilder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchQuery = editTag.getText().toString();
                searchWithQueries();
                dialog.dismiss();
            }
        });
        mBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void searchWithQueries(){
        ArrayList<Photo> filteredList = new ArrayList<Photo>();

        if (searchQuery.trim().length() == 0){
            filteredList = arrAllPhotos;
        }
        else
        {
            for (Photo p: arrAllPhotos){
                for(String tag: p.getArrLocationTags()) {
                    if ((!(tag.trim().length() < searchQuery.length()))  && !filteredList.contains(p)) {
                        if ((tag.trim().toLowerCase().substring(0,searchQuery.trim().length()).equals(searchQuery.trim().toLowerCase())))
                            filteredList.add(p);
                    }
                }
            }
        }


        mAdapter = new LocationSearchAdapter(this, filteredList);
        recLocationSearch.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }


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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
