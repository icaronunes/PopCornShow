package activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

import com.viewpagerindicator.LinePageIndicator;

import java.util.List;

import br.com.icaro.filme.R;
import fragment.PosterScrollFragment;
import info.movito.themoviedbapi.model.Artwork;
import utils.Constantes;

import static br.com.icaro.filme.R.id.pager;

/**
 * Created by icaro on 12/07/16.
 */




public class PosterActivity extends ActionBarActivity {

    ViewPager viewPager;
    List<Artwork> artworks;
    LinePageIndicator titlePageIndicator;
    String nome;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_poster);
        // Log.d("PosterActivity", "onCreate");
        artworks = (List<Artwork>) getIntent().getBundleExtra(Constantes.BUNDLE).getSerializable(Constantes.ARTWORKS);
        nome = getIntent().getStringExtra(Constantes.NOME);
       // Log.d("posterAcvitivy", String.valueOf(getWindow().getContext().getTheme()));
        viewPager = (ViewPager) findViewById(pager);
        titlePageIndicator = (LinePageIndicator) findViewById(R.id.indicator);
        viewPager.setAdapter(new PosterFragment(getSupportFragmentManager()));
        titlePageIndicator.setViewPager(viewPager);
        titlePageIndicator.setCurrentItem(getIntent().getExtras().getInt(Constantes.POSICAO));


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
}
