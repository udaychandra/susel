package io.github.udaychandra.susel.mock.svc.impl;

import io.github.udaychandra.susel.mock.svc.MyService;

public class MyServiceImpl implements MyService {
    @Override
    public String hello() {
        return "Hello";
    }
}

