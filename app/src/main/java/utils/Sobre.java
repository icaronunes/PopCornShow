package utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import br.com.icaro.filme.R;
import desenvolvimento.Desenvolvimento;
import site.Site;

/**
 * Created by icaro on 30/09/16.
 */

public class Sobre extends DialogPreference {

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
        LinearLayout linearLayout = view.findViewById(R.id.play_rated);
        LinearLayout desenvolvimento = view.findViewById(R.id.development);
        LinearLayout twitter = view.findViewById(R.id.twitter);
        ImageView tmdb = view.findViewById(R.id.img_tmdb);
        ImageView popcorn = view.findViewById(R.id.img_popcorn);

        popcorn.setOnClickListener(new View.OnClickListener() {
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

        tmdb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Site.class);
                intent.putExtra(Constant.SITE, "https://www.themoviedb.org/");
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

        desenvolvimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().startActivity(new Intent(getContext(), Desenvolvimento.class));
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Site.class);
                intent.putExtra(Constant.SITE, "https://twitter.com/appopcorn");
                getContext().startActivity(intent);
            }
        });

    }

}



