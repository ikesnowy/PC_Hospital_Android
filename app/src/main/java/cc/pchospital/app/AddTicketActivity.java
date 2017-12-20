package cc.pchospital.app;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import cc.pchospital.app.db.PushTicketTask;
import cc.pchospital.app.db.UploadImageTask;
import cc.pchospital.app.util.HttpUtil;
import cc.pchospital.app.util.Ticket;

public class AddTicketActivity extends AppCompatActivity {

    public AMapLocationClient client;
    public AMapLocationListener listener;
    private Ticket newTicket;
    public  Button submit;
    private Button getLocation;
    private View.OnClickListener getLocationListener;

    private int imageSum = 3;
    public List<Uri> imageUri;
    public List<String> imagePath;
    private AppCompatImageButton[] images;
    private AppCompatImageButton addPhoto;

    private static final String TAG = "AddTicketActivity";

    private static final int PICK_PHOTO_FROM_CAMERA = 1;
    private static final int PICK_PHOTO_FROM_ALBUM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ticket);

        newTicket = new Ticket();

        // UI-Elements
        Toolbar toolbar = findViewById(R.id.toolBar);
        final TextInputEditText name = findViewById(R.id.name);
        final TextInputEditText phone = findViewById(R.id.phone);
        final TextInputEditText location = findViewById(R.id.location);
        final TextInputEditText notes = findViewById(R.id.note);
        getLocation = findViewById(R.id.get_location);
        submit = findViewById(R.id.send);


        // UI-ToolBar
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.title_add_ticket));
        }

        // UI-Auto-Fill
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        name.setText(preferences.getString(getString(R.string.app_db_user_uname), ""));
        phone.setText(preferences.getString(getString(R.string.app_db_user_uphone), ""));

        newTicket.setUserId(preferences.getString(getString(R.string.app_db_user_uid), ""));
        newTicket.setTicketDate(System.currentTimeMillis());

        // UI-LocationButton
        getLocationListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!requestPermissions()) {
                    return;
                }
                getLocation.setText(getString(R.string.button_add_ticket_finding_location));
                getLocation.setEnabled(false);
                client = new AMapLocationClient(getApplicationContext());
                listener = new AMapLocationListener() {
                    @Override
                    public void onLocationChanged(AMapLocation aMapLocation) {
                        if (aMapLocation != null){
                            if (aMapLocation.getErrorCode() == 0){
                                newTicket.setTicketLocationLa(aMapLocation.getLatitude());
                                newTicket.setTicketLocationLo(aMapLocation.getLongitude());
                                String address = aMapLocation.getDistrict() + aMapLocation.getStreet();
                                getLocation.setText(address);
                                getLocation.setEnabled(true);
                            } else {
                                Toast.makeText(AddTicketActivity.this,
                                        getString(R.string.toast_add_ticket_no_gps),
                                        Toast.LENGTH_SHORT)
                                        .show();
                                Log.e(TAG, "onLocationChanged: " + aMapLocation.getErrorInfo());
                                getLocation.setEnabled(true);
                            }
                        }
                    }
                };
                client.setLocationListener(listener);
                AMapLocationClientOption option = new AMapLocationClientOption();
                option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                option.setOnceLocation(true);
                option.setOnceLocationLatest(true);
                option.setNeedAddress(true);

                client.setLocationOption(option);
                client.startLocation();
            }
        };
        getLocation.setOnClickListener(getLocationListener);

        // UI-Submit Button
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().length() == 0) {
                    name.setError(getString(R.string.error_add_ticket_no_name));
                    return;
                }
                if (phone.getText().length() != 11) {
                    phone.setError(getString(R.string.error_add_ticket_invalid_phone_number));
                    return;
                }
                if (location.getText().length() > 80) {
                    location.setError(getString(R.string.error_add_ticket_location_too_long));
                    return;
                }
                if (location.getText().length() == 0) {
                    location.setError(getString(R.string.error_add_ticket_no_location));
                    return;
                }

                newTicket.setTicketName(name.getText().toString());
                newTicket.setTicketPhone(phone.getText().toString());
                newTicket.setTicketLocation(location.getText().toString());
                newTicket.setTicketNote(notes.getText().toString());
                newTicket.setTicketStates(getString(R.string.app_ticket_states_unread));

                String url =
                        HttpUtil.buildURL(
                                getString(R.string.app_network_server_ip),
                                getString(R.string.app_network_push_ticket_page)
                        );

                if (!imageUri.isEmpty()){
                    new UploadImageTask(AddTicketActivity.this, newTicket).execute(url);
                } else {

                    new PushTicketTask(newTicket, AddTicketActivity.this).execute(url);
                }


            }
        });

        // UI-ImageButtons
        imageUri = new ArrayList<>();
        imagePath = new ArrayList<>();
        images = new AppCompatImageButton[imageSum];
        images[0] = findViewById(R.id.photo_1);
        images[1] = findViewById(R.id.photo_2);
        images[2] = findViewById(R.id.photo_3);
        addPhoto = findViewById(R.id.photo_add);

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickPhotoSourceDialog();
            }
        });

        images[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeletePhotoDialog(0);
            }
        });

        images[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeletePhotoDialog(1);
            }
        });

        images[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeletePhotoDialog(2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_PHOTO_FROM_CAMERA:
                if (resultCode == RESULT_OK) {
                    refreshPhotoList();
                } else {
                    imageUri.remove(imageUri.size() - 1);
                    imagePath.remove(imagePath.size() - 1);
                }
                break;
            case PICK_PHOTO_FROM_ALBUM:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                    imageUri.add(data.getData());
                    refreshPhotoList();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                    }
                    getLocationListener.onClick(getLocation);
                }
                break;
            case 2:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    return;
                }
            default:
        }
    }

    private boolean requestPermissions() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(AddTicketActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(AddTicketActivity.this,
                Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(AddTicketActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(AddTicketActivity.this, permissions, 1);
            return false;
        }
        return true;
    }

    private void showPickPhotoSourceDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.dialog_pick_photo_source_content)
                .setCancelable(true)
                .setItems(R.array.array_pick_photo_source, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                File image = new File(getExternalCacheDir(),
                                        "image" + System.currentTimeMillis() + ".jpg");
                                try {
                                    if (image.exists()) {
                                        image.delete();
                                    }
                                    image.createNewFile();
                                } catch (Exception e) {
                                    return;
                                }
                                Uri photoUri;
                                if (Build.VERSION.SDK_INT >= 24) {
                                    photoUri  = FileProvider.getUriForFile(AddTicketActivity.this,
                                            "camera", image);
                                } else {
                                    photoUri = Uri.fromFile(image);
                                }
                                imageUri.add(photoUri);
                                imagePath.add(image.getPath());
                                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                                startActivityForResult(intent, PICK_PHOTO_FROM_CAMERA);
                                break;
                            case 1:
                                if (ContextCompat
                                        .checkSelfPermission(AddTicketActivity.this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                                        PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(AddTicketActivity.this,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                            , 2);
                                } else {
                                    openAlbum();
                                }
                                break;
                        }
                    }
                });
        dialog.show();
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FROM_ALBUM);
    }

    private void showDeletePhotoDialog(final int source) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(R.string.dialog_delete_photo)
                .setPositiveButton(R.string.dialog_delete_photo_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imageUri.remove(source);
                        imagePath.remove(source);
                        images[source].setVisibility(View.GONE);
                        refreshPhotoList();
                    }
                })
                .setNegativeButton(R.string.dialog_delete_photo_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
        dialog.show();
    }

    private void refreshPhotoList() {
        try {
            for (int i = 0; i < imageSum; i++) {
                if (i >= imageUri.size()) {
                    Glide.with(this)
                            .clear(images[i]);
                    images[i].setVisibility(View.GONE);
                    continue;
                }
                images[i].setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(imageUri.get(i))
                        .apply(RequestOptions.centerCropTransform())
                        .into(images[i]);
            }

            if (imageUri.size() == 3) {
                addPhoto.setVisibility(View.GONE);
            } else {
                addPhoto.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String path = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                path = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                path = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            path = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        }
        imagePath.add(path);
    }

    private void handleImageBeforeKitKat(Intent intent) {
        Uri uri = intent.getData();
        String path = getImagePath(uri, null);
        imagePath.add(path);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver()
                .query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}
