package org.animesh.vmachine.command.processor;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.animesh.vmachine.VendingMachineContext;
import org.animesh.vmachine.command.CommandProcessor;
import org.animesh.vmachine.command.exp.CommandProcessException;
import org.animesh.vmachine.command.exp.UnknownCommandException;

@Singleton
public class HomeProcessor extends BaseProcessor {

    final CommandProcessor configureProcessor;
    final CommandProcessor vendProcessor;

    @Inject
    public HomeProcessor(VendingMachineContext context,
            @Named("configure") CommandProcessor configureProcessor,
            @Named("vend") CommandProcessor vendProcessor) {
        super(context);
        this.configureProcessor = configureProcessor;
        this.vendProcessor = vendProcessor;
    }

    @Override
    public void process(String command) throws CommandProcessException {
        super.process(command);

        // Configure?
        if (command.equalsIgnoreCase("CNF")) {
            context.setCommandProcessor(configureProcessor);
            return;
        }

        // Vend?
        if (command.equalsIgnoreCase("VND")) {
            context.setCommandProcessor(vendProcessor);
            return;
        }

        throw new UnknownCommandException(command);
    }

    @Override
    public void displayOptions() {
        context.printOptions("CNF", "Configure");
        context.printOptions("VND", "Vend");
        super.displayOptions();
    }

    @Override
    public String name() {
        return "Home";
    }
}
