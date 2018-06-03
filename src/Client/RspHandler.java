package Client;

import Support.BlockData;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

public class RspHandler implements Runnable {
	private byte[] rsp = null;
	private List queue = new LinkedList();

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

			String msg = new String(data);
			BlockData blockData = new BlockData();
			blockData.parse(msg);
			System.out.println(blockData.getMsg());
		}
	}

	@Override
	public void run() {
		waitForResponse();
	}
}