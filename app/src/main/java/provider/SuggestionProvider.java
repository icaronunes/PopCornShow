package provider;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by icaro on 19/09/16.
 */

public class SuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "br.com.icaro.filme.suggestions";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }


}
