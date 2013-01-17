package jdraw.typemenus;

import java.awt.Font;
import javax.swing.JCheckBoxMenuItem;

public class FontSubMenu extends JCheckBoxMenuItem{
    String familyName;
    public FontSubMenu(String familyName){
        this.familyName=familyName;
        setText(familyName);
        this.setFont(new Font(familyName, Font.PLAIN, 14));
    }
    public String getFamilyName(){
        return familyName;
    }
}