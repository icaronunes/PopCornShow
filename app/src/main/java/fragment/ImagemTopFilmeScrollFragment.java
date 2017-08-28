package fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import br.com.icaro.filme.R;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 26/09/16.
 */
public class ImagemTopFilmeScrollFragment extends Fragment {

    String endereco;

    public static Fragment newInstance(String artwork) {
        ImagemTopFilmeScrollFragment topScrollFragment = new ImagemTopFilmeScrollFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constantes.INSTANCE.getENDERECO(), artwork);
        topScrollFragment.setArguments(bundle);
        return topScrollFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        endereco = getArguments().getString(Constantes.INSTANCE.getENDERECO());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_scroll_viewpage_top, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.img_top_scroll);
        Picasso.with(getContext())
                .load(UtilsFilme.getBaseUrlImagem(UtilsFilme.getTamanhoDaImagem(getContext(), 5)) + endereco)
                .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .error(R.drawable.top_empty)
                .into(imageView);

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator alphaStar = ObjectAnimator.ofFloat(imageView, "y", -100, 0)
                .setDuration(8000);
        animatorSet.playTogether(alphaStar);
        // animatorSet.start();
        // Log.d("PosterScrollFragment", "onCreateView: -> " + UtilsFilme.getBaseUrlImagem(4) + endereco);
        return view;
    }

}
