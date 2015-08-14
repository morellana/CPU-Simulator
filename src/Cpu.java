
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/*
 * Clase que representa a una CPU, considerando registros,
 * ROM, RAM y program counter
 */

/**
 *
 * @author mauricio
 */
public class Cpu {
    
    private final int tam_rom = 32 * 1024;
    private final int tam_ram = 16 * 1024;
    
    private String rom[];
    private int ram[];
    private int pc; // program counter
    private int regA;
    private int regD;
    

    /**
     * Constructor de Cpu
     */
    public Cpu() {
        this.rom = new String[tam_rom];
        this.ram = new int[tam_ram];
        this.pc = 0;
        this.regA = 0;
        this.regD = 0;
    }
    
    /**
     * Método que resetea los registros, el program counter, y la RAM 
     */
    public void reset(){
        this.regA = 0;
        this.regD = 0;
        this.pc = 0;
        for (int i=0; i<this.tam_ram; i++)
            ram[i] = 0;
    }
    
    /**
     * Método que carga el contenido de un archivo en ROM 
     * 
     * @param nombre nombre de archivo a cargar
     * @throws FileNotFoundException Archivo no encontrado
     */
    public void cargarPrograma (String nombre) throws FileNotFoundException{
        File archivo = new File(nombre);
        if (!archivo.exists()){
            System.out.println("Archivo no existe!");
            System.exit(0);
        }
        Scanner in = new Scanner(archivo);
            int i = 0;
            while (in.hasNextLine()){
                String line = in.nextLine();
                //System.out.println("line: " + line);
                rom[i] = line;
                i++;
            }
        this.pc = 0;
        //mostrarRom();
    }
    
    /**
     * Imprime el valor almacenado en la posición pos de RAM
     * 
     * @param pos casilla de RAM
     */
    public void mostrarRam(int pos){
        System.out.println("RAM[" + pos + "]: " + ram[pos]);
    }

    /**
     * Imprime el valor almacenado en la posición pos de ROM
     * 
     * @param pos casilla de ROM
     */
    public void mostrarRom(int pos){
        System.out.println("ROM[" + pos + "]: " + rom[pos]);
    }

    private void mostrarRom() {
        for (int i = 0; i < tam_rom; i++) {
            System.out.println("" + rom[i]);
        }
    }

    /**
     * Ejecuta las instrucciones almacenadas en ROM
     * considerando que el loop infinito se produce
     * antes de llegar a los 100000 steps.
     * No es la mejor forma ya que no optimiza el tiempo.....
     */
    void run() {
        for (int i = 0; i < 100000; i++) {
            ejecutar(pc);
            pc++;
        }
        System.out.println("Ejecucion terminada!");
    }
        

    /**
     * Ejecuta la instruccion referenciada por el program counter
     * 
     * @param pc linea a ejecutar
     */
    private void ejecutar(int pc) {
        if (this.rom[pc] != null){
            String tipo = this.rom[pc].substring(0,1);
            if (tipo.equals("0")){ // tipo A
                this.regA =  Integer.parseInt(rom[pc], 2);
                //System.out.println("modificado registro A");
            }
            if (tipo.equals("1")){ // tipo C
               setDest(this.rom[pc].substring(10,13), Alu(this.rom[pc].substring(4, 10), this.rom[pc].charAt(3)));
               int value = Alu(this.rom[pc].substring(4, 10), this.rom[pc].charAt(3));
               if (jump(this.rom[pc].substring(13, 16), value)){
                   this.pc = this.regA-1;
                   //System.out.println("modificado cp: " + this.regA);
               }
            }
        }
            
    }

    /**
     * Método que almacena un valor en los destinos establecidos
     * por medio de los bits de control
     * 
     * @param dest bits de control de destino. "d1d2d3"
     * @param value valor a almacenar en las direcciones de destino 
     */
    private void setDest(String dest, int value) {
        char d1 = dest.charAt(0);
        char d2 = dest.charAt(1);
        char d3 = dest.charAt(2);    
        
        //System.out.println("valor.... " + value);
        if (d1 == '1') {regA = value; /*System.out.println("modificado registro A con " + value);*/}
        if (d2 == '1') {regD = value; /*System.out.println("modificado registro D con " + value);*/}
        if (d3 == '1') {ram[regA] = value;  /*System.out.println("modificado en ram[" + regA + "] con " + value);*/}
    }

    /**
     * Método que realiza cálculos lógicos a partir de bits de control
     * 
     * @param controlBits cadena que contiene los bits de control c1, c2, c3, c4, c5 y c6. Ejemplo: "101010"
     * @param a valor del bit a
     * @return resultado de operación realizada a partir de bits de control
     */
    private int Alu(String controlBits, char a) {
        char c1 = controlBits.charAt(0);
        char c2 = controlBits.charAt(1);
        char c3 = controlBits.charAt(2);
        char c4 = controlBits.charAt(3);
        char c5 = controlBits.charAt(4);
        char c6 = controlBits.charAt(5);
        
        if (a == '0'){
            if (c1 == '1' && c2 == '0' && c3 == '1' && c4 == '0' && c5 == '1' && c6 == '0') return 0; // 0
            if (c1 == '1' && c2 == '1' && c3 == '1' && c4 == '1' && c5 == '1' && c6 == '1') return 1; // 1
            if (c1 == '1' && c2 == '1' && c3 == '1' && c4 == '0' && c5 == '1' && c6 == '0') return -1; // -1
            if (c1 == '0' && c2 == '0' && c3 == '1' && c4 == '1' && c5 == '0' && c6 == '0') return regD;  // D
            if (c1 == '1' && c2 == '1' && c3 == '0' && c4 == '0' && c5 == '0' && c6 == '0') return regA;  // A
            if (c1 == '0' && c2 == '0' && c3 == '1' && c4 == '1' && c5 == '0' && c6 == '1') return ~ regD;  // !D
            if (c1 == '1' && c2 == '1' && c3 == '0' && c4 == '0' && c5 == '0' && c6 == '1') return ~ regA;  // !A
            if (c1 == '0' && c2 == '0' && c3 == '1' && c4 == '1' && c5 == '1' && c6 == '1') return 0 - regD;  // -D
            if (c1 == '1' && c2 == '1' && c3 == '0' && c4 == '0' && c5 == '1' && c6 == '1') return 0 - regA;  // -A
            if (c1 == '0' && c2 == '1' && c3 == '1' && c4 == '1' && c5 == '1' && c6 == '1') return regD + 1;  // D+1
            if (c1 == '1' && c2 == '1' && c3 == '0' && c4 == '1' && c5 == '1' && c6 == '1') return regA + 1;  // A+1
            if (c1 == '0' && c2 == '0' && c3 == '1' && c4 == '1' && c5 == '1' && c6 == '0') return regD - 1;  // D-1
            if (c1 == '1' && c2 == '1' && c3 == '0' && c4 == '0' && c5 == '1' && c6 == '0') return regA - 1;  // A-1
            if (c1 == '0' && c2 == '0' && c3 == '0' && c4 == '0' && c5 == '1' && c6 == '0') return regD + regA; // D+A
            if (c1 == '0' && c2 == '1' && c3 == '0' && c4 == '0' && c5 == '1' && c6 == '1') return regD - regA; // D-A
            if (c1 == '0' && c2 == '0' && c3 == '0' && c4 == '1' && c5 == '1' && c6 == '1') return regA - regD; // A-D
            if (c1 == '0' && c2 == '0' && c3 == '0' && c4 == '0' && c5 == '0' && c6 == '0') return regD & regA; // D&A
            if (c1 == '0' && c2 == '1' && c3 == '0' && c4 == '1' && c5 == '0' && c6 == '1') return regD | regA; // D|A
        }
        if (a == '1'){
            if (c1 == '1' && c2 == '1' && c3 == '0' && c4 == '0' && c5 == '0' && c6 == '0') return ram[regA];     // M
            if (c1 == '1' && c2 == '1' && c3 == '0' && c4 == '0' && c5 == '0' && c6 == '1') return ~ram[regA];    // !M
            if (c1 == '1' && c2 == '1' && c3 == '0' && c4 == '0' && c5 == '1' && c6 == '1') return 0 - ram[regA]; // -M
            if (c1 == '1' && c2 == '1' && c3 == '0' && c4 == '1' && c5 == '1' && c6 == '1') return ram[regA] +1;  // M+1
            if (c1 == '1' && c2 == '1' && c3 == '0' && c4 == '0' && c5 == '1' && c6 == '0') return ram[regA] -1;  // M-1
            if (c1 == '0' && c2 == '0' && c3 == '0' && c4 == '0' && c5 == '1' && c6 == '0') return regD + ram[regA];  // D+M
            if (c1 == '0' && c2 == '1' && c3 == '0' && c4 == '0' && c5 == '1' && c6 == '1') return regD - ram[regA];  // D-M
            if (c1 == '0' && c2 == '0' && c3 == '0' && c4 == '1' && c5 == '1' && c6 == '1') return ram[regA] - regD;  // M-D
            if (c1 == '0' && c2 == '0' && c3 == '0' && c4 == '0' && c5 == '0' && c6 == '0') return regD & ram[regA];  // D & M
            if (c1 == '0' && c2 == '1' && c3 == '0' && c4 == '1' && c5 == '0' && c6 == '1') return regD | ram[regA];       // D | M
        }
        return -1;
    }

    /**
     * Ejecuta la instrucción indicada por el program counter
     */
    void next() {
        ejecutar(pc);
        pc++;
    }

    /**
     * Imprime el valor del registro A
     */
    void mostrarRegistroA() {
        System.out.println("regA: " + this.regA);
    }
    
    /**
     * Imprime el valor del registro D
     */
    void mostrarRegistroD() {
        System.out.println("regD: " + this.regD);
    }

    /**
     * Imprime el valor de la variable program counter
     */
    void mostrarCp() {
        System.out.println("pc: " + this.pc);
    }

    /**
     * Ejecuta una cantidad n de instrucciones
     * 
     * @param n cantidad de instrucciones a ejecutar 
     */
    void next(int n) {
        for (int i = 0; i < n; i++) {
            this.next();
        }
    }

    /**
     * Método que verifica si se cumple la condición de salto
     * 
     * @param jump bits de control de salto. "j1j2j3"
     * @param value valor con el que se realizará la comparación
     * @return true en caso de que la condición de salto se cumpla, false en caso contrario
     */
    private boolean jump(String jump, int value) {
        char j1 = jump.charAt(0);
        char j2 = jump.charAt(1);
        char j3 = jump.charAt(2);
        
        if (j1 == '0' && j2 == '0' && j3 == '0') return false; // null
        if (j1 == '0' && j2 == '0' && j3 == '1' && value > 0) return true; // JGT
        if (j1 == '0' && j2 == '1' && j3 == '0' && value == 0) return true; // JEQ
        if (j1 == '0' && j2 == '1' && j3 == '1' && value >= 0) return true; // JGE
        if (j1 == '1' && j2 == '1' && j3 == '0' && value <= 0) return true; // JLT
        if (j1 == '1' && j2 == '0' && j3 == '1' && value != 0) return true; // JNE
        if (j1 == '1' && j2 == '1' && j3 == '0' && value <= 0) return true; // JLE
        if (j1 == '1' && j2 == '1' && j3 == '1') return true; // JMP
        
        return false;
    }
    
    public void mostrarAyuda(){
        System.out.println("Instruccciones de ejecucion");
        System.out.println("\tload [archivo]: Carga un archivo con instrucciones en binario");
        System.out.println("\trun: Ejecuta el programa ");
        System.out.println("\tnext [n]: Ejecuta las siguientes n instrucciones");
        System.out.println("\tn: Ejecuta la siguiente instrucción");
        System.out.println("\treset: Resetea el simulador");
        System.out.println("\tq: salir\n");
        System.out.println("Instruccciones de inspeccion");
        System.out.println("\tshow ram [n]: Muestra el valor almacenado en la posición n de la RAM");
        System.out.println("\tshow rom [n]: Muestra el valor almacenado en la posición n de la ROM");
        System.out.println("\tshow a: Muestra el valor almacenado en el registro A");
        System.out.println("\tshow d: Muestra el valor almacenado en el registro D");
        System.out.println("\tshow pc: Muestra el valor almacenado en el contador de líneas del programa");
        System.out.println("\tshow *: Muestra el valor almacenado en los registros y en contador de líneas");
        System.out.println("");
    }
    
    /**
     *
     */
    public String[] getRom(){
        return this.rom;
    }

    public String getA() {
        return "" + this.regA;
    }

    String getD() {
        return "" + this.regD;
    }

    int getPC() {
        return this.pc;
    }

    public int[] getRam() {
        return this.ram;
    }
}
