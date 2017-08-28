package utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.google.firebase.auth.FirebaseAuth;

import activity.MainActivity;
import br.com.icaro.filme.R;

/**
 * Created by icaro on 15/09/16.
 */

public class LogoffDialog extends DialogPreference {

    private final FirebaseAuth mAuth;

    public LogoffDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(false);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setTitle(getContext().getResources().getString(R.string.title_logoff_dialog));
        builder.setNegativeButton(getContext().getResources().getString(R.string.cancel), null);
        builder.setMessage(getContext().getResources().getString(R.string.text_logoff_dialog));
        builder.setPositiveButton(getContext()
                .getResources().getString(R.string.sair), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getContext().startActivity(intent);
                mAuth.signOut();
            }
        });
        super.onPrepareDialogBuilder(builder);
    }
}