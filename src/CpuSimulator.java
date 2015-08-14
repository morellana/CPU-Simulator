
import java.io.FileNotFoundException;
import java.util.Scanner;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/*
 * Clase de interaccion entre usuario y CPU
 */

/**
 *
 * @author mauricio
 */
public class CpuSimulator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, UnsupportedLookAndFeelException {
        // TODO code application logic here        
        Cpu cpu = new Cpu();
        
        JVentana v = new JVentana();
        v.setCPU(cpu);
        v.setVisible(true);
        
        Scanner in = new Scanner(System.in);
        System.out.print("> ");
        String input = in.nextLine();
        
        while (!input.equals("q")){ // interaccion con usuario hasta que ingresa 'q' [Quit]
            String[] split = input.split(" ");
            switch (split[0]) {
                case "load": // cargar archivo
                    cpu.cargarPrograma(split[1]);
                    break;
                case "show": // mostrar...
                    switch(split[1]){
                        case "ram":  // mostrar el valor almacenado en una casilla de RAM
                            cpu.mostrarRam(Integer.parseInt(split[2]));
                            break;
                        case "rom":  // mostrar el valor almacenado en una casilla de ROM
                            cpu.mostrarRom(Integer.parseInt(split[2]));
                            break;
                        case "a":   // mostrar el valor almacenado en el registro A
                            cpu.mostrarRegistroA();
                            break;
                        case "d":   // mostrar el valor almacenado en el registro D
                            cpu.mostrarRegistroD();
                            break;
                        case "pc":  // mostrar el valor almacenado en la variable program counter
                            cpu.mostrarCp();
                            break;
                        case "*":  // mostrar datos utiles
                            cpu.mostrarRegistroA();
                            cpu.mostrarRegistroD();
                            cpu.mostrarCp();
                            break;
                    }
                    break;
                case "run":  // ejecutar todo el programa
                    cpu.run();
                    break;
                case "reset": // resetear CPU
                    cpu.reset();
                    break;
                case "n":  // ejecutar la proxima instruccion
                    cpu.next();
                    break;
                case "next":     // ejecutar las  n  siguientes instrucciones
                    cpu.next(Integer.parseInt(split[1]));
                    break;
                case "help":
                    cpu.mostrarAyuda();
                    break;
                default:
                    System.out.println("Opcion invalida: " + split[0]);
            }
            System.out.print("> ");
            input = in.nextLine();
        }
        System.out.println("Salir");
    }
}
