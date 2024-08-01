package org.lamisplus.modules.ndr.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;
import org.springframework.core.annotation.Order;

@Order(2)
@Installer(name = "code-set-update ", description = "Installs the required database tables", version = 5)
public class CodeSetUpdate extends AcrossLiquibaseInstaller {
	public CodeSetUpdate() {
		super("classpath:installers/ndr/schema/code_set_update.xml");
	}
}

