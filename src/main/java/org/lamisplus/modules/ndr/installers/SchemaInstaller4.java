package org.lamisplus.modules.ndr.installers;
import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;
import org.springframework.core.annotation.Order;
@Order(6)
@Installer(name = "schema-installer4", description = "Create column for saving baseURl in the configuration table", version = 12)
public class SchemaInstaller4 extends AcrossLiquibaseInstaller {
    public SchemaInstaller4() {
        super("classpath:installers/ndr/schema/schema4.xml");
    }
}

