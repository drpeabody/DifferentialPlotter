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
    static double x1, y1, y_1; //Boundary conditions
    static int cycles;
    
    //Camera Stuff
    static double scale, //length per pixel
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
        return (x - posX) / scale;
    }
    public static double translateToScreenY(double y) {
        return (y - posY) / scale;
    }
    public static double translateToWorldX(double x) {
        return x * scale + posX;
    }
    public static double translateToWorldY(double y){
            return y * scale + posY;
    }
    
    public static void initDefaults(){
        //Math
        dy = dx = 0.001;
        cycles = 10;
        x = x1 = dx; 
        y = y1 = 1;
        y_1 = 1;
        
        //Screen
        height = 720; width = 1280;
        
        //Frame
        startX = -12.80; startY = -7.20;
        endX = 12.8; endY = 7.20;
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
        scaleX = ((Math.abs(endY - startY) + Math.abs(endX - startX))/(width + height);
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

        bufferOrderTwo(FxPlus, FxMinus);
        
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
        int a = 0;
        dy = y_1;
        while(true){
            for (int i = 0; i < cycles; i++) {
                d2y = secondOrder(x, y, dy/dx) * (dx)* (dx);
                dy += d2y;
            }
            y += dy;
            x += dx;
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
                dy = -y_1;
                dx = -dx;
                continue;
            }
            if(x >= 5 && x <= 5.0001) System.out.println(y);
            if(x < startX && dx < 0) return;
        }
        
    }
    
    public static double secondOrder(double x, double y, double Yx){
        return -Yx;
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
        glVertex2d(-width, (-posY)/scale - 1);
        glVertex2d(-width, (-posY)/scale + 1);
        glVertex2d(width, (-posY)/scale + 1);
        glVertex2d(width, (-posY)/scale - 1);
        glVertex2d((-posX)/scale - 1, -height);
        glVertex2d((-posX)/scale + 1, -height);
        glVertex2d((-posX)/scale + 1, height);
        glVertex2d((-posX)/scale - 1, height);
        glEnd();
    }

    public static void processInput(){
        if(Mouse.isButtonDown(0)) {
            posX -= Mouse.getDX()*scale;
            posY -= Mouse.getDY()*scale;
        }
        if(Mouse.isButtonDown(1)){
            scale += Mouse.getDX()*zoom;
            scale += Mouse.getDY()*zoom;
            if(scale <= 0) scale = zoom;
            if(scale <= 0) scale = zoom;
        }
    }
}
