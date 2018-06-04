package Support.Model;

import Support.Utils.TimeUtil;

import java.nio.channels.SocketChannel;

public class Player extends BaseObject{
    SocketChannel socketChannel;
    private Room room;

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
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
