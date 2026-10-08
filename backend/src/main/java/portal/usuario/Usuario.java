package portal.usuario;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

// @Entity = "esta classe representa uma tabela do banco"
@Entity
@Table(name = "usuario")
public class Usuario {

    // Chave primária. O banco gera o número sozinho (BIGSERIAL).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true)
    private String email;

    // O Spring converte senhaHash -> coluna senha_hash automaticamente
    private String senhaHash;

    // STRING = guarda o texto "CIDADAO" no banco (e não o número 0, 1, 2)
    @Enumerated(EnumType.STRING)
    private Perfil perfil;

    // Só funcionários têm departamento. Por enquanto é só o número do id;
    // mais pra frente a gente liga isso com a classe Departamento.
    private Long departamentoId;

    private LocalDateTime criadoEm;

    // Roda automaticamente logo antes de salvar pela primeira vez
    @PrePersist
    void antesDeSalvar() {
        this.criadoEm = LocalDateTime.now();
    }

    // ===== Getters e setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public Long getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Long departamentoId) {
        this.departamentoId = departamentoId;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}