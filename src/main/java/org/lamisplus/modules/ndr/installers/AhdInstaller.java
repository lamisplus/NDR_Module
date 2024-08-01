package org.lamisplus.modules.ndr.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;
import org.springframework.core.annotation.Order;

@Order(2)
@Installer(name = "ahd-test-code", description = "Add AHD test codes", version = 1)
public class AhdInstaller extends AcrossLiquibaseInstaller {
    public AhdInstaller() {
        super("classpath:installers/ndr/schema/ahd.xml");
    }
}

