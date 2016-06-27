package dssoft.com.notesapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import dssoft.com.notesapp.R;
import dssoft.com.notesapp.pojo.Nota;

/**
 * Created by Angel on 04/03/2016.
 */
public class BDAdapter
{
    private final String crearTabla = "Create Table Notas(id_nota Integer PRIMARY KEY AUTOINCREMENT, titulo String, contenido String, fecha String, imagen_uri String)";
    private final String eliminarTabla = "DROP TABLE IF EXISTS Notas";
    private final String nombreBD = "BDnotas";

    private int versionBD = 1;
    private Context context;
    private SQLiteDatabase db;
    private NotasSQLiteHelper notasSQLiteHelper;


    public BDAdapter(Context context)
    {
        this.context = context;
        notasSQLiteHelper = new NotasSQLiteHelper(context, nombreBD, null, versionBD);
    }

    //Se abre la BD en modo Lectura
    public void abrirBD_Lectura()
    {
        db = notasSQLiteHelper.getReadableDatabase();
    }

    //Se abre la BD en modo Escritura
    public void abrirBD_Escritura()
    {
        db = notasSQLiteHelper.getWritableDatabase();
    }

    //Se cierra la BD
    public void cerrarBD()
    {
        db.close();
    }

    public void insertar_Nota(Nota nota)
    {

        try
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put("titulo", nota.getTitulo());
            contentValues.put("contenido", nota.getContenido());
            contentValues.put("fecha", nota.getFecha());
            contentValues.put("imagen_uri", nota.getImagenUri());

            db.insert("Notas", null, contentValues);

        }catch(Exception ex)
        {
            Toast.makeText(context, R.string.errBD, Toast.LENGTH_LONG).show();
        }

    }

    public void modificar_Nota(Nota nota)
    {

        try
        {
            ContentValues contentValues = new ContentValues();
            contentValues.put("titulo", nota.getTitulo());
            contentValues.put("contenido", nota.getContenido());
            contentValues.put("fecha", nota.getFecha());
            contentValues.put("imagen_uri", nota.getImagenUri());

            String[] parametro = {String.valueOf(nota.getIdNota())};

            db.update("Notas", contentValues, "id_nota=?", parametro);

        }catch(Exception ex)
        {
            Toast.makeText(context, R.string.errBD3, Toast.LENGTH_LONG).show();
        }

    }

    public void eliminar_Nota(Nota nota)
    {
        try
        {
            String[] parametro = {String.valueOf(nota.getIdNota())};
            db.delete("Notas", "id_nota=?",parametro);

        }catch(Exception ex)
        {
            Toast.makeText(context, R.string.errBD2, Toast.LENGTH_LONG).show();
        }
    }

    //Se obtienen todos los registros almacenados en la BD
    public Cursor obtener_Notas()
    {
        try
        {
            Cursor cursor = db.rawQuery("Select * from Notas",null);

            return cursor;

        }catch(Exception ex)
        {
            Toast.makeText(context, "No se han podido recuperar las notas almacenadas", Toast.LENGTH_LONG).show();
            return null;
        }


    }



    private class NotasSQLiteHelper extends SQLiteOpenHelper
    {


        public NotasSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
        {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(crearTabla);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {

            db.execSQL(eliminarTabla);
            db.execSQL(crearTabla);

            /*Esto es una prueba en la que guardo lo que hubiera en la tabla y despues lo vuelvo a insertar


            //Se guarda todas las notas de la base de datos en una lista
            Cursor cursor = db.rawQuery("Select * from Notas",null);
            List<Nota> listaNotas = new ArrayList<Nota>();

            if(cursor != null)
            {
                if(cursor.getCount() >0)
                {
                    cursor.moveToFirst();
                    Nota nota = new Nota();
                    nota.setIdNota(cursor.getInt(0));
                    nota.setTitulo(cursor.getString(1));
                    nota.setContenido(cursor.getString(2));
                    nota.setFecha(cursor.getString(3));
                    nota.setImagenUri(cursor.getString(4));
                    listaNotas.add(nota);

                    while(cursor.moveToNext())
                    {
                        nota = new Nota();
                        nota.setIdNota(cursor.getInt(0));
                        nota.setTitulo(cursor.getString(1));
                        nota.setContenido(cursor.getString(2));
                        nota.setFecha(cursor.getString(3));
                        nota.setImagenUri(cursor.getString(4));
                        listaNotas.add(nota);
                    }
                }

            }

            Log.e("Actualizacion", "BD");
            db.execSQL(eliminarTabla);
            db.execSQL(crearTabla);

            //lleemos los elemetos de la lista y lo guardamos en la tabla

            for(Nota nota: listaNotas)
            {
                ContentValues contentValues = new ContentValues();
                contentValues.put("titulo", nota.getTitulo());
                contentValues.put("contenido", nota.getContenido());
                contentValues.put("fecha", nota.getFecha());
                contentValues.put("imagen_uri", nota.getImagenUri());

                db.insert("Notas", null, contentValues);
            }


            Log.e("Fin Actualizacion", "BD");
            */
        }
    }


}
