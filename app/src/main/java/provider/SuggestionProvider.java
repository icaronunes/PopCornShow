package provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.TimeUnit;

import br.com.icaro.filme.R;
import domain.Api;
import domain.busca.MultiSearch;
import domain.busca.ResultsItem;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import utils.UtilsApp;
import utils.enums.EnumTypeMedia;


public class SuggestionProvider extends ContentProvider {

	private MultiSearch multis;
	private CompositeSubscription subscriptions = new CompositeSubscription();
	private MatrixCursor cursor;

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
	                    String[] selectionArgs, String sortOrder) {
		String query1 = uri.getLastPathSegment().toLowerCase();
		if (query1.equals("search_suggest_query")) return null;

		Subscription inscricao = new Api(this.getContext())
				.procuraMulti(query1)
				.doOnNext(multiSearch -> {
					for (ResultsItem resultsItem : multiSearch.getResults()) {
						UtilsApp.gravarImg(getContext(), resultsItem);
					}
				})
				.distinctUntilChanged()
				.debounce(350, TimeUnit.MILLISECONDS)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<MultiSearch>() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {
						Toast.makeText(getContext(), getContext().getString(R.string.ops), Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onNext(MultiSearch multiRetorno) {
						multis = multiRetorno;
					}
				});
		subscriptions.add(inscricao);

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

			for (int position = 0; position < multis.getResults().size() && cursor.getCount() < 10; position++) {

				ResultsItem item = multis.getResults().get(position);
				String mediaType = item.getMediaType();
				String id = "";
				String nome = "";
				String data = "";
				String img = "";

				if (mediaType.equalsIgnoreCase(EnumTypeMedia.TV.getType())) {

					data = item.getFirstAirDate();
					nome = item.getName();
					id = item.getId().toString();
					img = item.getPosterPath();
				} else if (mediaType.equalsIgnoreCase(EnumTypeMedia.MOVIE.getType())) {

					id = item.getId().toString();
					nome = item.getTitle();
					data = item.getReleaseDate();
					img = item.getPosterPath();
				} else if (mediaType.equalsIgnoreCase(EnumTypeMedia.PERSON.getType())) {

					id = item.getId().toString();
					nome = item.getName();
					img = item.getProfile_path();
				}

				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
					if (img != null && !img.isEmpty() && !img.equalsIgnoreCase("")) {
						Uri uriImg = UtilsApp.getUriDownloadImage(getContext(), new File(getContext().getExternalCacheDir().toString() + "/" + img));
						cursor.addRow(new Object[]{position, nome, data, id, mediaType, uriImg});
					} else {
						cursor.addRow(new Object[]{position, nome, data, id, mediaType, null});
					}
				} else {
					cursor.addRow(new Object[]{position, nome, data, id, mediaType});
				}
			}
		}
		return cursor;
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