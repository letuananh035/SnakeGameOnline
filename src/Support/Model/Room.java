package Support.Model;

import java.util.List;

public class Room extends BaseObject {
    private List<Player> listPlayer;
    private String passWord;
    private Player playerHost;

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getPassWord() {
        return passWord;
    }

    public Player getPlayerHost() {
        return playerHost;
    }

    public void setPlayerHost(Player playerHost) {
        this.playerHost = playerHost;
    }

    public Room() {
        super();
    }
    public Room(long id) {
        //super();
        this.id = id;
    }
}
