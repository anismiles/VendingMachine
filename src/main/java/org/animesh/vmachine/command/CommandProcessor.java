package org.animesh.vmachine.command;

import org.animesh.vmachine.command.exp.CommandProcessException;

/**
 * The Interface CommandProcessor.
 * 
 * @author "Animesh Kumar <animesh@strumsoft.com>"
 */
public interface CommandProcessor {

    /**
     * Process command
     * 
     * @param command
     *            the command
     * @throws CommandProcessException
     *             the command process exception
     */
    public void process(String command) throws CommandProcessException;

    /**
     * Display headline.
     */
    public void displayHeadline();

    /**
     * Display options.
     */
    public void displayOptions();

    /**
     * Name.
     * 
     * @return the string
     */
    public String name();
}
