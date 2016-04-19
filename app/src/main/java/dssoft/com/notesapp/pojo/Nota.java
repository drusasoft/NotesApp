package dssoft.com.notesapp.pojo;

import android.text.Spannable;
import android.text.SpannableString;

/**
 * Created by Angel on 04/03/2016.
 */
public class Nota
{

    private int idNota;
    private String titulo;
    private String contenido;
    private String fecha;
    private String imagenUri;
    private Spannable spanableTitulo;
    private Spannable spanableContenido;

    public Nota()
    {

    }

    public Nota(int idNota, String titulo,  String contenido, String fecha, String imagenUri)
    {
        this.idNota = idNota;
        this.titulo = titulo;
        this.contenido = contenido;
        this.fecha = fecha;
        this.imagenUri = imagenUri;
    }

    public int getIdNota() {
        return idNota;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setIdNota(int idNota) {
        this.idNota = idNota;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getImagenUri() {
        return imagenUri;
    }

    public void setImagenUri(String imagenUri) {
        this.imagenUri = imagenUri;
    }

    public Spannable getSpanableTitulo() {
        return spanableTitulo;
    }

    public void setSpanableTitulo(Spannable spanableTitulo) {
        this.spanableTitulo = spanableTitulo;
    }

    public Spannable getSpanableContenido() {
        return spanableContenido;
    }

    public void setSpanableContenido(Spannable spanableContenido) {
        this.spanableContenido = spanableContenido;
    }


}
