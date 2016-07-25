package fragment;

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
 * Created by icaro on 12/07/16.
 */

public class PosterScrollFragment extends Fragment {

    int pagina;
    String endereco;
    int tamanho;


    public static PosterScrollFragment newInstance(int pagina, String id_filme, int tamanho) {

        PosterScrollFragment posterScrollFragment = new PosterScrollFragment();
        Bundle args = new Bundle();
        args.putInt(Constantes.PAGINAS, pagina);
        args.putString(Constantes.ENDERECO, id_filme);
        args.putInt(Constantes.TAMANHO, tamanho);
        posterScrollFragment.setArguments(args);
        Log.d("PosterScrollFragment", "newInstance: -> " + id_filme);
        return posterScrollFragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pagina = getArguments().getInt(Constantes.PAGINAS, 0) == 0 ? 1 :getArguments().getInt(Constantes.PAGINAS, 0) ;
        endereco = getArguments().getString(Constantes.ENDERECO);
        tamanho = 1+getArguments().getInt(Constantes.TAMANHO);
        Log.d("PosterScrollFragment", "onCreate: -> " + endereco);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_scroll_image, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.img_poster_scroll);
        Picasso.with(getContext()).load(UtilsFilme.getBaseUrlImagem(4) + endereco).into(imageView);
        Log.d("PosterScrollFragment", "onCreateView: -> " + endereco);


        return view;
    }

}
