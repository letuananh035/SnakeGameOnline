package Client;

import GUI.ClientLogin;
import Support.BlockData;
import Support.MessageBox;
import Support.TypeBlock;
import Support.Utils.DataUtil;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
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
							MessageBox.showInfo("Phòng không tồn tại!");
						}else if(error == -2){
							System.out.println("Mật khẩu không đúng!");
							MessageBox.showInfo("Mật khẩu không đúng!");
						}
						else if(error == -3){
							System.out.println("Room đẫ đầy hoặc đang chơi!");
							MessageBox.showInfo("Phòng đã đầy hoặc đang chơi!");
						}
					}else if(blockData.getType() == TypeBlock.UPDATEROOM){
						if(ClientLogin.roomGame == null){
							client.getGame().UpdateLobby(blockData.getMsg());
						}else{
							client.getGame().UpdateListPlayerInGame(blockData.getMsg());
						}

					}else if(blockData.getType() == TypeBlock.ALLPLAYER){
						String[] listPlayer = DataUtil.parseRoom(blockData.getMsg());
						client.getGame().UpdateListPlayer(Arrays.asList(listPlayer));
					}else if(blockData.getType() == TypeBlock.UPDATEGAME){
						client.getGame().UpdateGame(blockData.getMsg());
					}else if(blockData.getType() == TypeBlock.START){
						client.getGame().StartGame();
					}else if(blockData.getType() == TypeBlock.UPDATESCORE){
						client.getGame().UpdateScore(blockData.getMsg());
					}else if(blockData.getType() == TypeBlock.UPDATECOUNT){
						client.getGame().UpdateCount(blockData.getMsg());
					}else if(blockData.getType() == TypeBlock.ENDGAME){
                        client.getGame().updateEndGame(blockData.getMsg());
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