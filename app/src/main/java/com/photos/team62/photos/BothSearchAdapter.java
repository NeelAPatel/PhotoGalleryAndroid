package com.photos.team62.photos;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import objects.Photo;

public class BothSearchAdapter extends RecyclerView.Adapter<BothSearchAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<Photo> arrPhotos;

    public BothSearchAdapter(Context mContext, ArrayList<Photo> arrPhotos){
        this.mContext = mContext;
        this.arrPhotos = arrPhotos;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public View mView;
        public CardView mCardView;
        public ImageView cardThumb;
        public TextView imgTitle;
        public MyViewHolder(View view){
            super(view);
            mView = view;
            mCardView = (CardView) view.findViewById(R.id.card_search);
            cardThumb = (ImageView) view.findViewById(R.id.thumbnail);
            imgTitle = (TextView)  view.findViewById(R.id.imgTitle);
            mCardView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view){

        }
    }


    @Override
    public int getItemCount(){return arrPhotos.size();}

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()) .inflate(R.layout.card_search, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position){
        final Photo photo = arrPhotos.get(position);
        Uri temp = Uri.parse(arrPhotos.get(position).getFilePath());
        String tempPath = arrPhotos.get(position).getFilePath();
        holder.cardThumb.setImageURI(temp);
        holder.imgTitle.setText(photo.getFileName());
    }



}
