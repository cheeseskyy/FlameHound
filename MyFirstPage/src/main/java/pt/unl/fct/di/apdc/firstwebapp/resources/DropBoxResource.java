package pt.unl.fct.di.apdc.firstwebapp.resources;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class DropBoxResource {

    private static final String token = "Bearer sQ7PllcMxtAAAAAAAAAAB26I-GPtQm3ekOXreksxk_P6o7lXTmeXDqJ4ZqiYUFQt"; 
    private static final Logger LOG = Logger.getLogger(DropBoxResource.class.getName());
    
    public void listFolder(String foldername) throws Exception {
        
      try {

        URL url = new URL("https://api.dropboxapi.com/2/files/list_folder");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String parameters = "{\"path\": \"" + foldername + "\",\"recursive\": false,\"include_media_info\": false,\"include_deleted\": false,\"include_has_explicit_shared_members\": false}";
        
        conn.setRequestProperty("Accept", "application/json");        
        conn.addRequestProperty ("Authorization", token);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        
        conn.setDoOutput(true);

        DataOutputStream writer = new DataOutputStream(conn.getOutputStream());
        writer.writeBytes(parameters);
        writer.flush();

        if (writer != null)
            writer.close();
        
        if (conn.getResponseCode() != 200) {
            System.out.println(conn.getResponseMessage());
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
            (conn.getInputStream())));

        String output;
        System.out.println("Output from Server .... \n");
        while ((output = br.readLine()) != null) {
            System.out.println(output);
        }

        conn.disconnect();

      } catch (MalformedURLException e) {

        e.printStackTrace();

      } catch (IOException e) {

        e.printStackTrace();

      }

    }
    
    public void delete(String path) throws Exception {
        
          try {

            URL url = new URL("https://api.dropboxapi.com/2/files/delete_v2");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String parameters = "{\"path\": \"" + path + "\"}";
            
            conn.setRequestProperty("Accept", "application/json");        
            conn.addRequestProperty ("Authorization", token);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            
            conn.setDoOutput(true);

            DataOutputStream writer = new DataOutputStream(conn.getOutputStream());
            writer.writeBytes(parameters);
            writer.flush();

            if (writer != null)
                writer.close();
            
            if (conn.getResponseCode() != 200) {
                System.out.println(conn.getResponseMessage());
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();

          } catch (MalformedURLException e) {

            e.printStackTrace();

          } catch (IOException e) {

            e.printStackTrace();

          }

        }
    
    public void createFolder(String path) throws Exception {
        
          try {

            URL url = new URL("https://api.dropboxapi.com/2/files/create_folder");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String parameters = "{\"path\": \"" + path + "\"}";
            
            conn.setRequestProperty("Content-Type", "application/json");    
            conn.addRequestProperty ("Authorization", token);
            conn.setRequestMethod("POST");
            
            
            conn.setDoOutput(true);

            DataOutputStream writer = new DataOutputStream(conn.getOutputStream());
            writer.writeBytes(parameters);
            writer.flush();

            if (writer != null)
                writer.close();
            
            if (conn.getResponseCode() != 200) {
                System.out.println(conn.getResponseMessage());
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();

          } catch (MalformedURLException e) {

            e.printStackTrace();

          } catch (IOException e) {

            e.printStackTrace();

          }

        }
    
    public void putFile(String foldername, byte[] file, String ext) throws Exception {
        
          try {

            URL url = new URL("https://content.dropboxapi.com/2/files/upload");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String parameters = "{\"path\": \"" + "/" + foldername + "/" + foldername + "." + ext + "\"}";
            
            conn.setRequestProperty("Content-Type", "application/octet-stream");    
            conn.addRequestProperty ("Authorization", token);
            conn.addRequestProperty ("Dropbox-API-Arg", parameters);
            conn.setRequestMethod("POST");
            
            
            conn.setDoOutput(true);


            DataOutputStream writer = new DataOutputStream(conn.getOutputStream());
            writer.write(file);
            writer.flush();
            
            if (writer != null)
                writer.close();
                        
            if (conn.getResponseCode() != 200) {
                LOG.info(conn.getResponseMessage());
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

            String output;
            LOG.info("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                LOG.info(output);
            }

            conn.disconnect();

          } catch (MalformedURLException e) {

            e.printStackTrace();

          } catch (IOException e) {

            e.printStackTrace();

          }

        }
    
    public byte[] getFile(String foldername, String ext) throws Exception {
        
          try {

            URL url = new URL("https://content.dropboxapi.com/2/files/download");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String parameters = "{\"path\": \"" + "/" + foldername + "/" + foldername + "." + ext + "\"}";
            
            conn.addRequestProperty ("Authorization", token);
            conn.addRequestProperty ("Dropbox-API-Arg", parameters);
            conn.setDoOutput(true);
                                    
            if (conn.getResponseCode() != 200) {
                System.out.println(conn.getResponseMessage());
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            InputStream reply = conn.getInputStream();
            byte[] file = new byte[reply.available()];
            reply.read(file);

            conn.disconnect();
            
            return file;

          } catch (MalformedURLException e) {

            LOG.info("Bad URL");

          } catch (IOException e) {

            LOG.info("Bad IO");

          }
          return null;

        }

}