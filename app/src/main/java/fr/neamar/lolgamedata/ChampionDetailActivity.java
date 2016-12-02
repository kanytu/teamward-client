package fr.neamar.lolgamedata;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import fr.neamar.lolgamedata.adapter.MatchAdapter;
import fr.neamar.lolgamedata.pojo.Match;
import fr.neamar.lolgamedata.pojo.Player;

public class ChampionDetailActivity extends SnackBarActivity {
    private static final String TAG = "ChampionDetailActivity";
    private Player player;
    private String region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion_detail);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        player = (Player) getIntent().getSerializableExtra("player");
        region = "euw"; // TODO pass region properly

        /*final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        setTitle(player.champion.name);

        final ImageView splashArtImage = (ImageView) findViewById(R.id.splashArt);
        ImageLoader.getInstance().loadImage(player.champion.splashUrl, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                splashArtImage.setImageBitmap(loadedImage);
                Palette.from(loadedImage).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        int vibrantColor = palette.getVibrantColor(getResources().getColor(R.color.colorPrimary));
                        float[] hsv = new float[3];
                        Color.colorToHSV(vibrantColor, hsv);
                        hsv[2] *= 0.8f; // value component
                        int vibrantColorDark = Color.HSVToColor(hsv);

                        collapsingToolbarLayout.setContentScrimColor(vibrantColor);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = getWindow();
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            window.setStatusBarColor(vibrantColorDark);
                        }
                    }
                });
            }
        });

        ImageView championMasteryImage = (ImageView) findViewById(R.id.championMasteryImage);
        TextView championMasteryText = (TextView) findViewById(R.id.championMasteryText);
        View masteryHolder = findViewById(R.id.masteryHolder);

        @DrawableRes
        int championMasteryResource = CHAMPION_MASTERIES_RESOURCES[player.champion.mastery];
        if(championMasteryResource == 0) {
            masteryHolder.setVisibility(View.INVISIBLE);
        }
        else {
            championMasteryImage.setImageResource(CHAMPION_MASTERIES_RESOURCES[player.champion.mastery]);
            championMasteryText.setText(String.format(getString(R.string.champion_mastery_lvl), player.champion.mastery));
            masteryHolder.setVisibility(View.VISIBLE);
        }

        ImageView rankingTierImage = (ImageView) findViewById(R.id.rankingTierImage);
        TextView rankingText = (TextView) findViewById(R.id.rankingText);
        View rankingHolder = findViewById(R.id.rankingHolder);
        if (player.rank.tier.isEmpty() || !RANKING_TIER_RESOURCES.containsKey(player.rank.tier.toLowerCase())) {
            rankingHolder.setVisibility(View.INVISIBLE);
        }
        else {
            rankingTierImage.setImageResource(RANKING_TIER_RESOURCES.get(player.rank.tier.toLowerCase()));
            rankingText.setText(String.format(getString(R.string.ranking), player.rank.tier.toUpperCase(), player.rank.division));
            rankingHolder.setVisibility(View.VISIBLE);
        }*/

        downloadPerformance();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_gg) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(player.champion.ggUrl));
            startActivity(browserIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_champion_detail, menu);

        return true;
    }

    public void downloadPerformance() {
        ((LolApplication) getApplication()).getMixpanel().timeEvent("Details viewed");


        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(this);

        try {
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, ((LolApplication) getApplication()).getApiUrl() + "/summoner/performance?summoner=" + URLEncoder.encode("Riot neamar", "UTF-8") + "&region=" + region + "&champion=" + URLEncoder.encode("Kled", "UTF-8"), null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ArrayList<Match> matches = Match.getMatches(response);
                            displayPerformance(matches);

                            Log.i(TAG, "Displaying performance for " + "Riot Neamar");

/*                                // Timing automatically added (see timeEvent() call)
                                JSONObject j = account.toJsonObject();
                                j.putOpt("game_map_id", game.mapId);
                                j.putOpt("game_map_name", getString(getMapName(game.mapId)));
                                j.putOpt("game_mode", game.gameMode);
                                j.putOpt("game_type", game.gameType);
                                j.putOpt("game_id", game.gameId);
                                if (getIntent() != null && getIntent().hasExtra("source") && !getIntent().getStringExtra("source").isEmpty()) {
                                    j.putOpt("source", getIntent().getStringExtra("source"));
                                } else {
                                    j.putOpt("source", "unknown");
                                }

                                ((LolApplication) getApplication()).getMixpanel().track("Game viewed", j);
                            } catch (JSONException e) {
                                e.printStackTrace();
*/
                            queue.stop();

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, error.toString());

                    queue.stop();

                    if (error instanceof NoConnectionError) {
                        displaySnack(getString(R.string.no_internet_connection));
                        return;
                    }


                    try {
                        String responseBody = new String(error.networkResponse.data, "utf-8");
                        Log.i(TAG, responseBody);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        // Do nothing, no text content in the HTTP reply.
                    }

                }
            });

            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(jsonRequest);
        } catch (
                UnsupportedEncodingException e
                )

        {
            e.printStackTrace();
        }

    }

    private void displayPerformance(ArrayList<Match> matches) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.progressBar).setVisibility(View.GONE);

        recyclerView.setAdapter(new MatchAdapter(matches));
    }

}
