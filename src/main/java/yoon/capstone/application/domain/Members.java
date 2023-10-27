package yoon.capstone.application.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import yoon.capstone.application.enums.Role;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="members")
public class Members {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idx;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 250)
    private String password;

    @Column(nullable = false, length = 50)
    private String username;

    @ColumnDefault("false")
    private boolean oauth;

    @CreationTimestamp
    private LocalDateTime regdate;

    @UpdateTimestamp
    private LocalDateTime updated;

    private LocalDateTime lastVisit;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String address;

    private String refresh_token;

    @Column(nullable = false, length = 20)
    private String phone;

    @ColumnDefault("false")
    private boolean isDenied;

    @ColumnDefault("false")
    private boolean isSleep;

    @Builder
    Members(String email, String password, String username, Role role, boolean oauth){
        this.email = email;
        this.password = password;
        this.username = username;
        this.role = role;
        this.oauth = oauth;
    }

    public Collection<GrantedAuthority> getAuthority(){
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.getRoleKey()));
        return authorities;
    }

    public String getRoleKey(){
        return this.role.getRoleKey();
    }


}


