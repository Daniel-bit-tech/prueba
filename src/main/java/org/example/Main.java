@Controller
public class PuzzleController {

    // Tablero inicial desordenado
    private int[] tablero = {1,2,3,4,5,6,7,0,8}; // 0 = vacío

    @Autowired
    private PuzzleService puzzleService;

    @GetMapping("/puzzle")
    public String mostrarPuzzle(Model model) {
        Puzzle puzzle = puzzleService.obtenerPuzzle(); // trae la imagen desde la BD
        String base64Image = Base64.getEncoder().encodeToString(puzzle.getImage());

        model.addAttribute("imagen", base64Image);
        model.addAttribute("tablero", tablero);
        return "puzzle";
    }

    @PostMapping("/mover")
    public String moverFicha(@RequestParam("pos") int pos, RedirectAttributes redirectAttributes) {
        int emptyIndex = -1;
        for (int i = 0; i < tablero.length; i++) {
            if (tablero[i] == 0) {
                emptyIndex = i;
                break;
            }
        }

        // Validar si es adyacente al espacio vacío
        if (esAdyacente(pos, emptyIndex)) {
            int temp = tablero[pos];
            tablero[pos] = tablero[emptyIndex];
            tablero[emptyIndex] = temp;
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Movimiento inválido");
        }

        return "redirect:/puzzle";
    }

    private boolean esAdyacente(int pos, int empty) {
        int size = 3; // puzzle 3x3
        int row = pos / size, col = pos % size;
        int emptyRow = empty / size, emptyCol = empty % size;
        return (Math.abs(row - emptyRow) + Math.abs(col - emptyCol)) == 1;
    }
}



<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Rompecabezas</title>
    <style>
        .puzzle {
            display: grid;
            grid-template-columns: repeat(3, 100px);
            grid-template-rows: repeat(3, 100px);
            gap: 2px;
        }
        .piece {
            width: 100px;
            height: 100px;
            border: 1px solid black;
            background-image: url("data:image/png;base64,[[${imagen}]]");
            background-size: 300px 300px; /* 3x3 = 300px */
        }
        .empty {
            background: #eee; /* casilla vacía */
        }
    </style>
</head>
<body>
<h1>Rompecabezas</h1>

<div class="puzzle">
    <form method="post" action="/mover" th:each="val,iter : ${tablero}">
        <input type="hidden" name="pos" th:value="${iter.index}"/>
        <button type="submit"
                th:class="${val == 0} ? 'piece empty' : 'piece'"
                th:style="${val != 0} ?
                  'background-position: calc(-100px * ' + ((${val}-1)%3) + ') calc(-100px * ' + ((${val}-1)/3)) + ');' : ''">
        </button>
    </form>
</div>

<div th:if="${mensaje}">
    <p th:text="${mensaje}"></p>
</div>

</body>
</html>










@Controller
public class PuzzleController {

    private int[] tablero = {1,2,3,4,5,6,7,0,8}; // estado inicial

    @GetMapping("/puzzle")
    public String mostrarPuzzle(Model model) {
        model.addAttribute("tablero", tablero);
        return "puzzle";
    }

    @PostMapping("/mover")
    public String moverFicha(@RequestParam("pos") int pos, RedirectAttributes redirectAttributes) {
        int emptyIndex = -1;
        for (int i = 0; i < tablero.length; i++) {
            if (tablero[i] == 0) {
                emptyIndex = i;
                break;
            }
        }

        // Validar si es adyacente
        if (esAdyacente(pos, emptyIndex)) {
            int temp = tablero[pos];
            tablero[pos] = tablero[emptyIndex];
            tablero[emptyIndex] = temp;
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Movimiento inválido");
        }

        return "redirect:/puzzle";
    }

    private boolean esAdyacente(int pos, int empty) {
        int size = 3; // puzzle 3x3
        int row = pos / size, col = pos % size;
        int emptyRow = empty / size, emptyCol = empty % size;
        return (Math.abs(row - emptyRow) + Math.abs(col - emptyCol)) == 1;
    }
}


<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Rompecabezas</title>
    <style>
        .puzzle { display: grid; grid-template-columns: repeat(3, 100px); gap: 5px; }
        .piece { width: 100px; height: 100px; border: 1px solid black; display: flex; align-items: center; justify-content: center; }
        .empty { background: #eee; }
    </style>
</head>
<body>
<h1>Rompecabezas</h1>

<div class="puzzle">
    <form method="post" action="/mover" th:each="val,iter : ${tablero}">
        <input type="hidden" name="pos" th:value="${iter.index}"/>
        <button type="submit"
                th:text="${val == 0 ? '' : val}"
                th:class="${val == 0} ? 'piece empty' : 'piece'">
        </button>
    </form>
</div>

<div th:if="${mensaje}">
    <p th:text="${mensaje}"></p>
</div>

</body>
</html>











@GetMapping("/puzzle")
public String mostrarPuzzle(Model model) {
    Puzzle puzzle = puzzleService.obtenerPuzzle(); // trae la imagen
    String base64Image = Base64.getEncoder().encodeToString(puzzle.getImage());

    // Tablero desordenado (ejemplo)
    int[] tablero = {1,2,3,4,5,6,7,0,8}; // 0 es el espacio vacío

    model.addAttribute("imagen", base64Image);
    model.addAttribute("tablero", tablero);
    return "puzzle";
}


<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Rompecabezas</title>
    <style>
        .puzzle {
            display: grid;
            grid-template-columns: repeat(3, 100px);
            grid-template-rows: repeat(3, 100px);
            gap: 2px;
            margin: 20px;
        }
        .piece {
            width: 100px;
            height: 100px;
            background-image: url('data:image/png;base64,[[${imagen}]]');
            background-size: 300px 300px;
            cursor: pointer;
        }
        .empty {
            background: #eee;
        }
    </style>
</head>
<body>
<h1>Rompecabezas deslizante</h1>

<div id="puzzle" class="puzzle">
    <div th:each="val,iter : ${tablero}"
         th:id="'cell-' + ${iter.index}"
         th:class="${val == 0} ? 'piece empty' : 'piece'"
         th:attr="data-value=${val}"
         th:style="${val != 0} ? 'background-position: calc(-100px * ' + ((${val}-1)%3) + ') calc(-100px * ' + ((${val}-1)/3) + ');' : ''">
    </div>
</div>

<script>
    const size = 3; // tablero 3x3
    const puzzle = document.getElementById("puzzle");

    puzzle.addEventListener("click", e => {
        if (!e.target.classList.contains("piece")) return;

        const cells = [...puzzle.children];
        const index = cells.indexOf(e.target);
        const emptyIndex = cells.findIndex(c => c.dataset.value === "0");

        // calcular posición
        const row = Math.floor(index / size), col = index % size;
        const emptyRow = Math.floor(emptyIndex / size), emptyCol = emptyIndex % size;

        // si son adyacentes, intercambiar
        if ((Math.abs(row - emptyRow) + Math.abs(col - emptyCol)) === 1) {
            const clicked = cells[index];
            const empty = cells[emptyIndex];

            // swap valores
            [clicked.dataset.value, empty.dataset.value] = [empty.dataset.value, clicked.dataset.value];
            [clicked.style.backgroundPosition, empty.style.backgroundPosition] = [empty.style.backgroundPosition, clicked.style.backgroundPosition];
            clicked.classList.toggle("empty");
            empty.classList.toggle("empty");
        }

        // comprobar victoria
        const values = cells.map(c => c.dataset.value);
        if (values.join("") === "123456780") {
            alert("¡Felicidades, armaste el puzzle!");
        }
    });
</script>

</body>
</html>











@GetMapping("/puzzle")



public String mostrarPuzzle(Model model) {
    Puzzle puzzle = puzzleService.obtenerPuzzle(); // tu entidad con imagen y board
    String base64Image = Base64.getEncoder().encodeToString(puzzle.getImage());

    model.addAttribute("imagen", base64Image);
    model.addAttribute("tablero", "123406758"); // tablero desordenado (0 = espacio vacío)
    return "puzzle";
}


<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Puzzle</title>
    <style>
        .puzzle {
            display: grid;
            grid-template-columns: repeat(3, 100px);
            grid-template-rows: repeat(3, 100px);
            gap: 2px;
        }
        .piece {
            width: 100px;
            height: 100px;
            background-image: url('data:image/png;base64,[[${imagen}]]');
            background-size: 300px 300px;
        }
    </style>
</head>
<body>

<h1>Rompecabezas</h1>

<div class="puzzle">
    <div th:replace="fragments/piece :: piece(0,0,${imagen})"></div>
    <div th:replace="fragments/piece :: piece(1,0,${imagen})"></div>
    <div th:replace="fragments/piece :: piece(2,0,${imagen})"></div>
    <div th:replace="fragments/piece :: piece(0,1,${imagen})"></div>
    <div th:replace="fragments/piece :: piece(1,1,${imagen})"></div>
    <div th:replace="fragments/piece :: piece(2,1,${imagen})"></div>
    <div th:replace="fragments/piece :: piece(0,2,${imagen})"></div>
    <div th:replace="fragments/piece :: piece(1,2,${imagen})"></div>
    <div th:replace="fragments/piece :: piece(2,2,${imagen})"></div>
</div>

</body>
</html>


<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
    <div th:fragment="piece(x,y,img)"
         class="piece"
         th:style="'background-position: calc(-100px * ' + ${x} + ') calc(-100px * ' + ${y} + '); background-image:url(\'data:image/png;base64,'+${img}+'\')'">
    </div>
</body>
</html>




#############

package com.stfgames.model;

import javax.persistence.*;

@Entity
@Table(name = "Puzzle")
public class Puzzle {

    @Id
    @Column(name = "stf_game_board_structure")
    private int stfGameBoardStructure;

    @Lob
    @Column(name = "image")
    private byte[] image;

    public int getStfGameBoardStructure() {
        return stfGameBoardStructure;
    }

    public void setStfGameBoardStructure(int stfGameBoardStructure) {
        this.stfGameBoardStructure = stfGameBoardStructure;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}

package com.stfgames.dto;

public class PuzzleDTO {
    private String tableroOrdenado;
    private String tableroDesordenado;
    private String solucion;
    private String imagenBase64;

    public String getTableroOrdenado() {
        return tableroOrdenado;
    }

    public void setTableroOrdenado(String tableroOrdenado) {
        this.tableroOrdenado = tableroOrdenado;
    }

    public String getTableroDesordenado() {
        return tableroDesordenado;
    }

    public void setTableroDesordenado(String tableroDesordenado) {
        this.tableroDesordenado = tableroDesordenado;
    }

    public String getSolucion() {
        return solucion;
    }

    public void setSolucion(String solucion) {
        this.solucion = solucion;
    }

    public String getImagenBase64() {
        return imagenBase64;
    }

    public void setImagenBase64(String imagenBase64) {
        this.imagenBase64 = imagenBase64;
    }
}


package com.stfgames.service;

import com.stfgames.model.Puzzle;
import com.stfgames.repository.PuzzleRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

@Service
public class PuzzleService {

    private final PuzzleRepository puzzleRepository;

    public PuzzleService(PuzzleRepository puzzleRepository) {
        this.puzzleRepository = puzzleRepository;
    }

    public Puzzle obtenerPuzzle() {
        return puzzleRepository.findById(12345678)
                .orElseGet(() -> {
                    Puzzle nuevo = new Puzzle();
                    nuevo.setStfGameBoardStructure(12345678);
                    try {
                        ClassPathResource resource = new ClassPathResource("static/images/default.jpg");
                        byte[] defaultImage = Files.readAllBytes(resource.getFile().toPath());
                        nuevo.setImage(defaultImage);
                    } catch (IOException e) {
                        System.out.println("No se pudo cargar la imagen por defecto");
                    }
                    return puzzleRepository.save(nuevo);
                });
    }

    public String convertirImagenBase64(byte[] data) {
        return data != null ? Base64.getEncoder().encodeToString(data) : null;
    }

    public void actualizarImagen(MultipartFile file) throws IOException {
        Puzzle puzzle = obtenerPuzzle();
        puzzle.setImage(file.getBytes());
        puzzleRepository.save(puzzle);
    }
}



package com.stfgames.controller;

import com.stfgames.dto.PuzzleDTO;
import com.stfgames.model.Puzzle;
import com.stfgames.service.PuzzleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PuzzleController {

    private final PuzzleService puzzleService;

    public PuzzleController(PuzzleService puzzleService) {
        this.puzzleService = puzzleService;
    }

    @GetMapping("/puzzle")
    public String mostrarPuzzle(Model model) {
        Puzzle puzzle = puzzleService.obtenerPuzzle();

        PuzzleDTO dto = new PuzzleDTO();
        dto.setTableroOrdenado("123456780");       // tablero inicial
        dto.setTableroDesordenado("123406758");    // un estado desordenado fijo
        dto.setSolucion("RIGHT, DOWN, LEFT, UP, RIGHT, UP, LEFT, DOWN"); // secuencia fija
        dto.setImagenBase64(puzzleService.convertirImagenBase64(puzzle.getImage()));

        model.addAttribute("puzzleDTO", dto);
        return "puzzle";
    }

    @PostMapping("/subirImagen")
    public String subirImagen(@RequestParam("file") MultipartFile file,
                              RedirectAttributes redirectAttributes) {
        try {
            puzzleService.actualizarImagen(file);
            redirectAttributes.addFlashAttribute("mensaje", "Imagen actualizada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al subir la imagen.");
        }
        return "redirect:/puzzle";
    }
}




<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>

<div th:fragment="header">
    <h1>Rompecabezas Deslizante</h1>
    <hr>
</div>

<div th:fragment="mensaje">
    <div th:if="${mensaje}">
        <p th:text="${mensaje}" style="color: blue; font-weight: bold;"></p>
    </div>
</div>

</body>
</html>


<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Puzzle</title>
</head>
<body>

<div th:replace="fragments :: header"></div>
<div th:replace="fragments :: mensaje"></div>

<!-- Imagen -->
<div th:if="${puzzleDTO.imagenBase64}">
    <img th:src="'data:image/jpeg;base64,' + ${puzzleDTO.imagenBase64}" width="200"/>
</div>

<!-- Estados -->
<p><strong>Estado ordenado:</strong> <span th:text="${puzzleDTO.tableroOrdenado}"></span></p>
<p><strong>Estado desordenado:</strong> <span th:text="${puzzleDTO.tableroDesordenado}"></span></p>
<p><strong>Solución (8 pasos):</strong> <span th:text="${puzzleDTO.solucion}"></span></p>

<!-- Subir nueva imagen -->
<form th:action="@{/subirImagen}" method="post" enctype="multipart/form-data">
    <input type="file" name="file" accept="image/jpeg"/>
    <button type="submit">Actualizar imagen</button>
</form>

</body>
</html>














$$$$$$$$$$$$$$$$$$$$$

public class PuzzleLogic {

    private int size; // ej: 3 para tablero 3x3
    private int[][] board;

    public PuzzleLogic(int size) {
        this.size = size;
        initBoard();
    }

    // Inicializar tablero ordenado
    private void initBoard() {
        board = new int[size][size];
        int num = 1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = num;
                num++;
            }
        }
        board[size - 1][size - 1] = 0; // última casilla vacía
    }

    // Serializar tablero a String o int
    public String serializeBoard() {
        StringBuilder sb = new StringBuilder();
        for (int[] row : board) {
            for (int val : row) {
                sb.append(val);
            }
        }
        return sb.toString();
    }

    // Desordenar tablero con una secuencia fija
    public void shuffle() {
        // Ejemplo simple: mover la pieza izquierda del vacío 3 veces
        move("LEFT");
        move("UP");
        move("RIGHT");
        // (Aquí defines los 8 pasos 

package com.stfgames.service;

import com.stfgames.model.Puzzle;
import com.stfgames.repository.PuzzleRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class PuzzleService {

    private final PuzzleRepository puzzleRepository;

    public PuzzleService(PuzzleRepository puzzleRepository) {
        this.puzzleRepository = puzzleRepository;
    }

    public Puzzle obtenerPuzzle() {
        // Usamos siempre el registro ID=1
        Optional<Puzzle> optional = puzzleRepository.findById(1L);
        return optional.orElseGet(() -> {
            Puzzle nuevo = new Puzzle();
            nuevo.setStfGameBoardStructure(12345678); // estado inicial codificado en int
            return puzzleRepository.save(nuevo);
        });
    }

    public void reiniciarPuzzle() {
        Puzzle puzzle = obtenerPuzzle();
        puzzle.setStfGameBoardStructure(12345678); // estado inicial
        puzzleRepository.save(puzzle);
    }

    public void actualizarImagen(MultipartFile file) throws IOException {
        Puzzle puzzle = obtenerPuzzle();
        puzzle.setImage(file.getBytes());
        puzzleRepository.save(puzzle);
    }
}



package com.stfgames.repository;

import com.stfgames.model.Puzzle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PuzzleRepository extends JpaRepository<Puzzle, Long> {
}






package com.stfgames.model;

import javax.persistence.*;

@Entity
@Table(name = "Puzzle")
public class Puzzle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Estado del tablero en entero
    @Column(name = "stf_game_board_structure")
    private int stfGameBoardStructure;

    // Imagen como BLOB
    @Lob
    @Column(name = "image")
    private byte[] image;

    public Long getId() {
        return id;
    }

    public int getStfGameBoardStructure() {
        return stfGameBoardStructure;
    }

    public void setStfGameBoardStructure(int stfGameBoardStructure) {
        this.stfGameBoardStructure = stfGameBoardStructure;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}









package com.stfgames.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Puzzle")
public class Puzzle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Estado del tablero en entero
    @Column(name = "stf_game_board_structure")
    private int stfGameBoardStructure;

    // Imagen como BLOB
    @Lob
    @Column(name = "image")
    private byte[] image;

    public Long getId() {
        return id;
    }

    public int getStfGameBoardStructure() {
        return stfGameBoardStructure;
    }

    public void setStfGameBoardStructure(int stfGameBoardStructure) {
        this.stfGameBoardStructure = stfGameBoardStructure;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}




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




package com.example.clase2gtics.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Juego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int filas;
    private int columnas;

    private String estado; // "conectado" o "desconectado"

    @OneToMany(mappedBy = "juego", cascade = CascadeType.ALL)
    private List<Palabra> palabras;

    // getters y setters
}




package com.example.clase2gtics.entity;

import jakarta.persistence.*;

@Entity
public class Palabra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String texto;
    private boolean encontrada;

    @ManyToOne
    @JoinColumn(name = "juego_id")
    private Juego juego;

    // getters y setters
}



package com.example.clase2gtics.repository;

import com.example.clase2gtics.entity.Juego;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JuegoRepository extends JpaRepository<Juego, Integer> {
    Juego findByEstado(String estado);
}


package com.example.clase2gtics.repository;

import com.example.clase2gtics.entity.Palabra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PalabraRepository extends JpaRepository<Palabra, Integer> {
    List<Palabra> findByJuegoId(Integer juegoId);
    List<Palabra> findByEncontradaTrue();
}



@Autowired
private JuegoRepository juegoRepository;

@Autowired
private PalabraRepository palabraRepository;

@GetMapping("/guardarJuego")
public String guardarJuego(@RequestParam int filas,
                           @RequestParam int columnas,
                           @RequestParam String palabras) {

    Juego juego = new Juego();
    juego.setFilas(filas);
    juego.setColumnas(columnas);
    juego.setEstado("conectado");

    List<Palabra> listaPalabras = Arrays.stream(palabras.split(" "))
            .map(p -> {
                Palabra palabra = new Palabra();
                palabra.setTexto(p.toUpperCase());
                palabra.setEncontrada(false);
                palabra.setJuego(juego);
                return palabra;
            })
            .toList();

    juego.setPalabras(listaPalabras);

    juegoRepository.save(juego);

    return "redirect:/jugar?id=" + juego.getId();
}



<!-- Modo desconectado -->
<form action="/guardarJuego" method="get">
    <input type="hidden" name="filas" value="10">
    <input type="hidden" name="columnas" value="10">
    <input type="hidden" name="palabras" value="JAVA SPRING HTML CSS">
    <button type="submit">Modo desconectado</button>
</form>

<!-- Modo conectado -->
<form action="/guardarJuego" method="get">
    <input type="hidden" name="filas" value="10">
    <input type="hidden" name="columnas" value="10">
    <input type="hidden" name="palabras" value="JAVA SPRING HTML CSS">
    <button type="submit">Modo conectado</button>
</form>




public class PuzzleDTO {
    private String stfGameBoardStructure;
    private String image; // codificada en base64 o ruta de la imagen

    // getters y setters
}



package com.stfgames.dto;

public class PuzzleDTO {
    private String stfGameBoardStructure;
    private String image; // codificada en base64 o ruta de la imagen

    public String getStfGameBoardStructure() {
        return stfGameBoardStructure;
    }

    public void setStfGameBoardStructure(String stfGameBoardStructure) {
        this.stfGameBoardStructure = stfGameBoardStructure;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}



package com.stfgames.controller;

import com.stfgames.dto.PuzzleDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PuzzleController {

    private PuzzleDTO puzzle = new PuzzleDTO();

    @GetMapping("/puzzle")
    public String mostrarPuzzle(Model model) {
        if (puzzle.getStfGameBoardStructure() == null) {
            puzzle.setStfGameBoardStructure("1,2,3,4,5,6,7,8, "); // estado inicial (3x3)
            puzzle.setImage("/images/default.jpg"); // imagen por defecto
        }
        model.addAttribute("puzzle", puzzle);
        return "puzzle";
    }

    @PostMapping("/reiniciar")
    public String reiniciarPuzzle(RedirectAttributes redirectAttributes) {
        puzzle.setStfGameBoardStructure("1,2,3,4,5,6,7,8, ");
        redirectAttributes.addFlashAttribute("mensaje", "¡Juego reiniciado!");
        return "redirect:/puzzle";
    }

    @PostMapping("/subirImagen")
    public String subirImagen(String nuevaImagen, RedirectAttributes redirectAttributes) {
        puzzle.setImage(nuevaImagen); // aquí deberías manejar la carga real de imagen
        redirectAttributes.addFlashAttribute("mensaje", "Imagen actualizada correctamente.");
        return "redirect:/puzzle";
    }
}


<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>

<div th:fragment="header">
    <h1>Rompecabezas Deslizante</h1>
</div>

<div th:fragment="mensaje">
    <div th:if="${mensaje}">
        <p th:text="${mensaje}" class="alert alert-info"></p>
    </div>
</div>

</body>
</html>


<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Rompecabezas</title>
</head>
<body>

<!-- Fragmento de cabecera -->
<div th:replace="fragments :: header"></div>

<!-- Fragmento de mensaje -->
<div th:replace="fragments :: mensaje"></div>

<!-- Mostrar imagen actual -->
<img th:src="${puzzle.image}" alt="Imagen del puzzle" width="200"/>

<!-- Mostrar estado actual del tablero -->
<p><strong>Estado del tablero:</strong> <span th:text="${puzzle.stfGameBoardStructure}"></span></p>

<!-- Botones de acción -->
<form th:action="@{/reiniciar}" method="post">
    <button type="submit">Reiniciar juego</button>
</form>

<form th:action="@{/subirImagen}" method="post">
    <input type="text" name="nuevaImagen" placeholder="Ruta o URL de la nueva imagen"/>
    <button type="submit">Actualizar imagen</button>
</form>

</body>
</html>