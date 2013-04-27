/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package svg.oject;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import svg.attribute.SVGAttributes;

/**
 *
 * @author takashi
 */
public abstract class SVGAbstractGroup extends SVGObject implements Collection<SVGObject> {

    private Vector<SVGObject> children = new Vector<SVGObject>();
    
    protected SVGAbstractGroup() {
    }

    protected SVGAbstractGroup(SVGObject parent) {
        super(parent);
    }
    
    @Override
    public void paint(Graphics2D g){
        if (!isCompiled()){
            compile();
        }
        g=(Graphics2D)g.create();
        AffineTransform tx=null;
        SVGAttributes attributes=getAttributes();
        Shape clip=g.getClip();
        if (isViewBoxDefined() || isViewportDefined()){
            tx=new AffineTransform(viewTransform);
        }
        if (objectTransform !=null){
           if (tx==null){
               tx=objectTransform;
           }else{
               tx.concatenate(objectTransform);
           }           
        }
        if (tx !=null){
            if ((isViewBoxDefined()|| isViewportDefined())){
                Rectangle2D r=getCurrentViewport();
                r=r.createIntersection(g.getClipBounds());
                g.setClip(r);
            }
            g.transform(tx);
        }
        Composite cmp=g.getComposite();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, attributes.getOpacity(this)));
        for (SVGObject child:children)
            child.paint(g);
       g.dispose();
    }
  
    @Override
    public Rectangle2D getBounds(){
        Rectangle2D r=null;
        for (SVGObject obj:this){
            Rectangle2D ra=obj.getBounds();
            if (ra != null){
                if (r==null)
                    r=ra;
                else
                    r.add(ra);
            }
        }
        return r;
    }
    @Override
    public Iterator<SVGObject> iterator() {
        return children.iterator();
    }

    @Override
    public int size() {
        return children.size();
    }

    @Override
    public boolean isEmpty() {
        return children.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return children.contains(o);
    }

    @Override
    public Object[] toArray() {
        return children.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return children.toArray(a);
    }

    @Override
    public boolean add(SVGObject e) {
        e.setParent(this);
        return children.add(e);
    }

    public void add(int i, SVGObject e) {
        e.setParent(this);
        children.add(i, e);
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof SVGObject && contains(o)) {
            ((SVGObject) o).setParent(null);
        }
        return children.remove(o);
    }

    public SVGObject remove(int i) {
        SVGObject o = children.remove(i);
        if (o != null) {
            o.setParent(null);
        }
        return o;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return children.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends SVGObject> c) {
        for (SVGObject o : c) {
            o.setParent(this);
        }
        return children.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        for (Object o : c) {
            if (contains(o) && o instanceof SVGObject) {
                ((SVGObject) o).setParent(null);
            }
        }
        return children.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return children.retainAll(c);
    }

    @Override
    public void clear() {
        for (SVGObject o : children) {
            o.setParent(null);
        }
        children.clear();
    }
    
}
