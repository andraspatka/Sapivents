package szamtech.fejer_patka.ms.sapientia.ro.sapivents.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


public class LoadingDialogUtil extends Activity {

    private final Context mContext;
    private Dialog mLoadingDialog = null;
    private View view = null;

    public LoadingDialogUtil(Context context) {

        mContext = context;
        mLoadingDialog = loadingDialog();
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setCancelable(false);
    }

    private Dialog loadingDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        // Get the layout inflater
        LayoutInflater inflater = ((Activity)mContext).getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        view = inflater.inflate(szamtech.fejer_patka.ms.sapientia.ro.sapivents.R.layout.loading_dialog, null);
        builder.setView(view);

        return builder.create();
    }

    public void showDialog(){

        if(mLoadingDialog != null) {
            mLoadingDialog.show();
        }

    }

    public void endDialog(){
        if(mLoadingDialog != null) {

            if (mLoadingDialog.isShowing()) {

                mLoadingDialog.dismiss();
                mLoadingDialog = null;

            }

        }
    }

    public void setDialogText(String text){
        if(!text.isEmpty()){

            TextView txtView = (TextView) view.findViewById(szamtech.fejer_patka.ms.sapientia.ro.sapivents.R.id.loading_dialog_text_view);
            txtView.setText(text);

        }
    }
}
