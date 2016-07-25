package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.MovieDb;
import utils.Constantes;


/**
 * Created by icaro on 03/07/16.
 */
public class FilmeFragment extends Fragment {

    //************* Alguns metodos senco chamados 2 vezes

    int id_filme;
    String titulo;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            id_filme = getArguments().getInt(Constantes.FILME_ID);
            titulo = getArguments().getString(Constantes.NOME_FILME);
            Log.d("FilmeFragment", "onCreate Titulo: " + titulo);
            Log.d("FilmeFragment", "onCreate " + id_filme);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_container_filme, container, false);
        return view;
    }

}
