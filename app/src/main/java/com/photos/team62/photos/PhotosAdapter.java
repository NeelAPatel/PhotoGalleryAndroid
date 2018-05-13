package com.photos.team62.photos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.Activity;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;

import objects.Album;
import objects.Photo;
import objects.User;
import android.widget.TextView;
import android.net.Uri;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;

import static android.support.v4.content.ContextCompat.startActivity;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.myViewHolder> {



    //Fields upon creation
    private Context mContext;
    private User myUser;
    private int currAlbumIndex;

    //Fields derived
    private ArrayList<Album> arrAlbums;
    private Album currAlbum;
    private ArrayList<Photo> arrPhotos;


    public PhotosAdapter(Context mContext, User myUser, int currAlbumIndex, ArrayList<Photo> arrPhotos) {
        this.mContext = mContext;
        this.myUser = myUser;
        this.currAlbumIndex = currAlbumIndex;

        this.arrPhotos = arrPhotos;
        //derived
        arrAlbums = myUser.getMyAlbums();
        currAlbum = arrAlbums.get(currAlbumIndex);
        Log.d(">>>> ██PhotoAdapter 64██ <<<<", "NumOfPhotos: " + arrPhotos.size());
    }

    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public View mView;
        public CardView mCardView;
        public ImageView cardThumb, cardDelete, cardCopy, cardMove;

        public myViewHolder(View view){
            super(view);
            mView = view;
            mCardView = (CardView) view.findViewById(R.id.card_photo);
            mCardView.setOnClickListener(this);
            cardThumb = (ImageView) view.findViewById(R.id.thumbnail);
            cardDelete = (ImageView) view.findViewById(R.id.photoDelete);
            cardCopy = (ImageView) view.findViewById(R.id.photoCopy);
            cardMove = (ImageView) view.findViewById(R.id.photoMove);

        }

        @Override
        public void onClick(View view){
            //Toast.makeText(view.getContext(),"Image Clicked",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return arrPhotos.size();
    }

    @Override
    //Generated
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_photo, parent, false);
        return new myViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(final myViewHolder holder, int position) {
        /*
        This method populates each CardView's Children's fields.
        Holder = the entity of the card
        Position = the position of the card in albumList starting at 0
         */

        //Get current Photo details
        Photo thisPhoto = arrPhotos.get(position);

        //DisplayThumbnail
        Uri temp = Uri.parse(arrPhotos.get(position).getFilePath());
        String tempPath = arrPhotos.get(position).getFilePath();
        holder.cardThumb.setImageURI(temp);

        //Set Tag
        holder.mCardView.setTag(position);


        // == onAction Commands
        //Expected for Thumbnail: Open Phot into new activity and display details
        holder.cardThumb.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) // This onclick is for the Image element inside the Card view.
            {
                int pos = (int) holder.mCardView.getTag();
                //Toast.makeText(mContext, "Photo Clicked, Pos: "+ pos, Toast.LENGTH_SHORT).show();
                //TODO: REMOVE TOAST | ENABLE openClickedPhoto
                Log.d(">>>> ██PhotoAdapter 64██ <<<<", "Huh: " + pos);
                openClickedPhoto(pos,view);
            }
        });

        //Expected for delete: Ask user if they want to delete the file
        holder.cardDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //Displays a dialog box with Yes and No
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure you want to delete this image?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //if Yes is clicked
                        dialog.dismiss();
                        int imagePos = (int) holder.mCardView.getTag();
                        //DeleteProcess

                        arrPhotos.remove(imagePos);

                        // update the recycler view
                        notifyItemRemoved(imagePos);
                        notifyItemRangeChanged(imagePos,arrPhotos.size());

                        myUser.serializeMyData();
                        deleteImage(imagePos);
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //If No is clicked
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }


        });

        holder.cardCopy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final int imagePos = (int) holder.mCardView.getTag();

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                LayoutInflater myInflater = LayoutInflater.from(mContext);

                View mView = myInflater.inflate(R.layout.dialog_spinner, null);
                mBuilder.setTitle("Copy To");
                mBuilder.setMessage("Select an Album to copy to:");
                final Spinner mSpinner = (Spinner) mView.findViewById(R.id.dialogSpinner);

                ArrayList<String> listOfNames = new ArrayList<String>();
                listOfNames.add("Choose...");
                for (Album a : arrAlbums){
                    listOfNames.add(a.getAlbumName());
                }

                final String[] arrAlbumNames = new String[listOfNames.size()];

                for (int i = 0; i < arrAlbumNames.length; i++){
                    arrAlbumNames[i] = listOfNames.get(i);
                }
                ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item,arrAlbumNames);
                spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(spinAdapter);

                mBuilder.setPositiveButton("Copy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    if (!mSpinner.getSelectedItem().toString().equalsIgnoreCase("Choose...")){

                        if (mSpinner.getSelectedItem().toString().equals(arrAlbums.get(currAlbumIndex).getAlbumName())){
                            Toast.makeText(mContext, "Error: Source and Destination points are the same", Toast.LENGTH_SHORT).show();
                        }
                       else{
                        Photo selectedPic = arrPhotos.get(imagePos);
                            ArrayList<String> a = selectedPic.getArrPersonTags();
                            ArrayList<String> b = selectedPic.getArrLocationTags();

                        myUser.getMyAlbums().get(mSpinner.getSelectedItemPosition()-1).getAllPhotos().add(selectedPic);
                        Photo newPhoto = myUser.getMyAlbums().get(mSpinner.getSelectedItemPosition()-1).getAllPhotos().get(myUser.getMyAlbums().get(mSpinner.getSelectedItemPosition()-1).getAllPhotos().size()-1);
                                newPhoto.setArrLocationTags(b);
                                newPhoto.setArrPersonTags(a);
                        notifyItemChanged(0);
                            myUser.serializeMyData();
                        dialog.dismiss();
                        Toast.makeText(mContext, "Copy Complete", Toast.LENGTH_SHORT).show();}
                    }
                    else{
                        dialog.dismiss();
                    }
                    }
                });
                mBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });

        holder.cardMove.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final int imagePos = (int) holder.mCardView.getTag();

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                LayoutInflater myInflater = LayoutInflater.from(mContext);

                View mView = myInflater.inflate(R.layout.dialog_spinner, null);
                mBuilder.setTitle("Move To");
                mBuilder.setMessage("Select an Album to Move to:");
                final Spinner mSpinner = (Spinner) mView.findViewById(R.id.dialogSpinner);

                ArrayList<String> listOfNames = new ArrayList<String>();
                listOfNames.add("Choose...");
                for (Album a : arrAlbums){
                    listOfNames.add(a.getAlbumName());
                }

                final String[] arrAlbumNames = new String[listOfNames.size()];

                for (int i = 0; i < arrAlbumNames.length; i++){
                    arrAlbumNames[i] = listOfNames.get(i);
                }
                ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item,arrAlbumNames);
                spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinner.setAdapter(spinAdapter);

                mBuilder.setPositiveButton("Move", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!mSpinner.getSelectedItem().toString().equalsIgnoreCase("Choose...")){

                            if (mSpinner.getSelectedItem().toString().equals(arrAlbums.get(currAlbumIndex).getAlbumName())){
                                Toast.makeText(mContext, "Error: Source and Destination points are the same", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Photo selectedPic = arrPhotos.get(imagePos);


                                myUser.getMyAlbums().get(mSpinner.getSelectedItemPosition() - 1).getAllPhotos().add(selectedPic);
                                ArrayList<String> a = selectedPic.getArrPersonTags();
                                ArrayList<String> b = selectedPic.getArrLocationTags();
                                Photo newPhoto = myUser.getMyAlbums().get(mSpinner.getSelectedItemPosition()-1).getAllPhotos().get(myUser.getMyAlbums().get(mSpinner.getSelectedItemPosition()-1).getAllPhotos().size()-1);
                                newPhoto.setArrLocationTags(b);
                                newPhoto.setArrPersonTags(a);



                                myUser.getMyAlbums().get(currAlbumIndex).getAllPhotos().remove(imagePos);
                                myUser.serializeMyData();
                                arrPhotos = myUser.getMyAlbums().get(currAlbumIndex).getAllPhotos();
                                notifyItemRemoved(imagePos);
                                notifyItemRangeChanged(imagePos,arrPhotos.size());

                                dialog.dismiss();
                                Toast.makeText(mContext, "Move Complete", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            dialog.dismiss();
                        }
                    }
                });
                mBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });
    }

    private void deleteImage(int imagePos) {
            //



    }



    /*

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

    //extras.putParcelableArrayList("album_arr", albumList);        intent.putExtras(extras);        view.getContext().startActivity(intent);
//}*/




    private void openClickedPhoto(int imgPos, View view) {

        Intent dispIntent = new Intent(view.getContext(), Display.class);
        Bundle dispBundle = new Bundle();
        dispBundle.putSerializable("my_user", (Serializable) myUser);
        dispBundle.putInt("photo_index",imgPos);
        dispBundle.putInt("album_index", currAlbumIndex);




        dispIntent.putExtras(dispBundle);
        view.getContext().startActivity(dispIntent);


    }





}
