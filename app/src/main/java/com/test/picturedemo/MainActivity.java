package com.test.picturedemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import java.util.ArrayList;
import java.util.List;
import io.valuesfeng.picker.ImageSelectActivity;
import io.valuesfeng.picker.Picker;
import io.valuesfeng.picker.engine.PicassoEngine;
import io.valuesfeng.picker.utils.BundleUtils;
import io.valuesfeng.picker.utils.MediaStoreCompat;
import io.valuesfeng.picker.utils.PicturePickerUtils;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements TwoOnClickInterface,EasyPermissions.PermissionCallbacks{
    private String TAG = "MainActivity";
    public static final int REQUEST_CODE_CHOOSE = 1;
    private List<Uri> mSelected;
    private GridView mGridView;
    private ArrayList<PictureBean> mList;
    private PictureGridAdapter mAdapter;
    public static final int REQUEST_CODE_CAPTURE = 3;
    private MediaStoreCompat mMediaStoreCompat;
    public static final String STATE_CAPTURE_PHOTO_URI = BundleUtils.buildKey(ImageSelectActivity.class, "STATE_CAPTURE_PHOTO_URI");
    private String mCapturePhotoUriHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 6.0及以上需要相机与读写文件需要手动权限
         */
        String[] perms = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            Log.i(TAG, "已获取权限");
        } else {
            EasyPermissions.requestPermissions(this, "必要的权限", 0, perms);
        }


        mCapturePhotoUriHolder = savedInstanceState != null ? savedInstanceState.getString(STATE_CAPTURE_PHOTO_URI) : "";
        mMediaStoreCompat = new MediaStoreCompat(this, new Handler(Looper.getMainLooper()));

         mList = new ArrayList<PictureBean>();
        mGridView = (GridView) findViewById(R.id.gridview_photo);
        mAdapter = new PictureGridAdapter(this,mList);
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                if (position == mList.size()){
                    SelectPictureDialog mDialog = new SelectPictureDialog(MainActivity.this);
                    mDialog.setTwoOnClickInterface(MainActivity.this);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mList.clear();
            mAdapter.notifyDataSetChanged();
            mSelected = PicturePickerUtils.obtainResult(data);
            for (Uri u : mSelected) {
                PictureBean mBean = new PictureBean();
                Log.i("picture", u.getPath());
                String uri = u.getPath();
                getRealPathFromUri(this, u);
                mBean.setImgPath(getRealPathFromUri(this, u));
                mList.add(mBean);
//                showImg.setImageBitmap(getImageThumbnail(getRealPathFromUri(this,u),500,500));
            }

            mAdapter = new PictureGridAdapter(this, mList);
            mGridView.setAdapter(mAdapter);

        } else  if (requestCode == REQUEST_CODE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Uri captured = mMediaStoreCompat.getCapturedPhotoUri(data, mCapturePhotoUriHolder);
            if (captured != null) {
                    PictureBean mBean = new PictureBean();
                    Log.i("picture", captured.getPath());
                    String uri = captured.getPath();
                    getRealPathFromUri(this, captured);
                    mBean.setImgPath(getRealPathFromUri(this, captured));
                    mList.add(mBean);

                mAdapter = new PictureGridAdapter(this, mList);
                mGridView.setAdapter(mAdapter);

            }
        }
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void photoGraph() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        showCameraAction();
    }

    private void showCameraAction() {
        mCapturePhotoUriHolder = mMediaStoreCompat.invokeCameraCapture(this, REQUEST_CODE_CAPTURE);
    }

    @Override
    public void album() {

        Picker.from(MainActivity.this)
                .count(4)  //设置可选张数
                .enableCamera(true)
                .setEngine(new PicassoEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaStoreCompat.destroy();
    }
}
