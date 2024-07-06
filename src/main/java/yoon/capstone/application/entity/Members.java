package yoon.capstone.application.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
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
    @Column(name = "MEMBER_ID")
    private long memberIdx;

    @Column(name = "MEMBER_EMAIL", nullable = false, length = 50)
    private String email;

    @Column(name = "MEMBER_PASSWORD",nullable = false, length = 250)
    private String password;

    @Column(name = "MEMBER_NAME",nullable = false, length = 50)
    private String username;

    @ColumnDefault("false")
    @Column(name = "MEMBER_IS_OAUTH")
    private boolean isOauth;

    @Enumerated(EnumType.STRING)
    @Column(name = "MEMBER_OAUTH_PROVIDER")
    private String provider;

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

    @Column(name = "MEMBER_PHONE", length = 250, nullable = true)
    private String phone;

    @ColumnDefault("false")
    @Column(name = "MEMBER_IS_DENIED")
    private boolean isDenied;

    @ColumnDefault("false")
    @Column(name = "MEMBER_IS_DORMANT")
    private boolean isDormant;

    @Builder
    Members(String email, String password, String username, String profile, Role role, boolean oauth){
        this.email = email;
        this.password = password;
        this.username = username;
        this.profile = profile;
        this.role = role;
        this.isOauth = oauth;
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


