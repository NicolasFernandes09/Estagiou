const btnMenu = document.getElementById("btnMenu");
const sidebar = document.getElementById("sidebar");
const overlay = document.getElementById("overlay");

function abrirMenu() {
    sidebar.classList.add("aberto");
    overlay.classList.add("ativo");
}

function fecharMenu() {
    sidebar.classList.remove("aberto");
    overlay.classList.remove("ativo");
}

btnMenu.addEventListener("click", () => {
    if (sidebar.classList.contains("aberto")) {
        fecharMenu();
    } else {
        abrirMenu();
    }
});

overlay.addEventListener("click", fecharMenu);
