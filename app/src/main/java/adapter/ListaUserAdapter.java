package adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import activity.ListUserActivity;
import activity.ListaUserActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.TmdbAccount;
import info.movito.themoviedbapi.model.MovieList;
import utils.Constantes;

/**
 * Created by icaro on 14/08/16.
 */
public class ListaUserAdapter extends BaseAdapter {

    private ListUserActivity context;
    private String TAG = "ListaUserAdapter";
    private TmdbAccount.MovieListResultsPage lists;

    public ListaUserAdapter(ListUserActivity listUserActivity, TmdbAccount.MovieListResultsPage lists) {
        this.context = listUserActivity;
        this.lists = lists;
    }


    @Override
    public int getCount() {
        if (lists != null) {
            return lists.getResults().size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        lists.getResults().get(i);
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final MovieList lista_usuario = lists.getResults().get(i);

        View layout = LayoutInflater.from(context).inflate(R.layout.lista_user_adapter, viewGroup, false);
        TextView nome = (TextView) layout.findViewById(R.id.textview_user_name);
        TextView descricao = (TextView) layout.findViewById(R.id.textview_user_descricao);
        TextView tipo = (TextView) layout.findViewById(R.id.textview_tipo);
        ImageView imageView = (ImageView) layout.findViewById(R.id.imageview_lista_user);
        nome.setText(lista_usuario.getName());
        descricao.setText(lista_usuario.getDescription());
        tipo.setText(lista_usuario.getListType().toUpperCase());
        Picasso.with(context).load(R.drawable.lista)
                .into(imageView);

        return layout;
    }
}
