package com.example.lostfound;

/**
 * 失物 / 招领信息实体类。
 * 与数据库 lost_item 表一一对应。
 */
public class LostItem {

    private final int id;
    private final String title;
    private final String desc;
    private final double lat;
    private final double lng;
    private final String type;   // lost = 失物，found = 招领
    private final long createTime;

    public LostItem(int id, String title, String desc, double lat, double lng, String type, long createTime) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.lat = lat;
        this.lng = lng;
        this.type = type;
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getType() {
        return type;
    }

    public long getCreateTime() {
        return createTime;
    }

    /** 类型显示名，用于列表与地图气泡 */
    public String getTypeName() {
        return "lost".equals(type) ? "失物" : "招领";
    }
}
