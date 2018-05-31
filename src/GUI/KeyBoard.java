package GUI;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyBoard implements KeyListener {

	public boolean up, down, left, right;
	
	@Override
	public void keyPressed(KeyEvent e) {
			
		if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
			up = true;
			down = false;
			left = false;
			right = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
			up = false;
			down = true;
			left = false;
			right = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
			up = false;
			down = false;
			left = true;
			right = false;
		}
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
			up = false;
			down = false;
			left = false;
			right = true;
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		//keys[e.getKeyCode()] = false;
	}
	@Override
	public void keyTyped(KeyEvent e) {
	}


}