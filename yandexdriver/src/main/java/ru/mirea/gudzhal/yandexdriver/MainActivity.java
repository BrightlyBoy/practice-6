package ru.mirea.gudzhal.yandexdriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
//import android.graphics.Point;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.Error;
import com.yandex.runtime.network.*;
import com.yandex.runtime.network.RemoteError;
import com.yandex.mapkit.geometry.Geometry;
import com.yandex.mapkit.geometry.Point;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        DrivingSession.DrivingRouteListener {
    private final String MAPKIT_API_KEY = "1adb3a8a-c308-458a-aa30-e6cdf9c38d73";
    private final Point ROUTE_START_LOCATION = new Point(55.670005, 37.479894);
    private final Point ROUTE_END_LOCATION = new Point(55.794229, 37.700772);
    private final com.yandex.mapkit.geometry.Point SCREEN_CENTER = new Point(
            (ROUTE_START_LOCATION.getLatitude() +
                     ROUTE_END_LOCATION.getLatitude()) / 2,
             (ROUTE_START_LOCATION.getLongitude() +
                     ROUTE_END_LOCATION.getLongitude()) / 2);
    private MapView mapView;
    private MapObjectCollection mapObjects;
    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession;
    private int[] colors = {0xFFFF0000, 0xFF00FF00, 0x00FFBBBB, 0xFF0000FF};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        DirectionsFactory.initialize(this);
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        mapView = findViewById(R.id.mapview);
        mapView.getMap().move(new CameraPosition(
                SCREEN_CENTER, 10, 0, 0));
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
        mapObjects = mapView.getMap().getMapObjects().addCollection();
        submitRequest();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    private void submitRequest() {
        DrivingOptions options = new DrivingOptions();
// Кол-во альтернативных путей
        options.setAlternativeCount(3);
        ArrayList<RequestPoint> requestPoints = new ArrayList<>();
// Устанавливаем точки маршрута
        requestPoints.add(new RequestPoint(
                ROUTE_START_LOCATION,
                RequestPointType.WAYPOINT,
                null));
        requestPoints.add(new RequestPoint(
                ROUTE_END_LOCATION,
                RequestPointType.WAYPOINT,
                null));
// Делаем запрос к серверу
        drivingSession = drivingRouter.requestRoutes(requestPoints, options, this);
    }

    @Override
    public void onDrivingRoutes(@NonNull List<DrivingRoute> list) {
        int color;
        for (int i = 0; i < list.size(); i++) {
            color = colors[i];
            mapObjects.addPolyline(list.get(i).getGeometry()).setStrokeColor(color);
        }
    }

    @Override
    public void onDrivingRoutesError(@NonNull Error error) {
    }

//    @Override
//    public void onDrivingRoutesError(@NonNull Error error) {
//        String errorMessage = getString(R.string.unknown_error_message);
//        if (error instanceof RemoteError) {
//            errorMessage = getString(R.string.remote_error_message);
//        } else if (error instanceof NetworkError) {
//            errorMessage = getString(R.string.network_error_message);
//        }
//        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
//    }
}
