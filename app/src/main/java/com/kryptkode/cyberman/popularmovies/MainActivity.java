package com.kryptkode.cyberman.popularmovies;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.kryptkode.cyberman.popularmovies.adapters.MovieAdapter;
import com.kryptkode.cyberman.popularmovies.data_models.Movie;
import com.kryptkode.cyberman.popularmovies.utilities.JsonGet;
import com.kryptkode.cyberman.popularmovies.utilities.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    private static final String PAGE_NUMBER = "page_number";
    private static final String PREVIOUS_PAGE_NUMBER = "previous_page_number" ;
    private String sortParam = "popular";
    private static final String MOVIES_STATE = "MOVIES_STATE";

    //get reference to widgets
    private TextView errorTextView;

    private ProgressBar progressBar;
    private String moviesUrl;

    private int pageNumber = 1;
    private int previousPageNumber;
    boolean dataLoaded = false;

    private MovieAdapter movieAdapter;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private ArrayList<Movie> moviesStack = new ArrayList<>();
    private ConnectivityChangeReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        errorTextView = (TextView) findViewById(R.id.error_text_view);
        progressBar =  (ProgressBar) findViewById(R.id.loading_indicator);
        errorTextView.setText(R.string.error_message);

        moviesUrl = NetworkUtil.buildPopularMoviesURL(sortParam).toString();

        recyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        //set the number of columns of the grid Layout to 2 if the device is in portait or 3 if the device is on Landscape
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            gridLayoutManager = new GridLayoutManager(this, 2);
        }else{
            gridLayoutManager = new GridLayoutManager(this, 3);
        }

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addOnScrollListener(loadMoreData);

        if (savedInstanceState != null ){
            if (savedInstanceState.containsKey(MOVIES_STATE)){
                moviesStack = savedInstanceState.getParcelableArrayList(MOVIES_STATE);
            }
            if (savedInstanceState.containsKey(PAGE_NUMBER)){
                pageNumber = savedInstanceState.getInt(PAGE_NUMBER);
                Log.e("URL", "SIS_PageNumber -->" + pageNumber);
            }
            if (savedInstanceState.containsKey(PREVIOUS_PAGE_NUMBER)){
                previousPageNumber = savedInstanceState.getInt(PREVIOUS_PAGE_NUMBER);
                Log.e("URL", "SIS_PreviousPageNumber -->" + previousPageNumber);
            }

        }

        receiver = new ConnectivityChangeReceiver();

        movieAdapter = new MovieAdapter(this, moviesStack);
        recyclerView.setAdapter(movieAdapter);
        }

    @Override
    protected void onResume() {
        super.onResume();
        // Receiver for Network connectivity
        registerReceiver(receiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    //TODO: Restore Data with SavedInstance State
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIES_STATE, moviesStack);
        outState.putInt(PAGE_NUMBER, pageNumber);
        outState.putInt(PREVIOUS_PAGE_NUMBER, previousPageNumber);
        Log.e("Save","onSaved: " + String.valueOf(moviesStack));
    }


    //checks for network connectivity
    public class ConnectivityChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetworkUtil.isOnline(getApplicationContext())) {
                getData();
                Snackbar.make(findViewById(R.id.root_main), R.string.is_connected_text, Snackbar.LENGTH_SHORT).show();
                showRecyclerView();
            } else {
                Snackbar.make(findViewById(R.id.root_main), R.string.is_not_connected_text, Snackbar.LENGTH_SHORT).show();
                if (!dataLoaded)
                    displayError();
            }
        }
    }

    //Endless Scrolling
    RecyclerView.OnScrollListener loadMoreData = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
           super.onScrollStateChanged(recyclerView, newState);
            if (gridLayoutManager.findLastCompletelyVisibleItemPosition() == moviesStack.size() - 1) {
                previousPageNumber = pageNumber;
                Log.e("URL", "Listener_Assign_PreviousPageNumber -->" + previousPageNumber);
               if (dataLoaded){
                   pageNumber++;
                   Log.e("URL", "Listener_Increment_PageNumber -->" + pageNumber);
                   dataLoaded = false;
               }

                if (NetworkUtil.isOnline(getApplicationContext())){
                    Log.e("URL", "Listener_After_isOnline_Check_PageNumber -->" + pageNumber);
                    Log.e("URL", "Listener_After_isOnline_Check_Previous_PageNumber -->" + previousPageNumber);
                    if ( pageNumber > previousPageNumber){
                        getData();
                    }

                }
                else{
                    Snackbar.make(findViewById(R.id.root_main), R.string.is_not_connected_text, Snackbar.LENGTH_SHORT).show();

                }
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //caalled when each item in the menu is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelected = item.getItemId();
        switch (itemSelected) {
            case R.id.sort_by_popularity:
                sortParam = "popular";
                break;

            case R.id.sort_by_rating:
                sortParam = "rating";
                break;
        }
        getMovies(sortParam);
        return true;
    }


    // called when user selects a means to sort the movies
    private void getMovies(String sort){
        moviesUrl = NetworkUtil.buildPopularMoviesURL(sort).toString(); //sets the URl to the right one for rating
        if (NetworkUtil.isOnline(getApplicationContext())){
            resetView();
            if (sort.equals("popular")){
                Snackbar.make(findViewById(R.id.root_main), R.string.sort_by_polularity_toast, Snackbar.LENGTH_SHORT).show();
            }
            else{
                Snackbar.make(findViewById(R.id.root_main), R.string.sort_by_top_rated_toast, Snackbar.LENGTH_SHORT).show();
            }
            getData();
        }
        else{
            Snackbar.make(findViewById(R.id.root_main), R.string.is_not_connected_text, Snackbar.LENGTH_SHORT);

        }
    }

    private void resetView() {
            moviesStack.clear();
            pageNumber = 1;
            movieAdapter.notifyDataSetChanged();
        }


        public void showRecyclerView() {
            errorTextView.setVisibility(INVISIBLE);
            recyclerView.setVisibility(VISIBLE);
        }

        private void displayError() {
            errorTextView.setVisibility(VISIBLE);
            Log.e("Save","Display Error");
            recyclerView.setVisibility(INVISIBLE);
        }

        private void loadMovieData(String jsonData) {
            String poster;
            String title;
            String releaseDate;
            String overview;
            double popularity;
            double vote_average;
            int id;
            if (jsonData != null) {
                try {
                    JSONObject movie = new JSONObject(jsonData);
                    JSONArray results = movie.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject currentJson = results.getJSONObject(i);
                        poster = currentJson.getString("poster_path");
                        title = currentJson.getString("original_title");
                        overview = currentJson.getString("overview");
                        releaseDate = currentJson.getString("release_date");
                        popularity = currentJson.getDouble("popularity");
                        vote_average = currentJson.getDouble("vote_average");
                        id = currentJson.getInt("id");
                        Movie movieObject = new Movie(title, poster, overview, releaseDate, vote_average, popularity, id);
                        moviesStack.add(movieObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }



        public void getData(){
            if (NetworkUtil.isOnline(getApplicationContext())){
                new GetDataTask().execute();
            }
            else{
                Toast.makeText(MainActivity.this, R.string.is_not_connected_text, Toast.LENGTH_SHORT).show();
                displayError();
            }
        }


//Async Task  to get the data
        public class GetDataTask extends AsyncTask<Void, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (progressBar != null){
                    progressBar.setVisibility(VISIBLE);
                }

            }

            @Override
            protected String doInBackground(Void... params) {
                String str = null;
                Log.e("URL", moviesUrl);
                JSONObject jsonObject = JsonGet.getDataFromWeb(moviesUrl, pageNumber);
                if(jsonObject != null)
                    str = jsonObject.toString();
                Log.e("URL", String.valueOf(pageNumber));
                //Log.e("URL", str);
                return str;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loadMovieData(s);
                if (progressBar != null){
                    progressBar.setVisibility(INVISIBLE);
                }
                showRecyclerView();
                dataLoaded = true;
                movieAdapter.notifyDataSetChanged();
            }
        }
    }