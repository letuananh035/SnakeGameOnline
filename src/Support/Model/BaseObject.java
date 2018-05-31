package Support.Model;

import Support.Utils.TimeUtil;

public class BaseObject {
    private long id;
    public long getId() {
        return id;
    }
    public BaseObject() {
        id = TimeUtil.getTimeNow();
    }

}
