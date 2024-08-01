package org.lamisplus.modules.ndr.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;
import org.springframework.core.annotation.Order;

@Order(2)
@Installer(name = "run-once-ndr-regimen-resolver-installer",
		description = "clean up NDR code set and regimens", version = 2)
public class RunOnceNdrRegimenResolverInstaller extends AcrossLiquibaseInstaller {
	public RunOnceNdrRegimenResolverInstaller() {
		super("classpath:installers/ndr/schema/regimen-resolver.xml");
	}
}
