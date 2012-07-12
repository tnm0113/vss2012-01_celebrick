package com.example.celebrickv2.backup;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

import android.graphics.Bitmap;

public class Bullet extends CCSprite{
	private CGSize winSize = CCDirector.sharedDirector().displaySize();
	private CGPoint location, realDest;
	private int offX, offY, realX, realY, offRealX, offRealY;
	private float ratio, length, velocity, realMoveDuration;
	private double angleRadians, angleDegrees, cocosAngle;
	
	public Bullet(CGPoint location) {
		//super();
		this.location = location;
		//init_data();
	}
	
	protected void init_data(CGPoint location){
		this.location = location;
		
		offX = (int)(location.x - getPosition().x);
		offY = (int)(location.y - getPosition().y);
		ratio = (float)offY / (float)offX;
		
		realX = (int)(winSize.width + (getContentSize().width / 2.0f));
		realY = (int)((realX * ratio) + getPosition().y);
		realDest = CGPoint.ccp(realX, realY);
		
		offRealX = (int)(realX - getPosition().x);
		offRealY = (int)(realY - getPosition().y);
		length = (float)Math.sqrt((offRealX * offRealX) + (offRealY * offRealY));
		velocity = 480.0f / 1.0f;
		realMoveDuration = length / velocity;
		
		angleRadians = Math.atan((double)offRealY / (double)offRealX);
		angleDegrees = Math.toDegrees(angleRadians);
		cocosAngle = -1 * angleDegrees;
	}
	
	protected void reverse() {
		cocosAngle = -1 * cocosAngle;
		ratio = -1 * ratio;
		
		realY = (int)((realX * ratio) + getPosition().y);
	}
	
	protected int getOffX() {
		return offX;
	}
	
	protected double getCocosAngle() {
		return cocosAngle;
	}
	
	protected float getRealMoveDuration() {
		return realMoveDuration;
	}
	
	protected CGPoint getRealDest() {
		return realDest;
	}
}
