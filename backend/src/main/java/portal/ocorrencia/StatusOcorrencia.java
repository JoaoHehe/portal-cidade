package portal.ocorrencia;

// Os passos da vida de uma ocorrência (na ordem).
// Os nomes são IGUAIS aos do CHECK que escrevemos no banco.
public enum StatusOcorrencia {
    RECEBIDO,
    EM_ANALISE,
    ENCAMINHADO,
    EM_EXECUCAO,
    RESOLVIDO
}