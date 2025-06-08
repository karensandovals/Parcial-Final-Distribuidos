let clienteSocket = null;

window.addEventListener("DOMContentLoaded", () => {
  conectarWebSocket();

  const form = document.getElementById("pazYSalvoForm");
  form.addEventListener("submit", async (event) => {
    event.preventDefault();
    limpiarResultados();
    mostrarSpinner(true);

    const codigo = document.getElementById("codigoEstudiante").value;

    try {
      const response = await fetch("http://localhost:5004/api/orquestadorSincrono", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ codigoEstudiante: codigo})
      });

      if (!response.ok) {
        throw new Error("Error en la consulta");
      }

      // Aquí no hacemos nada porque las notificaciones llegarán por WebSocket
    } catch (error) {
      mostrarError("No se pudo realizar la consulta.");
    } finally {
      mostrarSpinner(false);
    }
  });
});

function conectarWebSocket() {
  const socket = new SockJS("http://localhost:5004/ws");
  clienteSocket = Stomp.over(socket);

  clienteSocket.connect({}, () => {
    console.log("Conectado a WebSocket");
    clienteSocket.subscribe("/notificacion/admin/laboratorio", (msg) => {
      mostrarResultado("Laboratorio", msg.body);
    });
    clienteSocket.subscribe("/notificacion/admin/financiera", (msg) => {
      mostrarResultado("Financiera", msg.body);
    });
    clienteSocket.subscribe("/notificacion/admin/deportes", (msg) => {
      mostrarResultado("Deportes", msg.body);
    });
  }, (error) => {
    console.error("Error al conectar WebSocket:", error);
  });
}

function mostrarSpinner(visible) {
  document.getElementById("loadingSpinner").classList.toggle("d-none", !visible);
}

function mostrarResultado(servicio, mensaje) {
  const contenedor = document.getElementById("resultadoConsulta");
  const alerta = document.createElement("div");
  alerta.className = "alert alert-info";
  alerta.innerHTML = `<strong>${servicio}:</strong> ${mensaje}`;
  contenedor.appendChild(alerta);
}

function mostrarError(mensaje) {
  const contenedor = document.getElementById("resultadoConsulta");
  contenedor.innerHTML = `<div class="alert alert-danger">${mensaje}</div>`;
}

function limpiarResultados() {
  document.getElementById("resultadoConsulta").innerHTML = "";
}
