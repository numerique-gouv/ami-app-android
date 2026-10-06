package fr.gouv.ami.components.webviewClient

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class AppUrlRewriterParameterizedTest(
    private val input: Pair<String, String?>
) {
    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun parameters(): List<Pair<String, String?>> = listOf(
            "https://google.com" to null,
            "https://service-public.gouv.fr.somethingelse.com" to null,
            "https://service-public.gouv.fr" to "https://service-public.gouv.fr?view=mobile",
            "https://www.service-public.gouv.fr" to "https://www.service-public.gouv.fr?view=mobile",
            "https://www.service-public.gouv.fr?app=ami" to "https://www.service-public.gouv.fr?app=ami&view=mobile",
            "https://www.service-public.gouv.fr/" to "https://www.service-public.gouv.fr/?view=mobile",
            "https://www.service-public.gouv.fr/?app=ami" to "https://www.service-public.gouv.fr/?app=ami&view=mobile",
        )
    }

    @Test
    fun rewrite() {
        val expected = if (input.second != null) Uri.parse(input.second) else null
        assertEquals(expected, WebviewUrlRewriter.rewrite(Uri.parse(input.first)))
    }
}