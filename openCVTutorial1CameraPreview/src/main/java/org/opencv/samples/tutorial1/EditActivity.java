package org.opencv.samples.tutorial1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Vector;

/**
 * Created by Malpica on 26/02/2016.
 */
public class EditActivity extends Activity {

    private double alpha=0.5;
    private double beta= 0;
    ImageView ev;
    private double thresh = 70;    //cuanto menor es este valor, mas colores hay en la imagen
    private double maxval = 255;    //???
    //private int thresholdType = Imgproc.THRESH_BINARY;    //original
    private int thresholdType = Imgproc.THRESH_TOZERO;      //este va bien
    private double distortion = -0.000001;

    Dialog dialog;
    Dialog dialogColor;
    Dialog dialogDistorsion;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.captured_layout);

        //set layout
        ImageView iv = (ImageView)findViewById(R.id.originalImage);
        ev = (ImageView)findViewById(R.id.editedImage);
        iv.setImageBitmap(Bitmap.createScaledBitmap(Mapas.original, Mapas.original.getWidth() / 2,
                Mapas.original.getHeight() / 2, false));
        ev.setImageBitmap(Bitmap.createScaledBitmap(Mapas.original, (Mapas.original.getWidth() / 2) - 2,
                (Mapas.original.getHeight() / 2) - 2, false));

        Button contraste = (Button) findViewById(R.id.btnContraste);
        Button alien = (Button) findViewById(R.id.btnAlien);
        Button distorsion = (Button) findViewById(R.id.btnDistorsion);
        Button guardar = (Button) findViewById(R.id.btnGuardar);
        Button poster = (Button) findViewById(R.id.btnPoster);
//        Button otros = (Button) findViewById(R.id.btnOtros);
        inicializacion();
        contraste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
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
                dialogColor.show();
                setPoster();
            }
        });
        distorsion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDistorsion();
            }
        });
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });
    }

    public void inicializacion(){
        //dialogo contraste
        dialog = new Dialog(EditActivity.this);
        dialog.setContentView(R.layout.slider_contraste);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle("Ajustes de Contraste");
        dialog.getWindow().setDimAmount(0);
        //dialogo colores
        dialogColor = new Dialog(EditActivity.this);
        dialogColor.setContentView(R.layout.slider_colores);
        dialogColor.getWindow().setGravity(Gravity.BOTTOM);
        dialogColor.setCanceledOnTouchOutside(true);
        dialogColor.setTitle("Ajustes de Colores");
        dialogColor.getWindow().setDimAmount(0);
        //dialogo distorsion
        dialogDistorsion = new Dialog(EditActivity.this);
        dialogDistorsion.setContentView(R.layout.slider_distorsion);
        dialogDistorsion.getWindow().setGravity(Gravity.BOTTOM);
        dialogDistorsion.setCanceledOnTouchOutside(true);
        dialogDistorsion.setTitle("Ajustes de Distorsion");
        dialogDistorsion.getWindow().setDimAmount(0);
    }

    //muestra el layout para los ajustes de contraste
    public void setContraste(){
        SeekBar contraste = (SeekBar) dialog.getWindow().findViewById(R.id.sliderContraste);
        SeekBar iluminacion = (SeekBar) dialog.getWindow().findViewById(R.id.sliderIluminacion);
        contraste.setMax(70);
        iluminacion.setMax(100);
        contraste.setProgress(((int) alpha * 100) - 30);
        Log.e("CONTRASTE", "progresso ajustado a: " + alpha * 100);
        iluminacion.setProgress((int) beta);
        //http://stackoverflow.com/questions/9467026/change-dialog-position-on-the-screen
        contraste.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                alpha = (progress + 30) / 100.0;
                Log.e("CONTRASTE", "alpha cambiado a(/100): " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                contraste();
                Log.e("CONTRASTE", "Contraste cambiado a: " + alpha);
            }
        });
        contraste.setSaveEnabled(true); //saves the state of the slider
        //contraste.setSaveFromParentEnabled(true);
        //contraste.setMax(1);    //max alpha
        iluminacion.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                beta = progress;
                Log.e("CONTRASTE", "beta cambiado a: " + beta);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                contraste();
                Log.e("CONTRASTE", "Contraste cambiado a: " + beta);
            }
        });
        iluminacion.setSaveEnabled(true);
    }

    //metodo para aplicar el contraste a una imagen
    public void contraste(){
        Log.e("CONTRASTE", "en contraste: " + alpha + " " + beta);
        Mat temp = Mapas.color.clone();
        //Imgproc.cvtColor(Mapas.color,temp,Imgproc.COLOR_RGB2YCrCb);
        Mat aux = Mat.zeros(temp.rows(),temp.cols(),Imgproc.COLOR_RGB2YCrCb);
        //-1 --> desired output matrix the same as the input one.
        temp.convertTo(aux, -1, alpha, beta);
        refrescar(aux);
        //equalizehistory
        //cambiar canal 0
    }

    public void setDistorsion(){
        dialogDistorsion.show();
        SeekBar distort = (SeekBar) dialogDistorsion.getWindow().findViewById(R.id.sliderDistorsion);
        distort.setMax(200);
        distort.setProgress(100);
        distort.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distortion = (progress-100)/100000000.0;
                Log.e("DISTORTION","valor de distorsion "+distortion);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                distorsion();
            }
        });
    }

    //metodo para aplicar distorsion de barril y de cojin ajustables a una imagen
    public void distorsion(){
        //http://stackoverflow.com/questions/6199636/formulas-for-barrel-pincushion-distortion
        Mat temp = Mapas.color.clone();
        Mat resul = Mat.zeros(temp.size(), temp.type());
        Mat mapx = Mat.zeros(temp.size(), CvType.CV_32FC1);
        Mat mapy = Mat.zeros(temp.size(), CvType.CV_32FC1);
        int w = temp.width();
        int h = temp.height();
        int Cx = w/2;
        int Cy = h/2;
        float[] fmapx = new float[temp.height()*temp.width()];
        float[] fmapy = new float[temp.height()*temp.width()];
        int i=0;
        Log.e("DISTORTION","En distorsion "+distortion);
        for(int y=0; y<h; y++){
            for(int x=0; x<w; x++){
                float u= (float)(Cx+(x-Cx)*(1+distortion*((x-Cx)*(x-Cx)+(y-Cy)*(y-Cy))));
                //mapx.put(x,y,u);
                fmapx[i]=u;
                i++;
            }
        }
        i=0;
        for(int y=0; y<h; y++){
            for(int x=0; x<w; x++){
                float u= (float)(Cy+(y-Cy)*(1+distortion*((x-Cx)*(x-Cx)+(y-Cy)*(y-Cy))));
                //mapy.put(x,y,u);
                fmapy[i]=u;
                i++;
            }
        }
        mapx.put(0,0,fmapx);
        mapy.put(0, 0, fmapy);
        Imgproc.remap(temp, resul, mapx, mapy, Imgproc.INTER_LINEAR, Imgproc.BORDER_CONSTANT, new Scalar(0, 0, 0));
        refrescar(resul);
    }
    //metodo para cambiar el color de piel a una imagen
    public void setAlien(){
        Mat temp = Mapas.color.clone();
        //cambiar rango de color a HSV
        Mat aux= Mat.zeros(temp.rows(),temp.cols(),Imgproc.COLOR_RGB2HSV);
        Mat mascara = aux.clone();
        Imgproc.cvtColor(temp, aux, Imgproc.COLOR_RGB2HSV);

        //obtener matriz blanco negro --> blanco = piel
        Log.e("ALIEN","pre inrange aux "+aux.empty());
        Log.e("ALIEN", "pre inrange aux " + aux.channels());
        Core.inRange(aux, new Scalar(0, 10, 60), new Scalar(20, 150, 255), mascara);
        Imgproc.GaussianBlur(mascara, mascara, new Size(7, 7), 1, 1);
        Log.e("ALIEN", "aux preBlur" + aux.empty());
        Log.e("ALIEN", "aux preBlur" + aux.channels());
        Log.e("ALIEN","aux size "+aux.size());

        //Mat piel=Mat.zeros(aux.size(), CvType.CV_8UC3);
//        Log.e("ALIEN","piel size "+piel.size());
//        Log.e("ALIEN","aux "+aux.empty());
//        Log.e("ALIEN","aux "+aux.channels());
//        Imgproc.cvtColor(aux,piel,Imgproc.COLOR_HSV2RGB);
//        Imgproc.cvtColor(aux,piel,Imgproc.COLOR_HSV2RGB);
//        Mat  nueva = Mat.zeros(temp.size(),CvType.CV_8UC3);
//        Imgproc.cvtColor(temp,nueva,CvType.CV_8UC3);
//        Log.e("ALIEN","piel size "+nueva.size());
//        Log.e("ALIEN","aux "+nueva.empty());
//        Log.e("ALIEN","aux "+nueva.channels());
//        //cambiar color de piel
//        int size = (int)nueva.total() * nueva.channels();
//        double[] tam = new double[size];
//        nueva.get(0,0,tam);
//        for(int i=0; i<tam.length; i++){
//            i++;
//            //if(){
//
//            //}
//            tam[i] = (tam[i]+50)%256;
//            i++;
//        }
//        nueva.put(0,0,tam);
        //refrescar imagen en pantalla
//        Mat image = Mapas.color.clone();
//        Mat converted = Mat.zeros(image.size(), CvType.CV_8SC3);
//        Imgproc.cvtColor(image, converted, Imgproc.COLOR_RGB2HSV);
//        Mat skinMask = Mat.zeros(converted.size(), converted.type());
//        Core.inRange(converted, new Scalar(0, 48, 80), new Scalar(20, 150, 255), skinMask);
//
//        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(1, 1));
//        Imgproc.erode(skinMask, skinMask, kernel);
//        Imgproc.dilate(skinMask, skinMask, kernel);
//
//        Imgproc.GaussianBlur(skinMask, skinMask, new Size(3, 3), 0);


        Vector<Mat> channels = new Vector<Mat>();
        Core.split(aux, channels);

        channels.get(0).setTo(new Scalar(60), mascara);

        Core.merge(channels, aux);

        Imgproc.cvtColor(aux, temp, Imgproc.COLOR_HSV2RGB);

        //return image;

        refrescar(temp);
    }

    public void setPoster(){
        SeekBar colores = (SeekBar) dialogColor.getWindow().findViewById(R.id.sliderColores);
        colores.setMax(255);
        colores.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                thresh = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                poster();
            }
        });
    }

    //metodo para aplicar el efecto poster a una imagen
    //reducir el numero de colores de una imagen
    public void poster(){
        Mat rgb = Mapas.color.clone();
        Vector<Mat> canales = new Vector<Mat>();
        Core.split(rgb, canales);
        //channels 0 --> red
        //channels 2 --> blue
        //channels 1 --> green
        for(int i=0; i<canales.size(); i++){
            //aplicar threshold para eliminar colores
            Imgproc.threshold(canales.elementAt(i),canales.elementAt(i),thresh,maxval,thresholdType);
        }
        Core.merge(canales,rgb);
        refrescar(rgb);
    }
    //metodo para guardar una imagen
    public void guardar(){
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "practica1"+ SystemClock.currentThreadTimeMillis()+".jpg");
        //Bitmap bmp =ev.getDrawingCache();
        Bitmap bmp = Mapas.editado;
        //Utils.matToBitmap(Mapas.color,bmp);
        Dialog d = new Dialog(EditActivity.this);
        try{
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG,100,fos);
            d.setTitle("Imagen guardada en "+file.getAbsolutePath());
        }catch(Exception e){
            Log.e("SAVE","Error al guardar el archivo");
            d.setTitle("Error al guardar el archivo");
            e.printStackTrace();
        }
        d.setCanceledOnTouchOutside(true);
        d.show();
    }
    //metodo para refrescar imagen
    public void refrescar(Mat mat){
        Bitmap bmp=Bitmap.createBitmap(Mapas.editado);
        Utils.matToBitmap(mat,bmp);
        Mapas.editado=bmp;
        ev.setImageBitmap(Bitmap.createScaledBitmap(bmp, (Mapas.original.getWidth() / 2)-2,
                (Mapas.original.getHeight() / 2)-2, false));
    }
}
