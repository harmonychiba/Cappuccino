/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cafe.cappuccino.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author chibayuuki
 */
@WebServlet(name = "Cappuccino", urlPatterns = {"/"})
public class Cappuccino extends HttpServlet {

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
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
            out.println("<p>Data:"+additionalData+"</p>");
            out.println("</body>");
            out.println("</html>");
        } finally {            
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
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
        HashMap<String,String> query_map = new HashMap<>();
        
        String request_uri = request.getRequestURI();
        String query = request.getQueryString();
        
        if(request_uri.startsWith("/")){
            request_uri = request_uri.substring(1);
        }
        String[] splited = request_uri.split("/");
        int index = 0;
        if(splited[0].equals("Cappuccino")){
            index++;
        }
        if(splited.length > index){
            request_service = splited[index];
        }
        index++;
        if(splited.length > index){
            request_page = splited[index];
        }
        index++;
        if(splited.length > index){
            request_status = splited[index];
        }
        
        String[] queries = query.split("&");
        
        for(String query_peer : queries){
            String[] key_and_value = query_peer.split("=");
            String key = key_and_value[0];
            String value = key_and_value[1];
            query_map.put(key, value);
        }
        
        processRequest(request, response,request_service+":"+request_page+":"+request_status+"?"+query);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
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
        if(request_uri.startsWith("/")){
            request_uri = request_uri.substring(1);
        }
        String[] splited = request_uri.split("/");
        int index = 0;
        if(splited[0].equals("Cappuccino")){
            index++;
        }
        
        
        
        processRequest(request, response,request_uri);
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
}
