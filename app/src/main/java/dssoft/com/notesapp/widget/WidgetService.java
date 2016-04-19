package dssoft.com.notesapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Angel on 12/04/2016.
 */
public class WidgetService extends RemoteViewsService
{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent)
    {
        WidgetDataProvider widgetDataProvider = new WidgetDataProvider(getApplicationContext(), intent);

        return widgetDataProvider;
    }

}
