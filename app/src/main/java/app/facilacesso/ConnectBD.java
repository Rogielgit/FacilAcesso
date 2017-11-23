package app.facilacesso;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by computeiro on 21/11/17.
 */

public class ConnectBD{

    private DatabaseReference mDatabase;

    public void setInstanceBD()
    {

        mDatabase = FirebaseDatabase.getInstance().getReference("position"); //need this to read e write at database

    }

    public void writeNewPosition(double latitude, double longitude) {
        final String userId = getUid();
        PointParking pointParking = new PointParking(latitude, longitude);
        mDatabase.child(userId).setValue(pointParking);
    }


    public String getUid() {
        return mDatabase.push().getKey();
    }

}
