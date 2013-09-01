package org.animesh.vmachine;

import java.io.InputStream;

import org.animesh.vmachine.command.CommandProcessor;

public interface VendingMachineContext {

    /**
     * Prints the error.
     *
     * @param error the error
     */
    void printError(String... error);

    /**
     * Prints the debug.
     *
     * @param debug the debug
     */
    void printDebug(String... debug);

    /**
     * Prints the options.
     *
     * @param command the command
     * @param details the details
     */
    void printOptions(Object command, Object details);

    /**
     * Prints the info.
     *
     * @param info the info
     */
    void printInfo(String... info);

    /**
     * Prints the info without new line.
     *
     * @param info the info
     */
    void printInfoWithoutNewLine(String... info);

    /**
     * Gets the input stream.
     *
     * @return the input stream
     */
    InputStream getInputStream();

    /**
     * Verify admin password.
     *
     * @param pass the pass
     * @return true, if successful
     */
    boolean verifyAdminPassword(String pass);

    /**
     * Sets the admin.
     *
     * @param isAdmin the new admin
     */
    void setAdmin(boolean isAdmin);

    /**
     * Checks if is admin.
     *
     * @return true, if is admin
     */
    boolean isAdmin();

    /**
     * Sets the command processor.
     *
     * @param commandProcessor the new command processor
     */
    void setCommandProcessor(CommandProcessor commandProcessor);

    /**
     * Gets the command processor.
     *
     * @return the command processor
     */
    CommandProcessor getCommandProcessor();

    /**
     * Reset.
     */
    void reset();

}
