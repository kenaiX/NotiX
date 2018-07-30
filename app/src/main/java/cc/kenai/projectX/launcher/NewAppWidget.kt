package cc.kenai.projectX.launcher

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.support.v4.content.FileProvider
import android.widget.RemoteViews

import cc.kenai.projectX.R
import java.io.File
import android.support.v4.content.FileProvider.getUriForFile



/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            val widgetText = context.getString(R.string.appwidget_text)
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.new_app_widget)

            val file = File("/sdcard/test/111.jpg")

            val photoURI = FileProvider.getUriForFile(context, "cc.xxx", file)


            val uri = Uri.fromFile(file)

            views.setImageViewUri(R.id.appwidget_text, photoURI)




            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)


//            val intent = Intent()
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.setDataAndType(photoURI, "image/*");
//
//            intent.setDataAndType(photoURI, "image/*");
//            intent.putExtra("crop", "true");
//            intent.putExtra("outputX", 80);
//            intent.putExtra("outputY", 80);
//            intent.putExtra("return-data", false);
//            context.startActivity(intent)


        }
    }
}

