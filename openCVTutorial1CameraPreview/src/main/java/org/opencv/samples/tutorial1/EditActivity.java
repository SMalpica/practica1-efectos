package org.opencv.samples.tutorial1;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Malpica on 26/02/2016.
 */
public class EditActivity extends Activity {

    private Bitmap original;
    private Bitmap editada;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.captured_layout);

        //retrieve captured image
        Bitmap img = (Bitmap) getIntent().getParcelableExtra("imagen");
        original = img;
        editada=original;

        //set layout
        ImageView iv = (ImageView)findViewById(R.id.originalImage);
        ImageView ev = (ImageView)findViewById(R.id.editedImage);
        iv.setImageBitmap(Bitmap.createScaledBitmap(img, img.getWidth() / 2,
                img.getHeight() / 2, false));
        ev.setImageBitmap(Bitmap.createScaledBitmap(img,img.getWidth()/2,
                img.getHeight()/2,false));

        Button contraste = (Button) findViewById(R.id.btnContraste);
        Button alien = (Button) findViewById(R.id.btnAlien);
        Button distorsion = (Button) findViewById(R.id.btnDistorsion);
        Button guardar = (Button) findViewById(R.id.btnGuardar);
        Button poster = (Button) findViewById(R.id.btnPoster);
        Button otros = (Button) findViewById(R.id.btnOtros);

        contraste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContraste();
            }
        });
        alien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAlien();
            }
        });
        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPoster();
            }
        });
        distorsion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDistorsion();
            }
        });
        contraste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });
        otros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    //metodo para aplicar el contraste a una imagen
    public void setContraste(){

    }
    //metodo para aplicar distorsion a una imagen
    public void setDistorsion(){

    }
    //metodo para cambiar el color de piel a una imagen
    public void setAlien(){

    }
    //metodo para aplicar el efecto poster a una imagen
    public void setPoster(){

    }
    //metodo para guardar una imagen
    public void guardar(){

    }
}
