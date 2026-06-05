import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import net.salesianos.pelicula.Pelicula;

public class App {
    public static void main(String[] args) {
        
        Path rutaCsv = Path.of("./peliculas.csv");
        Path rutaResumen = Path.of("resumen_peliculas.txt");
        Path rutaLog = Path.of("./log_peliculas.txt");

        ArrayList<Pelicula> mazo = new ArrayList<>();

        try {
            //readString lee todo el archivo de golpe
            String contenidoCompleto = Files.readString(rutaCsv);
            String[] lineas = contenidoCompleto.split("\r?\n");

            for (int i = 1; i < lineas.length; i++) {
                if (lineas[i].isBlank()) {
                    continue;
                }

                String[] partes = lineas[i].split(",");
                String titulo = partes[0].trim();
                String genero = partes[1].trim();
                int duracion = Integer.parseInt(partes[2].trim());

                mazo.add(new Pelicula(titulo, genero, duracion));
            }

            int totalPeliculas = mazo.size();
            int duracionTotal = 0;
            Pelicula peliculaMasLarga = mazo.get(0);

            for (Pelicula p : mazo) {
                duracionTotal += p.getDuracionMinutos();

                if (p.getDuracionMinutos() > peliculaMasLarga.getDuracionMinutos()) {
                    peliculaMasLarga = p;
                }
            }

            double duracionMedia = (double) duracionTotal / totalPeliculas;

            String resumen = "--- RESUMEN DE PELÍCULAS ---\n" +
                             "Número total de películas: " + totalPeliculas + "\n" +
                             "Duración total en minutos: " + duracionTotal + " min\n" +
                             "Duración media: " + String.format("%.2f", duracionMedia) + " min\n" +
                             "Película con mayor duración: " + peliculaMasLarga.getTitulo() + 
                             " (" + peliculaMasLarga.getDuracionMinutos() + " min)\n";

            //writeString crea el resumen 
            Files.writeString(rutaResumen, resumen);
            System.out.println("Fichero resumen_peliculas.txt generado con éxito.");

            String mensajeLog = "[LOG] Ejecución correcta. Se procesaron " + totalPeliculas + " películas con éxito.\n";
            
            Files.writeString(rutaLog, mensajeLog, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("Log actualizado.");

        } catch (IOException e) {
            System.err.println("Error al manejar los ficheros: " + e.getMessage());
            
            try {
                Files.writeString(rutaLog, "[ERROR] Fallo en la ejecución: " + e.getMessage() + "\n", 
                                  StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException ex) {
                System.err.println("No se pudo escribir en el log.");
            }
        }
    }
}