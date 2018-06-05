package SnakeGame;

import GUI.ClientLogin;
import Support.BlockData;
import Support.TypeBlock;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

public class KeyBoard implements KeyListener {

	public boolean up, down, left, right;
	@Override
	public void keyPressed(KeyEvent e) {
			
		if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) && !down && !up)  {
			setKeyUp();
			BlockData blockData = new BlockData(TypeBlock.UPDATEGAME,Long.toString(ClientLogin.client.getPlayer().getRoom().getId()) +"~" + Long.toString(ClientLogin.client.getPlayer().getId()) + "~" +  Integer.toString(KeyEvent.VK_UP));
			try {
				ClientLogin.client.send(blockData.toBytes());
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
		else if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) && !up  && !down) {
			setKeyDown();
			BlockData blockData = new BlockData(TypeBlock.UPDATEGAME,Long.toString(ClientLogin.client.getPlayer().getRoom().getId()) +"~" + Long.toString(ClientLogin.client.getPlayer().getId()) + "~" +  Integer.toString(KeyEvent.VK_DOWN));
			try {
				ClientLogin.client.send(blockData.toBytes());
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
		else if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && !right  && !left) {
			setKeyLeft();
			BlockData blockData = new BlockData(TypeBlock.UPDATEGAME,Long.toString(ClientLogin.client.getPlayer().getRoom().getId()) +"~" + Long.toString(ClientLogin.client.getPlayer().getId()) + "~" +  Integer.toString(KeyEvent.VK_LEFT));
			try {
				ClientLogin.client.send(blockData.toBytes());
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
		else if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && !left  && !right) {
			setKeyRight();
			BlockData blockData = new BlockData(TypeBlock.UPDATEGAME,Long.toString(ClientLogin.client.getPlayer().getRoom().getId()) +"~" + Long.toString(ClientLogin.client.getPlayer().getId()) + "~" +  Integer.toString(KeyEvent.VK_RIGHT));
			try {
				ClientLogin.client.send(blockData.toBytes());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {

	}
	@Override
	public void keyTyped(KeyEvent e) {
	}

	public void setKeyUp(){
		up = true;
		down = false;
		left = false;
		right = false;
		System.out.println("Press Up");
	}
	public void setKeyDown(){
		up = false;
		down = true;
		left = false;
		right = false;
		System.out.println("Press Down");
	}
	public void setKeyLeft(){
		up = false;
		down = false;
		left = true;
		right = false;
		System.out.println("Press Left");
	}
	public void setKeyRight(){
		up = false;
		down = false;
		left = false;
		right = true;
		System.out.println("Press Right");
	}

}