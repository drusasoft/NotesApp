package dssoft.com.notesapp.transformation;

import android.graphics.Bitmap;
import com.squareup.picasso.Transformation;

/**
 * Created by Angel on 22/03/2016.
 */

//Clase que implementa la interfaz Transformation de picasso
//en esta clase cambiamos el tamaño de la imagen que se va cargar con picasso
// para que tenga un tamaño maximo de 1024x768, y asi evitar que se produzcan errores de memoria
// o que la aplicacion vaya lenta al cargar una imagen demasiado grande
public class BitmapTransform implements Transformation
{
    int maxWidth;
    int maxHeight;

    public BitmapTransform(int maxWidth, int maxHeight)
    {
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }


    @Override
    public Bitmap transform(Bitmap source)
    {
        int targetWidth, targetHeight;
        double aspectRatio;

        //Si la imagen es mas ancha que alta
        if(source.getWidth() > source.getHeight())
        {
            targetWidth = maxWidth;
            aspectRatio = (double) source.getHeight() / (double) source.getWidth();
            targetHeight = (int) (targetWidth * aspectRatio);//se calcula el alto en funcion de el ancho establecido

        }else
        {
            targetHeight = maxHeight;
            aspectRatio = (double) source.getWidth() / (double) source.getHeight();
            targetWidth = (int) (targetHeight * aspectRatio);//se calcula el alto en funcion de el ancho establecido
        }

        //Se obiene el el nuevo bitmap (con el nuevo tamaño calculado) que se va a cargar en picasso
        Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);

        if (result != source) {
            source.recycle();
        }

        return result;
    }

    @Override
    public String key()
    {
        return maxWidth + "x" + maxHeight;
    }

}
