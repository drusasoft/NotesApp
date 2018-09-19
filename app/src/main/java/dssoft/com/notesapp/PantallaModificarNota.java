package dssoft.com.notesapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.Calendar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTextChanged;
import dssoft.com.notesapp.database.BDAdapter;
import dssoft.com.notesapp.pojo.Nota;
import dssoft.com.notesapp.transformation.BitmapTransform;

/**
 * Created by Angel on 15/03/2016.
 */
public class PantallaModificarNota extends AppCompatActivity
{

    @Bind(R.id.toolbarModNota) Toolbar toolbar;
    @Bind(R.id.editTituloMod) EditText editTextTitulo;
    @Bind(R.id.editContenidoMod) EditText editTextContenido;
    @Bind(R.id.txtImgModNota) TextView txtImgModNota;
    @Bind(R.id.imgModNota) ImageView imgModNota;

    private final int LOAD_IMAGE_CODE = 300;
    private final int MAX_WIDTH = 1024;
    private final int MAX_HEIGHT = 768;
    private int idNota;

    private boolean editTituloMayusculas=true;
    private boolean editContenidoMayusculas=true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pantalla_modificar_nota);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Se obtiene los datos de la nota a modificar, pasados como parametros
        //y se muestran en los EditText correspondientes
        idNota = getIntent().getIntExtra("id_nota",0);
        editTextTitulo.setText(getIntent().getStringExtra("titulo"));
        editTextContenido.setText(getIntent().getStringExtra("contenido"));

        if(getIntent().getStringExtra("imagen_uri") != null)
        {
            Uri uri_imagen = Uri.parse(getIntent().getStringExtra("imagen_uri"));
            imgModNota.setTag(uri_imagen.toString().trim());
            txtImgModNota.setText(R.string.txtCargarImagenMod_2);

            Picasso.with(this)
                    .load(uri_imagen)
                    .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                    .error(R.drawable.error_image)
                    .into(imgModNota);
        }else
        {
            imgModNota.setTag(null);
            txtImgModNota.setText(R.string.txtCargarImagenMod_1);

            Picasso.with(this)
                    .load(android.R.drawable.ic_menu_gallery)
                    .error(R.drawable.error_image)
                    .into(imgModNota);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_modificar_nota, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch(item.getItemId())
        {

            case android.R.id.home: cancelar_Modificacion();
                                    return true;

            case R.id.menu_modNote: guardar_Modificacion();
                                    return true;

        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if(requestCode == LOAD_IMAGE_CODE)
        {

            if(resultCode == RESULT_OK)
            {
                //Obtengo la Uri de la imagen seleccionada por el usuario de la galeria
                Uri uri_Imagen = data.getData();

                imgModNota.setTag(uri_Imagen.toString().trim());

                //Picasso es muy guay porque si al cargar una imagen da error de memoria (Por ejemplo, porque es muy grande y hay que reducirla mucho)
                //Entonces se muestra la imagen de error que hemos establecido por defecto
                Picasso.with(this)
                        .load(uri_Imagen)
                        .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                        .error(R.drawable.error_image)
                        .into(imgModNota);
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.btnFlotanteMod)
    //Se guardan las modificaciones realizadas sobre la nota en la Base de datos
    public void guardar_Modificacion()
    {

        String titulo = editTextTitulo.getText().toString();
        String contenido = editTextContenido.getText().toString();

        titulo = titulo.trim();
        contenido = contenido.trim();

        if(titulo.length() > 0 || contenido.length()>0)
        {

            String fecha = obtener_Fecha();

            Nota nota = new Nota();
            nota.setIdNota(idNota);
            nota.setTitulo(titulo);
            nota.setContenido(contenido);
            nota.setFecha(fecha);

            if(imgModNota.getTag() != null)
            {
                nota.setImagenUri(imgModNota.getTag().toString());
            }else
            {
                nota.setImagenUri(null);
            }

            BDAdapter bdAdapter = new BDAdapter(this);

            bdAdapter.abrirBD_Escritura();

            bdAdapter.modificar_Nota(nota);

            bdAdapter.cerrarBD();

            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();


        }else
        {
            cancelar_Modificacion();
        }

    }

    @OnClick(R.id.layoutCargarImagenMod)
    //Se accede a la galeria del dispositivo
    public void acceso_galeria()
    {
        Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intentGaleria, LOAD_IMAGE_CODE);
    }

    @OnLongClick(R.id.layoutCargarImagenMod)
    public boolean mostrar_DialogEliminar()
    {
        if(imgModNota.getTag() != null)
        {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.titDialogEliminar);
            builder.setMessage(R.string.txtDialogEliminar);
            builder.setCancelable(false);

            builder.setPositiveButton(R.string.btnSi, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    imgModNota.setTag(null);
                    txtImgModNota.setText(R.string.txtCargarImagenMod_1);

                    Picasso.with(getApplicationContext())
                            .load(android.R.drawable.ic_menu_gallery)
                            .into(imgModNota);

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


    //Se ejecuta cuando se cambia el texto del EditText titulo
    @OnTextChanged(R.id.editTituloMod)
    public void cambioTextoTitulo()
    {
        //se pone la primera letra del titulo en mayusculas
        if(editTextTitulo.getText().toString().length() == 1 && editTituloMayusculas)
        {
            String contenido = editTextTitulo.getText().toString().toUpperCase();
            editTituloMayusculas = false;
            editTextTitulo.setText(contenido);
            editTextTitulo.setSelection(1);
        }else
        {
            if(editTextTitulo.getText().toString().length() == 0)
                editTituloMayusculas = true;
        }
    }


    //Se ejecuta cuando se cambia el texto del EditText contenido
    @OnTextChanged(R.id.editContenidoMod)
    public void cambioTextoContenido()
    {

        //se pone la primera letra del contenido en mayusculas
        if(editTextContenido.getText().toString().length() == 1 && editContenidoMayusculas)
        {
            String contenido = editTextContenido.getText().toString().toUpperCase();
            editContenidoMayusculas = false;
            editTextContenido.setText(contenido);
            editTextContenido.setSelection(1);

        }else
        {
            if(editTextContenido.getText().toString().length() == 0)
                editContenidoMayusculas = true;
        }
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

    private void cancelar_Modificacion()
    {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
