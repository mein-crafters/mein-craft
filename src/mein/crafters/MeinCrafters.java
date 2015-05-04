package mein.crafters;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex3f;

/**
 *
 * @author michael
 */
public class MeinCrafters {

    public static void main(String[] args) {
        Basic3D basic = new Basic3D();
        basic.start();
    }

}
