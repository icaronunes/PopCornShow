package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.tv.TvEpisode;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.Constantes;

import static com.google.android.gms.wearable.DataMap.TAG;

/**
 * Created by icaro on 27/08/16.
 */
public class EpsodioFragment extends Fragment {
    int position;
    TvSeries tvSeries;

    public static Fragment newInstance(TvEpisode tvEpisode, int position) {

        EpsodioFragment fragment = new EpsodioFragment();
        Bundle bundle = new Bundle();
        Log.d("TvShowFragment", "Series " + tvEpisode.getName());
        bundle.putSerializable(Constantes.EPSODIO, tvEpisode);
        bundle.putInt(Constantes.ABA, position);
        fragment.setArguments(bundle);

        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(Constantes.ABA);
            tvSeries = (TvSeries) getArguments().getSerializable(Constantes.SERIE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.epsodio_fragment, container, false);

        return view;
    }

}
