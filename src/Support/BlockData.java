package Support;

public class BlockData {
    private TypeBlock type;
    private String msg;

    public TypeBlock getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setType(TypeBlock type) {
        this.type = type;
    }

    public BlockData(){
        type = TypeBlock.ERROR;
        msg = "";
    }

    public BlockData(TypeBlock type, String msg){
        this.type = type;
        this.msg = msg;
    }

    public BlockData(String msg){
        this.parse(msg);
    }

    public void parse(String text){
        String[] listItem = text.split(";");
        if(listItem.length == 1){
            type = TypeBlock.fromString(listItem[0]);
            msg = "";
        }
        else{
            type = TypeBlock.fromString(listItem[0]);
            msg = listItem[1];
        }
    }

    @Override
    public String toString(){
        return type.toString() + ";" + msg;
    }


    public byte[] toBytes(){
        return this.toString().getBytes();
    }
}
