package adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import activity.FilmeActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.CollectionInfo;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 22/07/16.
 */
public class CollectionPagerAdapter extends PagerAdapter {


    private CollectionInfo info;
    private Context context;
    private ImageView imageView;
    private LinearLayout linearLayout;
    private TextView nome;
    private FirebaseAnalytics firebaseAnalytics;



    public CollectionPagerAdapter(CollectionInfo info, Context context) {
        this.info = info;
        this.context = context;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public int getCount() {
       // Log.d("CollectionPagerAdapter", "getCount");
      //  Log.d("CollectionPagerAdapter", ""+ info.getParts().size());
        return info.getParts().size() > 0 ? info.getParts().size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
      //  Log.d("CollectionPagerAdapter", "isViewFromObject");
        return view == object;

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.collection, container, false);
        nome = (TextView) view.findViewById(R.id.dateCollection);
        linearLayout = (LinearLayout) view.findViewById(R.id.collection_linear);
        imageView = (ImageView) view.findViewById(R.id.img_collection);
        Picasso.with(context)
                .load(UtilsFilme.getBaseUrlImagem(5) + info.getParts().get(position).getPosterPath())
                .error(R.drawable.poster_empty)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        loadPaletteCollection();
                    }

                    @Override
                    public void onError() {

                    }
                });

        imageView.setOnClickListener(this.onClickListenerInterno(position));

       // Log.d("CollectionPagerAdapter", "instantiateItem");
        String ano = "xxxx";
        if (info.getParts().get(position).getReleaseDate() != null) {
            ano = info.getParts().get(position).getReleaseDate();
            ano = ano.substring(0, 4);
        }
        if (info.getParts().get(position).getName() != null) {
            nome.setText(info.getParts().get(position).getName() +
                    " - " + ano);
        }
        ((ViewGroup) container).addView(view);
        return view;

    }

    private void loadPaletteCollection() {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            Palette.Builder builder = new Palette.Builder(bitmap);
            Palette.Swatch swatch = builder.generate().getVibrantSwatch();
            if (swatch != null) {
                linearLayout.setBackgroundColor(swatch.getRgb());
                nome.setTextColor(swatch.getTitleTextColor());
            }
        }
    }

    private View.OnClickListener onClickListenerInterno(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = info.getParts().get(position).getId();
                String title = info.getParts().get(position).getTitle();
                Intent intent = new Intent(getContext(), FilmeActivity.class);
                intent.putExtra(Constantes.FILME_ID, id);
                intent.putExtra(Constantes.NOME_FILME, title);
                getContext().startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "Collection_interno");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, info.getName());
                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, info.getId());
                firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            }
        };
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
       // Log.d("CollectionPagerAdapter", "destroyItem");
        ((ViewPager) container).removeView((View) object);
    }

}