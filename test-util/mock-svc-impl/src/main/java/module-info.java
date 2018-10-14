import io.github.udaychandra.susel.mock.svc.MyAnotherService;
import io.github.udaychandra.susel.mock.svc.MyService;
import io.github.udaychandra.susel.mock.svc.impl.MyAnotherServiceImpl;
import io.github.udaychandra.susel.mock.svc.impl.MyServiceImpl;
import io.github.udaychandra.susel.mock.svc.impl.MyServiceImpl2;

module io.github.udaychandra.susel.mock.svc.impl {
    exports io.github.udaychandra.susel.mock.svc.impl;

    requires io.github.udaychandra.susel;
    requires io.github.udaychandra.susel.mock.svc;

    provides MyService
            with MyServiceImpl, MyServiceImpl2;
    provides MyAnotherService with MyAnotherServiceImpl;
}
