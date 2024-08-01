package org.lamisplus.modules.ndr.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;
import org.springframework.core.annotation.Order;
@Order(2)
@Installer(name = "schema-installer2", description = "Installs the required database tables for auto push", version = 12)
public class SchemaInstaller2 extends AcrossLiquibaseInstaller {
    public SchemaInstaller2() {
        super("classpath:installers/ndr/schema/schema2.xml");
    }
}

