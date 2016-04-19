package dssoft.com.notesapp;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import dssoft.com.notesapp.R;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Angel on 20/03/2016.
 */
//EN esta pantalla uso la libreria PhotoView que permite hacer zoom y otras cosas falcilmente sobre un ImageView
public class PantallaZoomPhotoView extends AppCompatActivity
{

    @Bind(R.id.toolbarZoomPhotoView) Toolbar toolbar;
    @Bind(R.id.imgZoom) ImageView imgZoom;
    private PhotoViewAttacher photoViewAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pantalla_zoom_photoview);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        Uri imagen_uri = Uri.parse(getIntent().getStringExtra("imagen_uri"));

        imgZoom.setImageURI(imagen_uri);
        photoViewAttacher = new PhotoViewAttacher(imgZoom);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch(item.getItemId())
        {
            case android.R.id.home: onBackPressed();
                                    return true;
        }

        return false;
    }
}
