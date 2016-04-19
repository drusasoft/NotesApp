package dssoft.com.notesapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.Calendar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import dssoft.com.notesapp.database.BDAdapter;
import dssoft.com.notesapp.pojo.Nota;
import dssoft.com.notesapp.transformation.BitmapTransform;

/**
 * Created by Angel on 04/03/2016.
 */
public class PantallaNuevaNota extends AppCompatActivity
{

    @Bind(R.id.toolbarNuevaNota) Toolbar toolbar;
    @Bind(R.id.editTitulo) EditText editTitulo;
    @Bind(R.id.editContenido) EditText editContenido;
    @Bind(R.id.imgNuevaNota) ImageView imgNota;
    private final int LOAD_IMAGE_CODE = 300;
    private final int MAX_WIDTH = 1024;
    private final int MAX_HEIGHT = 768;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pantalla_nueva_nota);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        imgNota.setTag(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nueva_nota, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch(item.getItemId())
        {

            case android.R.id.home: cancelar_Nota();
                                    return true;

            case R.id.menu_newNote: guardar_Nota();
                                    return true;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == LOAD_IMAGE_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                //Obtengo la Uri de la imagen seleccionada por el usuario de la galeria
                Uri uri_Imagen = data.getData();

                imgNota.setTag(uri_Imagen.toString().trim());

                //Picasso es muy guay porque si al cargar una imagen da error de memoria (Por ejemplo, porque es muy grande y hay que reducirla mucho)
                //Entonces se muestra la imagen de error que hemos establecido por defecto
                Picasso.with(this)
                        .load(uri_Imagen)
                        .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                        .error(R.drawable.error_image)
                        .into(imgNota);

            }
        }
    }

    //Se guarda una nueva en la BD
    @OnClick(R.id.btnFlotanteSave)
    //Se Guarda la nueva nota creada en la Base de Datos
    public void guardar_Nota()
    {
        String titulo = editTitulo.getText().toString();
        String contenido = editContenido.getText().toString();
        titulo = titulo.trim();
        contenido = contenido.trim();

        if(titulo.length() > 0 || contenido.length() > 0)
        {

            String fecha = obtener_Fecha();

            Nota nota = new Nota();
            nota.setTitulo(titulo);
            nota.setContenido(contenido);
            nota.setFecha(fecha);

            if(imgNota.getTag() != null)
            {
                nota.setImagenUri(imgNota.getTag().toString().trim());
            }else
            {
                nota.setImagenUri(null);
            }

            BDAdapter bdAdapter = new BDAdapter(this);

            bdAdapter.abrirBD_Escritura();
            bdAdapter.insertar_Nota(nota);

            bdAdapter.cerrarBD();

            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();

        }else
        {
            cancelar_Nota();
        }

    }

    @OnClick(R.id.layoutCargarImagen)
    //Se accede a la galeria del dispositivo
    public void acceso_galeria()
    {
        Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intentGaleria, LOAD_IMAGE_CODE);
    }

    @OnLongClick(R.id.layoutCargarImagen)
    public boolean mostrar_DialogEliminar()
    {

        if(imgNota.getTag() != null)
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.titDialogEliminar);
            builder.setMessage(R.string.txtDialogEliminar);
            builder.setCancelable(false);

            builder.setPositiveButton(R.string.btnSi, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    imgNota.setTag(null);

                    Picasso.with(getApplicationContext())
                            .load(android.R.drawable.ic_menu_gallery)
                            .into(imgNota);

                    dialog.dismiss();
                }
            });

            builder.setNegativeButton(R.string.btnNo, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    dialog.dismiss();
                }
            });

            builder.create().show();

        }

        return true;

    }

    //Se Obtiene la fecha del sistema y se pone en un formato que me gusta a mi
    // (si el numero es menor de 0) se le añade un cero delante
    private String obtener_Fecha()
    {
        String diaF="", mesF="";

        //Se obtiene la fecha del sistema
        Calendar calendar = Calendar.getInstance();
        int dia = calendar.get(Calendar.DATE);
        int mes = calendar.get(Calendar.MONTH) + 1;
        int anno = calendar.get(Calendar.YEAR);

        if(dia < 10)
        {
            diaF = "0"+dia;
        }else
        {
            diaF = String.valueOf(dia);
        }

        if(mes < 10)
        {
            mesF = "0"+mes;
        }else
        {
            mesF = String.valueOf(mesF);
        }

        return diaF+"/"+mesF+"/"+anno;

    }

    //Se llama a la actividad PantallaPrincipal pasandole resultado Cancelado
    private void cancelar_Nota()
    {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

}
