package activity;


import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;

import adapter.ReviewsAdapter;
import br.com.icaro.filme.R;
import domain.FilmeService;
import domain.ReviewsUflixit;
import utils.Constantes;

public class ReviewsActivity extends BaseActivity {
    private String id_filme;
    private RecyclerView recyclerView;
    private ReviewsUflixit reviewsUflixit;
    private String type = null;
    private TextView textview_reviews_empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getIntent().getStringExtra(Constantes.INSTANCE.getNOME_FILME()));
        id_filme = getIntent().getStringExtra(Constantes.INSTANCE.getFILME_ID());
        if (getIntent().getStringExtra(Constantes.INSTANCE.getMEDIATYPE()).equals("TV_SERIES")){
            type = "tv-shows";
        } else {
            type = "movies";
        }
        textview_reviews_empty = (TextView) findViewById(R.id.textview_reviews_empty);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_reviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.drawable.alerta)
                .setPositiveButton(R.string.ok, null)
                .setTitle(R.string.alerta_spoiler)
                .setMessage(R.string.msg_spoiler)
                .create();
        dialog.show();

//        AdView adview = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
//                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
//                .build();
//        adview.loadAd(adRequest);

        new TMDVAsync().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                reviewsUflixit = FilmeService.getReviews(id_filme, type);

            } catch (Exception e) {
                FirebaseCrash.report(e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ReviewsActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (reviewsUflixit == null) {
                textview_reviews_empty.setVisibility(View.VISIBLE);
                return;
            }

            if (!reviewsUflixit.isError()) {
                recyclerView.setAdapter(new ReviewsAdapter(ReviewsActivity.this,
                        reviewsUflixit));
                textview_reviews_empty.setVisibility(View.GONE);
            } else {
                textview_reviews_empty.setVisibility(View.VISIBLE);
            }
        }
    }

}
