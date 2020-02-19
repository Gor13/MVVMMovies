package com.hardzei.mvvmmovies.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hardzei.mvvmmovies.viewmodel.MovieViewModel;
import com.hardzei.mvvmmovies.R;
import com.hardzei.mvvmmovies.adapters.ReviewAdapter;
import com.hardzei.mvvmmovies.adapters.TrailerAdapter;
import com.hardzei.mvvmmovies.pojo.FavouriteMovie;
import com.hardzei.mvvmmovies.pojo.MovieResult;
import com.hardzei.mvvmmovies.pojo.ReviewResult;
import com.hardzei.mvvmmovies.pojo.TrailerResult;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewBigPoster;
    private ImageView imageViewFavourite;
    private TextView textViewTitleLableText;
    private TextView textViewOriginalLableText;
    private TextView textViewRatingLableText;
    private TextView textViewReleaseDateLableText;
    private TextView textViewOverview;

    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;

    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;

    private FavouriteMovie favouriteMovie;
    private MovieResult movie;

    private MovieViewModel viewModel;

    private static final String BIG_POSTER_SIZE = "w780";
    private static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";

    private static String lang;

    private int id;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        lang = Locale.getDefault().getLanguage();

        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        imageViewFavourite = findViewById(R.id.imageViewFavourite);
        textViewTitleLableText = findViewById(R.id.textViewTitleLableText);
        textViewOriginalLableText = findViewById(R.id.textViewOriginalLableText);
        textViewRatingLableText = findViewById(R.id.textViewRatingLableText);
        textViewReleaseDateLableText = findViewById(R.id.textViewReleaseDateLableText);
        textViewOverview = findViewById(R.id.textViewOverview);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);

        trailerAdapter = new TrailerAdapter();
        reviewAdapter = new ReviewAdapter();

        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentToTrailer);
            }
        });

        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewTrailers.setAdapter(trailerAdapter);
        recyclerViewReviews.setAdapter(reviewAdapter);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
        } else {
            finish();
        }

        viewModel = new ViewModelProvider(this).get(MovieViewModel.class);
        movie = viewModel.getMovieById(id);

        Picasso.get().load(BASE_POSTER_URL + BIG_POSTER_SIZE + movie.getPosterPath()).placeholder(R.drawable.yesyoucan).into(imageViewBigPoster);

        textViewTitleLableText.setText(movie.getTitle());
        textViewOriginalLableText.setText(movie.getOriginalTitle());
        textViewRatingLableText.setText(Double.toString(movie.getVoteAverage()));
        textViewReleaseDateLableText.setText(movie.getReleaseDate());
        textViewOverview.setText(movie.getOverview());

        changeFavourite();

        viewModel.getReviews().observe(this, new Observer<List<ReviewResult>>() {
            @Override
            public void onChanged(List<ReviewResult> reviewResults) {
                reviewAdapter.setReviews(reviewResults);
            }
        });

        viewModel.getTrailers().observe(this, new Observer<List<TrailerResult>>() {
            @Override
            public void onChanged(List<TrailerResult> trailerResults) {
                trailerAdapter.setTrailers(trailerResults);
            }
        });

        viewModel.downloadReviews(movie.getId(), lang);
        viewModel.downloadTrailers(movie.getId(), lang);

    }

    public void onClickChangeToFavourite(View view) {
        if (favouriteMovie == null){
            viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
            Toast.makeText(this, getResources().getString(R.string.added_to_favourite), Toast.LENGTH_SHORT).show();

        } else {
            viewModel.deleteFavouriteMovie(favouriteMovie);
            Toast.makeText(this, getResources().getString(R.string.deleted_from_favour), Toast.LENGTH_SHORT).show();
            imageViewFavourite.setImageResource((R.drawable.ic_star_border_yellow_24dp));
        }
        changeFavourite();

    }
    public void changeFavourite(){
        favouriteMovie = viewModel.getFavouriteMovieById(id);
        if (favouriteMovie != null){
            imageViewFavourite.setImageResource((R.drawable.ic_star_yellow_24dp));
        } else {
            imageViewFavourite.setImageResource((R.drawable.ic_star_border_yellow_24dp));
        }
    }


}
