package fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;

import br.com.icaro.filme.R;
import utils.Constantes;
import utils.UtilsApp;


/**
 * Created by icaro on 12/07/16.
 */

public class PosterScrollFragment extends Fragment {

    private String endereco, nome;
    private ImageView imageView;
    private LinearLayout linear_poster_grid;
    private final static int REQUEST_PERMISSIONS_CODE = 1;

    public static PosterScrollFragment newInstance(String endereco, String nome) {

        PosterScrollFragment posterScrollFragment = new PosterScrollFragment();
        Bundle args = new Bundle();
        args.putString(Constantes.INSTANCE.getENDERECO(), endereco);
        args.putString(Constantes.INSTANCE.getNOME_FILME(), nome);
        posterScrollFragment.setArguments(args);
        return posterScrollFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        endereco = getArguments().getString(Constantes.INSTANCE.getENDERECO()); // não usado!?!?!!
        nome = getArguments().getString(Constantes.INSTANCE.getNOME_FILME());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page_scroll_image, container, false);
        imageView = (ImageView) view.findViewById(R.id.img_poster_scroll);
        Picasso.with(getContext())
                .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(getContext(), 5)) + endereco)
                .into(imageView);

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linear_poster_grid = (LinearLayout) view.findViewById(R.id.linear_poster_grid);
        ImageView compartilhar = (ImageView) view.findViewById(R.id.compartilhar);
        ImageView salvar = (ImageView) view.findViewById(R.id.salvar);

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_PERMISSIONS_CODE:
                for (int i = 0; i < permissions.length; i++) {
                    if (permissions[i].equalsIgnoreCase(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            && grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        salvarImagem();
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private View.OnClickListener salvarImagem() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (getActivity() != null)
                    if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                            Toast.makeText(getContext(), getString(R.string.permitir_acesso_armazenamento), Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);
                        } else {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_CODE);
                        }

                    } else {
                        if (UtilsApp.isExternalStorageWritable()) {
                            salvarArquivoNaMemoriaInterna(getContext(), imageView);
                        } else {
                            Toast.makeText(getContext(), R.string.ops, Toast.LENGTH_LONG).show();
                        }
                    }
            }
        };
    }

    private View.OnClickListener compartilharOnClick() {

        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = salvaImagemMemoriaCache(getContext(), imageView);
                if (file != null) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, nome + "  -  " + "https://q2p5q.app.goo.gl/3hX6" + " by: " + Constantes.INSTANCE.getTWITTER_URL());
                    intent.setType("image/*"); // link dynamic - https://q2p5q.app.goo.gl/3hX6
                    intent.putExtra(Intent.EXTRA_STREAM, UtilsApp.getUriDownloadImage(getContext(), file));
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
        if (getContext() != null) {
            File file = new File(getContext().getExternalCacheDir(), getContext().getPackageName());
            if (file.exists()) {
                getContext().deleteDatabase(file.toString());
            }
        }
    }

    private File salvaImagemMemoriaCache(Context context, ImageView imageView) {
        //USar metodo do BaseActivity
        File file = new File(context.getExternalCacheDir(), context.getPackageName());

        if (!file.exists()) {
            file.mkdir();
            //  Log.e("salvarArqNaMemoriaIn", "Directory created");
        }
        File dir = new File(file, endereco);

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            UtilsApp.writeBitmap(dir, bitmap);
        }
        File file2 = new File(getContext().getExternalCacheDir(), getContext().getPackageName());
        // Log.d("PosterScrollFragment", "onDestroy: "+ file2.toString());
        return dir;
    }

    private File salvarArquivoNaMemoriaInterna(Context context, ImageView imageView) {
        File file = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath(), getResources().getString(R.string.app_name));

        if (!file.exists()) {
            file.mkdir();
        }

        File dir = new File(file, endereco);

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            UtilsApp.writeBitmap(dir, bitmap);
            Toast.makeText(context, R.string.toast_salvar_imagem, Toast.LENGTH_LONG).show();
        }
        return dir;
    }
}
