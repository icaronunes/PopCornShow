package fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import adapter.CalendarAdapter;
import br.com.icaro.filme.R;
import domian.UserTvshow;
import utils.Constantes;

/**
 * Created by icaro on 25/11/16.
 */
public class ListaSeguindoFragment extends Fragment {

    final String TAG = ListaSeguindoFragment.class.getName();
    private List<UserTvshow> userTvshows;
    private int tipo;
    private RecyclerView recyclerViewCalendar;
    private ValueEventListener eventListener;
    private DatabaseReference seguindoDataBase;

    public static Fragment newInstanceMovie(int tipo, List<UserTvshow> userTvshows) {
        ListaSeguindoFragment fragment = new ListaSeguindoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constantes.SEGUINDO, (Serializable) userTvshows);
        bundle.putInt(Constantes.ABA, tipo);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tipo = getArguments().getInt(Constantes.ABA);
            userTvshows = (List<UserTvshow>) getArguments().getSerializable(Constantes.SEGUINDO);
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        seguindoDataBase = database.getReference("users").child(mAuth.getCurrentUser()
                .getUid()).child("seguindo");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        switch (tipo) {

            case 0: {

                return getViewCalendar(inflater, container);
            }
            case 1: {

                return getViewCalendar(inflater, container);
                //return getViewTvShow(inflater, container);
            }
        }
        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userTvshows = new ArrayList<>();
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        userTvshows.add(snapshot.getValue(UserTvshow.class));
                        Log.d(TAG, snapshot.getValue(UserTvshow.class).getNome());
                    }

                    recyclerViewCalendar.setAdapter(new CalendarAdapter(getActivity(), userTvshows));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        seguindoDataBase.addValueEventListener(eventListener);

    }

    private View getViewCalendar(LayoutInflater inflater, ViewGroup container) {
            View view = inflater.inflate(R.layout.temporadas, container, false); // Criar novo layout
            view.findViewById(R.id.progressBarTemporadas).setVisibility(View.GONE);
            recyclerViewCalendar = (RecyclerView) view.findViewById(R.id.temporadas_recycle);
            recyclerViewCalendar.setHasFixedSize(true);
            recyclerViewCalendar.setItemAnimator(new DefaultItemAnimator());
            recyclerViewCalendar.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerViewCalendar.setAdapter(new CalendarAdapter(getActivity(), userTvshows));
            return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (eventListener != null){
            seguindoDataBase.removeEventListener(eventListener);
        }
    }
}
