package Support.Model;

import GUI.GameSever;

import java.util.ArrayList;
import java.util.List;

public class Room extends BaseObject {
    private List<Player> listPlayer = new ArrayList<Player>();
    private String passWord;
    private Player playerHost;

    public GameSever handle;

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

    public List<Player> getListPlayer() {
        return listPlayer;
    }

    public void addPlayer(Player p){
        listPlayer.add(p);
    }

    public void remove(long id){
        for(int i =0; i < listPlayer.size();++i){
            if(listPlayer.get(i).getId() == id){
                listPlayer.remove(i);
                break;
            }
        }
    }

    public boolean checkExist(long id){
        for(int i =0; i < listPlayer.size();++i){
            if(listPlayer.get(i).getId() == id){
                return true;
            }
        }
        return false;
    }

    public Room() {
        super();
    }
    public Room(long id) {
        //super();
        this.id = id;
    }
}
