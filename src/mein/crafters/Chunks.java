/**
 * *************************************************************
 * Team members: Michael Ortiz, Daniel Lin, Peter Maxwell Team Name:
 * Mein-crafters file: Chunks.java author: T. Diaz class: CS 445 – Computer
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
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import mein.crafters.SimplexNoise_octave;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunks {

    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    private int maxHeight = 0;

    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int StartX, StartY, StartZ;
    private Random r;
    private int VBOTextureHandle;
    private Texture texture;

    public void render() {
        glPushMatrix();
        //must be before glDraw arrays
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2, GL_FLOAT, 0, 0L);
        //Binds VBOVertexHandle and VBOColorHandle to GL_ARRAY_BUFFER
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glColorPointer(3, GL_FLOAT, 0, 0L);

        glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }

    public void rebuildMesh(float startX, float startY, float startZ) {
        //new stuff
        //feeding simplex noise its largest feature, persistence and seed to start generating random numbers
        SimplexNoise noise = new SimplexNoise(60, .05, 25);
        //getting the random height of our world

        //float height = (startY + (int)(100*noise.getNoise(1,1,1)) * CUBE_LENGTH);
        //This line is placed on the y loop below. Will gerneate a random terrain
        //VBO(Vertex Buffer Objects) handles
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        //Float buffers 
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        //render chuncks loop
        double height;
        for (int x = 0; x < CHUNK_SIZE; x += 1) {
            for (int z = 0; z < CHUNK_SIZE; z += 1) {
                height = (startY + (int) (100 * noise.getNoise(x, z)) * CUBE_LENGTH);
                for (int y = 0; y < CHUNK_SIZE; y++) {
                    //A special, magical height for...stuff
                    int specialHeight = y * CUBE_LENGTH + (int) height;
                    maxHeight = (specialHeight > maxHeight) ? specialHeight : maxHeight;
                    if (specialHeight > 20) {
                        VertexPositionData.put(createCube((float) (startX + x * CUBE_LENGTH), (float) specialHeight, (float) (startZ + z * CUBE_LENGTH)));
                        System.out.println("\nCube: " +x+","+y+","+z+" at: " + (startX + x * CUBE_LENGTH) + " Y: " + specialHeight + " Z: " + (startZ + z * CUBE_LENGTH));
                        Blocks[(int)x][(int)y][(int)z].setCoords((startX + x * CUBE_LENGTH), specialHeight, (startZ + z * CUBE_LENGTH));
                        VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int) x][(int) y][(int) z])));
                        VertexTextureData.put(createTexCube((float) 0, (float) 0, Blocks[(int) (x)][(int) (y)][(int) (z)]));
                    }
                }
            }
        }
        //Flip the data because it's calculated opposite of what we want to see (I think)
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        //Texture binding buffers
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        //vertex binding buffers
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        //Color binding buffers
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        return cubeColors;
    }

    //used for the basic position data of the chunk
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[]{
            // TOP QUAD
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,
            //BOTTOM QUAD 
            x + offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z,
            x + offset, y - offset, z,
            // FRONT QUAD 
            x + offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            // BACK QUAD
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            // LEFT QUAD
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - CUBE_LENGTH,
            // RIGHT QUAD 
            x + offset, y + offset, z,
            x + offset, y + offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z
        };
    }

    private float[] getCubeColor(Block block) {
        //no longer need this stuff
//        switch (block.GetID()) {
//            case 1:
//                return new float[]{0, 1, 0};
//            case 2:
//                return new float[]{1, 0.5f, 0};
//            case 3:
//                return new float[]{0, 0f, 1f};
//        }
        return new float[]{1, 1, 1};
    }

    //was originally chunck in slides but I think it was meant to be constructor so I renamed it 
    public Chunks(int startX, int startY, int startZ) {
        //try before anything else
        //gets a png of what we want the terrain to look like
        try {
            //System.out.println("The location we are looking for the picutre is: " + ResourceLoader.getResourceAsStream("terrain.png").toString());
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain2.png"));
        } catch (Exception e) {
            System.out.print("ER-ROAR!");
        }
        r = new Random();
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                for (int z = 0; z < CHUNK_SIZE; z++) {
                    if (y >= CHUNK_SIZE - 10) {
                        if (r.nextFloat() > 0.7f) {
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                        } else if (r.nextFloat() > 0.5f && y <= CHUNK_SIZE - 3) {
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                        } else if (r.nextFloat() > 0.37f && y >= CHUNK_SIZE - 3) {
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
                        } else if (r.nextFloat() > 0.3f && y >= CHUNK_SIZE - 5) {
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Sand);
                        } else if (r.nextFloat() > 0.2f && y >= CHUNK_SIZE - 1) {
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Pumpkin);
                        } 
                        else 
                        {
                            Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                        }
                    } else {
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Stone);
                    }
                }
            }
        }
        //VBO handles (here and in rebuilid mesh method)
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }

    //To create cube with texture
    public static float[] createTexCube(float x, float y, Block block) {
        float offset = (1024f / 16) / 1024f;
        //if it's being set up for a texture it's most likely active lol
        block.SetActive(true);
        switch (block.GetID()) {
            //Grass
            case 0:
                return grass(offset, x, y);
            //Sand
            case 1:
                return sand(offset, x, y);

            //Water
            case 2:
                return water(offset, x, y);

            //Dirt
            case 3:
                return dirt(offset, x, y);

            //Stone
            case 4:
                return stone(offset, x, y);

            //Pumpkin
            case 5:
                return pumpkin(offset, x, y);

            //Default - bedrock
            case 6:
                return bedrock(offset, x, y);

            default:
                return bedrock(offset, x, y);
        }
    }

    private static float[] grass(float offset, float x, float y) {
        return new float[]{
            // TOP QUAD(DOWN=+Y)
            x + offset * 3, y + offset * 10,
            x + offset * 2, y + offset * 10,
            x + offset * 2, y + offset * 9,
            x + offset * 3, y + offset * 9,
            // BOTTOM!
            x + offset * 3, y + offset * 1,
            x + offset * 2, y + offset * 1,
            x + offset * 2, y + offset * 0,
            x + offset * 3, y + offset * 0,
            // FRONT QUAD
            x + offset * 3, y + offset * 0,
            x + offset * 4, y + offset * 0,
            x + offset * 4, y + offset * 1,
            x + offset * 3, y + offset * 1,
            // BACK QUAD
            x + offset * 4, y + offset * 1,
            x + offset * 3, y + offset * 1,
            x + offset * 3, y + offset * 0,
            x + offset * 4, y + offset * 0,
            // LEFT QUAD
            x + offset * 3, y + offset * 0,
            x + offset * 4, y + offset * 0,
            x + offset * 4, y + offset * 1,
            x + offset * 3, y + offset * 1,
            // RIGHT QUAD
            x + offset * 3, y + offset * 0,
            x + offset * 4, y + offset * 0,
            x + offset * 4, y + offset * 1,
            x + offset * 3, y + offset * 1
        };
    }

    private static float[] sand(float offset, float x, float y) {
        return new float[]{
            // BOTTOM QUAD(DOWN=+Y)
            x + offset * 1, y + offset * 12,
            x + offset * 0, y + offset * 12,
            x + offset * 0, y + offset * 11,
            x + offset * 1, y + offset * 11,
            // TOP!
            x + offset * 3, y + offset * 2,
            x + offset * 2, y + offset * 1,
            x + offset * 2, y + offset * 1,
            x + offset * 3, y + offset * 2,
            // FRONT QUAD
            x + offset * 1, y + offset * 12,
            x + offset * 0, y + offset * 12,
            x + offset * 0, y + offset * 13,
            x + offset * 1, y + offset * 13,
            // BACK QUAD
            x + offset * 1, y + offset * 12,
            x + offset * 0, y + offset * 12,
            x + offset * 0, y + offset * 13,
            x + offset * 1, y + offset * 13,
            // LEFT QUAD
            x + offset * 1, y + offset * 12,
            x + offset * 0, y + offset * 12,
            x + offset * 0, y + offset * 13,
            x + offset * 1, y + offset * 13,
            // RIGHT QUAD
            x + offset * 1, y + offset * 12,
            x + offset * 0, y + offset * 12,
            x + offset * 0, y + offset * 13,
            x + offset * 1, y + offset * 13
        };
    }

    private static float[] water(float offset, float x, float y) {
        return new float[]{
            // TOP QUAD(DOWN=+Y)
            x + offset * 1, y + offset * 10,
            x + offset * 0, y + offset * 10,
            x + offset * 1, y + offset * 9,
            x + offset * 0, y + offset * 9,
            // BOTTOM!
            x + offset * 1, y + offset * 10,
            x + offset * 0, y + offset * 10,
            x + offset * 1, y + offset * 9,
            x + offset * 0, y + offset * 9,
            // FRONT QUAD
            x + offset * 1, y + offset * 9,
            x + offset * 0, y + offset * 9,
            x + offset * 1, y + offset * 10,
            x + offset * 0, y + offset * 10,
            // BACK QUAD
            x + offset * 1, y + offset * 10,
            x + offset * 0, y + offset * 10,
            x + offset * 1, y + offset * 9,
            x + offset * 0, y + offset * 9,
            // LEFT QUAD
            x + offset * 1, y + offset * 9,
            x + offset * 0, y + offset * 9,
            x + offset * 1, y + offset * 10,
            x + offset * 0, y + offset * 10,
            // RIGHT QUAD
            x + offset * 1, y + offset * 9,
            x + offset * 0, y + offset * 9,
            x + offset * 1, y + offset * 10,
            x + offset * 0, y + offset * 10
        };
    }

    private static float[] dirt(float offset, float x, float y) {
        return new float[]{
            // TOP QUAD(DOWN=+Y)
            x + offset * 2, y + offset * 11,
            x + offset * 1, y + offset * 11,
            x + offset * 1, y + offset * 10,
            x + offset * 2, y + offset * 10,
            // BOTTOM!
            x + offset * 2, y + offset * 11,
            x + offset * 1, y + offset * 11,
            x + offset * 1, y + offset * 10,
            x + offset * 2, y + offset * 10,
            // FRONT QUAD
            x + offset * 2, y + offset * 10,
            x + offset * 1, y + offset * 10,
            x + offset * 1, y + offset * 11,
            x + offset * 2, y + offset * 11,
            // BACK QUAD
            x + offset * 2, y + offset * 11,
            x + offset * 1, y + offset * 11,
            x + offset * 1, y + offset * 10,
            x + offset * 2, y + offset * 10,
            // LEFT QUAD
            x + offset * 2, y + offset * 10,
            x + offset * 1, y + offset * 10,
            x + offset * 1, y + offset * 11,
            x + offset * 2, y + offset * 11,
            // RIGHT QUAD
            x + offset * 2, y + offset * 10,
            x + offset * 1, y + offset * 10,
            x + offset * 1, y + offset * 11,
            x + offset * 2, y + offset * 11
        };
    }

    private static float[] stone(float offset, float x, float y) {
        return new float[]{
            // BOTTOM QUAD(DOWN=+Y)
            x + offset * 1, y + offset * 2,
            x + offset * 0, y + offset * 2,
            x + offset * 0, y + offset * 1,
            x + offset * 1, y + offset * 1,
            // TOP!
            x + offset * 1, y + offset * 2,
            x + offset * 0, y + offset * 2,
            x + offset * 0, y + offset * 1,
            x + offset * 1, y + offset * 1,
            // FRONT QUAD
            x + offset * 1, y + offset * 2,
            x + offset * 0, y + offset * 2,
            x + offset * 0, y + offset * 1,
            x + offset * 1, y + offset * 1,
            // BACK QUAD
            x + offset * 1, y + offset * 2,
            x + offset * 0, y + offset * 2,
            x + offset * 0, y + offset * 1,
            x + offset * 1, y + offset * 1,
            // LEFT QUAD
            x + offset * 0, y + offset * 1,
            x + offset * 1, y + offset * 1,
            x + offset * 1, y + offset * 2,
            x + offset * 0, y + offset * 2,
            // RIGHT QUAD
            x + offset * 0, y + offset * 1,
            x + offset * 1, y + offset * 1,
            x + offset * 1, y + offset * 2,
            x + offset * 0, y + offset * 2
        };
    }

    private static float[] pumpkin(float offset, float x, float y) {
        return new float[]{
            // BOTTOM QUAD(DOWN=+Y)
            x + offset * 7, y + offset * 7,
            x + offset * 6, y + offset * 7,
            x + offset * 6, y + offset * 6,
            x + offset * 7, y + offset * 6,
            // TOP!
            x + offset * 7, y + offset * 8,
            x + offset * 6, y + offset * 8,
            x + offset * 6, y + offset * 7,
            x + offset * 7, y + offset * 7,
            // FRONT QUAD
            x + offset * 9, y + offset * 7,
            x + offset * 8, y + offset * 7,
            x + offset * 8, y + offset * 8,
            x + offset * 9, y + offset * 8,
            // BACK QUAD
            x + offset * 7, y + offset * 8,
            x + offset * 6, y + offset * 8,
            x + offset * 6, y + offset * 7,
            x + offset * 7, y + offset * 7,
            // LEFT QUAD
            x + offset * 7, y + offset * 7,
            x + offset * 6, y + offset * 7,
            x + offset * 6, y + offset * 8,
            x + offset * 7, y + offset * 8,
            // RIGHT QUAD
            x + offset * 7, y + offset * 7,
            x + offset * 6, y + offset * 7,
            x + offset * 6, y + offset * 8,
            x + offset * 7, y + offset * 8,};
    }

    private static float[] bedrock(float offset, float x, float y) {
        return new float[]{
            // BOTTOM QUAD(DOWN=+Y)
            x + offset * 4, y + offset * 2,
            x + offset * 3, y + offset * 2,
            x + offset * 3, y + offset * 1,
            x + offset * 4, y + offset * 1,
            // TOP!
            x + offset * 4, y + offset * 2,
            x + offset * 3, y + offset * 2,
            x + offset * 3, y + offset * 1,
            x + offset * 4, y + offset * 1,
            // FRONT QUAD
            x + offset * 4, y + offset * 2,
            x + offset * 3, y + offset * 2,
            x + offset * 3, y + offset * 1,
            x + offset * 4, y + offset * 1,
            // BACK QUAD
            x + offset * 4, y + offset * 2,
            x + offset * 3, y + offset * 2,
            x + offset * 3, y + offset * 1,
            x + offset * 4, y + offset * 1,
            // LEFT QUAD
            x + offset * 4, y + offset * 2,
            x + offset * 3, y + offset * 2,
            x + offset * 3, y + offset * 1,
            x + offset * 4, y + offset * 1,
            // RIGHT QUAD
            x + offset * 4, y + offset * 2,
            x + offset * 3, y + offset * 2,
            x + offset * 3, y + offset * 1,
            x + offset * 4, y + offset * 1
        };
    }
    

    
    /**
     * It used an <u>axis aligned bounding box</u> around the chunk to detect if there was any 
     * collision with the chunk and the camera
     * @param camera -the camera position vector to compare to the chunk
     * @return boolean value if the camera collided with the chunk
     */
    boolean collision(Vector3f camera) {
        //some manipulation of this will get me in the right coordinate cube
        int x = (int) (-1 * camera.x);
        int y = (int) (-1 * camera.y);
        int z = (int) (-1 * camera.z);
        if((x >= 0 && x < CHUNK_SIZE * 2) && (y >= 20 && y <= maxHeight) && (z >= 0 && z < CHUNK_SIZE * 2)){
            System.out.println("The collision happened at- X: " + x + " Y: " + y + " Z: " + z );
            //return Blocks[x/2][y/2][z/2].checkCollision(camera);
            return true;
        } else {
            return false;
        }
//        if((Math.abs(camera.x) < CHUNK_SIZE && Math.abs(camera.x) > 0) && (Math.abs(camera.y) < CHUNK_SIZE && Math.abs(camera.y) > 0) && (Math.abs(camera.z) < CHUNK_SIZE && Math.abs(camera.z) > 0)){
//             return Blocks[(int)Math.abs(camera.x)][(int)Math.abs(camera.y)][(int)Math.abs(camera.z)].checkCollision(camera);
//        }
//        else {
//            return false;
//        }
       
    }
}
