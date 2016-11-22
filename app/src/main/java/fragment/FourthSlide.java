package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.icaro.filme.R;

/**
 * Created by icaro on 22/11/16.
 */
public class FourthSlide extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.intro, container, false);
        TextView title = (TextView) v.findViewById(R.id.intro_tite);
        title.setText(getResources().getString(R.string.title_intro_4));

        TextView subtitle = (TextView) v.findViewById(R.id.intro_subtitle);
        subtitle.setText(getResources().getText(R.string.subtitle_intro_4));

        ImageView imageView = (ImageView) v.findViewById(R.id.intro_img);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.intro_fab));
        //imageView.setBackground(getResources().getDrawable(R.drawable.icon_movie_now));
        return v;

    }

}
