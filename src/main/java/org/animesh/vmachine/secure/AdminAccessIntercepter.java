package org.animesh.vmachine.secure;

import java.io.Console;
import java.lang.reflect.Method;

import javax.inject.Inject;

import org.animesh.vmachine.VendingMachineContext;
import org.animesh.vmachine.command.exp.NotAuthorizedException;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminAccessIntercepter implements org.aopalliance.intercept.MethodInterceptor {
    // LOG
    private static final Logger LOG = LoggerFactory.getLogger(AdminAccessIntercepter.class);

    @Inject
    VendingMachineContext context;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        LOG.debug("invocation ==> {}", method);

        // Allow Admin
        if (context.isAdmin()) {
            return invocation.proceed();
        }

        // verify Admin
        Console console = System.console();
        String password = new String(console.readPassword("Authorization required - please enter your password: "));
        if (context.verifyAdminPassword(password)) {
            context.printDebug("Access Granted!");
            context.setAdmin(true);
            return invocation.proceed();
        }

        throw new NotAuthorizedException();
    }

}
