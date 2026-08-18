import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import java.util.Properties

abstract class GenerateNetworkSecurityConfigTask : DefaultTask() {

    @get:InputFile
    @get:Optional
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val envFile: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun generate() {
        val file = envFile.orNull?.asFile
            ?: throw GradleException("No .env file configured for this variant")

        val props = Properties().apply { file.inputStream().use(::load) }

        val baseHost = props.getProperty("BASE_HOST_STRING")?.removeSurrounding("\"")?.trim()
        if (baseHost.isNullOrEmpty()) {
            throw GradleException("BASE_HOST_STRING missing or empty in ${file.path}")
        }

        val xml = buildString {
            appendLine("""<?xml version="1.0" encoding="utf-8"?>
<network-security-config>""")

            appendLine(
                """
    <!-- Trust user-added certificates for debug builds -->
    <debug-overrides>
        <trust-anchors>
            <!-- Trust preinstalled CAs -->
            <certificates src="system" />
            <!-- Additionally trust user added CAs (like Charles Proxy, self-signed certs) -->
            <certificates src="user" />
        </trust-anchors>
    </debug-overrides>""")

            val baseHostRequiresNetworkConfig =
                props.getProperty("BASE_HOST_REQUIRES_NETWORK_CONFIG")?.removeSurrounding("\"")?.trim().toBoolean()
            if (baseHostRequiresNetworkConfig == true) {
                appendLine("""
    <!-- For specific staging domain -->
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">${baseHost.escapeXml()}</domain>
        <trust-anchors>
            <certificates src="system" />
            <certificates src="user" />
        </trust-anchors>
    </domain-config>""")
            }

            val sectigoHost =
                props.getProperty("NEED_SECTIGO_HOST_STRING")?.removeSurrounding("\"")?.trim()
            if (!sectigoHost.isNullOrEmpty()) {
                appendLine("""
    <!-- ${sectigoHost}: uses Sectigo Public Server Authentication Root R46 (March 2021),
         not present in Android 10 trust store on unpatched devices -->
    <domain-config cleartextTrafficPermitted="false">
        <domain includeSubdomains="true">${sectigoHost.escapeXml()}</domain>
        <trust-anchors>
            <certificates src="system" />
            <certificates src="@raw/sectigo_root_r46" />
        </trust-anchors>
    </domain-config>""")
            }

            var sandboxHosts = mutableListOf<String>()
            for (i in 1..9) {
                val domain = props.getProperty(
                    "SANDBOX_HOST_STRING_${
                        i.toString().padStart(2, '0')
                    }"
                )?.removeSurrounding("\"")?.trim()
                if (!domain.isNullOrEmpty()) {
                    sandboxHosts.add(domain)
                }
            }

            if (sandboxHosts.isNotEmpty()) {
                appendLine("""
    <!-- FranceConnect sandbox domains -->
    <domain-config cleartextTrafficPermitted="false">""")

                for (host in sandboxHosts) {
                    appendLine("""        <domain includeSubdomains="true">${host.escapeXml()}</domain>""")
                }

                appendLine(
        """        <trust-anchors>
            <certificates src="system" />
            <certificates src="user" />
        </trust-anchors>
    </domain-config>""")
            }

            appendLine("""
</network-security-config>""")
        }

        val out = outputDir.get().asFile.resolve("xml/network_security_config.xml")
        out.parentFile.mkdirs()
        out.writeText(xml)
    }

    private fun String.escapeXml() = this
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
}