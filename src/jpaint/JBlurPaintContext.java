package jpaint;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
/*
 * JShadowPaintContext.java
 *
 * Created on 2007/11/24, 21:15
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author admin
 */
public class JBlurPaintContext implements PaintContext{
    public static int RIGHT=0;
    public static int LEFT=1;
    public static int DOWN=2;
    private ColorModel cm;
    private Shape s;
    private GeneralPath flattenedShape;
    private Rectangle bdRect;
    private int radius;
    private float alpha;
    private Color baseColor;
    private AffineTransform xForm;
    private int[] rgbs=null;
    private WritableRaster raster=null;
    private static float maxRadius=50;
    /** Creates a new instance of JShadowPaintContext */
    public JBlurPaintContext(Shape s,float radius,Color baseColor,float alpha,AffineTransform xForm) {
        //setup Values;
        this.xForm=xForm;
        this.s=s;
        //this.radius=(int)(radius*xForm.getScaleX());
        this.baseColor=baseColor;
        this.alpha=alpha;
        this.radius=(int)(Math.round(radius*xForm.getScaleX()));
        flattenedShape=new GeneralPath();
        flattenedShape.append(s.getPathIterator(xForm,0.1f),false);
        bdRect=flattenedShape.getBounds();
        cm=ColorModel.getRGBdefault();
        //Initialize Color;
        rgbs=new int[256];
        int red=(baseColor.getRed()<<16) & 0xff0000;
        int green=(baseColor.getGreen()<<8) & 0xff00;
        int blue=(baseColor.getBlue()) & 0xff;
        for (int i=0;i<256;i++){
            int a=((int)(i*alpha)<<24) & 0xff000000;
            rgbs[i]=a|red|green|blue;
        }
    }
    @Override
    public void dispose() {
        raster=null;
    }
    
    @Override
    public ColorModel getColorModel() {
        return cm;
    }
    
    @Override
    public Raster getRaster(int x, int y, int w, int h) {
        if (raster!=null){
            if (raster.getWidth()<w || raster.getHeight()<h){
                raster=cm.createCompatibleWritableRaster(w,h);
            }
        }else{
            raster=cm.createCompatibleWritableRaster(w,h);
        }
        DataBufferInt buffer=(DataBufferInt)raster.getDataBuffer();
        SinglePixelPackedSampleModel model=(SinglePixelPackedSampleModel)raster.getSampleModel();
        int off=model.getOffset(0,0);
        int adjust=model.getScanlineStride();
        int[] pixels=buffer.getData();
        fillPixels(pixels,x,y,w,h,adjust,off);
        return raster;
    }
    byte[] pixBuffer=null;
    int bOffs=0;
    int stride=0;
    private void fillPixels(int[] pixels,int x,int y,int w,int h,int adjust,int off){
        Rectangle dr=new Rectangle();
        dr.setFrame(x-radius,y-radius,w+radius*2,h+radius*2);
        if (!dr.intersects(bdRect)){
            for (int vy=y;vy<y+h;vy++){
                for (int vx=0;vx<w;vx++){
                    pixels[off++]=rgbs[0];
                }
                off+=adjust-w;
            }
            return;
        }
        if (flattenedShape.contains(dr) || radius==0){
            for (int vy=y;vy<y+h;vy++){
                for (int vx=0;vx<w;vx++){
                    pixels[off++]=rgbs[255];
                }
                off+=adjust-w;
            }
            return;
        }
        createBuffer(x,y,w,h);
        int ret=getPixcel(0,0);
        int vx=0,vy=0;
        float l=radius*2;
        float sq=l*l;
        int inc=1;
        int direction=RIGHT;
        while (true){
            while (true){
                float r=ret/sq;
                r*=r;
                pixels[off]=rgbs[(int)(r*255)];
                if (vx+inc<0 || vx+inc>=w) break;
                vx+=inc;
                ret+=shift(vx,vy,direction);
                off+=inc;
                
            }
            vy++;
            if (vy>= h)break;
            ret+=shift(vx,vy,DOWN);
            inc *=-1;
            direction =(direction+1) & 1;
            off+=adjust;
        }
        pixBuffer=null;
    }
    private void createBuffer(int x,int y,int w,int h){
        BufferedImage img=new  BufferedImage((int)(w+2*radius),(int)(h+2*radius),BufferedImage.TYPE_BYTE_INDEXED);
        Graphics2D g=img.createGraphics();
        g.setClip(0,0,(int)(w+2*radius),(int)(h+2*radius));
        g.translate(-x+radius,-y+radius);
        g.setColor(Color.WHITE);
        g.fill(flattenedShape);
        DataBufferByte bt=(DataBufferByte) img.getData().getDataBuffer();
        pixBuffer=bt.getData();
        PixelInterleavedSampleModel model=(PixelInterleavedSampleModel)img.getData().getSampleModel();
        bOffs=model.getOffset(0,0);
        stride=model.getScanlineStride();
        
    }
    private int getPixcel(int x,int y){
        int l=2*radius;
        int ret=0;
        int idx=stride*y+bOffs+x;
        int adjust=stride;
        int mask=0x80>>((bOffs+x)%8);
        for (int vy=0;vy<l;vy++){
            for (int vx=0;vx<l;vx++){
                if (pixBuffer[idx++] != 0)
                    ret++;
            }
            idx+=(stride-l);
        }
        return ret;
    }
    private int shift(int x,int y,int direction){
        int l=(int)(radius+radius);
        int ret=0;
        int idx1,idx2;
        if (direction==RIGHT){
            idx1=bOffs+stride*y+x-1;
            idx2=idx1+l;
            for (int vy=0;vy<l;vy++){
                if (pixBuffer[idx1] !=0) ret--;
                if (pixBuffer[idx2]!=0) ret++;
                idx1+=stride;
                idx2+=stride;
            }
        } else if (direction==LEFT){
            idx1=bOffs+stride*y+x;
            idx2 = idx1+l;
            for (int vy=0;vy<l;vy++){
                if (pixBuffer[idx1]!=0) ret++;
                if (pixBuffer[idx2]!=0) ret--;
                idx1+=stride;
                idx2+=stride;
            }
            
        } else{
            idx1=bOffs+stride*(y-1)+x;
            idx2=idx1+l*stride;
            for (int vy=0;vy<l;vy++){
                if (pixBuffer[idx1++]!=0) ret--;
                if (pixBuffer[idx2++]!=0) ret++;
            }
        }
        return ret;
    }
}
