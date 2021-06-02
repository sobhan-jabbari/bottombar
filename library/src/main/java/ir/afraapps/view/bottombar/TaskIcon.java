package ir.afraapps.view.bottombar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class TaskIcon extends Thread {

    private String iconUri;
    private String targetPath;
    private int iconSize;

    private Handler handler;

    private OnIconLoadListener listener;

    public static void loadIcon(@NonNull String iconUri, @NonNull String targetPath, int size, OnIconLoadListener listener) {
        synchronized (TaskIcon.class) {
            TaskIcon taskImage = new TaskIcon(iconUri, targetPath, size, listener);
            taskImage.start();
        }
    }

    private TaskIcon(String iconUri, @NonNull String targetPath, int size, OnIconLoadListener listener) {
        this.iconUri = iconUri;
        this.targetPath = targetPath;
        this.iconSize = size;
        this.listener = listener;
        handler = new Handler(Looper.getMainLooper());
    }

    public void mute() {
        listener = null;
    }


    @Override
    public void run() {
        super.run();
        try {
            loadImage();
        } catch (Exception e) {
            postResult(null, true);
        }

    }


    private void postResult(final Bitmap bitmap, boolean isFromCatch) {
        handler.post(() -> {
            if (listener != null) {
                listener.onLoadIcon(bitmap, isFromCatch);
            }
        });
    }


    private void loadImage() {

        if (TextUtils.isEmpty(iconUri)) {
            postResult(null, true);
            return;
        }

        String imageFileName = MiscUtils.toMD5(iconUri);
        File targetFile = new File(targetPath, imageFileName);

        Bitmap bitmap = BitmapFactory.decodeFile(targetFile.getAbsolutePath());


        if (bitmap != null) {
            postResult(bitmap, true);
            return;
        }

        fetchImage(targetFile);
    }


    private void fetchImage(File targetFile) {
        try {
            URL url = new URL(iconUri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(120000);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            Bitmap icon = BitmapFactory.decodeStream(inputStream);

            closeInputStream(inputStream);
            connection.disconnect();

            if (icon == null) {
                postResult(null, true);
                return;

            } else {
                icon = Bitmap.createScaledBitmap(icon, iconSize, iconSize, true);
                FileOutputStream osImageFile = new FileOutputStream(targetFile);
                icon.compress(Bitmap.CompressFormat.PNG, 90, osImageFile);
                closeOutputStream(osImageFile);
            }

            postResult(icon, false);

        } catch (Exception ex) {
            if (targetFile != null && targetFile.exists()) {
                targetFile.delete();
            }
            postResult(null, true);
        }
    }


    private static void closeOutputStream(OutputStream outputStream) {
        try {
            outputStream.flush();
        } catch (Exception e) {
            //
        }

        try {
            outputStream.close();
        } catch (Exception e) {
            //
        }
    }

    private static void closeInputStream(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (Exception e) {
            //
        }
    }


    interface OnIconLoadListener {
        void onLoadIcon(Bitmap icon, boolean isFromCatch);
    }

}
