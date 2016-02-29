package org.opencv.samples.tutorial1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
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

import java.util.Vector;

/**
 * Created by Malpica on 26/02/2016.
 */
public class EditActivity extends Activity {

    private double alpha=0.5;
    private double beta= 0;
    //private double alienLower = 0.28;
    //private double alienUpper = 0.68;
    ImageView ev;
    private double thresh = 70;    //cuanto menor es este valor, mas colores hay en la imagen
    private double maxval = 255;    //???
    private int thresholdType = Imgproc.THRESH_BINARY;
    private double distortion = 0.000001;

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
        ev.setImageBitmap(Bitmap.createScaledBitmap(Mapas.original,(Mapas.original.getWidth()/2)-2,
                (Mapas.original.getHeight()/2)-2,false));

        Button contraste = (Button) findViewById(R.id.btnContraste);
        Button alien = (Button) findViewById(R.id.btnAlien);
        Button distorsion = (Button) findViewById(R.id.btnDistorsion);
        Button guardar = (Button) findViewById(R.id.btnGuardar);
        Button poster = (Button) findViewById(R.id.btnPoster);
//        Button otros = (Button) findViewById(R.id.btnOtros);

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
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardar();
            }
        });
//        otros.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    //muestra el layout para los ajustes de contraste
    public void setContraste(){
        //dialog
        Dialog dialog = new Dialog(EditActivity.this);
        dialog.setContentView(R.layout.slider_contraste);
        SeekBar contraste = (SeekBar) findViewById(R.id.sliderContraste);
        SeekBar iluminacion = (SeekBar) findViewById(R.id.sliderIluminacion);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        //http://stackoverflow.com/questions/9467026/change-dialog-position-on-the-screen
        //dialog.getWindow().addFlags(~WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        /*contraste.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });*/
        //contraste.setMax(1);    //max alpha
        //contraste.setSaveEnabled(true); //saves the state of the slider

        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle("Contraste");
        dialog.show();
    }

    //metodo para aplicar el contraste a una imagen
    public void contraste(){
        Mat temp = Mapas.color.clone();
        //Imgproc.cvtColor(Mapas.color,temp,Imgproc.COLOR_RGB2YCrCb);
        Mat aux = Mat.zeros(temp.rows(),temp.cols(),Imgproc.COLOR_RGB2YCrCb);
        //-1 --> desired output matrix the same as the input one.
        temp.convertTo(aux,-1,alpha,beta);
        refrescar(temp);
        //equalizehistory

        //cambiar canal 0
    }

    //metodo para aplicar distorsion de barril y de cojin ajustables a una imagen
    public void setDistorsion(){
        //http://stackoverflow.com/questions/6199636/formulas-for-barrel-pincushion-distortion
        Mat temp = Mapas.color.clone();
        Mat resul = Mat.zeros(temp.size(), temp.type());
        Mat mapx = Mat.zeros(temp.size(), CvType.CV_32FC1);
        Mat mapy = Mat.zeros(temp.size(), CvType.CV_32FC1);
        int w = temp.width();
        int h = temp.height();
        int Cx = w/2;
        int Cy = h/2;
        for(int y=0; y<h; y++){
            for(int x=0; x<w; x++){
                double u= Cx+(x-Cx)*(1+distortion*((x-Cx)*(x-Cx)+(y-Cy)*(y-Cy)));
                mapx.put(x,y,u);
            }
        }
        for(int y=0; y<h; y++){
            for(int x=0; x<w; x++){
                double u= Cy+(y-Cy)*(1+distortion*((x-Cx)*(x-Cx)+(y-Cy)*(y-Cy)));
                mapy.put(x,y,u);
            }
        }
        Imgproc.remap(temp,resul,mapx,mapy,Imgproc.INTER_LINEAR,Imgproc.BORDER_CONSTANT,new Scalar(0,0,0));
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
    //metodo para aplicar el efecto poster a una imagen
    //reducir el numero de colores de una imagen
    public void setPoster(){
        Mat rgb = Mapas.color.clone();
        Vector<Mat> canales = new Vector<Mat>();
        Core.split(rgb,canales);
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
