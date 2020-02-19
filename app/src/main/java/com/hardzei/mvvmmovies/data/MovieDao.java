package com.hardzei.mvvmmovies.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.hardzei.mvvmmovies.pojo.FavouriteMovie;
import com.hardzei.mvvmmovies.pojo.MovieResult;

import java.util.List;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM moviesresult")
    LiveData<List<MovieResult>> getAllMovies();

    @Query("SELECT * FROM favmoviesresult")
    LiveData<List<FavouriteMovie>> getAllFavouriteMovies();

    @Query("SELECT * FROM moviesresult WHERE id == :movieId")
    MovieResult getMovieById(int movieId);

    @Query("SELECT * FROM favmoviesresult WHERE id == :movieId")
    FavouriteMovie getFavouriteMovieById(int movieId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovies(List<MovieResult> movies);

    @Query("DELETE FROM moviesresult")
    void deleteAllMovies();

    @Insert
    void insertMovie(MovieResult movie);

    @Delete
    void deleteMovie(MovieResult movie);

    @Insert
    void insertFavouriteMovie(FavouriteMovie movie);

    @Delete
    void deleteFavouriteMovie(FavouriteMovie movie);
}
