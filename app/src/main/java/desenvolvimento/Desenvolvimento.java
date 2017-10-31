package desenvolvimento;

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import activity.BaseActivity;
import br.com.icaro.filme.R;
import desenvolvimento.adapter.DesenvolvimentoAdapater;

/**
 * Created by icaro on 18/12/16.
 */
public class Desenvolvimento extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.desenvolvimento_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.ajuda_desenvolvimento);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleView_desenvolvimento);
        recyclerView.setLayoutManager(new LinearLayoutManager(Desenvolvimento.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        Resources res = getResources();
        String[] bibliotecas = res.getStringArray(R.array.bibliotecas);

        recyclerView.setAdapter(new DesenvolvimentoAdapater(this, bibliotecas));
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
