package activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import adapter.DesenvolvimentoAdapater;
import br.com.icaro.filme.R;

/**
 * Created by icaro on 18/12/16.
 */
public class Desenvolvimento extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.desenvolvimento_layout);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.ajuda_desenvolvimento);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleView_desenvolvimento);
        recyclerView.setLayoutManager(new LinearLayoutManager(Desenvolvimento.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        Resources res = getResources();
        String[] planets = res.getStringArray(R.array.planets_array);

        recyclerView.setAdapter(new DesenvolvimentoAdapater(this, planets));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (android.R.id.home == item.getItemId()){
            finish();
        }
        return true;
    }
}
