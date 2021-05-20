package @@{{rootPackageName}}@@;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;
import com.tngtech.archunit.library.Architectures;
import org.junit.runner.RunWith;

@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(packagesOf = {OnionArchitectureValidator.class})
public class OnionArchitectureValidator {

    @ArchTest
    Architectures.OnionArchitecture onionArchitecture_style_check = Architectures
            .onionArchitecture()
            .adapter("domain", "@@{{rootPackageName}}@@.domain")
            .domainModels("@@{{rootPackageName}}@@.domain.*.model..")
            .domainServices()
            .adapter("application", "@@{{rootPackageName}}@@.application")
            .applicationServices("@@{{rootPackageName}}@@.application.amountinfo..")
            .adapter("infrastructure", "@@{{rootPackageName}}@@.infrastructure..")
            .adapter("rest", "@@{{rootPackageName}}@@.representation..");

}
