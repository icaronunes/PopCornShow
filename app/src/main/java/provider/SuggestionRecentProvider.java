package provider;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by icaro on 19/09/16.
 */

public class SuggestionRecentProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "br.com.icaro.filme.search";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SuggestionRecentProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
    //Parou de funcionar
}
