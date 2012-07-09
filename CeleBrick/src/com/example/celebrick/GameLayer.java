package com.example.celebrick;

import java.util.ArrayList;
import java.util.Random;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor4B;

import android.content.Context;
import android.view.MotionEvent;

public class GameLayer extends CCColorLayer{
	
	protected ArrayList<CCSprite> _targets;
	protected ArrayList<CCSprite> _bullets;
	protected int _bulletsDestroyed;
	protected int _targetsPassed;
	
	protected GameLayer(ccColor4B color) {
		super(color);

		this.setIsTouchEnabled(true);
		
		_targets = new ArrayList<CCSprite>();
		_bullets = new ArrayList<CCSprite>();
		_bulletsDestroyed = 0;
		_targetsPassed = 3;
		
		CGSize winSize = CCDirector.sharedDirector().displaySize();
		
		CCSprite player = CCSprite.sprite("player.png");
		 
	    player.setPosition(CGPoint.ccp(player.getContentSize().width / 2.0f, winSize.height / 2.0f));
	 
	    addChild(player);
	    
	    Context context = CCDirector.sharedDirector().getActivity();
	    SoundEngine.sharedEngine().preloadEffect(context, R.raw.shot);
	    SoundEngine.sharedEngine().preloadEffect(context, R.raw.death);
	    SoundEngine.sharedEngine().playSound(context, R.raw.background_music_aac, true);
	    
	    this.schedule("gameLogic", 1.0f);
	    this.schedule("update");
	}

	public static CCScene scene() {
		
		CCScene scene = CCScene.node();
		
	    CCColorLayer layer = new GameLayer(ccColor4B.ccc4(255, 255, 255, 255));
	 
	    scene.addChild(layer);
	 
	    return scene;
	}
	
	public void gameLogic(float dt)
	{
		addTarget();
	}
	
	public void spriteMoveFinished(Object sender)
	{
	    CCSprite sprite = (CCSprite)sender;
	    if (sprite.getTag() == 1)
	    {
	        _targets.remove(sprite);
	        if(--_targetsPassed == 0)
	        	CCDirector.sharedDirector().replaceScene(GameOverLayer.scene("Game Over"));
	    }
	    else if (sprite.getTag() == 2)
	    {
	    	_bullets.remove(sprite);
	    }
	}
	
	public boolean ccTouchesEnded(MotionEvent event) {
		Context context = CCDirector.sharedDirector().getActivity();
		SoundEngine.sharedEngine().playEffect(context, R.raw.shot);
		
	    // Choose one of the touches to work with
	    CGPoint location = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));
	    
	    CGSize winSize = CCDirector.sharedDirector().displaySize();
	 
	    addBullet(location, CGPoint.ccp(20, winSize.height / 2.0f));
	    
	    return true;
	}
	
	protected void addTarget() {
		Random rand = new Random();
	    CCSprite target = CCSprite.sprite("target.png");
	 
	    // Determine where to spawn the target along the Y axis
	    CGSize winSize = CCDirector.sharedDirector().displaySize();
	    int minY = (int)(target.getContentSize().height / 2.0f);
	    int maxY = (int)(winSize.height - target.getContentSize().height / 2.0f);
	    int rangeY = maxY - minY;
	    int actualY = rand.nextInt(rangeY) + minY;
	 
	    // Create the target slightly off-screen along the right edge,
	    // and along a random position along the Y axis as calculated above
	    target.setPosition(CGPoint.ccp(winSize.width + (target.getContentSize().width / 2.0f), actualY));
	    addChild(target);
	    target.setTag(1);
	    _targets.add(target);
	 
	    // Determine speed of the target
	    int minDuration = 1;
	    int maxDuration = 5;
	    int rangeDuration = maxDuration - minDuration;
	    int actualDuration = rand.nextInt(rangeDuration) + minDuration;
	 
	    // Create the actions
	    CCMoveTo actionMove = CCMoveTo.action(actualDuration, CGPoint.ccp(-target.getContentSize().width / 2.0f, actualY));
	    CCCallFuncN actionMoveDone = CCCallFuncN.action(this, "spriteMoveFinished");
	    CCSequence actions = CCSequence.actions(actionMove, actionMoveDone);
	 
	    target.runAction(actions);
	}
	
	protected void addTargetDied(CGPoint pos)
	{
		CCSprite target = CCSprite.sprite("target.png");
		target.setPosition(pos);
		addChild(target);
		
		int duration = 1;
		
	    double angleDegrees = -90;
	    double cocosAngle = -1 * angleDegrees;
	    target.setRotation((float)cocosAngle);
		
		target.runAction(CCSequence.actions(
	            CCMoveTo.action(duration, CGPoint.ccp(target.getPosition().x, -target.getContentSize().height/2.0f)),
	            CCCallFuncN.action(this, "spriteMoveFinished")));
	}
	
	protected void addBullet(CGPoint location, CGPoint pos) {
		// Set up initial location of bullet
	    CGSize winSize = CCDirector.sharedDirector().displaySize();
	    CCSprite bullet = CCSprite.sprite("brick.png");
	 
	    bullet.setPosition(pos);
	    
	    // Determine offset of location to bullet
	    int offX = (int)(location.x - bullet.getPosition().x);
	    int offY = (int)(location.y - bullet.getPosition().y);
	    
	    // Bail out if we are shooting down or backwards
	    if (offX <= 0)
	        return;
	 
	    // Ok to add now - we've double checked position
	    addChild(bullet);
	    bullet.setTag(2);
	    _bullets.add(bullet);
	    
	    // Determine where we wish to shoot the bullet to
	    int realX = (int)(winSize.width + (bullet.getContentSize().width / 2.0f));
	    float ratio = (float)offY / (float)offX;
	    int realY = (int)((realX * ratio) + bullet.getPosition().y);
	    CGPoint realDest = CGPoint.ccp(realX, realY);
	 
	    // Determine the length of how far we're shooting
	    int offRealX = (int)(realX - bullet.getPosition().x);
	    int offRealY = (int)(realY - bullet.getPosition().y);
	    float length = (float)Math.sqrt((offRealX * offRealX) + (offRealY * offRealY));
	    float velocity = 480.0f / 1.0f; // 480 pixels / 1 sec
	    float realMoveDuration = length / velocity;
	    
	    double angleRadians = Math.atan((double)offRealY / (double)offRealX);
	    double angleDegrees = Math.toDegrees(angleRadians);
	    double cocosAngle = -1 * angleDegrees;
	    bullet.setRotation((float)cocosAngle);
	 
	    // Move bullet to actual endpoint
	    bullet.runAction(CCSequence.actions(
	            CCMoveTo.action(realMoveDuration, realDest),
	            CCCallFuncN.action(this, "spriteMoveFinished")));
	}
	
	public void update(float dt)
	{
	    ArrayList<CCSprite> bulletsToDelete = new ArrayList<CCSprite>();
	    Context context = CCDirector.sharedDirector().getActivity();
	    CGSize winSize = CCDirector.sharedDirector().displaySize();
	
	    for (CCSprite bullet : _bullets)
	    {
	        CGRect bulletRect = CGRect.make(bullet.getPosition().x - (bullet.getContentSize().width / 4.0f),
	                                            bullet.getPosition().y - (bullet.getContentSize().height / 4.0f),
	                                            (bullet.getContentSize().width/2.0f),
	                                            (bullet.getContentSize().height/2.0f));
	        
	        if((bullet.getPosition().y >= winSize.height) || (bullet.getPosition().y <= 0)){
	        	//
	        }
	        
	        ArrayList<CCSprite> targetsToDelete = new ArrayList<CCSprite>();
	
	        for (CCSprite target : _targets)
	        {
	            CGRect targetRect = CGRect.make(target.getPosition().x - (target.getContentSize().width/4.0f),
	                                            target.getPosition().y - (target.getContentSize().height/4.0f),
	                                            (target.getContentSize().width/2.0f),
	                                            (target.getContentSize().height/2.0f));
	
	            if (CGRect.intersects(bulletRect, targetRect))
	            {
	            	targetsToDelete.add(target);
	            	SoundEngine.sharedEngine().playEffect(context, R.raw.death);
	            	//bang(bullet, target);
	            	addTargetDied(target.getPosition());
	            }
	                
	        }
	
	        for (CCSprite target : targetsToDelete)
	        {
	            _targets.remove(target);
	            removeChild(target, true);
	        }
	
	        if (targetsToDelete.size() > 0)
	            bulletsToDelete.add(bullet);
	    }
	
	    for (CCSprite bullet : bulletsToDelete)
	    {
	        _bullets.remove(bullet);
	        removeChild(bullet, true);
	        if (++_bulletsDestroyed > 30)
	        {
	            _bulletsDestroyed = 0;
	            CCDirector.sharedDirector().replaceScene(GameOverLayer.scene("Victory!"));
	        }
	    }
	}
}
