package io.github.udaychandra.susel.mock.svc.impl;

import io.github.udaychandra.susel.api.Activate;
import io.github.udaychandra.susel.api.Context;
import io.github.udaychandra.susel.api.ServiceReference;
import io.github.udaychandra.susel.mock.svc.MyAnotherService;
import io.github.udaychandra.susel.mock.svc.MyService;

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
