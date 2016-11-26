
package supermercado;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import javax.sql.rowset.serial.SerialBlob;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class AdministrarArticulos extends javax.swing.JFrame {
    private ArrayList<Articulo> articulos;
    private ArrayList<String> cod_categorias;
    private DefaultTableModel modeloTabla;
    private Connection conexionBaseDatos;
    private byte[] imagenArticulo;
    boolean imagenCambiada = false;
    ButtonGroup grupoFiltro = new ButtonGroup();
    public AdministrarArticulos(Connection conexion) {
        initComponents();
        modeloTabla = (DefaultTableModel) tabla_articulos.getModel();
        conexionBaseDatos = conexion;
        cargarTablaArticulos();
    }
    private void actualizarCategorias(){
        try {
            cod_categorias = Categoria.cargarCod_categorias(conexionBaseDatos);
        } catch (SQLException ex) {
             JOptionPane.showMessageDialog(this, "Error al leer los datos en la base de datos:\n " + ex.getMessage(), "Error al leer", JOptionPane.WARNING_MESSAGE);
        }
        
        
        String filtroCategoriaSeleccionada = (String) cmb_filtrarCategoria.getSelectedItem();
        cmb_categoriaArticulo.removeAllItems();
        
        for (String cod_categoria  : cod_categorias) {
            cmb_categoriaArticulo.addItem(cod_categoria);
            cmb_filtrarCategoria.addItem(cod_categoria);
        }
        if (filtroCategoriaSeleccionada!= null){
            cmb_filtrarCategoria.setSelectedItem(filtroCategoriaSeleccionada);
        }
    }
    private void actualizarArticulos(){
        actualizarCategorias();
        try {
            if (rd_categorias.isSelected()){
                articulos = Articulo.cargarArticulos(conexionBaseDatos, (String) cmb_filtrarCategoria.getSelectedItem());
            }else{
                articulos = Articulo.cargarArticulos(conexionBaseDatos, null);
            }
            imagenCambiada = false;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer los datos en la base de datos:\n " + ex.getMessage(), "Error al leer", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void cargarTablaArticulos(){
        actualizarArticulos();
        vaciarTablaArticulos();
        for (Articulo articulo : articulos) {
            Object[] lineaTablaArticulo = {articulo.getCod_articulo(),articulo.getNombre(),articulo.getCod_articulo()};
            modeloTabla.addRow(lineaTablaArticulo);
        }
        lb_totalArticulos.setText(articulos.size()+"");
        if (tabla_articulos.getRowCount()>0){
            tabla_articulos.setRowSelectionInterval(0, 0);
        }
        mostrarArticuloSeleccionado();
        cmb_categoriaArticulo.setEnabled(false);
    }
    private void vaciarTablaArticulos(){
        while (modeloTabla.getRowCount()>0){
            modeloTabla.removeRow(0);
        }
    }
    private void mostrarArticuloSeleccionado(){
        if (tabla_articulos.getRowCount()>0){
            String cod_articuloSeleccionado = (String) tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 0);
            for (Articulo articulo : articulos) {
                if (articulo.getCod_articulo().equals(cod_articuloSeleccionado)){
                    txf_codArticulo.setText(articulo.getCod_articulo());
                    txf_nombreArticulo.setText(articulo.getNombre());
                    fmf_stockArticulo.setText(articulo.getStock()+"");
                    fmf_precioUnidadArticulo.setText(articulo.getPrecio_unidad()+"");
                    fmf_ivaArticulo.setText(articulo.getPorcentaje_iva()+"");
                    lb_imagenArticulo.setIcon(articulo.getImagen());
                    cmb_categoriaArticulo.setSelectedItem(articulo.getCod_categoria());
                }
            }  
         }
        
    }
    private void activar_desactivarCampos(boolean estado){
        txf_codArticulo.setEditable(estado);
        txf_nombreArticulo.setEditable(estado);
        fmf_stockArticulo.setEditable(estado);
        fmf_precioUnidadArticulo.setEditable(estado);
        fmf_ivaArticulo.setEditable(estado);
        cmb_categoriaArticulo.setEnabled(estado);
    }
    private void vaciarCampos(){
        txf_codArticulo.setText("");
        txf_nombreArticulo.setText("");
        fmf_stockArticulo.setText("");
        fmf_precioUnidadArticulo.setText("");
        fmf_ivaArticulo.setText("");
        cmb_categoriaArticulo.setSelectedIndex(0);
    }
    private void modificarArticulo(){
        String cod_articulo = txf_codArticulo.getText();
        try {
            Statement statement = conexionBaseDatos.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resultset = statement.executeQuery("SELECT * FROM articulo WHERE COD_ARTICULO = '" + cod_articulo + "'");
            resultset.next();
            
            resultset.updateString("COD_ARTICULO", cod_articulo);
            resultset.updateString("NOMBRE", txf_nombreArticulo.getText());
            resultset.updateInt("STOCK", Integer.parseInt(fmf_stockArticulo.getText()));
            resultset.updateDouble("PRECIO_UNIDAD", Double.parseDouble(fmf_precioUnidadArticulo.getText().replace(',', '.')));
            resultset.updateInt("PORCENTAJE_IVA", Integer.parseInt(fmf_ivaArticulo.getText()));
            if (imagenCambiada){
                SerialBlob crearImagenArticulo = new SerialBlob(imagenArticulo);
                resultset.updateBlob("IMAGEN", crearImagenArticulo);
            }
            resultset.updateString("CATEGORIA", ((Categoria) cmb_categoriaArticulo.getSelectedItem()).getCod_categoria());
            
            resultset.updateRow();
            imagenCambiada = false;
            JOptionPane.showMessageDialog(this, "Datos modificados correctamente", "Completado", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al modificar los datos en la base de datos:\n " + ex.getMessage(), "Error al modificar", JOptionPane.WARNING_MESSAGE);
        } catch (NumberFormatException ex){
            JOptionPane.showMessageDialog(this, "Algun campo contiene un formato de numero incorrecto", "Error al modificar", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void nuevoArticulo(){
        if (imagenCambiada){
            try {
                Statement statement = conexionBaseDatos.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                ResultSet resultset = statement.executeQuery("SELECT * FROM `articulo` ");
                resultset.moveToInsertRow();
                
                resultset.updateString("COD_ARTICULO", txf_codArticulo.getText());
                resultset.updateString("NOMBRE", txf_nombreArticulo.getText());
                resultset.updateInt("STOCK", Integer.parseInt(fmf_stockArticulo.getText()));
                resultset.updateDouble("PRECIO_UNIDAD", Double.parseDouble(fmf_precioUnidadArticulo.getText().replace(',', '.')));
                resultset.updateInt("PORCENTAJE_IVA", Integer.parseInt(fmf_ivaArticulo.getText()));
                SerialBlob crearImagenArticulo = new SerialBlob(imagenArticulo);
                resultset.updateBlob("IMAGEN", crearImagenArticulo);
                resultset.updateString("CATEGORIA", ((Categoria) cmb_categoriaArticulo.getSelectedItem()).getCod_categoria());
                
                resultset.insertRow();
                imagenCambiada = false;
                JOptionPane.showMessageDialog(this, "Datos insertados correctamente", "Completado", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al insertar los datos en la base de datos:\n " + ex.getMessage(), "Error al insertar", JOptionPane.WARNING_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(this, "El nuevo articulo no ha sido intersado porque no se ha añadido una foto", "Error al insertar", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void eliminarArticulo(){
        String cod_articuloSeleccionado = (String) tabla_articulos.getValueAt(tabla_articulos.getSelectedRow(), 0);
        int eleccion = JOptionPane.showConfirmDialog(this, "Se eliminará el artículo con el código: " + cod_articuloSeleccionado + " , es posible que de error si algun descuento o factura "
                + "está asociado con esta categoria, ¿desea continuar?" , "Eliminar artículo " + cod_articuloSeleccionado, JOptionPane.OK_CANCEL_OPTION);
        if (eleccion == JOptionPane.OK_OPTION){
            try {
                Statement statement = conexionBaseDatos.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet resultset = statement.executeQuery("SELECT * FROM articulo WHERE COD_ARTICULO = '" + cod_articuloSeleccionado + "'");
                resultset.next();
                resultset.deleteRow();
                JOptionPane.showMessageDialog(this, "Datos borrados", "Operación completada", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al borrar el articulo:\n " + ex.getMessage(), "Error al eliminar", JOptionPane.WARNING_MESSAGE);
            }
            cargarTablaArticulos();
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

        lb_articuloSeleccionado = new javax.swing.JLabel();
        scroll_tablaArticulos = new javax.swing.JScrollPane();
        tabla_articulos = new javax.swing.JTable();
        lb_descTotalArticulos = new javax.swing.JLabel();
        lb_totalArticulos = new javax.swing.JLabel();
        lb_codArticulo = new javax.swing.JLabel();
        txf_codArticulo = new javax.swing.JTextField();
        lb_nombreArticulo = new javax.swing.JLabel();
        txf_nombreArticulo = new javax.swing.JTextField();
        lb_stockArticulo = new javax.swing.JLabel();
        fmf_stockArticulo = new javax.swing.JFormattedTextField();
        lb_precioUnidadArticulo = new javax.swing.JLabel();
        fmf_precioUnidadArticulo = new javax.swing.JFormattedTextField();
        lb_ivaArticulo = new javax.swing.JLabel();
        fmf_ivaArticulo = new javax.swing.JFormattedTextField();
        lb_categoriaArticulo = new javax.swing.JLabel();
        cmb_categoriaArticulo = new javax.swing.JComboBox<>();
        lb_descimagenArticulo = new javax.swing.JLabel();
        scroll_imagenArticulo = new javax.swing.JScrollPane();
        lb_imagenArticulo = new javax.swing.JLabel();
        bt_buscarArticulo = new javax.swing.JButton();
        tgbt_editarArticulo = new javax.swing.JToggleButton();
        tgbt_nuevoArticulo = new javax.swing.JToggleButton();
        bt_eliminarArticulo = new javax.swing.JButton();
        lb_filtro = new javax.swing.JLabel();
        rd_categorias = new javax.swing.JRadioButton();
        rd_todos = new javax.swing.JRadioButton();
        cmb_filtrarCategoria = new javax.swing.JComboBox<>();
        bt_aplicarFiltro = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Administración de artículos");

        lb_articuloSeleccionado.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lb_articuloSeleccionado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_articuloSeleccionado.setText("Articulo seleccionado");
        lb_articuloSeleccionado.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        tabla_articulos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "COD_Articulo", "Nombre", "Categoria"
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
        tabla_articulos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla_articulos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_articulosMouseClicked(evt);
            }
        });
        scroll_tablaArticulos.setViewportView(tabla_articulos);

        lb_descTotalArticulos.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lb_descTotalArticulos.setText("Total de articulos: ");

        lb_totalArticulos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lb_totalArticulos.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb_totalArticulos.setText("0");

        lb_codArticulo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_codArticulo.setText("COD_Articulo");

        txf_codArticulo.setEditable(false);

        lb_nombreArticulo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_nombreArticulo.setText("Nombre");

        txf_nombreArticulo.setEditable(false);

        lb_stockArticulo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_stockArticulo.setText("Stock");

        fmf_stockArticulo.setEditable(false);
        fmf_stockArticulo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        lb_precioUnidadArticulo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_precioUnidadArticulo.setText("Precio unidad");

        fmf_precioUnidadArticulo.setEditable(false);
        fmf_precioUnidadArticulo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));

        lb_ivaArticulo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_ivaArticulo.setText("IVA");

        fmf_ivaArticulo.setEditable(false);
        fmf_ivaArticulo.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        lb_categoriaArticulo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_categoriaArticulo.setText("Categoria");

        cmb_categoriaArticulo.setModel(new DefaultComboBoxModel<>());

        lb_descimagenArticulo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_descimagenArticulo.setText("Imágen del artículo");

        lb_imagenArticulo.setBackground(new java.awt.Color(255, 255, 255));
        lb_imagenArticulo.setOpaque(true);
        lb_imagenArticulo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lb_imagenArticuloMouseClicked(evt);
            }
        });
        scroll_imagenArticulo.setViewportView(lb_imagenArticulo);

        bt_buscarArticulo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        bt_buscarArticulo.setText("Buscar articulo");
        bt_buscarArticulo.setMaximumSize(new java.awt.Dimension(117, 23));
        bt_buscarArticulo.setMinimumSize(new java.awt.Dimension(117, 23));
        bt_buscarArticulo.setPreferredSize(new java.awt.Dimension(117, 23));
        bt_buscarArticulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_buscarArticuloActionPerformed(evt);
            }
        });

        tgbt_editarArticulo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tgbt_editarArticulo.setText("Editar articulo");
        tgbt_editarArticulo.setMaximumSize(new java.awt.Dimension(117, 23));
        tgbt_editarArticulo.setMinimumSize(new java.awt.Dimension(117, 23));
        tgbt_editarArticulo.setPreferredSize(new java.awt.Dimension(117, 23));
        tgbt_editarArticulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgbt_editarArticuloActionPerformed(evt);
            }
        });

        tgbt_nuevoArticulo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tgbt_nuevoArticulo.setText("Nuevo articulo");
        tgbt_nuevoArticulo.setMaximumSize(new java.awt.Dimension(117, 23));
        tgbt_nuevoArticulo.setMinimumSize(new java.awt.Dimension(117, 23));
        tgbt_nuevoArticulo.setPreferredSize(new java.awt.Dimension(117, 23));
        tgbt_nuevoArticulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgbt_nuevoArticuloActionPerformed(evt);
            }
        });

        bt_eliminarArticulo.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        bt_eliminarArticulo.setText("Eliminar articulo");
        bt_eliminarArticulo.setMaximumSize(new java.awt.Dimension(117, 23));
        bt_eliminarArticulo.setMinimumSize(new java.awt.Dimension(117, 23));
        bt_eliminarArticulo.setPreferredSize(new java.awt.Dimension(117, 23));
        bt_eliminarArticulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_eliminarArticuloActionPerformed(evt);
            }
        });

        lb_filtro.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lb_filtro.setText("Filtro");

        grupoFiltro.add(rd_categorias);
        rd_categorias.setSelected(true);
        rd_categorias.setText("Categorias");

        grupoFiltro.add(rd_todos);
        rd_todos.setText("Todos");

        cmb_filtrarCategoria.setModel(new DefaultComboBoxModel<>());

        bt_aplicarFiltro.setText("Aplicar filtro");
        bt_aplicarFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_aplicarFiltroActionPerformed(evt);
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lb_filtro)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(rd_todos)
                                            .addComponent(rd_categorias))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                                        .addComponent(cmb_filtrarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(83, 83, 83))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(139, 139, 139)
                                        .addComponent(bt_aplicarFiltro)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(scroll_tablaArticulos, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lb_ivaArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lb_categoriaArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lb_descimagenArticulo))
                                .addGap(73, 73, 73)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(scroll_imagenArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(cmb_categoriaArticulo, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(fmf_ivaArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lb_codArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(73, 73, 73)
                                .addComponent(txf_codArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lb_nombreArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lb_stockArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(73, 73, 73)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(fmf_stockArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txf_nombreArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lb_precioUnidadArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(73, 73, 73)
                                .addComponent(fmf_precioUnidadArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lb_articuloSeleccionado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lb_descTotalArticulos)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lb_totalArticulos, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(53, 53, 53)
                        .addComponent(bt_buscarArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgbt_editarArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgbt_nuevoArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_eliminarArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lb_articuloSeleccionado, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lb_codArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txf_codArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lb_nombreArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txf_nombreArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lb_stockArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fmf_stockArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lb_precioUnidadArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fmf_precioUnidadArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lb_ivaArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fmf_ivaArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lb_categoriaArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmb_categoriaArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lb_descimagenArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(scroll_imagenArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scroll_tablaArticulos, javax.swing.GroupLayout.PREFERRED_SIZE, 486, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lb_filtro, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(bt_aplicarFiltro)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(rd_categorias)
                                    .addComponent(cmb_filtrarCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rd_todos)))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lb_descTotalArticulos)
                        .addComponent(lb_totalArticulos, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(bt_buscarArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(tgbt_nuevoArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(bt_eliminarArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(tgbt_editarArticulo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tabla_articulosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_articulosMouseClicked
        if (tabla_articulos.getSelectedRow()!= -1){
            mostrarArticuloSeleccionado();
        }
    }//GEN-LAST:event_tabla_articulosMouseClicked

    private void lb_imagenArticuloMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lb_imagenArticuloMouseClicked
        if (tgbt_nuevoArticulo.isSelected() || tgbt_editarArticulo.isSelected()){
            JFileChooser elegirFichero = new JFileChooser();
            int eleccion = elegirFichero.showOpenDialog(this);
            if (eleccion == JFileChooser.APPROVE_OPTION){
                File ficheroElegido = elegirFichero.getSelectedFile();
                ImageIcon imagen = new ImageIcon(ficheroElegido.getAbsolutePath());
                lb_imagenArticulo.setIcon(imagen);
                imagenArticulo = new byte[(int)ficheroElegido.length()];
                try {
                    FileInputStream leerFichero = new FileInputStream(ficheroElegido);
                    leerFichero.read(imagenArticulo);
                    imagenCambiada = true;
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }//GEN-LAST:event_lb_imagenArticuloMouseClicked

    private void bt_buscarArticuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_buscarArticuloActionPerformed
        String artABuscar = JOptionPane.showInputDialog(this, "Escriba el código de artículo a buscar", "Busqueda de artículo", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        if (artABuscar != null){
            boolean articuloEncontrado = false;
            for (int i=0; i< modeloTabla.getRowCount(); i++){
                String cod_articulo = (String) modeloTabla.getValueAt(i, 0);
                if (cod_articulo.equals(artABuscar)){
                    articuloEncontrado = true;
                    tabla_articulos.setRowSelectionInterval(i, i);
                    mostrarArticuloSeleccionado();
                    break;
                }
            }
            if (articuloEncontrado == false){
                JOptionPane.showMessageDialog(this, "No hay ningun artículo con el código " + artABuscar, "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_bt_buscarArticuloActionPerformed

    private void tgbt_editarArticuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgbt_editarArticuloActionPerformed
       if (tgbt_editarArticulo.isSelected()){
           tabla_articulos.setEnabled(false);
           bt_buscarArticulo.setEnabled(false);
           tgbt_nuevoArticulo.setEnabled(false);
           bt_eliminarArticulo.setEnabled(false);
           bt_aplicarFiltro.setEnabled(false);
           activar_desactivarCampos(true);
       }else{
           modificarArticulo();
           cargarTablaArticulos();
           bt_aplicarFiltro.setEnabled(true);
           tabla_articulos.setEnabled(true);
           bt_buscarArticulo.setEnabled(true);
           tgbt_nuevoArticulo.setEnabled(true);
           bt_eliminarArticulo.setEnabled(true);
           activar_desactivarCampos(false);
       }
    }//GEN-LAST:event_tgbt_editarArticuloActionPerformed

    private void tgbt_nuevoArticuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgbt_nuevoArticuloActionPerformed
        if (tgbt_nuevoArticulo.isSelected()){
            tabla_articulos.setEnabled(false);
            bt_buscarArticulo.setEnabled(false);
            tgbt_editarArticulo.setEnabled(false);
            bt_eliminarArticulo.setEnabled(false);
            bt_aplicarFiltro.setEnabled(false);
            lb_imagenArticulo.setIcon(null);
            activar_desactivarCampos(true);
            vaciarCampos();
            JOptionPane.showMessageDialog(this, "Preparado para añadir un nuevo artículo, recuerda que la foto del artículo es obligatoria", "Información", JOptionPane.INFORMATION_MESSAGE);
            
        }else{
            nuevoArticulo();
            cargarTablaArticulos();
            bt_aplicarFiltro.setEnabled(true);
            tabla_articulos.setEnabled(true);
            bt_buscarArticulo.setEnabled(true);
            tgbt_editarArticulo.setEnabled(true);
            bt_eliminarArticulo.setEnabled(true);
            activar_desactivarCampos(false);
        }
    }//GEN-LAST:event_tgbt_nuevoArticuloActionPerformed

    private void bt_eliminarArticuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_eliminarArticuloActionPerformed
        eliminarArticulo();
    }//GEN-LAST:event_bt_eliminarArticuloActionPerformed

    private void bt_aplicarFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_aplicarFiltroActionPerformed
        cargarTablaArticulos();
    }//GEN-LAST:event_bt_aplicarFiltroActionPerformed

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
            java.util.logging.Logger.getLogger(AdministrarArticulos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdministrarArticulos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdministrarArticulos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdministrarArticulos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdministrarArticulos(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bt_aplicarFiltro;
    private javax.swing.JButton bt_buscarArticulo;
    private javax.swing.JButton bt_eliminarArticulo;
    private javax.swing.JComboBox<String> cmb_categoriaArticulo;
    private javax.swing.JComboBox<String> cmb_filtrarCategoria;
    private javax.swing.JFormattedTextField fmf_ivaArticulo;
    private javax.swing.JFormattedTextField fmf_precioUnidadArticulo;
    private javax.swing.JFormattedTextField fmf_stockArticulo;
    private javax.swing.JLabel lb_articuloSeleccionado;
    private javax.swing.JLabel lb_categoriaArticulo;
    private javax.swing.JLabel lb_codArticulo;
    private javax.swing.JLabel lb_descTotalArticulos;
    private javax.swing.JLabel lb_descimagenArticulo;
    private javax.swing.JLabel lb_filtro;
    private javax.swing.JLabel lb_imagenArticulo;
    private javax.swing.JLabel lb_ivaArticulo;
    private javax.swing.JLabel lb_nombreArticulo;
    private javax.swing.JLabel lb_precioUnidadArticulo;
    private javax.swing.JLabel lb_stockArticulo;
    private javax.swing.JLabel lb_totalArticulos;
    private javax.swing.JRadioButton rd_categorias;
    private javax.swing.JRadioButton rd_todos;
    private javax.swing.JScrollPane scroll_imagenArticulo;
    private javax.swing.JScrollPane scroll_tablaArticulos;
    private javax.swing.JTable tabla_articulos;
    private javax.swing.JToggleButton tgbt_editarArticulo;
    private javax.swing.JToggleButton tgbt_nuevoArticulo;
    private javax.swing.JTextField txf_codArticulo;
    private javax.swing.JTextField txf_nombreArticulo;
    // End of variables declaration//GEN-END:variables
}
