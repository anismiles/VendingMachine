package org.animesh.vmachine;

import java.io.InputStream;
import java.io.PrintStream;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.animesh.vmachine.command.CommandProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

@Singleton
public class VendingMachineContextImpl implements VendingMachineContext {
    // LOG
    private static final Logger LOG = LoggerFactory.getLogger(VendingMachineContextImpl.class);

    @Inject
    @Named("home")
    CommandProcessor homeProcessor;

    final PrintStream out = System.out;
    final PrintStream err = System.err;
    final InputStream in = System.in;
    final String adminPassword;

    CommandProcessor currentCommandProcessor;
    boolean isAdmin = false;

    @Inject
    public VendingMachineContextImpl(@Named("admin.password") String adminPassword) {
        this.adminPassword = adminPassword;
    }

    @PostConstruct
    public void init() {
        this.currentCommandProcessor = homeProcessor;
        this.isAdmin = false;
    }

    @Override
    public void reset() {
        setCommandProcessor(homeProcessor);
        setAdmin(false);
    }

    @Override
    public CommandProcessor getCommandProcessor() {
        return currentCommandProcessor;
    }

    @Override
    public void setCommandProcessor(CommandProcessor commandProcessor) {
        this.currentCommandProcessor = commandProcessor; // assign new
    }

    @Override
    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Override
    public boolean verifyAdminPassword(String pass) {
        return this.adminPassword.equals(pass);
    }

    @Override
    public InputStream getInputStream() {
        return in;
    }

    @Override
    public void printInfo(String... info) {
        out.println(Joiner.on(" ").join(info));
        LOG.debug("{}", Joiner.on(" ").join(info));
    }

    @Override
    public void printInfoWithoutNewLine(String... info) {
        out.print(Joiner.on(" ").join(info));
        LOG.debug("{}", Joiner.on(" ").join(info));
    }

    @Override
    public void printOptions(Object command, Object details) {
        out.println(String.format("[%s]\t%s", command, details));
        LOG.debug("{}", String.format("[%s]\t%s", command, details));
    }

    @Override
    public void printDebug(String... debug) {
        out.println("> " + Joiner.on(" ").join(debug));
        LOG.debug("{}", "> " + Joiner.on(" ").join(debug));
    }

    @Override
    public void printError(String... error) {
        err.println("* [ERROR] " + Joiner.on(" ").join(error));
        LOG.debug("{}", "* [ERROR] " + Joiner.on(" ").join(error));
    }

}
