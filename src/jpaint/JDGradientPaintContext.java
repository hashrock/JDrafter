/*
 * JDGradientPaintContext.java
 *
 * Created on 2007/02/10, 8:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jpaint;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

/**
 *
 * @author TK
 */
public class JDGradientPaintContext implements PaintContext{
    private ColorModel colorModel;
    private WritableRaster raster;
    private int gMode;
    private int[] pixColor;
    private Point2D p1,p2;
    private AffineTransform transform;
    private double x1,y1,dx,dy,vectorLength;
    

    public JDGradientPaintContext(ColorModel cm,
				Point2D p1, Point2D p2, AffineTransform xform,
				Color c1, Color c2, float[] ctrls,Color[] colors,int gMode) {
        
        colorModel=ColorModel.getRGBdefault();
        raster=null;
        this.p1=p1;
        this.p2=p2;
        this.transform=xform;
        this.gMode=gMode;
        //
        Point2D xvec = new Point2D.Double(1, 0);
	Point2D yvec = new Point2D.Double(0, 1);
	try {
	    AffineTransform inverse = xform.createInverse();
	    inverse.deltaTransform(xvec, xvec);
	    inverse.deltaTransform(yvec, yvec);
	} catch (NoninvertibleTransformException e) {
	    xvec.setLocation(0, 0);
	    yvec.setLocation(0, 0);
	}
        // Now calculate the (square of the) user space distance
	// between the anchor points. This value equals:
	//     (UserVec . UserVec)
	double udx = p2.getX() - p1.getX();
	double udy = p2.getY() - p1.getY();
	double ulenSq = udx * udx + udy * udy;

	if (ulenSq <= Double.MIN_VALUE) {
	    dx = 0;
	    dy = 0;
            vectorLength=0;
	} else {
            vectorLength=Math.sqrt(ulenSq);
            if (gMode==JDGradientPaint.LINEAR){
                dx = (xvec.getX() * udx + xvec.getY() * udy) / ulenSq;
                dy = (yvec.getX() * udx + yvec.getY() * udy) / ulenSq;
                /*if (dx < 0) {
                    Point2D p = p1; p1 = p2; p2 = p;
                    Color c = c1; c1 = c2; c2 = c;
                    dx = -dx;
                    dy = -dy;
                }*/
            }else{
                dx=xvec.getX()+xvec.getY();
                dy=xvec.getX()+xvec.getY();
            }
	}
        //
        int arraySize=2+((ctrls == null)?0:ctrls.length);
        float[] cp=new float[arraySize];
        Color[] cc=new Color[arraySize];
        cp[0]=0;
        cp[arraySize-1]=1;
        cc[0]=c1;
        cc[arraySize-1]=c2;
        if (arraySize>2){
            for (int i=1;i<arraySize-1;i++){
                cp[i]=ctrls[i-1];
                cc[i]=colors[i-1];
            }
        }
        pixColor=new int[(arraySize-1)*256];
        int pixSize=pixColor.length;
        int a1,r1,g1,b1,da,dr,dg,db;
        int rgb1,rgb2;
        int idx=0;
        int lastIndex=0;        
        while (idx<arraySize-1){
            rgb1 = cc[idx].getRGB();
            rgb2 = cc[idx+1].getRGB();
            a1 = (rgb1 >> 24) & 0xff;
            r1 = (rgb1 >> 16) & 0xff;
            g1 = (rgb1 >>  8) & 0xff;
            b1 = (rgb1      ) & 0xff;
            da = ((rgb2 >> 24) & 0xff) - a1;
            dr = ((rgb2 >> 16) & 0xff) - r1;
            dg = ((rgb2 >>  8) & 0xff) - g1;
            db = ((rgb2      ) & 0xff) - b1;      
        
            int distance=(int)((cp[idx+1]-cp[idx])*pixSize);
            for (int i=0;i<distance;i++){
                float rel=i/((float)distance);
                int rgb =
                    (((int) (a1 + da * rel)) << 24) |
                    (((int) (r1 + dr * rel)) << 16) |
                    (((int) (g1 + dg * rel)) <<  8) |
                    (((int) (b1 + db * rel))      );
                pixColor[lastIndex++]=rgb;
            }
            idx++;        
        }
        int[] pc =new int[lastIndex-1];
        if (dx < 0 && gMode==JDGradientPaint.LINEAR) {
            Point2D p = p1; p1 = p2; p2 = p;
            Color c = c1; c1 = c2; c2 = c;
            dx = -dx;
            dy = -dy;
            for (int i=0;i<pc.length;i++){
                pc[pc.length-1-i]=pixColor[i];
            }
        }else{
            for (int i=0;i<pc.length;i++){
                pc[i]=pixColor[i];
            }
        }
        pixColor=pc;
	Point2D dp1 = xform.transform(p1, null);
	this.x1 = dp1.getX();
	this.y1 = dp1.getY();

    }
    public void dispose() {
        raster=null;
    }

    public ColorModel getColorModel() {
        return colorModel;
    }

    public Raster getRaster(int x, int y, int w, int h) {
        if (raster!=null){
            if (w>raster.getWidth() || h > raster.getHeight()){
                raster=colorModel.createCompatibleWritableRaster(w,h);
            }
        }else{
            raster=colorModel.createCompatibleWritableRaster(w,h);
        }
        SinglePixelPackedSampleModel model=(SinglePixelPackedSampleModel)raster.getSampleModel();
        DataBufferInt buffer=(DataBufferInt)raster.getDataBuffer();
        int off=model.getOffset(0,0);
        int adjust=model.getScanlineStride()-w;
        int[] pixels = buffer.getData();
        if (gMode==JDGradientPaint.LINEAR)
            fillBuffer(x,y,w,h,pixels,off,adjust);
        else
            fillRadial(x,y,w,h,pixels,off,adjust);
        return raster;
  
    }
    private void fillRadial(int x,int y,int w,int h,int[] pixels,int off, int adjust){
        double deltaX=(x-x1)*dx;
        double deltaY=(y-y1)*dy;
        if (vectorLength==0){
            int rgb=pixColor[pixColor.length-1];
            for (int ay=0;ay<h;ay++){
                for (int ax=0;ax<w;ax++){
                    pixels[off++]=rgb;
                }
                off+=adjust;
            }
            return;
        }
        double len;
        int rgb=pixColor[pixColor.length-1];
        for (int ay=0;ay<h;ay++){
            deltaX=(x-x1)*dx;
            for (int ax=0;ax<w;ax++){
                len=Math.sqrt(deltaX*deltaX+deltaY*deltaY);
                len=len/vectorLength;
                if (len>=1.0)
                    pixels[off++]=rgb;
                else
                    pixels[off++]=pixColor[(int)((pixColor.length)*len)];
                deltaX+=dx;
            }
            deltaY+=dy;
            off+=adjust;
        }
        
    }
    private void fillBuffer(int x, int y,int w,int h,int[] pixels,int off,int adjust){
        double rowrel=(x-x1)*dx+(y-y1)*dy;
        int ax=0,ay=0;
        int[] ip=new int[1];
	while (--h >= 0) {
	    double colrel = rowrel;
	    int j = w;
	    if (colrel <= 0.0) {
		int rgb = pixColor[0];
		do {
		    pixels[off++] = rgb;
		    colrel += dx;
		} while (--j > 0 && colrel <= 0.0);
	    }
	    while (colrel < 1.0 && --j >= 0) {
		pixels[off++] = pixColor[(int) (colrel * pixColor.length)];
		colrel += dx;
	    }
	    if (j > 0) {
		int rgb = pixColor[pixColor.length-1];
		do {
		    pixels[off++] = rgb;
		} while (--j > 0);
	    }

	    off += adjust;
	    rowrel += dy;
        }
        
        
        
    }
}
