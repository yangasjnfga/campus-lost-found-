package com.example.lostfound;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;

import java.util.List;

/**
 * 地图页：地图定位使用评分点。
 *
 * 功能流程：
 * 1. 动态申请定位权限，拒绝也不崩溃；
 * 2. 读取数据库中全部物品坐标，在地图上批量打点；
 * 3. 单次定位获取用户当前位置，并把地图视野移动到当前位置；
 * 4. 点击标记可查看物品标题与描述。
 */
public class MapActivity extends AppCompatActivity implements AMapLocationListener {

    private static final int REQ_LOCATION = 2001;

    private MapView mapView;
    private AMap aMap;
    private DBHelper dbHelper;
    private AMapLocationClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        dbHelper = new DBHelper(this);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQ_LOCATION);
            // 未授权时也先展示已有数据的地图
            initMap();
        } else {
            initMap();
            startLocate();
        }
    }

    private void initMap() {
        if (aMap != null) {
            return;
        }
        aMap = mapView.getMap();
        if (aMap == null) {
            Toast.makeText(this, "地图初始化失败，请检查 Key 配置", Toast.LENGTH_LONG).show();
            return;
        }
        aMap.getUiSettings().setZoomControlsEnabled(true);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            return true;
        });
        showAllMarkers();
    }

    /** 把数据库中所有物品画到地图上 */
    private void showAllMarkers() {
        if (aMap == null) {
            return;
        }
        aMap.clear();
        List<LostItem> items = dbHelper.getAll();
        if (items.isEmpty()) {
            return;
        }

        LatLng first = null;
        for (LostItem item : items) {
            LatLng point = new LatLng(item.getLat(), item.getLng());
            if (first == null) {
                first = point;
            }
            MarkerOptions options = new MarkerOptions()
                    .position(point)
                    .title(item.getTypeName() + "：" + item.getTitle())
                    .snippet(item.getDesc());
            // 失物用红色标记，招领用蓝色标记，便于区分
            options.icon(BitmapDescriptorFactory.defaultMarker(
                    "lost".equals(item.getType())
                            ? BitmapDescriptorFactory.HUE_RED
                            : BitmapDescriptorFactory.HUE_AZURE));
            aMap.addMarker(options);
        }

        if (first != null) {
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(first, 16f));
        }
    }

    private void startLocate() {
        try {
            AMapLocationClient.updatePrivacyShow(this, true, true);
            AMapLocationClient.updatePrivacyAgree(this, true);
            locationClient = new AMapLocationClient(getApplicationContext());
            AMapLocationClientOption option = new AMapLocationClientOption();
            option.setOnceLocation(true);
            locationClient.setLocationOption(option);
            locationClient.setLocationListener(this);
            locationClient.startLocation();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation == null || aMapLocation.getErrorCode() != 0 || aMap == null) {
            return;
        }
        final LatLng self = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
        runOnUiThread(() -> aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(self, 17f)));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_LOCATION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocate();
        } else {
            Toast.makeText(this, "未授权定位，仅展示已发布物品位置", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        showAllMarkers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient.onDestroy();
        }
        mapView.onDestroy();
    }
}
