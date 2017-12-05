package app.facilacesso;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddPointParking extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private static final String TAG = "TESTE>>>>>>>>>>>>>>>";

    private GoogleMap mMap;

    private LatLng newPosition = null;

    private LatLng oldPosition = null;

    private Marker youPosition = null;

    private LocationManager locationManager = null;

    private double latitude;

    private double longitude;

    private String provider;

    private Button addVacancy;

    private ConnectBD connect;

    private String nameCity = "";

    private Button takepicture;

    private ImageView imageView;

    private Bitmap picturelocal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addvacancy);
        connect = new ConnectBD();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapPoint);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(criteria, true);
        try {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            Log.e("RouteActivity", "Permission Error");
        }

        addVacancy = (Button) findViewById(R.id.addVacancy);

        addVacancy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(AddPointParking.this);
                builder.setMessage("Você realmente gostaria de tornar essa vaga visível para outras pessoas?")
                        .setPositiveButton("sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (newPosition != null) {
                                    connect.writeNewPosition(latitude, longitude, picturelocal);
                                    Intent i = new Intent(AddPointParking.this, MapsActivity.class);
                                    startActivity(i);
                                }

                            }
                        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(AddPointParking.this, MapsActivity.class);
                        startActivity(i);
                    }
                });
                builder.create().show();
            }
        });


        imageView = (ImageView) findViewById(R.id.foto);
        takepicture = (Button) findViewById(R.id.idpicture);


        //Botao de tirar foto
        takepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePicture.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePicture, 1);
                }
            }
        });
    }

    //Printa Thumbnail da foto tirada no app
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Bitmap foto = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(foto);
                picturelocal = foto;
                addVacancy.setEnabled(true);
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Intent i = getIntent();
        LatLng positionUser = i.getParcelableExtra("positionUser");
        String nameCity = i.getStringExtra("City");
        connect.setInstanceBD(nameCity);

        latitude = positionUser.latitude;
        longitude = positionUser.longitude;


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                Location location = new Location(LocationManager.GPS_PROVIDER);
                location.setLatitude(latLng.latitude);
                location.setLongitude(latLng.longitude);

                updateLocation(location);
            }
        });
        if (newPosition != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 20));
        else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionUser, 20));
            mMap.addMarker(new MarkerOptions().position(positionUser).title("Sua posicao"));
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
      ///  updateLocation(location);
        //Log.i(TAG, "Latitude: " + latitude + ", Longitude: " + longitude);
    }

    private void updateLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latitude, longitude, 1);
            String address = addresses.get(0).getLocality();
            if (!nameCity.equals(address)){
                connect.setInstanceBD(address);
                nameCity = address;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        newPosition = new LatLng(latitude, longitude);
        if (youPosition != null)
            youPosition.setPosition(newPosition);

        mMap.clear();
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(newPosition,20);
        mMap.addMarker(new MarkerOptions().position(newPosition).title("Nova vaga?"));
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
}


