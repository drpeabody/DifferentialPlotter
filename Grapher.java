package Projects.DifferentialPlotter;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import static org.lwjgl.opengl.GL11.*;

/**
 * @since 27 Sep, 2016
 * @author Abhishek
 */
public class Grapher {
    //Math stuff
    static double x, y, dx, dy, d2y;
    static double x1, y1, x_1, y_1; //Boundary conditions
    static int cycles;
    
    //Camera Stuff
    static double scaleX, scaleY, //length per pixel
                posX, posY; //length not pixels
    
    //Screen Stuff
    static int fps;
    static int width, height;
    
    //Frame
    static double startX, startY, endX, endY;
    static double FxPlus[], FxMinus[];
    static int num;
    
    //Mouse
    static float sense, zoom;
            
    public static double translateToScreenX(double x) {
        return (x - posX) / scaleX;
    }
    public static double translateToScreenY(double y) {
        return (y - posY) / scaleY;
    }
    public static double translateToWorldX(double x) {
        return x * scaleX + posX;
    }
    public static double translateToWorldY(double y){
            return y * scaleY + posY;
    }
    
    public static void initDefaults(){
        //Math
        dy = dx = 0.001;
        cycles = 10;
        x = x1 = dx; 
        y = y1 = dx;
        
        //Screen
        height = 720; width = 1280;
        
        //Frame
        startX = -10; startY = 0;
        endX = 10; endY = 0;
        num = (int)((endX - startX)/dx);
        FxPlus = new double[num*2];
        for(int i = 0; i < FxPlus.length; i++){
            FxPlus[i] = Double.NaN;
        }
        FxMinus = new double[num*2];
        for(int i = 0; i < FxMinus.length; i++){
            FxMinus[i] = Double.NaN;
        }
        
        //Camera
        scaleX = (endX - startX)/width; 
        scaleY = (endY - startY)/height;
        scaleX = scaleX;
        scaleY = 0.01;
        posX = posY = 0f;
        
        //Mouse
        zoom = 0.00001f;
    }

    public static void main(String[] args) throws LWJGLException {
        initDefaults();
        Display.setDisplayMode(new DisplayMode(width, height));
        Display.create();
        try {
            Mouse.create();
        } catch (LWJGLException ex) {}

        glDisable(GL_DEPTH_TEST);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glMatrixMode(GL_MODELVIEW);
        glOrtho(-width, width, -height, height, 1, -1);
        glClearColor(.1f, 0f, .15f, 1f);
        glColor4f(1f, 1f, 1f, 1f);

        bufferOrderOne(FxPlus, FxMinus);
        
        while(!Display.isCloseRequested()){
            glClear(GL_COLOR_BUFFER_BIT);
            glBegin(GL11.GL_LINE_STRIP);
            for (int i = 0; i < FxPlus.length - 4; i += 4) {
                glVertex2d(translateToScreenX(FxPlus[i]), translateToScreenY(FxPlus[i+1]));
                glVertex2d(translateToScreenX(FxPlus[i+2]), translateToScreenY(FxPlus[i+3]));
                glVertex2d(translateToScreenX(FxPlus[i+2]), translateToScreenY(FxPlus[i+3]));
            }
            for (int i = 0; i < FxMinus.length - 4; i += 4) {
                glVertex2d(translateToScreenX(FxMinus[i]), translateToScreenY(FxMinus[i+1]));
                glVertex2d(translateToScreenX(FxMinus[i+2]), translateToScreenY(FxMinus[i+3]));
                glVertex2d(translateToScreenX(FxMinus[i+2]), translateToScreenY(FxMinus[i+3]));
            }
            glEnd();
            drawAxes();
            Display.sync(fps);
            Display.update();
            processInput();
        }
    }
    
    public static void bufferOrderOne(double[] f, double[] g){
        int a = 0;
        x = x1; y = y1;
        while(true){
            for (int i = 0; i < cycles; i++) {
                dy = (firstOrder(x, y))* dx;
                y += dy;
                x += dx;
            }
            if(dx > 0){
                f[a++] = x;
                f[a++] = y;
            }
            else{
                g[a++] = x;
                g[a++] = y;
            }
            if(x > endX){
                a = 0;
                x = x1;
                y = y1;
                dx = -dx;
                continue;
            }
            if(x < startX && dx < 0) return;
        }
    }
    
    public static void bufferOrderTwo(double[] f, double[] g){
        System.out.println("Do This");
    }
    
    public static double secondOrder(double x, double y, double Yx){
        return 1;
    }
    
    //Doesn't work with functions of y
    public static double firstOrder(double x, double y){
//        return (5*x*x*x - 4*x*x - 3*x + 2);
        return y;
//        return   1/x;
//        return Math.sin(x);
        
//                dy = (Math.abs(2*x)*(Math.sin(Math.abs(x)))) * dx;
    }
    public static void drawAxes() {
        glBegin(GL_QUADS);
        glVertex2d(-width, (-posY)/scaleY - 1);
        glVertex2d(-width, (-posY)/scaleY + 1);
        glVertex2d(width, (-posY)/scaleY + 1);
        glVertex2d(width, (-posY)/scaleY - 1);
        glVertex2d((-posX)/scaleX - 1, -height);
        glVertex2d((-posX)/scaleX + 1, -height);
        glVertex2d((-posX)/scaleX + 1, height);
        glVertex2d((-posX)/scaleX - 1, height);
        glEnd();
    }

    public static void processInput(){
        if(Mouse.isButtonDown(0)) {
            posX -= Mouse.getDX()*scaleX;
            posY -= Mouse.getDY()*scaleY;
        }
        if(Mouse.isButtonDown(1)){
            scaleX += Mouse.getDX()*zoom;
            scaleY += Mouse.getDY()*zoom;
            if(scaleX < 0) scaleX = zoom;
            if(scaleY < 0) scaleY = zoom;
        }
    }
}