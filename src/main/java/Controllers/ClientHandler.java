package Controllers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

@Path("client/")
public class ClientHandler {
    public static byte[] getFile (String path) {
        try {
            File file = new File(path);
            byte[] fileData = new byte[(int) file.length()];
            DataInputStream dis = new DataInputStream(new FileInputStream(file));
            dis.readFully(fileData);
            dis.close();
            return fileData;

        } catch (Exception e) {
            System.out.println("Failed to read file");
            return null;
        }
    }

    @GET
    @Path("{path}")
    @Produces("text/html")
    public static byte[] getHTML (@PathParam("path") String path) {
        return getFile("resources/website/" + path);
    }

    @GET
    @Path("JS/{path}")
    @Produces("text/javascript")
    public static byte[] getJS (@PathParam("path") String path) {
        return getFile("resources/website/JS/" + path);
    }
}
