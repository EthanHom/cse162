package ethanhom.cse_162.lab6a_face_detection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    ImageView iw;
    Canvas canvas;
    Bitmap mutableBitmap;


//    Prepare for the image detection
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        configure the detection options
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();

//        read the image
        Bitmap bm=getBitmapFromAssests("faces.png");

//        display the image
        iw = (ImageView) findViewById(R.id.image_view);
        iw.setImageBitmap(bm);

//        create a copy of the face image to draw upon
        mutableBitmap = bm.copy(Bitmap.Config.ARGB_8888, true);
        canvas=new Canvas(mutableBitmap);

        InputImage image = InputImage.fromBitmap(bm, 0);    // convert the image to correct format using InputImage object
        Log.d("TAG", "before recognition");
        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);      // get an instance of FaceDetector

//        process the image


        Task<List<Face>> result =
                detector.process(image)
                        // If the face detection operation succeeds, a list of Face objects are passed to the success listener.
                        // Each Face object represents a face that was detected in the image.
                        // For each face, you can get its bounding coordinates in the input image, as well as any other information you configured the face detector to find.
                        // In this lab, we want to plot a rectangle for each face.
                        .addOnSuccessListener(
                            new OnSuccessListener<List<Face>>() {
                                @Override
                                public void onSuccess (List<Face> faces) {
                                    Log.d("TAG", "on success recognition succeed");
                                    // use the canvas to draw the detection boxes
                                    for (Face face : faces) {
                                        Rect bounds = face.getBoundingBox();
                                        Paint paint = new Paint();
                                        paint.setAntiAlias(true);
                                        paint.setColor(Color.RED);
                                        paint.setStyle(Paint.Style.STROKE);
                                        paint.setStrokeWidth(8);

                                        canvas.drawRect(bounds, paint);

                                        iw = (ImageView) findViewById(R.id.image_view);
                                        iw.setImageBitmap(mutableBitmap);
                                        Log.d("TAG", "recognition succeed");
                                    }
                                }
                            })
                        .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure (@NonNull Exception e){
                                    //Task failed with exception
                                    Log.d("TAG", "recognition failed");
                                    Toast.makeText(getApplicationContext(), (String) e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

    }

    private Bitmap getBitmapFromAssests(String fileName){
        AssetManager am = getAssets();
        InputStream is = null;
        try{
            is = am.open(fileName);
        }catch(IOException e){
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }
}
