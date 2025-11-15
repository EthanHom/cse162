package ethanhom.cse_162.activitydetectionlab3_2;

import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.location.DetectedActivity;
import ethanhom.cse_162.activityrecognitionlab3.services.ActivityDetectionService;
import ethanhom.cse_162.activityrecognitionlab3.utils.Constant;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private TextView mTextARLabel;
    private TextView mTextConfidence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextARLabel = findViewById(R.id.text_label);
        mTextConfidence = findViewById(R.id.text_confidence);
    }

    // register the RX and start up the ActivityDetectionService service
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart().start ActivityDetectionService");
        LocalBroadcastManager.getInstance(this).registerReceiver(mActivityBroadcastReceiver,
                new IntentFilter(Constant.BROADCAST_DETECTED_ACTIVITY));
        startService(new Intent(this, ActivityDetectionService.class));
    }

    // unregister the RX and stop up the ActivityDetectionService service
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause():stop ActivityDetectionService");
        if (mActivityBroadcastReceiver != null) {
            stopService(new Intent(this, ActivityDetectionService.class));
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mActivityBroadcastReceiver);
        }
    }

    BroadcastReceiver mActivityBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Log.d(TAG, "onReceive()");
            if (intent.getAction().equals(Constant.BROADCAST_DETECTED_ACTIVITY)) {
                int type = intent.getIntExtra("type", -1);
                int confidence = intent.getIntExtra("confidence", 0);
                handleUserActivity(type, confidence);
            }
        }
    };

    private void handleUserActivity(int type, int confidence) {
        String label = "Unknown";
        switch (type) {
            case DetectedActivity.IN_VEHICLE:
                label = "In_Vehicle";
                break;

            case DetectedActivity.ON_BICYCLE:
                label = "On_Bicycle";
                break;

            case DetectedActivity.ON_FOOT:
                label = "On_Foot";
                break;

            case DetectedActivity.RUNNING:
                label = "Running";
                break;

            case DetectedActivity.STILL:
                label = "Still";
                break;

            case DetectedActivity.TILTING:
                label = "Tilting";
                break;

            case DetectedActivity.UNKNOWN:
                break;
        }

        Log.d(TAG, "broadcast:onReceive(): Activity is " + label + " and confidence level is: " + confidence);
        mTextARLabel.setText(label);
        mTextConfidence.setText(confidence + "");
    }
}
