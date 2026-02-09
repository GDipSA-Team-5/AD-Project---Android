// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.sonarqube)
}

sonarqube {
    properties {
        property("sonar.projectKey", "GDipSA-Team-5_AD-Project---Android")
        property("sonar.organization", "gdipsa-team-5")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.sources", "app/src/main/java")
        property("sonar.tests", "app/src/test/java,app/src/androidTest/java")
        property("sonar.java.binaries", "app/build/tmp/kotlin-classes/debug,app/build/intermediates/javac/debug/classes")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml"
        )
        property("sonar.androidLint.reportPaths", "app/build/reports/lint-results-debug.xml")
    }
}

tasks.named("sonarqube") {
    dependsOn(":app:jacocoTestReport", ":app:lintDebug", ":app:testDebugUnitTest")
}
