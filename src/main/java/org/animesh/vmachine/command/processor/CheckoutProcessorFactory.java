package org.animesh.vmachine.command.processor;

// Helper factory
public interface CheckoutProcessorFactory {

    CheckoutProcessor create(String productCode);
}
