package SnakeGame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyBoard implements KeyListener {

	public boolean up, down, left, right;
	
	@Override
	public void keyPressed(KeyEvent e) {
			
		if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) && !down) {
			setKeyUp();
	}
		else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) && !up) {
			setKeyDown();
		}
		else if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && !right) {
			setKeyLeft();
		}
		else if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && !left) {
			setKeyRight();
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		//keys[e.getKeyCode()] = false;
	}
	@Override
	public void keyTyped(KeyEvent e) {
	}

	public void setKeyUp(){
		up = true;
		down = false;
		left = false;
		right = false;
	}
	public void setKeyDown(){
		up = false;
		down = true;
		left = false;
		right = false;
	}
	public void setKeyLeft(){
		up = false;
		down = false;
		left = true;
		right = false;
	}
	public void setKeyRight(){
		up = false;
		down = false;
		left = false;
		right = true;
	}

}