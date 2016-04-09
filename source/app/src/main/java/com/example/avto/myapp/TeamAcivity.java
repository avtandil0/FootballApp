package com.example.avto.myapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class TeamAcivity extends AppCompatActivity {
    private static final String TAG = "RecyclerViewExample";
    private List<Team> teams;
    private RecyclerView mRecyclerView;
    private TeamAdapter madapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_acivity);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_team);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        Bundle extras = getIntent().getExtras();
        String myurl1=extras.getString("href");
        // Downloading data from below url
        final String url2 = myurl1;
        new TeamAcivity.AsyncHttpTask().execute(url2);
    }
    public class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            HttpURLConnection urlConnection;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int statusCode = urlConnection.getResponseCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }
                    parseResult(response.toString());
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed to fetch data!";
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result; //"Failed to fetch data!";
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            //

            if (result == 1) {
                madapter = new TeamAdapter( teams,TeamAcivity.this);
                mRecyclerView.setAdapter(madapter);
            } else {
                Toast.makeText(TeamAcivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void parseResult(String result) {
        try {
            JSONObject jsonObject=new JSONObject(result);
            teams = new ArrayList<>();
            Team item=new Team();
            JSONObject jsonObject1=jsonObject.optJSONObject("_links");
            JSONObject jsonObject2=jsonObject1.optJSONObject("players");
            String teamHref=jsonObject2.optString("href");
            GetPlayer getPlayer=new GetPlayer();
            item.team_player=""+getPlayer.execute(teamHref);
            item.team_name=jsonObject.optString("name");
            String s=jsonObject.optString("crestUrl");
            item.image=s;
            teams.add(item);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}