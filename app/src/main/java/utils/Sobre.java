package utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import activity.Site;
import br.com.icaro.filme.R;

/**
 * Created by icaro on 30/09/16.
 */

public class Sobre extends DialogPreference {

    private LinearLayout linearLayout;
    private ImageView tmdb;

    public Sobre(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(false);
        setPositiveButtonText(null);
        setNegativeButtonText(null);
        setDialogLayoutResource(R.layout.sobre);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        linearLayout = (LinearLayout) view.findViewById(R.id.play_rated);
        tmdb = (ImageView) view.findViewById(R.id.img_tmdb);

        tmdb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Site.class);
                intent.putExtra(Constantes.SITE, "https://www.themoviedb.org/");
                getContext().startActivity(intent);
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = getContext().getPackageName(); // getPackageName() from Context or Activity object
                try {
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
    }

}



