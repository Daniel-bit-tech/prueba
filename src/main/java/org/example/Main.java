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