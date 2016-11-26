
package supermercado;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class AdministrarDescuentos extends javax.swing.JFrame {

    private ArrayList<Descuento> descuentos;
    
    private DefaultTableModel modeloTabla;
    private Connection conexionBaseDatos;
    private byte[] imagenCategoria;
    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    private boolean articuloComprobado = false;
    public AdministrarDescuentos(Connection conexion) {
        initComponents();
        modeloTabla = (DefaultTableModel) tabla_descuentos.getModel();
        conexionBaseDatos = conexion;
        cargarTablaDescuentos();
    }
    private void actualizarDescuentos(){
        try {
            descuentos = Descuento.cargarDescuentos(conexionBaseDatos);
            articuloComprobado = false;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer los datos en la base de datos:\n " + ex.getMessage(), "Error al leer", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void vaciarTablaDescuento(){
        while (modeloTabla.getRowCount()>0){
            modeloTabla.removeRow(0);
        }
    }
    private void cargarTablaDescuentos(){
        vaciarTablaDescuento();
        actualizarDescuentos();
        for (Descuento descuento : descuentos) {
            Object[] lineaTabla = {descuento.getCod_descuento(), descuento.getCod_articulo(), descuento.getPorcentaje_descuento(), formatoFecha.format(descuento.getFecha_validez())};
            modeloTabla.addRow(lineaTabla);
        }
        lb_totalDescuentos.setText(descuentos.size()+"");
        tabla_descuentos.setRowSelectionInterval(0, 0);
        mostrarDescuentoSeleccionado();
    }
    private void mostrarDescuentoSeleccionado(){
         String codDescuentoSeleccionado = (String) modeloTabla.getValueAt(tabla_descuentos.getSelectedRow(), 0);
         for (Descuento descuento : descuentos) {
            if (descuento.getCod_descuento().equals(codDescuentoSeleccionado)){
                txf_codDescuento.setText(descuento.getCod_descuento());
                txf_articulo.setText(descuento.getCod_articulo());
                fmf_porcentaje.setText(descuento.getPorcentaje_descuento()+"");
                fmf_fechaValidez.setText(formatoFecha.format(descuento.getFecha_validez()));
            }
        }
    }
    private void activar_desactivarCampos(boolean estado){
        txf_codDescuento.setEditable(estado);
        txf_articulo.setEditable(estado);
        bt_comprobarArticulo.setEnabled(estado);    
        fmf_porcentaje.setEditable(estado);
        fmf_fechaValidez.setEditable(estado);
    }
    private void vaciarCampos(){
        txf_codDescuento.setText("");
        txf_articulo.setText("");
        fmf_porcentaje.setText("");
        fmf_fechaValidez.setText("");
    }
    private void modificarDescuento(){
        if (articuloComprobado){
                String cod_descuento = txf_codDescuento.getText();
            try {
                Statement statement = conexionBaseDatos.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                ResultSet resultset = statement.executeQuery("SELECT * FROM descuento WHERE COD_DESCUENTO = '" + cod_descuento + "'");
                resultset.next();

                resultset.updateString("COD_DESCUENTO", cod_descuento);
                resultset.updateString("ARTICULO", txf_articulo.getText());
                resultset.updateInt("PORCENTAJE_DESCUENTO", Integer.parseInt(fmf_porcentaje.getText()));
                resultset.updateDate("FECHA_VALIDEZ", new Date(formatoFecha.parse(fmf_fechaValidez.getText()).getTime()));
                resultset.updateRow();
                articuloComprobado = false;
                JOptionPane.showMessageDialog(this, "Datos modificados correctamente", "Completado", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al modificar los datos en la base de datos:\n " + ex.getMessage(), "Error al modificar", JOptionPane.WARNING_MESSAGE);
            } catch (NumberFormatException ex){
                JOptionPane.showMessageDialog(this, "Algun campo contiene un formato de numero incorrecto", "Error al modificar", JOptionPane.WARNING_MESSAGE);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "La fecha de validez introducida no es correcta", "Error al modificar", JOptionPane.WARNING_MESSAGE);
            }
        }else{
             JOptionPane.showMessageDialog(this, "Debe verificar el codigo de articulo", "Error al modificar", JOptionPane.WARNING_MESSAGE);
        }
    }
     private void nuevoDescuento(){
         if (articuloComprobado){
            try {
                Statement statement = conexionBaseDatos.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                ResultSet resultset = statement.executeQuery("SELECT * FROM descuento");
                resultset.moveToInsertRow();
                resultset.updateString("COD_DESCUENTO", txf_codDescuento.getText());
                resultset.updateString("ARTICULO", txf_articulo.getText());
                resultset.updateInt("PORCENTAJE_DESCUENTO", Integer.parseInt(fmf_porcentaje.getText()));
                resultset.updateDate("FECHA_VALIDEZ", new Date(formatoFecha.parse(fmf_fechaValidez.getText()).getTime()));
                resultset.insertRow();
                articuloComprobado = false;
                JOptionPane.showMessageDialog(this, "Datos insertados correctamente", "Completado", JOptionPane.INFORMATION_MESSAGE);
                 
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al modificar los datos en la base de datos:\n " + ex.getMessage(), "Error al insertar", JOptionPane.WARNING_MESSAGE);
            } catch (NumberFormatException ex){
                JOptionPane.showMessageDialog(this, "Algun campo contiene un formato de numero incorrecto", "Error al insertar", JOptionPane.WARNING_MESSAGE);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(this, "La fecha de validez introducida no es correcta", "Error al insertar", JOptionPane.WARNING_MESSAGE);
            }
         }else{
             JOptionPane.showMessageDialog(this, "Debe verificar el codigo de articulo", "Error al insertar", JOptionPane.WARNING_MESSAGE);
        }
     }
     private void eliminarDescuento(){
        String cod_descuentoSeleccionado = (String) tabla_descuentos.getValueAt(tabla_descuentos.getSelectedRow(), 0);
        int eleccion = JOptionPane.showConfirmDialog(this, "Se eliminará el descuento con el código: " + cod_descuentoSeleccionado + " ,¿desea continuar?" , 
                "Eliminar descuento", JOptionPane.OK_CANCEL_OPTION);
        if (eleccion == JOptionPane.OK_OPTION){
            try {
                Statement statement = conexionBaseDatos.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet resultset = statement.executeQuery("SELECT * FROM descuento WHERE COD_DESCUENTO = '" + cod_descuentoSeleccionado + "'");
                resultset.next();
                resultset.deleteRow();
                JOptionPane.showMessageDialog(this, "Datos borrados", "Operación completada", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al borrar el descuento:\n " + ex.getMessage(), "Error al eliminar", JOptionPane.WARNING_MESSAGE);
            }
            cargarTablaDescuentos();
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

        lb_descuentoSeleccionado = new javax.swing.JLabel();
        scroll_tablaDescuentos = new javax.swing.JScrollPane();
        tabla_descuentos = new javax.swing.JTable();
        lb_descTotalDescuentos = new javax.swing.JLabel();
        lb_totalDescuentos = new javax.swing.JLabel();
        lb_articulo = new javax.swing.JLabel();
        lb_codDescuento = new javax.swing.JLabel();
        lb_porcentaje = new javax.swing.JLabel();
        lb_fechaValidez = new javax.swing.JLabel();
        txf_codDescuento = new javax.swing.JTextField();
        txf_articulo = new javax.swing.JTextField();
        fmf_porcentaje = new javax.swing.JFormattedTextField();
        fmf_fechaValidez = new javax.swing.JFormattedTextField();
        bt_buscarDescuento = new javax.swing.JButton();
        tgbt_editarDescuento = new javax.swing.JToggleButton();
        tgbt_nuevoDescuento = new javax.swing.JToggleButton();
        bt_eliminarDescuento = new javax.swing.JButton();
        bt_comprobarArticulo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Administración de descuentos");

        lb_descuentoSeleccionado.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lb_descuentoSeleccionado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_descuentoSeleccionado.setText("Descuento seleccionado");
        lb_descuentoSeleccionado.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        tabla_descuentos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "COD_Descuento", "Articulo", "Porcentaje", "Validez"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabla_descuentos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla_descuentos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_descuentosMouseClicked(evt);
            }
        });
        scroll_tablaDescuentos.setViewportView(tabla_descuentos);

        lb_descTotalDescuentos.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lb_descTotalDescuentos.setText("Total de descuentos: ");

        lb_totalDescuentos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lb_totalDescuentos.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb_totalDescuentos.setText("0");

        lb_articulo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_articulo.setText("Articulo");

        lb_codDescuento.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_codDescuento.setText("COD_Descuento");

        lb_porcentaje.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_porcentaje.setText("Porcentaje");

        lb_fechaValidez.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_fechaValidez.setText("Fecha validez");

        txf_codDescuento.setEditable(false);

        txf_articulo.setEditable(false);

        fmf_porcentaje.setEditable(false);
        fmf_porcentaje.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        fmf_fechaValidez.setEditable(false);
        fmf_fechaValidez.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("dd/MM/yyyy"))));

        bt_buscarDescuento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        bt_buscarDescuento.setText("Buscar descuento");
        bt_buscarDescuento.setMaximumSize(new java.awt.Dimension(117, 23));
        bt_buscarDescuento.setMinimumSize(new java.awt.Dimension(117, 23));
        bt_buscarDescuento.setPreferredSize(new java.awt.Dimension(117, 23));
        bt_buscarDescuento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_buscarDescuentoActionPerformed(evt);
            }
        });

        tgbt_editarDescuento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tgbt_editarDescuento.setText("Editar descuento");
        tgbt_editarDescuento.setMaximumSize(new java.awt.Dimension(117, 23));
        tgbt_editarDescuento.setMinimumSize(new java.awt.Dimension(117, 23));
        tgbt_editarDescuento.setPreferredSize(new java.awt.Dimension(117, 23));
        tgbt_editarDescuento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgbt_editarDescuentoActionPerformed(evt);
            }
        });

        tgbt_nuevoDescuento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tgbt_nuevoDescuento.setText("Nuevo descuento");
        tgbt_nuevoDescuento.setMaximumSize(new java.awt.Dimension(117, 23));
        tgbt_nuevoDescuento.setMinimumSize(new java.awt.Dimension(117, 23));
        tgbt_nuevoDescuento.setPreferredSize(new java.awt.Dimension(117, 23));
        tgbt_nuevoDescuento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgbt_nuevoDescuentoActionPerformed(evt);
            }
        });

        bt_eliminarDescuento.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        bt_eliminarDescuento.setText("Eliminar descuento");
        bt_eliminarDescuento.setMaximumSize(new java.awt.Dimension(117, 23));
        bt_eliminarDescuento.setMinimumSize(new java.awt.Dimension(117, 23));
        bt_eliminarDescuento.setPreferredSize(new java.awt.Dimension(117, 23));
        bt_eliminarDescuento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_eliminarDescuentoActionPerformed(evt);
            }
        });

        bt_comprobarArticulo.setText("Comprobar articulo");
        bt_comprobarArticulo.setEnabled(false);
        bt_comprobarArticulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_comprobarArticuloActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(scroll_tablaDescuentos, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lb_descuentoSeleccionado, javax.swing.GroupLayout.PREFERRED_SIZE, 549, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lb_fechaValidez, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lb_porcentaje, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lb_articulo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lb_codDescuento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txf_codDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(fmf_porcentaje, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txf_articulo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                                            .addComponent(fmf_fechaValidez, javax.swing.GroupLayout.Alignment.LEADING))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(bt_comprobarArticulo)))
                                .addGap(89, 89, 89))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(lb_descTotalDescuentos, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lb_totalDescuentos, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(bt_buscarDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgbt_editarDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgbt_nuevoDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_eliminarDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lb_descuentoSeleccionado, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lb_codDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txf_codDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(30, 30, 30)
                                .addComponent(lb_articulo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txf_articulo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(bt_comprobarArticulo)))
                        .addGap(33, 33, 33)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lb_porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fmf_porcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lb_fechaValidez, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fmf_fechaValidez, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(scroll_tablaDescuentos, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(bt_buscarDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tgbt_nuevoDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(bt_eliminarDescuento, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tgbt_editarDescuento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 5, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lb_descTotalDescuentos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lb_totalDescuentos)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tabla_descuentosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_descuentosMouseClicked
        if (tabla_descuentos.getSelectedRow()!= -1){
            mostrarDescuentoSeleccionado();
        }
    }//GEN-LAST:event_tabla_descuentosMouseClicked

    private void bt_buscarDescuentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_buscarDescuentoActionPerformed
        String descABuscar = JOptionPane.showInputDialog(this, "Escriba el código del descuento a buscar", "Busqueda de descuento", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        if (descABuscar != null){
            boolean descuentoEncontrado = false;
            for (int i = 0; i < tabla_descuentos.getRowCount(); i++) {
                String cod_descuento = (String) modeloTabla.getValueAt(i, 0);
                if (cod_descuento.equals(descABuscar)){
                    descuentoEncontrado = true;
                    tabla_descuentos.setRowSelectionInterval(i, i);
                    mostrarDescuentoSeleccionado();
                    break;
                }
            }
             if (descuentoEncontrado == false){
                JOptionPane.showMessageDialog(this, "No hay ningun descuento con el código " + descABuscar, "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_bt_buscarDescuentoActionPerformed

    private void tgbt_editarDescuentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgbt_editarDescuentoActionPerformed
        if (tgbt_editarDescuento.isSelected()){
            tabla_descuentos.setEnabled(false);
            tgbt_nuevoDescuento.setEnabled(false);
            bt_eliminarDescuento.setEnabled(false);
            bt_buscarDescuento.setEnabled(false);
            activar_desactivarCampos(true);
        }else{
            modificarDescuento();
            cargarTablaDescuentos();
            tabla_descuentos.setEnabled(true);
            tgbt_nuevoDescuento.setEnabled(true);
            bt_eliminarDescuento.setEnabled(true);
            bt_buscarDescuento.setEnabled(true);
            activar_desactivarCampos(false);
        }
    }//GEN-LAST:event_tgbt_editarDescuentoActionPerformed

    private void tgbt_nuevoDescuentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgbt_nuevoDescuentoActionPerformed
        if (tgbt_nuevoDescuento.isSelected()){
            tabla_descuentos.setEnabled(false);
            tgbt_editarDescuento.setEnabled(false);
            bt_eliminarDescuento.setEnabled(false);
            bt_buscarDescuento.setEnabled(false);
            activar_desactivarCampos(true);
            vaciarCampos();
            JOptionPane.showMessageDialog(this, "Preparado para añadir un nuevo descuento, recuerda que debes verificar el articulo", "Información", JOptionPane.INFORMATION_MESSAGE);
        }else{
            nuevoDescuento();
            cargarTablaDescuentos();
            tabla_descuentos.setEnabled(true);
            bt_eliminarDescuento.setEnabled(true);
            tgbt_editarDescuento.setEnabled(true);
            bt_buscarDescuento.setEnabled(true);
            activar_desactivarCampos(false); 
        }
    }//GEN-LAST:event_tgbt_nuevoDescuentoActionPerformed

    private void bt_eliminarDescuentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_eliminarDescuentoActionPerformed
        eliminarDescuento();
    }//GEN-LAST:event_bt_eliminarDescuentoActionPerformed

    private void bt_comprobarArticuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_comprobarArticuloActionPerformed
        String cod_articulo = txf_articulo.getText();
        try {
            articuloComprobado = Articulo.obtenerArticulo(conexionBaseDatos, cod_articulo) != null;
            if (articuloComprobado){
                JOptionPane.showMessageDialog(this, "Articulo verificado correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "El codigo de articulo indicado no existe", "Sin resultados", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_bt_comprobarArticuloActionPerformed

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
            java.util.logging.Logger.getLogger(AdministrarDescuentos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdministrarDescuentos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdministrarDescuentos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdministrarDescuentos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdministrarDescuentos(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bt_buscarDescuento;
    private javax.swing.JButton bt_comprobarArticulo;
    private javax.swing.JButton bt_eliminarDescuento;
    private javax.swing.JFormattedTextField fmf_fechaValidez;
    private javax.swing.JFormattedTextField fmf_porcentaje;
    private javax.swing.JLabel lb_articulo;
    private javax.swing.JLabel lb_codDescuento;
    private javax.swing.JLabel lb_descTotalDescuentos;
    private javax.swing.JLabel lb_descuentoSeleccionado;
    private javax.swing.JLabel lb_fechaValidez;
    private javax.swing.JLabel lb_porcentaje;
    private javax.swing.JLabel lb_totalDescuentos;
    private javax.swing.JScrollPane scroll_tablaDescuentos;
    private javax.swing.JTable tabla_descuentos;
    private javax.swing.JToggleButton tgbt_editarDescuento;
    private javax.swing.JToggleButton tgbt_nuevoDescuento;
    private javax.swing.JTextField txf_articulo;
    private javax.swing.JTextField txf_codDescuento;
    // End of variables declaration//GEN-END:variables
}
