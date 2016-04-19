package dssoft.com.notesapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;
import dssoft.com.notesapp.PantallaPrincipal;
import dssoft.com.notesapp.R;

/**
 * Created by Angel on 11/04/2016.
 */

/*En esta clase deberemos implementar los mensajes a los que vamos a responder desde nuestro widget, entre los que destacan:

 - onEnabled(): lanzado cuando se añade al escritorio la primera instancia de un widget.

 - onUpdate(): lanzado periodicamente cada vez que se debe actualizar un widget.

 - onDelete(): lanzado cuando se elimina del escritorio una instancia de un widget

 - onDisable(): lanzado cuando se elimina del escritorio la ultima instancia de un widget.*/

 //para Implemetar un listview en un widget es un coñazo, hay que crea una clase que implemente la
//interfaz RemoteViewsFactory esta clase seria como el Adapter de los ListView normales y en ella
//obtenemos los datos y los mostramos en los componentes correspondiente del layout asociado al ListView,
//y tambien es necesario crear una Clase que herede, en esta clase simplemente se crea un objetos de la clase que implementa la interfaz RemoteViewsFactory
//por ultimo Se registra el componente ListView contenido en el widget y se le asigna el Adaptador de datos a traves de la clase WidgetService
public class WidgetNotas extends AppWidgetProvider
{


    @Override
    public void onEnabled(Context context)
    {
        super.onEnabled(context);
    }

    @Override
    //This is called to update the App Widget at intervals defined by the updatePeriodMillis attribute
    // in the AppWidgetProviderInfo (see Adding the AppWidgetProviderInfo Metadata above).
    // This method is also called when the user adds the App Widget, so it should perform
    // the essential setup, such as define event handlers for Views and start a temporary Service
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for(int i=0; i<appWidgetIds.length;i++)
        {
            int widgetId = appWidgetIds[i];
            ActualizarWidget(context, appWidgetManager, widgetId);
        }

    }

    @Override
    //En este metodo es donde se actua en caso de recibir nuestro intent personalizado de actualizacion
    // (cuando el usuario pulsa sobre la imagen de actualizar)
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);

        if(intent.getAction().equals("com.dssoft.notasApp.ACTUACLIZAR_WIDGET"))
        {

            ////Obtenemos el ID del widget a actualizar
            int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            //Obtenemos el widget manager de nuestro contexto
            AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);

            //Actualizamos el widget
            if(widgetId != AppWidgetManager.INVALID_APPWIDGET_ID)
            {
                //Log.e("Actualziar","boton");
                widgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.listNotasWidget);//Con este orden se llama al metodo onDataSetChanged() que esta en la clase WidgetDataProvider
                ActualizarWidget(context,widgetManager, widgetId);

                //Se actualiza el contenido del componete ListView del widget
                //RemoteViews controles = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
                //ComponentName component=new ComponentName(context,WidgetNotas.class);
                //widgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.listNotasWidget);//Con este orden se llama al metodo onDataSetChanged() que esta en la clase WidgetDataProvider
                //widgetManager.updateAppWidget(component, controles);
                //ActualizarWidget(context,widgetManager, widgetId);
            }

        }else
        {

            if(intent.getAction().equals("com.dssoft.notasApp.LANZAR_APLICACION"))
            {
                //Se lanza la aplicacion Notas
                Intent intentAplicacion = new Intent(context, PantallaPrincipal.class);
                intentAplicacion.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//Esto es para poder abrir la App desde el widget (si no se pone peta)
                context.startActivity(intentAplicacion);
            }

        }

    }


    private void  ActualizarWidget(Context context, AppWidgetManager appWidgetManager, int widgetId)
    {

        //Los componentes de un widget se basan en un tipo especial de vistas que llamamos Remote Views
        //Para acceder a la lista de estos componentes que constituyen la interfaz del widget construiremos
        //un nuevo objeto RemoteViews
        RemoteViews controles = new RemoteViews(context.getPackageName(), R.layout.layout_widget);

        //Se registra el componente ListView contenido en el widget
        //y se le asigna el Adaptador de datos a traves de la clase WidgetService dentro de la cual
        //se crea un objeto de la clase WidgetDataProvider la cual contiene los datos a mostrar en el ListView
        Intent intent = new Intent(context, WidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        controles.setRemoteAdapter(widgetId, R.id.listNotasWidget, intent);

        //Se registran los onClickListener
        //Si se pulsa la imagen de la linterna se lanza el Intent Presonalizado creado
        //que llama al metodo OnReceive donde se enciende/apaga la linterna
        Intent intentActualizar = new Intent(context, WidgetNotas.class);
        intentActualizar.setAction("com.dssoft.notasApp.ACTUACLIZAR_WIDGET");
        intentActualizar.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, widgetId, intentActualizar, PendingIntent.FLAG_UPDATE_CURRENT);
        controles.setOnClickPendingIntent(R.id.imgActualizarWidget,pendingIntent);

        //Si se pulsa en el Linearlayout principal se lanza la aplicacion
        //Intent intent2 = new Intent(context, PantallaPrincipal.class);
        //PendingIntent pendingInten2 = PendingIntent.getActivity(context, widgetId, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
        //controles.setOnClickPendingIntent(R.id.FrmWidget, pendingInten2);

        //Se registra el manejador para cada elemeto de la lista
        //Este funciona de forma que cuando pulsemos sobre unos de los elemetos de la lista se llama al metodo onReceive del broadcast
        //dentro de ese metodo lo que hacemos es lanzar la aplicacion
        Intent intentAplicacion = new Intent(context, WidgetNotas.class);
        intentAplicacion.setAction("com.dssoft.notasApp.LANZAR_APLICACION");
        intentAplicacion.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        intentAplicacion.setData(Uri.parse(intentAplicacion.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingIntentAplicacion = PendingIntent.getBroadcast(context, 0,
                intentAplicacion, PendingIntent.FLAG_UPDATE_CURRENT);
        controles.setPendingIntentTemplate(R.id.listNotasWidget, pendingIntentAplicacion);

        //Esto es importante y necesario ya que de no hacerlo la actualiacion no se reflejara correctamente en la interfaz del widget
        appWidgetManager.updateAppWidget(widgetId, controles);

    }

}
