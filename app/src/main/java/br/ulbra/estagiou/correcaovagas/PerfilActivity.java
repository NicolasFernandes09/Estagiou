
//codigo para salvar no perfil
.setPositiveButton("Alterar", (confirmDialog, which) -> {
        UsuarioStore.atualizarPerfil(PerfilActivity.this, usuarioLogado, nome, email, profissional, pessoal, foto);
// ^ só grava local, nunca chamava a API
    ...
            })
