package com.example.lostfound;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 列表页：读取数据库，按发布时间倒序展示全部失物 / 招领信息。
 */
public class ItemListActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private ListView listView;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        dbHelper = new DBHelper(this);
        listView = findViewById(R.id.lv_items);
        tvEmpty = findViewById(R.id.tv_empty);
        loadData();
    }

    private void loadData() {
        List<LostItem> items = dbHelper.getAll();
        if (items.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            return;
        }
        tvEmpty.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);

        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);
        List<Map<String, String>> rows = new ArrayList<>();
        for (LostItem item : items) {
            Map<String, String> row = new HashMap<>();
            row.put("title", "[" + item.getTypeName() + "] " + item.getTitle());
            row.put("time", format.format(new Date(item.getCreateTime())));
            row.put("desc", item.getDesc());
            row.put("location", String.format(Locale.CHINA, "坐标 %.4f, %.4f",
                    item.getLat(), item.getLng()));
            rows.add(row);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, rows, R.layout.item_row,
                new String[]{"title", "time", "desc", "location"},
                new int[]{R.id.tv_row_title, R.id.tv_row_time, R.id.tv_row_desc, R.id.tv_row_location});
        listView.setAdapter(adapter);
    }
}
