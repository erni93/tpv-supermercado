
package supermercado;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import javax.swing.ImageIcon;

public class Conexion{
    //Empresa
    private String nombreEmpresa;
    private String esloganEmpresa;
    private ImageIcon logoEmpresa;
    
    
    //Base de datos
    private String nombreBaseDatos;
    private String direccionBaseDatos;
    private int puertoBaseDatos;
    private String usuarioBaseDatos;
    private String contrasenaBaseDatos;
    private Connection conexion;
    private String descripcionErrorConexion;

    public Conexion(String nombreEmpresa, String esloganEmpresa, ImageIcon logoEmpresa, String nombreBaseDatos, String direccionBaseDatos, int puertoBaseDatos, String usuarioBaseDatos, String contrasenaBaseDatos) {
        this.nombreEmpresa = nombreEmpresa;
        this.esloganEmpresa = esloganEmpresa;
        this.logoEmpresa = logoEmpresa;
        this.nombreBaseDatos = nombreBaseDatos;
        this.direccionBaseDatos = direccionBaseDatos;
        this.puertoBaseDatos = puertoBaseDatos;
        this.usuarioBaseDatos = usuarioBaseDatos;
        this.contrasenaBaseDatos = contrasenaBaseDatos;
        conectarBaseDatos();
    }
    
    private void conectarBaseDatos(){
        try {
            // 1.1) Cargamos el driver JDBC que vayamos a usar 
            Class.forName("com.mysql.jdbc.Driver"); 
            // 1.2) Establecemos una conexión con nuestra base de datos 
            //El objeto Properties hace que salgan tildes y eñes, también podemos añadir el usuario y contraseña 
            Properties datosConexion = new Properties(); 
            datosConexion.put("charSet", "iso-8859-1"); 
            datosConexion.put("user", usuarioBaseDatos); 
            datosConexion.put("password", contrasenaBaseDatos); 
            //conectamos con la base de datos 
            String url = "jdbc:mysql://" + direccionBaseDatos + ":" + puertoBaseDatos + "/" + nombreBaseDatos; 
            conexion = (Connection) DriverManager.getConnection(url, datosConexion);
            
        }catch (java.sql.SQLException | ClassNotFoundException e) { 
            descripcionErrorConexion = e.getMessage();
        }
    }
    public GuardarConexion guardarDatosConexion(){
        return new GuardarConexion(nombreEmpresa, esloganEmpresa, logoEmpresa, nombreBaseDatos, direccionBaseDatos, puertoBaseDatos, usuarioBaseDatos, contrasenaBaseDatos);
    }
    public boolean comprobarConexion(){
        return conexion != null;
    }
    public void volverConectar(){
        conectarBaseDatos();
    }

    public Connection getConexion() {
        return conexion;
    }

    public String getDescripcionErrorConexion() {
        return descripcionErrorConexion;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getEsloganEmpresa() {
        return esloganEmpresa;
    }

    public void setEsloganEmpresa(String esloganEmpresa) {
        this.esloganEmpresa = esloganEmpresa;
    }

    public int getPuertoBaseDatos() {
        return puertoBaseDatos;
    }

    public void setPuertoBaseDatos(int puertoBaseDatos) {
        this.puertoBaseDatos = puertoBaseDatos;
    }

    public String getUsuarioBaseDatos() {
        return usuarioBaseDatos;
    }

    public void setUsuarioBaseDatos(String usuarioBaseDatos) {
        this.usuarioBaseDatos = usuarioBaseDatos;
    }

    public String getContrasenaBaseDatos() {
        return contrasenaBaseDatos;
    }

    public void setContrasenaBaseDatos(String contrasenaBaseDatos) {
        this.contrasenaBaseDatos = contrasenaBaseDatos;
    }

    public ImageIcon getLogoEmpresa() {
        return logoEmpresa;
    }

    public void setLogoEmpresa(ImageIcon logoEmpresa) {
        this.logoEmpresa = logoEmpresa;
    }

    public String getNombreBaseDatos() {
        return nombreBaseDatos;
    }

    public void setNombreBaseDatos(String nombreBaseDatos) {
        this.nombreBaseDatos = nombreBaseDatos;
    }

    public String getDireccionBaseDatos() {
        return direccionBaseDatos;
    }

    public void setDireccionBaseDatos(String direccionBaseDatos) {
        this.direccionBaseDatos = direccionBaseDatos;
    }
    
    
    
}
