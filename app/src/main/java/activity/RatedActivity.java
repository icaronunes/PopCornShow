package activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import adapter.RatedAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 03/08/16.
 */
public class RatedActivity extends BaseActivity {

    RecyclerView recyclerView;
    MovieResultsPage rated;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(getIntent()
                .getIntExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.avaliados)));
        setCheckable(getIntent().getIntExtra(Constantes.ABA, 0));
        progressBar = (ProgressBar) findViewById(R.id.progress);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_favorite);
        recyclerView.setLayoutManager(new GridLayoutManager(RatedActivity.this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new TMDVAsync().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }

    private RatedAdapter.RatedOnClickListener onclickListerne() {
        return new RatedAdapter.RatedOnClickListener() {


            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(RatedActivity.this, FilmeActivity.class);

                ImageView imageView = (ImageView) view;
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                if (drawable != null) {
                    Bitmap bitmap = drawable.getBitmap();
                    Palette.Builder builder = new Palette.Builder(bitmap);
                    Palette palette = builder.generate();
                    for (Palette.Swatch swatch : palette.getSwatches()) {
                        intent.putExtra(Constantes.COLOR_TOP, swatch.getRgb());
                    }
                }
                intent.putExtra(Constantes.FILME_ID, rated.getResults().get(position).getId());
                intent.putExtra(Constantes.NOME_FILME, rated.getResults().get(position).getTitle());
                startActivity(intent);
            }

            @Override
            public void onClickLong(View view, final int position) {
                Log.d("setupNavDrawer", "Login");
                final boolean[] status = {false};
                final Dialog alertDialog = new Dialog(RatedActivity.this);
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(R.layout.adialog_custom_rated);

                Button ok = (Button) alertDialog.findViewById(R.id.ok_rated);
                final RatingBar ratingBar = (RatingBar) alertDialog.findViewById(R.id.ratingBar_rated);
                int width = getResources().getDimensionPixelSize(R.dimen.popup_width); //Criar os Dimen do layout do login - 300dp - 300dp ??
                int height = getResources().getDimensionPixelSize(R.dimen.popup_height_rated);
                Log.d(TAG, "Valor Rated" + rated.getResults().get(position).getUserRating());
                ratingBar.setRating(rated.getResults().get(position).getUserRating() / 2);

                alertDialog.getWindow().setLayout(width, height);

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "Adialog Rated");
                        final ProgressDialog progressDialog = new ProgressDialog(RatedActivity.this,
                                android.R.style.Theme_Material_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Salvando...");
                        progressDialog.show();

                        new Thread() {
                            @Override
                            public void run() {
                                if (UtilsFilme.isNetWorkAvailable(RatedActivity.this)) {
                                    status[0] = FilmeService.setRatedMovie(rated.getResults().get(position).getId(), ratingBar.getRating() * 2);
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (status[0]) {
                                            Toast.makeText(RatedActivity.this, getResources().getString(R.string.filme_rated), Toast.LENGTH_SHORT)
                                                    .show();
                                            RecyclerView.ViewHolder view = recyclerView.findViewHolderForAdapterPosition(position);
                                            TextView textView = (TextView) view.itemView.findViewById(R.id.text_rated_favoritos);
                                            String valor = String.valueOf((ratingBar.getRating() * 2));
                                            textView.setText(valor);

                                        } else {
                                            Toast.makeText(RatedActivity.this, getResources().getString(R.string.falha_rated), Toast.LENGTH_SHORT).show();
                                        }
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        }.start();

                        alertDialog.dismiss();
                    }

                });

                alertDialog.show();
                recyclerView.getAdapter().notifyItemChanged(position);
            }
        };
    }

    private class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            rated = FilmeService.getRatedListTotal();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            recyclerView.setAdapter(new RatedAdapter(RatedActivity.this,
                    rated != null ? rated.getResults() : null, onclickListerne()));
        }
    }
}
