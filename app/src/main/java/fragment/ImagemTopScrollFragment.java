package fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import br.com.icaro.filme.R;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 26/07/16.
 */
public class ImagemTopScrollFragment extends Fragment {

    String endereco;

    public static Fragment newInstance(String artwork) {
        ImagemTopScrollFragment topScrollFragment = new ImagemTopScrollFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constantes.ENDERECO, artwork);
        topScrollFragment.setArguments(bundle);
      //  Log.d("PosterScrollFragment", "newInstance: -> " + artwork);
        return topScrollFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        endereco = getArguments().getString(Constantes.ENDERECO);
        //Log.d("PosterScrollFragment", "onCreate: -> " + endereco);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_scroll_image_top, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.img_top_scroll);
        Picasso.with(getContext()).load(UtilsFilme.getBaseUrlImagem(5) + endereco)
                .error(R.drawable.top_empty)
                .into(imageView);

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator alphaStar = ObjectAnimator.ofFloat(imageView, "y", -100, 0)
                .setDuration(1000);
        animatorSet.playTogether(alphaStar);
       // animatorSet.start();
       // Log.d("PosterScrollFragment", "onCreateView: -> " + UtilsFilme.getBaseUrlImagem(4) + endereco);
        return view;
    }

}
