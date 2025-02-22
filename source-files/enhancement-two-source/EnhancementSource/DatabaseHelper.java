package com.example.project2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "NuoroApp.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USER_DATA = "user_data";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_GOAL_WEIGHT = "goal_weight";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_USER_DATA + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_WEIGHT + " REAL, " +
                COLUMN_GOAL_WEIGHT + " REAL)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_DATA);
        onCreate(db);
    }

    // Check if username exists in the database
    public boolean checkUserCredentials(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USER_DATA + " WHERE " + COLUMN_USERNAME + " = ?";
        try (Cursor cursor = db.rawQuery(query, new String[]{username})) {
            return cursor.getCount() > 0;
        }
    }

    // Add a new user to the database
    public boolean addUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        return db.insert(TABLE_USER_DATA, null, values) != -1;
    }

    // Insert a weight entry into the database with validation
    public boolean insertData(String date, double weight) {
        if (weight < 1 || weight > 500) {
            Log.e(TAG, "Invalid weight entry: " + weight);
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_WEIGHT, weight);
        return db.insert(TABLE_USER_DATA, null, values) != -1;
    }

    // Clear all user data
    public boolean clearUserData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USER_DATA, null, null) > 0;
    }

    // Retrieve the goal weight
    public double getGoalWeight() {
        SQLiteDatabase db = this.getReadableDatabase();
        double goalWeight = 0.0;

        String query = "SELECT " + COLUMN_GOAL_WEIGHT + " FROM " + TABLE_USER_DATA + " LIMIT 1";
        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                goalWeight = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_GOAL_WEIGHT));
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error retrieving goal weight: " + e.getMessage());
        }

        return goalWeight;
    }

    // Set a new goal weight
    public boolean setGoalWeight(double goalWeight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GOAL_WEIGHT, goalWeight);

        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_USER_DATA + " LIMIT 1";
        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                db.update(TABLE_USER_DATA, values, COLUMN_ID + " = ?", new String[]{cursor.getString(0)});
            } else {
                values.put(COLUMN_DATE, ""); // Placeholder
                values.put(COLUMN_WEIGHT, 0.0); // Placeholder
                db.insert(TABLE_USER_DATA, null, values);
            }
        }
        return true;
    }

    // Update a weight entry
    public boolean updateWeight(int id, String newDate, double newWeight) {
        if (newWeight < 1 || newWeight > 500) {
            Log.e(TAG, "Invalid weight update attempt: " + newWeight);
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, newDate);
        values.put(COLUMN_WEIGHT, newWeight);
        return db.update(TABLE_USER_DATA, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    // Delete an entry from the database
    public boolean deleteData(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_USER_DATA, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    // Retrieve all weight entries with sorting options
    public List<WeightEntry> getAllData(String sortBy) {
        List<WeightEntry> weightEntries = new ArrayList<>();
        String query = "SELECT " + COLUMN_ID + ", " + COLUMN_DATE + ", " + COLUMN_WEIGHT + " FROM " + TABLE_USER_DATA;

        switch (sortBy) {
            case "date_asc":
                query += " ORDER BY " + COLUMN_DATE + " ASC";
                break;
            case "date_desc":
                query += " ORDER BY " + COLUMN_DATE + " DESC";
                break;
            case "weight_asc":
                query += " ORDER BY " + COLUMN_WEIGHT + " ASC";
                break;
            case "weight_desc":
                query += " ORDER BY " + COLUMN_WEIGHT + " DESC";
                break;
            default:
                query += " ORDER BY " + COLUMN_DATE + " ASC"; // Default sorting
        }

        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                    double weight = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT));
                    weightEntries.add(new WeightEntry(id, date, weight));
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error retrieving data: " + e.getMessage());
        }

        return weightEntries;
    }

    // Search for weight entries in a date range and weight threshold
    public List<WeightEntry> searchEntries(String startDate, String endDate, double minWeight, double maxWeight) {
        List<WeightEntry> weightEntries = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_USER_DATA +
                " WHERE " + COLUMN_DATE + " BETWEEN ? AND ?" +
                " AND " + COLUMN_WEIGHT + " BETWEEN ? AND ?";

        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.rawQuery(query, new String[]{startDate, endDate, String.valueOf(minWeight), String.valueOf(maxWeight)})) {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                    double weight = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT));
                    weightEntries.add(new WeightEntry(id, date, weight));
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error retrieving data: " + e.getMessage());
        }

        return weightEntries;
    }

    // Nested class to represent weight entries
    public static class WeightEntry {
        private final int id;
        private final String date;
        private final double weight;

        public WeightEntry(int id, String date, double weight) {
            this.id = id;
            this.date = date;
            this.weight = weight;
        }

        public int getId() { return id; }
        public String getDate() { return date; }
        public double getWeight() { return weight; }
    }
}
