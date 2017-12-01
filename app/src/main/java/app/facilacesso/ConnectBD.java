package app.facilacesso;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by computeiro on 21/11/17.
 */

public class ConnectBD {

    private DatabaseReference mDatabase;

    private List<PointParking> pointsParkings;

    public void setInstanceBD() {
        pointsParkings = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("position"); //need this to read e write at database
        mDatabase.getDatabase();
        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String userId = getUid();
                for (DataSnapshot point : dataSnapshot.getChildren()) {
                    pointsParkings.add(point.getValue(PointParking.class));
                }
                setAllPositionFromDataBase(pointsParkings);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void writeNewPosition(double latitude, double longitude) {
        final String userId = getUid();
        PointParking pointParking = new PointParking(latitude, longitude);
        mDatabase.child(userId).setValue(pointParking);
    }

    private void setAllPositionFromDataBase(List<PointParking> points) {
        for (PointParking points1 : points) {
            Log.d("AgoraTeste->>", points1.getLatitude() + "->>" + points1.getLongitude());
        }
        this.pointsParkings = points;
    }

    public List<PointParking> getAllPositionFromDataBase() {
        for (PointParking points : pointsParkings) {
            Log.d("Agora->>", points.getLatitude() + "->>" + points.getLongitude());
        }
        return pointsParkings;
    }

    public String getUid() {
        return mDatabase.push().getKey();
    }

}
