package sdk.appadplay.adplaysdk;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import sdk.appadplay.adplaysdk.test.AdPlayAd;
import sdk.appadplay.adplaysdk.test.network.HttpHandler;

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

//        videoView = findViewById(R.id.videoView);
//        videoView.setZOrderOnTop(true);
//        playVideo();
        adView = findViewById(R.id.rl);

        HttpHandler a = new HttpHandler();
        //new BackgroundTask().execute();
        //String d = a.makeServiceCall("http://android.vumobile.biz/bdnewapi/DataOther/Slidder");
//        String d = a.makeServiceCall("https://adsapi.adplay-mobile.com/adplaysdk?pid=59c1fbdb85203&useragent=Mozilla/5.0%20(Linux;%20Android%205.1.1;%20SM-J200H%20Build/LMY48B;%20wv)%20AppleWebKit/537.36%20(KHTML,%20like%20Gecko)%20Version/4.0%20Chrome/68.0.3440.91%20Mobile%20Safari/537.36&type=banner&dimension=&gid=18ddf26ed50dc743&packagename=bdtube.vumobile.com.bdtube&request=banner");
//        String d = getJSON("http://android.vumobile.biz/bdnewapi/DataOther/Slidder",3000);
//        Log.d("Banner", d);

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

            @Override
            public void tapInstallButton(boolean tapInstall) {

            }
        });

//        new AdPlayAd(this,adView).Banner("59c1fbdb85203","sdk.appadplay.adplaysdk","banner","300x50");
    }

//    private void playVideo() {
//        mControler = new MediaController(this);
//        Uri uri = Uri.parse(video_url);
//        videoView.setVideoURI(uri);
//
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                mControler.setAnchorView(videoView);
//                videoView.setMediaController(mControler);
//                videoView.start();
//                mediaPlayer.setLooping(true);
//                mediaPlayer.setVolume(50, 50);
//            }
//        });
//
//    }

    public class BackgroundTask extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... strings) {
            return getJSON("https://adsapi.adplay-mobile.com/adplaysdk?pid=59c1fbdb85203&useragent=Mozilla/5.0%20(Linux;%20Android%205.1.1;%20SM-J200H%20Build/LMY48B;%20wv)%20AppleWebKit/537.36%20(KHTML,%20like%20Gecko)%20Version/4.0%20Chrome/68.0.3440.91%20Mobile%20Safari/537.36&type=banner&dimension=&gid=18ddf26ed50dc743&packagename=bdtube.vumobile.com.bdtube&request=banner",3000);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("Banner", s);
        }
    }

    public String getJSON(String url, int timeout) {
        HttpURLConnection c = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }
}
