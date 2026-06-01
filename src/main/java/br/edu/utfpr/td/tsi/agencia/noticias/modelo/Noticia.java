package br.edu.utfpr.td.tsi.agencia.noticias.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Noticia {

	@Id
	private String id;
	private LocalDate dataCriacao;
	private String titulo;
	private String assunto;
	private Autor autor;
	private String conteudo;
	private String urlImagem;

	// Autoria original e imutável — base da checagem de propriedade
	private String autorId;

	// Máquina de status
	private StatusNoticia status;

	// Rastreabilidade da edição por admin (sem sobrescrever a autoria)
	private boolean editadaPorAdmin;
	private String editadoPorId;
	private String editadoPorNome;
	private LocalDateTime dataEdicaoAdmin;
	private boolean vistoPeloAutor;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public LocalDate getDataCriacao() {
		return dataCriacao;
	}
	public void setDataCriacao(LocalDate dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getAssunto() {
		return assunto;
	}
	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}
	public Autor getAutor() {
		return autor;
	}
	public void setAutor(Autor autor) {
		this.autor = autor;
	}
	public String getConteudo() {
		return conteudo;
	}
	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}
	public String getUrlImagem() {
		return urlImagem;
	}
	public void setUrlImagem(String urlImagem) {
		this.urlImagem = urlImagem;
	}
	public String getAutorId() {
		return autorId;
	}
	public void setAutorId(String autorId) {
		this.autorId = autorId;
	}
	public StatusNoticia getStatus() {
		return status;
	}
	public void setStatus(StatusNoticia status) {
		this.status = status;
	}
	public boolean isEditadaPorAdmin() {
		return editadaPorAdmin;
	}
	public void setEditadaPorAdmin(boolean editadaPorAdmin) {
		this.editadaPorAdmin = editadaPorAdmin;
	}
	public String getEditadoPorId() {
		return editadoPorId;
	}
	public void setEditadoPorId(String editadoPorId) {
		this.editadoPorId = editadoPorId;
	}
	public String getEditadoPorNome() {
		return editadoPorNome;
	}
	public void setEditadoPorNome(String editadoPorNome) {
		this.editadoPorNome = editadoPorNome;
	}
	public LocalDateTime getDataEdicaoAdmin() {
		return dataEdicaoAdmin;
	}
	public void setDataEdicaoAdmin(LocalDateTime dataEdicaoAdmin) {
		this.dataEdicaoAdmin = dataEdicaoAdmin;
	}
	public boolean isVistoPeloAutor() {
		return vistoPeloAutor;
	}
	public void setVistoPeloAutor(boolean vistoPeloAutor) {
		this.vistoPeloAutor = vistoPeloAutor;
	}
}
