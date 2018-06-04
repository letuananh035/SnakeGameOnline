package Support;

public enum TypeBlock {
    //Type Define
    ERROR("ERROR", 0),
    LOGIN("LOGIN", 1),
    START("START", 2),
    DISCONNECT("DISCONNECT", 3),
    CREATEROOM("CREATEROOM", 4),
    NAME("NAME", 5),
    ALLROOM("ALLROOM",6);

    private String stringValue;
    private int intValue;

    private TypeBlock(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    public static TypeBlock fromString(String text) {
        for (TypeBlock item : TypeBlock.values()) {
            if (item.toString().equalsIgnoreCase(text)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
