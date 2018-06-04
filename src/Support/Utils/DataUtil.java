package Support.Utils;

public class DataUtil {
    public static String parsePlayerId(String str){
            return str.split("~")[0];
    }
    public static String parseData(String str){
        return str.split("~")[1];
    }
    public static String[] parseRoom(String str){
        return str.split("~");
    }
}
