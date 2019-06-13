package Controllers;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Path("game/")
public class GameHandler {
    private static boolean gameInitialized = false;
    private static int currentId = 1;
    private double xGravity = 0.0;
    private double yGravity = 0.001;
    private static final double GUARD  = 0.01;
    static ArrayList<Sprite> spriteArray = new ArrayList<>();
    static ArrayList<Block> blockArray = new ArrayList<>();


    class Sprite {
        int id;
        float x;
        float y;
        float xVelocity;
        float yVelocity;
        int size;

        Sprite (int id, float x, float y, float xVelocity, float yVelocity, int size) {
            this.id = id; this.x = x; this.y = y; this.xVelocity = xVelocity; this.yVelocity = yVelocity; this.size = size;
        }

        public void checkBoxCollision () {
            for (Block block : blockArray) {
                //top of box first
                if (y + size <= block.y && y + size + yVelocity >= block.y) {
                    if (x >= block.x && x <= block.x + block.size) {
                        yVelocity = 0;
                        y = (float) (block.y - size - GUARD);
                    }
                    else if (x + size >= block.x && x + size <= block.x + block.size) {
                        yVelocity = 0;
                        y = (float) (block.y - size - GUARD);
                    }
                }

                //right of box
                if (x >= block.x + block.size && x + xVelocity <= block.x + block.size) {
                    if (y >= block.y && y <= block.y + block.size) {
                        this.xVelocity = 0;
                        this.x = (float) (block.x + block.size + GUARD);
                    }
                    else if (y + size >= block.y && y + size <= block.y + block.size) {
                        this.xVelocity = 0;
                        this.x = (float) (block.x + block.size + GUARD);
                    }
                }

                //bottom of box
                if (y >= block.y + block.size && y + yVelocity <= block.y + block.size) {
                    if (x >= block.x && x <= block.x + block.size) {
                        yVelocity = 0;
                        y = (float) (block.y + block.size + GUARD);
                    }
                    else if (x + size >= block.x && x + size <= block.x + block.size) {
                        yVelocity = 0;
                        y = (float) (block.y + size + GUARD);
                    }
                }

                //left of box
                if (x + size <= block.x && x + size + xVelocity >= block.x) {
                    if (y >= block.y && y <= block.y + block.size) {
                        xVelocity = 0;
                        x = (float) (block.y - size - GUARD);
                    }
                    else if (y + size >= block.y && y + size <= block.y + block.size) {
                        xVelocity = 0;
                        x = (float) (block.y - size - GUARD);
                    }
                }
            }
        }

        public void update () {
            this.xVelocity += xGravity;
            this.yVelocity += yGravity;
            checkBoxCollision();
            this.x += this.xVelocity;
            this.y += this.yVelocity;
        }
    }

    class Block {
        float x;
        float y;
        int size;

        Block (float x, float y, int size) {
            this.x = x; this.y = y; this.size = size;
        }
    }

    @POST
    @Path("new")
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public String addSprite () {
        Random rand = new Random();
        synchronized (spriteArray) {
            spriteArray.add(new Sprite(currentId, rand.nextFloat() * 1000, rand.nextFloat() * 500, 0, 0, 30));
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", currentId);
        currentId++;
        return jsonObject.toString();
    }

    @GET
    @Path("getBlocks")
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public String getBlocks () {
        if (!gameInitialized) {
            initWorld();
            gameInitialized = true;
        }

        JSONArray jsonArray = new JSONArray();
        synchronized (blockArray) {
            for (Block b : blockArray) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("x", b.x);
                jsonObject.put("y", b.y);
                jsonObject.put("size", b.size);
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray.toString();
    }

    @GET
    @Path("list")
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public String listSprites () {
        JSONArray jsonArray = new JSONArray();
        synchronized (spriteArray) {
            for (int i = 0; i < spriteArray.size(); i++) { //only need to send id, x, y and size to be able to draw, velocities would be pointless
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", spriteArray.get(i).id);
                jsonObject.put("x", spriteArray.get(i).x);
                jsonObject.put("y", spriteArray.get(i).y);
                jsonObject.put("size", spriteArray.get(i).size);
                jsonArray.add(jsonObject);
            }
            return jsonArray.toString();
        }
    }

    // could have a movement buffer so speeds don't vary, one sprite id can be in the buffer at one time
    // But I can't be fucked to try this now
    @POST
    @Path("move")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public String moveSprite (@FormDataParam("id") int id, @FormDataParam("jump") char jump, @FormDataParam("direction") char direction) {
        for (Sprite s : spriteArray) {
            if (s.id == id) {
                if (jump == 'T') s.yVelocity = (float) -0.5;
                if (direction == 'a') s.xVelocity = (float) -0.05;
                else if (direction == 'd') s.xVelocity = (float) 0.05;
            }
        }
        return "{\"status\": \"OK\"}";
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public String deleteSprite (@FormDataParam("id") int id) {
        synchronized (spriteArray) {
            for (int i = spriteArray.size(); i >= 0; i--) {
                if (spriteArray.get(i).id == id) {
                    spriteArray.remove(i);
                    return "{\"status\": \"OK\"}";
                }
            }
            return "{\"error\": \"No avatar found with id " + id + ".\"}";
        }
    }

    public void initWorld () {
        Random rand = new Random();
        synchronized (blockArray) {
            for (int i = 0; i < 10; i++) {
                blockArray.add(new Block(i * 50, 500, 50));
            }
        }
    }

    public static void gameLoop () throws InterruptedException {
        while (true) {
            TimeUnit.NANOSECONDS.sleep(10);
            for (Sprite s : spriteArray) s.update();
        }
    }
}
