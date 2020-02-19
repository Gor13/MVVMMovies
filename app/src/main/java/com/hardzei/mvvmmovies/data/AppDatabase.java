package com.hardzei.mvvmmovies.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.hardzei.mvvmmovies.pojo.FavouriteMovie;
import com.hardzei.mvvmmovies.pojo.MovieResult;

@Database(entities = {MovieResult.class, FavouriteMovie.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase database;
    private static final String DB_NAME = "mvvmmovies.db";
    private static final Object LOCK = new Object();

    public static AppDatabase getInstance(Context context){
        synchronized (LOCK){
            if (database == null){
                database = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).build();
            }
            return database;
        }
    }
    public abstract MovieDao movieDao();
}
