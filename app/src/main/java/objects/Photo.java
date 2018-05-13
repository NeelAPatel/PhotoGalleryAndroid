package objects;

import android.net.Uri;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;

public class Photo implements Serializable {

    //private File photoFile;
    private ArrayList<String> arrPersonTags;
    private ArrayList<String> arrLocationTags;
    private String filePath;
    private String fileName;

    //private Uri myUri;


    public Photo(){

        this.arrPersonTags = new ArrayList<String>();
        this.arrLocationTags = new ArrayList<String>();
    }

    public Photo(String albumName){
        this.arrPersonTags = new ArrayList<String>();
        this.arrLocationTags = new ArrayList<String>();
    }

    public Photo(String fileName, String albumName, Uri uri){
        this.arrPersonTags = new ArrayList<String>();
        this.arrLocationTags = new ArrayList<String>();
       // this.myUri = uri;
        this.filePath = uri.toString();
        this.fileName = fileName;
    }

    public String getFileName(){
        return fileName;
    }

    public String getFilePath(){
        return filePath;
    }

    public ArrayList<String> getArrPersonTags(){
        return arrPersonTags;
    }

    public void setArrPersonTags(ArrayList<String> tags){
        arrPersonTags = new ArrayList<String>();
        for(String tag : tags){
            arrPersonTags.add(tag);
        }
    }

    public void setArrLocationTags(ArrayList<String> tags){
        arrLocationTags = new ArrayList<String>();
        for(String tag : tags){
            arrLocationTags.add(tag);
        }
    }

    public ArrayList<String> getArrLocationTags() {
        return arrLocationTags;
    }
}
