package com.example.lostfound;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * 发布页：录入标题、描述、联系方式，自动获取当前位置后写入数据库。
 * 安全点：定位权限动态申请；全部输入经 InputValidator 校验；入库走参数化写入。
 */
public class PublishActivity extends AppCompatActivity implements AMapLocationListener {

    private static final int REQ_LOCATION = 1001;

    private EditText etTitle;
    private EditText etDesc;
    private EditText etContact;
    private RadioGroup rgType;

    private DBHelper dbHelper;
    private AMapLocationClient locationClient;

    private double lat = 0d;
    private double lng = 0d;
    private boolean located = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        etTitle = findViewById(R.id.et_title);
        etDesc = findViewById(R.id.et_desc);
        etContact = findViewById(R.id.et_contact);
        rgType = findViewById(R.id.rg_type);
        dbHelper = new DBHelper(this);

        requestLocation();
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQ_LOCATION);
        } else {
            startLocate();
        }
    }

    private void startLocate() {
        try {
            AMapLocationClient.updatePrivacyShow(this, true, true);
            AMapLocationClient.updatePrivacyAgree(this, true);
            locationClient = new AMapLocationClient(getApplicationContext());
            AMapLocationClientOption option = new AMapLocationClientOption();
            option.setOnceLocation(true);
            option.setNeedAddress(true);
            locationClient.setLocationOption(option);
            locationClient.setLocationListener(this);
            locationClient.startLocation();
        } catch (Exception ignored) {
            // 定位失败不阻断发布，允许手填坐标
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation == null || aMapLocation.getErrorCode() != 0) {
            return;
        }
        lat = aMapLocation.getLatitude();
        lng = aMapLocation.getLongitude();
        located = true;
        runOnUiThread(() -> {
            EditText etLat = findViewById(R.id.et_lat);
            EditText etLng = findViewById(R.id.et_lng);
            etLat.setText(String.valueOf(lat));
            etLng.setText(String.valueOf(lng));
            Toast.makeText(this, "已获取当前位置", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_LOCATION && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocate();
        } else {
            Toast.makeText(this, "未授权定位，可手动填写坐标", Toast.LENGTH_SHORT).show();
        }
    }

    public void submit(View view) {
        String title = etTitle.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String contact = etContact.getText().toString().trim();

        if (!InputValidator.isValidTitle(title)) {
            Toast.makeText(this, "标题为空、含非法字符或超过 40 字", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!InputValidator.isValidDesc(desc)) {
            Toast.makeText(this, "描述为空、含非法字符或超过 200 字", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!located) {
            String latStr = ((EditText) findViewById(R.id.et_lat)).getText().toString().trim();
            String lngStr = ((EditText) findViewById(R.id.et_lng)).getText().toString().trim();
            try {
                lat = Double.parseDouble(latStr);
                lng = Double.parseDouble(lngStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "坐标格式错误，请先等待定位或手动填写", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!InputValidator.isValidLat(lat) || !InputValidator.isValidLng(lng)) {
            Toast.makeText(this, "坐标超出合法范围", Toast.LENGTH_SHORT).show();
            return;
        }

        String type = rgType.getCheckedRadioButtonId() == R.id.rb_found ? "found" : "lost";
        long row = dbHelper.addItem(title, desc, lat, lng, type, contact);
        if (row > 0) {
            Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "保存失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationClient != null) {
            locationClient.stopLocation();
            locationClient.onDestroy();
        }
    }
}
