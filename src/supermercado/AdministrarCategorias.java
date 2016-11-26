
package supermercado;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.sql.rowset.serial.SerialBlob;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class AdministrarCategorias extends javax.swing.JFrame {
    private ArrayList<Categoria> categorias;
    private ArrayList<Empleado> encargados;
    private DefaultTableModel modeloTabla;
    private Connection conexionBaseDatos;
    private byte[] imagenCategoria;
    boolean imagenCambiada = false;
    public AdministrarCategorias(Connection conexion) {
        initComponents();
        modeloTabla = (DefaultTableModel) tabla_categorias.getModel();
        conexionBaseDatos = conexion;
        cargarTablaCategorias();
    }
    private void actualizarEncargados(){

        try {
            encargados = Empleado.cargarEncargados(conexionBaseDatos);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer los datos en la base de datos:\n " + ex.getMessage(), "Error al leer", JOptionPane.WARNING_MESSAGE);
        }

        //Rellenamos el comboBox de encargados
        DefaultComboBoxModel<Empleado> modeloCombo = (DefaultComboBoxModel) cmb_encargados.getModel();
        modeloCombo.removeAllElements();
        for (Empleado encargado : encargados) {
            modeloCombo.addElement(encargado);
        }
    }
    private void actualizarCategorias(){
        actualizarEncargados();
        try {
            categorias = Categoria.cargarCategorias(conexionBaseDatos, encargados);
            imagenCambiada = false;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer los datos en la base de datos:\n " + ex.getMessage(), "Error al leer", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cargarTablaCategorias(){
        vaciarTablaCategoria();
        actualizarCategorias();
        
        for (Categoria categoria : categorias) {
            Object[] lineaTablaCategoria  = {categoria.getCod_categoria(), categoria.getNombre(), categoria.getResponsable().getCod_empleado()};
            modeloTabla.addRow(lineaTablaCategoria);
        }
        lb_totalCategorias.setText(categorias.size()+"");
        tabla_categorias.setRowSelectionInterval(0, 0);
        mostrarCategoriaSeleccionada();
        cmb_encargados.setEnabled(false);
    }
    private void vaciarTablaCategoria(){
        while (modeloTabla.getRowCount()>0){
            modeloTabla.removeRow(0);
        }
    }
    private void mostrarCategoriaSeleccionada(){
        String codCategoriaSeleccionada = (String) modeloTabla.getValueAt(tabla_categorias.getSelectedRow(), 0);
        
        for (Categoria categoria : categorias) {
            if (categoria.getCod_categoria().equals(codCategoriaSeleccionada)){
                txf_codCategoria.setText(categoria.getCod_categoria());
                txf_nombreCategoria.setText(categoria.getNombre());
                cmb_encargados.setSelectedItem(categoria.getResponsable());
                lb_imagenCategoria.setIcon(categoria.getImagen());
            }
        }
    }
    private void activar_desactivarCampos(boolean estado){
        txf_codCategoria.setEditable(estado);
        txf_nombreCategoria.setEditable(estado);
        cmb_encargados.setEnabled(estado);
    }
    private void vaciarCampos(){
        txf_codCategoria.setText("");
        txf_nombreCategoria.setText("");
        cmb_encargados.setSelectedIndex(0);
        lb_imagenCategoria.setIcon(null);
    }
    private void modificarCategoria(){
        String cod_categoria = txf_codCategoria.getText();
        try {
            Statement statement = conexionBaseDatos.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resulset = statement.executeQuery("SELECT * FROM `categoria` WHERE COD_CATEGORIA = '" + cod_categoria + "'");
            resulset.next();
            
            resulset.updateString("COD_CATEGORIA", cod_categoria);
            resulset.updateString("NOMBRE", txf_nombreCategoria.getText());
            Empleado encargadoSeleccionado = (Empleado) cmb_encargados.getSelectedItem();
            resulset.updateString("RESPONSABLE", encargadoSeleccionado.getCod_empleado());
             //Si la imagen ha sido editada subimos la foto guardada en la variable global
            if (imagenCambiada){
                SerialBlob crearImagenCategoria = new SerialBlob(imagenCategoria);
                resulset.updateBlob("IMAGEN", crearImagenCategoria);
            }
            resulset.updateRow();
            imagenCambiada = false;
            JOptionPane.showMessageDialog(this, "Datos modificados correctamente", "Completado", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al modificar los datos en la base de datos:\n " + ex.getMessage(), "Error al insertar", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void nuevaCategoria(){
        if (imagenCambiada){
            try {
                Statement statement = conexionBaseDatos.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                ResultSet resulset = statement.executeQuery("SELECT * FROM `categoria`");
                resulset.moveToInsertRow();
                
                resulset.updateString("COD_CATEGORIA", txf_codCategoria.getText());
                resulset.updateString("NOMBRE", txf_nombreCategoria.getText());
                
                Empleado encargadoSeleccionado = (Empleado) cmb_encargados.getSelectedItem();
                resulset.updateString("RESPONSABLE", encargadoSeleccionado.getCod_empleado());
                
                SerialBlob crearImagenCategoria = new SerialBlob(imagenCategoria);
                resulset.updateBlob("IMAGEN", crearImagenCategoria);
                
                resulset.insertRow();
                imagenCambiada = false;
                JOptionPane.showMessageDialog(this, "Datos insertados correctamente", "Completado", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al insertar los datos en la base de datos:\n " + ex.getMessage(), "Error al insertar", JOptionPane.WARNING_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(this, "La nueva categoria no ha sido añadida porque no se ha añadido una foto", "Error al insertar", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void eliminarCategoria(){
        String cod_categoriaSeleccionada = (String) modeloTabla.getValueAt(tabla_categorias.getSelectedRow(), 0);
        int eleccion = JOptionPane.showConfirmDialog(this, "Se eliminará la categoria con el código: " + cod_categoriaSeleccionada + " , es posible que de error si algun artículo "
                + "está asociado con esta categoria, ¿desea continuar?" , "Eliminar categoria " + cod_categoriaSeleccionada, JOptionPane.OK_CANCEL_OPTION);
        
        if (eleccion == JOptionPane.OK_OPTION){
            try {
                Statement statement = conexionBaseDatos.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet resultSet = statement.executeQuery("SELECT * FROM categoria WHERE COD_CATEGORIA = '" + cod_categoriaSeleccionada + "'");
                resultSet.next();
                resultSet.deleteRow();
                JOptionPane.showMessageDialog(this, "Datos borrados", "Operación completada", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al borrar la categoria:\n " + ex.getMessage(), "Error al eliminar", JOptionPane.WARNING_MESSAGE);
            }
            cargarTablaCategorias();
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

        lb_descTotalCategorias = new javax.swing.JLabel();
        lb_categoriaSeleccionada = new javax.swing.JLabel();
        bt_buscarCategoria = new javax.swing.JButton();
        tgbt_editarCategoria = new javax.swing.JToggleButton();
        tgbt_nuevaCategoria = new javax.swing.JToggleButton();
        bt_eliminarCategoria = new javax.swing.JButton();
        lb_totalCategorias = new javax.swing.JLabel();
        lb_codCategoria = new javax.swing.JLabel();
        lb_dniEmpleado = new javax.swing.JLabel();
        lb_nombreEmpleado = new javax.swing.JLabel();
        scroll_tablaCategorias = new javax.swing.JScrollPane();
        tabla_categorias = new javax.swing.JTable();
        txf_codCategoria = new javax.swing.JTextField();
        txf_nombreCategoria = new javax.swing.JTextField();
        cmb_encargados = new javax.swing.JComboBox<>();
        scroll_imagenCategoria = new javax.swing.JScrollPane();
        lb_imagenCategoria = new javax.swing.JLabel();
        lb_descimagenCategoria = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Administración de cagetorias");

        lb_descTotalCategorias.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lb_descTotalCategorias.setText("Total de categorias: ");

        lb_categoriaSeleccionada.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lb_categoriaSeleccionada.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_categoriaSeleccionada.setText("Categoria seleccionada");
        lb_categoriaSeleccionada.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        bt_buscarCategoria.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        bt_buscarCategoria.setText("Buscar categoria");
        bt_buscarCategoria.setMaximumSize(new java.awt.Dimension(117, 23));
        bt_buscarCategoria.setMinimumSize(new java.awt.Dimension(117, 23));
        bt_buscarCategoria.setPreferredSize(new java.awt.Dimension(117, 23));
        bt_buscarCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_buscarCategoriaActionPerformed(evt);
            }
        });

        tgbt_editarCategoria.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tgbt_editarCategoria.setText("Editar categoria");
        tgbt_editarCategoria.setMaximumSize(new java.awt.Dimension(117, 23));
        tgbt_editarCategoria.setMinimumSize(new java.awt.Dimension(117, 23));
        tgbt_editarCategoria.setPreferredSize(new java.awt.Dimension(117, 23));
        tgbt_editarCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgbt_editarCategoriaActionPerformed(evt);
            }
        });

        tgbt_nuevaCategoria.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tgbt_nuevaCategoria.setText("Nueva categoria");
        tgbt_nuevaCategoria.setMaximumSize(new java.awt.Dimension(117, 23));
        tgbt_nuevaCategoria.setMinimumSize(new java.awt.Dimension(117, 23));
        tgbt_nuevaCategoria.setPreferredSize(new java.awt.Dimension(117, 23));
        tgbt_nuevaCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgbt_nuevaCategoriaActionPerformed(evt);
            }
        });

        bt_eliminarCategoria.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        bt_eliminarCategoria.setText("Eliminar categoria");
        bt_eliminarCategoria.setMaximumSize(new java.awt.Dimension(117, 23));
        bt_eliminarCategoria.setMinimumSize(new java.awt.Dimension(117, 23));
        bt_eliminarCategoria.setPreferredSize(new java.awt.Dimension(117, 23));
        bt_eliminarCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_eliminarCategoriaActionPerformed(evt);
            }
        });

        lb_totalCategorias.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lb_totalCategorias.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb_totalCategorias.setText("0");

        lb_codCategoria.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_codCategoria.setText("COD_Categoria");

        lb_dniEmpleado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_dniEmpleado.setText("Nombre");

        lb_nombreEmpleado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_nombreEmpleado.setText("Encargado");

        tabla_categorias.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "COD_Categoria", "Nombre", "Encargado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class
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
        tabla_categorias.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla_categorias.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_categoriasMouseClicked(evt);
            }
        });
        scroll_tablaCategorias.setViewportView(tabla_categorias);

        txf_codCategoria.setEditable(false);

        txf_nombreCategoria.setEditable(false);

        cmb_encargados.setModel(new DefaultComboBoxModel<>());

        lb_imagenCategoria.setBackground(new java.awt.Color(255, 255, 255));
        lb_imagenCategoria.setOpaque(true);
        lb_imagenCategoria.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lb_imagenCategoriaMouseClicked(evt);
            }
        });
        scroll_imagenCategoria.setViewportView(lb_imagenCategoria);

        lb_descimagenCategoria.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_descimagenCategoria.setText("Imágen de la categoria");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scroll_tablaCategorias, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lb_nombreEmpleado, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                            .addComponent(lb_codCategoria, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lb_dniEmpleado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lb_descimagenCategoria, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txf_nombreCategoria, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                                .addComponent(txf_codCategoria)
                                .addComponent(scroll_imagenCategoria, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cmb_encargados, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lb_descTotalCategorias, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lb_totalCategorias, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_buscarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgbt_editarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgbt_nuevaCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_eliminarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(22, 22, 22))
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(351, 351, 351)
                    .addComponent(lb_categoriaSeleccionada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lb_codCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txf_codCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(33, 33, 33)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lb_dniEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txf_nombreCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(35, 35, 35)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmb_encargados)
                            .addComponent(lb_nombreEmpleado, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
                        .addGap(35, 35, 35)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lb_descimagenCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(scroll_imagenCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(scroll_tablaCategorias, javax.swing.GroupLayout.PREFERRED_SIZE, 464, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(bt_buscarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tgbt_nuevaCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(bt_eliminarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lb_totalCategorias)
                        .addComponent(lb_descTotalCategorias, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tgbt_editarCategoria, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lb_categoriaSeleccionada, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(500, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tabla_categoriasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_categoriasMouseClicked
        if (tabla_categorias.getSelectedRow() != -1){
            mostrarCategoriaSeleccionada();
        }
    }//GEN-LAST:event_tabla_categoriasMouseClicked

    private void bt_buscarCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_buscarCategoriaActionPerformed
        String catAbuscar = JOptionPane.showInputDialog(this, "Escriba el código de categoria a buscar", "Busqueda de categoria", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        if (catAbuscar!= null){
            boolean categoriaEncontrada = false;
            for (int i=0; i< modeloTabla.getRowCount(); i++){
                String cod_categoria = (String) modeloTabla.getValueAt(i, 0);
                if (catAbuscar.equals(cod_categoria)){
                    categoriaEncontrada = true;
                    tabla_categorias.setRowSelectionInterval(i, i);
                    mostrarCategoriaSeleccionada();
                    break;
                }
            }
            if (categoriaEncontrada == false){
                JOptionPane.showMessageDialog(this, "No hay ninguna categoria con el código " + catAbuscar, "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_bt_buscarCategoriaActionPerformed

    private void tgbt_editarCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgbt_editarCategoriaActionPerformed
        if (tgbt_editarCategoria.isSelected()){
            tabla_categorias.setEnabled(false);
            bt_buscarCategoria.setEnabled(false);
            tgbt_nuevaCategoria.setEnabled(false);
            bt_eliminarCategoria.setEnabled(false);
            activar_desactivarCampos(true);
        }else{
            modificarCategoria();
            cargarTablaCategorias();
            tabla_categorias.setEnabled(true);
            bt_buscarCategoria.setEnabled(true);
            tgbt_nuevaCategoria.setEnabled(true);
            bt_eliminarCategoria.setEnabled(true);
            activar_desactivarCampos(false);
        }
    }//GEN-LAST:event_tgbt_editarCategoriaActionPerformed

    private void tgbt_nuevaCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgbt_nuevaCategoriaActionPerformed
        if (tgbt_nuevaCategoria.isSelected()){
            tabla_categorias.setEnabled(false);
            bt_buscarCategoria.setEnabled(false);
            tgbt_editarCategoria.setEnabled(false);
            bt_eliminarCategoria.setEnabled(false);
            activar_desactivarCampos(true);
            vaciarCampos();
            JOptionPane.showMessageDialog(this, "Preparado para añadir una nueva Categoria, recuerda que la foto de la categoria es obligatoria", "Información", JOptionPane.INFORMATION_MESSAGE);
        }else{
            nuevaCategoria();
            cargarTablaCategorias();
            tabla_categorias.setEnabled(true);
            bt_buscarCategoria.setEnabled(true);
            tgbt_editarCategoria.setEnabled(true);
            bt_eliminarCategoria.setEnabled(true);
            activar_desactivarCampos(false);
        }
    }//GEN-LAST:event_tgbt_nuevaCategoriaActionPerformed

    private void bt_eliminarCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_eliminarCategoriaActionPerformed
        eliminarCategoria();
    }//GEN-LAST:event_bt_eliminarCategoriaActionPerformed

    private void lb_imagenCategoriaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lb_imagenCategoriaMouseClicked
        if (tgbt_nuevaCategoria.isSelected() || tgbt_editarCategoria.isSelected()){
            JFileChooser elegirFichero = new JFileChooser();
            int eleccion = elegirFichero.showOpenDialog(this);
            if (eleccion == JFileChooser.APPROVE_OPTION){
                File ficheroElegido = elegirFichero.getSelectedFile();
                ImageIcon imagen = new ImageIcon(ficheroElegido.getAbsolutePath());
                lb_imagenCategoria.setIcon(imagen);
                imagenCategoria = new byte[(int)ficheroElegido.length()];
                try {
                    FileInputStream leerFichero = new FileInputStream(ficheroElegido);
                    leerFichero.read(imagenCategoria);
                    imagenCambiada = true;
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }//GEN-LAST:event_lb_imagenCategoriaMouseClicked

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
            java.util.logging.Logger.getLogger(AdministrarCategorias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdministrarCategorias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdministrarCategorias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdministrarCategorias.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdministrarCategorias(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bt_buscarCategoria;
    private javax.swing.JButton bt_eliminarCategoria;
    private javax.swing.JComboBox<String> cmb_encargados;
    private javax.swing.JLabel lb_categoriaSeleccionada;
    private javax.swing.JLabel lb_codCategoria;
    private javax.swing.JLabel lb_descTotalCategorias;
    private javax.swing.JLabel lb_descimagenCategoria;
    private javax.swing.JLabel lb_dniEmpleado;
    private javax.swing.JLabel lb_imagenCategoria;
    private javax.swing.JLabel lb_nombreEmpleado;
    private javax.swing.JLabel lb_totalCategorias;
    private javax.swing.JScrollPane scroll_imagenCategoria;
    private javax.swing.JScrollPane scroll_tablaCategorias;
    private javax.swing.JTable tabla_categorias;
    private javax.swing.JToggleButton tgbt_editarCategoria;
    private javax.swing.JToggleButton tgbt_nuevaCategoria;
    private javax.swing.JTextField txf_codCategoria;
    private javax.swing.JTextField txf_nombreCategoria;
    // End of variables declaration//GEN-END:variables
}
