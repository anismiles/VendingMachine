package org.animesh.vmachine.command.exp;

public class UnknownCommandException extends CommandProcessException {

    private static final long serialVersionUID = -5997860154593746556L;

    public UnknownCommandException(String message) {
        super(message);
    }
}
