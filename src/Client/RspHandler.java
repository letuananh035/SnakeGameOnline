package Client;

import Support.BlockData;
import Support.TypeBlock;
import Support.Utils.DataUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

public class RspHandler implements Runnable {
	private byte[] rsp = null;
	private List queue = new LinkedList();
	public  NioClient client;
	private String saveData = "";
	public void handleResponse(byte[] rsp) {
		synchronized (queue) {
			queue.add(rsp);
			queue.notify();
		}
	}

	public void waitForResponse() {
		String data;
		while (true) {
			// Wait for data to become available
			synchronized (queue) {
				while (queue.isEmpty()) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
					}
				}
				data = new String((byte[])queue.remove(0));
			}

			String[] list = data.split("]");
			for(int i =0; i < list.length;++i){
				if(list[i] != ""){
					String msg = new String(list[i]);
					BlockData blockData = new BlockData(msg);
					if(blockData.getType() == TypeBlock.CREATEROOM){
						System.out.println("Room create: " + blockData.getMsg());
						client.setPlayerCreateRoom(blockData.getMsg());
					}else if(blockData.getType() == TypeBlock.ALLROOM){
						System.out.println("All Room: " + blockData.getMsg());
						client.parseAllRoom(blockData.getMsg());
					}else if(blockData.getType() == TypeBlock.LOGIN){
						System.out.println("Player ID: " + blockData.getMsg());
						client.setPlayerID(blockData.getMsg());
					}else if(blockData.getType() == TypeBlock.JOINROOM){
						int error = Integer.parseInt(blockData.getMsg());
						if(error == 0){
							client.getGame().UpdateLobby("");
						}else if(error == -1){
							System.out.println("Room không tồn tại!");
						}else if(error == -2){
							System.out.println("Mật khẩu không đúng!");
						}
					}else if(blockData.getType() == TypeBlock.UPDATEROOM){
						client.getGame().UpdateLobby(blockData.getMsg());
					}
				}
			}
		}
	}

	@Override
	public void run() {
		waitForResponse();
	}
}