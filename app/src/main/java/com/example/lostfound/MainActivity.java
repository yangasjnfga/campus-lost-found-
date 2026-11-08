package com.example.lostfound;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 首页：入口导航 + 数据概览。
 */
public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DBHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCount();
    }

    private void refreshCount() {
        TextView tvCount = findViewById(R.id.tv_count);
        int total = dbHelper.countAll();
        tvCount.setText("当前共 " + total + " 条记录");
    }

    public void goPublish(View view) {
        startActivity(new Intent(this, PublishActivity.class));
    }

    public void goList(View view) {
        startActivity(new Intent(this, ItemListActivity.class));
    }

    public void goMap(View view) {
        startActivity(new Intent(this, MapActivity.class));
    }
}
