package com.example.android.mynewsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


//Helper methods related to requesting and receiving news data.

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }


    /**
     * Returns new URL object from the given string URL.
     */
    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if(url==null){
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }else{
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the newsfeed JSON results.", e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Newsfeeds} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<Newsfeeds> extractFeatureFromJson(String newsfeedsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsfeedsJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding newsfeeds to
        List<Newsfeeds> newsfeeds = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsfeedsJSON);

            JSONObject apiResponse = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results(newsfeeds).
            JSONArray newsfeedsArray = apiResponse.getJSONArray("results");

            // For each newsfeed in the newsfeedsArray, create an {@link newsfeeds} object
            for (int i = 0; i < newsfeedsArray.length(); i++) {

                // Get a single newsfeed at position i within the list of newsfeeds
                JSONObject currentNewsfeed = newsfeedsArray.getJSONObject(i);

                // Extract the value for the key called "sectionName"
                String sectionName = currentNewsfeed.getString("sectionName");

                // Extract the value for the key called "webTitle"
                String webTitle = currentNewsfeed.getString("webTitle");

                // Extract the value for the key called "webUrl"
                String webUrl = currentNewsfeed.getString("webUrl");

                // Extract the value for the key called "webPublicationDate"
                String webPublicationDate = currentNewsfeed.getString("webPublicationDate");

                JSONObject thumbnailObject = currentNewsfeed.getJSONObject("fields");
                String thumbnailUrl = thumbnailObject.getString("thumbnail");
                Bitmap newsfeedThumbnail = getNewsfeedThumbnail(thumbnailUrl);

                JSONArray tagsArray = currentNewsfeed.optJSONArray("tags");
                JSONObject tagsObject = tagsArray.optJSONObject(0);
                String authorTitle = tagsObject.optString("webTitle");

                // Create a new {@link newsfeeds} object with the sectionName, webTitle, url,
                // and url from the JSON response.
                Newsfeeds newsfeed = new Newsfeeds(sectionName, webTitle, webUrl);
                newsfeed.setPublicationDate(webPublicationDate);
                newsfeed.setThumbnail(newsfeedThumbnail);
                newsfeed.setAuthorName(authorTitle);

                // Add the new {@link newsfeeds} to the list of newsfeeds.
                newsfeeds.add(newsfeed);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the newsfeeds JSON results", e);
        }

        // Return the list of newsfeeds
        return newsfeeds;
    }

    private static Bitmap getNewsfeedThumbnail(String thumbnailURL) {
        String urldisplay = thumbnailURL;
        Bitmap mIcon = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon;
    }


    /**
     * Query the guardian webserver and return a list of {@link Newsfeeds} objects.
     */
    public static List<Newsfeeds> fetchNewsfeedsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Newsfeeds}s
        List<Newsfeeds> newsfeeds = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Newsfeeds}
        return newsfeeds;
    }
}