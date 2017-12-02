package de.tu.darmstadt.es.xtext.utils.ui.highlighting.utils;

import org.eclipse.swt.graphics.RGB;

import de.tu.darmstadt.es.xtext.utils.ui.highlighting.AbstractHighlightingConfiguration;

public class XtextColorManager
{
   private AbstractHighlightingConfiguration config;
   
   public XtextColorManager()
   {
      
   }
   
   public void setConfig(AbstractHighlightingConfiguration config) {
	   this.config = config;
   }
   
   public RGB getColor(XtextColor color){
      switch(color){
      case BLACK:
         return new RGB(0,0,0);
      case BLUE:
         return new RGB(0,0,255);
      case BROWN:
         return new RGB(205, 133, 63);
      case DARK_BLUE:
         return new RGB(0,0,128);
      case DARK_RED:
         return new RGB(128,0,0);
      case GREEN:
         return new RGB(0,255,0);
      case LIGHT_BLUE:
         return new RGB(128,128,255);
      case LIGHT_RED:
         return new RGB(255,128,128);
      case ORANGE:
         return new RGB(255, 165, 0);
      case RED:
         return new RGB(255,0,0);
      case VIOLETT:
         return new RGB(149, 0, 85);
      case WHITE:
         return new RGB(255,255,255);
      case YELLOW:
         return new RGB(255, 255, 0);
      case DARK_GREEN:
         return new RGB(0,128,0);
      case LIGHT_GREEN:
         return new RGB(128,255,128);
      case GRAY:
         return new RGB(128,128,128);
      case DARK_GOLD:
         return new RGB(205,173,0);
      case GOLD:
         return new RGB(255,215,0);
      case DARK_ORANGE:
         return new RGB(255, 140, 0);
      case MISTY_ROSE:
         return new RGB(255, 228, 225);
      case LIGHT_YELLOW:
         return new RGB(255, 255, 224);
      case DEFAULT:
         return config.defaultTextStyle().getColor();
      default:
         return null;      
      }
   }
}
