package dssoft.com.notesapp.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import java.util.ArrayList;
import java.util.List;
import dssoft.com.notesapp.R;
import dssoft.com.notesapp.database.BDAdapter;
import dssoft.com.notesapp.pojo.Nota;

/**
 * Created by Angel on 12/04/2016.
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory
{

    private List<Nota> listaNotas = new ArrayList<Nota>();
    private Context context;

    public WidgetDataProvider(Context context, Intent intent)
    {
        this.context = context;
    }

    @Override
    public void onCreate()
    {
        initData();
    }

    @Override
    public void onDataSetChanged()
    {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return listaNotas.size();
    }

    @Override
    //En este metodo obtenemos los componentes del Layout de ListView
    //y se muestran en ellos los datos que hemos obtenido de la BD y hemos guardado en una Lista
    public RemoteViews getViewAt(int position)
    {

        RemoteViews rm = new RemoteViews(context.getPackageName(), R.layout.layout_lista_notas_widget);
        rm.setTextViewText(R.id.txtTituloWidget, listaNotas.get(position).getTitulo());
        rm.setTextViewText(R.id.txtContenidoWidget, listaNotas.get(position).getContenido());
        rm.setTextViewText(R.id.txtFechaWidget, listaNotas.get(position).getFecha());

        if(listaNotas.get(position).getImagenUri() != null)
        {
            rm.setImageViewUri(R.id.imgNotaListaWidget, Uri.parse(listaNotas.get(position).getImagenUri()));
            rm.setViewVisibility(R.id.imgNotaListaWidget, View.VISIBLE);
        }else
        {
            rm.setViewVisibility(R.id.imgNotaListaWidget, View.GONE);
        }

        //para que funcione la pulsacion sobre los elemetos de la lista hay que añadir el evento setOnClickFillInIntent
        final Intent fillInIntent = new Intent();
        fillInIntent.setAction("com.dssoft.notasApp.LANZAR_APLICACION");
        //final Bundle bundle = new Bundle();
        //bundle.putInt("posicion_pulsada", position);//se añade como pararametro del intent la posicion pulsada (podriamos haber incluido cualquier otra cosa)
        //fillInIntent.putExtras(bundle);
        rm.setOnClickFillInIntent(R.id.layoutNotaWidget, fillInIntent);

        return rm;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    //Se obtienen las notas de la base de Dato y se guardan en una List
    private void initData()
    {
        listaNotas.clear();

        BDAdapter bdAdapter = new BDAdapter(context);

        bdAdapter.abrirBD_Lectura();

        Cursor cursor = bdAdapter.obtener_Notas();

        while(cursor.moveToNext())
        {
            Nota nota = new Nota();
            nota.setIdNota(cursor.getInt(0));
            nota.setTitulo(cursor.getString(1));
            nota.setContenido(cursor.getString(2));
            nota.setFecha(cursor.getString(3));
            nota.setImagenUri(cursor.getString(4));

            listaNotas.add(nota);
        }

        cursor.close();
        bdAdapter.cerrarBD();

    }

}
