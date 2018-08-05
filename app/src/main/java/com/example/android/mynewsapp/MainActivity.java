package com.example.android.mynewsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Newsfeeds>> {

    TextView mEmptyStateTextView;
    ProgressBar mProgressBar;
    private NewsAdapter mAdapter;

    private static final int NEWS_LOADER_ID = 1;

    private static final String REQUEST_URL = "https://content.guardianapis.com/search?api-key=9761653b-1dc4-4c86-9815-0799a59094e6&show-tags=contributor&show-fields=thumbnail&section=football&page-size=8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        mEmptyStateTextView = findViewById(R.id.empty_view);
        mProgressBar = findViewById(R.id.progress_bar);

        // Find a reference to the {@link ListView} in the layout
        ListView mNewsListView = findViewById(R.id.list);

        mNewsListView.setEmptyView(mEmptyStateTextView);

        // Create a new {@link ArrayAdapter} of newsfeeds
        mAdapter = new NewsAdapter (this, new ArrayList<Newsfeeds>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        mNewsListView.setAdapter(mAdapter);

        mNewsListView.setEmptyView(mEmptyStateTextView);



        if(isConnected){
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(NEWS_LOADER_ID, null, this);

        }else{
            mProgressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet);
        }

        mNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current newsfeed that was clicked on
                Newsfeeds currentNewsfeed = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri webURL = Uri.parse(currentNewsfeed.getWebUrl());

                // Create a new intent to view the newsfeed URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, webURL);

                if (websiteIntent.resolveActivity(getPackageManager()) != null) {
                    // Send the intent to launch a new activity
                    startActivity(websiteIntent);
                }
            }
        });

    }

    @Override
    public Loader<List<Newsfeeds>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        return new NewsfeedsLoader(this, REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Newsfeeds>> loader, List<Newsfeeds> newsfeeds) {
        mEmptyStateTextView.setText(R.string.no_newsfeeds);
        mProgressBar.setVisibility(View.GONE);
        // Clear the adapter of previous newsfeeds data
        mAdapter.clear();

        // If there is a valid list of {@link Newsfeeds}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (newsfeeds != null && !newsfeeds.isEmpty()) {
            mAdapter.addAll(newsfeeds);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Newsfeeds>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }
}
