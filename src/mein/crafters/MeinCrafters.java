/***************************************************************
*  Team members: Michael Ortiz, Daniel Lin, Peter Maxwell
*  Team Name: Mein-crafters
*  file: MeinCrafters.java
*  author: T. Diaz
*  class: CS 445 – Computer Graphics
*
*  Final Project: checkpoint 2
*  date last modified: 5/18/2015
*
*  purpose: This program draws multiple cubes using a chunks method, with each cube
*  textured and then randomly placed using simplex noise. There are 6 cube types defined:
*  grass, sand, water, dirt, stone, and bedrock
****************************************************************/ 

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
