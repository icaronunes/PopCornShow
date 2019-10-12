package fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import br.com.icaro.filme.R;
import domain.TopMain;
import filme.activity.MovieDetailsActivity;
import info.movito.themoviedbapi.model.Multi;
import tvshow.activity.TvShowActivity;
import utils.Constantes;
import utils.UtilsApp;

/**
 * Created by icaro on 26/07/16.
 */
public class ImagemTopScrollFragment extends Fragment {

    private TopMain topMains;

    public static Fragment newInstance(TopMain topMainList) {
        ImagemTopScrollFragment topScrollFragment = new ImagemTopScrollFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constantes.INSTANCE.getMAIN(), topMainList);
        topScrollFragment.setArguments(bundle);

        return topScrollFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topMains = (TopMain) getArguments().getSerializable(Constantes.INSTANCE.getMAIN());

    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_scroll_image_top, container, false);
        final ImageView imageView = view.findViewById(R.id.img_top_scroll);
        TextView title = view.findViewById(R.id.title);




        if (topMains.getMediaType().equalsIgnoreCase(Multi.MediaType.MOVIE.name())) {

            Picasso.get()
                    .load(UtilsApp.INSTANCE.getBaseUrlImagem(UtilsApp.INSTANCE.getTamanhoDaImagem(getContext(), 5)) + topMains.getImagem())
                    .error(R.drawable.top_empty)
                    .into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
                    intent.putExtra(Constantes.INSTANCE.getNOME_FILME(), topMains.getNome());
                    intent.putExtra(Constantes.INSTANCE.getFILME_ID(), topMains.getId());
                    intent.putExtra(Constantes.INSTANCE.getCOLOR_TOP(), UtilsApp.INSTANCE.loadPalette(imageView));
                    startActivity(intent);
                }
            });
            title.setText(topMains.getNome());
        } else {

            Picasso.get().load(UtilsApp.INSTANCE.getBaseUrlImagem(5) + topMains.getImagem())
                    .error(R.drawable.top_empty)
                    .into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), TvShowActivity.class);
                    intent.putExtra(Constantes.INSTANCE.getNOME_TVSHOW(), topMains.getNome());
                    intent.putExtra(Constantes.INSTANCE.getTVSHOW_ID(), topMains.getId());
                    intent.putExtra(Constantes.INSTANCE.getCOLOR_TOP(), UtilsApp.INSTANCE.loadPalette(imageView));
                    startActivity(intent);
                }
            });
            title.setText(topMains.getNome());
        }

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator alphaStar = ObjectAnimator.ofFloat(imageView, "y", -100, 0)
                .setDuration(800);
        animatorSet.playTogether(alphaStar);

        return view;
    }

}
