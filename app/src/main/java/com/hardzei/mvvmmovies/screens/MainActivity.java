package com.hardzei.mvvmmovies.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.hardzei.mvvmmovies.viewmodel.MovieViewModel;
import com.hardzei.mvvmmovies.R;
import com.hardzei.mvvmmovies.adapters.MovieAdapter;
import com.hardzei.mvvmmovies.pojo.MovieResult;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewPosters;
    private MovieAdapter movieAdapter;
    private Switch switchSort;
    private TextView textViewPopularity;
    private TextView textViewTopRated;
    private ProgressBar progressBar;

    public static final String SORT_BY_POPULARITY = "popularity.desc";
    public static final String SORT_BY_TOP_RATED = "vote_average.desc";

    private static int page = 1;
    private static String methodOfSort;

    private  MovieViewModel viewModel;

    private static String lang;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemMenu:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.itemFavourite:
                startActivity(new Intent(this, FavouriteActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getColumnCount() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return width / 185 > 2 ? width / 185 : 2;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lang = Locale.getDefault().getLanguage();

        recyclerViewPosters = findViewById(R.id.recyclerViewPosters);
        switchSort = findViewById(R.id.switchSort);
        textViewPopularity = findViewById(R.id.textViewPopularity);
        textViewTopRated = findViewById(R.id.textViewTopRated);
        progressBar = findViewById(R.id.progressBarLoading);

        movieAdapter = new MovieAdapter();
        recyclerViewPosters.setAdapter(movieAdapter);
        recyclerViewPosters.setLayoutManager(new GridLayoutManager(this, getColumnCount()));

        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        viewModel.getMovies().observe(this, new Observer<List<MovieResult>>() {
            @Override
            public void onChanged(List<MovieResult> movieResults) {
                movieAdapter.setMovies(movieResults);
            }
        });
        viewModel.getErrors().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(Throwable throwable) {
                if (throwable != null) {
                    Log.d("mainActivity", throwable.getMessage());
                    Toast.makeText(MainActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                    viewModel.clearErrors();
                }

            }
        });
        viewModel.downloadNewMovies(methodOfSort, page, lang);

        switchSort.setChecked(true);
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                page = 1;
                setMethodOfSort(b);
            }
        });
        switchSort.setChecked(false);

        movieAdapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                MovieResult movie = movieAdapter.getMovies().get(position);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });

        movieAdapter.setOnRichEndListener(new MovieAdapter.OnRichEndListener() {
            @Override
            public void onRichEnd() {
                    page++;
                    viewModel.downloadMoviesAndAdd(methodOfSort, page, lang);
            }
        });

        LiveData<List<MovieResult>> moviesFromLiveData = viewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<MovieResult>>() {
            @Override
            public void onChanged(List<MovieResult> movies) {
                if (page == 1) {
                    movieAdapter.setMovies(movies);
                }
            }
        });
    }

    public void onClickSetPopularity(View view) {
        setMethodOfSort(false);
    }

    public void onClickSetTopRated(View view) {
        setMethodOfSort(true);
    }

    private void setMethodOfSort(boolean isTopRated) {
        if (isTopRated) {
            methodOfSort = SORT_BY_TOP_RATED;
            textViewTopRated.setTextColor(getResources().getColor(R.color.redColor));
            textViewPopularity.setTextColor(getResources().getColor(R.color.whiteColor));
            switchSort.setChecked(true);
        } else {
            methodOfSort = SORT_BY_POPULARITY;
            textViewTopRated.setTextColor(getResources().getColor(R.color.whiteColor));
            textViewPopularity.setTextColor(getResources().getColor(R.color.redColor));
            switchSort.setChecked(false);
        }
        viewModel.downloadNewMovies(methodOfSort, page, lang);
    }
}
