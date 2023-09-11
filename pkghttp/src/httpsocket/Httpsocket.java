/*
 * Java web basico
 */
package httpsocket;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
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
    public void serverHomzode( HttpExchange exchange ){
        try{
            String task = getParameterValue( exchange, "task" );
            String requestB = exchange.getRequestMethod();
            String respath = exchange.getRequestURI().getPath();
            System.out.println("Request method: " + requestB+" path: "+ respath);
            if( requestB.contains("GET")){
                System.out.println("task_out= "+task);
                if( task.equals("default") && respath.equals("/home") ){ 
                    InputStream ins =  getClass().getClassLoader().getResourceAsStream("resources/embedpage.html");
                    String response = new BufferedReader(new InputStreamReader(ins)).lines().collect(Collectors.joining("\n"));
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }else if( task.equals("getimage") ){ 
                    InputStream ins =  getClass().getClassLoader().getResourceAsStream("resources/fill_movil.jpg");
                    OutputStream os = exchange.getResponseBody();
                    byte[] targetArray = ins.readAllBytes();
                    exchange.sendResponseHeaders(200, targetArray.length);
                    os.write( targetArray );
                    os.close();
                }else if( task.equals("getvideo") ){ 
                    InputStream ins =  getClass().getClassLoader().getResourceAsStream("resources/small_exportado.mp4");
                    OutputStream os = exchange.getResponseBody();
                    byte[] targetArray = ins.readAllBytes();
                    exchange.sendResponseHeaders(200, targetArray.length);
                    os.write( targetArray );
                    os.close();
                }else if( respath.contains(".js") ){ 
                    String inpath = respath.substring( respath.lastIndexOf( "/" ) );
                    System.out.println("inpath: " + inpath);
                    InputStream ins =  getClass().getClassLoader().getResourceAsStream("resources"+inpath);
                    OutputStream os = exchange.getResponseBody();
                    byte[] targetArray = ins.readAllBytes();
                    exchange.sendResponseHeaders(200, targetArray.length);
                    os.write( targetArray );
                    os.close();
                }
            }else if( requestB.contains("POST") ) { 
                System.out.println("Arrive to POST now..");
            }  
        }catch(Exception e){
            e.printStackTrace();
        }
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
