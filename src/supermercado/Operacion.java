package supermercado;

/**
 *
 * @author ernid
 */
public class Operacion {
    private double numero1;
    private double numero2;
    private String tipoOperacion;
    private double resultado;

    public Operacion(double numero1, double numero2, String operacion) {
        this.numero1 = numero1;
        this.numero2 = numero2;
        this.tipoOperacion = operacion;
        operar();
    }
    public Operacion(double numero1, String operacion){
        this.numero1 = numero1;
        this.tipoOperacion = operacion;
        operar();
    }
    
    private double suma(){
        return numero1+numero2;
    }
    private double resta(){
        return numero1-numero2;
    }
    private double multiplicacion(){
        return numero1*numero2;
    }
    private double division(){
        return numero1/numero2;
    }
    private double raizCuadrada(){
        return Math.sqrt(numero1);
    }
    private double unoEntreX(){
        return 1/numero1;
    }
    private double alCuadrado(){
        return numero1*numero1;
    }
    private void operar(){
        if (tipoOperacion.equals("suma")){
            resultado = suma();
            tipoOperacion = "+";
        }else if(tipoOperacion.equals("resta")){
            resultado = resta();
            tipoOperacion = "-";
        }else if(tipoOperacion.equals("multiplicacion")){
            resultado = multiplicacion();
            tipoOperacion = "X";
        }else if(tipoOperacion.equals("division")){
            resultado = division();
            tipoOperacion = "/";
        }else if(tipoOperacion.equals("raizCuadrada")){
            resultado = raizCuadrada();
            tipoOperacion = "√";
        }else if(tipoOperacion.equals("unoEntreX")){
            resultado = unoEntreX();
            tipoOperacion = "1/x";
        }else if(tipoOperacion.equals("alCuadrado")){
            resultado = alCuadrado();
            tipoOperacion = "x²";
        }
    }

    public double getNumero1() {
        return numero1;
    }
    public double getNumero2() {
        return numero2;
    }
    public double getResultado() {
        return resultado;
    }
    public String gettipoOperacion() {
        return tipoOperacion;
    }

    @Override
    public String toString() {
        String completo;
        if (tipoOperacion.equals("√") || tipoOperacion.equals("1/x") || tipoOperacion.equals("x²")){
            completo = (tipoOperacion + " " + numero1 + " = " + resultado).replace('.', ',');
        }else{
            completo = (numero1 + " " + tipoOperacion + " " + numero2 + " = " + resultado).replace('.', ',');
        }
        return completo;
    }
    
}
