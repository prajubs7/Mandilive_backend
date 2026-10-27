package com.mandilive.mandilive.user;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users") // "user" is a reserved word in PostgreSQL
@Getter
@Setter
@NoArgsConstructor
public class User {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false, length = 100)
    private String name;
 
    @Column(nullable = false, unique = true, length = 150)
    private String email;
 
    @Column(length = 20)
    private String phone;
 
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
 
    @Enumerated(EnumType.STRING) // store the name, never the ordinal
    @Column(nullable = false, length = 20)
    private Role role;
 
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}