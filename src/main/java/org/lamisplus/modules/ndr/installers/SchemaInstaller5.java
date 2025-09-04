package org.lamisplus.modules.ndr.installers;
import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;
import org.springframework.core.annotation.Order;
@Order(7)
@Installer(name = "schema-installer5", description = "Update ndr regimen code", version = 3)
public class SchemaInstaller5 extends AcrossLiquibaseInstaller {
    public SchemaInstaller5() {
        super("classpath:installers/ndr/schema/schema5.xml");
    }
}
