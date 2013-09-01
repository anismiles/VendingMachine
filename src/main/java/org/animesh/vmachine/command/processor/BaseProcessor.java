package org.animesh.vmachine.command.processor;

import org.animesh.vmachine.VendingMachineContext;
import org.animesh.vmachine.command.CommandProcessor;
import org.animesh.vmachine.command.exp.BreakProcessingException;
import org.animesh.vmachine.command.exp.CommandProcessException;
import org.animesh.vmachine.command.exp.QuitProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseProcessor implements CommandProcessor {
    // LOG
    private static final Logger LOG = LoggerFactory.getLogger(BaseProcessor.class);

    final static String CMD_QUIT = "QT";
    final static String CMD_HOME = "HM";

    final VendingMachineContext context;

    public BaseProcessor(VendingMachineContext context) {
        this.context = context;
    }

    @Override
    public void process(String command) throws CommandProcessException {
        LOG.debug("command = {}", command);

        // Quit?
        if (command.equalsIgnoreCase(CMD_QUIT)) {
            context.printDebug("Quit");
            throw new QuitProcessingException();
        }

        // Home?
        if (command.equalsIgnoreCase(CMD_HOME)) {
            context.printDebug("Home");
            throw new BreakProcessingException();
        }
    }

    @Override
    public void displayHeadline() {
        displayHeadline("Please select an option");
    }

    public void displayHeadline(String message) {
        context.printInfo("\n### " + message + " (" + name() + ")");
    }

    @Override
    public void displayOptions() {
        context.printOptions(CMD_QUIT, "Quit");
        context.printOptions(CMD_HOME, "Home");
    }

    public VendingMachineContext getContext() {
        return context;
    }

}
