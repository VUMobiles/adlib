package sdk.appadplay.adplaysdk;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import sdk.appadplay.adplaysdk.test.AdPlayAd;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout adView;
    private String myPublisherId = "59c1fbdb85203";

    private VideoView videoView;
    MediaController mControler = null;
    private String video_url = "http://wap.shabox.mobi/CMS/Content/Graphics/FullVideo/D480x320/Roj_Hasore_Allah_Amar_BY_Noshin_Laila.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.videoView);
        videoView.setZOrderOnTop(true);
        playVideo();
        adView = findViewById(R.id.rl);

        new AdPlayAd(this, adView).loadVideoAd(myPublisherId, new AdPlayAd.VideoAdCallBack() {
            @Override
            public void isPlayingVideoAD(boolean isPlaying) {

            }

            @Override
            public void finishVideoAd(boolean finishAd) {

            }

            @Override
            public void skipVideoAd(boolean skipAd) {

            }
        });
    }

    private void playVideo() {
        mControler = new MediaController(this);
        Uri uri = Uri.parse(video_url);
        videoView.setVideoURI(uri);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mControler.setAnchorView(videoView);
                videoView.setMediaController(mControler);
                videoView.start();
                mediaPlayer.setLooping(true);
                mediaPlayer.setVolume(50, 50);
            }
        });

    }
}
