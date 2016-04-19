package dssoft.com.notesapp.componentes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;
import java.io.FileNotFoundException;
import java.io.InputStream;
import dssoft.com.notesapp.PantallaZoom;
import dssoft.com.notesapp.R;


//Es un ImageVIew que hemos tunueado para que se puede hacer zoom sobre la imagen que contiene con el gesto de hacer zoom
public class ImagenZoom extends ImageView
{

	private ScaleGestureDetector mScaleGestureDetector;
	//private float mScaleFactor = 1f;
	private Drawable imagen;
	private PantallaZoom pantallaZoom;
	private int anchoActual, altoActual, anchoMin, altoMin, anchoMax, altoMax;
	
	public ImagenZoom(Context context, PantallaZoom pantallaZoom, int anchoMin, int altoMin, Uri imagen_Uri)
	{
		super(context);
		// TODO Auto-generated constructor stub
		this.pantallaZoom = pantallaZoom;
		this.anchoActual = anchoMin;
		this.altoActual = altoMin;
		this.anchoMin = anchoMin;
		this.altoMin = altoMin;
		anchoMax = anchoActual + 240;
		altoMax = altoActual + 240;
		
		//Se define el detector del gesto de escalar
		mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());

		//Se obtiene el el objeto drawable de la direccion uri pasada
		try		{

			InputStream inputStream = context.getContentResolver().openInputStream(imagen_Uri);
			imagen = Drawable.createFromStream(inputStream, imagen_Uri.toString().trim());

		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
			imagen = context.getResources().getDrawable(R.drawable.error_image);//Se carga la imagen error por defecto
		}

		imagen.setBounds(0, 0, anchoActual, altoActual);//Se establecen los limites de la imagen (el rectangulo)

	}
	
	public ImagenZoom(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		canvas.save();
		//canvas.scale(mScaleFactor, mScaleFactor);
		imagen.draw(canvas);		
		canvas.restore();
		
	}
	
	
	@Override
	//Necesario para que funcione el gesto de zoom
	public boolean onTouchEvent(MotionEvent ev)
	{
	    // Let the ScaleGestureDetector inspect all events.
		mScaleGestureDetector.onTouchEvent(ev);
	    return true;
	}

		
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
	{
		
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector)
		{
			//Se bloquea el scroll en el HorizontalScrollView Tuneado
			pantallaZoom.habilitarScroll(false);
			
			return true;
		}
		
		@Override
		//Se ejecuta cuando se hace el gesto de Zoom
		public boolean onScale(ScaleGestureDetector detector)
		{
		    //mScaleFactor *= detector.getScaleFactor();

		    // Don't let the object get too small or too large.
		    //mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
		 
		    if(detector.getScaleFactor() > 1.0)
		    {
		    	if(anchoActual < anchoMax)
		    		anchoActual = anchoActual + 10;
		    	
		    	if(altoActual < altoMax)
		    		altoActual = altoActual + 10;		    	
		    				
		    }else
		    {
		    	
		    	if(anchoActual > anchoMin)
		    		anchoActual = anchoActual - 10;
		    	
		    	if(altoActual > altoMin)
		    		altoActual = altoActual - 10;		    	
				
		    }		   
		    
		    imagen.setBounds(0, 0, anchoActual, altoActual);
		    invalidate();//borramos y volvemos a pintar el canvas
		    
		    return true;
		}
		
		@Override
		public void onScaleEnd(ScaleGestureDetector detector)
		{
			// TODO Auto-generated method stub
			super.onScaleEnd(detector);
			
			//Si el ancho actual de la imagen es mayor que el ancho de la pantalla entonces habitimamos el scroll en el HorizontalScrollView tuneado
			if(anchoActual > anchoMin)
			{
				pantallaZoom.habilitarScroll(true);
			}else
			{
				pantallaZoom.habilitarScroll(false);
			}
			
		}
		
	}


	//Este metodo es para aumentar el tama�o de la imagen pulsando el boton +
	/*public void escala_ConBoton()
	{
		
		if(anchoActual < anchoMax)
    		anchoActual = anchoActual + 10;
    	
    	if(altoActual < altoMax)
    		altoActual = altoActual + 10;	
		
		imagen.setBounds(0, 0, anchoActual, altoActual);
		invalidate();
		
		//Si el ancho actual de la imagen es mayor que el ancho de la pantalla entonces habitimamos el scroll en el HorizontalScrollView tuneado
		if(anchoActual > anchoMin)
		{
			pantallaZoom.habilitarScroll(true);
		}else
		{
			pantallaZoom.habilitarScroll(false);
		}
	}
	
	
	//Este metodo es para reducir el tama�o de la imagen pulsando el boton -
	public void reducir_ConBoton()
	{
		if(anchoActual > anchoMin)
    		anchoActual = anchoActual - 10;
    	
    	if(altoActual > altoMin)
    		altoActual = altoActual - 10;	
		
		imagen.setBounds(0, 0, anchoActual, altoActual);
		invalidate();
		
		//Si el ancho actual de la imagen es mayor que el ancho de la pantalla entonces habitimamos el scroll en el HorizontalScrollView tuneado
		if(anchoActual > anchoMin)
		{
			pantallaZoom.habilitarScroll(true);
		}else
		{
			pantallaZoom.habilitarScroll(false);
		}
		
	}*/

	
}
