
package supermercado;

import com.itextpdf.text.DocumentException;
import java.awt.Dimension;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class TerminalVentas extends javax.swing.JFrame {

    private Connection conexionBaseDatos;
    private Empleado empleado;
    private int numFactura;
    private ArrayList<LineaFactura> lineasFactura = new ArrayList<>();
    private java.util.Date fechaActual;
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private ArrayList<Categoria> categorias;
    private ArrayList<Articulo> articulos;
    private ArrayList<JButton> botonesArticulos = new ArrayList<>();
    private ArrayList<JLabel> descripcionesArticulos = new ArrayList<JLabel>();
    private DefaultTableModel modeloTablaLineasFactura;
    private Factura nuevaFactura;
    public TerminalVentas(Connection conexion, Empleado empleado) {
        initComponents();
        conexionBaseDatos = conexion;
        this.empleado = empleado;
        modeloTablaLineasFactura = (DefaultTableModel) tabla_lineasFactura.getModel();
        cargarDatosFactura();
        cargarBotonesCategoria();
    }
    private void cargarDatosFactura(){
        try {
            numFactura = Factura.obtenerNumSiguienteFactura(conexionBaseDatos);
            fechaActual = new Date();
            lb_empleado.setText(empleado.toString());
            lb_factura.setText(numFactura+"");
            lb_fecha.setText(formatoFecha.format(fechaActual));
            lb_Total.setText("0€");
            lb_totalSinIva.setText("0€");
            nuevaFactura = new Factura("fac_" + numFactura, empleado.getCod_empleado(), fechaActual, lineasFactura);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        } 
    }
    private void cargarCategorias(){
        try { 
            categorias = Categoria.cargarCategorias(conexionBaseDatos, Empleado.cargarEncargados(conexionBaseDatos));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar las categorias en la base de datos:\n " + ex.getMessage(), "Error al cargar", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void prepararScrollPanelCategorias(int numCategorias){
        //http://optimizarsinmas.blogspot.com.es/2011/01/jpanel-con-jscrollpane.html
        panel_categorias.setPreferredSize(new Dimension(470, 160*numCategorias/3+160));
    }
    private void cargarBotonesCategoria(){
        cargarCategorias();
        prepararScrollPanelCategorias(categorias.size());
        int x = 0;
        int y = 10;
        for (Categoria categoria : categorias) {
            JButton nuevoBoton = new JButton();
            nuevoBoton.setIcon(categoria.getImagen());
            nuevoBoton.setBackground(new java.awt.Color(255, 255, 255));
            nuevoBoton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
            nuevoBoton.setContentAreaFilled(false);
            nuevoBoton.setOpaque(true);
            nuevoBoton.setBounds(x, y, 130, 140);
            x += 145+20;
            if (x>= 465){
                y += 140+20;
                x = 0;
            }
            
            nuevoBoton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicBotonCategoria(evt);
            }});
            panel_categorias.add(nuevoBoton);
        }
    }
    private void clicBotonCategoria(java.awt.event.ActionEvent evt) {
        JButton botonPulsado = (JButton) evt.getSource();
        for (Categoria categoria : categorias) {
            if (categoria.getImagen().equals(botonPulsado.getIcon())){
                cargarBotonesArticulo(categoria);
            }
        }
    }
    private void cargarArticulos(Categoria categoria){
        try {
            articulos = Articulo.cargarArticulos(conexionBaseDatos, categoria.getCod_categoria());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar los articulos en la base de datos:\n " + ex.getMessage(), "Error al cargar", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void prepararScrollPanelArticulos(int numArticulos){
        panel_articulos.setPreferredSize(new Dimension(470, 160*numArticulos/3+160));
    }
    private void cargarBotonesArticulo(Categoria categoria){
        for (int i = 0; i < botonesArticulos.size(); i++) {
            panel_articulos.remove(botonesArticulos.get(i));
            panel_articulos.remove(descripcionesArticulos.get(i));
        }
        panel_articulos.repaint();
        scroll_articulos.repaint();
        cargarArticulos(categoria);
        prepararScrollPanelArticulos(articulos.size());
        int x = 10;
        int y = 10;
        for (Articulo articulo : articulos) {
            articulo.redimensionarImagen(142, 156);
            JButton nuevoBoton = new JButton();
            nuevoBoton.setIcon(articulo.getImagen());
            nuevoBoton.setBackground(new java.awt.Color(255, 255, 255));
            nuevoBoton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
            nuevoBoton.setContentAreaFilled(false);
            nuevoBoton.setOpaque(true);
            nuevoBoton.setBounds(x, y, 130, 140);
            JLabel nuevoLabel = new JLabel();
            nuevoLabel.setText(articulo.getNombre().replace('_', ' '));
            nuevoLabel.setBounds(x,y+142,140,20);
            nuevoLabel.setHorizontalAlignment(JLabel.CENTER);
            x += 145+20;
            if (x>= 465){
                y += 140+30;
                x = 10;
            }
            
            nuevoBoton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clicBotonArticulo(evt);
            }});
            panel_articulos.add(nuevoBoton);
            panel_articulos.add(nuevoLabel);
            botonesArticulos.add(nuevoBoton);
            descripcionesArticulos.add(nuevoLabel);
        }
        panel_articulos.repaint();
        scroll_articulos.repaint();
    }
    private void clicBotonArticulo(java.awt.event.ActionEvent evt){
        JButton botonPulsado = (JButton) evt.getSource();
        for (Articulo articulo : articulos) {
            if (articulo.getImagen().equals(botonPulsado.getIcon())){
                anadirArticuloTabla(articulo);
            }
        }
    }
    private void anadirArticuloTabla(Articulo articulo){
        try {
            String cod_linea = "fac_" + numFactura + "-ln_" + lineasFactura.size();
            String cod_factura = "fac_" + numFactura;
            int cantidad = Integer.parseInt(fmf_repetirInsercion.getText());
            Descuento descuento = Descuento.cargarDescuentoArticulo(conexionBaseDatos, articulo.getCod_articulo());
            LineaFactura nuevaLineaFactura = new LineaFactura(cod_linea, cod_factura, articulo, cantidad, descuento);
            
            
            Object[] lineaTablaLineasFactura = new Object[5];
            nuevaLineaFactura.calcularSumaLinea();
            lineaTablaLineasFactura[0] = articulo.getNombre();
            if (descuento != null){
                lineaTablaLineasFactura[1] = descuento.getPorcentaje_descuento()+"%";  
            }else{
                lineaTablaLineasFactura[1] = "-";
            }
            lineaTablaLineasFactura[2] = nuevaLineaFactura.getCantidad();
            lineaTablaLineasFactura[3] = articulo.getPrecio_unidad();
            lineaTablaLineasFactura[4] = nuevaLineaFactura.getSumaSinIva();
            modeloTablaLineasFactura.addRow(lineaTablaLineasFactura);
            lineasFactura.add(nuevaLineaFactura);
            
//            nuevaFactura.anadirLineaFactura(nuevaLineaFactura);
            nuevaFactura.calcularPrecios();
            lb_totalSinIva.setText(nuevaFactura.getTotalSinIva()+"€");
            lb_Total.setText(nuevaFactura.getTotal()+"€");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    private void borrarLineaSeleccionada(){
        int lineaSeleccionada = tabla_lineasFactura.getSelectedRow();
        if (lineaSeleccionada != -1){
            modeloTablaLineasFactura.removeRow(lineaSeleccionada);
            lineasFactura.remove(lineaSeleccionada);
        }
        
    }
    private void borrarTodo(){
        while (tabla_lineasFactura.getRowCount()>0){
            modeloTablaLineasFactura.removeRow(0);
        }
        lineasFactura = new ArrayList<>();
    }
    private void terminarFactura(){
        try {
            
            Factura.anadirNuevaFactura(conexionBaseDatos, nuevaFactura);
            borrarTodo();
            int eleccion = JOptionPane.showConfirmDialog(this, "Factura añadida a la base de datos, ¿desea generar el fichero PDF?" , 
                "Generar PDF", JOptionPane.OK_CANCEL_OPTION);
            if (eleccion == JOptionPane.OK_OPTION){
               Factura.generarPDF(nuevaFactura); 
            }
            cargarDatosFactura();
        } catch (SQLException|DocumentException|IOException ex) {
           JOptionPane.showMessageDialog(this, "Error al crear una nueva factura:\n " + ex.getMessage(), "Error al cargar", JOptionPane.WARNING_MESSAGE);
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

        lb_descEmpleado = new javax.swing.JLabel();
        lb_empleado = new javax.swing.JLabel();
        lb_descFactura = new javax.swing.JLabel();
        lb_factura = new javax.swing.JLabel();
        lb_descFecha = new javax.swing.JLabel();
        lb_fecha = new javax.swing.JLabel();
        lb_categorias = new javax.swing.JLabel();
        scroll_categorias = new javax.swing.JScrollPane();
        panel_categorias = new javax.swing.JPanel();
        scroll_articulos = new javax.swing.JScrollPane();
        panel_articulos = new javax.swing.JPanel();
        lb_articulos = new javax.swing.JLabel();
        scroll_tablaLineasFactura = new javax.swing.JScrollPane();
        tabla_lineasFactura = new javax.swing.JTable();
        lb_descTotalSinIva = new javax.swing.JLabel();
        lb_descTotal = new javax.swing.JLabel();
        lb_Total = new javax.swing.JLabel();
        lb_totalSinIva = new javax.swing.JLabel();
        lb_lineasFactura = new javax.swing.JLabel();
        lb_repetirInsercion = new javax.swing.JLabel();
        lb_cantidadArticulos = new javax.swing.JLabel();
        fmf_repetirInsercion = new javax.swing.JFormattedTextField();
        lb_administracionFactura = new javax.swing.JLabel();
        bt_borrarLineaSeleccionada = new javax.swing.JButton();
        bt_borrarTodo = new javax.swing.JButton();
        bt_terminarVenta = new javax.swing.JButton();
        bt_abrirCalculadora = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Terminal de ventas");

        lb_descEmpleado.setFont(new java.awt.Font("Calibri", 1, 24)); // NOI18N
        lb_descEmpleado.setText("Empleado: ");

        lb_empleado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_empleado.setForeground(new java.awt.Color(51, 153, 0));
        lb_empleado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_empleado.setText("nombreEmpleado");

        lb_descFactura.setFont(new java.awt.Font("Calibri", 1, 24)); // NOI18N
        lb_descFactura.setText("Factura:");

        lb_factura.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_factura.setForeground(new java.awt.Color(51, 153, 0));
        lb_factura.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_factura.setText("cod_factura");

        lb_descFecha.setFont(new java.awt.Font("Calibri", 1, 24)); // NOI18N
        lb_descFecha.setText("Fecha:");

        lb_fecha.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_fecha.setForeground(new java.awt.Color(51, 153, 0));
        lb_fecha.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_fecha.setText("fecha/hora");

        lb_categorias.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        lb_categorias.setText("Categorias");

        scroll_categorias.setAutoscrolls(true);
        scroll_categorias.setMinimumSize(new java.awt.Dimension(500, 306));
        scroll_categorias.setPreferredSize(new java.awt.Dimension(500, 306));

        panel_categorias.setBackground(new java.awt.Color(227, 225, 206));
        panel_categorias.setAutoscrolls(true);
        panel_categorias.setMinimumSize(new java.awt.Dimension(500, 306));
        panel_categorias.setPreferredSize(new java.awt.Dimension(500, 306));
        panel_categorias.setLayout(null);
        scroll_categorias.setViewportView(panel_categorias);

        scroll_articulos.setMinimumSize(new java.awt.Dimension(544, 306));
        scroll_articulos.setPreferredSize(new java.awt.Dimension(544, 306));

        panel_articulos.setBackground(new java.awt.Color(227, 225, 206));
        panel_articulos.setAutoscrolls(true);
        panel_articulos.setMinimumSize(new java.awt.Dimension(544, 306));
        panel_articulos.setPreferredSize(new java.awt.Dimension(544, 306));
        panel_articulos.setLayout(null);
        scroll_articulos.setViewportView(panel_articulos);

        lb_articulos.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        lb_articulos.setText("Artículos");

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

        lb_descTotalSinIva.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lb_descTotalSinIva.setText("Total sin IVA: ");

        lb_descTotal.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lb_descTotal.setText("Total: ");

        lb_Total.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lb_Total.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb_Total.setText("0");

        lb_totalSinIva.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lb_totalSinIva.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb_totalSinIva.setText("0");

        lb_lineasFactura.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        lb_lineasFactura.setText("Factura");

        lb_repetirInsercion.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        lb_repetirInsercion.setText("Repetir inserción de artículo");

        lb_cantidadArticulos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lb_cantidadArticulos.setText("Cantidad de articulos:");

        fmf_repetirInsercion.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        fmf_repetirInsercion.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fmf_repetirInsercion.setText("1");
        fmf_repetirInsercion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fmf_repetirInsercionKeyPressed(evt);
            }
        });

        lb_administracionFactura.setFont(new java.awt.Font("Calibri", 1, 14)); // NOI18N
        lb_administracionFactura.setText("Administración de factura");

        bt_borrarLineaSeleccionada.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        bt_borrarLineaSeleccionada.setText("Borrar linea seleccionada");
        bt_borrarLineaSeleccionada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_borrarLineaSeleccionadaActionPerformed(evt);
            }
        });

        bt_borrarTodo.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        bt_borrarTodo.setText("Borrar todo");
        bt_borrarTodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_borrarTodoActionPerformed(evt);
            }
        });

        bt_terminarVenta.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        bt_terminarVenta.setText("TERMINAR VENTA");
        bt_terminarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_terminarVentaActionPerformed(evt);
            }
        });

        bt_abrirCalculadora.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        bt_abrirCalculadora.setText("Abrir calculadora");
        bt_abrirCalculadora.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_abrirCalculadoraActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bt_abrirCalculadora, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lb_descEmpleado)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lb_empleado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(scroll_categorias, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lb_categorias)
                                .addGap(444, 444, 444)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lb_articulos)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lb_descFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lb_factura, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lb_descFecha)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lb_fecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(scroll_articulos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(scroll_tablaLineasFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 590, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(4, 4, 4)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                    .addComponent(lb_descTotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(lb_descTotalSinIva))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(lb_Total, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(lb_totalSinIva, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                            .addComponent(lb_repetirInsercion)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(lb_cantidadArticulos)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(fmf_repetirInsercion, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(lb_administracionFactura)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(bt_terminarVenta)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(bt_borrarLineaSeleccionada, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(50, 50, 50)
                                                .addComponent(bt_borrarTodo, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                            .addComponent(lb_lineasFactura))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lb_descEmpleado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lb_empleado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lb_descFactura, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                        .addComponent(lb_factura, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lb_descFecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lb_fecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lb_categorias)
                    .addComponent(lb_articulos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scroll_categorias, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scroll_articulos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lb_lineasFactura)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scroll_tablaLineasFactura, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bt_abrirCalculadora, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addComponent(lb_repetirInsercion)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lb_cantidadArticulos)
                            .addComponent(fmf_repetirInsercion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(lb_administracionFactura)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(bt_borrarTodo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bt_borrarLineaSeleccionada, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(40, 40, 40)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lb_descTotalSinIva, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lb_totalSinIva))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lb_descTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lb_Total)
                            .addComponent(bt_terminarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void fmf_repetirInsercionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fmf_repetirInsercionKeyPressed
        char teclaPulsada = evt.getKeyChar();
        if (teclaPulsada<'0' && teclaPulsada> '9'){
            JOptionPane.showMessageDialog(this, "Número no válido", "Error", JOptionPane.WARNING_MESSAGE);
            fmf_repetirInsercion.setText("1");
        }
    }//GEN-LAST:event_fmf_repetirInsercionKeyPressed

    private void bt_borrarLineaSeleccionadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_borrarLineaSeleccionadaActionPerformed
        int eleccion = JOptionPane.showConfirmDialog(this, "Se eliminará la linea " + tabla_lineasFactura.getSelectedRow() + " ,¿desea continuar?" , 
                "Eliminar linea", JOptionPane.OK_CANCEL_OPTION);
        if (eleccion == JOptionPane.OK_OPTION){
           borrarLineaSeleccionada(); 
        }
        
    }//GEN-LAST:event_bt_borrarLineaSeleccionadaActionPerformed

    private void bt_borrarTodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_borrarTodoActionPerformed
        int eleccion = JOptionPane.showConfirmDialog(this, "Se eliminará TODAS las lineas de la tabla " + tabla_lineasFactura.getSelectedRow() + " ,¿desea continuar?" , 
                "Eliminar linea", JOptionPane.OK_CANCEL_OPTION);
        if (eleccion == JOptionPane.OK_OPTION){
            borrarTodo();
        }
    }//GEN-LAST:event_bt_borrarTodoActionPerformed

    private void bt_terminarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_terminarVentaActionPerformed
        int eleccion = JOptionPane.showConfirmDialog(this, "¿Quieres terminar la factura actual?" , 
                "Terminar factura", JOptionPane.OK_CANCEL_OPTION);
        if (eleccion == JOptionPane.OK_OPTION){
            terminarFactura();
        }
    }//GEN-LAST:event_bt_terminarVentaActionPerformed

    private void bt_abrirCalculadoraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_abrirCalculadoraActionPerformed
        Calculadora nuevaCalculadora = new Calculadora();
        nuevaCalculadora.setVisible(true);
    }//GEN-LAST:event_bt_abrirCalculadoraActionPerformed

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
            java.util.logging.Logger.getLogger(TerminalVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TerminalVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TerminalVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TerminalVentas.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TerminalVentas(null,null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bt_abrirCalculadora;
    private javax.swing.JButton bt_borrarLineaSeleccionada;
    private javax.swing.JButton bt_borrarTodo;
    private javax.swing.JButton bt_terminarVenta;
    private javax.swing.JFormattedTextField fmf_repetirInsercion;
    private javax.swing.JLabel lb_Total;
    private javax.swing.JLabel lb_administracionFactura;
    private javax.swing.JLabel lb_articulos;
    private javax.swing.JLabel lb_cantidadArticulos;
    private javax.swing.JLabel lb_categorias;
    private javax.swing.JLabel lb_descEmpleado;
    private javax.swing.JLabel lb_descFactura;
    private javax.swing.JLabel lb_descFecha;
    private javax.swing.JLabel lb_descTotal;
    private javax.swing.JLabel lb_descTotalSinIva;
    private javax.swing.JLabel lb_empleado;
    private javax.swing.JLabel lb_factura;
    private javax.swing.JLabel lb_fecha;
    private javax.swing.JLabel lb_lineasFactura;
    private javax.swing.JLabel lb_repetirInsercion;
    private javax.swing.JLabel lb_totalSinIva;
    private javax.swing.JPanel panel_articulos;
    private javax.swing.JPanel panel_categorias;
    private javax.swing.JScrollPane scroll_articulos;
    private javax.swing.JScrollPane scroll_categorias;
    private javax.swing.JScrollPane scroll_tablaLineasFactura;
    private javax.swing.JTable tabla_lineasFactura;
    // End of variables declaration//GEN-END:variables
}
