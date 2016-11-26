
package supermercado;

import com.itextpdf.text.DocumentException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class RegistroVentas extends javax.swing.JFrame {
    private Connection conexionBaseDatos;
    private DefaultTableModel modeloTablaFacturas;
    private DefaultTableModel modeloTablaLineasFactura;
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private ArrayList<Factura> facturas;
    private ArrayList<LineaFactura> lineasFactura;

    public RegistroVentas(Connection conexion) {
        initComponents();
        modeloTablaFacturas = (DefaultTableModel) tabla_facturas.getModel();
        modeloTablaLineasFactura = (DefaultTableModel) tabla_lineasFactura.getModel();
        conexionBaseDatos = conexion;
        cargarTablaFacturas();
    }
    private void actualizarFacturas(){
        try {
            facturas = Factura.cargarFacturas(conexionBaseDatos);
        } catch (SQLException ex) {
             JOptionPane.showMessageDialog(this, "Error al leer los datos en la base de datos:\n " + ex.getMessage(), "Error al leer", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void vaciarTablaFacturas(){
        while (modeloTablaFacturas.getRowCount()> 0){
            modeloTablaFacturas.removeRow(0);
        }
    }
    private void vaciarTablaLineasFactura(){
        while (modeloTablaLineasFactura.getRowCount()> 0){
            modeloTablaLineasFactura.removeRow(0);
        }
    }
    private void cargarTablaFacturas(){
        vaciarTablaFacturas();
        actualizarFacturas();
        for (Factura factura : facturas) {
            Object[] lineaTablaFacturas = {factura.getCod_factura(), factura.getCod_empleado(), formatoFecha.format(factura.getFecha())};
            modeloTablaFacturas.addRow(lineaTablaFacturas);
        }
        lb_totalFacturas.setText(facturas.size()+"");
        tabla_facturas.setRowSelectionInterval(0, 0);
        mostrarFacturaSeleccionada();
    }
    private void mostrarFacturaSeleccionada(){
        vaciarTablaLineasFactura();
        String cod_facturaSeleccionado =  (String) tabla_facturas.getValueAt(tabla_facturas.getSelectedRow(), 0);
        try {
            lineasFactura = LineaFactura.lineasFactura(conexionBaseDatos, cod_facturaSeleccionado);
            for (LineaFactura lineaFactura: lineasFactura) {
                Object[] lineaTablaLineasFactura = new Object[5];
                lineaFactura.calcularSumaLinea();
                Descuento descuento = lineaFactura.getDescuento();
                lineaTablaLineasFactura[0] = lineaFactura.getArticulo().getNombre();
                if (descuento != null){
                    lineaTablaLineasFactura[1] = descuento.getPorcentaje_descuento()+"%";  
                }else{
                    lineaTablaLineasFactura[1] = "-";
                }
                lineaTablaLineasFactura[2] = lineaFactura.getCantidad();
                lineaTablaLineasFactura[3] = lineaFactura.getArticulo().getPrecio_unidad();
                lineaTablaLineasFactura[4] = lineaFactura.getSumaSinIva();
                modeloTablaLineasFactura.addRow(lineaTablaLineasFactura);
            }
            for (Factura factura : facturas) {
                if (factura.getCod_factura().equals(cod_facturaSeleccionado)){
                    factura.calcularPrecios();
                    lb_totalSinIva.setText(factura.getTotalSinIva()+"€");
                    lb_Total.setText(factura.getTotal()+"€");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer el codigo de factura:\n " + ex.getMessage(), "Error al leer", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void eliminarFacturaSeleccionada(){
        String cod_facturaSeleccionado = (String) modeloTablaFacturas.getValueAt(tabla_facturas.getSelectedRow(), 0);
        int eleccion = JOptionPane.showConfirmDialog(this, "Se eliminará la factura con el código: " + cod_facturaSeleccionado + " ,¿desea continuar?" , 
                "Eliminar factura", JOptionPane.OK_CANCEL_OPTION);
        if (eleccion == JOptionPane.OK_OPTION){
            try {
                LineaFactura.eliminarLineaFacturas(conexionBaseDatos, cod_facturaSeleccionado);
                Statement statement = conexionBaseDatos.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet resultset = statement.executeQuery("SELECT * FROM factura WHERE COD_FACTURA = '" + cod_facturaSeleccionado + "'");
                resultset.next();
                resultset.deleteRow();
                JOptionPane.showMessageDialog(this, "Datos borrados", "Operación completada", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al borrar la factura:\n " + ex.getMessage(), "Error al eliminar", JOptionPane.WARNING_MESSAGE);
            }
            cargarTablaFacturas();
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scroll_tablaFacturas = new javax.swing.JScrollPane();
        tabla_facturas = new javax.swing.JTable();
        lb_facturaSeleccionada = new javax.swing.JLabel();
        lb_descTotalDescuentos = new javax.swing.JLabel();
        lb_totalFacturas = new javax.swing.JLabel();
        scroll_tablaLineasFactura = new javax.swing.JScrollPane();
        tabla_lineasFactura = new javax.swing.JTable();
        bt_eliminarFactura = new javax.swing.JButton();
        bt_buscarFactura = new javax.swing.JButton();
        lb_descTotalSinIva = new javax.swing.JLabel();
        lb_totalSinIva = new javax.swing.JLabel();
        lb_descTotal = new javax.swing.JLabel();
        lb_Total = new javax.swing.JLabel();
        bt_generarPDF = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Registro de ventas");

        tabla_facturas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "COD_Factura", "Empleado", "Fecha"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabla_facturas.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla_facturas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_facturasMouseClicked(evt);
            }
        });
        scroll_tablaFacturas.setViewportView(tabla_facturas);

        lb_facturaSeleccionada.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lb_facturaSeleccionada.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_facturaSeleccionada.setText("Factura seleccionada");
        lb_facturaSeleccionada.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lb_descTotalDescuentos.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lb_descTotalDescuentos.setText("Total de Facturas: ");

        lb_totalFacturas.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lb_totalFacturas.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb_totalFacturas.setText("0");

        tabla_lineasFactura.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tabla_lineasFactura.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Articulo", "Descuento", "Cantidad", "Precio unidad/kg", "Precio final"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabla_lineasFactura.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        scroll_tablaLineasFactura.setViewportView(tabla_lineasFactura);

        bt_eliminarFactura.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        bt_eliminarFactura.setText("Eliminar factura");
        bt_eliminarFactura.setMaximumSize(new java.awt.Dimension(117, 23));
        bt_eliminarFactura.setMinimumSize(new java.awt.Dimension(117, 23));
        bt_eliminarFactura.setPreferredSize(new java.awt.Dimension(117, 23));
        bt_eliminarFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_eliminarFacturaActionPerformed(evt);
            }
        });

        bt_buscarFactura.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        bt_buscarFactura.setText("Buscar factura");
        bt_buscarFactura.setMaximumSize(new java.awt.Dimension(117, 23));
        bt_buscarFactura.setMinimumSize(new java.awt.Dimension(117, 23));
        bt_buscarFactura.setPreferredSize(new java.awt.Dimension(117, 23));
        bt_buscarFactura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_buscarFacturaActionPerformed(evt);
            }
        });

        lb_descTotalSinIva.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lb_descTotalSinIva.setText("Total sin IVA: ");

        lb_totalSinIva.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lb_totalSinIva.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb_totalSinIva.setText("0");

        lb_descTotal.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lb_descTotal.setText("Total: ");

        lb_Total.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lb_Total.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb_Total.setText("0");

        bt_generarPDF.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        bt_generarPDF.setText("Generar PDF");
        bt_generarPDF.setMaximumSize(new java.awt.Dimension(117, 23));
        bt_generarPDF.setMinimumSize(new java.awt.Dimension(117, 23));
        bt_generarPDF.setPreferredSize(new java.awt.Dimension(117, 23));
        bt_generarPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_generarPDFActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lb_descTotalDescuentos, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lb_totalFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bt_buscarFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_eliminarFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(scroll_tablaFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lb_descTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lb_descTotalSinIva, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lb_Total, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lb_totalSinIva, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_generarPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lb_facturaSeleccionada, javax.swing.GroupLayout.PREFERRED_SIZE, 605, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 13, Short.MAX_VALUE))
                    .addComponent(scroll_tablaLineasFactura)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lb_facturaSeleccionada, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scroll_tablaLineasFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(scroll_tablaFacturas, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lb_descTotalDescuentos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lb_totalFacturas))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bt_buscarFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bt_eliminarFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lb_descTotalSinIva, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lb_totalSinIva))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lb_descTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lb_Total)
                            .addComponent(bt_generarPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tabla_facturasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_facturasMouseClicked
        if (tabla_facturas.getSelectedRow() != -1){
            mostrarFacturaSeleccionada();
        }
    }//GEN-LAST:event_tabla_facturasMouseClicked

    private void bt_eliminarFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_eliminarFacturaActionPerformed
        eliminarFacturaSeleccionada();
    }//GEN-LAST:event_bt_eliminarFacturaActionPerformed

    private void bt_buscarFacturaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_buscarFacturaActionPerformed
        String factABuscar = JOptionPane.showInputDialog(this, "Escriba el código de factura a buscar", "Busqueda de factura", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        if (factABuscar != null){
            boolean facturaEncontrada = false;
            for (int i = 0; i < tabla_facturas.getRowCount(); i++) {
               String cod_factura = (String) modeloTablaFacturas.getValueAt(i, 0);
               if (cod_factura.equals(factABuscar)){
                   facturaEncontrada = true;
                   tabla_facturas.setRowSelectionInterval(i, i);
                   mostrarFacturaSeleccionada();
                   break;
               }
            }
            if (facturaEncontrada == false){
               JOptionPane.showMessageDialog(this, "No hay ninguna factura con el código " + factABuscar, "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_bt_buscarFacturaActionPerformed

    private void bt_generarPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_generarPDFActionPerformed
        try {
            int eleccion = JOptionPane.showConfirmDialog(this, "¿Desea generar el fichero PDF?" , 
                "Generar PDF", JOptionPane.OK_CANCEL_OPTION);
            if (eleccion == JOptionPane.OK_OPTION){
               Factura.generarPDF(Factura.cargarFactura(conexionBaseDatos, (String) modeloTablaFacturas.getValueAt(tabla_facturas.getSelectedRow(), 0))); 
            }
        } catch (SQLException|DocumentException|IOException ex) {
           JOptionPane.showMessageDialog(this, "Error al crear una nueva factura:\n " + ex.getMessage(), "Error al cargar", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_bt_generarPDFActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RegistroVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistroVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistroVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistroVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegistroVentas(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bt_buscarFactura;
    private javax.swing.JButton bt_eliminarFactura;
    private javax.swing.JButton bt_generarPDF;
    private javax.swing.JLabel lb_Total;
    private javax.swing.JLabel lb_descTotal;
    private javax.swing.JLabel lb_descTotalDescuentos;
    private javax.swing.JLabel lb_descTotalSinIva;
    private javax.swing.JLabel lb_facturaSeleccionada;
    private javax.swing.JLabel lb_totalFacturas;
    private javax.swing.JLabel lb_totalSinIva;
    private javax.swing.JScrollPane scroll_tablaFacturas;
    private javax.swing.JScrollPane scroll_tablaLineasFactura;
    private javax.swing.JTable tabla_facturas;
    private javax.swing.JTable tabla_lineasFactura;
    // End of variables declaration//GEN-END:variables
}
