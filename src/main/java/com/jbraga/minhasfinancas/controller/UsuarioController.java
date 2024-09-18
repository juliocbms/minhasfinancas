package com.jbraga.minhasfinancas.controller;


import com.jbraga.minhasfinancas.api.dto.UsuarioDTO;
import com.jbraga.minhasfinancas.exception.ErroAutenticacao;
import com.jbraga.minhasfinancas.exception.RegraNegocioException;
import com.jbraga.minhasfinancas.model.entity.Usuario;
import com.jbraga.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @PostMapping("/autenticar")
    public ResponseEntity autenticar(@RequestBody UsuarioDTO dto){
        try{
Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
return  ResponseEntity.ok(usuarioAutenticado);
        }catch (ErroAutenticacao e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

@PostMapping
   public ResponseEntity salvar(@RequestBody UsuarioDTO dto){

       Usuario usuario = Usuario.builder()
               .nome(dto.getNome())
               .email(dto.getEmail())
               .senha(dto.getSenha())
               .build();
       try {
          Usuario usuarioSalvo = service.salvarUsuario(usuario);
          return  new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
       }catch (RegraNegocioException e){
return ResponseEntity.badRequest().body(e.getMessage());
       }
   }
}
