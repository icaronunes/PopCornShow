package busca;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

import activity.BaseActivity;
import br.com.icaro.filme.R;
import configuracao.SettingsActivity;
import domain.FilmeService;
import filme.activity.MovieDetailsActivity;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.Multi;
import pessoa.activity.PersonActivity;
import tvshow.activity.TvShowActivity;
import utils.Constantes;
import utils.UtilsApp;
import utils.enums.EnumTypeMedia;


/**
 * Created by icaro on 08/07/16.
 */

public class SearchMultiActivity extends BaseActivity {


	private RecyclerView recyclerView;
	private String query = "";
	private List<Multi> movieDbList = null;
	private TextView text_search_empty;
	private SwipeRefreshLayout swipeRefreshLayout;
	private ProgressBar progressBar;
	private int pagina = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_layout);
		setUpToolBar();
		setupNavDrawer();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		text_search_empty = findViewById(R.id.text_search_empty);
		swipeRefreshLayout = findViewById(R.id.swipeToRefresh);
		progressBar = findViewById(R.id.progress);
		recyclerView = findViewById(R.id.recycleView_search);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setHasFixedSize(true);
		query = getIntent().getStringExtra(SearchManager.QUERY);
		getSupportActionBar().setTitle(query);

		/*TODO
		 *
		 * Arrumar! Gambiara. Funcionando.
		 *
		 */

		if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {

			Intent intent;
			if (getIntent().getData().getLastPathSegment().equalsIgnoreCase(EnumTypeMedia.MOVIE.getType())) {

				intent = new Intent(this, MovieDetailsActivity.class);
				int id = Integer.parseInt(getIntent().getExtras().getString(SearchManager.EXTRA_DATA_KEY));//ID
				intent.putExtra(Constantes.FILME_ID, id);
				intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
				startActivity(intent);
				finish();
				return;
			} else if (getIntent().getData().getLastPathSegment().equalsIgnoreCase(EnumTypeMedia.TV.getType())) {
				final int id = Integer.parseInt(getIntent().getExtras().getString(SearchManager.EXTRA_DATA_KEY));//ID

				intent = new Intent(this, TvShowActivity.class);
				intent.putExtra(Constantes.TVSHOW_ID, id);
				intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
				startActivity(intent);
				finish();
				return;
			} else if (getIntent().getData().getLastPathSegment().equalsIgnoreCase(EnumTypeMedia.PERSON.getType())) {

				intent = new Intent(this, PersonActivity.class);
				int id = Integer.parseInt(getIntent().getExtras().getString(SearchManager.EXTRA_DATA_KEY));//ID
				intent.putExtra(Constantes.PERSON_ID, id);
				intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
				startActivity(intent);
				finish();
				return;
			}
		} else {

			if (UtilsApp.INSTANCE.isNetWorkAvailable(getBaseContext())) {
				new TMDVAsync().execute();

			} else {
				text_search_empty.setText(R.string.no_internet);
				text_search_empty.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.INVISIBLE);
			}
		}

		swipeRefreshLayout.setOnRefreshListener(OnRefreshListener());
	}


	private SwipeRefreshLayout.OnRefreshListener OnRefreshListener() {
		return () -> {
			progressBar.setVisibility(View.INVISIBLE);
			//TODO adicionar infinityScroll
			if (UtilsApp.INSTANCE.isNetWorkAvailable(SearchMultiActivity.this)) {
				TMDVAsync tmdvAsync = new TMDVAsync();
				tmdvAsync.execute();
				text_search_empty.setVisibility(View.GONE);
			} else {
				text_search_empty.setText(R.string.no_internet);
				text_search_empty.setText(View.VISIBLE);
				swipeRefreshLayout.setEnabled(false);
			}
		};
	}


	@Override
	protected void onResume() {
		super.onResume();
		AdView adview = findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
				//.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
				//.addTestDevice("8515241CF1F20943DD64804BD3C06CCB")  // An example device ID
				.build();
		adview.loadAd(adRequest);
	}

	private class TMDVAsync extends AsyncTask<Void, Void, List<Multi>> {

		@Override
		protected void onPreExecute() {
			swipeRefreshLayout.setEnabled(false);
		}

		@Override
		protected List<Multi> doInBackground(Void... voids) {
			if (!query.isEmpty()) {
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SearchMultiActivity.this);
				boolean idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
				try {
					if (idioma_padrao) {
						TmdbSearch tmdbSearch = FilmeService.getTmdbSearch();
						TmdbSearch.MultiListResultsPage movieResultsPage = tmdbSearch.searchMulti(query,
								getLocale() + "en,null", pagina);

						return movieResultsPage.getResults();
					} else {
						TmdbSearch tmdbSearch = FilmeService.getTmdbSearch();
						TmdbSearch.MultiListResultsPage movieResultsPage = tmdbSearch.searchMulti(query,
								"en,null", pagina);
						return movieResultsPage.getResults();
					}
				} catch (Exception e) {
					Crashlytics.logException(e);
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(SearchMultiActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<Multi> movieDbs) {

			swipeRefreshLayout.setEnabled(true);
			if (movieDbs != null && pagina != 1) {
				List<Multi> x = movieDbList;
				movieDbList = movieDbs;
				for (Multi movie : x) {
					movieDbList.add(movie);
				}
				pagina++;
			} else {

				movieDbList = movieDbs;
			}
			if (movieDbList != null && movieDbList.size() > 0) { // TODO: 09/01/17 pode ser null? - vai dar erro?
				swipeRefreshLayout.setRefreshing(false);
				recyclerView.setAdapter(new SearchAdapter(SearchMultiActivity.this, movieDbList));
				swipeRefreshLayout.setEnabled(true);
				pagina++;
				progressBar.setVisibility(View.GONE);
			} else {
				progressBar.setVisibility(View.GONE);
				text_search_empty.setVisibility(View.VISIBLE);
			}
		}
	}
}
