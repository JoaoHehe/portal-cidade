package portal.categoria;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Representa a tabela "categoria" (Infraestrutura, Iluminação...)
@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;

    // Vira a coluna palavras_chave (o Spring troca maiúscula por _ sozinho)
    private String palavrasChave;

    // Gravidade padrão da categoria, de 1 a 5
    private Integer gravidadeBase;

    // Por enquanto só o id do departamento; ligamos de verdade na Etapa 5
    private Long departamentoSugeridoId;

    // ===== Getters e setters:

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPalavrasChave() {
        return palavrasChave;
    }

    public void setPalavrasChave(String palavrasChave) {
        this.palavrasChave = palavrasChave;
    }

    public Integer getGravidadeBase() {
        return gravidadeBase;
    }

    public void setGravidadeBase(Integer gravidadeBase) {
        this.gravidadeBase = gravidadeBase;
    }

    public Long getDepartamentoSugeridoId() {
        return departamentoSugeridoId;
    }

    public void setDepartamentoSugeridoId(Long departamentoSugeridoId) {
        this.departamentoSugeridoId = departamentoSugeridoId;
    }
}