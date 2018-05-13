package objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Album implements Serializable {

    private String albumName;
    private int numOfPics;
    private ArrayList<Photo> arrPhotos;


    public Album(){
        this.albumName = " - ";
        this.arrPhotos = new ArrayList<Photo>();
    }
    public Album(String albumName) {
        this.albumName = albumName;
        this.arrPhotos = new ArrayList<Photo>();
        //this.numOfPics = arrPhotos.size();
    }
    public Album(String albumName, ArrayList<Photo> myPhotos) {
        this.albumName = albumName;
        this.arrPhotos = new ArrayList<Photo>();
        for (Photo p : myPhotos) {
            this.arrPhotos.add(p);
        }
        //this.numOfPics = arrPhotos.size();
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumName() {
        return albumName;
    }


    public void setNumOfPics(int numOfPics) {
        this.numOfPics = numOfPics;
    }

    public int getNumOfPics() {
        return numOfPics;
    }


    public ArrayList<Photo> getAllPhotos() {
        return arrPhotos;
    }

    public Photo getPhoto(int indexOf) {
        return arrPhotos.get(indexOf);
    }

    public void addToAlbum(Photo photo) {
        arrPhotos.add(photo);
    }

    public void updatePhotosList(ArrayList<Photo> photoList) {
        this.arrPhotos = new ArrayList<Photo>();
        for(Photo p: photoList){
            arrPhotos.add(p);
        }
    }

    /*

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(albumName);
        dest.writeInt(numOfPics);
        dest.writeList(arrPhotos);
    }


    public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {

        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        public Album[] newArray(int size) {
            return new Album[size];
        }
    };


    public Album(Parcel in) {
        this.albumName = in.readString();
        this.numOfPics = in.readInt();
        this.arrPhotos = new ArrayList<Photo>();
        in.readList(this.arrPhotos, null);

    }
    */

}
