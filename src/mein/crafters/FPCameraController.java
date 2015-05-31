/**
 * *************************************************************
 * Team members: Michael Ortiz, Daniel Lin, Peter Maxwell Team Name:
 * Mein-crafters file: Basic3D.java author: T. Diaz class: CS 445 – Computer
 * Graphics
 * 
* Final Project: checkpoint 2 date last modified: 5/18/2015
 * 
* purpose: This program draws multiple cubes using a chunks method, with each
 * cube textured and then randomly placed using simplex noise. There are 6 cube
 * types defined: grass, sand, water, dirt, stone, and bedrock
 * **************************************************************
 */
package mein.crafters;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.opengl.GL11.*;

public class FPCameraController {

    private Vector3f position = null;
    private Vector3f lPosition = null;

    private float yaw = 0.0f;
    private float pitch = 0.0f;
    private Vector3Float me;
    

    public FPCameraController(float x, float y, float z) {
        position = new Vector3f(x, y, z);
        lPosition = new Vector3f(x, y, z);
        lPosition.x = 15f;
        lPosition.y = 60f;
        lPosition.z = 30f;
    }

    public void yaw(float amount) {
        yaw = yaw + amount;
    }

    public void pitch(float amount) {
        pitch = pitch - amount;
    }

    public void walkForward(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
        System.out.println("I am at these coordinates. X: " + position.x + " Y: " + position.y + " Z: " + position.z);
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x += xOffset).put(lPosition.y).put(lPosition.z -= zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    public void walkBackwards(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x -= xOffset).put(lPosition.y).put(lPosition.z += zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    public void strafeLeft(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw + 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw + 90));
        position.x += xOffset;
        position.z -= zOffset;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x -= xOffset).put(lPosition.y).put(lPosition.z += zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    public void strafeRight(float distance) {
        float xOffset = distance * (float) Math.sin(Math.toRadians(yaw - 90));
        float zOffset = distance * (float) Math.cos(Math.toRadians(yaw - 90));
        position.x += xOffset;
        position.z -= zOffset;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x -= xOffset).put(lPosition.y).put(lPosition.z += zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    public void moveUp(float distance) {
        position.y -= distance;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x).put(lPosition.y).put(lPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    public void moveDown(float distance) {
        position.y += distance;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x).put(lPosition.y).put(lPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    public void lookThrough() {
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        glTranslatef(position.x, position.y, position.z);
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x).put(lPosition.y).put(lPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    public Vector3f getPosition(){
        return position;
    }

}
