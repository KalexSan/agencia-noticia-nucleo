package br.edu.utfpr.td.tsi.agencia.noticias.controle;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Autor;
import br.edu.utfpr.td.tsi.agencia.noticias.modelo.Noticia;
import br.edu.utfpr.td.tsi.agencia.noticias.persistencia.AutorRepository;
import br.edu.utfpr.td.tsi.agencia.noticias.persistencia.NoticiaRepository;

@Controller
public class NoticiaController {

	@Autowired
	private AutorRepository autorRepository;

	@Autowired
	private NoticiaRepository noticiaRepository;

	@GetMapping(value = "/cadastrarNoticia")
	public String exibirPaginaCadastrarNoticia(Model model) {
		List<Autor> listaAutores = autorRepository.findAll();
		model.addAttribute("autores", listaAutores);

		return "cadastrarNoticia";
	}

	@PostMapping(value = "/cadastrarNoticia")
	public String cadastrarDocumento(Noticia noticia) {
		noticia.setDataCriacao(LocalDate.now());
		noticia.setId(UUID.randomUUID().toString());
		noticiaRepository.insert(noticia);
		return "index";
	}

	@GetMapping(value = "/listarNoticias")
	public String exibirPaginaListarNoticias(Model model) {
		List<Noticia> noticias = noticiaRepository.findAll();
		for (Noticia noticia : noticias) {
			Autor autor = autorRepository.findById(noticia.getAutor().getId()).orElse(null);
			noticia.setAutor(autor);
		}
		model.addAttribute("noticias", noticias);
		return "listarNoticias";
	}

	@GetMapping(value = "/removerNoticia")
	public String removerDocumentos(@RequestParam String idNoticia) {
		System.out.println("removendo noticia de id " + idNoticia);
		noticiaRepository.deleteById(idNoticia);
		return "index";
	}

	@GetMapping(value = "/editarNoticia")
	public String mostrarpaginaEditaNoticia(@RequestParam String idNoticia, Model model) {
		Noticia noticia = noticiaRepository.findById(idNoticia).orElse(null);
		model.addAttribute("noticia", noticia);
		return "editarNoticia";
	}

	@PostMapping(value = "/editarNoticia")
	public String editaNoticia(@RequestParam String idNoticia, Noticia noticia) {
		noticiaRepository.save(noticia);
		return "redirect:listarNoticias";
	}
}
