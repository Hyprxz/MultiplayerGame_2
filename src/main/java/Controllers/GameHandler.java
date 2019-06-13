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
    private static int currentId = 1;
    private double xGravity = 0.0;
    private double yGravity = 0.009;
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
            for (int i = 0; i < blockArray.size(); i++) {

            }
        }

        public void update () {
            this.xVelocity += xGravity;
            this.yVelocity += yGravity;

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
        synchronized (blockArray) {
            blockArray.add(new Block(rand.nextFloat() * 500, 500, 50));
        }
        return jsonObject.toString();
    }

    @GET
    @Path("getBlocks")
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("unchecked")
    public String getBlocks () {
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

    public static void gameLoop () throws InterruptedException {
        while (true) {
            TimeUnit.NANOSECONDS.sleep(1000);
            for (Sprite s : spriteArray) s.update();
        }
    }
}
