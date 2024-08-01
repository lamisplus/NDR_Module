package org.lamisplus.modules.ndr.installers;
import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;
import org.springframework.core.annotation.Order;
@Order(2)
@Installer(name = "schema-installer3", description = "Installs the required properties for auto push", version = 12)
public class SchemaInstaller3 extends AcrossLiquibaseInstaller {
    public SchemaInstaller3() {
        super("classpath:installers/ndr/schema/schema3.xml");
    }
}

