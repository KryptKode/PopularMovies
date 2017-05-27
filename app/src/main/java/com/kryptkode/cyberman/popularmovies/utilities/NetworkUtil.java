package com.kryptkode.cyberman.popularmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.kryptkode.cyberman.popularmovies.BuildConfig;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class NetworkUtil extends Context {


    //constants declaration
    private final static String BASE_URL = "https://api.themoviedb.org/3/";//API base url
    private static final String API_KEY = BuildConfig.API_KEY; //API key from the build.gradle  file
    private final static String API_KEY_QUERY_PARAM = "api_key";
    private final static String POPULAR = "popular"; //sort by the popularity rating
    private final static String RATING = "top_rated";//sort by the top viewed rating
    private final static String MOVIE = "movie";


    //method to build the appropriate URL when viewing by RATINGS or POPULARITY
    public static URL buildPopularMoviesURL(String sortParameter){
        Uri builtUri = null;
        switch (sortParameter){
            case "popular":
                builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(MOVIE)
                        .appendPath(POPULAR)
                        .appendQueryParameter(API_KEY_QUERY_PARAM,API_KEY )
                        .build();
                break;
            case "rating":
                builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(MOVIE)
                        .appendPath(RATING)
                        .appendQueryParameter(API_KEY_QUERY_PARAM,API_KEY )
                        .build();
                break;
        }
        URL url = null;
        try{
            url = new URL (builtUri.toString());
        } catch (MalformedURLException | NullPointerException e){
            e.printStackTrace();
        }
        return url;
    }

    //method to check if there is internet connectivity
    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
