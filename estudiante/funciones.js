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

    if(callback) callback();
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
  const url = esAsincrono
    ? "http://localhost:5004/api/orquestadorAsincrono"
    : "http://localhost:5004/api/orquestadorSincrono";

  fetch(url, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ codigoEstudiante: codigo })
  })
    .then(response => {
      return esAsincrono ? response.text() : response.json();
    })
    .then(data => {
      if (!esAsincrono) {
        mostrarResultado(data); // solo mostramos de inmediato si es síncrono
      } else {
        console.log("Petición asíncrona enviada, esperando respuesta por WebSocket...");
      }
    })
    .catch(error => {
      console.error("Error al enviar petición:", error);
      mostrarError("Error al enviar la petición.");
    });
}



function mostrarResultado(respuesta) {
  const contenedor = document.getElementById("resultado");
  contenedor.innerHTML = "";

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
