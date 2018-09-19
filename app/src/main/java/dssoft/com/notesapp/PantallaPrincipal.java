package dssoft.com.notesapp;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.analytics.Tracker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import dssoft.com.notesapp.adapters.AdaptadorListaNotas;
import dssoft.com.notesapp.database.BDAdapter;
import dssoft.com.notesapp.pojo.Nota;
import dssoft.com.notesapp.widget.WidgetNotas;

public class PantallaPrincipal extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener
{

    @Bind(R.id.toolbarMain) Toolbar toolbar;
    @Bind(R.id.titToolbarMain) TextView titToolbar;
    @Bind(R.id.barraBusqueda) SearchView barraBusqueda;
    @Bind(R.id.listViewNotas) ListView listViewNotas;
    @Bind(R.id.btnFlotante)  FloatingActionButton btnFlotante;
    @Bind(R.id.parentLayout_main) CoordinatorLayout parentLayout;
    @BindColor(R.color.verde_claro) int colorVerde;
    @BindColor(R.color.naranja_oscuro) int colorNaranja;
    @BindDrawable(R.mipmap.ic_add) Drawable imgAdd;
    @BindDrawable(R.mipmap.ic_trash) Drawable imgBasura;

    private  boolean deshacerCambios=false;
    private final int CODE_NOTE_ADD = 100;
    private final int CODE_NOTE_MOD = 200;
    private List<Nota> listaNotas = new ArrayList<Nota>();
    private List<Nota> listaNotasBusquedas = new ArrayList<Nota>();
    private Map<Integer, Integer> mapItemSelecc = new HashMap<Integer,Integer>();
    private AdaptadorListaNotas aln;

    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_principal);
        MultiDex.install(this);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        barraBusqueda.setOnQueryTextListener(this);
        barraBusqueda.setOnCloseListener(this);

        obtener_Notas_BD();

        aln = new AdaptadorListaNotas(this, listaNotas, mapItemSelecc);
        listViewNotas.setAdapter(aln);

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        //Se ejecuta cuando pulsamos sobre el icono de buscar de la Barra de Busqueda
        barraBusqueda.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Ocultamos el TextView que muestra el titulo de la Toolbar
                titToolbar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    //Se ejecuta cuando se cierra la barra buscador
    public boolean onClose()
    {
        //Al cerrar la barra de busqueda volvemos a mostrar el titulo de la ToolBar
        titToolbar.setVisibility(View.VISIBLE);

        //Se obtienen de nuevo todas la Notas de la BD y se muestran en el ListView
        obtener_Notas_BD();
        aln.notifyDataSetChanged();

        return false;
    }

    @Override
    //Se ejecuta cuando se pulsa el boton buscar del buscador
    public boolean onQueryTextSubmit(String query)
    {
        //buscar_palabra(query);
        return false;
    }

    @Override
    //Se ejecuta cuando se produce un cambi en el texto escrito en el buscador
    public boolean onQueryTextChange(String newText)
    {

        //Si la longitud del texto es 0 significa que se ha borrado la palabra escrita en el buscador
        //pulsando la tecla X del mismo.
        if(newText.length()==0)
        {
            //Se obtienen de nuevo todas la Notas de la BD y se muestran en el ListView
            obtener_Notas_BD();
            aln.notifyDataSetChanged();

        }else
        {
            //Segun se van intruciendo letras en la barra buscador se realiza la busqueda de la palabra actual
            //En el titulo y contenido de todas las notas almacenadas
            buscar_palabra(newText);
        }

        return false;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CODE_NOTE_ADD || requestCode == CODE_NOTE_MOD)
        {

            if(resultCode == Activity.RESULT_OK)
            {
                //Se obtienen los datos de la BD y se actualiza la lista
                obtener_Notas_BD();
                aln.notifyDataSetChanged();//Se actualiza el ListAdapter para que se muestren los cambios en el ListView

                //Tambien actualizamos el widget, para que muestre el nuevo contenido
                //actualizarWidget();

            }else
            {
                Snackbar.make(parentLayout, R.string.errNotaVacia, Snackbar.LENGTH_LONG).show();
            }

            barraBusqueda.onActionViewCollapsed();//Se cierra la barra de busqueda, por si estuviera abierta

            //Al cerrar la barra de busqueda volvemos a mostrar el titulo de la ToolBar
            titToolbar.setVisibility(View.VISIBLE);

        }

    }

    @OnClick(R.id.btnFlotante)
    public void addNote(View view)
    {

        if(mapItemSelecc.size() == 0)//Significa que no hay ningun item seleccionado
        {
            Intent intent = new Intent(this, PantallaNuevaNota.class);
            startActivityForResult(intent, CODE_NOTE_ADD);

        }else//Significa que hay algun item seleccionado, por lo que se procede a su borrado
        {

            List<Nota> listaAux = new ArrayList<Nota>();
            final List<Nota> copiaListaNotas = new ArrayList<Nota>();
            final Map<Integer, Integer> copiaItemSelecc = new HashMap<Integer, Integer>();

            //primero Se hace una copia de la lista de notas por si el usuario decide deshacer los cambios
            copiaListaNotas.addAll(listaNotas);

            //Se hace una copia del HashMap con los elementos seleccionados
            copiaItemSelecc.putAll(mapItemSelecc);

            //Ahora se copia en la lista auxiliar aquellos elementos que no han sido seleccionados
            for(int i=0; i<listaNotas.size(); i++)
            {
                if(mapItemSelecc.get(i) == null)
                    listaAux.add(listaNotas.get(i));
            }

            listaNotas.clear();//Se borra la lista de notas

            //Y ahora se guardan en la lista de notas los elementos guardados en la lista auxiliar
            // (es decir aquello que no han sido seleccionados)
            for(int i=0; i<listaAux.size(); i++)
            {
                listaNotas.add(listaAux.get(i));
            }

            listaAux.clear();
            mapItemSelecc.clear();
            aln.notifyDataSetChanged();
            btnFlotante.setImageDrawable(imgAdd);


            //********* Se muestra la snackbar indicando el numero de elemento eliminados **********
            Snackbar.make(parentLayout, copiaListaNotas.size() - listaNotas.size()+" se ha eliminado", Snackbar.LENGTH_LONG)
                    .setActionTextColor(colorVerde)
                    .setAction("Deshacer", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deshacerCambios = true;
                        }
                    })
                    .setCallback(new Snackbar.Callback()
                    {
                        @Override
                        //Se ejecuta cuando el Snackbar deja de mostrarse
                        public void onDismissed(Snackbar snackbar, int event)
                        {
                            super.onDismissed(snackbar, event);

                            if(deshacerCambios)
                            {
                                //Se deshacen los cambios volviendo a volcar todos los datos de la copia de seguridad con la lista de notas
                                listaNotas.clear();
                                listaNotas.addAll(copiaListaNotas);
                                copiaListaNotas.clear();
                                mapItemSelecc.clear();
                                aln.notifyDataSetChanged();

                            }else
                            {
                                //Se borran las notas seleccionadas de la BD
                                eliminar_Notas_BD(copiaListaNotas,copiaItemSelecc);

                                listaNotasBusquedas.clear();
                                listaNotasBusquedas.addAll(listaNotas);//Se copian los elementos no borrados en la lista, usada como copia para las busquedas

                                mapItemSelecc.clear();//se eliminan todos los elementos del HashMap
                                copiaListaNotas.clear();
                                aln.notifyDataSetChanged();
                            }

                            deshacerCambios = false;
                        }

                        @Override
                        //Se ejecuta cuando la Snackbar se muestra
                        public void onShown(Snackbar snackbar)
                        {
                            super.onShown(snackbar);
                        }

                    }).show();
            //****************************** Fin SnackBar ******************************************

        }

    }

    @OnItemClick(R.id.listViewNotas)
    public void selecc_Nota_Modificar(int posicion)
    {

        if(mapItemSelecc.get(posicion) != null)//Significa que se ha pulsado sobre un item seleccionado
        {
            //Se deselecciona el item pulsado en la lista
            mapItemSelecc.remove(posicion);//Se elimina el elemento del HashMap que guarda la posicion de los items seleccionados
            aln.notifyDataSetChanged();

            if(mapItemSelecc.size() == 0)
                btnFlotante.setImageDrawable(imgAdd);

        }else//Se ha pulsado sobre un elemento no seleccionado
        {
            //Si hay elementos seleccionados previamente, deseleccionan
            mapItemSelecc.clear();
            aln.notifyDataSetChanged();
            btnFlotante.setImageDrawable(imgAdd);

            //Se lanza la actividad para la edicion de la nota
            Intent intent = new Intent(this, PantallaModificarNota.class);
            intent.putExtra("id_nota", listaNotas.get(posicion).getIdNota());
            intent.putExtra("titulo", listaNotas.get(posicion).getTitulo());
            intent.putExtra("contenido", listaNotas.get(posicion).getContenido());
            intent.putExtra("imagen_uri", listaNotas.get(posicion).getImagenUri());
            startActivityForResult(intent, CODE_NOTE_MOD);
        }
    }

    @OnItemLongClick(R.id.listViewNotas)
    public boolean selecc_Nota_Borrar(int posicion)
    {

        if(mapItemSelecc.get(posicion) == null)
        {
            //Se añade la posicion del item seleccionado en el Map para que se muestre seleccionado
            mapItemSelecc.put(posicion, posicion);
            btnFlotante.setImageDrawable(imgBasura);

        }else
        {
            //Significa que se ha pulsado sobre un item ya seleccionado y por lo tanto lo deseleccionamos
            mapItemSelecc.remove(posicion);//Se elimina el elemento del HashMap que guarda la posicion de los items seleccionados

            if(mapItemSelecc.size() == 0)
                btnFlotante.setImageDrawable(imgAdd);
        }

        aln.notifyDataSetChanged();

        return true;

    }

    //En este metodo se actulzia el widget, para mostrar los cambios en la lista de notas
    //Al final no uso esto de actualizar el widget desde la aplicacion, porque en el telefono con version api21
    //pasa como con el widget de utilities que la mayoria de las veces no responde a las pulsaciones despues de a ver usado esto
    private void actualizarWidget()
    {

        //para actualiza el widget se hace todo esto que llama al metodo onUpdate del Widget
        /*AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        ComponentName componentName = new ComponentName(getApplicationContext(), WidgetNotas.class);
        int[] ids = appWidgetManager.getAppWidgetIds(componentName);
        Intent update_widget = new Intent();
        update_widget.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        update_widget.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        getApplicationContext().sendBroadcast(update_widget);*/

    }

    //Se busca el texto introducido en el SearchView en el titulo y contenido de cada una
    //de las notas, y se muestran en la lista solo aquellas notas que incluyen el texto buscado.
    private void buscar_palabra(String palabra)
    {

        listaNotas.clear();

        for(Nota nota: listaNotasBusquedas)
        {

           if(nota.getTitulo().contains(palabra))//Si el texto del titulo contiene la palabra buscada
           {
               nota.setSpanableContenido(null);

               //Obtenemos la posicion que ocupa el texto buscado en texto titulo
               int posicion = nota.getTitulo().indexOf(palabra);

               //Se crea un objeto spanable con el texto del titulo y dentro de el resaltamos en color naranja
               //la palabra buscada que aparece dentro del texto titulo
               Spannable spannableText = new SpannableString(nota.getTitulo());
               spannableText.setSpan(new ForegroundColorSpan(colorNaranja), posicion, posicion + palabra.length(), 0);

               nota.setSpanableTitulo(spannableText);

               listaNotas.add(nota);


           }else
           {

               //Si el texto del contenido contiene la palabra buscada
               if(nota.getContenido().contains(palabra))
               {
                   nota.setSpanableTitulo(null);

                   //Obtenemos la posicion que ocupa el texto buscado en texto titulo
                   int posicion = nota.getContenido().indexOf(palabra);

                   //Se crea un objeto spanable con el texto del titulo y dentro de el resaltamos en color naranja
                   //la palabra buscada que aparece dentro del texto titulo
                   Spannable spannableText = new SpannableString(nota.getContenido());
                   spannableText.setSpan(new ForegroundColorSpan(colorNaranja), posicion, posicion+palabra.length(), 0);

                   nota.setSpanableContenido(spannableText);

                   listaNotas.add(nota);

               }

           }

        }

        aln.notifyDataSetChanged();

    }

    //Se obtienen todas las notas almacenadas en la Base de Datos y se guardan en una Lista
    private void obtener_Notas_BD()
    {

        BDAdapter bdAdapter = new BDAdapter(this);
        bdAdapter.abrirBD_Lectura();

        Cursor cursor = bdAdapter.obtener_Notas();

        if(cursor != null)
        {
            listaNotas.clear();

            while(cursor.moveToNext())
            {
                Nota nota = new Nota();
                nota.setIdNota(cursor.getInt(0));
                nota.setTitulo(cursor.getString(1));
                nota.setContenido(cursor.getString(2));
                nota.setFecha(cursor.getString(3));
                nota.setImagenUri(cursor.getString(4));

                nota.setSpanableTitulo(null);
                nota.setSpanableContenido(null);

                listaNotas.add(nota);
            }
        }

        listaNotasBusquedas.clear();
        listaNotasBusquedas.addAll(listaNotas);//Se copian los elementos en la lista usada como copia para las busquedas

        bdAdapter.cerrarBD();

    }

    //Se recorre el HashMap para obtener la posicion de los elementos a eliminar
    //Se obtiene dichos elementos de la lista de notas y se eliminan de la Base de Datos
    private void eliminar_Notas_BD(List<Nota> listaNotas, Map<Integer,Integer> mapItemSelecc)
    {

        BDAdapter bdAdapter = new BDAdapter(this);
        bdAdapter.abrirBD_Escritura();

        Iterator it = mapItemSelecc.entrySet().iterator();

        while(it.hasNext() && listaNotas.size()>0)
        {
            Map.Entry entry = (Map.Entry) it.next();
            bdAdapter.eliminar_Nota(listaNotas.get((int)entry.getKey()));
        }

        bdAdapter.cerrarBD();

    }

}
