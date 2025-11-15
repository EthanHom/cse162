package ethanhom.cse_162.lab5b_media_recorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.video.MediaStoreOutputOptions;
import androidx.camera.video.Recorder;
import androidx.camera.video.Recording;
import androidx.camera.video.VideoCapture;
import androidx.camera.video.VideoRecordEvent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.camera.view.PreviewView;
import androidx.lifecycle.LifecycleOwner;
import com.google.common.util.concurrent.ListenableFuture;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class MainActivity extends AppCompatActivity {

    private PreviewView previewView;
    private Button captureButton;
    private EditText editText;
    private boolean isRecording = false;
    private VideoCapture<Recorder> videoCapture;
    private Recording currentRecording;

//    extra credit
    private VideoView videoView;
    private Button playbackButton;

    private static final String TAG = "Recorder";
    private static final int REQUEST_CODE_PERMISSIONS = 200;
    private final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.preview_view);
        captureButton = findViewById(R.id.button_capture);
        editText = findViewById(R.id.video_name);
//        extra credit
        videoView = findViewById(R.id.video_view);
        playbackButton = findViewById(R.id.playback);


        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            );
        }

        captureButton.setOnClickListener(this::onCaptureClick);

//        ec
        playbackButton.setOnClickListener(this::onPlaybackClick);

    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this, permission) != PackageManager.PERMISSION_GRANTED
            ) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
//               Permission not granted, exit the app
                finish();
            }
        }
    }

//    configure the camera
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreviewAndVideoCapture(cameraProvider);
            } catch (InterruptedException | ExecutionException e) {
                Log.e(TAG, "Error starting camera: " + e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreviewAndVideoCapture(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        Recorder recorder = new Recorder.Builder().build();
        videoCapture = VideoCapture.withOutput(recorder);

        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(
                    (LifecycleOwner) this, cameraSelector, preview, videoCapture
            );
        } catch (Exception e) {
            Log.e(TAG, "Use case binding failed", e);
        }
    }

    private void onCaptureClick(View view) {
        if (isRecording) {
//            stop recording
            currentRecording.stop();
            currentRecording = null;
            setCaptureButtonText("START");
            isRecording = false;
        } else {
            startRecording();
            setCaptureButtonText("STOP");
            isRecording = true;
        }
    }

//    extra credit
//    https://developer.android.com/training/data-storage/shared/media#java

    /*  old onPlaybackClick (just duplicate of onCaptureClick)
    private void onPlaybackClick(View view) {
        if (isRecording) {
//            stop recording
            currentRecording.stop();
            currentRecording = null;
            playbackButton.setText("PLAYBACK");
            isRecording = false;
        } else {
            startRecording();
            playbackButton.setText("STOP");
            isRecording = true;
        }
    }
     */

    private void onPlaybackClick(View view) {
//        method to check if there is a video, then play it
        Uri lastVideoUri = getLastRecordedVideoUri();
        if (lastVideoUri != null) {
            playVideo(lastVideoUri);
        } else {
            Log.e(TAG, "No video to play");
        }
    }

    /*  media store / get most recent video

    String[] projection = new String[] {
        media-database-columns-to-retrieve
    };
    String selection = sql-where-clause-with-placeholder-variables;
    String[] selectionArgs = new String[] {
            values-of-placeholder-variables
    };
    String sortOrder = sql-order-by-clause;

    Cursor cursor = getApplicationContext().getContentResolver().query(
        MediaStore.media-type.Media.EXTERNAL_CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        sortOrder
    );

    while (cursor.moveToNext()) {
        // Use an ID column from the projection to get
        // a URI representing the media item itself.
    }
     */

    /* Query a media collection

    // Need the READ_EXTERNAL_STORAGE permission if accessing video files that your
    // app didn't create.

    // Container for information about each video.
    class Video {
        private final Uri uri;
        private final String name;
        private final int duration;
        private final int size;

        public Video(Uri uri, String name, int duration, int size) {
            this.uri = uri;
            this.name = name;
            this.duration = duration;
            this.size = size;
        }
    }
    List<Video> videoList = new ArrayList<Video>();

    Uri collection;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
    } else {
        collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    }

    String[] projection = new String[] {
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DURATION,
        MediaStore.Video.Media.SIZE
    };
    String selection = MediaStore.Video.Media.DURATION +
            " >= ?";
    String[] selectionArgs = new String[] {
        String.valueOf(TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES));
    };
    String sortOrder = MediaStore.Video.Media.DISPLAY_NAME + " ASC";

    try (Cursor cursor = getApplicationContext().getContentResolver().query(
        collection,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )) {
        // Cache column indices.
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        int nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
        int durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
        int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);

        while (cursor.moveToNext()) {
            // Get values of columns for a given video.
            long id = cursor.getLong(idColumn);
            String name = cursor.getString(nameColumn);
            int duration = cursor.getInt(durationColumn);
            int size = cursor.getInt(sizeColumn);

            Uri contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);

            // Stores column values and the contentUri in a local object
            // that represents the media file.
            videoList.add(new Video(contentUri, name, duration, size));
        }
    }

     */

    private Uri getLastRecordedVideoUri() {
//        define data
        String[] projection = new String[] {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_ADDED
        };
//    filter to location and by newest video
        String selection = MediaStore.Video.Media.RELATIVE_PATH + " LIKE ?";
        String[] selectionArgs = new String[]{"Movies/Lab5b_Recordings%"};
        String sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC";

//        execute query
        Cursor cursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );

//        get first/newest video
        if (cursor != null && cursor.moveToFirst()) {
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            long id = cursor.getLong(idColumn);
            cursor.close();
//            convert ID to URI
            return ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
        }

//        unsure if needed (included in picture doc)
//        if (cursor != null) {
//            cursor.close();
//        }

        return null;
    }

//    play the video
//    developer.android.com/reference/android/widget/VideoView
    private void playVideo(Uri videoUri) {
        previewView.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);

//        load and play video
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();
        videoView.start();

        videoView.setOnCompletionListener(mp -> {
            previewView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
        });
    }



    private void startRecording() {
        String videoName = editText.getText().toString();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.US).format(new Date());
        String fileName = videoName + "_" + timeStamp + ".mp4";

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.put(
                    MediaStore.Video.Media.RELATIVE_PATH, "Movies/Lab5b_Recordings"
            );
        }

        MediaStoreOutputOptions mediaStoreOutputOptions =
                new MediaStoreOutputOptions.Builder(
                        getContentResolver(),
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                )
                        .setContentValues(contentValues)
                        .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Log.d("permission check", "permission check failed");
            return;
        } else {
            Log.d("permission check", "permission check passed");
        }

//        prepare for video recording
//        prepare for file storage
//        videoCapture.getOutput()
        currentRecording = videoCapture.getOutput()
                .prepareRecording(this, mediaStoreOutputOptions)
                .withAudioEnabled()
                .start(ContextCompat.getMainExecutor(this), videoRecordEvent -> {
                    if (videoRecordEvent instanceof VideoRecordEvent.Finalize) {
                        if (!((VideoRecordEvent.Finalize) videoRecordEvent).hasError()) {
                            Log.d(TAG, "Video saved successfully");
                        } else {
                            Log.e(TAG, "Video saving failed: " +
                                    ((VideoRecordEvent.Finalize) videoRecordEvent).getError());
                        }
                    }
                });
    }

//    when recording stops, release the camera and media recorder
    private void setCaptureButtonText(String title) {
        captureButton.setText(title);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentRecording != null) {
            currentRecording.stop();
            currentRecording = null;
        }
    }
}
