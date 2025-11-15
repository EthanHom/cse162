package ethanhom.cse_162.fragment;

//import androidx.appcompat.app.AppCompatActivity;
//
//import android.app.FragmentManager;
//import android.os.Bundle;
import android.os.Parcelable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;   // AndroidX FragmentManager
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements HeadlineFragment.OnHeadLineSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        values vs values-land
        FragmentManager manager = getSupportFragmentManager();
        if (getResources().getBoolean(R.bool.landscapeMode)) {
            manager.beginTransaction()
                    .show(manager.findFragmentById(R.id.headlines_fragment))
                    .show(manager.findFragmentById(R.id.news_fragment))
                    .commit();
        } else {
            manager.beginTransaction()
                    .show(manager.findFragmentById(R.id.headlines_fragment))
                    .hide(manager.findFragmentById(R.id.news_fragment))
                    .commit();
        }
    }

    public void onArticleSelected(int position) {
        FragmentManager manager = getSupportFragmentManager();
        NewsFragment news = (NewsFragment) manager.findFragmentById(R.id.news_fragment);
        if (news != null) {
            news.updateArticleView(position);
        }

        if (!getResources().getBoolean(R.bool.landscapeMode)) {
            manager.beginTransaction()
                    .hide(manager.findFragmentById(R.id.headlines_fragment))
                    .show(manager.findFragmentById(R.id.news_fragment))
                    .addToBackStack(null)
                    .commit();
        }

    }
}
