package com.yahoo.yuningj.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yahoo.yuningj.nytimessearch.Article;
import com.yahoo.yuningj.nytimessearch.ArticleArrayAdapter;
import com.yahoo.yuningj.nytimessearch.EndlessScrollListener;
import com.yahoo.yuningj.nytimessearch.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    EditText etQuery;
    GridView gvResults;
    Button btnSearch;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    String beginDate;
    String endDate;
    String sortOrder;

    String query;

    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();

        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadNextDataFromApi(page);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
        //reloadQueryRefined(query, beginDate, endDate, sortOrder);
    }

    public void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        // data init
        beginDate = new String();
        endDate = new String();
        sortOrder = new String();
        query = new String();

        //hook up listener for grid click
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //create an intent to display the article
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                //get the article to display
                Article article = articles.get(i);
                //pass in that article into intent
                intent.putExtra("article", article);
                //launch the activity
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(View view) {
        query = etQuery.getText().toString();

        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter what you want to search", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Search for " + query, Toast.LENGTH_LONG).show();

        }
        articles.clear();
        reloadQueryRefined(query, 0, beginDate, endDate, sortOrder);
    }

//

    private void reloadQueryRefined(String query, int page, String b, String e, String s) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key", "06dc7ea1872b44dd861f35eea5c21d4e");
        params.put("page", page);
        params.put("q", query);
        if (!b.isEmpty()) params.put("begin_date", b);
        if (!e.isEmpty()) params.put("end_date", e);
        if (!s.isEmpty()) params.put("sort", s);

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    //Log.d("DEBUG", articleJsonResults.toString());
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                    Log.d("DEBUG", articles.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void goFilter(MenuItem item) {
        Intent intent = new Intent(SearchActivity.this, FilterActivity.class);
        intent.putExtra("beginDate", beginDate);
        intent.putExtra("endDate", endDate);
        intent.putExtra("sortOrder", sortOrder);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            beginDate = data.getExtras().getString("beginDate");
            endDate = data.getExtras().getString("endDate");
            sortOrder = data.getExtras().getString("sortOrder");
            articles.clear();
            reloadQueryRefined(query, 0, beginDate, endDate, sortOrder);
            // Toast the name to display temporarily on screen
            //Toast.makeText(this, beginDate + endDate, Toast.LENGTH_SHORT).show();
        }
    }

    public void loadNextDataFromApi(int offset) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyDataSetChanged()`
        reloadQueryRefined(query, offset, beginDate, endDate, sortOrder);
    }
}
