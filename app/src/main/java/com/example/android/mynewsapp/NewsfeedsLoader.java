package com.example.android.mynewsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.mynewsapp.Newsfeeds;
import com.example.android.mynewsapp.QueryUtils;

import java.util.List;

/**
 * Created by pc on 8/2/2018.
 */

public class NewsfeedsLoader extends AsyncTaskLoader<List<Newsfeeds>> {

    /** Tag for log messages */
    private static final String LOG_TAG = NewsfeedsLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link Newsfeeds}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public NewsfeedsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Newsfeeds> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of newsfeeds.
        List<Newsfeeds> newsfeeds = QueryUtils.fetchNewsfeedsData(mUrl);
        return newsfeeds;
    }

}
