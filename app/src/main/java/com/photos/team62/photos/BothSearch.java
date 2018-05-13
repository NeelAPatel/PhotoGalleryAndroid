package com.photos.team62.photos;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import objects.Album;
import objects.Photo;
import objects.User;

public class BothSearch extends AppCompatActivity {
    private User myUser;
    private ArrayList<Photo> arrAllPhotos;
    private int andOrState = 1;

    private String searchQuery1;
    private String searchQuery2;

    private BothSearchAdapter bothAdapater;
    private RecyclerView recBothView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_both_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent rcv = getIntent();
        myUser = new User();
        myUser = (User) rcv.getExtras().getSerializable("myUser");


        arrAllPhotos = new ArrayList<Photo>();
        for(Album a: myUser.getMyAlbums()){
            for(Photo p: a.getAllPhotos()) {
                arrAllPhotos.add(p);
            }
        }


        initializeRecyclerView();
    }



    // ===== ACTION BAR BUTTON SETUP
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_both, menu);
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
            case R.id.search_bothbutton:
                resetProcess();
                searchProcess();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void searchProcess() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(BothSearch.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_bothsearch,null);

        final Button btnAndOr = (Button) mView.findViewById(R.id.btnAndOr);
        final EditText editTag1 = (EditText) mView.findViewById(R.id.editTag1);
        final EditText editTag2 = (EditText) mView.findViewById(R.id.editTag2);

        btnAndOr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (andOrState == 1){
                    btnAndOr.setText("OR");
                    andOrState = 2;
                }
                else if (andOrState == 2){
                    btnAndOr.setText("AND");
                    andOrState = 1;
                }
            }
        });
        mBuilder.setTitle("Search");
        mBuilder.setMessage("Enter two tags to search by. Use the button to choose an operator. Leaving a field blank will assume any tag.");
        mBuilder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchQuery1 = editTag1.getText().toString();
                searchQuery2 = editTag2.getText().toString();
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


    private void resetProcess() {
        bothAdapater = new BothSearchAdapter(this, arrAllPhotos);
        recBothView.setAdapter(bothAdapater);
        bothAdapater.notifyDataSetChanged();
    }

    private void searchWithQueries(){
        ArrayList<Photo> filteredList = new ArrayList<Photo>();
        if (searchQuery1.trim().length() == 0 && searchQuery2.trim().length() == 0){
                filteredList = arrAllPhotos;
        }
        else if (searchQuery1.trim().length() == 0 || searchQuery2.trim().length() == 0){


            String finalquery = "";
            if (searchQuery1.trim().length() == 0){
                finalquery = searchQuery2;
                Log.d("██SEARCH TYPE██ ", "QUERY 1 WAS EMPTY");
            }
            else if (searchQuery2.trim().length() == 0){
                finalquery = searchQuery1;
                Log.d("██SEARCH TYPE██ ", "QUERY 2 WAS EMPTY");
            }


            for(Photo p: arrAllPhotos) {
                ArrayList<String> fullTags = new ArrayList<String>();
                for (String tag : p.getArrPersonTags())
                    fullTags.add(tag);
                for (String tag : p.getArrLocationTags())
                    fullTags.add(tag);

                for(String tag: fullTags) {
                    if ((!(tag.trim().length() < finalquery.length()))  && !filteredList.contains(p)) {
                        if ((tag.trim().toLowerCase().substring(0,finalquery.trim().length()).equals(finalquery.trim().toLowerCase())))
                            filteredList.add(p);
                    }
                }
            }
        }
        else
        {

            for(Photo p: arrAllPhotos) {
                ArrayList<String> fullTags = new ArrayList<String>();
                for (String tag : p.getArrPersonTags())
                    fullTags.add(tag);
                for (String tag : p.getArrLocationTags())
                    fullTags.add(tag);

                for (String tag: fullTags){

                    if (andOrState == 1)
                    {
                        if ((!(tag.trim().length() < searchQuery1.length()))  && (!(tag.trim().length() < searchQuery2.length())) && (!filteredList.contains(p))) {
                            if ((tag.trim().toLowerCase().substring(0,searchQuery1.trim().length()).equals(searchQuery1.trim().toLowerCase()))
                                    && ((tag.trim().toLowerCase().substring(0,searchQuery2.trim().length()).equals(searchQuery2.trim().toLowerCase())))){
                                filteredList.add(p);
                            }

                        }
                    }
                    else if (andOrState == 2)
                    {
                        if ((!(tag.trim().length() < searchQuery1.length()))  && (!(tag.trim().length() < searchQuery2.length())) && (!filteredList.contains(p))) {
                            if ((tag.trim().toLowerCase().substring(0,searchQuery1.trim().length()).equals(searchQuery1.trim().toLowerCase()))
                                    || ((tag.trim().toLowerCase().substring(0,searchQuery2.trim().length()).equals(searchQuery2.trim().toLowerCase())))){
                                filteredList.add(p);
                            }

                        }
                    }

                }

            }
        }


        bothAdapater = new BothSearchAdapter(this, filteredList);
        recBothView.setAdapter(bothAdapater);
        bothAdapater.notifyDataSetChanged();
    }

    // ======= INITIALIZERS =========
    public void initializeRecyclerView(){
        bothAdapater = new BothSearchAdapter(this, arrAllPhotos); //Uses my array of albums to create the adapter

        //Initialize recyclerview
        recBothView = (RecyclerView) findViewById(R.id.recBothView);
        RecyclerView.LayoutManager myLayoutManager = new GridLayoutManager(this, 2); //Creates a layout manager and tells it to have 2 columns
        recBothView.setLayoutManager(myLayoutManager);
        recBothView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recBothView.setItemAnimator(new DefaultItemAnimator());
        recBothView.setAdapter(bothAdapater);
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


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
