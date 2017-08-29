package adapter;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;

import domain.Lista;
import domain.ViewType;
import oscar.adapter.ListasDelegateAdapter;
import pessoaspopulares.ViewTypeDelegateAdapter;
import pessoaspopulares.adapter.LoadingDelegateAdapter;
import utils.Constantes;

/**
 * Created by icaro on 14/08/16.
 */
public class ListUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static ViewType loading = new ViewType() {
        @Override
        public int getViewType() {
            return 2;
        }
    };
    private FirebaseAnalytics mFirebaseAnalytics;
    private Context context;
    private ArrayList<ViewType> listaResult = new ArrayList<>();
    private SparseArrayCompat<ViewTypeDelegateAdapter> delegateAdapters = new SparseArrayCompat<>();

    public ListUserAdapter(Context listaUserActivity) {
        this.context = listaUserActivity;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        delegateAdapters.put(Constantes.BuscaConstants.INSTANCE.getLOADING(), new LoadingDelegateAdapter());
        delegateAdapters.put(Constantes.BuscaConstants.INSTANCE.getNEWS(), new ListasDelegateAdapter());
        listaResult.add(loading);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return delegateAdapters.get(viewType).onCreateViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        delegateAdapters.get(getItemViewType(position)).onBindViewHolder(holder, listaResult.get(position), context);
    }

    @Override
    public int getItemViewType(int position) {
        return listaResult.get(position).getViewType();
    }


    public void addPersonPopular(Lista listaMedia, int totalPagina ) {

        int initPosition = listaResult.size() - 1;
        this.listaResult.remove(initPosition);
        notifyItemRemoved(initPosition);

        this.listaResult.addAll(listaMedia.getResults());
        notifyItemRangeChanged(initPosition, this.listaResult.size() + 1 /* plus loading item */);
        if (listaResult.size() < totalPagina)
        this.listaResult.add(loading);

    }

    @Override
    public int getItemCount() {
        return listaResult.size();
    }

}
