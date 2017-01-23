package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import activity.ReviewsActivity;
import activity.Site;
import br.com.icaro.filme.R;
import domain.MessageItem;
import domain.ReviewsUflixit;
import utils.Constantes;

/**
 * Created by icaro on 17/07/16.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.FilmeViewHolder> {
    private final String TAG = ReviewsActivity.class.getName();
    Context context;
    private ReviewsUflixit reviews;

    public ReviewsAdapter(Context baseContext, ReviewsUflixit reviews) {
        context = baseContext;
        this.reviews = reviews;
    }

    @Override
    public ReviewsAdapter.FilmeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(context).inflate(R.layout.reviews, parent, false);
        context = view.getContext();
        return new ReviewsAdapter.FilmeViewHolder(view);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(final ReviewsAdapter.FilmeViewHolder holder,  int position) {

        final MessageItem reportagem = reviews.getMessage().get(position);

        holder.author.setText(reportagem.getAttr() != null  ? reportagem.getAttr() : "...");

        holder.reviews_content.setText(reportagem.getLabel());

        if (verificarImportancia(reportagem)) {
            holder.reviews_content.setTextColor(context.getResources().getColor(R.color.gray_reviews));
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Site.class);
                intent.putExtra(Constantes.SITE, reportagem.getUrl());
                context.startActivity(intent);

                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, reportagem.getUrl());
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Reviews");
                bundle.putString("Critito de cinema", reportagem.getAttr() );
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            }
        });
    }

    private boolean verificarImportancia(MessageItem reportagem) {
        List<String> importante = new LinkedList<>(Arrays.asList( //Linkedlist pois Arrays.aslist direto é um array umutavel
                "New York Times",
                "Hugo Gomes",
                "Los Angeles Times",
                "washington post",
                "french",
                "spanish",
                "india",
                "italian",
                "Hollywood News",
                "rolling stone",
                "Metro us",
                "cbs news",
                "pt-br",
                "portugueses",
                "portuguese",
                "brasil",
                Locale.getDefault().getDisplayName(),
                Locale.getDefault().getDisplayCountry(),
                Locale.getDefault().getDisplayLanguage()
        ));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            importante.add(Locale.getDefault().toLanguageTag());
        }

        for (String s : importante) {

            if (reportagem.getLabel().toLowerCase().contains(s.toLowerCase())){
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return reviews.getMessage().size() > 0 ? reviews.getMessage().size() : 0;
    }

    public class FilmeViewHolder extends RecyclerView.ViewHolder {
        TextView author, reviews_content;
        CardView cardView;

        public FilmeViewHolder(View itemView) {
            super(itemView);
            author = (TextView) itemView.findViewById(R.id.author);
            reviews_content = (TextView) itemView.findViewById(R.id.content_reviews);
            cardView = (CardView) itemView.findViewById(R.id.card_view_reviews);
        }
    }
}
