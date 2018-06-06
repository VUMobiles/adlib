package sdk.appadplay.adplaysdk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout adView;
    private String myPublisherId = "59c1fbdb85203";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adView = findViewById(R.id.rl);

//        new AdPlayAd(this, adView).loadVideoAd(myPublisherId, new AdPlayAd.VideoAdCallBack() {
//            @Override
//            public void isPlayingVideoAD(boolean b) {
//
//            }
//        });
//        Log.d("ResponseVideo",new AdPlayAd(this, adView).startVideo());
    }
}
