package yoon.capstone.application.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import yoon.capstone.application.enums.Provider;
import yoon.capstone.application.enums.Role;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="members", indexes = {
        @Index(name = "OX_EMAIL", columnList = "MEMBER_EMAIL"),
        @Index(name = "OX_TOKEN", columnList = "MEMBER_REFRESH_TOKEN")
})
public class Members {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private long memberIdx;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "members")
    private List<Projects> projects = new ArrayList<>();

    @Column(name = "MEMBER_EMAIL", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "MEMBER_PASSWORD",nullable = false, length = 250)
    private String password;    //BCrypt Encoding

    @Column(name = "MEMBER_PHONE", length = 250)
    private String phone;       //AES Encoding

    @Column(name = "MEMBER_NAME",nullable = false, length = 50)
    private String username;

    @Column(name = "MEMBER_IS_OAUTH")
    private boolean isOauth;

    @Enumerated(EnumType.STRING)
    @Column(name = "MEMBER_OAUTH_PROVIDER")
    private Provider provider;

    @CreationTimestamp
    @Column(name = "MEMBER_CREATED_AT")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "MEMBER_UPDATED_AT")
    private LocalDateTime updatedAt;

    @Column(name = "MEMBER_LAST_VISIT")
    private LocalDateTime lastVisit;

    @Enumerated(EnumType.STRING)
    @Column(name = "MEMBER_ROLE")
    private Role role;

    @Column(name = "MEMBER_PROFILE")
    private String profile;

    @Column(name = "MEMBER_ADDRESS")
    private String address;

    @Column(name = "MEMBER_REFRESH_TOKEN",length = 250)
    private String refreshToken;

    @Column(name = "MEMBER_IS_DENIED", columnDefinition = "boolean default false")
    private boolean isDenied;

    @Column(name = "MEMBER_IS_DORMANT", columnDefinition = "boolean default false")
    private boolean isDormant;

    @Builder
    Members(String email, String password, String username, String profile, Role role, boolean oauth, Provider provider){
        this.email = email;
        this.password = password;
        this.username = username;
        this.profile = profile;
        this.role = role;
        this.isOauth = oauth;
        this.provider = provider;
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


