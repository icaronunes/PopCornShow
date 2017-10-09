package activity;

import domain.UserTvshow;
import domain.tvshow.Tvshow;

/**
 * Created by root on 08/10/17.
 */

public class teste {


    private UserTvshow userTvshow;
    private Tvshow series;

//    private void  atualizarRealDate() {
//        try {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    TmdbTvSeasons tvSeasons = new TmdbApi(Config.TMDB_API_KEY).getTvSeasons();
//
//                    userTvshow = UtilsApp.setUserTvShow(series);
//
//                    for (int i = 0; i < series.getSeasons().size(); i++) {
//                        SeasonsItem tvS = series.getSeasons().get(i);
//                        TvSeason tvSeason = tvSeasons.getSeason(series.getId(), tvS.getSeasonNumber(), "en", TmdbTvSeasons.SeasonMethod.external_ids); //?
//                        userTvshow.getSeasons().get(i).setUserEps(setEp(tvSeason));
//                        // Atualiza os eps em userTvShow
//                    }
//
//                    for (int i = 0; i < userTvshowOld.getSeasons().size(); i++) {
//                        userTvshow.getSeasons().get(i).setId(userTvshowOld.getSeasons().get(i).getId());
//                        userTvshow.getSeasons().get(i).setSeasonNumber(userTvshowOld.getSeasons().get(i).getSeasonNumber());
//                        userTvshow.getSeasons().get(i).setVisto(userTvshowOld.getSeasons().get(i).isVisto());
//                        //Atualiza somente os campos do temporada em userTvShow
//                    }
//
//                    for (int i = 0; i < userTvshowOld.getSeasons().size(); i++) {
//                        //  Log.d(TAG, "Numero de eps - "+ userTvshow.getSeasons().get(i).getUserEps().size());
//                        if (userTvshow.getSeasons().get(i).getUserEps().size() > userTvshowOld.getSeasons().get(i).getUserEps().size()) {
//                            userTvshow.getSeasons().get(i).setVisto(false);
//                            // Se huver novos ep. coloca temporada com não 'vista'
//                        }
//                        for (int i1 = 0; i1 < userTvshowOld.getSeasons().get(i).getUserEps().size(); i1++) {
//                            if (i1 < userTvshow.getSeasons().get(i).getUserEps().size())
//                                userTvshow.getSeasons().get(i).getUserEps().set(i1, userTvshowOld.getSeasons().get(i).getUserEps().get(i1));
//                            //  Log.d(TAG, "run: EPS "+ i1);
//                            //coloca as informações antigas na nova versão dos dados.
//                        }
//                    }
//
//                    final DatabaseReference myRef = database.getReference("users");
//                    myRef.child(mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "")
//                            .child("seguindo")
//                            .child(String.valueOf(series.getId()))
//                            .setValue(userTvshow)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        seguindo = true;
//                                        setupViewPagerTabs();
//                                        setTitle();
//                                        setImageTop();
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                Toast.makeText(TvShowActivity.this, R.string.season_updated, Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                        //
//                                    }
//                                }
//                            });
//                }
//            }).start();
//        } catch (Exception e){
//            FirebaseCrash.report(e);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(TvShowActivity.this, R.string.ops_seguir_novamente, Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }


}
