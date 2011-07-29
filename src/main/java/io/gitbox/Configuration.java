package io.gitbox;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

/**
 * @author Jean-Baptiste lemée
 */
public  class Configuration {

   public static ImageData getIcon(){
      return new ImageData(Configuration.class.getClassLoader().getResourceAsStream("051.gif"));
   }
}
