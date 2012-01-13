package control;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import persistencia.PersistenceInterface;

/**
 * @author Juan Díez-Yanguas Barber
 */
public class LogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        HttpServletRequest modRequest = (HttpServletRequest) request;                
        String fechaHora = Tools.getDate();        
        String requestedURL = modRequest.getRequestURL().toString();
        String remoteAddr = modRequest.getRemoteAddr();
        String remoteHost = modRequest.getRemoteHost();
        String method = modRequest.getMethod();
        String param = getParamString(modRequest);
        String userAgent = modRequest.getHeader("user-agent");        
        PersistenceInterface persistencia = (PersistenceInterface)
                modRequest.getServletContext().getAttribute("persistence");
        boolean ok = persistencia.saveRequest(fechaHora, requestedURL, remoteAddr, remoteHost,
                method, param, userAgent);
        if (ok == false){
            Logger.getLogger(LogFilter.class.getName()).log(Level.INFO, "No se ha guardado el log de petición en la BD");
        }        
        chain.doFilter(request, response);        
    }
    
    private String getParamString (HttpServletRequest request){
        StringBuilder param = new StringBuilder("");
        
        Map <String, String []> parametros = request.getParameterMap();
        Iterator <String> iterador = parametros.keySet().iterator();        
        while (iterador.hasNext()){
            String key = iterador.next();
            if (key.toUpperCase().contains("PASS") == true){
                continue;
            }
            String  [] value = parametros.get(key);    
            for (int i = 0; i < value.length; i++){
                param.append("&");
                param.append(key);
                param.append("=");
                param.append(value[i]);
            }  
        }
        if (param.toString().equals("")){
            return null;
        }else{
            return param.toString();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}
