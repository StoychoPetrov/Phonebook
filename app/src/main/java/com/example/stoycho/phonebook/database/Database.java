package com.example.stoycho.phonebook.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.stoycho.phonebook.models.Country;
import com.example.stoycho.phonebook.models.User;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stoycho on 10/20/2016.
 */

public class Database extends SQLiteOpenHelper {

    /********** Database info ************/
    private static final String DATABASE_NAME = "phonebook";
    private static final int DATABASE_VERSION = 1;

    /********** Table names **************/
    private final static String USERS_TABLE_NAME = "users";
    private final static String COUNTRIES_TABLE_NAME = "countries";

    /********** Users table columns ************/
    private final static String COLUMN_USER_ID = "user_id";
    private final static String COLUMN_FIRST_NAME = "first_name";
    private final static String COLUMN_LAST_NAME = "last_name";
    private final static String COLUMN_EMAIL = "email";
    private final static String COLUMN_PHONE_NUMBER = "phone_number";
    private final static String COLUMN_GENDER = "gender";
    private final static String COLUMN_COUNTRY_ID_FK = "coutry_id_fk";

    /*********** Countries table columns*********/
    private final static String COLUMN_COUNTRY_ID = "country_id";
    private final static String COLUMN_COUNTRY_NAME = "country_name";
    private final static String COLUMN_CALLING_CODE = "calling_code";

    /*********** Create tables *******************/
    private final static String CREATE_USERS = " CREATE TABLE " + USERS_TABLE_NAME + " ( " + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_FIRST_NAME + " TEXT, " + COLUMN_LAST_NAME + " TEXT, "
            + COLUMN_EMAIL + " TEXT, " + COLUMN_PHONE_NUMBER + " TEXT, " + COLUMN_GENDER + " TEXT, " + COLUMN_COUNTRY_ID_FK + " INTEGER, FOREIGN KEY (" + COLUMN_COUNTRY_ID_FK + ") REFERENCES " + COUNTRIES_TABLE_NAME
            + "(" + COLUMN_COUNTRY_ID + "))";
    private final static String CREATE_COUNTRIES = " CREATE TABLE " + COUNTRIES_TABLE_NAME + " ( " + COLUMN_COUNTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_COUNTRY_NAME + " TEXT, "
            + COLUMN_CALLING_CODE + " TEXT)";

    public final static int SELECT_ALL_COUNTRIES = 0;
    public final static int SELECT_SEARCH_PLACES = 1;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public Database(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_COUNTRIES);
        db.execSQL(CREATE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + COUNTRIES_TABLE_NAME);
            onCreate(db);
        }
    }

    public void insertCountry(Country country)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COUNTRY_NAME, country.getCountryName());
        values.put(COLUMN_CALLING_CODE, country.getCallingCode());
        database.insert(COUNTRIES_TABLE_NAME, null, values);
        database.close();
    }

    public long insertUser(User user)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, user.getFirstName());
        values.put(COLUMN_LAST_NAME, user.getLastName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PHONE_NUMBER, user.getPhoneNumber());
        values.put(COLUMN_GENDER,user.getGender());
        values.put(COLUMN_COUNTRY_ID_FK, user.getCountry());
        long id = database.insert(USERS_TABLE_NAME, null, values);
        database.close();
        return id;
    }

    public List<Country> selectAllCountries(String word, int selectQuery)
    {
        String query = null;
        if(selectQuery == SELECT_ALL_COUNTRIES)
            query = "SELECT * FROM " + COUNTRIES_TABLE_NAME;
        else if(selectQuery == SELECT_SEARCH_PLACES)
            query = "SELECT * FROM " + COUNTRIES_TABLE_NAME
                    + " WHERE " + COLUMN_COUNTRY_NAME + " LIKE " + "'" + word + "%'";
        if(query != null) {
            List<Country> countries = new ArrayList<>();
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor cursor = database.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    Country country = new Country();
                    country.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_COUNTRY_ID)));
                    country.setCountryName(cursor.getString(cursor.getColumnIndex(COLUMN_COUNTRY_NAME)));
                    country.setCallingCode(cursor.getString(cursor.getColumnIndex(COLUMN_CALLING_CODE)));
                    countries.add(country);
                } while (cursor.moveToNext());
            }
            cursor.close();
            database.close();
            return countries;
        }
        return null;
    }

    public List<User> selectUsersAndTheirCountries(List<Country> countries,int countryId,String gender,String phone)
    {
        String query = "SELECT * "
                    +  "FROM " + USERS_TABLE_NAME + " users " + "INNER JOIN " + COUNTRIES_TABLE_NAME + " countries "
                    +  "ON " + "users." + COLUMN_COUNTRY_ID_FK + " = countries." + COLUMN_COUNTRY_ID;
        if(countryId >= 0 && gender == null)
            query += " WHERE users." + COLUMN_COUNTRY_ID_FK + " = " + countryId;
        else if(countryId < 0 && gender != null)
            query += " WHERE users." + COLUMN_GENDER + " = '" + gender + "'";
        else if(countryId >=0 && gender != null)
            query += " WHERE users." + COLUMN_COUNTRY_ID_FK + " = " + countryId + " AND users." + COLUMN_GENDER + " = '" + gender + "'";
        else if(phone != null)
            query += " WHERE users." + COLUMN_PHONE_NUMBER + " = " + phone;

        List<User> users = new ArrayList<>();
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                Country country = new Country();

                user.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID)));
                user.setFirstName(cursor.getString(cursor.getColumnIndex(COLUMN_FIRST_NAME)));
                user.setLastName(cursor.getString(cursor.getColumnIndex(COLUMN_LAST_NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
                user.setGender(cursor.getString(cursor.getColumnIndex(COLUMN_GENDER)));
                user.setPhoneNumber(cursor.getString(cursor.getColumnIndex(COLUMN_PHONE_NUMBER)));
                user.setCountry(cursor.getInt(cursor.getColumnIndex(COLUMN_COUNTRY_ID_FK)));
                users.add(user);

                country.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_COUNTRY_ID)));
                country.setCountryName(cursor.getString(cursor.getColumnIndex(COLUMN_COUNTRY_NAME)));
                country.setCallingCode(cursor.getString(cursor.getColumnIndex(COLUMN_CALLING_CODE)));
                countries.add(country);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return users;
    }

    public boolean deleteUser(int userId)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        boolean result = database.delete(USERS_TABLE_NAME,COLUMN_USER_ID + "=?",new String[]{String.valueOf(userId)}) > 0;
        database.close();
        return result;
    }

    public boolean updateUser(User user)
    {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, user.getFirstName());
        values.put(COLUMN_LAST_NAME, user.getLastName());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PHONE_NUMBER, user.getPhoneNumber());
        values.put(COLUMN_GENDER,user.getGender());
        values.put(COLUMN_COUNTRY_ID_FK, user.getCountry());
        int result = database.update(USERS_TABLE_NAME,values,COLUMN_USER_ID + "=?",new String[]{String.valueOf(user.getId())});
        database.close();

        return result > 0;
    }
}
