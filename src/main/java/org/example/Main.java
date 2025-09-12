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