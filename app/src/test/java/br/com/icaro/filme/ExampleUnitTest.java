package br.com.icaro.filme;

import android.util.Log;

import org.junit.Assert;
import org.junit.Test;

import domain.FilmeService;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void TesteMuiti(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                TmdbSearch.MultiListResultsPage multis = FilmeService.getTmdbSearch().searchMulti("jason", "pt", 1);
                MovieDb movieDb = (MovieDb) multis.getResults().get(2);
                Log.d("setOnItemClickListener", movieDb.getOriginalTitle());
                Assert.assertEquals("Pedro", movieDb.getTitle());


            }
        }).start();

    }

}