package app.facilacesso;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final String TAG = "TESTE>>>>>>>>>>>>>>>";

    private GoogleMap mMap;

    private Marker marker;

    private LatLng newPosition = null;

    private LatLng oldPosition = null;

    private Marker youPosition = null;

    private LocationManager locationManager = null;

    private double latitude;

    private double longitude;

    private String provider;

    private Button goAddVacancy;

    private ConnectBD connectBD;

    private List<PointParking> pointsParkings;

    private String nameCity = "";

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        connectBD = new ConnectBD();
        pointsParkings = new ArrayList<>();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(criteria, true);

        try {
            locationManager.requestSingleUpdate(criteria, this, null);
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0, this);

        } catch (SecurityException e) {
            Log.e("RouteActivity", "Permission Error");
        }

        goAddVacancy = (Button) findViewById(R.id.goAddVacancy);

        goAddVacancy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, AddPointParking.class);
                if(newPosition != null) {
                    i.putExtra("positionUser", newPosition);
                    i.putExtra("City", nameCity);
                }
                startActivity(i);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        oldPosition = new LatLng(-9.2384616, -38.1865609); // change this later

        if (newPosition != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 10));
        else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(oldPosition, 10));
            marker = mMap.addMarker(new MarkerOptions().position(oldPosition).title("Sua posicao"));
        }
    }

    /**
     * Called when the location has changed.
     * <p>
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location) {

        updateLocation(location);
        Log.i(TAG, "Latitude: " + latitude + ", Longitude: " + longitude);
    }

    private void updateLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getLocality();
            if (!nameCity.equals(address)) {
                connectBD.setInstanceBD(address);
                mDatabase = connectBD.getInstanceBD();
                nameCity = address;
                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot point : dataSnapshot.getChildren()) {
                            pointsParkings.add(point.getValue(PointParking.class));
                        }
                        showAllParkingToUser();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        newPosition = new LatLng(latitude, longitude);

        if (youPosition != null)
            youPosition.setPosition(newPosition);

        marker.remove();
        CameraUpdate update = CameraUpdateFactory.newLatLng(newPosition);
        marker = mMap.addMarker(new MarkerOptions().position(newPosition).title("Sua posicao"));
        mMap.animateCamera(update);
    }

    /**
     * Called when the provider status changes. This method is called when
     * a provider is unable to fetch a location or if the provider has recently
     * become available after a period of unavailability.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     * @param status   {@link LocationProvider#OUT_OF_SERVICE} if the
     *                 provider is out of service, and this is not expected to change in the
     *                 near future; {@link LocationProvider#TEMPORARILY_UNAVAILABLE} if
     *                 the provider is temporarily unavailable but is expected to be available
     *                 shortly; and {@link LocationProvider#AVAILABLE} if the
     *                 provider is currently available.
     * @param extras   an optional Bundle which will contain provider specific
     *                 status variables.
     *                 <p>
     *                 <p> A number of common key/value pairs for the extras Bundle are listed
     *                 below. Providers that use any of the keys on this list must
     *                 provide the corresponding value as described below.
     *                 <p>
     *                 <ul>
     *                 <li> satellites - the number of satellites used to derive the fix
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    /**
     * Called when the provider is enabled by the user.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getBaseContext(), "Gps ativado! ",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates
     * is called on an already disabled provider, this method is called
     * immediately.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderDisabled(String provider) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    public void showAllParkingToUser() {
     //   List<PointParking> positionsBD = connectBD.getAllPositionFromDataBase();

        for (PointParking points : pointsParkings) {
            LatLng positions = new LatLng(points.getLatitude(), points.getLongitude());
            mMap.addMarker(new MarkerOptions().position(positions).title("Vaga")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.logomarker)
                    ));
        }
    }
}


