
package supermercado;

import java.awt.Image;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.ImageIcon;


public class Articulo implements Comparable<Articulo>{
    private String cod_articulo;
    private String nombre;
    private int stock;
    private double precio_unidad;
    private int porcentaje_iva;
    private ImageIcon imagen;
    private String cod_categoria;

    public Articulo(String cod_articulo, String nombre, int stock, double precio_unidad, int porcentaje_iva, ImageIcon imagen, String cod_categoria) {
        this.cod_articulo = cod_articulo;
        this.nombre = nombre;
        this.stock = stock;
        this.precio_unidad = precio_unidad;
        this.porcentaje_iva = porcentaje_iva;
        this.imagen = imagen;
        this.cod_categoria = cod_categoria;
    }
    
    public static ArrayList<Articulo> cargarArticulos (Connection conexion, String filtroCategoria)throws SQLException{
        ArrayList<Articulo> articulos = new ArrayList<>();
        Statement statement = conexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resultset = null;
        if (filtroCategoria!= null){
            resultset = statement.executeQuery("SELECT * FROM articulo WHERE categoria = '" + filtroCategoria + "'");
            System.out.println("SELECT * FROM articulo WHERE categoria = '" + filtroCategoria + "'");
        }else{
            resultset = statement.executeQuery("SELECT * FROM articulo");
            System.out.println("SELECT * FROM articulo");
        }
        
        
        while (resultset.next()){
            String cod_articulo = resultset.getString("COD_ARTICULO");
            String nombre = resultset.getString("NOMBRE");
            int stock = resultset.getInt("STOCK");
            double precio_unidad = resultset.getDouble("PRECIO_UNIDAD");
            int porcentaje_iva = resultset.getInt("PORCENTAJE_IVA");
            ImageIcon imagen = new ImageIcon(resultset.getBytes("IMAGEN"));
            String cod_categoria = resultset.getString("CATEGORIA");
            Articulo nuevoArticulo = new Articulo(cod_articulo, nombre, stock, precio_unidad, porcentaje_iva, imagen, cod_categoria);
            articulos.add(nuevoArticulo);
        }
        articulos.sort(null);
        return articulos;
    }
    
    public static Articulo obtenerArticulo (Connection conexion, String buscarCod_articulo) throws SQLException{
        Articulo nuevoArticulo = null;
        Statement statement = conexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resultset = statement.executeQuery("SELECT * FROM articulo WHERE COD_ARTICULO = '" + buscarCod_articulo + "'");
        System.out.println("SELECT * FROM articulo WHERE COD_ARTICULO = '" + buscarCod_articulo + "'");
        if (resultset.next()){
            String cod_articulo = resultset.getString("COD_ARTICULO");
            String nombre = resultset.getString("NOMBRE");
            int stock = resultset.getInt("STOCK");
            double precio_unidad = resultset.getDouble("PRECIO_UNIDAD");
            int porcentaje_iva = resultset.getInt("PORCENTAJE_IVA");
            ImageIcon imagen = new ImageIcon(resultset.getBytes("IMAGEN"));
            String cod_categoria = resultset.getString("CATEGORIA");
            nuevoArticulo = new Articulo(cod_articulo, nombre, stock, precio_unidad, porcentaje_iva, imagen, cod_categoria);     
        }
        return nuevoArticulo;
    }
    public static void eliminarStockArticulo(Connection conexion, String cod_articulo, int cantidad) throws SQLException{
        Statement statement = conexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resultset = statement.executeQuery("SELECT * FROM articulo WHERE COD_ARTICULO = '" + cod_articulo + "'");
        System.out.println("SELECT * FROM articulo WHERE COD_ARTICULO = '" + cod_articulo + "'");
        if (resultset.next()){
            int stockActual = resultset.getInt("STOCK");
            resultset.updateInt("STOCK", stockActual-cantidad);
            resultset.updateRow();
        }
    }
    public void redimensionarImagen(int ancho, int alto){
        //http://www.coderanch.com/t/331731/GUI/java/Resize-ImageIcon
        Image preparImagen = imagen.getImage();
        Image nuevaImagen = preparImagen.getScaledInstance(ancho,alto, java.awt.Image.SCALE_SMOOTH);
        imagen = new ImageIcon(nuevaImagen);
    }
    public String getCod_articulo() {
        return cod_articulo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getStock() {
        return stock;
    }

    public double getPrecio_unidad() {
        return precio_unidad;
    }

    public int getPorcentaje_iva() {
        return porcentaje_iva;
    }

    public ImageIcon getImagen() {
        return imagen;
    }

    public String getCod_categoria() {
        return cod_categoria;
    }
    
    
    @Override
    public int compareTo(Articulo o) {
        int num1 = Integer.parseInt(o.getCod_articulo().substring(2));
        int num2 = Integer.parseInt(this.getCod_articulo().substring(2));
        if (num1 == num2){
            return 0;
        }else if (num2 > num1){
            return 1;
        }else{
            return -1;
        }
    }
    
    
}
