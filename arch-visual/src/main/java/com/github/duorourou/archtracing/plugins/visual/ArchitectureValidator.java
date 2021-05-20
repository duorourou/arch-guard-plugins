package com.github.duorourou.archtracing.plugins.visual;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;
import com.tngtech.archunit.library.Architectures;
import org.junit.runner.RunWith;

@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(packagesOf = {ArchitectureValidator.class})
public class ArchitectureValidator {

    @ArchTest
    Architectures.OnionArchitecture onionArchitecture_style_check = Architectures
            .onionArchitecture()
            .domainModels("com.thoughtworks.creditamountcenter.amountservice.domain.amountinfo.model..")
//        .domainServices("com.myapp.domain.service..")
            .applicationServices("com.thoughtworks.creditamountcenter.amountservice.application.amountinfo..")
            .adapter("infrastructure", "com.thoughtworks.creditamountcenter.amountservice.infrastructure..")
            .adapter("rest", "com.thoughtworks.creditamountcenter.amountservice.representation..");

}
