package objects;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class User implements Serializable {

    private ArrayList<Album> myAlbums;
    public User(){
        myAlbums = new ArrayList<Album>();
        //createFileIfNotExists();
    }

    public void updateAlbumList(ArrayList<Album> newSet){
        myAlbums = new ArrayList<Album>();

        for (Album a : newSet){
            myAlbums.add(a);
        }
    }

    public ArrayList<Album> getMyAlbums(){
        return myAlbums;
    }

    public void serializeMyData(){

        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("/data/data/com.photos.team62.photos/files/appData.dat"));
            oos.writeObject(myAlbums);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void retrieveMyData(){
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("/data/data/com.photos.team62.photos/files/appData.dat"));
            myAlbums = (ArrayList<Album>) ois.readObject();
            ois.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void updatePhotosList(ArrayList<Album> albumList, ArrayList<Photo> photoList, int currentAlbumIndex) {
        Log.d(">>>> MESSAGE <<<< : ", "AFT DELETE = CurrPic: " + currentAlbumIndex + "size of Photos: " + photoList.size() + "size of Album: " + albumList.size());
        Album currAlbum = myAlbums.get(currentAlbumIndex);
        ArrayList<Photo> currPhotos = currAlbum.getAllPhotos();

        currPhotos.clear();

        for(Photo p: photoList){
            currPhotos.add(p);
        }


    }
}
