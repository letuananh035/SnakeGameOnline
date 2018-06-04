package Support.Model;

import Support.Utils.TimeUtil;

public class BaseObject {
    protected long id;
    public long getId() {
        return id;
    }
    public BaseObject() {
        id = TimeUtil.getTimeNow();
    }

}
