package activity;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import adapter.PersonPopularAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbPeople;

/**
 * Created by icaro on 04/10/16.
 */
public class PersonPopularActivity extends BaseActivity {

    private TmdbPeople.PersonResultsPage personResultsPage;
    private RecyclerView recyclerView;
    private AdView adView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_popular);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.person_rated);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        adView = (AdView) findViewById(R.id.adView);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_person_popular);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        AdView adview = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build();
        adview.loadAd(adRequest);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:{
                finish();
                break;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new PersonPopularAsync().execute();
    }

    private class PersonPopularAsync extends AsyncTask<Void,Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            personResultsPage =  FilmeService.getTmdbPerson().getPersonPopular(1);
            personResultsPage.getResults().addAll(FilmeService.getTmdbPerson().getPersonPopular(2).getResults());
            Log.d("PersonPopularActivity", personResultsPage.getResults().get(1).toString());
            Log.d("PersonPopularActivity", ""+personResultsPage.getResults().size());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (personResultsPage != null) {

                progressBar.setVisibility(View.GONE);
                 recyclerView.setAdapter(new PersonPopularAdapter(PersonPopularActivity.this, personResultsPage));
            }
        }
    }
}