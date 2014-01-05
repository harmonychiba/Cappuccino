/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cafe.cappuccino.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author chibayuuki
 */
@WebServlet(name = "Cappuccino", urlPatterns = {"/"})
public class Cappuccino extends HttpServlet {

    private final String db_user = "root";
    private final String db_name = "cappuccino";
    
    private final String images_dir = "./cappuccino/images";

    /**
     *
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response, String additionalData)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Cappuccino</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Cappuccino at " + request.getContextPath() + "</h1>");
            out.println("<p>Data:" + additionalData + "</p>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String request_service = "cappuccino";
        String request_page = "index";
        String request_status = "";
        HashMap<String, String> query_map = new HashMap<>();

        String request_uri = request.getRequestURI();
        String query = request.getQueryString();

        if (request_uri.startsWith("/")) {
            request_uri = request_uri.substring(1);
        }
        String[] splited = request_uri.split("/");
        int index = 0;
        if (splited[0].equals("Cappuccino")) {
            index++;
        }
        if (splited.length > index) {
            request_service = splited[index];
        }
        index++;
        if (splited.length > index) {
            request_page = splited[index];
        }
        index++;
        if (splited.length > index) {
            request_status = splited[index];
        }

        if (query != null) {
            String[] queries = query.split("&");

            for (String query_peer : queries) {
                String[] key_and_value = query_peer.split("=");
                String key = key_and_value[0];
                String value = key_and_value[1];
                query_map.put(key, value);
            }
        }

        processRequest(request, response, request_service + ":" + request_page + ":" + request_status + "?" + query);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String request_uri = request.getRequestURI();
        if (request_uri.startsWith("/")) {
            request_uri = request_uri.substring(1);
        }
        String[] splited = request_uri.split("/");
        int index = 0;
        if (splited[0].equals("Cappuccino")) {
            index++;
        }
        String request_service;
        if (splited.length > index) {
            request_service = splited[index];
        } else {
            request_service = "";
        }

        if (request_service.equals("register")) {
            register(request, response);
        } else if (request_service.equals("register_image")) {
            registerImage(request, response);
        } else if (request_service.equals("register_page")) {
            registerPage(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void register(HttpServletRequest request, HttpServletResponse response) {
        try {
            BufferedReader bsr = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line = bsr.readLine();
            while (line != null) {
                System.out.println(line);
                line = bsr.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(Cappuccino.class.getName()).log(Level.SEVERE, null, ex);
        }
        Enumeration<String> data = request.getParameterNames();
        System.out.println("---------params---------");
        while (data.hasMoreElements()) {
            System.out.println(data.nextElement());
        }
        String email = request.getParameter("email");
        String service_name = request.getParameter("service_name");
        boolean result = addServiceToDB(email, service_name);
        if (result) {
            sendResponse("success", 200, response);
        } else {
            sendResponse("failed", 400, response);
        }
    }

    private void registerImage(HttpServletRequest request, HttpServletResponse response) {
        try {
            try {
                BufferedReader bsr = new BufferedReader(new InputStreamReader(request.getInputStream()));
                String line = bsr.readLine();
                while (line != null) {
                    System.out.println(line);
                    line = bsr.readLine();
                }
            } catch (IOException ex) {
                Logger.getLogger(Cappuccino.class.getName()).log(Level.SEVERE, null, ex);
            }
            Enumeration<String> data = request.getParameterNames();
            System.out.println("---------params---------");
            while (data.hasMoreElements()) {
                System.out.println(data.nextElement());
            }
            String email = request.getParameter("email");
            String service_name = request.getParameter("service_name");
            int service_id = 0;
            ResultSet service = queryDB("SELECT id from services where email=" + email + " and service_name=" + service_name);
            service_id = service.getInt("id");
            Calendar cal = Calendar.getInstance();
            long timestamp = cal.getTime().getTime();
            
            FileItem file = getUploadedFileByName(request, "image");
            File img_dir = new File(images_dir);
            if(!img_dir.exists()){
                img_dir.mkdirs();
            }
            File service_img_dir = new File(images_dir+"/service"+service_id);
            if(!service_img_dir.exists()){
                img_dir.mkdir();
            }
            
            String filename = images_dir+"/service"+service_id+"/"+timestamp+FilenameUtils.getName(file.getName());
            File output = new File(filename);
            FileOutputStream stream = new FileOutputStream(output);
            BufferedInputStream in = new BufferedInputStream(file.getInputStream());
            byte[] buffer = new byte[128];
            int readsize;
            while((readsize=in.read(buffer)) != -1){
                stream.write(buffer, 0, readsize);
            }
            
            
            
            boolean result = addServiceToDB(email, service_name);
            if (result) {
                sendResponse("success", 200, response);
            } else {
                sendResponse("failed", 400, response);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Cappuccino.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Cappuccino.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Cappuccino.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void registerPage(HttpServletRequest request, HttpServletResponse response) {
        try {
            try {
                BufferedReader bsr = new BufferedReader(new InputStreamReader(request.getInputStream()));
                String line = bsr.readLine();
                while (line != null) {
                    System.out.println(line);
                    line = bsr.readLine();
                }
            } catch (IOException ex) {
                Logger.getLogger(Cappuccino.class.getName()).log(Level.SEVERE, null, ex);
            }
            Enumeration<String> data = request.getParameterNames();
            System.out.println("---------params---------");
            while (data.hasMoreElements()) {
                System.out.println(data.nextElement());
            }
            String email = request.getParameter("email");
            String service_name = request.getParameter("service_name");
            String html_data = request.getParameter("html");
            String page_name = request.getParameter("page_name");
            int service_id = 0;
            ResultSet service = queryDB("SELECT id from services where email=" + email + " and service_name=" + service_name);
            service_id = service.getInt("id");
            boolean result = addPageToDB(service_id, html_data, page_name);
            if (result) {
                sendResponse("success", 200, response);
            } else {
                sendResponse("failed", 400, response);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Cappuccino.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ResultSet queryDB(String query) {
        try {
            Class.forName("org.gjt.mm.mysql.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/" + db_name, db_user, "");
            java.sql.Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            return rs;

        } catch (ClassNotFoundException | SQLException e) {
            String msg = "ドライバのロードに失敗しました";
            System.out.println(msg);
            System.err.println(e.getMessage());
            return null;
        }
    }

    /*
     DB scheme
     services
     |-id - int - auto_increment - primary_key
     |-service_name - varchar(31)
     |-email - varchar(63)
     */
    private boolean addServiceToDB(String email, String service_name) {
        try {
            Class.forName("org.gjt.mm.mysql.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/" + db_name, db_user, "");
            java.sql.Statement stmt = con.createStatement();
            String sqlStr = "INSERT into services(service_name,email) VALUES(" + service_name + "," + email + ")";
            boolean result = stmt.execute(sqlStr);
            System.out.println(sqlStr + ":" + result);
            return result;
        } catch (ClassNotFoundException | SQLException e) {
            String msg = "ドライバのロードに失敗しました";
            System.out.println(msg);
            return false;
        }
    }

    /*
     DB scheme
     pages
     |-id - int auto_increment - primary_key
     |-service_id - int
     |-html - longtext
     |-name - varchar(31)
     */
    private boolean addPageToDB(int service_id, String html_data, String page_name) {
        try {
            Class.forName("org.gjt.mm.mysql.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/" + db_name, db_user, "");
            java.sql.Statement stmt = con.createStatement();
            String sqlStr = "INSERT into pages(service_id,html,name) VALUES(" + service_id + "," + html_data + "," + page_name + ")";
            boolean result = stmt.execute(sqlStr);
            System.out.println(sqlStr + ":" + result);
            return result;
        } catch (ClassNotFoundException | SQLException e) {
            String msg = "ドライバのロードに失敗しました";
            System.out.println(msg);
            return false;
        }
    }
    
    /*
     DB scheme
     images
     |-id - int auto_increment - primary_key
     |-service_id - int
     |-path - varchar(64)
     |-name - varchar(31)
     */
    private boolean addImageToDB(int service_id, String path, String name) {
        try {
            Class.forName("org.gjt.mm.mysql.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/" + db_name, db_user, "");
            java.sql.Statement stmt = con.createStatement();
            String sqlStr = "INSERT into images(service_id,path,name) VALUES(" + service_id + "," + path + "," + name + ")";
            boolean result = stmt.execute(sqlStr);
            System.out.println(sqlStr + ":" + result);
            return result;
        } catch (ClassNotFoundException | SQLException e) {
            String msg = "ドライバのロードに失敗しました";
            System.out.println(msg);
            return false;
        }
    }

    private void sendResponse(String data, int status, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            response.setContentType("text/html;charset=UTF-8");
            response.setStatus(status);
            out = response.getWriter();
            try {
                /* TODO output your page here. You may use following sample code. */
                out.println(data);
            } finally {
                out.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Cappuccino.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private FileItem getUploadedFileByName(HttpServletRequest request, String name) {
        try {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
            for (FileItem item : items) {
                if (item.isFormField()) {
                    String fieldname = item.getFieldName();
                    String fieldvalue = item.getString();
                    if(fieldname.equals(name)){
                        return item;
                    }
                } else {
                    String fieldname = item.getFieldName();
                    String filename = FilenameUtils.getName(item.getName());
                    if(fieldname.equals(name)){
                        return item;
                    }
                }
            }
        } catch (FileUploadException ex) {
            Logger.getLogger(Cappuccino.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
