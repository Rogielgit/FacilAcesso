package app.facilacesso;

import android.graphics.Bitmap;

/**
 * Created by computeiro on 22/11/17.
 */

public class PointParking {

    public Double latitude;

    public Double longitude;

    public String namePoint;

    public Bitmap bitmap;

    public PointParking(){

    }

    public PointParking(Double latitude, Double longitude, Bitmap bitmap){
        this.latitude = latitude;
        this.longitude = longitude;
       // this.bitmap = bitmap;
    }

    public void setPosition (Double latitude, Double longitude)
    {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public double getLatitude()
    {
        return this.latitude;
    }
    public double getLongitude()
    {
        return this.longitude;
    }
    public void setNamePoint(String namePoint){
        this.namePoint = namePoint;
    }

    public void setImag(Bitmap bitmap){
        this.bitmap = bitmap;
    }
    public Bitmap getImage()
    {
        return bitmap;
    }




}
