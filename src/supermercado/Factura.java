
package supermercado;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


public class Factura {
    private String cod_factura;
    private String cod_empleado;
    private java.util.Date fecha;
    private ArrayList<LineaFactura> lineasFactura;
    private double totalSinIva;
    private double total;
    private DecimalFormat formatoDecimal = new DecimalFormat("#0.00");
    public Factura(String cod_factura, String cod_empleado, Date fecha, ArrayList<LineaFactura> lineasFactura) {
        this.cod_factura = cod_factura;
        this.cod_empleado = cod_empleado;
        this.fecha = fecha;
        this.lineasFactura = lineasFactura;
    }
    public static ArrayList<Factura> cargarFacturas(Connection conexion)  throws SQLException{
        ArrayList<Factura> facturas = new ArrayList<>();
        Statement statement = conexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resultset = statement.executeQuery("SELECT * FROM `factura` ");
        while (resultset.next()){
             String cod_factura = resultset.getString("COD_FACTURA");
             String cod_empleado = resultset.getString("EMPLEADO");
             java.util.Date fecha = new Date(resultset.getTimestamp("FECHA").getTime());
             ArrayList<LineaFactura> lineasFactura = LineaFactura.lineasFactura(conexion, cod_factura);
             Factura nuevaFactura = new Factura(cod_factura, cod_empleado, fecha, lineasFactura);
             facturas.add(nuevaFactura);
        }
        return facturas;
    }
    public static Factura cargarFactura(Connection conexion, String cod_factura)  throws SQLException{
        Statement statement = conexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resultset = statement.executeQuery("SELECT * FROM `factura` WHERE cod_factura = '" + cod_factura + "'");
        if (resultset.next()){
            String cod_empleado = resultset.getString("EMPLEADO");
            java.util.Date fecha = new Date(resultset.getTimestamp("FECHA").getTime());
            ArrayList<LineaFactura> lineasFactura = LineaFactura.lineasFactura(conexion, cod_factura);
            Factura nuevaFactura = new Factura(cod_factura, cod_empleado, fecha, lineasFactura);
            return nuevaFactura;
        }else{
            return null;
        }
    }
    public static int obtenerNumSiguienteFactura(Connection conexion)throws SQLException{
        Statement statement = conexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resultset = statement.executeQuery("SELECT count(*) FROM `factura` ");
        resultset.next();
        return resultset.getInt(1)+1;
    }
    public static void anadirNuevaFactura(Connection conexion, Factura factura) throws SQLException{
        Statement statement = conexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet resultset = statement.executeQuery("SELECT * FROM `factura` ");
        resultset.moveToInsertRow();
        resultset.updateString("COD_FACTURA", factura.getCod_factura());
        resultset.updateString("EMPLEADO", factura.getCod_empleado());
        resultset.updateTimestamp("FECHA", new Timestamp(factura.getFecha().getTime()));
        resultset.insertRow();
        LineaFactura.anadirLineasFacturas(conexion, factura);
    }
    public static void generarPDF(Factura factura) throws FileNotFoundException, DocumentException, IOException{
        factura.calcularPrecios();
        JFileChooser elegirRuta = new JFileChooser();
        int eleccion = elegirRuta.showSaveDialog(null);
        if (eleccion == JFileChooser.APPROVE_OPTION){
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            ArrayList<LineaFactura> lineasFactura = factura.getLineasFactura();
            FileOutputStream escribirPDF = new FileOutputStream(elegirRuta.getSelectedFile());
            Document documento = new Document();
            PdfWriter.getInstance(documento,escribirPDF).setInitialLeading(20);
            documento.open();
            
            documento.add(new Paragraph("FACTURA: " + factura.getCod_factura() + "      Empleado: " + factura.getCod_empleado() + "      Fecha: " + formatoFecha.format(factura.getFecha()), 
                FontFactory.getFont("Calibri", // fuente 
                14, // tamaño 
                Font.ITALIC, // estilo 
                BaseColor.BLACK))); // color 
            documento.add(new Paragraph("Artículos:", 
                FontFactory.getFont("arial", // fuente 
                22, // tamaño 
                BaseColor.RED))); // color 
            documento.add(new Paragraph());
            PdfPTable tabla = new PdfPTable(5);
            tabla.addCell("Artículo"); 
            tabla.addCell("Descuento"); 
            tabla.addCell("Cantidad"); 
            tabla.addCell("Precio unidad/kg");
            tabla.addCell("Precio final"); 
           
            for (LineaFactura lineaFactura : lineasFactura) {
                lineaFactura.calcularSumaLinea();
                tabla.addCell(lineaFactura.getArticulo().getNombre().replace('_', ' ')); 
                if (lineaFactura.getDescuento() != null){
                    tabla.addCell(lineaFactura.getDescuento().getPorcentaje_descuento()+"%");  
                }else{
                     tabla.addCell("-");  
                }
                tabla.addCell(lineaFactura.getCantidad()+""); 
                tabla.addCell(lineaFactura.getArticulo().getPrecio_unidad()+"€");
                tabla.addCell(lineaFactura.getSumaSinIva()+"€"); 
            }
            documento.add(tabla);
            documento.add(new Paragraph("Precio total sin IVA: " + factura.getTotalSinIva() + "€", 
                FontFactory.getFont("arial", // fuente 
                22, // tamaño 
                BaseColor.BLACK))); // color 
            documento.add(new Paragraph("Precio total: " + factura.getTotal() + "€", 
                FontFactory.getFont("arial", // fuente 
                22, // tamaño 
                BaseColor.BLACK))); // color 
            documento.close();
            int elegirAbrir = JOptionPane.showConfirmDialog(null, "¿Quieres abrir el archivo PDF de la factura generada?" , 
                "Abrir PDF", JOptionPane.OK_CANCEL_OPTION);
            if (elegirAbrir == JOptionPane.OK_OPTION){
                Desktop.getDesktop().open(elegirRuta.getSelectedFile());
            }
        }
    }
    public void calcularPrecios(){
        totalSinIva = 0;
        total = 0;
        for (LineaFactura lineaFactura : lineasFactura) {
            lineaFactura.calcularSumaLinea();
            totalSinIva+= lineaFactura.getSumaSinIva();
            total+= lineaFactura.getSumaTotal();
        }
    }
    public void anadirLineaFactura(LineaFactura nuevaLineaFactura){
        lineasFactura.add(nuevaLineaFactura);
    }
    public String getCod_factura() {
        return cod_factura;
    }

    public String getCod_empleado() {
        return cod_empleado;
    }

    public Date getFecha() {
        return fecha;
    }

    public ArrayList<LineaFactura> getLineasFactura() {
        return lineasFactura;
    }
     public double getTotalSinIva() {
       return Double.parseDouble(formatoDecimal.format(totalSinIva).replace(',', '.'));
    }

    public double getTotal() {
        return Double.parseDouble(formatoDecimal.format(total).replace(',', '.'));
    }
}
