package fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.squareup.picasso.Picasso;

import activity.FilmeActivity;
import activity.MainActivity;
import activity.TvShowActivity;
import br.com.icaro.filme.BuildConfig;
import br.com.icaro.filme.R;
import domian.TopMain;
import info.movito.themoviedbapi.model.Multi;
import utils.Constantes;
import utils.UtilsFilme;

import static android.R.attr.id;

/**
 * Created by icaro on 26/07/16.
 */
public class ImagemTopScrollFragment extends Fragment {

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    TopMain topMains;
    private static final String ID_MAIN = "id_main";
    private static final String DESCRICAO = "descricao";
    private TextView destaque;


    public static Fragment newInstance(TopMain topMainList) {
        ImagemTopScrollFragment topScrollFragment = new ImagemTopScrollFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constantes.MAIN, topMainList);
        topScrollFragment.setArguments(bundle);

        return topScrollFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topMains = (TopMain) getArguments().getSerializable(Constantes.MAIN);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        //mFirebaseRemoteConfig.setDefaults(R.xml.xml_defaults);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_scroll_image_top, container, false);
        final ImageView imageView = (ImageView) view.findViewById(R.id.img_top_scroll);
        TextView title = (TextView) view.findViewById(R.id.title);
        destaque = (TextView) view.findViewById(R.id.destaque);
        teste();

        if (topMains.getMediaType().equalsIgnoreCase(Multi.MediaType.MOVIE.name())) {
            Log.d("ImagemTopScrollFragment", "Movie " + topMains.getNome());
            Picasso.with(getContext()).load(UtilsFilme.getBaseUrlImagem(4) + topMains.getImagem())
                    .error(R.drawable.top_empty)
                    .into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), FilmeActivity.class);
                    intent.putExtra(Constantes.NOME_FILME, topMains.getNome());
                    intent.putExtra(Constantes.FILME_ID, topMains.getId());
                    intent.putExtra(Constantes.COLOR_TOP, UtilsFilme.loadPalette(imageView));
                    startActivity(intent);
                }
            });
            title.setText(topMains.getNome());
        } else {
            Log.d("ImagemTopScrollFragment", "TVshow " + topMains.getNome());
            Picasso.with(getContext()).load(UtilsFilme.getBaseUrlImagem(4) + topMains.getImagem())
                    .error(R.drawable.top_empty)
                    .into(imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), TvShowActivity.class);
                    intent.putExtra(Constantes.NOME_TVSHOW, topMains.getNome());
                    intent.putExtra(Constantes.TVSHOW_ID, topMains.getId());
                    intent.putExtra(Constantes.COLOR_TOP, UtilsFilme.loadPalette(imageView));
                    startActivity(intent);
                }
            });
            title.setText(topMains.getNome());
        }

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator alphaStar = ObjectAnimator.ofFloat(imageView, "y", -100, 0)
                .setDuration(900);
        animatorSet.playTogether(alphaStar);

        return view;
    }

    private void teste(){

        long cacheExpiration = 3600; // 1 hour in seconds.
        // If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from
        // the server.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                       // Log.d("fetch", "sucesso" );
                            // Once the config is successfully fetched it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
//                            Toast.makeText(getActivity(), "Fetch Failed",
//                                    Toast.LENGTH_SHORT).show();
                        }
                        displayPrice();
                    }
                });
    }

    private void displayPrice() {
        String descricao = mFirebaseRemoteConfig.getString(DESCRICAO);
        int id_main = (int) mFirebaseRemoteConfig.getLong(ID_MAIN);

        if (id_main == topMains.getId()){
            Log.d("fetch", "entrou" );
            destaque.setVisibility(View.VISIBLE);
            destaque.setText(descricao);
        }
    }

}
