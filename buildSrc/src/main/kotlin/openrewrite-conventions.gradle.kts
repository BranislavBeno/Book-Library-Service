import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("org.openrewrite.rewrite")
}

val libs = the<LibrariesForLibs>()

dependencies {
    rewrite(platform(libs.rewrite.recipe.bom))
    rewrite(libs.rewrite.migrate.java)
    rewrite(libs.rewrite.spring)
}

rewrite {
    activeRecipe("org.openrewrite.java.RemoveUnusedImports")
    activeRecipe("org.openrewrite.java.OrderImports")
    activeRecipe("org.openrewrite.java.migrate.UpgradeToJava21")
    activeRecipe("org.openrewrite.java.spring.boot3.SpringBoot3BestPractices")
    activeRecipe("org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_3")
}
