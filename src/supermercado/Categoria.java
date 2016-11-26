
package supermercado;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.ImageIcon;


public class Categoria {
    private String cod_categoria;
    private String nombre;
    private ImageIcon imagen;
    private Empleado responsable;

    public Categoria(String cod_categoria, String nombre, ImageIcon imagen, Empleado responsable) {
        this.cod_categoria = cod_categoria;
        this.nombre = nombre;
        this.imagen = imagen;
        this.responsable = responsable;
    }
    
    public static ArrayList<Categoria> cargarCategorias(Connection conexion, ArrayList<Empleado> encargados) throws SQLException{
        ArrayList<Categoria> categorias = new ArrayList<>();
        
        Statement statement = conexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        ResultSet resulset = statement.executeQuery("SELECT * FROM `categoria`");
        while (resulset.next()){
            String cod_categoria = resulset.getString("COD_CATEGORIA");
            String nombre = resulset.getString("NOMBRE");
            ImageIcon imagen = new ImageIcon(resulset.getBytes("IMAGEN"));
            String cod_responsable = resulset.getString("RESPONSABLE");
            Empleado responsable = null;
            for (Empleado encargado : encargados) {
                if (encargado.getCod_empleado().equals(cod_responsable)){
                    responsable = encargado;
                }
            }
            Categoria nuevaCategoria = new Categoria(cod_categoria, nombre, imagen, responsable);
            categorias.add(nuevaCategoria);
        }
        return categorias;
    }
    public static ArrayList<String> cargarCod_categorias(Connection conexion) throws SQLException{
        ArrayList<String> cod_Categorias = new ArrayList<>();
        Statement statement = conexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        ResultSet resulset = statement.executeQuery("SELECT * FROM `categoria`");
        while (resulset.next()){
            String cod_categoria = resulset.getString("COD_CATEGORIA");
            cod_Categorias.add(cod_categoria);
        }
        return cod_Categorias;
    }
    public String getCod_categoria() {
        return cod_categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public ImageIcon getImagen() {
        return imagen;
    }

    public Empleado getResponsable() {
        return responsable;
    }

    @Override
    public String toString() {
        return nombre;
    }
    
    
    
}
