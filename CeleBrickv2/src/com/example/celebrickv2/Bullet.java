package com.example.celebrickv2;

import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGSize;

public class Bullet{
	private Boolean hitTheWall;
	CCSprite brick;
	protected static String TAG = "Bullet";
	private CGPoint location, str_pos, realDest;
	private CGSize winSize = CCDirector.sharedDirector().displaySize();
	private float offX, offY, realX, realY, offRealX, offRealY;
	private float ratio, length, velocity, realMoveDuration;
	private double angleRadians, angleDegrees, cocosAngle;
	
	public Bullet(CGPoint location, CGPoint pos) {
		hitTheWall = false;
		brick = CCSprite.sprite("brick.png");
		init_data(location, pos);
	}
	
	protected void init_data(CGPoint location, CGPoint pos){
		brick.setPosition(pos);
		str_pos = pos;
		
		offX = location.x - brick.getPosition().x;
		offY = location.y - brick.getPosition().y;
		ratio = offY / offX;
		
		realX = winSize.width + (brick.getContentSize().width / 2.0f);
		realY = (realX * ratio) + brick.getPosition().y;
		realDest = CGPoint.ccp(realX, realY);
		
		offRealX = realX - brick.getPosition().x;
		offRealY = realY - brick.getPosition().y;
		length = (float)Math.sqrt((offRealX * offRealX) + (offRealY * offRealY));
		velocity = 480.0f / 1.0f;
		realMoveDuration = length / velocity;
		
		angleRadians = Math.atan((double)offRealY / (double)offRealX);
		angleDegrees = Math.toDegrees(angleRadians);
		cocosAngle = -1 * angleDegrees;
	}
	
	public void reverse() {
		cocosAngle = -1 * cocosAngle;
//		ratio = -1 * ratio;
		ratio = (brick.getPosition().y - str_pos.y) / (brick.getPosition().x - str_pos.x);
		
		angleRadians = -1 * angleRadians;
		realY = (float)(Math.tan(angleRadians) * (realX - brick.getPosition().x)) + brick.getPosition().y;
		
//		realY = (realX * ratio * -1) + brick.getPosition().y;
//		realY = ((realX - brick.getPosition().x) * ratio * -1) + brick.getPosition().y;
		update();
	}
	
	public void update() {
		realDest = CGPoint.ccp(realX, realY);
		
		offRealX = realX - brick.getPosition().x;
		offRealY = realY - brick.getPosition().y;
		length = (float)Math.sqrt((offRealX * offRealX) + (offRealY * offRealY));
		velocity = 480.0f / 1.0f;
		realMoveDuration = length / velocity;
	}
	
	public void setHit(Boolean hit) {
		hitTheWall = hit;
	}
	
	public Boolean getHit() {
		return hitTheWall;
	}
	
	public Boolean isValid() {
		return offX > 0;
	}
	
	public float getCocosAngle() {
		return (float)cocosAngle;
	}
	
	public float getRealMoveDuration() {
		return realMoveDuration;
	}
	
	public CGPoint getRealDest() {
		return realDest;
	}
}
