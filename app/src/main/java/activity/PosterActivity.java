package activity;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.viewpagerindicator.LinePageIndicator;

import java.util.List;

import br.com.icaro.filme.R;
import domain.PostersItem;
import fragment.PosterScrollFragment;
import utils.Constantes;

import static br.com.icaro.filme.R.id.pager;

/**
 * Created by icaro on 12/07/16.
 */

public class PosterActivity extends AppCompatActivity {

    private List<PostersItem> artworks;
    private String nome;

    @Override
    public void


    onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_poster);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        artworks = (List<PostersItem>) getIntent().getBundleExtra(Constantes.INSTANCE.getBUNDLE()).getSerializable(Constantes.INSTANCE.getARTWORKS());
        nome = getIntent().getStringExtra(Constantes.INSTANCE.getNOME());
        ViewPager viewPager = (ViewPager) findViewById(pager);
        LinePageIndicator titlePageIndicator = (LinePageIndicator) findViewById(R.id.indicator);
        viewPager.setAdapter(new PosterFragment(getSupportFragmentManager()));
        titlePageIndicator.setViewPager(viewPager);
        titlePageIndicator.setCurrentItem(getIntent().getExtras().getInt(Constantes.INSTANCE.getPOSICAO()));
 
    }

    private class PosterFragment extends FragmentPagerAdapter {


        PosterFragment(FragmentManager fm) {
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
