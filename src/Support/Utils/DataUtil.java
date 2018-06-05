package Support.Utils;

public class DataUtil {
    public static String parseData(String str,int id){
        return str.split("~")[id];
    }
    public static String[] parseRoom(String str){
        return str.split("~");
    }
}
