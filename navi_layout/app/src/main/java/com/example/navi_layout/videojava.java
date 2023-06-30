package com.example.navi_layout;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class videojava extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video);

        //artivitiy_video.xml에 있는 VidioView
        final VideoView videoview=(VideoView)findViewById(R.id.videoView);
        //Video View에서 보여줄 동영상주소.
        Uri url= Uri.parse("https://www.youtube.com/watch?v=UNKlX9J6m-A&list=PLC51MBz7PMyyyR2l4gGBMFMMUfYmBkZxm");
        videoview.setVideoURI(url);
        //비디오 컨트롤바.
        videoview.setMediaController(new MediaController(this));
        //videoview.start();

    }
}
