package Server;

import Support.BlockData;
import Support.Model.Room;
import Support.TypeBlock;
import Support.Utils.DataUtil;

import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

public class EchoWorker implements Runnable {
	private List queue = new LinkedList();

	public void processData(NioServer server, SocketChannel socket, byte[] data, int count) {
		byte[] dataCopy = new byte[count];
		System.arraycopy(data, 0, dataCopy, 0, count);
		synchronized (queue) {
			queue.add(new ServerDataEvent(server, socket, dataCopy));
			queue.notify();
		}
	}

	public void run() {
		ServerDataEvent dataEvent;

		while (true) {
			// Wait for data to become available
			synchronized (queue) {
				while (queue.isEmpty()) {
					try {
						queue.wait();
					} catch (InterruptedException e) {
					}
				}
				dataEvent = (ServerDataEvent) queue.remove(0);
			}

			String data = new String(dataEvent.data);
			String[] msg = data.split("]");
			for(int i =0; i < msg.length;++i) {
				BlockData blockData = new BlockData(msg[i]);
				if(blockData.getType() == TypeBlock.CREATEROOM){
					String ID = DataUtil.parseData(blockData.getMsg(),0);
					String Data = DataUtil.parseData(blockData.getMsg(),1);
					String idRoom = dataEvent.server.createRoom(ID, Data );
					BlockData blockCreate = new BlockData(TypeBlock.CREATEROOM,idRoom);
					dataEvent.server.send(dataEvent.socket,blockCreate.toBytes());
					dataEvent.server.sendCreateRoomAll();
					dataEvent.server.getSeverLog().UpdateList("Player " + ID + " create room " + idRoom);
				}else if(blockData.getType() == TypeBlock.LOGIN){
					dataEvent.server.send(dataEvent.socket, dataEvent.data);
					dataEvent.server.sendRoomtToPlayer(dataEvent.socket);
				}else if(blockData.getType() == TypeBlock.DISCONNECT){
					System.out.println(blockData.getMsg());
				}else if(blockData.getType() == TypeBlock.OUTROOM){
					long id = Long.parseLong(blockData.getMsg());
					List<Room> list = dataEvent.server.getRooms();
					for(int j = 0; j < list.size();++j){
						if(list.get(j).checkExist(id)){
							list.get(j).remove(id);
							if(id == list.get(j).getPlayerHost().getId()){
								list.get(j).remove(list.get(j).getPlayerHost().getId());
								if(list.get(j).getListPlayer().size() == 0){
									dataEvent.server.removeRoom(list.get(j).getId());
									dataEvent.server.sendCreateRoomAll();
								}else{
									list.get(j).setPlayerHost(list.get(j).getListPlayer().get(0));
									dataEvent.server.sendCreateRoomAll();
								}
							}
							break;
						}
					}
				}else if(blockData.getType() == TypeBlock.JOINROOM){
					String ID = DataUtil.parseData(blockData.getMsg(),0);
					String room = DataUtil.parseData(blockData.getMsg(),1);
					String pass = DataUtil.parseData(blockData.getMsg(),2);
					int error = dataEvent.server.joinRoom(ID,room,pass);
					BlockData blockData1 = new BlockData(TypeBlock.JOINROOM,Integer.toString(error));
					dataEvent.server.send(dataEvent.socket,blockData1.toBytes());
					dataEvent.server.sendUpdateRoom(room);
					//System.out.println(blockData.getMsg());
				}
			}




			//System.out.println(blockData.getMsg());
			//Sử lí dữ liệu gửi về client;

			// Return to sender
			//dataEvent.server.sendAll(dataEvent.data);
			//dataEvent.server.send(dataEvent.socket, dataEvent.data);
		}
	}
}