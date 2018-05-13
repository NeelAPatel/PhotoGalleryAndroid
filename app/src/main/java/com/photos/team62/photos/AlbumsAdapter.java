package com.photos.team62.photos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.Serializable;
import java.util.ArrayList;

import objects.Album;
import objects.User;


public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.myViewHolder> {
    /*
        Note:
        - A lot of these methods are generated
     */

    //AlbumsAdapter handles the actions of the Recycler view and Cardview elements inside it

    // Fields upon creation
    private Context mContext;
    private User myUser;

    //Fields derived
    private ArrayList<Album> arrAlbums;


    // CardView related:
    private myViewHolder popupParent; //holds the parent cardview of the popup (showPopupmethod)


    // ====================== Constructor
    public AlbumsAdapter(Context mContext, User myUser) {
        this.mContext = mContext;
        this.myUser = myUser;

        //Create from myUser:
        this.arrAlbums = myUser.getMyAlbums();
        Log.d(">>>> ██AlbumAdapter 56██ <<<<", "numOfAlbums: " + arrAlbums.size());

    }

    // ========================== Inner Class
    //Generated
    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public View mView;
        public CardView mCardView;
        public TextView cardTitle, cardCount;
        public ImageView cardThumb, cardOverflow;

        public myViewHolder(View view) {
            /* Initializes each CardView element's child elements */

            super(view);
            mView = view;
            mCardView = (CardView) view.findViewById(R.id.card_album);
            cardTitle = (TextView) view.findViewById(R.id.imgTitle);
            cardCount = (TextView) view.findViewById(R.id.albumCount);
            cardThumb = (ImageView) view.findViewById(R.id.albumThumbnail);
            cardOverflow = (ImageView) view.findViewById(R.id.albumOverflow);


            mCardView.setOnClickListener(this);

            //clickedTitle = title.getText().toString();
        }


        @Override
        public void onClick(View view)  // This  onClick is for the Card itself
        {
            //int position = (int) mCardView.getTag();
            //openClickedAlbum(position, clickedTitle, view);
        }
    }


    // ===================== METHODS =========================

    @Override // DONE
    public int getItemCount() {
        return arrAlbums.size();
    }

    @Override // DONE
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()) .inflate(R.layout.card_album, parent, false);
        return new myViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final myViewHolder holder, int position) {
        /*
        This method populates each CardView's Children's fields.
        Holder = the entity of the card
        Position = the position of the card in albumList starting at 0
         */

        // Get current Album details
        Album thisAlbum = arrAlbums.get(position);

        //Set Card title
        holder.cardTitle.setText(thisAlbum.getAlbumName());

        //Sets the number of pictures
        int sizeOfThisAlbum = thisAlbum.getAllPhotos().size();
        thisAlbum.setNumOfPics(sizeOfThisAlbum);
        if (sizeOfThisAlbum > 0)
            holder.cardCount.setText(thisAlbum.getAllPhotos().size() + " photos");
        else
            holder.cardCount.setText(thisAlbum.getAllPhotos().size() + " photo");

        /* Sets the Tag of the cardview.
         This enables us to know what number/position is each card in the arraylist */
        holder.mCardView.setTag(position);

        /* Sets the Thumbnail of the album tot he first picture inside that album
            Or sets a default picture */
        if (thisAlbum.getNumOfPics() > 0){
            holder.cardThumb.setImageURI(Uri.parse(thisAlbum.getAllPhotos().get(0).getFilePath()));
        }
        else
        {
            //TODO: SET DEFAULT PICTURE | USE RESOURCES FOLDER
        }


        // === OnAction Command for tapping Thumbnail
        //Expected for Thumbnail: Open the album and display its contents
        holder.cardThumb.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view){
                int position = (int) holder.mCardView.getTag();
                openClickedAlbum(position, holder.cardTitle.getText().toString() , view);
            }


        });

        //Expected for Overflow: Show a menu at that position for deleting and renaming
        holder.cardOverflow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //When the overflow button/image is clicked, the popup menu shows.
                showPopupMenu(holder.cardOverflow, holder);
            }
        });

        //clickedPosition = position;
    }

    /* Opens the Album itself */
    private void openClickedAlbum(int position,String albumTitle, View view){

        // Toast.makeText(view.getContext(),Integer.toString(position),Toast.LENGTH_SHORT).show();

        //Intent intent = new Intent(this, PhotosViewActivity.class);
        Intent intent = new Intent (view.getContext(), PhotosViewActivity.class);

        Bundle extras = new Bundle();
        Log.d(">>>> ██AlbumAdapter██ <<<<", "currAlbumIndex: " + position);
        Log.d(">>>> ██AlbumAdapter██ <<<<", "numOfAlbums: " + myUser.getMyAlbums().size());
        extras.putSerializable("my_user", (Serializable) myUser);
        extras.putInt("album_pos", position);
        //extras.putString("album_title", albumTitle);
        //extras.putSerializable("album_arr", (Serializable) albumList);
        /* MIGHT WANT TO GET USER VARIABLE TOO, FOR SERIALIZATION */

        //extras.putParcelableArrayList("album_arr", albumList);

        intent.putExtras(extras);
        view.getContext().startActivity(intent);

    }




    // ====== Popup menu related
    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view, myViewHolder holder) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popupParent = holder;



        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /* Click listener for popup menu items */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        long id;
        public MyMenuItemClickListener() {
            this.id = id;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                case R.id.action_rename: {
                    renameAlbum();
                    return true;
                }
                case R.id.action_delete: {
                    deleteAlbum();
                    //Toast.makeText(mContext, "Delete" + clickedPosition + albumList.get(clickedPosition).getAlbumName() , Toast.LENGTH_SHORT).show();
                    return true;
                }
                default:
            }
            return false;
        }
    }

    private void renameAlbum(){
        //Toast.makeText(mContext, "Rename" + popupParent.mCardView.getTag() + albumList.get(Integer.parseInt(popupParent.mCardView.getTag().toString())).getAlbumName() , Toast.LENGTH_SHORT).show();
        final EditText fieldRename = new EditText(mContext);
        fieldRename.setHint("Replacement album name");
        AlertDialog dialogNewAlbum = new AlertDialog.Builder(mContext)
                .setTitle("Rename album")
                .setMessage("Provide a name to rename this album to: ")
                .setView(fieldRename)
                .setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // When positive answer is clicked....

                        // rename the album
                        String name = String.valueOf(fieldRename.getText());
                        if (name.length() > 0) {


                            arrAlbums.get(Integer.parseInt(popupParent.mCardView.getTag().toString())).setAlbumName(name);
                            //Toast.makeText(mContext,"SIZE: "+ albumList.size(), Toast.LENGTH_SHORT).show();
                            Log.d(">>>>>> TESTING <<<<<< ", "SIZE: " + arrAlbums.size());

                            // update the recycler view
                            notifyDataSetChanged();

                            // update app data
                            //myUser.updateAlbumList(arrAlbums);
                            myUser.serializeMyData();
                        }
                        else
                        {
                            Toast.makeText(mContext, "Rename Canceled", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialogNewAlbum.show();


    }

    private void deleteAlbum(){
        AlertDialog dialogNewAlbum = new AlertDialog.Builder(mContext)
                .setTitle("Delete")
                .setMessage("Delete this album? Any photos inside cannot be recovered.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Delete album
                        int pos = Integer.parseInt(popupParent.mCardView.getTag().toString());
                        arrAlbums.remove(pos);

                        // update the recycler view
                        /*if (pos == 0)
                            notifyDataSetChanged();
                        else
                            notifyItemRemoved(Integer.parseInt(popupParent.mCardView.getTag().toString()));
                        */
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos,arrAlbums.size());

                        // update app data
                        myUser.serializeMyData();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialogNewAlbum.show();
    }

}
