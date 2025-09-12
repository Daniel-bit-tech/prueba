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
        // (Aquí defines los 8 pasos obligatorios que pide tu enunciado)
    }

    // Mover ficha
    public boolean move(String direction) {
        int[] empty = findEmpty();
        int i = empty[0], j = empty[1];
        switch (direction) {
            case "UP":
                if (i > 0) swap(i, j, i - 1, j);
                break;
            case "DOWN":
                if (i < size - 1) swap(i, j, i + 1, j);
                break;
            case "LEFT":
                if (j > 0) swap(i, j, i, j - 1);
                break;
            case "RIGHT":
                if (j < size - 1) swap(i, j, i, j + 1);
                break;
            default:
                return false;
        }
        return true;
    }

    // Buscar vacío
    private int[] findEmpty() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 0) return new int[]{i, j};
            }
        }
        return null;
    }

    private void swap(int i1, int j1, int i2, int j2) {
        int temp = board[i1][j1];
        board[i1][j1] = board[i2][j2];
        board[i2][j2] = temp;
    }

    // Verificar si está resuelto
    public boolean isSolved() {
        int num = 1;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == size - 1 && j == size - 1) {
                    if (board[i][j] != 0) return false;
                } else {
                    if (board[i][j] != num) return false;
                    num++;
                }
            }
        }
        return true;
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
                    nuevo.setStfGameBoardStructure(12345678); // PK fija

                    try {
                        // Leer imagen por defecto de resources/static/images/default.jpg
                        ClassPathResource resource = new ClassPathResource("static/images/default.jpg");
                        byte[] defaultImage = Files.readAllBytes(resource.getFile().toPath());
                        nuevo.setImage(defaultImage);
                    } catch (IOException e) {
                        System.out.println("No se pudo cargar la imagen por defecto: " + e.getMessage());
                    }

                    return puzzleRepository.save(nuevo);
                });
    }

    public void reiniciarPuzzle() {
        Puzzle puzzle = obtenerPuzzle();
        puzzle.setStfGameBoardStructure(12345678); // volver al estado inicial
        puzzleRepository.save(puzzle);
    }

    public void actualizarImagen(MultipartFile file) throws IOException {
        Puzzle puzzle = obtenerPuzzle();
        puzzle.setImage(file.getBytes());
        puzzleRepository.save(puzzle);
    }
}




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

    // Getters y setters
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

<!-- Mostrar imagen -->
<div th:if="${puzzleDTO.imageBase64}">
    <img th:src="'data:image/jpeg;base64,' + ${puzzleDTO.imageBase64}" width="200"/>
</div>

<!-- Estado del tablero -->
<p><strong>Estado del tablero (INT):</strong> 
   <span th:text="${puzzleDTO.stfGameBoardStructure}"></span>
</p>

<!-- Botón reiniciar -->
<form th:action="@{/reiniciar}" method="post">
    <button type="submit">Reiniciar juego</button>
</form>

<!-- Subir imagen -->
<form th:action="@{/subirImagen}" method="post" enctype="multipart/form-data">
    <input type="file" name="file" accept="image/jpeg"/>
    <button type="submit">Actualizar imagen</button>
</form>

</body>
</html>



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

import java.io.IOException;
import java.util.Base64;

@Controller
public class PuzzleController {

    private final PuzzleService puzzleService;

    public PuzzleController(PuzzleService puzzleService) {
        this.puzzleService = puzzleService;
    }

    @GetMapping("/puzzle")
    public String mostrarPuzzle(Model model) {
        Puzzle puzzle = puzzleService.obtenerPuzzle();

        // Convertimos a DTO
        PuzzleDTO dto = new PuzzleDTO();
        dto.setStfGameBoardStructure(puzzle.getStfGameBoardStructure());

        if (puzzle.getImage() != null) {
            String base64 = Base64.getEncoder().encodeToString(puzzle.getImage());
            dto.setImageBase64(base64);
        }

        model.addAttribute("puzzleDTO", dto);
        return "puzzle";
    }

    @PostMapping("/reiniciar")
    public String reiniciarPuzzle(RedirectAttributes redirectAttributes) {
        puzzleService.reiniciarPuzzle();
        redirectAttributes.addFlashAttribute("mensaje", "¡Juego reiniciado con éxito!");
        return "redirect:/puzzle";
    }

    @PostMapping("/subirImagen")
    public String subirImagen(@RequestParam("file") MultipartFile file,
                              RedirectAttributes redirectAttributes) {
        try {
            puzzleService.actualizarImagen(file);
            redirectAttributes.addFlashAttribute("mensaje", "Imagen actualizada correctamente.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al subir la imagen.");
        }
        return "redirect:/puzzle";
    }




package com.stfgames.dto;

public class PuzzleDTO {
    private int stfGameBoardStructure;
    private String imageBase64; // imagen lista para Thymeleaf

    public int getStfGameBoardStructure() {
        return stfGameBoardStructure;
    }

    public void setStfGameBoardStructure(int stfGameBoardStructure) {
        this.stfGameBoardStructure = stfGameBoardStructure;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }
}




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