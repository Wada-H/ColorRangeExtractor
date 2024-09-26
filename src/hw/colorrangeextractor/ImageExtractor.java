package hw.colorrangeextractor;

import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.ImageCanvas;
import ij.plugin.Duplicator;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import javafx.scene.paint.Color;

import java.util.stream.IntStream;

public class ImageExtractor {

    ImagePlus mainImage;
    int width;
    int height;
    int maxValue = 255;

    public ImageExtractor(ImagePlus imp){
        mainImage = imp;
        width = mainImage.getWidth();
        height = mainImage.getHeight();
    }

    public ImagePlus extractFromRGB(Color lowColor, Color highColor){
        //mainImage.killRoi(); //killしないほうがいいかも
        ImageStack stackImage = mainImage.getImageStack().duplicate(); //1.52nのImagePlusのduplicate()の仕様変更に合わせてくるかも
        IntStream intStream = IntStream.range(0, stackImage.getSize());

        intStream.parallel().forEach(i ->{
            ColorProcessor cp = (ColorProcessor) stackImage.getProcessor(i+1);

            for(int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    java.awt.Color color = cp.getColor(x, y);
                    boolean rb = this.checkReagion(lowColor.getRed()*maxValue, highColor.getRed()*maxValue, color.getRed());
                    boolean gb = this.checkReagion(lowColor.getGreen()*maxValue, highColor.getGreen()*maxValue, color.getGreen());
                    boolean bb = this.checkReagion(lowColor.getBlue()*maxValue, highColor.getBlue()*maxValue, color.getBlue());

                    if(!rb||!gb||!bb){
                        cp.set(x, y, 0xff000000); //black
                    }
                }
            }
        });
        ImagePlus result = new ImagePlus();
        result.setTitle("Extracted Image");
        result.setStack(stackImage);
        return result;
    }

    private boolean checkReagion(double low, double high, double value){
        boolean b = false;
        if((low <= value)&&(value <= high)){
            b = true;
        }
        return b;
    }


}
