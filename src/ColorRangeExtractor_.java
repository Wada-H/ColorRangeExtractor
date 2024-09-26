import hw.colorrangeextractor.CreateColorRangeExtractorUI;
import ij.IJ;
import ij.ImageListener;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.Toolbar;
import ij.plugin.frame.PlugInFrame;
import javafx.embed.swing.JFXPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ColorRangeExtractor_ extends PlugInFrame implements ImageListener, MouseListener, MouseMotionListener, MouseWheelListener, WindowListener, KeyListener {
    static String version = "20180418";
    CreateColorRangeExtractorUI ui;

    ImagePlus mainImage;
    ImageCanvas ic;
    String uiFileName;


    private int chSize;
    private int zSize;
    private int tSize;
    private int imgDepth;

    private int imgWidth;
    private int imgHeight;

    // Panel //
    Point ij_location;

    public ColorRangeExtractor_() {

        super("ColorRangeExtractor ver:" + version);



        if(checkImage()){
            this.getBasicInformation(mainImage);

            this.createPanelFx();

            //this.setListener(mainImage);
            //if(mainImage.isComposite()) {
            //    ci_imp = (CompositeImage) mainImage;
            //}

        }else{
            IJ.noImage();
            return;
        }


    }


    public boolean checkImage(){
        boolean b = false;


        ImagePlus checkImage = WindowManager.getCurrentImage();
        if(checkImage == null){
            b = false;
        }else{
            //if(checkImage.isHyperStack() == false){
            //    IJ.run(checkImage, "Stack to Hyperstack...", ""); //なんかgetWindow().toFront()でエラー出る
            //}
            mainImage = WindowManager.getCurrentImage();
            ic = mainImage.getCanvas();
            this.setListener();

            b = true;
        }


        return b;
    }

    public void getBasicInformation(ImagePlus img){
        chSize = img.getNChannels();
        zSize = img.getNSlices();
        tSize = img.getNFrames();
        imgWidth = img.getWidth();
        imgHeight = img.getHeight();
        imgDepth = img.getBitDepth();

    }



    private void createPanelFx(){

        uiFileName = "ui.fxml"; //ここを画像のタイプで変更すればよい？

        ui = new CreateColorRangeExtractorUI(mainImage, uiFileName);
        JFXPanel jfxPanel = ui.getFXML();
        IJ.setTool(Toolbar.LINE);
        this.setPanelPosition();
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setBounds(100,100,jfxPanel.getWidth(),jfxPanel.getHeight());
        this.add(jfxPanel);
        this.pack(); //推奨サイズのｗindow
        this.setVisible(true);//thisの表示
        ui.showPreviewImage();

    }

    private void setPanelPosition(){
        ij_location = IJ.getInstance().getLocation(); //imagejのtoolboxの開始座標
        int ij_height = IJ.getInstance().getHeight();
        this.setLocation(ij_location.x, ij_location.y + ij_height);
    }

    private void setListener(){
        mainImage.getWindow().addWindowListener(this);

    }

    private void removeListener(){
        mainImage.getWindow().removeWindowListener(this);

    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {

    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        ui.close();
        this.removeListener();
        this.close();

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    @Override
    public void imageOpened(ImagePlus imp) {

    }

    @Override
    public void imageClosed(ImagePlus imp) {

    }

    @Override
    public void imageUpdated(ImagePlus imp) {

    }
}
