module ud.susel.mock.svc.impl {
    exports ud.susel.mock.svc.impl;

    requires ud.susel;
    requires ud.susel.mock.svc;

    provides ud.susel.mock.svc.MyService
            with ud.susel.mock.svc.impl.MyServiceImpl, ud.susel.mock.svc.impl.MyServiceImpl2;
    provides ud.susel.mock.svc.MyAnotherService with ud.susel.mock.svc.impl.MyAnotherServiceImpl;
}