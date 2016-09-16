package utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import activity.FilmeActivity;
import activity.MainActivity;
import applicaton.FilmeApplication;
import br.com.icaro.filme.R;

/**
 * Created by icaro on 15/09/16.
 */

public class LogoffDialog extends DialogPreference {

    public LogoffDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(false);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setTitle(getContext().getResources().getString(R.string.title_logoff_dialog));
        builder.setNegativeButton(getContext().getResources().getString(R.string.cancel), null);
        builder.setMessage(getContext().getResources().getString(R.string.text_logoff_dialog));
        builder.setPositiveButton(getContext().getResources().getString(R.string.sair), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Prefs.apagar(getContext(), Prefs.LOGIN_PASS);
                FilmeApplication.getInstance().setLogado(false);
                Intent intent = new Intent(getContext(), MainActivity.class);
                getContext().startActivity(intent);
            }
        });
        super.onPrepareDialogBuilder(builder);
    }
}