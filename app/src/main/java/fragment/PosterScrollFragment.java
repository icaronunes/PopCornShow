package fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import java.io.File;

import br.com.icaro.filme.R;
import utils.Constantes;
import utils.UtilsFilme;


/**
 * Created by icaro on 12/07/16.
 */

public class PosterScrollFragment extends Fragment {

    String endereco, nome;
    ImageView imageView;
    LinearLayout linear_poster_grid;
    ImageView compartilhar;
    ImageView salvar;
    FirebaseAnalytics firebaseAnalytics;


    public static PosterScrollFragment newInstance(String endereco, String nome) {

        PosterScrollFragment posterScrollFragment = new PosterScrollFragment();
        Bundle args = new Bundle();
        args.putString(Constantes.ENDERECO, endereco);
        args.putString(Constantes.NOME_FILME, nome);
        posterScrollFragment.setArguments(args);
      //  Log.d("PosterScrollFragment", "newInstance: -> " + endereco);
        return posterScrollFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        endereco = getArguments().getString(Constantes.ENDERECO); // não usado!?!?!!
        nome = getArguments().getString(Constantes.NOME_FILME);
       // Log.d("PosterScrollFragment", "onCreate: -> " + endereco);
        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_scroll_image, container, false);
        imageView = (ImageView) view.findViewById(R.id.img_poster_scroll);
        Picasso.with(getContext()).load(UtilsFilme.getBaseUrlImagem(5) + endereco).into(imageView);

       // Log.d("PosterScrollFragment", "onCreateView: -> " + endereco);

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linear_poster_grid = (LinearLayout) view.findViewById(R.id.linear_poster_grid);
        compartilhar = (ImageView) view.findViewById(R.id.compartilhar);
        salvar = (ImageView) view.findViewById(R.id.salvar);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (linear_poster_grid.getVisibility() == View.INVISIBLE) {
                        linear_poster_grid.setVisibility(View.VISIBLE);
                    } else {
                        linear_poster_grid.setVisibility(View.INVISIBLE);
                    }
                }
            });

            compartilhar.setOnClickListener(compartilharOnClick());

            salvar.setOnClickListener(salvarImagem());
        }

    }

    private View.OnClickListener salvarImagem() {
        Bundle bundle = new Bundle();
        bundle.putString("Download_Imagem", PosterScrollFragment.this.getClass().getName());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, nome);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UtilsFilme.isExternalStorageWritable()) {
                    salvarArquivoNaMemoriaInterna(getContext(), imageView);
                } else {
                  //  Log.e("salvarArqNaMemoriaIn", "Directory not created");
                }
            }

        };
    }

    private View.OnClickListener compartilharOnClick() {

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Event.SHARE, PosterScrollFragment.this.getClass().getName());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, nome);

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = salvaImagemMemoriaCache(getContext(), imageView);
                if (file != null) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    //final String appPackageName = getContext().getPackageName();
                    intent.putExtra(Intent.EXTRA_TEXT, nome + "  -  " + "https://q2p5q.app.goo.gl/3hX6" + " by: " + Constantes.TWITTER_URL);
                    intent.setType("image/*"); // link dynamic - https://q2p5q.app.goo.gl/3hX6
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    startActivity(Intent.createChooser(intent, getResources().getString(R.string.compartilhar_filme)));
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.erro_na_gravacao_imagem), Toast.LENGTH_SHORT).show();
                }
        //Avaliar se  é melhor usar, o campartilhamento usado em Tvshowactivity e FilmeActivity
            }

        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        File file = getContext().getExternalCacheDir();
        if (file != null && file.exists()) {
            getContext().deleteDatabase(getContext().getExternalCacheDir().getPath()); //Funciona??????
        }
    }

    private File salvaImagemMemoriaCache(Context context, ImageView imageView) {
        //USar metodo do BaseActivity
        File file = context.getExternalCacheDir();

        if (file != null) {
            if (!file.exists()) {
                file.mkdir();
                //  Log.e("salvarArqNaMemoriaIn", "Directory created");
            }
        }
        File dir = new File(file, endereco);

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            UtilsFilme.writeBitmap(dir, bitmap);
        }
        return dir;
    }

    private File salvarArquivoNaMemoriaInterna(Context context, ImageView imageView) {
        File file = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() );
                //"/Filme");

        if (file != null) {
            if (!file.exists()) {
                file.mkdir();
                //  Log.e("salvarArqNaMemoriaIn", "Directory created");
            }
        }
        File dir = new File(file, endereco);

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            UtilsFilme.writeBitmap(dir, bitmap);
            Toast.makeText(context, R.string.toast_salvar_imagem, Toast.LENGTH_LONG).show();
        }
        return dir;
    }
}
