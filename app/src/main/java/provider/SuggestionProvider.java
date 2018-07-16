package provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

import domain.API;
import domain.busca.MultiSearch;
import domain.busca.ResultsItem;
import info.movito.themoviedbapi.model.Multi;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import utils.UtilsApp;

public class SuggestionProvider extends ContentProvider {

	private final String TAG = getClass().getName();
	private MultiSearch multis;
	private CompositeSubscription subscriptions = new CompositeSubscription();
	private File img = null;
	private Bitmap imageView;
	private MatrixCursor cursor;

	@Override
	public boolean onCreate() {
		//Log.d("SuggestionProvider", "Entrou");
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
	                    String[] selectionArgs, String sortOrder) {
		String query1 = uri.getLastPathSegment().toLowerCase();
		// Log.d("SuggestionProvider", query1);
		//new TmdbSearch(new TmdbApi(Config.TMDB_API_KEY)).searchMulti(query1, "en", 1);
		Subscription inscricao = new API(this.getContext()).procuraMulti(query1)
				.onBackpressureBuffer(4)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<MultiSearch>() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {

					}

					@Override
					public void onNext(MultiSearch multiRetorno) {
						multis = multiRetorno;
					}
				});


		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			cursor = new MatrixCursor(
					new String[]{
							BaseColumns._ID,
							SearchManager.SUGGEST_COLUMN_TEXT_1,
							SearchManager.SUGGEST_COLUMN_TEXT_2,
							SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA,
							SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,
							SearchManager.SUGGEST_COLUMN_ICON_1
					}
			);
		} else {
			cursor = new MatrixCursor(
					new String[]{
							BaseColumns._ID,
							SearchManager.SUGGEST_COLUMN_TEXT_1,
							SearchManager.SUGGEST_COLUMN_TEXT_2,
							SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA,
							SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID,
					}
			);
		}

		if (multis != null) {
			String query = uri.getLastPathSegment().toUpperCase();
			int limit = Integer.parseInt(uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT));

			for (int position = 0; position < multis.getResults().size() && cursor.getCount() < 7; position++) {

				ResultsItem item = multis.getResults().get(position);
				String mediaType = item.getMediaType();
				String id = "";
				String nome = "";
				String data = "";

				if (mediaType.equalsIgnoreCase(Multi.MediaType.TV_SERIES.toString())) {
					// Log.d("SuggestionProvider", String.valueOf(Multi.MediaType.TV_SERIES));
					data = item.getFirstAirDate();
					nome = item.getName();
					id = item.getId().toString();
				}

				if (mediaType.equalsIgnoreCase(Multi.MediaType.MOVIE.toString())) {

					id = item.getId().toString();
					nome = item.getTitle();
					data = item.getReleaseDate();
				}

				if (mediaType.equalsIgnoreCase(Multi.MediaType.PERSON.toString())) {
					id = item.getId().toString();
					nome = item.getName();
				}
				String url = UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(getContext(), 1)) + item.getPosterPath();
				if (item.getPosterPath() != null)
					img = UtilsApp.aguardarImagemPesquisa(getContext(), getImagem(url), item.getPosterPath());


				//Log.d("SuggestionProvider", nome);
				cursor.addRow(new Object[]{position, nome, data, id, mediaType, img.toURI()});
			}
		}

		subscriptions.add(inscricao);
		return cursor;
	}

	private Bitmap getImagem(String url) {
		imageView = null;
		Handler uiHandler = new Handler(Looper.getMainLooper());
		uiHandler.post(new Runnable() {
			@Override
			public void run() {
				Picasso.get().load(url).into(new Target() {
					@Override
					public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
						imageView = bitmap;
					}

					@Override
					public void onBitmapFailed(Exception e, Drawable errorDrawable) {
						imageView = null;
					}

					@Override
					public void onPrepareLoad(Drawable placeHolderDrawable) {

					}
				});
			}
		});

		return imageView;
	}

	@Override
	public void shutdown() {
		super.shutdown();
		subscriptions.unsubscribe();
	}

	@Nullable
	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Nullable
	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		return null;
	}

	@Override
	public int delete(Uri uri, String s, String[] strings) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
		return 0;
	}

}