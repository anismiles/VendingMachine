package org.animesh.vmachine;

import org.animesh.vmachine.command.CommandListener;
import org.animesh.vmachine.command.CommandProcessor;
import org.animesh.vmachine.command.processor.AddProductProcessor;
import org.animesh.vmachine.command.processor.AddStockProcessor;
import org.animesh.vmachine.command.processor.CashBoxProcessor;
import org.animesh.vmachine.command.processor.CashDepositProcessor;
import org.animesh.vmachine.command.processor.CheckoutProcessorFactory;
import org.animesh.vmachine.command.processor.CheckoutProductSelectProcessor;
import org.animesh.vmachine.command.processor.ConfigureProcessor;
import org.animesh.vmachine.command.processor.HomeProcessor;
import org.animesh.vmachine.command.processor.InventoryProcessor;
import org.animesh.vmachine.command.processor.VendingProcessor;
import org.animesh.vmachine.inventory.InMemoryInventoryImpl;
import org.animesh.vmachine.inventory.Inventory;
import org.animesh.vmachine.money.Cashbox;
import org.animesh.vmachine.money.InMemoryCashboxImpl;
import org.animesh.vmachine.secure.AdminAccess;
import org.animesh.vmachine.secure.AdminAccessIntercepter;
import org.animesh.vmachine.utils.PropertyLoader;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

/**
 * Guice Module
 * 
 * @author "Animesh Kumar <animesh@strumsoft.com>"
 *
 */
public class VendingMachineModule extends AbstractModule {
    // LOG
    // private static final Logger LOG = LoggerFactory.getLogger(VendingMachineModule.class);

    @Override
    protected void configure() {
        PropertyLoader.loadProperties(binder(), "app.properties");

        // Context
        bind(VendingMachineContext.class).to(VendingMachineContextImpl.class).asEagerSingleton();

        // Inventory
        bind(Inventory.class).to(InMemoryInventoryImpl.class).asEagerSingleton();
        // Cashbox
        bind(Cashbox.class).to(InMemoryCashboxImpl.class).asEagerSingleton();

        // Commands
        bind(CommandListener.class).asEagerSingleton();
        bind(CommandProcessor.class).annotatedWith(Names.named("home")).to(HomeProcessor.class);
        bind(CommandProcessor.class).annotatedWith(Names.named("configure")).to(ConfigureProcessor.class);
        bind(CommandProcessor.class).annotatedWith(Names.named("cashbox")).to(CashBoxProcessor.class);
        bind(CommandProcessor.class).annotatedWith(Names.named("cashDeposit")).to(CashDepositProcessor.class);

        bind(CommandProcessor.class).annotatedWith(Names.named("inventory")).to(InventoryProcessor.class);
        bind(CommandProcessor.class).annotatedWith(Names.named("addProduct")).to(AddProductProcessor.class);
        bind(CommandProcessor.class).annotatedWith(Names.named("addStock")).to(AddStockProcessor.class);

        bind(CommandProcessor.class).annotatedWith(Names.named("vend")).to(VendingProcessor.class);
        bind(CommandProcessor.class).annotatedWith(Names.named("checkoutSelect")).to(
                CheckoutProductSelectProcessor.class);

        // CheckoutProcessor Assisted Factory
        install(new FactoryModuleBuilder().build(CheckoutProcessorFactory.class));

        // Security
        AdminAccessIntercepter securityIntercepter = new AdminAccessIntercepter();
        requestInjection(securityIntercepter);
        bindInterceptor(Matchers.annotatedWith(AdminAccess.class), // annotated with @AdminAccess
                Matchers.any(), // Any method
                securityIntercepter);
    }

    public String getName() {
        return getClass().getName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VendingMachineModule other = (VendingMachineModule) obj;
        if (getName() == null) {
            if (other.getName() != null)
                return false;
        } else if (!getName().equals(other.getName()))
            return false;
        return true;
    }

}
