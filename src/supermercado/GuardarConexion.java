
package supermercado;

import java.io.Serializable;
import javax.swing.ImageIcon;


public class GuardarConexion  implements Serializable{
    
    
    private String nombreEmpresa;
    private String esloganEmpresa;
    private ImageIcon logoEmpresa;
    //Base de datos
    private String nombreBaseDatos;
    private String direccionBaseDatos;
    private int puertoBaseDatos;
    private String usuarioBaseDatos;
    private String contrasenaBaseDatos;

    public GuardarConexion(String nombreEmpresa, String esloganEmpresa, ImageIcon logoEmpresa, String nombreBaseDatos, String direccionBaseDatos, int puertoBaseDatos, String usuarioBaseDatos, String contrasenaBaseDatos) {
        this.nombreEmpresa = nombreEmpresa;
        this.esloganEmpresa = esloganEmpresa;
        this.logoEmpresa = logoEmpresa;
        this.nombreBaseDatos = nombreBaseDatos;
        this.direccionBaseDatos = direccionBaseDatos;
        this.puertoBaseDatos = puertoBaseDatos;
        this.usuarioBaseDatos = usuarioBaseDatos;
        this.contrasenaBaseDatos = contrasenaBaseDatos;
    }
    public Conexion cargarConexion(){
        return new Conexion(nombreEmpresa, esloganEmpresa, logoEmpresa, nombreBaseDatos, direccionBaseDatos, puertoBaseDatos, usuarioBaseDatos, contrasenaBaseDatos);
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
    
}
