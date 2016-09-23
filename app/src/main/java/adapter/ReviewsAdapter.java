package adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.Reviews;

/**
 * Created by icaro on 17/07/16.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.FilmeViewHolder> {
    Context context;
    List<Reviews> reviewses;

    public ReviewsAdapter(Context baseContext, List<Reviews> reviewses) {
        context = baseContext;
        this.reviewses = reviewses;
    }

    @Override
    public ReviewsAdapter.FilmeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(context).inflate(R.layout.reviews, parent, false);
        context = view.getContext();
        return new ReviewsAdapter.FilmeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewsAdapter.FilmeViewHolder holder, final int position) {
        holder.author.setText(reviewses.get(position).getAuthor());
        holder.reviews_content.setText(reviewses.get(position).getContent());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(reviewses.get(position).getUrl()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewses.size() > 0 ? reviewses.size() : 0;
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
