/***************************************************************
*  Team members: Michael Ortiz, Daniel Lin, Peter Maxwell
*  Team Name: Mein-crafters
*  file: Vector3Float.java
*  author: T. Diaz
*  class: CS 445 – Computer Graphics
*
*  Final Project: date last modified: 6/1/2015
*
*  purpose: This program draws multiple cubes using a chunks method, with each cube
*  textured and then randomly placed using simplex noise. There are 6 cube types defined:
*  grass, sand, water, dirt, stone, and bedrock
****************************************************************/ 



package mein.crafters;


public class Vector3Float
{
   public float x;
   public float y;
   public float z;
   
   public Vector3Float(int x, int y, int z)
   {
      this.x = x;
      this.y = y;
      this.z = z;
   }
}
