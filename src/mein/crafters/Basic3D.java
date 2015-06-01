/**
 * *************************************************************
 * Team members: Michael Ortiz, Daniel Lin, Peter Maxwell Team Name:
 * Mein-crafters file: Basic3D.java author: T. Diaz class: CS 445 – Computer
 * Graphics
 * 
* Final Project: date last modified: 6/1/2015
 * 
* purpose: This program draws multiple cubes using a chunks method, with each
 * cube textured and then randomly placed using simplex noise. There are 6 cube
 * types defined: grass, sand, water, dirt, stone, and bedrock
 * **************************************************************
 */
package mein.crafters;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.GL_AMBIENT;
import static org.lwjgl.opengl.GL11.GL_COLOR_ARRAY;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_DIFFUSE;
import static org.lwjgl.opengl.GL11.GL_LIGHT0;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_POSITION;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_SPECULAR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_COORD_ARRAY;
import static org.lwjgl.opengl.GL11.GL_VERTEX_ARRAY;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnableClientState;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLight;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.input.Mouse;
import org.lwjgl.Sys;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glCullFace;
import java.util.Random;
/**
 *
 * @author michael
 */
class Basic3D {

    private DisplayMode displayMode;
    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;
    private boolean lightMove = false;

    public void start() {
        try {
            createWindow();
            initGL();
            gameLoop();//render();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initGL() {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);
        //Culling to render only important surfaces
        glCullFace(GL_FRONT);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        glLoadIdentity();
        GLU.gluPerspective(100.0f, (float) displayMode.getWidth() / (float) displayMode.getHeight(), 0.1f, 300.0f);
        glMatrixMode(GL_MODELVIEW);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

        //lighting stuff
        initLightArrays();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition); //sets our light’s position
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);//sets our specular light
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);//sets our diffuse light
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);//sets our ambient light
        glEnable(GL_LIGHTING);//enables our lighting
        glEnable(GL_LIGHT0);//enables light0

    }

    private void createWindow() throws Exception {
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == 640
                    && d[i].getHeight() == 480
                    && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("Mein-Crafter!");
        Display.create();
    }

    private void initLightArrays() {
        lightPosition = BufferUtils.createFloatBuffer(4);
        //This is where we want to map our light source to be 
        //in the world we make
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();

        whiteLight = BufferUtils.createFloatBuffer(4);
        //color value for white light
        whiteLight.put(3.0f).put(3.0f).put(3.0f).put(3.0f).flip();

    }

    public void gameLoop() {
        FPCameraController camera = new FPCameraController(-10f, -70f, -20f);

        Chunks chunks = new Chunks(0, 0, 0);
        
        float dx = 0.0f;
        float dy = 0.0f;
        float dt = 0.0f;
        float lastTime = 0.0f;
        long time = 0;
        float mouseSensitivity = 0.09f;
        float movementSpeed = .35f;
        Mouse.setGrabbed(true);

        while (!Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            time = Sys.getTime();
            lastTime = time;
            dx = Mouse.getDX();
            dy = Mouse.getDY();
            camera.yaw(dx * mouseSensitivity);
            camera.pitch(dy * mouseSensitivity);

            if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)) {
                if(!chunks.collision(camera.getPosition())){
                    camera.walkForward(movementSpeed, lightMove);
                }else {
                    System.out.println("Collision!!!!");
                }
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
                if(!chunks.collision(camera.getPosition())){
                    camera.strafeLeft(movementSpeed, lightMove);
                } else {
                    
                }
                
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
                if(!chunks.collision(camera.getPosition())){
                camera.walkBackwards(movementSpeed, lightMove);
                } else {
                    
                }
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
                if(!chunks.collision(camera.getPosition())){
                camera.strafeRight(movementSpeed, lightMove);
                } else {}
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                camera.moveUp(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                camera.moveDown(movementSpeed);
            }
            
            //if l key is held down light source will move as camera moves otherwise --> half lit/half dim
            if(Keyboard.isKeyDown(Keyboard.KEY_L)){
                lightMove = true;
            } else {
                lightMove = false;
            }
            
            //regenerates landscape
            if(Keyboard.isKeyDown(Keyboard.KEY_APOSTROPHE))
            {
                chunks = new Chunks(0, 0, 0);
                camera = new FPCameraController(-26f, -70f, -27f);
            }
            //new spawn location
            if(Keyboard.isKeyDown(Keyboard.KEY_DELETE))
            {
                Random r = new Random();
                int num = r.nextInt(60);
                int num2 = r.nextInt(60);
                camera = new FPCameraController(-(float)num, -70f, -(float)num2);
            }
            
            

                    
            glLoadIdentity();
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            chunks.render();
            //render();
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }

}
