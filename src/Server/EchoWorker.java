package Server;

import Support.BlockData;

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


			String msg = dataEvent.data.toString();
			BlockData blockData = new BlockData();
			blockData.parse(msg);
			System.out.println(blockData.getMsg());
			//Sử lí dữ liệu gửi về client;

			// Return to sender
			dataEvent.server.send(dataEvent.socket, dataEvent.data);
		}
	}
}