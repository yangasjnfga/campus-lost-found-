# campus-lost-found-
校园失物招领 Android 应用（移动应用开发及安全大作业）
# 校园失物招领 Android APP

移动应用开发及安全 课程大作业 · 开题与实现工程

## 项目简介

一款面向校园场景的 Android 失物招领应用。学生在教室、食堂、图书馆遗失物品后，可在 APP 中发布失物信息；捡到物品的同学可发布招领信息。应用集成地图定位，可在地图上查看周边遗失物品的位置分布，并通过本地数据库持久化全部数据。

## 功能清单

| 模块 | 功能 | 说明 |
| --- | --- | --- |
| 发布 | 发布失物 / 招领 | 填写标题、描述、位置经纬度，选择类型 |
| 浏览 | 失物列表 | 按时间倒序展示全部信息 |
| 地图 | 地图定位与标记 | 地图展示全部物品坐标，支持单次定位 |
| 存储 | SQLite 持久化 | 增删改查完整支持 |
| 个人 | 我的发布 | 查看并标记已处理状态 |

## 技术栈

- 语言：Java
- 开发工具：Android Studio
- 地图：高德地图 Android SDK（3D 地图 + 定位）
- 存储：SQLite（SQLiteOpenHelper）
- 构建：Gradle

## 目录结构

```
app/
├── build.gradle
└── src/main/
    ├── AndroidManifest.xml
    ├── java/com/example/lostfound/
    │   ├── MainActivity.java        # 首页
    │   ├── PublishActivity.java     # 发布
    │   ├── ItemListActivity.java    # 列表
    │   ├── MapActivity.java         # 地图定位
    │   ├── LostItem.java            # 实体类
    │   └── DBHelper.java            # 数据库封装
    └── res/
        ├── layout/                  # 页面布局
        └── values/                  # 字符串、主题、颜色
```

## 移动应用安全设计

本课程要求同时关注移动应用安全，工程中落实了以下安全措施：

1. **动态权限申请**：定位权限在使用时申请，拒绝后给出提示且不崩溃，遵循最小权限原则。
2. **输入合法性校验**：发布内容统一走 `InputValidator`，拦截空值、超长内容、引号与分号等危险字符。
3. **参数化查询防注入**：所有数据库写入使用 `ContentValues`，查询使用 `selectionArgs` 占位符，不做 SQL 字符串拼接。
4. **敏感信息不明文存储**：联系方式等字段入库前统一处理，日志不打印用户隐私数据。
5. **异常兜底**：定位、数据库操作全部 try-catch，避免异常导致应用崩溃或数据损坏。

## 运行步骤

1. 在高德开放平台申请 Android Key（服务平台选 Android 平台 SDK）。
2. 将 Key 填入 `AndroidManifest.xml` 的 `com.amap.api.v2.apikey`。
3. Android Studio 打开工程，同步 Gradle。
4. 运行后授予定位权限，即可使用发布与地图功能。

## 提交记录说明

本仓库采用分阶段提交，保留完整迭代过程：

1. 开题报告与选题方案
2. 开题答辩材料
3. Android 工程骨架
4. 页面与 SQLite 数据存储
5. 地图定位集成
6. 移动应用安全加固

## 文档材料

| 文件 | 内容 |
| --- | --- |
| `校园失物招领APP-开题报告.pptx` | 开题答辩演示文稿（9 页） |
| `开题发言稿.md` | 逐页口语化答辩发言稿 |
| `docs/安全设计说明.md` | 权限、输入校验、防注入等安全设计 |
| `docs/目录结构.txt` | 工程目录树与评分项对应表 |

## 更新记录

- 补充 `InputValidator` 输入校验工具类与 `docs/安全设计说明.md`，完善安全设计部分。

