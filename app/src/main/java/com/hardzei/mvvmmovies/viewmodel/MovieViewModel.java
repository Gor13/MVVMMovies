package com.hardzei.mvvmmovies.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hardzei.mvvmmovies.api.ApiFactory;
import com.hardzei.mvvmmovies.api.ApiService;
import com.hardzei.mvvmmovies.data.AppDatabase;
import com.hardzei.mvvmmovies.pojo.FavouriteMovie;
import com.hardzei.mvvmmovies.pojo.MovieResponse;
import com.hardzei.mvvmmovies.pojo.MovieResult;
import com.hardzei.mvvmmovies.pojo.ReviewResponse;
import com.hardzei.mvvmmovies.pojo.ReviewResult;
import com.hardzei.mvvmmovies.pojo.TrailerResponse;
import com.hardzei.mvvmmovies.pojo.TrailerResult;

import java.util.List;
import java.util.concurrent.ExecutionException;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MovieViewModel extends AndroidViewModel {

    private static AppDatabase db;
    private LiveData<List<MovieResult>> movies;
    private LiveData<List<FavouriteMovie>> favouriteMovies;
    private MutableLiveData<Throwable> errors;

    private MutableLiveData<List<ReviewResult>> reviews;
    private MutableLiveData<List<TrailerResult>> trailers;

    private CompositeDisposable compositeDisposable;
    private Disposable disposable;

    private static final String API_KEY = "e9f82f402e2b5160fa9b5e4f1a6a00d1";

    public MovieViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        movies = db.movieDao().getAllMovies();
        errors = new MutableLiveData<>();
        favouriteMovies = db.movieDao().getAllFavouriteMovies();
        compositeDisposable = new CompositeDisposable();
        reviews = new MutableLiveData<>();;
        trailers = new MutableLiveData<>();
    }

    public MutableLiveData<List<ReviewResult>> getReviews() {
        return reviews;
    }

    public MutableLiveData<List<TrailerResult>> getTrailers() {
        return trailers;
    }

    public LiveData<List<MovieResult>> getMovies() {
        return movies;
    }

    public LiveData<List<FavouriteMovie>> getFavouriteMovies() {
        return favouriteMovies;
    }

    public MutableLiveData<Throwable> getErrors() {
        return errors;
    }

    public void clearErrors() {
        errors.setValue(null);
    }

    public MovieResult getMovieById(int id){
        try {
            return new GetMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public FavouriteMovie getFavouriteMovieById(int id){
        try {
            return new GetFavouriteMovieTask().execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void insertMovies(List<MovieResult> movies){
        new InsertMoviesTask().execute(movies);
    }

    private void deleteAllMovies() {
       new DeleteAllMoviesTask().execute();
    }

    public void insertFavouriteMovie(FavouriteMovie movie){
        new InsertFavouriteMovieTask().execute(movie);
    }

    public void deleteFavouriteMovie(FavouriteMovie movie){
        new DeleteFavouriteMovieTask().execute(movie);
    }

    private static class InsertMoviesTask extends AsyncTask<List<MovieResult>, Void, Void> {

        @Override
        protected Void doInBackground(List<MovieResult>... lists) {
            if (lists != null && lists.length > 0){
                db.movieDao().insertMovies(lists[0]);
            }
            return null;
        }
    }

    private static class DeleteAllMoviesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            db.movieDao().deleteAllMovies();
            return null;
        }
    }

    private static class InsertFavouriteMovieTask extends AsyncTask<FavouriteMovie, Void, Void> {
        @Override
        protected Void doInBackground(FavouriteMovie... favouriteMovies) {
            if (favouriteMovies != null && favouriteMovies.length > 0){
                db.movieDao().insertFavouriteMovie(favouriteMovies[0]);
            }
            return null;
        }
    }

    private static class DeleteFavouriteMovieTask extends AsyncTask<FavouriteMovie, Void, Void> {
        @Override
        protected Void doInBackground(FavouriteMovie... favouriteMovies) {
            if (favouriteMovies != null && favouriteMovies.length > 0){
                db.movieDao().deleteFavouriteMovie(favouriteMovies[0]);
            }
            return null;
        }
    }

    private static class InsertMovieTask extends AsyncTask<MovieResult, Void, Void>{
        @Override
        protected Void doInBackground(MovieResult... movieResults) {
            if (movieResults != null && movieResults.length > 0){
                db.movieDao().insertMovie(movieResults[0]);
            }
            return null;
        }
    }

    private static class DeleteMovieTask extends AsyncTask<MovieResult, Void, Void>{
        @Override
        protected Void doInBackground(MovieResult... movieResults) {
            if (movieResults != null && movieResults.length > 0){
                db.movieDao().deleteMovie(movieResults[0]);
            }
            return null;
        }
    }

    private static class GetMovieTask extends AsyncTask<Integer, Void, MovieResult>{

        @Override
        protected MovieResult doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0){
                return db.movieDao().getMovieById(integers[0]);
            }
            return null;
        }
    }

    private static class GetFavouriteMovieTask extends AsyncTask<Integer, Void, FavouriteMovie>{

        @Override
        protected FavouriteMovie doInBackground(Integer... integers) {
            if (integers != null && integers.length > 0){
                return db.movieDao().getFavouriteMovieById(integers[0]);
            }
            return null;
        }
    }

    public void downloadNewMovies(String methodOfSort, int page, String lang){
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();
        disposable = apiService.getMovies(API_KEY, lang, methodOfSort, 1000, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MovieResponse>() {
                    @Override
                    public void accept(MovieResponse movieResponse) throws Exception {
                        deleteAllMovies();
                        insertMovies(movieResponse.getResults());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        errors.setValue(throwable);
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void downloadMoviesAndAdd(String methodOfSort, int page, String lang){
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();
        disposable = apiService.getMovies(API_KEY, lang, methodOfSort, 1000, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MovieResponse>() {
                    @Override
                    public void accept(MovieResponse movieResponse) throws Exception {
                       // deleteAllMovies();
                        insertMovies(movieResponse.getResults());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        errors.setValue(throwable);
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void downloadTrailers(int id, String lang){
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();
        disposable = apiService.getTrailers(id, API_KEY, lang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TrailerResponse>() {
                    @Override
                    public void accept(TrailerResponse trailerResponse) throws Exception {
                        trailers.setValue(trailerResponse.getResults());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(disposable);
    }

    public void downloadReviews(int id, String lang){
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();
        disposable = apiService.getReviews(id, API_KEY, lang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ReviewResponse>() {
                    @Override
                    public void accept(ReviewResponse reviewResponse) throws Exception {
                        reviews.setValue(reviewResponse.getResults());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        if (compositeDisposable != null){
            compositeDisposable.dispose();
        }
        super.onCleared();
    }
}
