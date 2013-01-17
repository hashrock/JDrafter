/*
 * JDNumericTextField.java
 *
 * Created on 2007/02/03, 10:55
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package jtools.jcontrol;
import javax.swing.*;
import javax.swing.event.*;
import java.text.*;
import java.util.*;
/**
 *
 * @author TK
 */
public class JDNumericTextField extends JFormattedTextField{
    
    /** Creates a new instance of JDNumericTextField */
    private Vector<ChangeListener> changeListeners;
    private String format;
    private Number minValue,maxValue;

    public JDNumericTextField() { 
        format=null;
        minValue=null;
        maxValue=null;
        changeListeners=new Vector<ChangeListener>();
        this.setFormatterFactory(new myFormatterFactory());
    }
    public void addChangeListener(ChangeListener listener){
        if (changeListeners.contains(listener))
            return;
        changeListeners.add(listener);
    }
        
    public void removeChangeListener(ChangeListener listener){
        changeListeners.remove(listener);
    }
    public void setFormat(String f){
        format=f;
    }
    public double getDoubleValue(){
        if (getValue()==null) return 0;
        if (getValue() instanceof Number){
            Number n=(Number)getValue();
            return n.doubleValue();
        }
        return 0;
    }
    public float getFloatValue(){
        return ((float)getDoubleValue());
    }
    public void setDoubleValue(double d){
        setValue(new Double(d));
    }
    public void setFloatValue(float f){
        setValue(new Double((double)f));
    }
    public void setMinValue(Number d){
        minValue=d;
    }
    public Number getMinValue(){
        return minValue;
    }
    public void setMaxValue(Number d){
        maxValue=d;
    }
    public Number getMaxValue(){
        return maxValue;
    }
    private void fireChangeEvent(){
        ChangeEvent e=new ChangeEvent(this);
        for (int i=0;i<changeListeners.size();i++)
            changeListeners.get(i).stateChanged(e);
    }
    @Override
    public void commitEdit() throws java.text.ParseException{
        try{
            super.commitEdit();
        }catch (java.text.ParseException e){
            throw e;
        }
        fireChangeEvent();
    }
    class myFormatterFactory extends JFormattedTextField.AbstractFormatterFactory{
        myFormatter formatter;
        public myFormatterFactory(){
            formatter=new myFormatter();
        }
        public JFormattedTextField.AbstractFormatter getFormatter(JFormattedTextField tf) {
            return formatter;
        }
        
    }
    class myFormatter extends JFormattedTextField.AbstractFormatter{
        @Override
        public Object stringToValue(String text) throws ParseException {
            Double ret=null;
            String c=text.trim();
            if (c=="") c="0";
            try {
                ret=Double.parseDouble(c);
            }catch(NumberFormatException e){
                throw new ParseException(e.getMessage(),0);
            }
            if (minValue !=null && minValue.doubleValue()>ret.doubleValue()){
                throw new ParseException("",0);
            }
            if (maxValue !=null && maxValue.doubleValue()<ret.doubleValue()){
                throw new ParseException("",0);
            }
            return ret;
        }
        @Override
        public String valueToString(Object value) throws ParseException {
            if (!(value instanceof Double))
                throw new ParseException("NumelicError",0);
            Double vl=(Double)value;
            if (format==null)
                return String.format("%5.4g",vl.doubleValue());
            else
                return String.format(format,vl.doubleValue());
        }
        
    }
    
}
