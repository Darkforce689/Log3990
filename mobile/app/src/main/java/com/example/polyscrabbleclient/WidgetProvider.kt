package com.example.polyscrabbleclient

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.example.polyscrabbleclient.user.User
import com.example.polyscrabbleclient.user.User.currentLevel


class WidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.example_loading_appwidget)
        views.setTextViewText(R.id.name, User.name)
        views.setTextViewText(R.id.level, currentLevel().toString())
        views.setTextViewText(R.id.game_played_value, User.nGamePlayed.toInt().toString())
        views.setTextViewText(R.id.game_winned_value, User.nGameWon.toInt().toString())
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
