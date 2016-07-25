//package adapter;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentStatePagerAdapter;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
//import br.com.icaro.filme.R;
//import info.movito.themoviedbapi.model.people.PersonCast;
//
///**
// * Created by icaro on 05/07/16.
// */
//public class TestAdapter extends FragmentStatePagerAdapter {
//
//    List<PersonCast> list;
//    int position;
//    int position2;
//
//
//    public TestAdapter(FragmentManager childFragmentManager, List<PersonCast> list) {
//        super(childFragmentManager);
//        this.list = list;
//        Log.d("LIST", "TAMANHO DA LISTA " + list.size());
//    }
//
//
//    @Override
//    public int getCount() {
//        if (list == null) {
//            return 0;
//        } else {
//            return list.size();
//        }
//    }
//
//    @Override
//    public Fragment getItem(int position) {
//        Log.d("TestAdapter", "getItem");
//        this.position = position;
//        PagerViewImagemFilme pagerViewImagemFilme = new PagerViewImagemFilme();
//        return pagerViewImagemFilme.newInstance(position);//????????????????
//
//    }
//
//    public String getStringFilme() {
//        Log.d("TestAdapter", "getStringFilme");
//        String urlBase = "http://image.tmdb.org/t/p/";
//        final StringBuilder stringBuilder = new StringBuilder(urlBase);
//        stringBuilder.append("/")
//                .append("w185");
//        Log.d("onViewCreated", stringBuilder.toString());
//        return stringBuilder.toString();
//    }
//
//    //################################################################
//
//    public class PagerViewImagemFilme extends Fragment {
//
//        ImageView imageView;
//        TextView textCast;
//        int position;
//
//        public PagerViewImagemFilme newInstance(int position) {
//            Log.d("PagerViewImagemFilme", "newInstance");
//            PagerViewImagemFilme pagerViewImagemFilme = new PagerViewImagemFilme();
//            Bundle args = new Bundle();
//            args.putInt("position", position);
//            pagerViewImagemFilme.setArguments(args);
//            Log.d("instantiateItem", "PagerViewImagemFilme");
//            return pagerViewImagemFilme;
//        }
//
//        @Override
//        public void onCreate(@Nullable Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            Log.d("PagerViewImagemFilme", "onCreate");
//            position2 = getArguments().getInt("position");
//        }
//
//        @Nullable
//        @Override
//        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//            Log.d("PagerViewImagemFilme", "onCreateView");
//            View view = inflater.inflate(R.layout.scroll_elenco, container, false);
//            textCast = (TextView) view.findViewById(R.id.textCast);
//            //imageView = (ImageView) view.findViewById(R.id.imgPager);
//
//            return view;
//        }
//
//        @Override
//        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//            Log.d("PagerViewImagemFilme", "onViewCreated");
//            textCast.setText(list.get(position2).getName());
//            Log.d("PagerViewImagemFilme", list.get(position2).getName());
//            Picasso.with(getContext()).load(getStringFilme() + list.get(position2).getProfilePath()).into(imageView);
//            Log.d("PagerViewImagemFilme", "" + getStringFilme() + list.get(position2).getProfilePath());
//
//        }
//    }
//}
//
