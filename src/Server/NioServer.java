package Server;

import GUI.ClientLogin;
import GUI.GameSever;
import GUI.Sever.SeverLog;
import Support.BlockData;
import Support.ChangeRequest;
import Support.Model.Player;
import Support.Model.Room;
import Support.TypeBlock;
import jdk.nashorn.internal.ir.Block;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;

public class NioServer implements Runnable {
    // The host:port combination to listen on
    private InetAddress hostAddress;
    private int port;

    // The channel on which we'll accept connections
    private ServerSocketChannel serverChannel;

    // The selector we'll be monitoring
    private Selector selector;

    // The buffer into which we'll read data when it's available
    private ByteBuffer readBuffer = ByteBuffer.allocate(65536);

    private EchoWorker worker;

    // A list of PendingChange instances
    private List pendingChanges = new LinkedList();

    // Maps a SocketChannel to a list of ByteBuffer instances
    private Map pendingData = new HashMap();

    //List player connection
    private List<Player> players = new ArrayList<Player>();

    //List room connection
    private List<Room> rooms = new ArrayList<Room>();

    private SeverLog severLog;

    public ClientLogin getGame() {
        return game;
    }

    public void setGame(ClientLogin game) {
        this.game = game;
    }

    ClientLogin game;

    public List<Room> getRooms() {
        return rooms;
    }

    public SeverLog getSeverLog() {
        return severLog;
    }

    public static NioServer mainSever;

    public void setSeverLog(SeverLog severLog) {
        this.severLog = severLog;
    }

    public NioServer(InetAddress hostAddress, int port, EchoWorker worker) throws IOException {
        this.hostAddress = hostAddress;
        this.port = port;
        this.selector = this.initSelector();
        this.worker = worker;
        this.mainSever = this;
    }

    public void send(SocketChannel socket, byte[] data) {
        synchronized (this.pendingChanges) {
            // Indicate we want the interest ops set changed
            this.pendingChanges.add(new ChangeRequest(socket, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

            // And queue the data we want written
            synchronized (this.pendingData) {
                List queue = (List) this.pendingData.get(socket);
                if (queue == null) {
                    queue = new ArrayList();
                    this.pendingData.put(socket, queue);
                }
                queue.add(ByteBuffer.wrap(data));
            }
        }

        // Finally, wake up our selecting thread so it can make the required changes
        this.selector.wakeup();
    }

    public void sendAll(byte[] data) {
        synchronized (this.pendingChanges) {
            players.forEach(player -> {
                // Indicate we want the interest ops set changed
                this.pendingChanges.add(new ChangeRequest(player.getSocketChannel(), ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

                // And queue the data we want written
                synchronized (this.pendingData) {
                    List queue = (List) this.pendingData.get(player.getSocketChannel());
                    if (queue == null) {
                        queue = new ArrayList();
                        this.pendingData.put(player.getSocketChannel(), queue);
                    }
                    queue.add(ByteBuffer.wrap(data));
                }
            });
        }

        // Finally, wake up our selecting thread so it can make the required changes
        this.selector.wakeup();
    }


    public void sendCountToRooom(String id, int data){
        List<Player> playerList = new ArrayList<Player>();
        for(int i = 0; i < rooms.size();++i){
            if(rooms.get(i).getId() == Long.parseLong(id)){
                playerList = rooms.get(i).getListPlayer();
            }
        }
        synchronized (this.pendingChanges) {
            playerList.forEach(player -> {
                // Indicate we want the interest ops set changed
                this.pendingChanges.add(new ChangeRequest(player.getSocketChannel(), ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

                BlockData blockData = new BlockData(TypeBlock.UPDATECOUNT, Integer.toString(data));

                // And queue the data we want written
                synchronized (this.pendingData) {
                    List queue = (List) this.pendingData.get(player.getSocketChannel());
                    if (queue == null) {
                        queue = new ArrayList();
                        this.pendingData.put(player.getSocketChannel(), queue);
                    }
                    queue.add(ByteBuffer.wrap(blockData.toBytes()));
                }
            });
        }
        // Finally, wake up our selecting thread so it can make the required changes
        this.selector.wakeup();
    }

    public void sendEndGameToRooom(String id, String data){
        List<Player> playerList = new ArrayList<Player>();
        for(int i = 0; i < rooms.size();++i){
            if(rooms.get(i).getId() == Long.parseLong(id)){
                playerList = rooms.get(i).getListPlayer();
            }
        }
        synchronized (this.pendingChanges) {
            playerList.forEach(player -> {
                // Indicate we want the interest ops set changed
                this.pendingChanges.add(new ChangeRequest(player.getSocketChannel(), ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

                BlockData blockData = new BlockData(TypeBlock.ENDGAME, data);

                // And queue the data we want written
                synchronized (this.pendingData) {
                    List queue = (List) this.pendingData.get(player.getSocketChannel());
                    if (queue == null) {
                        queue = new ArrayList();
                        this.pendingData.put(player.getSocketChannel(), queue);
                    }
                    queue.add(ByteBuffer.wrap(blockData.toBytes()));
                }
            });
        }
        // Finally, wake up our selecting thread so it can make the required changes
        this.selector.wakeup();
    }

    public void sendScoreToRooom(String id, String data){
        List<Player> playerList = new ArrayList<Player>();
        for(int i = 0; i < rooms.size();++i){
            if(rooms.get(i).getId() == Long.parseLong(id)){
                playerList = rooms.get(i).getListPlayer();
            }
        }
        synchronized (this.pendingChanges) {
            playerList.forEach(player -> {
                // Indicate we want the interest ops set changed
                this.pendingChanges.add(new ChangeRequest(player.getSocketChannel(), ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

                BlockData blockData = new BlockData(TypeBlock.UPDATESCORE, data);

                // And queue the data we want written
                synchronized (this.pendingData) {
                    List queue = (List) this.pendingData.get(player.getSocketChannel());
                    if (queue == null) {
                        queue = new ArrayList();
                        this.pendingData.put(player.getSocketChannel(), queue);
                    }
                    queue.add(ByteBuffer.wrap(blockData.toBytes()));
                }
            });
        }
        // Finally, wake up our selecting thread so it can make the required changes
        this.selector.wakeup();
    }

    public void UpdateGame(long idRoom,long idPlayer, int key){
        for(int i =0; i < rooms.size();++i){
            if(rooms.get(i).getId() == idRoom){
                rooms.get(i).handle.updateKey(idPlayer,key);
            }
        }
    }

    public void reStartGame(String id){
        for(int i = 0; i < rooms.size();++i){
            if(rooms.get(i).getId() == Long.parseLong(id)) {
                sendGamePlayRooom(rooms.get(i).getListPlayer());
                rooms.get(i).handle.Stop();
                rooms.get(i).handle = new GameSever(rooms.get(i).getListPlayer());
                rooms.get(i).handle.Start();
                break;
            }
        }
    }

    public void runGame(String id){
        for(int i = 0; i < rooms.size();++i){
            if(rooms.get(i).getId() == Long.parseLong(id)) {
                if (rooms.get(i).getListPlayer().size() >= 2) {
                    sendGamePlayRooom(rooms.get(i).getListPlayer());
                    rooms.get(i).handle = new GameSever(rooms.get(i).getListPlayer());
                    rooms.get(i).handle.Start();
                    break;
                }
            }
        }
    }

    public void sendGamePlayRooom( List<Player> playerList){
        synchronized (this.pendingChanges) {
            playerList.forEach(player -> {
                // Indicate we want the interest ops set changed
                this.pendingChanges.add(new ChangeRequest(player.getSocketChannel(), ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

                BlockData blockData = new BlockData(TypeBlock.START, "1");

                // And queue the data we want written
                synchronized (this.pendingData) {
                    List queue = (List) this.pendingData.get(player.getSocketChannel());
                    if (queue == null) {
                        queue = new ArrayList();
                        this.pendingData.put(player.getSocketChannel(), queue);
                    }
                    queue.add(ByteBuffer.wrap(blockData.toBytes()));
                }
            });
        }
        // Finally, wake up our selecting thread so it can make the required changes
        this.selector.wakeup();
    }

    public void sendGameToRooom(String id, String data){
        List<Player> playerList = new ArrayList<Player>();
        for(int i = 0; i < rooms.size();++i){
            if(rooms.get(i).getId() == Long.parseLong(id)){
                playerList = rooms.get(i).getListPlayer();
            }
        }
        synchronized (this.pendingChanges) {
            playerList.forEach(player -> {
                // Indicate we want the interest ops set changed
                this.pendingChanges.add(new ChangeRequest(player.getSocketChannel(), ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

                BlockData blockData = new BlockData(TypeBlock.UPDATEGAME, data);

                // And queue the data we want written
                synchronized (this.pendingData) {
                    List queue = (List) this.pendingData.get(player.getSocketChannel());
                    if (queue == null) {
                        queue = new ArrayList();
                        this.pendingData.put(player.getSocketChannel(), queue);
                    }
                    queue.add(ByteBuffer.wrap(blockData.toBytes()));
                }
            });
        }
        // Finally, wake up our selecting thread so it can make the required changes
        this.selector.wakeup();
    }

    public void sendPlayerAll(){

        synchronized (this.pendingChanges) {
            players.forEach(player -> {
                // Indicate we want the interest ops set changed
                this.pendingChanges.add(new ChangeRequest(player.getSocketChannel(), ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));


                String listPlayer = "";
                for(int i = 0; i < players.size();++i){
                    listPlayer += Long.toString(players.get(i).getId()) + "~";
                }
                if(players.size() > 0) listPlayer = listPlayer.substring(0,listPlayer.length() - 1);

                BlockData data = new BlockData(TypeBlock.ALLPLAYER,listPlayer);

                // And queue the data we want written
                synchronized (this.pendingData) {
                    List queue = (List) this.pendingData.get(player.getSocketChannel());
                    if (queue == null) {
                        queue = new ArrayList();
                        this.pendingData.put(player.getSocketChannel(), queue);
                    }
                    queue.add(ByteBuffer.wrap(data.toBytes()));
                }
            });
        }
        // Finally, wake up our selecting thread so it can make the required changes
        this.selector.wakeup();
    }

    public String createRoom(String ID,String password){
        Room room = new Room();
        int length = players.size();
        for(int i =0; i < length; ++i){
            if(Long.toString(players.get(i).getId()).equals(ID)){
                players.get(i).setRoom(room);
                room.setPlayerHost(players.get(i));
                room.addPlayer(players.get(i));
                room.setPassWord(password);
                rooms.add(room);
                return Long.toString(room.getId());
            }
        }
        return "";
    }

    public void removeRoom(long id){
        for(int i =0; i < rooms.size();++i){
            if(rooms.get(i).getId() == id){
                if(rooms.get(i).handle != null){
                    rooms.get(i).handle.Stop();
                    rooms.get(i).handle = null;
                }
                rooms.remove(i);
                break;
            }
        }
    }

    public void removePlayer(String id){
        for(int i =0; i < players.size();++i){
            if(players.get(i).getId() == Long.parseLong(id)){
                players.remove(i);
                break;
            }
        }
    }

    public int joinRoom(String id,String room,String pass){
        long idLong = Long.parseLong(id);
        long roomLong = Long.parseLong(room);
        int error = -1;
        for(int i =0; i < rooms.size();++i){
            if(rooms.get(i).getId() == roomLong ){
                if(rooms.get(i).getPassWord().equals(pass)){
                    if(rooms.get(i).getListPlayer().size() < 4 && rooms.get(i).handle == null){
                        Player p = getPlayerFromID(idLong);
                        if(p != null){
                            p.setRoom(rooms.get(i));
                            rooms.get(i).addPlayer(p);
                            error = 0;
                        }
                    } else{
                      error = -3;
                    }
                }
                else{
                    error = -2;
                }
                break;
            }
        }
        return error;
    }


    public Player getPlayerFromID(long id){
        for(int i =0; i < players.size();++i){
            if(players.get(i).getId() == id){
                return players.get(i);
            }
        }
        return null;
    }

    public void sendUpdateRoom(String id) {
        List<Player> players_ = null;
        for(int i =0; i < rooms.size();++i){
            if(rooms.get(i).getId() == Long.parseLong(id)){
                players_ = rooms.get(i).getListPlayer();
                if(players_.size() == 1 && rooms.get(i).handle != null && rooms.get(i).handle.ENDGAME == true){
                    rooms.get(i).handle.Stop();
                    rooms.get(i).handle = null;
                }
            }
        }
        synchronized (this.pendingChanges) {
            List<Player> finalPlayers_ = players_;
            if (players_ != null) {
                players_.forEach(player -> {
                    // Indicate we want the interest ops set changed
                    this.pendingChanges.add(new ChangeRequest(player.getSocketChannel(), ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));


                    String listPlayer = "";
                    for (int i = 0; i < finalPlayers_.size(); ++i) {
                        listPlayer += Long.toString(finalPlayers_.get(i).getId()) + "~";
                    }
                    if (finalPlayers_.size() > 0) listPlayer = listPlayer.substring(0, listPlayer.length() - 1);

                    BlockData data = new BlockData(TypeBlock.UPDATEROOM, listPlayer);

                    // And queue the data we want written
                    synchronized (this.pendingData) {
                        List queue = (List) this.pendingData.get(player.getSocketChannel());
                        if (queue == null) {
                            queue = new ArrayList();
                            this.pendingData.put(player.getSocketChannel(), queue);
                        }
                        queue.add(ByteBuffer.wrap(data.toBytes()));
                    }
                });
            }
            // Finally, wake up our selecting thread so it can make the required changes
            this.selector.wakeup();
        }
    }

    public void sendCreateRoomAll() {
        synchronized (this.pendingChanges) {
            players.forEach(player -> {
                // Indicate we want the interest ops set changed
                this.pendingChanges.add(new ChangeRequest(player.getSocketChannel(), ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));


                String listRoom = "";
                for(int i = 0; i < rooms.size();++i){
                    listRoom += Long.toString(rooms.get(i).getId()) + "~";
                }
                if(rooms.size() > 0) listRoom = listRoom.substring(0,listRoom.length() - 1);

                BlockData data = new BlockData(TypeBlock.ALLROOM,listRoom);

                // And queue the data we want written
                synchronized (this.pendingData) {
                    List queue = (List) this.pendingData.get(player.getSocketChannel());
                    if (queue == null) {
                        queue = new ArrayList();
                        this.pendingData.put(player.getSocketChannel(), queue);
                    }
                    queue.add(ByteBuffer.wrap(data.toBytes()));
                }
            });
        }
        // Finally, wake up our selecting thread so it can make the required changes
        this.selector.wakeup();
    }

    public void sendRoomtToPlayer(SocketChannel socket) {
        if(rooms.size() == 0) return;
        synchronized (this.pendingChanges) {
            // Indicate we want the interest ops set changed
            this.pendingChanges.add(new ChangeRequest(socket, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

            String listRoom = "";
            for(int i = 0; i < rooms.size();++i){
                listRoom += Long.toString(rooms.get(i).getId()) + "~";
            }
            listRoom = listRoom.substring(0,listRoom.length() - 1);
            BlockData data = new BlockData(TypeBlock.ALLROOM,listRoom);

            // And queue the data we want written
            synchronized (this.pendingData) {
                List queue = (List) this.pendingData.get(socket);
                if (queue == null) {
                    queue = new ArrayList();
                    this.pendingData.put(socket, queue);
                }
                queue.add(ByteBuffer.wrap(data.toBytes()));
            }
        }

        // Finally, wake up our selecting thread so it can make the required changes
        this.selector.wakeup();
    }

    public void run() {
        while (true) {
            try {
                // Process any pending changes
                synchronized (this.pendingChanges) {
                    Iterator changes = this.pendingChanges.iterator();
                    while (changes.hasNext()) {
                        ChangeRequest change = (ChangeRequest) changes.next();
                        switch (change.type) {
                            case ChangeRequest.CHANGEOPS:
                                SelectionKey key = change.socket.keyFor(this.selector);
                                key.interestOps(change.ops);
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
                    if (key.isAcceptable()) {
                        this.accept(key);
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

    private void accept(SelectionKey key) throws IOException {
        // For an accept to be pending the channel must be a server socket channel.
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        // Accept the connection and make it non-blocking
        SocketChannel socketChannel = serverSocketChannel.accept();
        Socket socket = socketChannel.socket();
        socketChannel.configureBlocking(false);

        // Register the new SocketChannel with our Selector, indicating
        // we'd like to be notified when there's data waiting to be read
        socketChannel.register(this.selector, SelectionKey.OP_READ);

        //Add player to list and return id to client

        Player player = new Player();
        player.setSocketChannel(socketChannel);


        players.add(player);
        BlockData blockData = new BlockData(TypeBlock.LOGIN, Long.toString(player.getId()));
        getSeverLog().UpdateList("Player " + Long.toString(player.getId()) + " login!");
        this.worker.processData(this, player.getSocketChannel(), blockData.toBytes(), blockData.toBytes().length);
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

        // Hand the data off to our worker thread
        this.worker.processData(this, socketChannel, this.readBuffer.array(), numRead);
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        synchronized (this.pendingData) {
            List queue = (List) this.pendingData.get(socketChannel);

            // Write until there's not more data ...
            while (!queue.isEmpty()) {
                ByteBuffer buf = (ByteBuffer) queue.get(0);
                socketChannel.write(buf);

                if (buf.remaining() > 0) {
                    // ... or the socket's buffer fills up
                    break;
                }
                queue.remove(0);
            }

            if (queue.isEmpty()) {
                // We wrote away all data, so we're no longer interested
                // in writing on this socket. Switch back to waiting for
                // data.
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    private Selector initSelector() throws IOException {
        // Create a new selector
        Selector socketSelector = SelectorProvider.provider().openSelector();

        // Create a new non-blocking server socket channel
        this.serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        // Bind the server socket to the specified address and port
        InetSocketAddress isa = new InetSocketAddress(this.hostAddress, this.port);
        serverChannel.socket().bind(isa);

        // Register the server socket channel, indicating an interest in
        // accepting new connections
        serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

        return socketSelector;
    }




    public static void main(String[] args) {

    }

}