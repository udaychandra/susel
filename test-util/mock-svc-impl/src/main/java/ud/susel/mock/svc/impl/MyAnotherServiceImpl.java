package ud.susel.mock.svc.impl;

import ud.susel.api.Activate;
import ud.susel.api.Cardinality;
import ud.susel.api.Context;
import ud.susel.api.ServiceReference;
import ud.susel.mock.svc.MyAnotherService;
import ud.susel.mock.svc.MyService;

import java.util.List;

public class MyAnotherServiceImpl implements MyAnotherService {

    private MyService myService;
    private String name;

    @ServiceReference
    public void setMyService(MyService myService) {
        this.myService = myService;
    }

    @Activate
    public void activateCustom(Context context) {
        name = context.value("name");
    }

    @Override
    public String hello() {
        return myService.hello() + " " + name;
    }
}
