package poster;

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
import utils.Constant;

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
        artworks = (List<PostersItem>) getIntent().getBundleExtra(Constant.BUNDLE).getSerializable(Constant.ARTWORKS);
        nome = getIntent().getStringExtra(Constant.NAME);
        ViewPager viewPager = findViewById(pager);
        LinePageIndicator titlePageIndicator = findViewById(R.id.indicator);
        viewPager.setAdapter(new PosterFragment(getSupportFragmentManager()));
        titlePageIndicator.setViewPager(viewPager);
        titlePageIndicator.setCurrentItem(getIntent().getExtras().getInt(Constant.POSICAO));
 
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
