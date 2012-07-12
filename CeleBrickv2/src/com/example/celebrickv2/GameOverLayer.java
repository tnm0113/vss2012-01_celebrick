package com.example.celebrickv2;

import org.cocos2d.actions.instant.CCCallFunc;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCLabel;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor3B;
import org.cocos2d.types.ccColor4B;

import android.content.Context;
import android.view.MotionEvent;

public class GameOverLayer extends CCColorLayer{
	
	protected CCLabel label;

	protected GameOverLayer(ccColor4B color) {
		super(color);

		this.setIsTouchEnabled(true);
		
		Context context = CCDirector.sharedDirector().getActivity();
		SoundEngine.sharedEngine().preloadEffect(context, R.raw.lose_sound);
		
		CGSize winSize = CCDirector.sharedDirector().displaySize();
		
		label = CCLabel.makeLabel("Game Over", "DroidSans", 32);
		label.setColor(ccColor3B.ccBLACK);
		label.setPosition(CGPoint.ccp(winSize.width / 2.0f, winSize.height / 2.0f));
		addChild(label);
		
		this.runAction(CCSequence.actions(CCDelayTime.action(3.0f), CCCallFunc.action(this, "gameOverDone")));
	}

	public static CCScene scene(String message) {
		CCScene scene = CCScene.node();
		GameOverLayer layer = new GameOverLayer(ccColor4B.ccc4(255, 255, 255, 255));
		
		layer.getLabel().setString(message);
		
		if(message.equalsIgnoreCase("You Lose"))
        {
        	Context context = CCDirector.sharedDirector().getActivity();
        	SoundEngine.sharedEngine().playEffect(context, R.raw.lose_sound);
        }
		 
        scene.addChild(layer);
		
		return scene;
	}
	
	private CCLabel getLabel() {
		
		return label;
	}
	
	private void gameOverDone() {
		
		CCDirector.sharedDirector().replaceScene(GameLayer.scene());
	}
	
	@Override
	public boolean ccTouchesEnded(MotionEvent event) {

		gameOverDone();
		 
        return true;
	}
	
}
