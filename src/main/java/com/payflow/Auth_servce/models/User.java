package com.payflow.Auth_servce.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "userT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "password")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false,name = "user_id")
    private Long userId;

    @Column(unique = true,nullable = false,name = "email")
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name",nullable = false)
    private String firstName;

    @Column(name = "last_name",nullable = false)
    private String lastName;

    @Column(name = "currency",nullable = false)
    private String currency;

    @CreationTimestamp
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "enabled")
    private boolean enabled = false;

    @Column(name = "is_credentials_expired")
    private boolean isCredentialsExpired = false;

    @Column(name = "is_locked")
    private boolean isLocked = true;

    @Column(name = "locked_until")
    private OffsetDateTime lockedUntil = null;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private UserRole role;
}
