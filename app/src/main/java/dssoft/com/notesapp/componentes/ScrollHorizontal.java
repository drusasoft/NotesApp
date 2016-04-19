package dssoft.com.notesapp.componentes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

//Creo un HorizontalScroll tuneado para poder hacer que se pueda bloquea y desbloquear
//el scroll cuando queramos, el objetivo es que cuando se haga el gesto de de escalar o reducir la imagen
//se bloquee el movimiento de scroll para que se pueda hacer de forma mas comoda para el usuario
@SuppressLint("ClickableViewAccessibility")
public class ScrollHorizontal extends HorizontalScrollView
{

	private boolean habilitar;
	
	public ScrollHorizontal(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public ScrollHorizontal(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
		
	public void bloquearScroll()
	{
		habilitar = false;
	}
	
	public void habilitarScroll()
	{
		habilitar = true;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		switch(ev.getAction())
		{
		
			case MotionEvent.ACTION_DOWN: if(habilitar)
										  {
												return super.onTouchEvent(ev);
										  }else
										  {
											  return habilitar;
										  }
			
			default: return super.onTouchEvent(ev);
				
		}
		
		
	}
	
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		if(!habilitar)
		{
			return false;
		}else
		{
			return super.onInterceptTouchEvent(ev);
		}
		
	}

}
