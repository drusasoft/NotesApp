package dssoft.com.notesapp;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.IOException;
import butterknife.Bind;
import butterknife.ButterKnife;
import dssoft.com.notesapp.componentes.ImagenZoom;
import dssoft.com.notesapp.componentes.ScrollHorizontal;

/**
 * Created by Angel on 18/03/2016.
 */
public class PantallaZoom extends AppCompatActivity
{

    @Bind(R.id.toolbarZoom) Toolbar toolbar;
    @Bind(R.id.scroll_horizontal) ScrollHorizontal scroll_Horizontal;//HorizontaScrollView tuneado por mi para que se pueda bloquer y habilitar el scroll cuando se quiera
    @Bind(R.id.layout_zoom1) LinearLayout layoutZoom;
    private ImagenZoom imagenZoom;
    private int anchoMin, altoMin, anchoActual, altoActual, anchoMax, altoMax;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pantalla_zoom);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Uri imagen_uri = Uri.parse(getIntent().getStringExtra("imagen_uri"));

        calcular_Dimension_Imagen(imagen_uri);

        scroll_Horizontal.bloquearScroll();//Como cuando se inicia la pantalla la imagen tiene el ancho de la pantalla pues bloqueamos el scroll

        imagenZoom = new ImagenZoom(getApplicationContext(), this, anchoMin, altoMin, imagen_uri);
        imagenZoom.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imagenZoom.setLayoutParams(new ViewGroup.LayoutParams(anchoMax, altoMax));
        layoutZoom.addView(imagenZoom);

    }


    public void habilitarScroll(boolean habilitar)
    {
        if(habilitar)
        {
            scroll_Horizontal.habilitarScroll();//Se habilita el scroll en el HorizontalScrollView tuneado
        }else
        {
            scroll_Horizontal.bloquearScroll();//Se deshabilita el scroll en el HorizontalScrollView tuneado
        }
    }


    private void calcular_Dimension_Imagen(Uri imagen_uri)
    {
        try
        {
            //Se crea un bitmap a partir de una URI y obtenemos datos sobre su tamaño
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagen_uri);
            int ancho_original = bitmap.getWidth();
            int alto_original = bitmap.getHeight();
            int densidadImagen = bitmap.getDensity();

            //Obtnemos el ancho de la pantalla ya que el ancho minimo de la imagen va a ser ese
            DisplayMetrics displayMetrics=new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            anchoMin = displayMetrics.widthPixels;

            if(ancho_original > alto_original)
            {
                altoMin = anchoMin - (anchoMin/3);
                anchoMax = anchoMin + 240;
                altoMax = altoMin + 240;

            }else
            {
                altoMin = anchoMin + (anchoMin/4);
                anchoMax = anchoMin + 240;
                altoMax = altoMin + 240;
            }

            Toast.makeText(this, "Ancho = " + ancho_original + "  Alto = " + alto_original + "  Desnsidad = " + densidadImagen, Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
