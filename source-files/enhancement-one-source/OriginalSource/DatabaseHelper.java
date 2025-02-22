package com.example.project2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

// The DatabaseHelper class handles all database operations, including creating tables,
// inserting data, querying data, and managing updates and deletions.
public class DatabaseHelper extends SQLiteOpenHelper {

    // Database name and version
    private static final String DATABASE_NAME = "UserData.db";  // The name of the SQLite database file
    private static final int DATABASE_VERSION = 1;  // Database version for upgrades

    // Table and column definitions
    private static final String TABLE_NAME = "user_data";  // Name of the main table
    private static final String COLUMN_ID = "id";  // Primary key column for unique records
    private static final String COLUMN_USERNAME = "username";  // Column to store usernames
    private static final String COLUMN_DATE = "date";  // Column to store dates of weight logs
    private static final String COLUMN_WEIGHT = "weight";  // Column to store weight entries
    private static final String COLUMN_GOAL_WEIGHT = "goal_weight";  // Column to store user's goal weight

    // Constructor to initialize the DatabaseHelper
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL command to create the user_data table with necessary columns
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_WEIGHT + " REAL, " +
                COLUMN_GOAL_WEIGHT + " REAL)";
        db.execSQL(createTable);  // Executes the SQL command to create the table
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drops the existing table and recreates it during an upgrade
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);  // Calls onCreate to recreate the table structure
    }

    // Method to add a new user to the database
    public long addUser(String username, String password, double goalWeight) {
        SQLiteDatabase db = this.getWritableDatabase();  // Opens the database in writable mode
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);  // Adds the username
        contentValues.put(COLUMN_GOAL_WEIGHT, goalWeight);  // Adds the initial goal weight
        return db.insert(TABLE_NAME, null, contentValues);  // Inserts the data into the table
        // Note: No error handling is implemented for database failures here
    }

    // Method to verify if a user exists in the database
    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();  // Opens the database in readable mode
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
        boolean exists = (cursor != null && cursor.moveToFirst());  // Checks if the query returned any results
        if (cursor != null) {
            cursor.close();  // Closes the cursor to free resources
        }
        return exists;  // Returns true if the user exists, false otherwise
        // Weakness: Combines database operation and business logic; should be refactored
    }

    // Method to retrieve a user's goal weight from the database
    public double getGoalWeight(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_GOAL_WEIGHT}, COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            double goalWeight = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_GOAL_WEIGHT));  // Retrieves the goal weight
            cursor.close();  // Closes the cursor
            return goalWeight;  // Returns the retrieved goal weight
        } else {
            return 0.0;  // Returns 0.0 if no goal weight is found
            // Weakness: No feedback or error handling for missing or invalid data
        }
    }

    // Method to retrieve all weight entries for a user
    public List<String[]> getAllData(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String[]> dataList = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_DATE, COLUMN_WEIGHT}, COLUMN_USERNAME + "=?", new String[]{username}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));  // Retrieves the date
                String weight = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEIGHT));  // Retrieves the weight
                dataList.add(new String[]{date, weight});  // Adds the entry to the list
            } while (cursor.moveToNext());
            cursor.close();  // Closes the cursor
        }
        return dataList;  // Returns the list of weight entries
    }

    // Method to update the goal weight for a user
    public long setGoalWeight(String username, double goalWeight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_GOAL_WEIGHT, goalWeight);  // Sets the new goal weight
        return db.update(TABLE_NAME, contentValues, COLUMN_USERNAME + "=?", new String[]{username});  // Updates the record
    }

    // Method to insert a new weight entry for a user
    public long insertData(String username, String date, double weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);  // Adds the username
        contentValues.put(COLUMN_DATE, date);  // Adds the date of the weight log
        contentValues.put(COLUMN_WEIGHT, weight);  // Adds the weight entry
        return db.insert(TABLE_NAME, null, contentValues);  // Inserts the data into the table
    }

    // Method to clear all data for a specific user
    public boolean clearUserData(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_NAME, COLUMN_USERNAME + "=?", new String[]{username});  // Deletes the records
        return deletedRows > 0;  // Returns true if records were deleted successfully
    }
}
