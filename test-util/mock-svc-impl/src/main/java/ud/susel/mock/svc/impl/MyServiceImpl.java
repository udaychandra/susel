package ud.susel.mock.svc.impl;

import ud.susel.mock.svc.MyService;

public class MyServiceImpl implements MyService {
    @Override
    public String hello() {
        return "Hello";
    }
}

