package org.animesh.vmachine;

import org.animesh.vmachine.command.CommandListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;
import com.google.inject.Stage;
import com.mycila.inject.jsr250.Jsr250;
import com.mycila.inject.jsr250.Jsr250Injector;

/**
 * The Class VendingMachine.
 * 
 * @author "Animesh Kumar <animesh@strumsoft.com>"
 * 
 */
public class VendingMachine {
    // LOG
    private static final Logger LOG = LoggerFactory.getLogger(VendingMachine.class);

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws InterruptedException the interrupted exception
     */
    public static void main(String[] args) throws InterruptedException {
        LOG.info("Starting VendingMachine...");

        // Injector
        final Injector injector = Jsr250.createInjector(Stage.PRODUCTION, new VendingMachineModule());

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (null != injector && injector instanceof Jsr250Injector) {
                    ((Jsr250Injector) injector).destroy();
                }
            }
        });

        // start CommandListener
        final CommandListener commandListener = injector.getInstance(CommandListener.class);
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                commandListener.init();
            }
        });
        th.start();
        th.join();
    }

}
