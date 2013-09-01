package org.animesh.vmachine.command.processor;

public interface CheckoutProcessorFactory {

    CheckoutProcessor create(String productCode);
}
