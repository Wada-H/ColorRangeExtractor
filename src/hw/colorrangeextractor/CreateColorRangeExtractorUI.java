package hw.colorrangeextractor;

import ij.ImagePlus;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.controlsfx.control.RangeSlider;

import java.io.IOException;
import java.util.ArrayList;

public class CreateColorRangeExtractorUI extends AnchorPane {
    int chSize;
    int zSize;
    int tSize;
    int imgWidth;
    int imgHeight;
    int imgDepth;


    String fileName;
    ImagePlus mainImage;
    ImagePlus previewImage;


    int currentC;
    int currentZ;
    int currentT;


    String title;
    String imageFileDir;
    String imageFileName;


    Scene scene;
    JFXPanel jfxPanel;
    FXMLLoader loader;


    @FXML private Button createImageButton;

    @FXML public RangeSlider redRangeSlider;
    @FXML public RangeSlider greenRangeSlider;
    @FXML public RangeSlider blueRangeSlider;

    @FXML public TextField redLow;
    @FXML public TextField redHigh;
    @FXML public TextField greenLow;
    @FXML public TextField greenHigh;
    @FXML public TextField blueLow;
    @FXML public TextField blueHigh;

    @FXML public ColorPicker lowerPicker;
    @FXML public ColorPicker higherPicker;



    public CreateColorRangeExtractorUI(ImagePlus img, String file_name){
        fileName = file_name;
        mainImage = img;
        if(mainImage.getOriginalFileInfo() != null){
            imageFileName = mainImage.getOriginalFileInfo().fileName;
            imageFileDir = mainImage.getOriginalFileInfo().directory;

        }else{
            imageFileName = mainImage.getTitle();
            imageFileDir = System.getProperty("user.home");

        }

        // 何かの拍子でnullになると保存ができなくなるので保険 //
        if(imageFileName == null) {
            imageFileName = "NewData";
        }
        if(imageFileDir == null) {
            imageFileDir = "./";
        }
        //


        this.getBasicInformation();
    }


    public void getBasicInformation(){
        chSize = mainImage.getNChannels();
        zSize = mainImage.getNSlices();
        tSize = mainImage.getNFrames();
        imgWidth = mainImage.getWidth();
        imgHeight = mainImage.getHeight();
        imgDepth = mainImage.getBitDepth();

    }

    public JFXPanel getFXML(){
        Pane result = new Pane();
        jfxPanel = new JFXPanel();
        loader = new FXMLLoader();
        loader.setRoot(this);
        loader.setController(this);
        //loader.setController(new Test()); //こんな書き方でもいける。ただし、今回の場合は分離するほうが面倒
        try {
            //result = FXMLLoader.load(getClass().getResource(fileName));
            result = loader.load(getClass().getResourceAsStream(fileName));

            scene = new Scene(result,result.getPrefWidth(),result.getPrefHeight());
            jfxPanel.setScene(scene);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        this.setIventListener();
        this.setInitialValue();
        return jfxPanel;
    }


    private void setIventListener(){

        ArrayList<RangeSlider> rangeSliders = new ArrayList<RangeSlider>();
        ArrayList<TextField> lowFields = new ArrayList<TextField>();
        ArrayList<TextField> highFields = new ArrayList<TextField>();

        rangeSliders.add(redRangeSlider);
        rangeSliders.add(greenRangeSlider);
        rangeSliders.add(blueRangeSlider);

        lowFields.add(redLow);
        lowFields.add(greenLow);
        lowFields.add(blueLow);

        highFields.add(redHigh);
        highFields.add(greenHigh);
        highFields.add(blueHigh);

        this.setOnScrolltoFields(lowFields, highFields, rangeSliders);

    }

    private void setInitialValue(){
        this.setLowerPickerColor(Color.BLACK);
        redRangeSlider.setHighValue(255);
        greenRangeSlider.setHighValue(255);
        blueRangeSlider.setHighValue(255);

    }


    private void setLowerPickerColor(Color c){
        Platform.runLater(()->lowerPicker.setValue(c));

    }


    private void setOnScrolltoFields(ArrayList<TextField> lowFields, ArrayList<TextField> highFields, ArrayList<RangeSlider> rangeSliders){
        for(int i = 0; i < lowFields.size(); i++){
            final int index = i;
            lowFields.get(i).setOnScroll(e ->{
                double y = e.getDeltaY();
                int lowV = Integer.valueOf(lowFields.get(index).getText());
                int highV = Integer.valueOf(highFields.get(index).getText());

                if(y > 0){
                    lowV += 1;

                }else{
                    lowV -= 1;
                }

                int[] checkedV = this.checkValue(lowV, highV,1);

                lowFields.get(index).setText(String.valueOf(checkedV[0]));
                rangeSliders.get(index).setLowValue((double)checkedV[0]);

                this.changeLowPickerColorWithFields();
                if(previewImage != null){
                    this.createPreview();
                }

            });


            highFields.get(i).setOnScroll(e ->{
                double y = e.getDeltaY();
                int lowV = Integer.valueOf(lowFields.get(index).getText());
                int highV = Integer.valueOf(highFields.get(index).getText());

                if(y > 0){
                    highV += 1;
                }else{
                    highV -= 1;
                }
                int[] checkedV = this.checkValue(lowV, highV, 0);

                highFields.get(index).setText(String.valueOf(checkedV[1]));
                rangeSliders.get(index).setHighValue((double)checkedV[1]);

                this.changeHighPickerColorWithFields();
                if(previewImage != null){
                    this.createPreview();
                }
            });

            rangeSliders.get(i).setOnScroll(e ->{
                double y = e.getDeltaY();
                int lowV = Integer.valueOf(lowFields.get(index).getText());
                int highV = Integer.valueOf(highFields.get(index).getText());


                int sub = highV - lowV;
                if((lowV == 0)&&(highV == 255)) {

                }else if((lowV == 0)){
                    if(y > 0){
                        lowV += 1;
                        highV += 1;
                    }
                }else if(highV == 255) {
                    if (y < 0) {
                        lowV -= 1;
                        highV -= 1;
                    }
                }else{
                    if(y > 0){
                        lowV += 1;
                        highV += 1;
                    }else{
                        lowV -= 1;
                        highV -= 1;
                    }
                }


                lowFields.get(index).setText(String.valueOf(lowV));
                rangeSliders.get(index).setLowValue((double)lowV);

                highFields.get(index).setText(String.valueOf(highV));
                rangeSliders.get(index).setHighValue((double)highV);

                this.changeHighPickerColorWithFields();
                if(previewImage != null){
                    this.createPreview();
                }

            });

        }

    }




    @FXML
    public void mouseReleasedRedRangeSlider(){
        double[] position = this.getRedSliderPosition();
        this.setRedSliderValue(position[0], position[1]);
        this.changeHighPickerColorWithFields();
        this.changeLowPickerColorWithFields();
        if(previewImage != null){
            this.createPreview();
        }    }

    @FXML
    public void mouseReleasedGreenRangeSlider(){
        double[] position = this.getGreenSliderPosition();
        this.setGreenSliderValue(position[0], position[1]);
        this.changeHighPickerColorWithFields();
        this.changeLowPickerColorWithFields();
        if(previewImage != null){
            this.createPreview();
        }
    }

    @FXML
    public void mouseReleasedBlueRangeSlider(){
        double[] position = this.getBlueSliderPosition();
        this.setBlueSliderValue(position[0], position[1]);
        this.changeHighPickerColorWithFields();
        this.changeLowPickerColorWithFields();
        if(previewImage != null){
            this.createPreview();
        }
    }

    private double[] checkValue(double low, double high, double inf, double sup, int priority){
        double[] result = new double[2];

        if(low < inf){
            low = inf;
        }

        if(high > sup){
            high = sup;
        }

        if(priority == 0){
            if(high < low){
                high = low;
            }
        }else if(priority == 1){
            if(low > high){
                low = high;
            }
        }


        result[0] = low;
        result[1] = high;

        return result;
    }

    private int[] checkValue(int low, int high, int priority){
        double[] d = this.checkValue((double)low, (double) high, priority);
        int[] result = {(int)d[0], (int)d[1]};
        return result;
    }

    private double[] checkValue(double low, double high, int priority){
        double[] d = this.checkValue(low, high, 0.0, 255.0, priority);
        return d;
    };


    //R//
    public void setRedSliderValue(double low, double high){
        redLow.setText(String.valueOf((int)low));
        redHigh.setText(String.valueOf((int)high));
    }



    public double[] getRedSliderPosition(){
        double[] position = {redRangeSlider.getLowValue(), redRangeSlider.getHighValue()};
        return position;
    }

    //G//
    public void setGreenSliderValue(double low, double high){
        greenLow.setText(String.valueOf((int)low));
        greenHigh.setText(String.valueOf((int)high));
    }


    public double[] getGreenSliderPosition(){
        double[] position = {greenRangeSlider.getLowValue(), greenRangeSlider.getHighValue()};
        return position;
    }

    //B//
    public void setBlueSliderValue(double low, double high){
        blueLow.setText(String.valueOf((int)low));
        blueHigh.setText(String.valueOf((int)high));
    }


    public double[] getBlueSliderPosition(){
        double[] position = {blueRangeSlider.getLowValue(), blueRangeSlider.getHighValue()};
        return position;
    }


    public double[] getLowerRGB(){
        double[] v = new double[3];

        v[0] = Double.valueOf(redLow.getText())/255;
        v[1] = Double.valueOf(greenLow.getText())/255;
        v[2] = Double.valueOf(blueLow.getText())/255;

        return v;
    }

    public double[] getHigherRGB(){
        double[] v = new double[3];

        v[0] = Double.valueOf(redHigh.getText())/255;
        v[1] = Double.valueOf(greenHigh.getText())/255;
        v[2] = Double.valueOf(blueHigh.getText())/255;

        return v;
    }



    public void changeLowPickerColorWithFields(){
        double[] rgb = this.getLowerRGB();
        Color c = new Color(rgb[0], rgb[1], rgb[2],1);
        lowerPicker.setValue(c);
    }

    public void changeHighPickerColorWithFields(){
        double[] rgb = this.getHigherRGB();
        Color c = new Color(rgb[0], rgb[1], rgb[2],1);
        higherPicker.setValue(c);
    }


    @FXML
    public void changeRedLowValue(){
        int v = Integer.valueOf(redLow.getText());
        redRangeSlider.setLowValue(v);
        redLow.setText(String.valueOf((int)redRangeSlider.getLowValue()));
        this.changeLowPickerColorWithFields();
    }

    @FXML
    public void changeRedHighValue(){
        int v = Integer.valueOf(redHigh.getText());
        redRangeSlider.setHighValue(v);
        redHigh.setText(String.valueOf((int)redRangeSlider.getHighValue()));
        this.changeHighPickerColorWithFields();
    }


    @FXML
    public void changeGreenLowValue(){
        int v = Integer.valueOf(greenLow.getText());
        greenRangeSlider.setLowValue(v);
        greenLow.setText(String.valueOf((int)greenRangeSlider.getLowValue()));
        this.changeLowPickerColorWithFields();

    }

    @FXML
    public void changeGreenHighValue(){
        int v = Integer.valueOf(greenHigh.getText());
        greenRangeSlider.setHighValue(v);
        greenHigh.setText(String.valueOf((int)greenRangeSlider.getHighValue()));
        this.changeHighPickerColorWithFields();

    }
    @FXML
    public void changeBlueLowValue(){
        int v = Integer.valueOf(blueLow.getText());
        blueRangeSlider.setLowValue(v);
        blueLow.setText(String.valueOf((int)blueRangeSlider.getLowValue()));
        this.changeLowPickerColorWithFields();

    }

    @FXML
    public void changeBlueHighValue(){
        int v = Integer.valueOf(blueHigh.getText());
        blueRangeSlider.setHighValue(v);
        blueHigh.setText(String.valueOf((int)blueRangeSlider.getHighValue()));
        this.changeHighPickerColorWithFields();

    }

    @FXML
    public void setPickedValueForLowFields(){
        double r = lowerPicker.getValue().getRed() * 255;
        double g = lowerPicker.getValue().getGreen() * 255;
        double b = lowerPicker.getValue().getBlue() * 255;

        //　上限下限を超えないため一旦 sliderへ入力
        redRangeSlider.setLowValue((int)r);
        greenRangeSlider.setLowValue((int)g);
        blueRangeSlider.setLowValue((int)b);

        double rsv = redRangeSlider.getLowValue();
        double gsv = greenRangeSlider.getLowValue();
        double bsv = blueRangeSlider.getLowValue();

        redLow.setText(String.valueOf((int)rsv));
        greenLow.setText(String.valueOf((int)gsv));
        blueLow.setText(String.valueOf((int)bsv));

        this.changeLowPickerColorWithFields();
        if(previewImage != null){
            this.createPreview();
        }
    }

    @FXML
    public void setPickedValueForHighFields(){
        double r = higherPicker.getValue().getRed() * 255;
        double g = higherPicker.getValue().getGreen() * 255;
        double b = higherPicker.getValue().getBlue() * 255;

        //　上限下限を超えないため一旦 sliderへ入力
        redRangeSlider.setHighValue((int)r);
        greenRangeSlider.setHighValue((int)g);
        blueRangeSlider.setHighValue((int)b);

        double rsv = redRangeSlider.getHighValue();
        double gsv = greenRangeSlider.getHighValue();
        double bsv = blueRangeSlider.getHighValue();

        redHigh.setText(String.valueOf((int)rsv));
        greenHigh.setText(String.valueOf((int)gsv));
        blueHigh.setText(String.valueOf((int)bsv));

        this.changeHighPickerColorWithFields();
        if(previewImage != null){
            this.createPreview();
        }

    }

    @FXML
    public void createExtractImage(){
        ImageExtractor ie = new ImageExtractor(mainImage);
        double[] lv = this.getLowerRGB();
        double[] hv = this.getHigherRGB();
        Color lc = Color.color(lv[0],lv[1],lv[2]);
        Color hc = Color.color(hv[0],hv[1],hv[2]);
        ImagePlus newImage = ie.extractFromRGB(lc, hc);

        newImage.show();
    }



    //int delay_time = 0;
    long orth_timer = 0;
    private void createPreview(){
        int delay_time = 2; //ms

        long now_time = System.currentTimeMillis();
        orth_timer = orth_timer - now_time;

        if(orth_timer <= 0){
            ChangePreviewThread cst = new ChangePreviewThread();
            cst.start();
            orth_timer = System.currentTimeMillis() + delay_time;

        }else{
            orth_timer = System.currentTimeMillis() + delay_time;

        }

    }


    class ChangePreviewThread extends Thread{
        public void run(){
            setPreview();
        }
    }

    private void setPreview(){
        if(previewImage == null){
            previewImage = new ImagePlus();
            previewImage.setTitle("Preview");
        }

        ImagePlus currentImage = new ImagePlus();
        currentImage.setProcessor(mainImage.getProcessor());

        ImageExtractor ie = new ImageExtractor(currentImage);
        double[] lv = this.getLowerRGB();
        double[] hv = this.getHigherRGB();
        Color lc = Color.color(lv[0],lv[1],lv[2]);
        Color hc = Color.color(hv[0],hv[1],hv[2]);
        previewImage.setProcessor(ie.extractFromRGB(lc, hc).getProcessor());

        if(!previewImage.isVisible()){
            previewImage.show();
        }
    }

    public void showPreviewImage(){
        this.setPreview();
        previewImage.show();
    }

    public void close(){
        Platform.setImplicitExit(false);
        previewImage.close();
    }

}
