package desenvolvimento.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.icaro.filme.R;

/**
 * Created by icaro on 18/12/16.
 */
public class DesenvolvimentoAdapater extends RecyclerView.Adapter<DesenvolvimentoAdapater.HolderDesenvolvimento> {

    private Context desenvolvimento;
    private String[] planets;

    public DesenvolvimentoAdapater(Context desenvolvimento, String[] planets) {
        this.desenvolvimento = desenvolvimento;
        this.planets = planets;
    }

    @Override
    public HolderDesenvolvimento onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(desenvolvimento).inflate(R.layout.desenvolvimento_adapter_layout, parent, false);
        DesenvolvimentoAdapater.HolderDesenvolvimento holder = new DesenvolvimentoAdapater.HolderDesenvolvimento(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(HolderDesenvolvimento holder, int position) {
        final String texto = planets[position];

        holder.textView.setText(texto);

    }

    @Override
    public int getItemCount() {
        return planets.length;
    }

    class HolderDesenvolvimento extends RecyclerView.ViewHolder {

        private TextView textView;

        HolderDesenvolvimento(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textview_desenvolvimento);
        }
    }
}
