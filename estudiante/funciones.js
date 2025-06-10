let stompClient = null;
let recibido = false;
let codigoActual = null;
let suscripcionActual = null;

function conectarWebSocket(codigoEstudiante, callback) {
  const socket = new SockJS("http://localhost:5004/ws");
  stompClient = Stomp.over(socket);

  stompClient.connect({}, () => {
    console.log("Conectado al WebSocket como estudiante");

    // Si ya existe una suscripción activa, cancelarla
    if (suscripcionActual) {
      suscripcionActual.unsubscribe();
    }

    suscripcionActual = stompClient.subscribe(`/notificacion/estudiante/${codigoEstudiante}`, (mensaje) => {
      console.log("🔔 Mensaje recibido en canal estudiante:", mensaje.body);
      recibido = true;
      mostrarResultado(JSON.parse(mensaje.body));
    });

    if (callback) callback();
  });
}

function consultar(esAsincrono = false) {
  const codigoEstudiante = document.getElementById("codigoEstudiante").value.trim();
  if (!codigoEstudiante) {
    alert("Ingrese un código de estudiante.");
    return;
  }

  recibido = false;

  if (esAsincrono) {
    if (!stompClient || !stompClient.connected) {
      conectarWebSocket(codigoEstudiante, () => {
        codigoActual = codigoEstudiante;
        hacerPeticion(codigoEstudiante, true);
      });
    } else if (codigoEstudiante !== codigoActual) {
      // Si cambió el estudiante, nos desuscribimos y nos volvemos a conectar
      conectarWebSocket(codigoEstudiante, () => {
        codigoActual = codigoEstudiante;
        hacerPeticion(codigoEstudiante, true);
      });
    } else {
      // Ya conectado y mismo estudiante
      hacerPeticion(codigoEstudiante, true);
    }
  } else {
    hacerPeticion(codigoEstudiante, false);
  }
}


function hacerPeticion(codigo, esAsincrono = false) {
  const estado = document.getElementById("estado");
  const botones = document.querySelectorAll("button");

  // Deshabilitar botones durante la petición
  botones.forEach(boton => boton.disabled = true);

  // Mostrar estado inicial
  estado.style.display = "block";
  estado.className = "alert alert-info mt-3";
  estado.innerHTML = `
    <div class="spinner-border spinner-border-sm" role="status"></div>
    <span class="ms-2">Iniciando consulta ${esAsincrono ? 'asíncrona' : 'síncrona'}...</span>
  `;

  const url = esAsincrono
    ? "http://localhost:5004/api/orquestadorAsincrono"
    : "http://localhost:5004/api/orquestadorSincrono";

  fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ codigoEstudiante: codigo })
  })
    .then(response => {
      if (!response.ok) {
        // Si es error HTTP (4xx, 5xx)
        throw new Error(`Error ${response.status}: ${response.statusText}`);
      }
      return esAsincrono ? response.text() : response.json();
    })
    .then(data => {
      if (!esAsincrono) {
        // Manejo respuesta síncrona
        if (data.mensaje && data.mensaje.includes("Error")) {
          estado.className = "alert alert-danger mt-3";
          estado.innerHTML = `<i class="fas fa-exclamation-triangle"></i> ${data.mensaje}`;
        } else {
          estado.className = "alert alert-success mt-3";
          estado.innerHTML = `<i class="fas fa-check-circle"></i> Consulta completada`;
          setTimeout(() => estado.style.display = "none", 2000);
          mostrarResultado(data);
        }
      } else {
        // Manejo respuesta asíncrona
        estado.className = "alert alert-warning mt-3";
        estado.innerHTML = `
          <div class="spinner-border spinner-border-sm" role="status"></div>
          <span class="ms-2">Consulta asíncrona en progreso. Esperando notificación...</span>
        `;
        console.log("Petición asíncrona enviada, esperando respuesta por WebSocket...");
      }
    })
    .catch(error => {
      console.error("Error al enviar petición:", error);
      estado.className = "alert alert-danger mt-3";
      estado.innerHTML = `
        <i class="fas fa-exclamation-circle"></i> Error en la consulta: ${error.message}
        ${!esAsincrono ? '<div class="mt-2">Intente nuevamente.</div>' : ''}
      `;
      mostrarError(`Error al procesar la petición: ${error.message}`);
    })
    .finally(() => {
      // Rehabilitar botones al finalizar
      botones.forEach(boton => boton.disabled = false);
    });
}



function mostrarResultado(respuesta) {
  const contenedor = document.getElementById("resultado");
  contenedor.innerHTML = "";

  if (respuesta.errores && respuesta.errores.length > 0) {
    contenedor.innerHTML = `
    <div class="alert alert-danger" role="alert">
      <strong>Errores detectados:</strong>
      <ul>${respuesta.errores.map(e => `<li>${e}</li>`).join("")}</ul>
    </div>
  `;
    return;
  }


  // Mostrar datos generales
  contenedor.innerHTML += `
    <h4 class="mb-3"><strong>Código estudiante:</strong> ${respuesta.codigoEstudiante}</h4>
  `;

  const tieneDeudas = (respuesta.objFinanciera?.length > 0) ||
    (respuesta.objLaboratorio?.length > 0) ||
    (respuesta.objDeportes?.length > 0);

  if (!tieneDeudas) {
    contenedor.innerHTML += `
      <div class="alert alert-success" role="alert">
        ✅ Usted se encuentra a paz y salvo.
      </div>
    `;
    return;
  }

  contenedor.innerHTML += `
    <div class="alert alert-danger" role="alert">
      ⚠️ Usted no está a paz y salvo debido a que tiene pendientes en algunas áreas.
    </div>
  `;

  const renderTabla = (titulo, lista, headers, getFilaHtml) => {
    if (Array.isArray(lista) && lista.length > 0) {
      contenedor.innerHTML += `<h4 class="mt-4">${titulo}</h4>`;
      let tabla = `<div class="table-responsive"><table class="table table-bordered table-striped table-sm align-middle">`;
      tabla += "<thead class='table-dark'><tr>";
      headers.forEach(header => {
        tabla += `<th>${header}</th>`;
      });
      tabla += "</tr></thead><tbody>";

      lista.forEach(item => {
        tabla += "<tr>" + getFilaHtml(item) + "</tr>";
      });

      tabla += "</tbody></table></div>";
      contenedor.innerHTML += tabla;
    }
  };

  renderTabla("Área Financiera", respuesta.objFinanciera,
    ["Monto Adeudado", "Motivo", "Fecha Generación", "Fecha Límite", "Estado"],
    item => `
      <td>$${item.montoAdeudado}</td>
      <td>${item.motivoDeuda}</td>
      <td>${item.fechaGeneracionDeuda}</td>
      <td>${item.fechaLimitePago}</td>
      <td>${item.estadoDeuda}</td>
    `
  );

  renderTabla("Laboratorio", respuesta.objLaboratorio,
    ["Equipo Prestado", "Estado", "Fecha Préstamo", "Fecha Devolución Estimada", "Fecha Devolución Real"],
    item => `
      <td>${item.equipoPrestado}</td>
      <td>${item.estadoPrestamo}</td>
      <td>${item.fechaPrestamo}</td>
      <td>${item.fechaDevolucionEstimada}</td>
      <td>${item.fechaDevolucionReal ?? "—"}</td>
    `
  );

  renderTabla("Deportes", respuesta.objDeportes,
    ["Implemento", "Fecha Préstamo", "Fecha Devolución Estimada", "Fecha Devolución Real"],
    item => `
      <td>${item.implementoDeportivoPrestado}</td>
      <td>${item.fechaPrestamo}</td>
      <td>${item.fechaDevolucionEstimada}</td>
      <td>${item.fechaDevolucionReal ?? "—"}</td>
    `
  );

  // Mostrar mensaje general si viene
  if (respuesta.mensaje) {
    contenedor.innerHTML += `<div class="alert alert-info mt-3"><strong>Mensaje:</strong> ${respuesta.mensaje}</div>`;
  }
}


function mostrarError(mensaje) {
  const contenedor = document.getElementById("resultado");
  contenedor.innerHTML = `<p style="color:red;"><strong>${mensaje}</strong></p>`;
}

function mostrarEstado(mensaje, tipo = 'info') {
  const estado = document.getElementById("estado");
  const estadoTexto = document.getElementById("estado-texto");

  estado.style.display = "block";
  estadoTexto.textContent = mensaje;

  // Cambiar clase según tipo
  estado.className = `alert alert-${tipo} mt-3`;
}

function ocultarEstado() {
  document.getElementById("estado").style.display = "none";
}