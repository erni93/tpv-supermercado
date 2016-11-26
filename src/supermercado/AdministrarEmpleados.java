
package supermercado;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.serial.SerialBlob;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;


public class AdministrarEmpleados extends javax.swing.JFrame {

    private ArrayList<Empleado> empleados;
    private DefaultTableModel modeloTabla;
    private Connection conexionBaseDatos;
    private SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    private DecimalFormat formatoDecimal = new DecimalFormat("#0.00");
    private byte[] imagenEmpleado;
    boolean imagenCambiada = false;
    public AdministrarEmpleados(Connection conexion) {
        initComponents();
        modeloTabla = (DefaultTableModel) tabla_empleados.getModel();
        conexionBaseDatos = conexion;
        cargarTablaEmpleados();
    }
    private void actualizarEmpleados(){
        try {
            empleados = Empleado.cargarEmpleados(conexionBaseDatos);
            imagenCambiada = false;
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer los datos en la base de datos:\n " + ex.getMessage(), "Error al leer", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void cargarTablaEmpleados(){
        vaciarTablaEmpleados();
        actualizarEmpleados();
        for (Empleado empleado : empleados) {
            Object[] lineaTablaEmpleado = {empleado.getCod_empleado(),empleado.getDni(),empleado.getNombre()};
            modeloTabla.addRow(lineaTablaEmpleado);
        }
        lb_totalEmpleados.setText(empleados.size()+"");
        tabla_empleados.setRowSelectionInterval(0, 0);
        mostrarEmpleadoSeleccionado();
        cmb_generoEmpleado.setEnabled(false);
    }
    private void vaciarTablaEmpleados(){
        while (modeloTabla.getRowCount()>0){
            modeloTabla.removeRow(0);
        }
    }
    private void mostrarEmpleadoSeleccionado(){
        String codEmpleadoSeleccionado = (String) modeloTabla.getValueAt(tabla_empleados.getSelectedRow(), 0);
        for (Empleado empleado : empleados) {
            if (empleado.getCod_empleado().equals(codEmpleadoSeleccionado)){
                txf_codEmpleado.setText(empleado.getCod_empleado());
                txf_dniEmpleado.setText(empleado.getDni());
                txf_nombreEmpleado.setText(empleado.getNombre());
                txf_apellidoEmpleado.setText(empleado.getApellidos());
                switch (empleado.getGenero()) {
                    case 'H':
                        cmb_generoEmpleado.setSelectedIndex(0);
                        break;
                    case 'M':
                        cmb_generoEmpleado.setSelectedIndex(1);
                        break;
                    default:
                        cmb_generoEmpleado.setSelectedIndex(2);
                        break;
                }
                fmf_fechaContratacionEmpleado.setText(formatoFecha.format(empleado.getFecha_contratacion().getTime()));
                fmf_fechaNacimientoEmpleado.setText(formatoFecha.format(empleado.getFecha_nacimiento().getTime()));
                txf_puestoEmpleado.setText(empleado.getPuesto());
                fmf_sueldoBrutoEmpleado.setText(formatoDecimal.format(empleado.getSueldo_bruto()));
                fmf_sueldoBonificacionEmpleado.setText(formatoDecimal.format(empleado.getSueldo_bonificacion()));
                fmf_sueldoPenalizacionEmpleado.setText(formatoDecimal.format(empleado.getSueldo_penalizacion()));
                lb_sueldoTotal.setText(formatoDecimal.format(empleado.getSueldo_TOTAL())+"€");
                lb_imagenEmpleado.setIcon(empleado.getFoto());
                
            }
        }
    }
    private void activar_desactivarCampos(boolean estado){
        txf_codEmpleado.setEditable(estado);
        txf_dniEmpleado.setEditable(estado);
        txf_nombreEmpleado.setEditable(estado);
        txf_apellidoEmpleado.setEditable(estado);
        cmb_generoEmpleado.setEnabled(estado);
        fmf_fechaContratacionEmpleado.setEditable(estado);
        fmf_fechaNacimientoEmpleado.setEditable(estado);
        txf_puestoEmpleado.setEditable(estado);
        fmf_sueldoBrutoEmpleado.setEditable(estado);
        fmf_sueldoBonificacionEmpleado.setEditable(estado);
        fmf_sueldoPenalizacionEmpleado.setEditable(estado);
    }
    private void vaciarCampos(){
        txf_codEmpleado.setText("");
        txf_dniEmpleado.setText("");
        txf_nombreEmpleado.setText("");
        txf_apellidoEmpleado.setText("");
        cmb_generoEmpleado.setSelectedIndex(0);
        fmf_fechaContratacionEmpleado.setText("");
        fmf_fechaNacimientoEmpleado.setText("");
        txf_puestoEmpleado.setText("");
        fmf_sueldoBrutoEmpleado.setText("");
        fmf_sueldoBonificacionEmpleado.setText("");
        fmf_sueldoPenalizacionEmpleado.setText("");
        lb_sueldoTotal.setText("0");
        lb_imagenEmpleado.setIcon(null);
    }
    private void modificarEmpleado(){
        String cod_empleado = txf_codEmpleado.getText();
        try {
            Statement statement = conexionBaseDatos.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            ResultSet resulset = statement.executeQuery("SELECT * FROM `empleado` WHERE COD_EMPLEADO = '" + cod_empleado + "'");
            resulset.next();
            
            resulset.updateString("COD_EMPLEADO", cod_empleado);
            resulset.updateString("DNI", txf_dniEmpleado.getText());
            resulset.updateString("NOMBRE", txf_nombreEmpleado.getText());
            resulset.updateString("APELLIDOS", txf_apellidoEmpleado.getText());
            switch (cmb_generoEmpleado.getSelectedIndex()) {
                case 0:
                    resulset.updateString("GENERO", "H");
                    break;
                case 1:
                    resulset.updateString("GENERO", "M");
                    break;
                default:
                    resulset.updateString("GENERO", "A");
                    break;
            }
            java.sql.Date fechaContratacion = new Date(formatoFecha.parse(fmf_fechaContratacionEmpleado.getText()).getTime());
            java.sql.Date fechaNacimiento = new Date(formatoFecha.parse(fmf_fechaNacimientoEmpleado.getText()).getTime());
            resulset.updateDate("FECHA_CONTRATACION", fechaContratacion);
            resulset.updateDate("FECHA_NACIMIENTO", fechaNacimiento);
            resulset.updateString("PUESTO", txf_puestoEmpleado.getText());
            resulset.updateDouble("SUELDO_BRUTO", Double.parseDouble(fmf_sueldoBrutoEmpleado.getText().replace(",", ".")));
            resulset.updateDouble("SUELDO_BONIFICACION", Double.parseDouble(fmf_sueldoBonificacionEmpleado.getText().replace(",", ".")));
            resulset.updateDouble("SUELDO_PENALIZACION", Double.parseDouble(fmf_sueldoPenalizacionEmpleado.getText().replace(",", ".")));
            
            
            //Si la imagen ha sido editada subimos la foto guardada en la variable global
            if (imagenCambiada){
                SerialBlob crearImagenEmpleado = new SerialBlob(imagenEmpleado);
                resulset.updateBlob("FOTO", crearImagenEmpleado);
            }
            
            resulset.updateRow();
            imagenCambiada = false;
            JOptionPane.showMessageDialog(this, "Datos modificados correctamente", "Completado", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al insertar los datos en la base de datos:\n " + ex.getMessage(), "Error al insertar", JOptionPane.WARNING_MESSAGE);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this, "El formato fecha introducido en alguno de los campos no es correcto:\n " + ex.getMessage(), "Error al insertar", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void nuevoEmpleado(){
        if (imagenCambiada){
            try {
                Statement statement = conexionBaseDatos.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                ResultSet resulset = statement.executeQuery("SELECT * FROM `empleado`");
                resulset.moveToInsertRow();
                resulset.updateString("COD_EMPLEADO", txf_codEmpleado.getText());
                resulset.updateString("DNI", txf_dniEmpleado.getText());
                resulset.updateString("NOMBRE", txf_nombreEmpleado.getText());
                resulset.updateString("APELLIDOS", txf_apellidoEmpleado.getText());
                switch (cmb_generoEmpleado.getSelectedIndex()) {
                    case 0:
                        resulset.updateString("GENERO", "H");
                        break;
                    case 1:
                        resulset.updateString("GENERO", "M");
                        break;
                    default:
                        resulset.updateString("GENERO", "A");
                        break;
                }
                java.sql.Date fechaContratacion = new Date(formatoFecha.parse(fmf_fechaContratacionEmpleado.getText()).getTime());
                java.sql.Date fechaNacimiento = new Date(formatoFecha.parse(fmf_fechaNacimientoEmpleado.getText()).getTime());
                resulset.updateDate("FECHA_CONTRATACION", fechaContratacion);
                resulset.updateDate("FECHA_NACIMIENTO", fechaNacimiento);
                resulset.updateString("PUESTO", txf_puestoEmpleado.getText());
                resulset.updateDouble("SUELDO_BRUTO", Double.parseDouble(fmf_sueldoBrutoEmpleado.getText().replace(",", ".")));
                resulset.updateDouble("SUELDO_BONIFICACION", Double.parseDouble(fmf_sueldoBonificacionEmpleado.getText().replace(",", ".")));
                resulset.updateDouble("SUELDO_PENALIZACION", Double.parseDouble(fmf_sueldoPenalizacionEmpleado.getText().replace(",", ".")));
                SerialBlob crearImagenEmpleado = new SerialBlob(imagenEmpleado);
                resulset.updateBlob("FOTO", crearImagenEmpleado);
                resulset.insertRow();
                imagenCambiada = false;
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error al insertar los datos en la base de datos:\n " + ex.getMessage(), "Error al insertar", JOptionPane.WARNING_MESSAGE);
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(this, "El formato fecha introducido en alguno de los campos no es correcto:\n " + ex.getMessage(), "Error al insertar", JOptionPane.WARNING_MESSAGE);
                }
        }else{
            JOptionPane.showMessageDialog(this, "El nuevo empleado no ha sido añadido porque no se ha añadido una foto", "Error al insertar", JOptionPane.WARNING_MESSAGE);
        }
    }
    private void eliminarEmpleado(){
        String codEmpleadoSeleccionado = (String) modeloTabla.getValueAt(tabla_empleados.getSelectedRow(), 0);
        int eleccion = JOptionPane.showConfirmDialog(this, "Se eliminará al empleado con el código: " + codEmpleadoSeleccionado + " , ¿estás seguro?" , "Eliminar empleado " + 
                codEmpleadoSeleccionado, JOptionPane.OK_CANCEL_OPTION);
        if (eleccion == JOptionPane.OK_OPTION){
            try {
                Statement statement = conexionBaseDatos.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet resultSet = statement.executeQuery("SELECT * FROM empleado WHERE COD_EMPLEADO = '" + codEmpleadoSeleccionado + "'");
                resultSet.next();
                resultSet.deleteRow();
                JOptionPane.showMessageDialog(this, "Datos borrados", "Operación completada", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error al borrar al empleado:\n " + ex.getMessage(), "Error al eliminar", JOptionPane.WARNING_MESSAGE);
            }
        }
        cargarTablaEmpleados();
    }
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scroll_tablaEmpleados = new javax.swing.JScrollPane();
        tabla_empleados = new javax.swing.JTable();
        lb_empleadoSeleccionado = new javax.swing.JLabel();
        scroll_imagenEmpleado = new javax.swing.JScrollPane();
        lb_imagenEmpleado = new javax.swing.JLabel();
        lb_codEmpleado = new javax.swing.JLabel();
        lb_dniEmpleado = new javax.swing.JLabel();
        lb_nombreEmpleado = new javax.swing.JLabel();
        lb_apellidoEmpleado = new javax.swing.JLabel();
        lb_generoEmpleado = new javax.swing.JLabel();
        lb_fechaNacimientoEmpleado = new javax.swing.JLabel();
        lb_fechaContratacionEmpleado = new javax.swing.JLabel();
        lb_puestoEmpleado = new javax.swing.JLabel();
        lb_sueldoBrutoEmpleado = new javax.swing.JLabel();
        lb_sueldoBonificacionEmpleado = new javax.swing.JLabel();
        lb_sueldoPenalizacionEmpleado = new javax.swing.JLabel();
        txf_codEmpleado = new javax.swing.JTextField();
        txf_dniEmpleado = new javax.swing.JTextField();
        txf_nombreEmpleado = new javax.swing.JTextField();
        txf_apellidoEmpleado = new javax.swing.JTextField();
        cmb_generoEmpleado = new javax.swing.JComboBox<>();
        fmf_fechaContratacionEmpleado = new javax.swing.JFormattedTextField();
        fmf_fechaNacimientoEmpleado = new javax.swing.JFormattedTextField();
        txf_puestoEmpleado = new javax.swing.JTextField();
        fmf_sueldoBrutoEmpleado = new javax.swing.JFormattedTextField();
        fmf_sueldoBonificacionEmpleado = new javax.swing.JFormattedTextField();
        fmf_sueldoPenalizacionEmpleado = new javax.swing.JFormattedTextField();
        lb_descTotalEmpleados = new javax.swing.JLabel();
        lb_totalEmpleados = new javax.swing.JLabel();
        tgbt_editarEmpleado = new javax.swing.JToggleButton();
        lb_desSueldoTotal = new javax.swing.JLabel();
        lb_sueldoTotal = new javax.swing.JLabel();
        tgbt_nuevoEmpleado = new javax.swing.JToggleButton();
        bt_buscarEmpleado = new javax.swing.JButton();
        bt_eliminarEmpleado = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Administración de empleados");

        tabla_empleados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "COD_Empleado", "DNI", "Nombre"
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
        tabla_empleados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla_empleados.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabla_empleadosMouseClicked(evt);
            }
        });
        scroll_tablaEmpleados.setViewportView(tabla_empleados);

        lb_empleadoSeleccionado.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lb_empleadoSeleccionado.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lb_empleadoSeleccionado.setText("Empleado seleccionado");
        lb_empleadoSeleccionado.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lb_imagenEmpleado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lb_imagenEmpleadoMouseClicked(evt);
            }
        });
        scroll_imagenEmpleado.setViewportView(lb_imagenEmpleado);

        lb_codEmpleado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_codEmpleado.setText("COD_Empleado");

        lb_dniEmpleado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_dniEmpleado.setText("DNI");

        lb_nombreEmpleado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_nombreEmpleado.setText("Nombre");

        lb_apellidoEmpleado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_apellidoEmpleado.setText("Apellidos");

        lb_generoEmpleado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_generoEmpleado.setText("Género");

        lb_fechaNacimientoEmpleado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_fechaNacimientoEmpleado.setText("Fecha de nacimiento");

        lb_fechaContratacionEmpleado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_fechaContratacionEmpleado.setText("Fecha de contratación");

        lb_puestoEmpleado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_puestoEmpleado.setText("Puesto");

        lb_sueldoBrutoEmpleado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_sueldoBrutoEmpleado.setText("Sueldo bruto");

        lb_sueldoBonificacionEmpleado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_sueldoBonificacionEmpleado.setText("Sueldo bonificación");

        lb_sueldoPenalizacionEmpleado.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lb_sueldoPenalizacionEmpleado.setText("Sueldo penalización");

        txf_codEmpleado.setEditable(false);

        txf_dniEmpleado.setEditable(false);

        txf_nombreEmpleado.setEditable(false);

        txf_apellidoEmpleado.setEditable(false);

        cmb_generoEmpleado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hombre", "Mujer", "Ambos" }));

        fmf_fechaContratacionEmpleado.setEditable(false);
        fmf_fechaContratacionEmpleado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("dd/MM/yyyy"))));

        fmf_fechaNacimientoEmpleado.setEditable(false);
        fmf_fechaNacimientoEmpleado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(new java.text.SimpleDateFormat("dd/MM/yyyy"))));

        txf_puestoEmpleado.setEditable(false);

        fmf_sueldoBrutoEmpleado.setEditable(false);
        fmf_sueldoBrutoEmpleado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));

        fmf_sueldoBonificacionEmpleado.setEditable(false);
        fmf_sueldoBonificacionEmpleado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));

        fmf_sueldoPenalizacionEmpleado.setEditable(false);
        fmf_sueldoPenalizacionEmpleado.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));

        lb_descTotalEmpleados.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lb_descTotalEmpleados.setText("Total de empleados: ");

        lb_totalEmpleados.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lb_totalEmpleados.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lb_totalEmpleados.setText("0");

        tgbt_editarEmpleado.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tgbt_editarEmpleado.setText("Editar empleado");
        tgbt_editarEmpleado.setMaximumSize(new java.awt.Dimension(117, 23));
        tgbt_editarEmpleado.setMinimumSize(new java.awt.Dimension(117, 23));
        tgbt_editarEmpleado.setPreferredSize(new java.awt.Dimension(117, 23));
        tgbt_editarEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgbt_editarEmpleadoActionPerformed(evt);
            }
        });

        lb_desSueldoTotal.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lb_desSueldoTotal.setText("Sueldo total: ");

        lb_sueldoTotal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lb_sueldoTotal.setText("0");

        tgbt_nuevoEmpleado.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tgbt_nuevoEmpleado.setText("Nuevo empleado");
        tgbt_nuevoEmpleado.setMaximumSize(new java.awt.Dimension(117, 23));
        tgbt_nuevoEmpleado.setMinimumSize(new java.awt.Dimension(117, 23));
        tgbt_nuevoEmpleado.setPreferredSize(new java.awt.Dimension(117, 23));
        tgbt_nuevoEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgbt_nuevoEmpleadoActionPerformed(evt);
            }
        });

        bt_buscarEmpleado.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        bt_buscarEmpleado.setText("Buscar empleado");
        bt_buscarEmpleado.setMaximumSize(new java.awt.Dimension(117, 23));
        bt_buscarEmpleado.setMinimumSize(new java.awt.Dimension(117, 23));
        bt_buscarEmpleado.setPreferredSize(new java.awt.Dimension(117, 23));
        bt_buscarEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_buscarEmpleadoActionPerformed(evt);
            }
        });

        bt_eliminarEmpleado.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        bt_eliminarEmpleado.setText("Eliminar empleado");
        bt_eliminarEmpleado.setMaximumSize(new java.awt.Dimension(117, 23));
        bt_eliminarEmpleado.setMinimumSize(new java.awt.Dimension(117, 23));
        bt_eliminarEmpleado.setPreferredSize(new java.awt.Dimension(117, 23));
        bt_eliminarEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_eliminarEmpleadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scroll_tablaEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, 335, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(lb_descTotalEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lb_totalEmpleados, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(bt_buscarEmpleado, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                            .addComponent(lb_desSueldoTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tgbt_editarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tgbt_nuevoEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_eliminarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lb_generoEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lb_nombreEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lb_apellidoEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txf_apellidoEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmb_generoEmpleado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txf_nombreEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txf_dniEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txf_codEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(lb_codEmpleado, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lb_fechaContratacionEmpleado, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lb_dniEmpleado, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lb_fechaNacimientoEmpleado, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lb_puestoEmpleado, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lb_sueldoBrutoEmpleado, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lb_sueldoBonificacionEmpleado, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(lb_sueldoPenalizacionEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(fmf_fechaContratacionEmpleado)
                                            .addComponent(fmf_fechaNacimientoEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(txf_puestoEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(fmf_sueldoBrutoEmpleado)
                                        .addComponent(fmf_sueldoBonificacionEmpleado)
                                        .addComponent(fmf_sueldoPenalizacionEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lb_sueldoTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                        .addGap(58, 58, 58)
                        .addComponent(scroll_imagenEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lb_empleadoSeleccionado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lb_empleadoSeleccionado, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(100, 100, 100)
                                        .addComponent(lb_nombreEmpleado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(86, 86, 86))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(lb_codEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(lb_dniEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(scroll_imagenEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addComponent(lb_generoEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txf_codEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txf_dniEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txf_nombreEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lb_apellidoEmpleado, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                                    .addComponent(txf_apellidoEmpleado))
                                .addGap(18, 18, Short.MAX_VALUE)
                                .addComponent(cmb_generoEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lb_fechaContratacionEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(19, 19, 19)
                                .addComponent(lb_fechaNacimientoEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(lb_puestoEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lb_sueldoBrutoEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(fmf_fechaContratacionEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(fmf_fechaNacimientoEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txf_puestoEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(fmf_sueldoBrutoEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(fmf_sueldoBonificacionEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lb_sueldoBonificacionEmpleado))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(fmf_sueldoPenalizacionEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lb_sueldoPenalizacionEmpleado))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lb_desSueldoTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lb_sueldoTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(36, 36, 36))
                    .addComponent(scroll_tablaEmpleados))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(tgbt_editarEmpleado, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tgbt_nuevoEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(bt_eliminarEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lb_descTotalEmpleados)
                        .addComponent(lb_totalEmpleados))
                    .addComponent(bt_buscarEmpleado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tabla_empleadosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabla_empleadosMouseClicked
        if (tabla_empleados.getSelectedRow() != -1){
            mostrarEmpleadoSeleccionado();
        }
    }//GEN-LAST:event_tabla_empleadosMouseClicked

    private void tgbt_editarEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgbt_editarEmpleadoActionPerformed
        if (tgbt_editarEmpleado.isSelected()){
            tabla_empleados.setEnabled(false);
            bt_buscarEmpleado.setEnabled(false);
            tgbt_nuevoEmpleado.setEnabled(false);
            bt_eliminarEmpleado.setEnabled(false);
            activar_desactivarCampos(true);
            
        }else{
            modificarEmpleado();
            cargarTablaEmpleados();
            tabla_empleados.setEnabled(true);
            bt_buscarEmpleado.setEnabled(true);
            tgbt_nuevoEmpleado.setEnabled(true);
            bt_eliminarEmpleado.setEnabled(true);
            activar_desactivarCampos(false);
        }
    }//GEN-LAST:event_tgbt_editarEmpleadoActionPerformed

    private void bt_buscarEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_buscarEmpleadoActionPerformed
       String dniAbuscar = JOptionPane.showInputDialog(this, "Escriba el dni del Empleado", "Busqueda de Empleado", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        if (dniAbuscar!=null){
            boolean empleadoEncontrado = false;
            for (int i=0; i< modeloTabla.getRowCount(); i++){
                String dni = (String) modeloTabla.getValueAt(i, 1);
                if (dni.equals(dniAbuscar)){
                    tabla_empleados.setRowSelectionInterval(i, i);
                    empleadoEncontrado = true;
                    mostrarEmpleadoSeleccionado();
                    break;
                }
                
            }
            if (empleadoEncontrado == false){
                JOptionPane.showMessageDialog(this, "No hay ningun empleado con el DNI " + dniAbuscar, "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            }
            
        }
    }//GEN-LAST:event_bt_buscarEmpleadoActionPerformed

    private void lb_imagenEmpleadoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lb_imagenEmpleadoMouseClicked
        if (tgbt_editarEmpleado.isSelected() || tgbt_nuevoEmpleado.isSelected()){
            JFileChooser elegirFichero = new JFileChooser();
            int eleccion = elegirFichero.showOpenDialog(this);
            if (eleccion == JFileChooser.APPROVE_OPTION){
                File ficheroElegido = elegirFichero.getSelectedFile();
                ImageIcon imagen = new ImageIcon(ficheroElegido.getAbsolutePath());
                lb_imagenEmpleado.setIcon(imagen);
                
                try {
                    FileInputStream leerFichero = new FileInputStream(ficheroElegido);
                    imagenEmpleado = new byte[(int) ficheroElegido.length()];
                    leerFichero.read(imagenEmpleado);
                    imagenCambiada = true;
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        
    }//GEN-LAST:event_lb_imagenEmpleadoMouseClicked

    private void tgbt_nuevoEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgbt_nuevoEmpleadoActionPerformed
        if (tgbt_nuevoEmpleado.isSelected()){
            tabla_empleados.setEnabled(false);
            bt_buscarEmpleado.setEnabled(false);
            tgbt_editarEmpleado.setEnabled(false);
            bt_eliminarEmpleado.setEnabled(false);
            activar_desactivarCampos(true);
            vaciarCampos();
            JOptionPane.showMessageDialog(this, "Preparado para añadir un nuevo Empleado, recuerda que la foto de empleado es obligatoria", "Información", JOptionPane.INFORMATION_MESSAGE);
        }else{
            nuevoEmpleado();
            cargarTablaEmpleados();
            tabla_empleados.setEnabled(true);
            bt_buscarEmpleado.setEnabled(true);
            tgbt_editarEmpleado.setEnabled(true);
            bt_eliminarEmpleado.setEnabled(true);
            activar_desactivarCampos(false);
        }
    }//GEN-LAST:event_tgbt_nuevoEmpleadoActionPerformed

    private void bt_eliminarEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_eliminarEmpleadoActionPerformed
        eliminarEmpleado();
    }//GEN-LAST:event_bt_eliminarEmpleadoActionPerformed

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
            java.util.logging.Logger.getLogger(AdministrarEmpleados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AdministrarEmpleados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AdministrarEmpleados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AdministrarEmpleados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AdministrarEmpleados(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bt_buscarEmpleado;
    private javax.swing.JButton bt_eliminarEmpleado;
    private javax.swing.JComboBox<String> cmb_generoEmpleado;
    private javax.swing.JFormattedTextField fmf_fechaContratacionEmpleado;
    private javax.swing.JFormattedTextField fmf_fechaNacimientoEmpleado;
    private javax.swing.JFormattedTextField fmf_sueldoBonificacionEmpleado;
    private javax.swing.JFormattedTextField fmf_sueldoBrutoEmpleado;
    private javax.swing.JFormattedTextField fmf_sueldoPenalizacionEmpleado;
    private javax.swing.JLabel lb_apellidoEmpleado;
    private javax.swing.JLabel lb_codEmpleado;
    private javax.swing.JLabel lb_desSueldoTotal;
    private javax.swing.JLabel lb_descTotalEmpleados;
    private javax.swing.JLabel lb_dniEmpleado;
    private javax.swing.JLabel lb_empleadoSeleccionado;
    private javax.swing.JLabel lb_fechaContratacionEmpleado;
    private javax.swing.JLabel lb_fechaNacimientoEmpleado;
    private javax.swing.JLabel lb_generoEmpleado;
    private javax.swing.JLabel lb_imagenEmpleado;
    private javax.swing.JLabel lb_nombreEmpleado;
    private javax.swing.JLabel lb_puestoEmpleado;
    private javax.swing.JLabel lb_sueldoBonificacionEmpleado;
    private javax.swing.JLabel lb_sueldoBrutoEmpleado;
    private javax.swing.JLabel lb_sueldoPenalizacionEmpleado;
    private javax.swing.JLabel lb_sueldoTotal;
    private javax.swing.JLabel lb_totalEmpleados;
    private javax.swing.JScrollPane scroll_imagenEmpleado;
    private javax.swing.JScrollPane scroll_tablaEmpleados;
    private javax.swing.JTable tabla_empleados;
    private javax.swing.JToggleButton tgbt_editarEmpleado;
    private javax.swing.JToggleButton tgbt_nuevoEmpleado;
    private javax.swing.JTextField txf_apellidoEmpleado;
    private javax.swing.JTextField txf_codEmpleado;
    private javax.swing.JTextField txf_dniEmpleado;
    private javax.swing.JTextField txf_nombreEmpleado;
    private javax.swing.JTextField txf_puestoEmpleado;
    // End of variables declaration//GEN-END:variables
}
