package org.animesh.vmachine.command;

import java.util.Scanner;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.animesh.vmachine.VendingMachineContext;
import org.animesh.vmachine.command.exp.BreakProcessingException;
import org.animesh.vmachine.command.exp.CommandProcessException;
import org.animesh.vmachine.command.exp.NotAuthorizedException;
import org.animesh.vmachine.command.exp.QuitProcessingException;
import org.animesh.vmachine.command.exp.UnknownCommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class CommandListener {
    // LOG
    private static final Logger LOG = LoggerFactory.getLogger(CommandListener.class);

    final VendingMachineContext context;

    @Inject
    public CommandListener(VendingMachineContext context) {
        this.context = context;
    }

    public void init() {
        Scanner console = new Scanner(context.getInputStream());
        String command = null;
        while (true) {
            try {
                context.getCommandProcessor().displayHeadline();
                context.getCommandProcessor().displayOptions();
                context.printInfoWithoutNewLine("$ ");
                command = console.nextLine(); // wait for user input

                LOG.debug("processor={}, command = {}", context.getCommandProcessor().getClass()
                        .getSimpleName(), command);

                // process
                context.getCommandProcessor().process(command);
            }
            // NotAuthorized?
            catch (NotAuthorizedException qp) {
                LOG.error("Failed to authorize!");
                context.printError("Authorization failed!");
                context.reset(); // reset
            }
            // Quit?
            catch (QuitProcessingException qp) {
                LOG.debug("Quit processing");
                break;
            }
            // Break?
            catch (BreakProcessingException bp) {
                LOG.debug("Break processing");
                context.reset();
            }
            // Unknown command?
            catch (UnknownCommandException uc) {
                LOG.error("Unknown command = {}", uc.getMessage());
                context.printError("Unknown command, please try again!");
            }
            // Error?
            catch (CommandProcessException e) {
                LOG.error("Error in processing command = {}, by processor = {}, ==>> error: {}",
                        new Object[] { command, context.getCommandProcessor().getClass().getName(), e });
                context.printError("Apologies! There was some error in processing, please try again.");
            }
            // Generic
            catch (Exception e) {
                LOG.error("Error in processing command = {}, by processor = {}, ==>> error: {}",
                        new Object[] { command, context.getCommandProcessor().getClass().getName(), e });
                context.printError("Apologies! There was an error in processing. Please try again.");
            }
        }
    }
}
