package Client;

import GUI.ClientLogin;
import Support.BlockData;
import Support.ChangeRequest;
import Support.Model.Player;
import Support.Model.Room;
import Support.TypeBlock;
import Support.Utils.DataUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

public class NioClient implements Runnable {
    // The host:port combination to connect to
    private InetAddress hostAddress;
    private int port;

    // The selector we'll be monitoring
    private Selector selector;

    // The buffer into which we'll read data when it's available
    private ByteBuffer readBuffer = ByteBuffer.allocate(8192);

    // A list of PendingChange instances
    private List pendingChanges = new LinkedList();

    // Maps a SocketChannel to a list of ByteBuffer instances
    private List pendingData = new LinkedList();

    // Maps a SocketChannel to a RspHandler
    //private Map rspHandlers = Collections.synchronizedMap(new HashMap());
    // SocketChannel Sever
    private SocketChannel socket = null;

    public ClientLogin getGame() {
        return game;
    }

    public void setGame(ClientLogin game) {
        this.game = game;
    }

    ClientLogin game;

    private Player player;
    public Player getPlayer() {
        return player;
    }
    private List<String> rooms;
    private List<String> players;
    private RspHandler worker;


    public NioClient(InetAddress hostAddress, int port, RspHandler worker) throws IOException {
        this.hostAddress = hostAddress;
        this.port = port;
        this.worker = worker;
        socket = this.initiateConnection();
        this.selector = this.initSelector();
    }

    public void send(byte[] data) throws IOException {
        synchronized (this.pendingChanges) {
            // Indicate we want the interest ops set changed
            this.pendingChanges.add(new ChangeRequest(socket, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

            // And queue the data we want written
            synchronized (this.pendingData) {
                this.pendingData.add(ByteBuffer.wrap(data));
            }
        }
        // Finally, wake up our selecting thread so it can make the required changes
        this.selector.wakeup();
    }

    public void setPlayerID(String id){
        player = new Player(Long.parseLong(id));
    }

    public void setPlayerRoom(String id){
        player.setRoom(Long.parseLong(id));
    }

    public void parseAllRoom(String str){
        rooms = Arrays.asList(DataUtil.parseRoom(str));
      //  players = Arrays.asList(DataUtil.parseRo(str));
        game.UpdateList(rooms );
//        int length = list.length;
//        for(int i =0; i < length;++i){
//            rooms.add(new Room(Long.parseLong(list[i])));
//        }

    }

    public void run() {
        while (true) {
            try {
                // Process any pending changes
//                synchronized (this.pendingChanges) {
//                    while (!this.pendingChanges.isEmpty()) {
//                        ChangeRequest change = (ChangeRequest) this.pendingChanges.get(0);
//                        switch (change.type) {
//                            case ChangeRequest.CHANGEOPS:
//                                SelectionKey key = change.socket.keyFor(this.selector);
//                                key.interestOps(change.ops);
//                                break;
//                            case ChangeRequest.REGISTER:
//                                change.socket.register(this.selector, change.ops);
//                                break;
//                        }
//                        this.pendingChanges.remove(0);
//                        break;
//                    }
//                }
                synchronized (this.pendingChanges) {
                    Iterator changes = this.pendingChanges.iterator();
                    while (changes.hasNext()) {
                        ChangeRequest change = (ChangeRequest) changes.next();
                        switch (change.type) {
                            case ChangeRequest.CHANGEOPS:
                                SelectionKey key = change.socket.keyFor(this.selector);
                                key.interestOps(change.ops);
                                break;
                            case ChangeRequest.REGISTER:
                                change.socket.register(this.selector, change.ops);
                                break;
                        }
                    }
                    this.pendingChanges.clear();
                }

                // Wait for an event one of the registered channels
                this.selector.select();

                // Iterate over the set of keys for which events are available
                Iterator selectedKeys = this.selector.selectedKeys().iterator();
                while (selectedKeys.hasNext()) {
                    SelectionKey key = (SelectionKey) selectedKeys.next();
                    selectedKeys.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    // Check what event is available and deal with it
                    if (key.isConnectable()) {
                        this.finishConnection(key);
                    } else if (key.isReadable()) {
                        this.read(key);
                    } else if (key.isWritable()) {
                        this.write(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        // Clear out our read buffer so it's ready for new data
        this.readBuffer.clear();

        // Attempt to read off the channel
        int numRead;
        try {
            numRead = socketChannel.read(this.readBuffer);
        } catch (IOException e) {
            // The remote forcibly closed the connection, cancel
            // the selection key and close the channel.
            key.cancel();
            socketChannel.close();
            return;
        }

        if (numRead == -1) {
            // Remote entity shut the socket down cleanly. Do the
            // same from our end and cancel the channel.
            key.channel().close();
            key.cancel();
            return;
        }

        // Handle the response
        this.handleResponse(socketChannel, this.readBuffer.array(), numRead);
    }

    private void handleResponse(SocketChannel socketChannel, byte[] data, int numRead) throws IOException {
        // Make a correctly sized copy of the data before handing it
        // to the client
        byte[] rspData = new byte[numRead];
        System.arraycopy(data, 0, rspData, 0, numRead);

        // Look up the handler for this channel
        //RspHandler handler = (RspHandler) this.rspHandlers.get(socketChannel);

        // And pass the response to it
        worker.handleResponse(rspData);
//       if ) {
//			// The handler has seen enough, close the connection
//			socketChannel.close();
//			socketChannel.keyFor(this.selector).cancel();
//        }
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        synchronized (this.pendingData) {
            // Write until there's not more data ...
            while (!this.pendingData.isEmpty()) {
                ByteBuffer buf = (ByteBuffer) this.pendingData.get(0);
                socket.write(buf);
                if (buf.remaining() > 0) {
                    // ... or the socket's buffer fills up
                    break;
                }
                this.pendingData.remove(0);
            }

            if (this.pendingData.isEmpty()) {
                // We wrote away all data, so we're no longer interested
                // in writing on this socket. Switch back to waiting for
                // data.
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    private void finishConnection(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        // Finish the connection. If the connection operation failed
        // this will raise an IOException.
        try {
            socketChannel.finishConnect();
        } catch (IOException e) {
            // Cancel the channel's registration with our selector
            System.out.println(e);
            key.cancel();
            return;
        }
        // Register an interest in writing on this channel
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private SocketChannel initiateConnection() throws IOException {
        // Create a non-blocking socket channel
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        // Kick off connection establishment
        socketChannel.connect(new InetSocketAddress(this.hostAddress, this.port));

        // Queue a channel registration since the caller is not the
        // selecting thread. As part of the registration we'll register
        // an interest in connection events. These are raised when a channel
        // is ready to complete connection establishment.
        synchronized (this.pendingChanges) {
            this.pendingChanges.add(new ChangeRequest(socketChannel, ChangeRequest.REGISTER, SelectionKey.OP_CONNECT));
        }

        return socketChannel;
    }

    private Selector initSelector() throws IOException {
        // Create a new selector
        return SelectorProvider.provider().openSelector();
    }

    public static void main(String[] args) {
        try {
            RspHandler handler = new RspHandler();
            NioClient client = new NioClient(InetAddress.getByName("localhost"), 9090, handler);
            Thread t = new Thread(client);
            t.setDaemon(true);
            t.start();
            //client.SetUpSocket();
            new Thread(handler).start();
            while(true){

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}