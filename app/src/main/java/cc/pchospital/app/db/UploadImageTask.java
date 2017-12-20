package cc.pchospital.app.db;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.upyun.library.common.Params;
import com.upyun.library.common.UploadEngine;
import com.upyun.library.listener.UpCompleteListener;
import com.upyun.library.utils.UpYunUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import cc.pchospital.app.AddTicketActivity;
import cc.pchospital.app.R;
import cc.pchospital.app.util.HttpUtil;
import cc.pchospital.app.util.Ticket;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;


public class UploadImageTask extends AsyncTask<String, Integer, Boolean> {

    private WeakReference<AddTicketActivity> context;

    private AlertDialog processDialog;
    private WeakReference<ProgressBar> progressBar;

    private Boolean res = true;

    private Ticket newTicket;

    public UploadImageTask(AddTicketActivity context, Ticket newTicket) {
        this.context = new WeakReference<>(context);
        this.newTicket = newTicket;
    }


    @Override
    protected void onPreExecute() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context.get());
        dialog.setView(R.layout.dialog_uploading_image)
                .setCancelable(false);
        processDialog = dialog.create();
        processDialog.show();
        progressBar = new WeakReference<>((ProgressBar) processDialog.findViewById(R.id.progressBar));
        progressBar.get().setMax(context.get().imagePath.size());
        progressBar.get().setProgress(0);
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        if (!pushTicket(strings[0])) {
            return false;
        }

        String SPACE = "pchospital";

        String OPERATER = "ike";

        String PASSWORD = "qq332578653";

        String serverPrefix = "https://pchospital.b0.upaiyun.com";

        for (int i = 0; i < context.get().imagePath.size(); i++) {
            String savePath = "/uploads/"+ newTicket.getTicketId() +"/" + i + "{.suffix}";
            File picture = new File(context.get().imagePath.get(i));
            String filename = picture.getName();
            String postFix = filename.substring(filename.lastIndexOf("."));
            final Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put(Params.BUCKET, SPACE);
            paramsMap.put(Params.SAVE_KEY, savePath);
            paramsMap.put(Params.CONTENT_MD5, UpYunUtils.md5Hex(picture));


            UpCompleteListener completeListener = new UpCompleteListener() {
                @Override
                public void onComplete(boolean isSuccess, String result) {
                    if (isSuccess) {
                        publishProgress();
                    } else {
                        res = false;
                    }
                }
            };

            UploadEngine.getInstance()
                    .formUpload(picture, paramsMap, OPERATER,
                            UpYunUtils.md5(PASSWORD), completeListener, null);

            String addPictureURL = HttpUtil.buildURL(
                    context.get().getString(R.string.app_network_server_ip),
                    context.get().getString(R.string.app_network_add_picture_page),
                    context.get().getString(R.string.app_db_picture_tid),
                    newTicket.getTicketId(),
                    context.get().getString(R.string.app_db_picture_url),
                    serverPrefix + "/uploads/"+ newTicket.getTicketId() +"/" + i + postFix
                    );
            try {
                Response response = HttpUtil.sendGetOkHttpRequest(addPictureURL);
                String data = response.body().string();
                if (data.trim().length() != 0) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }

        }

        return res;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressBar.get().incrementProgressBy(1);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (aBoolean) {
            Toast.makeText(context.get(),
                    context.get().getString(R.string.toast_add_ticket_success),
                    Toast.LENGTH_SHORT)
                    .show();
            context.get().finish();
        } else {
            Toast.makeText(context.get(),
                    context.get().getString(R.string.toast_add_ticket_failed),
                    Toast.LENGTH_SHORT)
                    .show();
            context.get().submit.setText(context.get().getString(R.string.button_add_ticket_send));
            context.get().submit.setEnabled(true);
        }
        processDialog.dismiss();
    }

    @NonNull
    private Boolean pushTicket(String url) {
        AddTicketActivity activity = context.get();

        RequestBody body = new FormBody.Builder()
                .add(activity.getString(R.string.app_db_ticket_uid), newTicket.getUserId())
                .add(activity.getString(R.string.app_db_ticket_name), newTicket.getTicketName())
                .add(activity.getString(R.string.app_db_ticket_phone), newTicket.getTicketPhone())
                .add(activity.getString(R.string.app_db_ticket_location), newTicket.getTicketLocation())
                .add(activity.getString(R.string.app_db_ticket_location_la), newTicket.getTicketLocationLa().toString())
                .add(activity.getString(R.string.app_db_ticket_location_lo), newTicket.getTicketLocationLo().toString())
                .add(activity.getString(R.string.app_db_ticket_note), newTicket.getTicketNote())
                .add(activity.getString(R.string.app_db_ticket_date), newTicket.getTicketDate().toString())
                .add(activity.getString(R.string.app_db_ticket_state), newTicket.getTicketStates())
                .build();

        try {
            Response response = HttpUtil.sendPostOkHttpRequest(url, body);
            String responseData = response.body().string();
            if (responseData == null){
                return false;
            }

            newTicket.setTicketId(Integer.parseInt(responseData.trim()) + "");
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}
