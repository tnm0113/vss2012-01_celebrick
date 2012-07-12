package com.example.celebrickv2;

import java.util.ArrayList;
import java.util.Random;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCDelayTime;
import org.cocos2d.actions.interval.CCJumpTo;
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
	protected ArrayList<Bullet> _bullets;
	protected ArrayList<CCSprite> _walls;
	protected int _bulletsDestroyed;
	protected int _targetsPassed;
	protected static String TAG = "GameLayer";
	
	protected GameLayer(ccColor4B color) {
		super(color);

		this.setIsTouchEnabled(true);
		
		_targets = new ArrayList<CCSprite>();
		_bullets = new ArrayList<Bullet>();
		_walls = new ArrayList<CCSprite>();
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
	    
	    addWall();
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
	
	protected void addWall() {
		CCSprite wall = CCSprite.sprite("hardblock.png");
		CGSize winSize = CCDirector.sharedDirector().displaySize();
		float theX = wall.getContentSize().width / 2.0f;
		float theY = wall.getContentSize().height / 2.0f;
		
		while (theX < winSize.width) {
			CGPoint pos = CGPoint.ccp(theX, theY);
			newWall(pos);
			theX = theX + wall.getContentSize().width;
		}
		
		theX = wall.getContentSize().width / 2.0f;
		theY = winSize.height - wall.getContentSize().height / 2.0f;
		
		while (theX < winSize.width) {
			CGPoint pos = CGPoint.ccp(theX, theY);
			newWall(pos);
			theX = theX + wall.getContentSize().width;
		}
	}
	
	protected void newWall(CGPoint pos) {
		CCSprite wall = CCSprite.sprite("hardblock.png");
		wall.setPosition(pos);
		addChild(wall);
		_walls.add(wall);
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
	 
	    // Determine speed, height, jumps of the target
	    int minDuration = 1;
	    int maxDuration = 5;
	    int rangeDuration = maxDuration - minDuration;
	    int actualDuration = rand.nextInt(rangeDuration) + minDuration;
	    
	    int minHeight = 10;
	    int maxHeight = 70;
	    int rangeHeight = maxHeight - minHeight;
	    int actualHeight = rand.nextInt(rangeHeight) + minHeight;
	    
	    int minJump = 10;
	    int maxJump = 15;
	    int rangeJump = maxJump - minJump;
	    int actualJump = rand.nextInt(rangeJump) + minJump;
	 
	    // Create the actions
	    CCJumpTo actionJump = CCJumpTo.action(actualDuration, 
	    		CGPoint.ccp(-target.getContentSize().width / 2.0f, actualY), actualHeight, actualJump);

	    CCCallFuncN actionMoveDone = CCCallFuncN.action(this, "spriteMoveFinished");
	    CCSequence actions = CCSequence.actions(actionJump, actionMoveDone);
	 
	    target.runAction(actions);
	}
	
	protected void addBullet(CGPoint location, CGPoint pos) {
		Bullet bullet = new Bullet(location, pos);
		
		if (bullet.isValid()){
			addChild(bullet.brick);
		    bullet.brick.setTag(2);
		    _bullets.add(bullet);
		    
		    bullet.brick.setRotation((float)bullet.getCocosAngle());
		    
		    // Move bullet to actual endpoint
		    bullet.brick.runAction(CCSequence.actions(
		            CCMoveTo.action(bullet.getRealMoveDuration(), bullet.getRealDest()),
		            CCCallFuncN.action(this, "spriteMoveFinished")));
		}
		// Set up initial location of bullet
		/*CGSize winSize = CCDirector.sharedDirector().displaySize();
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
	    
	    Log.i(TAG, CGPoint.ccp(offX, offY).toString());
		Log.i(TAG, location.toString() + "***");
		Log.i(TAG, "***" + ratio);
	    
	    // Move bullet to actual endpoint
	    bullet.runAction(CCSequence.actions(
	            CCMoveTo.action(realMoveDuration, realDest),
	            CCCallFuncN.action(this, "spriteMoveFinished")));*/
	}

	protected void addTargetDied(CGPoint pos)
	{
		CCSprite target = CCSprite.sprite("target_died.png");
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
	
	protected void bang(CCSprite objA, CCSprite objB) {
		CGPoint pos = CGPoint.ccp((objA.getPosition().x + objB.getPosition().x) / 2.0f, 
									(objA.getPosition().y + objB.getPosition().y) / 2.0f);
		CCSprite bang = CCSprite.sprite("bang.png");
		bang.setPosition(pos);
		addChild(bang);
		
		bang.runAction(CCSequence.actions(CCDelayTime.action(0.2f), CCCallFuncN.action(this, "bangAction")));
	}
	
	public void bangAction(Object sender){
		CCSprite sprite = (CCSprite)sender;
		//CCDelayTime.action(1);
		removeChild(sprite, true);
	}
	
	protected void reverseBullet(Bullet bullet) {
		CGSize winSize = CCDirector.sharedDirector().displaySize();
		CGPoint str_pos = CGPoint.ccp(20, winSize.height / 2.0f);
		
//		float ratio = (bullet.getPosition().y - str_pos.y) / (bullet.getPosition().x - str_pos.x);
//		int realX = (int)(winSize.width + (bullet.getContentSize().width / 2.0f));
//		int realY = (int)(((realX - bullet.getPosition().x) * ratio * -1) + bullet.getPosition().y);
//		CGPoint realDest = CGPoint.ccp(realX, realY);
//		CGPoint fakeDest = CGPoint.ccp(realX, winSize.height / 2.0f);
//		Log.i(TAG, realDest.toString());
//		Log.i(TAG, fakeDest.toString());
		
//		int realMoveDuration = 1;
		bullet.reverse();
//		bullet.brick.stopAllActions();
		bullet.brick.setRotation(bullet.getCocosAngle());
		bullet.brick.runAction(CCSequence.actions(
	            CCMoveTo.action(bullet.getRealMoveDuration(), bullet.getRealDest()),
	            CCCallFuncN.action(this, "spriteMoveFinished")));
	}
	
//	public CCSprite callBulletBack(CCSprite bullet) {
//		bullet.setRotation(-1 * bullet.getRotation());
//		_bullets.add(bullet);
//		return bullet;
//	}
	
	public void update(float dt)
	{
	    ArrayList<Bullet> bulletsToDelete = new ArrayList<Bullet>();
	    Context context = CCDirector.sharedDirector().getActivity();
	    CGSize winSize = CCDirector.sharedDirector().displaySize();
	
	    for (Bullet bullet : _bullets)
	    {
	        CGRect bulletRect = CGRect.make(bullet.brick.getPosition().x - (bullet.brick.getContentSize().width / 4.0f),
	        		bullet.brick.getPosition().y - (bullet.brick.getContentSize().height / 4.0f),
	                                            (bullet.brick.getContentSize().width/2.0f),
	                                            (bullet.brick.getContentSize().height/2.0f));
	        
	        ArrayList<CCSprite> bulletsHited = new ArrayList<CCSprite>();
	        ArrayList<CCSprite> targetsToDelete = new ArrayList<CCSprite>();
	        
//	        for (CCSprite wall : _walls){
//	        	CGRect wallRect = CGRect.make(wall.getPosition().x - (wall.getContentSize().width / 2.0f),
//	        									wall.getPosition().y - (wall.getContentSize().height / 2.0f),
//	        									wall.getContentSize().width,
//	        									wall.getContentSize().height);
//	        	
//	        	if (CGRect.intersects(bulletRect, wallRect) && !bullet.getHit()){
////	        		_bullets.remove(bullet);
////	        		bulletsHited.add(bullet);
////	        		Log.i(TAG, "hit");
//	        		bullet.setHit(true);
//	        		reverseBullet(bullet);
////	        		CCDelayTime.action(0.5f);
////	        		bullet.setHit(false);
////	        		_bullets.add(bullet);
//	        	}
//	        }
	        
	        if ((((bullet.brick.getPosition().y + bullet.brick.getContentSize().height/3) >= (winSize.height - 32)) || 
	        		((bullet.brick.getPosition().y - bullet.brick.getContentSize().height/3) <= 32)) && !bullet.getHit()){
	        	bullet.setHit(true);
        		reverseBullet(bullet);
	        }
	        
	        if (bullet.brick.getPosition().y > (winSize.height / 4.0f) && 
	        		bullet.brick.getPosition().y < (3 * winSize.height / 4.0f)){
	        	bullet.setHit(false);
	        }
	        
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
	        
	        if (bullet.brick.getPosition().x >= (winSize.width - 1)){
	        	removeChild(bullet.brick, true);
	        	_bullets.remove(bullet);
	        }
	    }
	
	    for (Bullet bullet : bulletsToDelete)
	    {
	        _bullets.remove(bullet);
	        removeChild(bullet.brick, true);
	        if (++_bulletsDestroyed > 100)
	        {
	            _bulletsDestroyed = 0;
	            CCDirector.sharedDirector().replaceScene(GameOverLayer.scene("Victory!"));
	        }
	    }
	}
}
