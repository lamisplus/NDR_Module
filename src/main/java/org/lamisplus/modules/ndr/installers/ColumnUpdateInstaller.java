package org.lamisplus.modules.ndr.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;
import org.springframework.core.annotation.Order;

@Order(2)
@Installer(name = "column-update_installer",
        description = "add new column to NDR related tables", version = 2)
public class ColumnUpdateInstaller extends AcrossLiquibaseInstaller {
    public ColumnUpdateInstaller() {
        super("classpath:installers/ndr/schema/colum_update.xml");
    }
}
