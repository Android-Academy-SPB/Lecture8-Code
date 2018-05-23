package ru.androidacademy.spb.imguruploader;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.androidacademy.spb.imguruploader.network.ImgurApiProvider;
import ru.androidacademy.spb.imguruploader.service.MyService;
import ru.androidacademy.spb.imguruploader.utils.UriUtils;
import ru.androidacademy.spb.imguruploader.work.UploadWork;

/**
 * @author Artur Vasilov
 */
public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 122;

    private static final String AUTHORIZATION_HEADER = "Authorization: Bearer YOUR_TOKEN_HERE";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == PICK_IMAGE_REQUEST && data.getData() != null) {
            String filePath = UriUtils.getPathFromUri(this, data.getData());
            if (!TextUtils.isEmpty(filePath)) {
                uploadImage(filePath);
            } else {
                showUploadStatusToast(R.string.select_image_error);
            }
        }
    }

    private void uploadImage(@NonNull String path) {
        ImgurApiProvider.getApiService().uploadImage(
                AUTHORIZATION_HEADER,
                "rsiqfvb",
                RequestBody.create(MediaType.parse("image/jpg"), new File(path))
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                showUploadStatusToast(R.string.upload_success);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showUploadStatusToast(R.string.upload_error);
            }
        });
    }

    private void showUploadStatusToast(@StringRes int textResId) {
        Toast.makeText(this, textResId, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissionOnStart();

        findViewById(R.id.upload_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }

    private void requestPermissionOnStart() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    private void startMyService() {
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        startService(intent);
    }

    private void scheduleWork() {
        Constraints workConstraints = new Constraints();
        workConstraints.setRequiredNetworkType(NetworkType.UNMETERED);
        WorkRequest workRequest = new OneTimeWorkRequest.Builder(UploadWork.class)
                .setConstraints(workConstraints)
                .build();

        WorkManager.getInstance().enqueue(workRequest);
    }
}
