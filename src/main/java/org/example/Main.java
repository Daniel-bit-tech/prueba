package org.example;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner; // Para entrada del usuario, si usas Scanner
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        //--------------------------------------------------
        System.out.print("Ingrese el nombre del usuario a buscar: ");
        String nombreUsuario = sc.nextLine();
        //--------------------------------------------------

        //--------------------------------------------------
        String url = "jdbc:mysql://localhost:3306/iweb_proy";
        String user = "root";
        String pass = "root";
        //--------------------------------------------------

        Connection conn = null;
        Statement stmt1 = null, stmt2 = null;
        PreparedStatement pstmt3 = null;
        ResultSet rs1 = null, rs2 = null, rs3 = null;

        //-------------------------------------------------
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, pass);
            //---------------------------------------------------
            // Query 1: Usuarios
            String sql1 = "SELECT idusuario, nombres, apellidos, correo FROM usuario WHERE nombres LIKE '%" + nombreUsuario + "%'";
            stmt1 = conn.createStatement();
            rs1 = stmt1.executeQuery(sql1);
            //---------------------------------------------------
            System.out.println("\n--- Usuarios ---");
            while (rs1.next()) {
                int id = rs1.getInt("idusuario");
                String nombres = rs1.getString("nombres");
                String apellidos = rs1.getString("apellidos");
                String correo = rs1.getString("correo");
                System.out.println("ID: " + id + " | Nombre: " + nombres + " " + apellidos + " | Correo: " + correo);
            }
            //--------------------------------------------------
            System.out.print("Ingrese el ID del formulario: ");
            int idFormulario = sc.nextInt();
            // Query 2: Categorías
            String sql2 = "SELECT idcategoria, nombre FROM categoria";
            stmt2 = conn.createStatement();
            rs2 = stmt2.executeQuery(sql2);
            System.out.println("\n--- Categorías ---");
            while (rs2.next()) {
                int id = rs2.getInt("idcategoria");
                String nombre = rs2.getString("nombre");
                System.out.println("ID: " + id + " | Categoría: " + nombre);
            }
            //----------------------------------------------------
            // Query 3: Preguntas
            String sql3 = "SELECT idPreguntas, enunciado FROM preguntas WHERE formulario_idformulario = ?";
            pstmt3 = conn.prepareStatement(sql3);
            pstmt3.setInt(1, idFormulario);
            rs3 = pstmt3.executeQuery();
            System.out.println("\n--- Preguntas ---");
            while (rs3.next()) {
                int id = rs3.getInt("idPreguntas");
                String enunciado = rs3.getString("enunciado");
                System.out.println("ID: " + id + " | Enunciado: " + enunciado);
            }
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();



            
        }finally {
            try {
                if (rs1 != null) rs1.close();
                if (rs2 != null) rs2.close();
                if (rs3 != null) rs3.close();
                if (stmt1 != null) stmt1.close();
                if (stmt2 != null) stmt2.close();
                if (pstmt3 != null) pstmt3.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}





@Controller
public class PupiletrasController {

    private Juego juego; // se guarda el estado del juego

    @GetMapping("/pupiletras")
    public String configurarJuego(Model model) {
        return "config"; // muestra el formulario inicial
    }

    @PostMapping("/jugar")
    public String iniciarJuego(@RequestParam int filas,
                               @RequestParam int columnas,
                               @RequestParam String palabras,
                               Model model) {
        List<String> listaPalabras = Arrays.asList(palabras.split(" "));
        juego = new Juego(filas, columnas, listaPalabras);
        model.addAttribute("tablero", juego.getTablero());
        model.addAttribute("palabras", listaPalabras);
        return "jugar";
    }

    @PostMapping("/buscar")
    public String buscarPalabra(@RequestParam int x1, @RequestParam int y1,
                                @RequestParam int x2, @RequestParam int y2,
                                Model model) {
        boolean encontrada = juego.buscar(x1, y1, x2, y2);
        model.addAttribute("tablero", juego.getTablero());
        model.addAttribute("palabras", juego.getPalabrasRestantes());
        model.addAttribute("mensaje", encontrada ? "¡Palabra encontrada!" : "No se encontró la palabra.");

        if (juego.terminado()) {
            model.addAttribute("mensajeFinal", "¡Has encontrado todas las palabras!");
        }

        return "jugar";
    }
}



public class Juego {
    private Tablero tablero;
    private List<String> palabras;
    private Set<String> encontradas;

    public Juego(int filas, int columnas, List<String> palabras) {
        this.tablero = new Tablero(filas, columnas, palabras);
        this.palabras = palabras;
        this.encontradas = new HashSet<>();
    }

    public char[][] getTablero() {
        return tablero.getMatriz();
    }

    public List<String> getPalabrasRestantes() {
        return palabras.stream()
                .filter(p -> !encontradas.contains(p))
                .toList();
    }

    public boolean buscar(int x1, int y1, int x2, int y2) {
        String palabra = tablero.extraerPalabra(x1, y1, x2, y2);
        if (palabras.contains(palabra)) {
            encontradas.add(palabra);
            return true;
        }
        return false;
    }

    public boolean terminado() {
        return encontradas.size() == palabras.size();
    }
}


public class Tablero {
    private char[][] matriz;

    public Tablero(int filas, int columnas, List<String> palabras) {
        matriz = new char[filas][columnas];
        generarTablero(palabras);
    }

    private void generarTablero(List<String> palabras) {
        // Lógica para ubicar palabras aleatoriamente en distintas direcciones
        // y rellenar espacios con letras random
    }

    public char[][] getMatriz() {
        return matriz;
    }

    public String extraerPalabra(int x1, int y1, int x2, int y2) {
        // Devolver string formado entre coordenadas inicio-fin
        return "...";
    }
}



<form action="/jugar" method="post">
  Filas: <input type="number" name="filas">
  Columnas: <input type="number" name="columnas">
  Palabras (separadas por espacio): <input type="text" name="palabras">
  <button type="submit">Iniciar Juego</button>
</form>


<table>
  <tr th:each="fila : ${tablero}">
    <td th:each="celda : ${fila}" th:text="${celda}"></td>
  </tr>
</table>

<p th:text="${mensaje}"></p>
<p th:text="${mensajeFinal}"></p>

<form action="/buscar" method="post">
  X1: <input type="number" name="x1">
  Y1: <input type="number" name="y1">
  X2: <input type="number" name="x2">
  Y2: <input type="number" name="y2">
  <button type="submit">Buscar</button>
</form>

<h3>Palabras restantes:</h3>
<ul>
  <li th:each="pal : ${palabras}" th:text="${pal}"></li>
</ul>



Listo 💡. Te paso la solución completa del controlador corregido para que tu tabla sí se vea en jugar.html.
El cambio clave es que convierto el char[][] (que Thymeleaf no sabe iterar) a Character[][] antes de enviarlo al modelo.


---

📄 PupiletrasController.java

package com.example.pupiletras.controller;

import com.example.pupiletras.model.Juego;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
public class PupiletrasController {

    private Juego juego; // guardamos el estado del juego en memoria

    @GetMapping("/pupiletras")
    public String configurarJuego() {
        return "config"; // formulario inicial
    }

    @PostMapping("/jugar")
    public String iniciarJuego(@RequestParam int filas,
                               @RequestParam int columnas,
                               @RequestParam String palabras,
                               Model model) {

        List<String> listaPalabras = Arrays.asList(palabras.split(" "));
        juego = new Juego(filas, columnas, listaPalabras);

        // 👇 Convertimos char[][] a Character[][] para que Thymeleaf pueda iterar
        Character[][] tablero = convertir(juego.getTablero());

        model.addAttribute("tablero", tablero);
        model.addAttribute("palabras", listaPalabras);

        return "jugar";
    }

    @PostMapping("/buscar")
    public String buscarPalabra(@RequestParam int x1, @RequestParam int y1,
                                @RequestParam int x2, @RequestParam int y2,
                                Model model) {

        boolean encontrada = juego.buscar(x1, y1, x2, y2);

        // 👇 Volvemos a pasar el tablero convertido
        Character[][] tablero = convertir(juego.getTablero());

        model.addAttribute("tablero", tablero);
        model.addAttribute("palabras", juego.getPalabrasRestantes());
        model.addAttribute("mensaje", encontrada ? "¡Palabra encontrada!" : "No se encontró la palabra.");

        if (juego.terminado()) {
            model.addAttribute("mensajeFinal", "¡Has encontrado todas las palabras!");
        }

        return "jugar";
    }

    // 🔄 Método para convertir char[][] → Character[][]
    private Character[][] convertir(char[][] matriz) {
        Character[][] resultado = new Character[matriz.length][];
        for (int i = 0; i < matriz.length; i++) {
            resultado[i] = new Character[matriz[i].length];
            for (int j = 0; j < matriz[i].length; j++) {
                resultado[i][j] = matriz[i][j];
            }
        }
        return resultado;
    }
}


---

✅ Qué cambia

Antes estabas enviando char[][] al modelo → Thymeleaf no lo iteraba.

Ahora envío Character[][] (objetos, no primitivos) → Thymeleaf puede iterar y la tabla se renderiza correctamente.



---

👉 ¿Quieres que también te prepare el config.html (pantalla inicial con formulario de filas, columnas y palabras) para que completes todo el flujo?


