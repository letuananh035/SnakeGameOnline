package Support.Model;

import Support.Utils.TimeUtil;

import java.nio.channels.SocketChannel;

public class Player extends BaseObject{
    SocketChannel socketChannel;
    private long room;

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public long getRoom() {
        return room;
    }

    public void setRoom(long room) {
        this.room = room;
    }

    public Player() {
        super();
    }

    public Player(long id) {
        //super();
        this.id = id;
    }
}
