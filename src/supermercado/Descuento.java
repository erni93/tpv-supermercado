
package supermercado;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;


public class Descuento implements Comparable<Descuento>{
   private String cod_descuento;
   // Por optimización se usa unicamente el cod de articulo
   private String cod_articulo;
   private int porcentaje_descuento;
   private java.util.Date fecha_validez;

    public Descuento(String cod_descuento, String cod_articulo, int porcentaje_descuento, Date fecha_validez) {
        this.cod_descuento = cod_descuento;
        this.cod_articulo = cod_articulo;
        this.porcentaje_descuento = porcentaje_descuento;
        this.fecha_validez = fecha_validez;
    }
    public static ArrayList<Descuento> cargarDescuentos(Connection conexion) throws SQLException{
        
        ArrayList<Descuento> descuentos = new ArrayList<>();
        Statement statement = conexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resultset = statement.executeQuery("SELECT * FROM `descuento` ");
        while (resultset.next()){
            String cod_descuento = resultset.getString("COD_DESCUENTO");
            String cod_articulo = resultset.getString("ARTICULO");
            int porcentaje_descuento = resultset.getInt("PORCENTAJE_DESCUENTO");
            java.util.Date fecha_validez = new Date(resultset.getDate("FECHA_VALIDEZ").getTime());
            
            Descuento nuevoDescuento = new Descuento(cod_descuento,cod_articulo, porcentaje_descuento, fecha_validez);
            descuentos.add(nuevoDescuento);
        }
        descuentos.sort(null);
        return descuentos;
    }
    public static Descuento cargarDescuentoArticulo(Connection conexion, String cod_articulo) throws SQLException{
        Statement statement = conexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        /* Consulta para sacar el máximo descuento de un artículo con fecha válida
        SELECT * 
        FROM  `descuento` AS descuento1
        WHERE descuento1.articulo =  cod_articulo
        AND  `porcentaje_descuento` = (

        SELECT MAX( descuento2.`porcentaje_descuento` ) 
        FROM  `descuento` AS descuento2
        WHERE descuento1.articulo = descuento2.articulo
        AND descuento2.`fecha_validez` > CURDATE( )
        )
        */
        String sentenciaSQL = "SELECT * FROM `descuento` as descuento1 WHERE descuento1.articulo = '" + cod_articulo + "' AND `porcentaje_descuento` = "
                + "(SELECT MAX(descuento2.`porcentaje_descuento`) from `descuento` as descuento2 WHERE  descuento1.articulo =  descuento2.articulo  "
                + "AND descuento2.`fecha_validez` > curdate() )";
        ResultSet resultset = statement.executeQuery(sentenciaSQL);
        if (resultset.next()){
            String cod_descuento = resultset.getString("COD_DESCUENTO");
            int porcentaje_descuento = resultset.getInt("PORCENTAJE_DESCUENTO");
            java.util.Date fecha_validez = new Date(resultset.getDate("FECHA_VALIDEZ").getTime());
            Descuento nuevoDescuento = new Descuento(cod_descuento,cod_articulo, porcentaje_descuento, fecha_validez);
            return nuevoDescuento;
        }else{
            return null;
        }
        
        
    }
    public String getCod_descuento() {
        return cod_descuento;
    }

    public String getCod_articulo() {
        return cod_articulo;
    }

    public int getPorcentaje_descuento() {
        return porcentaje_descuento;
    }

    public Date getFecha_validez() {
        return fecha_validez;
    }

    @Override
    public int compareTo(Descuento o) {
        int num1 = Integer.parseInt(o.getCod_descuento().substring(4));
        int num2 = Integer.parseInt(this.getCod_descuento().substring(4));
        if (num1 == num2){
            return 0;
        }else if (num2 > num1){
            return 1;
        }else{
            return -1;
        }
    }
   
}
