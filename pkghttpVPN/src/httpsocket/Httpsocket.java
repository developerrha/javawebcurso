/*
 * Java web basico
 */
package httpsocket;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author homzode
 */
public class Httpsocket {
    private static ServerHomzode svh;
    private static String serverHome;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            svh = new ServerHomzode();
            String homeIp = svh.getServerIPs();
            if( args.length > 0){
                serverHome = args[0];
                System.out.println("con argumento: "+serverHome);
            }else{
                serverHome = homeIp+":"+8500;
            }
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
            InetSocketAddress insok = new InetSocketAddress( homeIp, 8500);
            HttpServer server = HttpServer.create( insok, 0);
            HttpContext context = server.createContext("/home");
            server.setExecutor(threadPoolExecutor);
            context.setHandler(Httpsocket::handleRequest);
            server.start();
            System.out.println("Server started conect from: http://"+homeIp+":8500/home");
        } catch (IOException ex) {
            Logger.getLogger(Httpsocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private static void handleRequest(HttpExchange exchange) throws IOException {
        svh.serverHomzode(exchange, serverHome);
    }
}

class ServerHomzode{
    private String task;
    private String requestMethod;
    private String respath;
    //private String query;
    private String kf;
    
    public void serverHomzode( HttpExchange exchange, String serverHome){
        try{
            requestMethod = exchange.getRequestMethod();
            System.out.println("Request method: " + requestMethod);
            if( requestMethod.contains("GET")){
                task = getParameterValue( exchange, "task" );
                respath = exchange.getRequestURI().getPath();
                System.out.println("task_out= "+task+" path: "+ respath);
                doGet(exchange, serverHome );
            }else if( requestMethod.contains("POST") ) { 
                System.out.println("Arrive to POST now..");
                doPost(exchange); 
            }  
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
    * Metodo que procesa requerimiento por POST
    * @param exchange  
    */
        
    private void doPost( HttpExchange exchange){
        try{
            HashMap<String,String> parMap = getPostParam(exchange);
            //System.out.println("param razon: "+ parMap.get("razon") );
            for(Map.Entry m:parMap.entrySet()){  
               System.out.println("parMap: "+m.getKey()+"-> "+m.getValue());   
            }  
            InputStream ins =  getClass().getClassLoader().getResourceAsStream("resources/formreg.html");
            String response = new BufferedReader(new InputStreamReader(ins)).lines().collect(Collectors.joining("\n"));
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }catch( Exception e){
            e.printStackTrace();
        }
    }
    /**
    * Metodo que procesa requerimiento por GET
    * @param exchange  
    */
    private void doGet( HttpExchange exchange, String serverHome){
        try{
                if( task.equals("default") && respath.equals("/home") ){ 
                    //InputStream ins =  getClass().getClassLoader().getResourceAsStream("resources/formreg.html");
                    //String response = new BufferedReader(new InputStreamReader(ins)).lines().collect(Collectors.joining("\n"));
                    String response = "<!DOCTYPE html PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\n" +
                            "<HTML>\n" +
                            "   <HEAD>\n" +
                            "      <TITLE>Formulario de registro </TITLE>\n" +
                            "      <link rel=\"stylesheet\" href=\"http://"+serverHome+"/home/guest.css\" type=\"text/css\"/>\n" +
                            "   </HEAD>\n" +
                            "    <BODY>\n" +
                            "        <style>\n" +
                            "            .register_form input:required { \n" +
                            "                background: transparent url(http://"+serverHome+"/home/red_asterisk.png) no-repeat 98% center; \n" +
                            "            } \n" +
                            "            .register_form input:required:valid { \n" +
                            "                background: #fff url(http://"+serverHome+"/home/valid.png) no-repeat 98% center; box-shadow: 0 0 5px #5cd053; border-color: #28921f; } \n" +
                            "            .register_form input:focus:invalid º{ \n" +
                            "                background: #fff url(http://"+serverHome+"/home/invalid.png) no-repeat 98% center; box-shadow: 0 0 5px #d45252; border-color: #b03535; \n" +
                            "            }\n" +
                            "        </style>\n" +
                            "       <H2>Java web envio de formulario a HttpServer</H2>\n" +
                            "        <form class='register_form' id='register_form' method='POST' enctype='multipart/form-data' >\n" +
                            "            <div id='doregister'>\n" +
                            "                <ul><li><h2>Nuevo registro</h2><span class='required_notification'>* Datos requeridos</span><br><span class='promocion'>Diligencie el formulário y envie su registro.</span></br></li>\n" +
                            "                    <li><label for='nombres'>Nombres:</label> <input type='text' name='nombres' placeholder='Nombres del usuario...' maxlength='98' pattern='[A-Za-z0-9ñÑáéíóúÁÉÍÓÚ. ]{1,98}' required/> </li>\n" +
                            "                    <li><label for='apellidos'>Apellidos:</label> <input type='text' name='apellidos' placeholder='Apellidos del usuario...' maxlength='58' pattern='[A-Za-z0-9ñÑáéíóúÁÉÍÓÚ. ]{1,58}' required/> </li>\n" +
                            "                    <li><label for='pais'>Pais:</label> <input type='text' name='pais' placeholder='Pais de residencia...' maxlength='98' pattern='[A-Za-z0-9ñÑáéíóúÁÉÍÓÚ. ]{1,98}' required/> </li>\n" +
                            "                    <li><label for='mail'>Correo:</label> <input type='email' name='mail' placeholder='Minegocio@nmail.com' maxlength='58' required/> </li>\n" +
                            "                    <li><label for='tel'>Teléfono:</label> <input type='number' name='tel' placeholder='Telefono de contacto...' maxlength='14' required/> </li>\n" +
                            "                    <li><label for='usuario'>Usuario:</label> <input type='text' name='usuario' placeholder='Usuario para acceso al sistema...' maxlength='14' pattern='[A-Za-z0-9]{1,14}' required/></li>\n" +
                            "                    <li><label for='clave'>Clave:</label> <input type='password' name='clave' placeholder='Clave para acceso al sistema, 4 números...' maxlength='4' pattern='[0-9]{1,4}' required/> </li>\n" +
                            "                    <li>\n" +
                            "                        <button id='submit' class='submit' type='submit' >Enviar registro</button>\n" +
                            "                    </li>\n" +
                            "                </ul>\n" +
                            "            </div>\n" +
                            "        </form>\n" +
                            "    </BODY>\n" +
                            "</HTML>";
                    //System.out.println("response: "+response);
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                // Respouesta a requerimiento con parametros "?"        
                }else if( task.equals("getimage") ){ 
                    InputStream ins =  getClass().getClassLoader().getResourceAsStream("resources/red_asterisk.png");
                    OutputStream os = exchange.getResponseBody();
                    byte[] targetArray = ins.readAllBytes();
                    exchange.sendResponseHeaders(200, targetArray.length);
                    os.write( targetArray );
                    os.close();
                // Respouesta a requerimiento con parametros "?"            
                }else if( task.equals("getvideo") ){ 
                    InputStream ins =  getClass().getClassLoader().getResourceAsStream("resources/small_exportado.mp4");
                    OutputStream os = exchange.getResponseBody();
                    byte[] targetArray = ins.readAllBytes();
                    exchange.sendResponseHeaders(200, targetArray.length);
                    os.write( targetArray );
                    os.close();
                // Respouesta a requerimiento con path "/"            
                }else if( respath.contains(".js") || respath.contains(".css")  ){ 
                    String inpath = respath.substring( respath.lastIndexOf( "/" ) );
                    System.out.println("inpath: " + inpath);
                    InputStream ins =  getClass().getClassLoader().getResourceAsStream("lib"+inpath);
                    OutputStream os = exchange.getResponseBody();
                    byte[] targetArray = ins.readAllBytes();
                    exchange.sendResponseHeaders(200, targetArray.length);
                    os.write( targetArray );
                    os.close();
                // Respouesta a requerimiento con path "/"                
                }else if( respath.contains(".jpg")
                        || respath.contains(".png")
                        || respath.contains(".mp4") ){ 
                    String inpath = respath.substring( respath.lastIndexOf( "/" ) );
                    System.out.println("inpath: " + inpath);
                    InputStream ins =  getClass().getClassLoader().getResourceAsStream("resources"+inpath);
                    OutputStream os = exchange.getResponseBody();
                    byte[] targetArray = ins.readAllBytes();
                    exchange.sendResponseHeaders(200, targetArray.length);
                    os.write( targetArray );
                    os.close();
                }

        }catch( Exception e){
            e.printStackTrace();
        }
    }
    /**
    * Metodo que obriene el valor de parametros enviados por POST
    * @param exchange  
    */
    private HashMap getPostParam( HttpExchange exchange){
        HashMap<String,String> parMap = new HashMap<String,String>();
        try{
            byte[] array = new byte[1024];
            InputStream inpost = exchange.getRequestBody();
            BufferedReader reader = new BufferedReader(new InputStreamReader( inpost ));
            while(reader.ready()) {
                String line = reader.readLine();
                if( line.length() <= 0 || line.contains("---") )continue;
                if( line.contains("=")  ){
                    int posini = line.indexOf("\"") + 1;
                    int posfin = line.lastIndexOf( "\"" );
                    kf = line.substring( posini , posfin  );
                }else{
                    parMap.put(kf, line);
                }
            }
            inpost.close();
        }catch( Exception e){
            e.printStackTrace();
        }
        return parMap;
    }
    
    private String getParameterValue( HttpExchange exch, String toMatch ){
        String paramValue = "";
        try{
            if( !exch.getRequestURI().toString().contains("?") ){
                return "default";
            }
            paramValue = exch.getRequestURI().toString().split("\\?")[1];  
            if( paramValue.contains(toMatch) ){
                if( paramValue.contains("&") ){
                    String temp[] = paramValue.split("&");
                    for(int i = 0; i< temp.length; i++){
                        if( temp[i].contains(toMatch) ){
                            paramValue = temp[i].split("=")[1];
                            break;
                        }
                    }
                }else{
                    paramValue = paramValue.split("=")[1];
                }
            }else{
                return "default";
            }
                
        }catch( Exception e){
            e.printStackTrace();
        }
        return paramValue;
    }
    /**
     * Metodo que obtiene la IP local del sistema
     * @return String
     */
    public String getServerIPs(){
        String inAd = "";
        try{
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            NetworkInterface nint;
            while(e.hasMoreElements()) {
                nint = (NetworkInterface) e.nextElement();
                Enumeration nintip = nint.getInetAddresses();
                while (nintip.hasMoreElements()) {
                   InetAddress inetAd = (InetAddress) nintip.nextElement();
                   System.out.println(inetAd.getHostAddress());
                   if( inetAd.getHostAddress().indexOf("192.168") != -1 ){
                       inAd = inetAd.getHostAddress().toString();
                       System.out.println("find: "+ inAd );
                       return inAd;
                   }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return inAd;
    }
}
