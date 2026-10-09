package portal.ocorrencia;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import portal.usuario.Usuario;

// Cada linha é uma mudança de status: quem mudou, de quê, para quê e quando
@Entity
@Table(name = "historico_status")
public class HistoricoStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ocorrencia_id")
    private Ocorrencia ocorrencia;

    // Fica vazio (null) no primeiro registro, porque antes não havia status
    @Enumerated(EnumType.STRING)
    private StatusOcorrencia statusAnterior;

    @Enumerated(EnumType.STRING)
    private StatusOcorrencia statusNovo;

    // Quem fez a mudança (cidadão, funcionário ou admin)
    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private String observacao;

    private LocalDateTime criadoEm;

    @PrePersist
    void antesDeSalvar() {
        this.criadoEm = LocalDateTime.now();
    }

    // ===== Getters e setters:

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ocorrencia getOcorrencia() {
        return ocorrencia;
    }

    public void setOcorrencia(Ocorrencia ocorrencia) {
        this.ocorrencia = ocorrencia;
    }

    public StatusOcorrencia getStatusAnterior() {
        return statusAnterior;
    }

    public void setStatusAnterior(StatusOcorrencia statusAnterior) {
        this.statusAnterior = statusAnterior;
    }

    public StatusOcorrencia getStatusNovo() {
        return statusNovo;
    }

    public void setStatusNovo(StatusOcorrencia statusNovo) {
        this.statusNovo = statusNovo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}