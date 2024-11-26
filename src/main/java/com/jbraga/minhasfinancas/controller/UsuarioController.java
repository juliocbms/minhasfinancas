package com.jbraga.minhasfinancas.controller;


import com.jbraga.minhasfinancas.api.dto.TokenDTO;
import com.jbraga.minhasfinancas.api.dto.UsuarioDTO;
import com.jbraga.minhasfinancas.exception.ErroAutenticacao;
import com.jbraga.minhasfinancas.exception.RegraNegocioException;
import com.jbraga.minhasfinancas.model.entity.Usuario;
import com.jbraga.minhasfinancas.service.JwtService;
import com.jbraga.minhasfinancas.service.LancamentoService;
import com.jbraga.minhasfinancas.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Tag(name = "usuario", description = "Controlador para salvar, autenticar, e obter saldo do usuario")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService service;
    private final LancamentoService lancamentoService;
    private  final JwtService jwtService;

    @PostMapping("/autenticar")
    @Operation(summary = "autentica usuarios", description = "metodo para autenticar dados de usuarios")
    @ApiResponse(responseCode = "200" , description = "usuario autenticado")
    @ApiResponse(responseCode = "400", description = "Usuario não cadastrado")
    public ResponseEntity<?> autenticar(@RequestBody UsuarioDTO dto){
        try{
Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
String token = jwtService.gerarToken(usuarioAutenticado);
            TokenDTO tokenDTO = new TokenDTO(usuarioAutenticado.getNome(), token);
return  ResponseEntity.ok(tokenDTO);
        }catch (ErroAutenticacao e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

@PostMapping
@Operation(summary = "salva usuarios", description = "metodo para salvar dados de usuarios")
@ApiResponse(responseCode = "201" , description = "usuario salvo")
@ApiResponse(responseCode = "400", description = "Usuario ja cadastrado")
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

   @GetMapping("{id}/saldo")
   public ResponseEntity obterSaldo(@PathVariable("id") Long id){
       Optional<Usuario> usuario = service.obterPorId(id);

       if (!usuario.isPresent()){
           return  new ResponseEntity(HttpStatus.NOT_FOUND);
       }

BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
return ResponseEntity.ok(saldo);
   }
}
