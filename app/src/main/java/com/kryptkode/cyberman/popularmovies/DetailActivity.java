package com.kryptkode.cyberman.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.kryptkode.cyberman.popularmovies.data_models.Movie;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    //declare the widgets
    private TextView title;
    private TextView releaseDate;
    private TextView rating;
    private TextView overview;
    private ImageView poster;

    private static final String POSTER = "POSTER";
    private static final String MOVIE_TITLE = "MOVIE_TITLE";
    private static final String MOVIE_RELEASE_DATE = "MOVIE_RELEASE_DATE";
    private static final String MOVIE_VOTE = "MOVIE_VOTE";
    private static final String MOVIE_OVERVIEW = "MOVIE_OVERVIEW";
    private double vote;
    private String moviePoster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //instantiate the widgets
        title = (TextView) findViewById(R.id.title);
        releaseDate = (TextView) findViewById(R.id.release_date);
        rating = (TextView) findViewById(R.id.rating);
        overview = (TextView) findViewById(R.id.overview_content);
        poster = (ImageView) findViewById(R.id.detail_image_view);


        Intent intent = getIntent();
        if (null != intent) {

            if (intent.hasExtra(POSTER))
                moviePoster = intent.getStringExtra(POSTER);
            if (intent.hasExtra(MOVIE_TITLE)) {
                //getSupportActionBar().setTitle(intent.getStringExtra(MOVIE_TITLE)); //set the AppBar title to the movie title
                title.setText(intent.getStringExtra(MOVIE_TITLE));
            }
            if (intent.hasExtra(MOVIE_OVERVIEW)) {
                overview.setText(intent.getStringExtra(MOVIE_OVERVIEW));
            }
            if (intent.hasExtra(MOVIE_RELEASE_DATE)) {
                releaseDate.setText(intent.getStringExtra(MOVIE_RELEASE_DATE));
            }
            if (intent.hasExtra(MOVIE_VOTE)) {
                vote = intent.getDoubleExtra(MOVIE_VOTE, 0);
                rating.setText(String.valueOf(vote) + "/10");
            }
            Picasso.with(getApplicationContext())
                    .load(moviePoster) //get the high resolution image
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.no_image)
                    .into(poster);
        }
    }
}
