
package supermercado;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class LineaFactura {
    private String cod_lineaVenta;
    private String cod_factura;
    private Articulo articulo;
    private int cantidad;
    private Descuento descuento;
    private double sumaSinIva;
    private double sumaTotal;
    private DecimalFormat formatoDecimal = new DecimalFormat("#0.00");
    
    public LineaFactura(String cod_lineaVenta, String cod_factura, Articulo articulo, int cantidad,Descuento descuento) {
        this.cod_lineaVenta = cod_lineaVenta;
        this.cod_factura = cod_factura;
        this.articulo = articulo;
        this.cantidad = cantidad;
        this.descuento = descuento;
    }

   
    public static ArrayList<LineaFactura> lineasFactura (Connection conexion, String cod_factura) throws SQLException{
        ArrayList<LineaFactura> lineasFactura = new ArrayList<>();       
        Statement statement = conexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resultset = statement.executeQuery("SELECT * FROM `linea_factura` where venta = '" + cod_factura + "'");
        while (resultset.next()){
            String cod_lineaVenta = resultset.getString("COD_LINEA_FACTURA");
            Articulo articulo = Articulo.obtenerArticulo(conexion, resultset.getString("ARTICULO"));
            int cantidad = resultset.getInt("CANTIDAD");
            Descuento descuento = Descuento.cargarDescuentoArticulo(conexion,articulo.getCod_articulo());
            LineaFactura nuevaLinea = new LineaFactura(cod_lineaVenta, cod_factura, articulo, cantidad, descuento);
            lineasFactura.add(nuevaLinea);
        }
        return lineasFactura;
    }
    public static void anadirLineasFacturas(Connection conexion,Factura factura)throws SQLException{
        Statement statement = conexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resultset = statement.executeQuery("SELECT * FROM `linea_factura`");
        ArrayList<LineaFactura> lineasFactura = factura.getLineasFactura();
        for (LineaFactura lineaFactura : lineasFactura) {
            resultset.moveToInsertRow();
            resultset.updateString("COD_LINEA_FACTURA", lineaFactura.getCod_lineaVenta());
            resultset.updateString("VENTA", lineaFactura.getCod_factura());
            resultset.updateString("ARTICULO", lineaFactura.getArticulo().getCod_articulo());
            resultset.updateInt("CANTIDAD", lineaFactura.getCantidad());
            resultset.insertRow();
            Articulo.eliminarStockArticulo(conexion, lineaFactura.getArticulo().getCod_articulo(), lineaFactura.getCantidad());
        }  
    }
    public static void eliminarLineaFacturas(Connection conexion,String cod_factura)throws SQLException{
        Statement statement = conexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resultset = statement.executeQuery("SELECT * FROM `linea_factura` WHERE venta = '" + cod_factura + "'");
        while (resultset.next()){
            resultset.deleteRow();
        }
    }
    public void calcularSumaLinea(){
        if (descuento!=null){
            sumaSinIva = (articulo.getPrecio_unidad()* (1-descuento.getPorcentaje_descuento()/100.0))*cantidad;
        }else{
            sumaSinIva = articulo.getPrecio_unidad() * cantidad;  
        }
        sumaTotal = sumaSinIva * (articulo.getPorcentaje_iva()/100.0+1);
    }

    public Descuento getDescuento() {
        return descuento;
    }
    public String getCod_lineaVenta() {
        return cod_lineaVenta;
    }

    public String getCod_factura() {
        return cod_factura;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public int getCantidad() {
        return cantidad;
    }
    public double getSumaSinIva() {
        return Double.parseDouble(formatoDecimal.format(sumaSinIva).replace(',', '.'));
    }

    public double getSumaTotal() {
        return Double.parseDouble(formatoDecimal.format(sumaTotal).replace(',', '.'));
    }
    
}
