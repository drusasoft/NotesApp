package dssoft.com.notesapp.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;
import java.util.Map;
import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dssoft.com.notesapp.PantallaZoomPhotoView;
import dssoft.com.notesapp.R;
import dssoft.com.notesapp.pojo.Nota;
import dssoft.com.notesapp.transformation.BitmapTransform;

/**
 * Created by Angel on 09/03/2016.
 */
public class AdaptadorListaNotas extends ArrayAdapter<Nota>
{
    private static Activity context;
    private List<Nota> listaNotas;
    private Map<Integer, Integer> mapItemSelecc;
    private LayoutInflater Inflater;
    private final int MAX_WIDTH = 1024;
    private final int MAX_HEIGHT = 768;
    private Typeface fuente;

    public AdaptadorListaNotas(Activity context, List<Nota> listaNotas, Map<Integer,Integer> mapItemSelecc)
    {
        super(context, R.layout.layout_lista_notas, listaNotas);
        this.context = context;
        this.listaNotas = listaNotas;
        this.mapItemSelecc = mapItemSelecc;
        Inflater = context.getLayoutInflater();

        //Se cambia la fuente de los textView
        fuente = Typeface.createFromAsset(context.getAssets(),"fonts/gnyrwn971.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View fila = convertView;
        ViewHolder holder;

        if(fila != null)
        {
            holder = (ViewHolder) fila.getTag();
        }else
        {
            fila = Inflater.inflate(R.layout.layout_lista_notas, parent, false);
            holder = new ViewHolder(fila);
            fila.setTag(holder);
        }

        //Se muestra el Titulo y el Contenido en los TextView Correspondientes
        //Si alguno de los Objetos Spanables es distinto de nulo entonces se muestran ellos en los textview
        //ya que en ellos se muestra la palabra buscada resaltada en naranja dentro del texto.
        if(listaNotas.get(position).getSpanableTitulo() != null)
        {
            holder.txtTitulo.setText(listaNotas.get(position).getSpanableTitulo());
        }else
        {
            holder.txtTitulo.setText(listaNotas.get(position).getTitulo());
        }

        if(listaNotas.get(position).getSpanableContenido() != null)
        {
            holder.txtContenido.setText(listaNotas.get(position).getSpanableContenido());
        }else
        {
            holder.txtContenido.setText(listaNotas.get(position).getContenido());
        }

        //Mostramos la fecha de cracion de la Nota
        holder.txtFecha.setText(listaNotas.get(position).getFecha());


        //Si la nota incluye una imagen entonces de muestra dicha imagen en el ImageView imgNota
        //EN caso contrario se oculta el ImageView
        if(listaNotas.get(position).getImagenUri() != null)
        {
            Uri uri_Imagen = Uri.parse(listaNotas.get(position).getImagenUri());
            holder.imgNota.setTag(listaNotas.get(position).getImagenUri());
            holder.imgNota.setVisibility(View.VISIBLE);

            //Picasso es muy guay porque si al cargar una imagen da error de memoria (Por ejemplo, porque es muy grande y hay que reducirla mucho)
            //Entonces se muestra la imagen de error que hemos establecido por defecto
            Picasso.with(context)
                    .load(uri_Imagen)
                    .transform(new BitmapTransform(MAX_WIDTH,MAX_HEIGHT))
                    .error(R.drawable.error_image)
                    .into(holder.imgNota);
        }else
        {
            holder.imgNota.setVisibility(View.GONE);
        }


        //Si elemento se encuantra en el HashMap de elenetos seleccionados
        //Entonces se cambia el color de fondo del layout y el color de las letras para que aparezca resaltado
        if(mapItemSelecc.get(position) != null)
        {
            holder.layoutNota.setBackgroundColor(holder.colorVerde);
            holder.txtTitulo.setTextColor(holder.colorNaranja);
            holder.txtContenido.setTextColor(holder.colorNaranja);
            holder.txtFecha.setTextColor(holder.colorNaranja);

        }else
        {

            if(Build.VERSION.SDK_INT >= 16)
            {
                holder.layoutNota.setBackground(holder.fondo_item_nota);
            }else
            {
                holder.layoutNota.setBackgroundDrawable(holder.fondo_item_nota);
            }

            holder.txtTitulo.setTextColor(holder.colorNegro);
            holder.txtContenido.setTextColor(holder.colorGris);
            holder.txtFecha.setTextColor(holder.colorPrimary);

        }

        //Se cambia la fuente de los TextViews
        //holder.txtTitulo.setTypeface(fuente);
        //holder.txtContenido.setTypeface(fuente);
        holder.txtFecha.setTypeface(fuente);

        return fila;

    }

    static class ViewHolder
    {
        @Bind(R.id.layoutNota) LinearLayout layoutNota;
        @Bind(R.id.txtTitulo) TextView txtTitulo;
        @Bind(R.id.txtContenido) TextView txtContenido;
        @Bind(R.id.txtFecha) TextView txtFecha;
        @Bind(R.id.imgNotaLista) ImageView imgNota;
        @BindDrawable(R.drawable.fondo_item_nota) Drawable fondo_item_nota;
        @BindColor(R.color.colorPrimary) int colorPrimary;
        @BindColor(R.color.negro) int colorNegro;
        @BindColor(R.color.gris) int colorGris;
        @BindColor(R.color.naranja_oscuro) int colorNaranja;
        @BindColor(R.color.verde_claro) int colorVerde;

        public ViewHolder(View view)
        {
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.imgNotaLista)
        public void push_Imagen_Nota(View view)//Si se pulsa sobre la imagen de la nota
        {
            //Se lanza la actividad para mostrar en grande la imagen pulsada
            Intent intent = new Intent(context, PantallaZoomPhotoView.class);
            intent.putExtra("imagen_uri", view.getTag().toString());//Se pasa como parametro la direccion uri de la imagen

            if(Build.VERSION.SDK_INT < 21)
            {
                context.startActivity(intent);
            }else
            {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(context, view, "imgNota");
                context.startActivity(intent, options.toBundle());
            }

        }
    }
}
