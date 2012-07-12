package com.example.celebrickv2;

import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.sound.SoundEngine;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;

public class CeleBrickv2 extends Activity {

	protected CCGLSurfaceView _glSurfaceView;
	protected static String TAG = "CeleBrickv2";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, 
        		WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
     
        _glSurfaceView = new CCGLSurfaceView(this);
        
        setContentView(_glSurfaceView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_cele_brickv2, menu);
        return true;
    }

    @Override
	protected void onStart() {
		
		super.onStart();
		
		CCDirector.sharedDirector().attachInView(_glSurfaceView);
   	 
        CCDirector.sharedDirector().setDisplayFPS(true);
     
        CCDirector.sharedDirector().setAnimationInterval(1.0f / 60.0f);
        
        CCScene scene = GameLayer.scene();
        CCDirector.sharedDirector().runWithScene(scene); 
	}
	
	
	@Override
	protected void onPause() {
		
		super.onPause();
		Log.e(TAG, "onPause");
	    SoundEngine.sharedEngine().pauseSound();

		CCDirector.sharedDirector().pause();
	}
	
	@Override
    protected void onResume() {
    	
    	super.onResume();
    	Log.e(TAG, "onResume"); 
    	Context context = CCDirector.sharedDirector().getActivity();
	    SoundEngine.sharedEngine().preloadEffect(context, R.raw.shot);
	    SoundEngine.sharedEngine().preloadEffect(context, R.raw.death);
	    SoundEngine.sharedEngine().playSound(context, R.raw.background_music_aac, true);
    	
    	CCDirector.sharedDirector().resume();
    }
    
    @Override
    protected void onStop() {
    	
    	super.onStop();
    	
    	CCDirector.sharedDirector().end();
    }
}
