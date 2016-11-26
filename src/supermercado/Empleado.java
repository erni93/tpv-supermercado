
package supermercado;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;


public class Empleado implements Comparable<Empleado>{
    public static final String CONTRASENA_EMPLEADO = "empleado";
    public static final String CONTRASENA_ENCARGADO = "encargado2016";
    public static final String CONTRASENA_DIRECTOR = "director2016";
    private String cod_empleado;
    private String dni;
    private String nombre;
    private String apellidos;
    private char genero;
    private GregorianCalendar fecha_nacimiento;
    private GregorianCalendar fecha_contratacion;
    private String puesto;
    private double sueldo_bruto;
    private double sueldo_bonificacion;
    private double sueldo_penalizacion;
    private ImageIcon foto;

    public Empleado(String cod_empleado, String dni, String nombre, String apellidos, char genero, GregorianCalendar fecha_nacimiento, GregorianCalendar fecha_contratacion, String puesto, double sueldo_bruto, double sueldo_bonificacion, double sueldo_penalizacion, ImageIcon foto) {
        this.cod_empleado = cod_empleado;
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.genero = genero;
        this.fecha_nacimiento = fecha_nacimiento;
        this.fecha_contratacion = fecha_contratacion;
        this.puesto = puesto;
        this.sueldo_bruto = sueldo_bruto;
        this.sueldo_bonificacion = sueldo_bonificacion;
        this.sueldo_penalizacion = sueldo_penalizacion;
        this.foto = foto;
    }
    
    public static ArrayList<Empleado> cargarEmpleados(Connection conexion) throws SQLException{
        ArrayList<Empleado> empleados = new ArrayList<>();
         
        Statement statement = conexion.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        ResultSet resulset = statement.executeQuery("SELECT * FROM `empleado`");
        while (resulset.next()){
            String cod_empleado = resulset.getString("COD_EMPLEADO");
            String dni = resulset.getString("DNI");
            String nombre = resulset.getString("NOMBRE");
            String apellidos = resulset.getString("APELLIDOS");
            char genero = resulset.getString("GENERO").charAt(0);

            GregorianCalendar fecha_nacimiento = new GregorianCalendar();
            fecha_nacimiento.setTimeInMillis(resulset.getDate("FECHA_NACIMIENTO").getTime());
            GregorianCalendar fecha_contratacion = new GregorianCalendar();
            fecha_contratacion.setTimeInMillis(resulset.getDate("FECHA_CONTRATACION").getTime());

            String puesto = resulset.getString("puesto");
            double sueldo_bruto = resulset.getDouble("SUELDO_BRUTO");
            double sueldo_bonificacion = resulset.getDouble("SUELDO_BONIFICACION");
            double sueldo_penalizacion = resulset.getDouble("SUELDO_PENALIZACION");
            ImageIcon foto = new ImageIcon(resulset.getBytes("FOTO"));

            Empleado nuevoEmpleado = new Empleado(cod_empleado, dni, nombre, apellidos, genero, fecha_nacimiento, 
                    fecha_contratacion, puesto, sueldo_bruto, sueldo_bonificacion, sueldo_penalizacion, foto);
            empleados.add(nuevoEmpleado);

        }
        empleados.sort(null);
            
        return empleados;
    }
    public static ArrayList<Empleado> cargarEncargados(Connection conexion) throws SQLException{
        ArrayList<Empleado> empleados = cargarEmpleados(conexion);
        ArrayList<Empleado> encargados = new ArrayList<>();
         
        //Borramos los empleados que no son encargados
        for (Empleado empleado: empleados) {
            if (empleado.getPuesto().equals("encargado")){
                encargados.add(empleado);
            }
        }
        return encargados;
        
    }

    public String getCod_empleado() {
        return cod_empleado;
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public char getGenero() {
        return genero;
    }

    public GregorianCalendar getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public GregorianCalendar getFecha_contratacion() {
        return fecha_contratacion;
    }

    public String getPuesto() {
        return puesto;
    }

    public double getSueldo_bruto() {
        return sueldo_bruto;
    }

    public double getSueldo_bonificacion() {
        return sueldo_bonificacion;
    }

    public double getSueldo_penalizacion() {
        return sueldo_penalizacion;
    }

    public ImageIcon getFoto() {
        return foto;
    }
    
    public double getSueldo_TOTAL(){
        return sueldo_bruto+sueldo_bonificacion-sueldo_penalizacion;
    }

    @Override
    public String toString() {
        return cod_empleado + " - " + nombre + " " + apellidos;
    }

    @Override
    public int compareTo(Empleado o) {
        int cod1 = Integer.parseInt(o.getCod_empleado().substring(4));
        int cod2 = Integer.parseInt(this.getCod_empleado().substring(4));
        if (cod1 > cod2){
            return -1;
        }else if (cod1 == cod2){
            return 0;
        }else{
            return 1;
        }
    }
    
}
