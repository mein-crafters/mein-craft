/***************************************************************
*  Team members: Michael Ortiz, Daniel Lin, Peter Maxwell
*  Team Name: Mein-crafters
*  file: Block.java
*  author: T. Diaz
*  class: CS 445 – Computer Graphics
*
* Final Project: date last modified: 6/1/2015
*
*  purpose: This program draws multiple cubes using a chunks method, with each cube
*  textured and then randomly placed using simplex noise. There are 6 cube types defined:
*  grass, sand, water, dirt, stone, and bedrock
****************************************************************/ 
package mein.crafters;

import org.lwjgl.util.vector.Vector3f;

public class Block {

    private boolean IsActive;
    private BlockType Type;
    private float x, y, z;

    boolean IsTrue() {
        return true;
    }

    public enum BlockType {

        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Pumpkin(5),
        BlockType_Default(6);
        private int BlockID;

        
        
        BlockType(int i) {
            BlockID = i;
        }

        public int GetID() {
            return BlockID;
        }

        public void SetID(int i) {
            BlockID = i;
        }
    }

    public Block(BlockType type) {
        Type = type;
    }

    public void setCoords(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public boolean checkCollision(Vector3f camera){
        if((camera.x < this.x && camera.y < this.y && camera.z < this.z) && IsActive){
            System.out.println("Collision at block position X: " + this.x + " Y: " + this.y + " Z: " + this.z);
            return true;
        } else {
           return false;
        }
        
    }

    public boolean IsActive() {
        return IsActive;
    }

    public void SetActive(boolean active) {
        IsActive = active;
    }

    public int GetID() {
        return Type.GetID();
    }
}