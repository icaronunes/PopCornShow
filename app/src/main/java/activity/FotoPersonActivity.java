package activity;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.viewpagerindicator.LinePageIndicator;

import java.util.List;

import br.com.icaro.filme.R;
import domian.FilmeService;
import fragment.PosterScrollFragment;
import info.movito.themoviedbapi.model.Artwork;
import utils.Constantes;

import static br.com.icaro.filme.R.id.pager;

/**
 * Created by icaro on 12/07/16.
 */


public class FotoPersonActivity extends BaseActivity {
    int id_foto, position = 0;
    ViewPager viewPager;
    List<Artwork> artworks;
    LinePageIndicator titlePageIndicator;
    String nome;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_poster);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
       // Log.d("PosterActivity", "onCreate");
        getExtras();

        viewPager = (ViewPager) findViewById(pager);
        titlePageIndicator = (LinePageIndicator) findViewById(R.id.indicator);
       // Log.d("PosterActivity", "onCreate ID: " + id_foto);

    }

    private void getExtras() {
        if (getIntent().getAction() == null){
            id_foto = getIntent().getExtras().getInt(Constantes.PERSON_ID);
            nome = getIntent().getExtras().getString(Constantes.NOME_PERSON);
             position = getIntent().getExtras().getInt(Constantes.POSICAO);
        } else {
            id_foto = getIntent().getExtras().getInt(Constantes.PERSON_ID);
            nome = getIntent().getExtras().getString(Constantes.NOME_PERSON);
            position = getIntent().getExtras().getInt(Constantes.POSICAO);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        new FotoAsync().execute();
    }


    public class PosterFragment extends FragmentPagerAdapter {


        public PosterFragment(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return new PosterScrollFragment().newInstance(artworks.get(position).getFilePath(), nome);
        }

        @Override
        public int getCount() {
            return artworks.size();

        }
    }

    private class FotoAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                artworks = FilmeService.getTmdbPerson().getPersonImages(id_foto);
            } catch (Exception e){
                FirebaseCrash.report(e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FotoPersonActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (artworks != null) {
                viewPager.setAdapter(new PosterFragment(getSupportFragmentManager()));
                titlePageIndicator.setViewPager(viewPager);
                //  titlePageIndicator.setFillColor(R.color.black);
                titlePageIndicator.setCurrentItem(position);
            }
        }
    }
}
