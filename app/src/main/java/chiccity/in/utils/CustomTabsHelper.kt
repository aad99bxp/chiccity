package chiccity.`in`.utils

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import chiccity.`in`.R

/**
 * Utility to launch URLs in Chrome Custom Tabs for better performance and native feel.
 */
object CustomTabsHelper {
    fun openUrl(context: Context, url: String) {
        try {
            val builder = CustomTabsIntent.Builder()
            
            // Customize the appearance
            builder.setShowTitle(true)
            builder.setInstantAppsEnabled(true)
            
            // Set toolbar color to match app theme (if you have a primary color defined)
            // val color = ContextCompat.getColor(context, R.color.purple_500)
            // builder.setToolbarColor(color)

            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, Uri.parse(url))
        } catch (e: Exception) {
            // Fallback to external browser if Custom Tabs fails
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    }
}
