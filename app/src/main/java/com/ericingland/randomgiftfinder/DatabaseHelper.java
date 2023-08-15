package com.ericingland.randomgiftfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

class DatabaseHelper extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ItemsManager";

    // Items table name
    private static final String TABLE_ItemS = "Items";

    // Items Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_IMAGEURL = "imageurl";
    private static final String KEY_URL = "url";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ItemS_TABLE = "CREATE TABLE " + TABLE_ItemS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGEURL + " TEXT,"
                + KEY_URL + " TEXT" + ")";
        db.execSQL(CREATE_ItemS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ItemS);

        // Create tables again
        onCreate(db);
    }

    // Adding new Item
    void addItem(Item Item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, Item.getTitle());
        values.put(KEY_IMAGEURL, Item.getImageUrl());
        values.put(KEY_URL, Item.getUrl());

        // Inserting Row
        db.insert(TABLE_ItemS, null, values);
        db.close(); // Closing database connection
    }

    public List<Item> getAllItems() {
        List<Item> ItemList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ItemS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Item Item = new Item();
                Item.setId(Integer.parseInt(cursor.getString(0)));
                Item.setTitle(cursor.getString(1));
                Item.setImageUrl(cursor.getString(2));
                Item.setUrl(cursor.getString(3));
                // Adding contact to list
                ItemList.add(Item);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // return contact list
        return ItemList;
    }

    public List<Item> getAllItemsReverse() {
        List<Item> ItemList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ItemS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Item Item = new Item();
                Item.setId(Integer.parseInt(cursor.getString(0)));
                Item.setTitle(cursor.getString(1));
                Item.setImageUrl(cursor.getString(2));
                Item.setUrl(cursor.getString(3));
                // Adding contact to list
                ItemList.add(0, Item);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // return contact list
        return ItemList;
    }

    // Deleting single Item
    public void deleteItem(Item Item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ItemS, KEY_ID + " = ?",
                new String[]{String.valueOf(Item.getId())});
        db.close();
    }

    // Delete all Item
    public void deleteAllItem() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ItemS, null, null);
        db.close();
    }

}