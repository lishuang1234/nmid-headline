package cn.edu.cqupt.nmid.headline.support.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import cn.edu.cqupt.nmid.headline.support.api.headline.bean.Datum;
import cn.edu.cqupt.nmid.headline.support.db.tables.BaseTable;
import cn.edu.cqupt.nmid.headline.support.db.tables.ClassmateBaseTable;
import cn.edu.cqupt.nmid.headline.support.db.tables.CollegeBaseTable;
import cn.edu.cqupt.nmid.headline.support.db.tables.ScientificBaseTable;
import cn.edu.cqupt.nmid.headline.support.db.tables.YouthBaseTable;
import cn.edu.cqupt.nmid.headline.utils.LogUtils;
import java.util.ArrayList;

/**
 * Created by leon on 1/27/15.
 */
public class DatabaseManager {

  public static String TAG = LogUtils.makeLogTag(DatabaseManager.class);

  private static SQLiteDatabase getWsd() {

    return DatabaseHelper.getInstance().getWritableDatabase();
  }

  private static SQLiteDatabase getRsd() {
    DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
    return databaseHelper.getReadableDatabase();
  }

  public static ArrayList<Datum> get(String tablename, int limit) {
    ArrayList<Datum> listBeans = new ArrayList<>();
    String sql = "select * from "
        + tablename
        + " order by "
        + BaseTable.COLUMN_NAME_ID
        + " desc limit "
        + limit;
    Cursor c = getRsd().rawQuery(sql, null);
    while (c.moveToNext()) {

      Datum newsBean = getBeanFromDb(c);
      listBeans.add(newsBean);
    }
    c.close();

    return listBeans;
  }

  public static void update(ArrayList<Datum> datumArrayList, String tablename, String category) {

    if (datumArrayList == null || datumArrayList.size() == 0) {
      return;
    }

    clear(tablename, category);
    Log.d(TAG, datumArrayList.size() + "");
    for (Datum news : datumArrayList) {
      ContentValues values = new ContentValues();
      values.put(BaseTable.COLUMN_NAME_ID, news.getId());
      values.put(BaseTable.COLUMN_NAME_CATEGORY, news.getCategory());
      values.put(BaseTable.COLUMN_NAME_TITLE, news.getTitle());
      values.put(BaseTable.COLUMN_NAME_SIMPLE_CONTENT, news.getSimpleContent());
      values.put(BaseTable.COLUMN_NAME_IMAGE1, news.getImage1());
      values.put(BaseTable.COLUMN_NAME_IMAGE2, news.getImage2());
      values.put(BaseTable.COLUMN_NAME_IMAGE3, news.getImage3());
      values.put(BaseTable.COLUMN_NAME_TIME_RELEASE, news.getTimeRelease());
      values.put(BaseTable.COLUMN_NAME_ISCOLLECT, news.getIsCollect());
      getWsd().insert(tablename, BaseTable.COLUMN_NAME_ID, values);
    }
  }

  private static void clear(String tablename, String category) {
    String sql =
        "delete from " + tablename + " where " + BaseTable.COLUMN_NAME_CATEGORY + " = " + category;
    getWsd().execSQL(sql);
  }

  private static Datum getBeanFromDb(Cursor cursor) {
    Datum newsBean = new Datum();
    newsBean.setId(cursor.getInt(0));
    newsBean.setCategory((cursor.getInt(1)));
    newsBean.setTitle(cursor.getString(2));
    newsBean.setSimpleContent(cursor.getString(3));
    newsBean.setImage1((cursor.getString(4)));
    newsBean.setImage2(cursor.getString(5));
    newsBean.setImage3((cursor.getString(6)));
    newsBean.setTimeRelease(cursor.getString(7));
    newsBean.setIsCollect(cursor.getInt(8));
    return newsBean;
  }

  public static synchronized void isLikeIt(String tablename, int id, Boolean isLikeit) {
    ContentValues cv = new ContentValues();
    cv.put(BaseTable.COLUMN_NAME_ISCOLLECT, isLikeit);
    String[] args = { Integer.toString(isLikeit ? 1 : 0) };
    getWsd().update(tablename, cv, id + "=?", args);
  }

  //得到收藏列表
  public static ArrayList<Datum> getFavoriteList() {

    ArrayList<Datum> tempList = new ArrayList<Datum>();
    Cursor cursor = getRsd().rawQuery("select * from "
        + ClassmateBaseTable.TABLE_NAME
        + " where "
        + BaseTable.COLUMN_NAME_ISCOLLECT
        + "=1 order by "
        + BaseTable.COLUMN_NAME_ID
        + " desc", null);
    while (cursor.moveToNext()) {
      tempList.add(getBeanFromDb(cursor));
    }
    cursor.close();
    cursor = getRsd().rawQuery("select * from "
        + CollegeBaseTable.TABLE_NAME
        + " where "
        + BaseTable.COLUMN_NAME_ISCOLLECT
        + "=1 order by "
        + BaseTable.COLUMN_NAME_ID
        + " desc", null);
    while (cursor.moveToNext()) {
      tempList.add(getBeanFromDb(cursor));
    }
    cursor.close();
    cursor = getRsd().rawQuery("select * from "
        + ScientificBaseTable.TABLE_NAME
        + " where "
        + BaseTable.COLUMN_NAME_ISCOLLECT
        + "=1 order by "
        + BaseTable.COLUMN_NAME_ID
        + " desc", null);
    while (cursor.moveToNext()) {
      tempList.add(getBeanFromDb(cursor));
    }
    cursor.close();
    cursor = getRsd().rawQuery("select * from "
        + YouthBaseTable.TABLE_NAME
        + " where "
        + BaseTable.COLUMN_NAME_ISCOLLECT
        + "=1 order by "
        + BaseTable.COLUMN_NAME_ID
        + " desc", null);
    while (cursor.moveToNext()) {
      tempList.add(getBeanFromDb(cursor));
    }
    cursor.close();
    return tempList;
  }
}