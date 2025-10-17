package org.lamisplus.modules.ndr.installers;
import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;
import org.springframework.core.annotation.Order;
@Order(8)
@Installer(name = "schema-installer6", description = "Add ndr missing regimen", version = 2)
public class SchemaInstaller6 extends AcrossLiquibaseInstaller {
    public SchemaInstaller6() {
        super("classpath:installers/ndr/schema/schema6.xml");
    }
}
