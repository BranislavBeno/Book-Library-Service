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
    activeRecipe("com.book.library.NoStaticImport")
    activeRecipe("org.openrewrite.java.RemoveUnusedImports")
    activeRecipe("org.openrewrite.java.OrderImports")
    activeRecipe("org.openrewrite.java.migrate.UpgradeToJava25")
    activeRecipe("org.openrewrite.java.spring.boot4.SpringBootProperties_4_0")
    activeRecipe("org.openrewrite.java.spring.boot4.UpgradeSpringBoot_4_0")
}
