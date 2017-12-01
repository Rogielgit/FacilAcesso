package app.facilacesso;

/**
 * Created by computeiro on 22/11/17.
 */

public class PointParking {

    public Double latitude;

    public Double longitude;

    public String namePoint;

    public PointParking(){

    }

    public PointParking(Double latitude, Double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
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

    public void setImag(){

    }




}
