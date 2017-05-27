package com.kryptkode.cyberman.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.kryptkode.cyberman.popularmovies.DetailActivity;
import com.kryptkode.cyberman.popularmovies.MainActivity;
import com.kryptkode.cyberman.popularmovies.R;
import com.kryptkode.cyberman.popularmovies.data_models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cyberman on 5/3/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder4Movies> {

        //declare the constants
        private static final String POSTER = "POSTER";
        private static final String MOVIE_TITLE = "MOVIE_TITLE";
        private static final String MOVIE_RELEASE_DATE = "MOVIE_RELEASE_DATE";
        private static final String MOVIE_VOTE = "MOVIE_VOTE";
        private static final String MOVIE_OVERVIEW = "MOVIE_OVERVIEW";

        private Context context;
        private ArrayList<Movie> moviesList;
        private LayoutInflater inflater;


        public MovieAdapter(Context c, ArrayList<Movie> movies) {
            context = c;
            this.inflater = LayoutInflater.from(c);
            this.moviesList = movies;
        }

    public void setMovies(ArrayList<Movie> movies) {
        this.moviesList = movies;
    }

    public interface ListItemClickListener{
            void onItemClick(int clickedItemIndex);
        }

        @Override
        public ViewHolder4Movies onCreateViewHolder(ViewGroup parent, int viewType) {
            View movieView = inflater.inflate(R.layout.item_main, parent, false);

            return new ViewHolder4Movies(movieView);
        }

        @Override
        public void onBindViewHolder(ViewHolder4Movies holder, int position) {
            Movie movie = moviesList.get(position);
            ImageView poster = holder.moviePoster;
            // set the movie poster content description
            /*String movieContentDescription = "Movie: " + movie.getOriginalTitle();
            imageView.setContentDescription(movieContentDescription);*/

            // load the movie poster into the imageView
           Picasso.with(context)
                        .load(movie.getPoster(false)) //get the low resolution image
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.no_image)
                        .into(poster);
        }

        @Override
        public int getItemCount() {
            if (moviesList == null){
                moviesList = new ArrayList<>(10);
            }
            return moviesList.size();
        }

        public class ViewHolder4Movies extends RecyclerView.ViewHolder {
            // get reference to the imageView in a row
            private ImageView moviePoster;
            private View container;

            public ViewHolder4Movies(View itemView) {
                super(itemView);
                moviePoster = (ImageView) itemView.findViewById(R.id.movie_poster);
                container = itemView.findViewById(R.id.container);


                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();

                        String poster = moviesList.get(position).getPoster(true);
                        String movieTitle = moviesList.get(position).getOriginalTitle();
                        String overview = moviesList.get(position).getOverview();
                        String releaseDate = moviesList.get(position).getReleaseDate();
                        double vote = moviesList.get(position).getVoteAverage();

                        Intent intent = new Intent(context, DetailActivity.class);

                        Bundle bundle = new Bundle();

                        //send extra data to the DetailActivity... Using Bundle for faster loading
                        bundle.putString(POSTER, poster);
                        bundle.putString(MOVIE_TITLE, movieTitle);
                        bundle.putString(MOVIE_OVERVIEW,overview );
                        bundle.putString(MOVIE_RELEASE_DATE, releaseDate);
                        bundle.putDouble(MOVIE_VOTE, vote);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }
                });
            }
        }
}
