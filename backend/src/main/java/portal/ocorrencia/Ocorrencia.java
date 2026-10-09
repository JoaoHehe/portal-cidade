package portal.ocorrencia;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import portal.categoria.Categoria;
import portal.usuario.Usuario;

@Entity
@Table(name = "ocorrencia")
public class Ocorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String protocolo;   // ex: 2026-000001
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    // @ManyToOne = "MUITAS ocorrências pertencem a UMA categoria".
    // @JoinColumn diz qual coluna da tabela guarda o id (a chave estrangeira).
    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // O cidadão que registrou (é um Usuario com perfil CIDADAO)
    @ManyToOne(optional = false)
    @JoinColumn(name = "cidadao_id")
    private Usuario cidadao;

    // @OneToOne = cada ocorrência tem UMA localização.
    // cascade ALL = ao salvar a ocorrência, a localização é salva junto.
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "localizacao_id")
    private Localizacao localizacao;

    // Valores iniciais definidos aqui no Java, porque ao salvar o Hibernate
    // envia TODAS as colunas (inclusive as que têm DEFAULT no banco).
    @Enumerated(EnumType.STRING)
    private StatusOcorrencia status = StatusOcorrencia.RECEBIDO;

    @Enumerated(EnumType.STRING)
    private Prioridade prioridade = Prioridade.MEDIA;

    private int gravidade = 3;          // 1 a 5
    private int impacto = 3;            // 1 a 5
    private int scorePrioridade = 0;    // calculado na Etapa 4

    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private LocalDateTime resolvidoEm;  // só preenchido ao resolver

    // Roda sozinho antes de salvar pela primeira vez
    @PrePersist
    void antesDeSalvar() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = this.criadoEm;
    }

    // Roda sozinho antes de cada atualização
    @PreUpdate
    void antesDeAtualizar() {
        this.atualizadoEm = LocalDateTime.now();
    }

    // ===== Getters e setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Usuario getCidadao() {
        return cidadao;
    }

    public void setCidadao(Usuario cidadao) {
        this.cidadao = cidadao;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    public StatusOcorrencia getStatus() {
        return status;
    }

    public void setStatus(StatusOcorrencia status) {
        this.status = status;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public int getGravidade() {
        return gravidade;
    }

    public void setGravidade(int gravidade) {
        this.gravidade = gravidade;
    }

    public int getImpacto() {
        return impacto;
    }

    public void setImpacto(int impacto) {
        this.impacto = impacto;
    }

    public int getScorePrioridade() {
        return scorePrioridade;
    }

    public void setScorePrioridade(int scorePrioridade) {
        this.scorePrioridade = scorePrioridade;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public LocalDateTime getResolvidoEm() {
        return resolvidoEm;
    }

    public void setResolvidoEm(LocalDateTime resolvidoEm) {
        this.resolvidoEm = resolvidoEm;
    }
}