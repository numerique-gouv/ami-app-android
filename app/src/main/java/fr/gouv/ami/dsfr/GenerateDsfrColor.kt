package fr.gouv.ami.dsfr

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory


fun main() {
    val lightFile = File("colorsLight.xml")
    val darkFile = File("colorsDark.xml")

    val lightColors = parseColors(lightFile)
    val darkColors = parseColors(darkFile)

    val output = StringBuilder()
    output.appendLine("import androidx.compose.runtime.Composable")
    output.appendLine("import androidx.compose.foundation.isSystemInDarkTheme")
    output.appendLine("import androidx.compose.ui.graphics.Color\n")
    output.appendLine("object DsfrColors {")

    // Déclarer toutes les constantes Light et Dark
    for ((name, value) in lightColors) {
        val darkValue = darkColors[name] ?: value
        output.appendLine("    val ${name} = Color($value)")
        output.appendLine("    val ${name}Dark = Color($darkValue)")
        output.appendLine()
    }

    // Créer les fonctions @Composable
    for ((name, _) in lightColors) {
        val funcName = name.toCamelCase()
        output.appendLine("    @Composable")
        output.appendLine("    fun ${funcName}(): Color = if (isSystemInDarkTheme()) ${name}Dark else ${name}")
        output.appendLine()
    }

    output.appendLine("}")

    val outputDir = File("..")
    val outputFile = File(outputDir, "ui/theme/DsfrColors.kt")

    File(outputFile.absolutePath).writeText(output.toString())
    println("DsfrColors.kt generated successfully!")
}

fun parseColors(file: File): Map<String, String> {
    val colors = mutableMapOf<String, String>()
    val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
    val nodes = doc.getElementsByTagName("color")
    for (i in 0 until nodes.length) {
        val node = nodes.item(i)
        val name = node.attributes.getNamedItem("name").nodeValue
        val value = node.textContent.trim().replace("#", "0xFF")
        colors[name] = value
    }
    return colors
}

fun String.toCamelCase(): String {
    return split('_').mapIndexed { index, s ->
        if (index == 0) s.lowercase() else s.replaceFirstChar { it.uppercase() }
    }.joinToString("")
}
