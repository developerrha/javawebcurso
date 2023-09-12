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
import java.net.InetSocketAddress;
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

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            svh = new ServerHomzode();
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);
            HttpServer server = HttpServer.create(new InetSocketAddress(8500), 0);
            HttpContext context = server.createContext("/home");
            server.setExecutor(threadPoolExecutor);
            context.setHandler(Httpsocket::handleRequest);
            server.start();
            System.out.println("Server started conect from: http://192.168.10.60:8500/home");
        } catch (IOException ex) {
            Logger.getLogger(Httpsocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private static void handleRequest(HttpExchange exchange) throws IOException {
        svh.serverHomzode(exchange);
    }
}

class ServerHomzode{
    private String task;
    private String requestMethod;
    private String respath;
    //private String query;
    private String kf;
    
    public void serverHomzode( HttpExchange exchange ){
        try{
            requestMethod = exchange.getRequestMethod();
            //query = exchange.getRequestURI().getQuery();
            //System.out.println("query: "+query);
            System.out.println("Request method: " + requestMethod);
            if( requestMethod.contains("GET")){
                task = getParameterValue( exchange, "task" );
                respath = exchange.getRequestURI().getPath();
                System.out.println("task_out= "+task+" path: "+ respath);
                doGet(exchange);
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
    private void doGet( HttpExchange exchange ){
        try{
                if( task.equals("default") && respath.equals("/home") ){ 
                    InputStream ins =  getClass().getClassLoader().getResourceAsStream("resources/formreg.html");
                    String response = new BufferedReader(new InputStreamReader(ins)).lines().collect(Collectors.joining("\n"));
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
}
