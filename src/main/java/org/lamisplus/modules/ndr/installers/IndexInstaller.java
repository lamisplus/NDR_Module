package org.lamisplus.modules.ndr.installers;


import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;
import org.springframework.core.annotation.Order;

@Order(2)
@Installer(name = "index-installer", description = "Installs the required indexes on  database tables", version =2 )
public class IndexInstaller extends AcrossLiquibaseInstaller {

    public IndexInstaller() {
        super("classpath:installers/ndr/schema/index-creation.xml");
        System.out.println("running... Index installer");
    }

}
