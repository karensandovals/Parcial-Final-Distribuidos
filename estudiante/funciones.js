let stompClient = null;
let intentos = 0;
let recibido = false;
let codigoActual = null;
let timeoutId = null;

function conectarWebSocket(codigoEstudiante) {
  const socket = new SockJS("http://localhost:5004/ws");
  stompClient = Stomp.over(socket);

  stompClient.connect({}, () => {
    console.log("Conectado al WebSocket como estudiante");

    stompClient.subscribe(`/notificacion/estudiante/${codigoEstudiante}`, (mensaje) => {
      recibido = true;
      clearTimeout(timeoutId);
      mostrarResultado(JSON.parse(mensaje.body));
    });
  });
}

function consultarPazYSalvo() {
  const codigoEstudiante = document.getElementById("codigoEstudiante").value.trim();
  if (!codigoEstudiante) {
    alert("Ingrese un código de estudiante.");
    return;
  }

  codigoActual = codigoEstudiante;
  recibido = false;
  intentos = 0;

  if (!stompClient || !stompClient.connected) {
    conectarWebSocket(codigoEstudiante);
  }

  hacerPeticion(codigoEstudiante);
}

function hacerPeticion(codigo) {
  intentos++;

  fetch("http://localhost:5004/api/orquestadorSincrono", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ codigoEstudiante: codigo })
  })
    .then(response => response.json())
    .then(data => {
      console.log("Petición enviada. Esperando respuesta...");
      iniciarTimeout();
    })
    .catch(error => {
      console.error("Error al enviar petición:", error);
      mostrarError("Error al enviar la petición.");
    });
}

function iniciarTimeout() {
  timeoutId = setTimeout(() => {
    if (!recibido && intentos < 3) {
      console.warn(`No se recibió respuesta. Reintentando (${intentos}/3)...`);
      hacerPeticion(codigoActual);
    } else if (!recibido) {
      mostrarError("No se obtuvo respuesta tras 3 intentos.");
    }
  }, 5000); // Esperar 5 segundos antes de decidir si se reintenta
}

function mostrarResultado(respuesta) {
  const contenedor = document.getElementById("resultado");
  contenedor.innerHTML = "";

  contenedor.innerHTML += `<p><strong>Código estudiante:</strong> ${respuesta.codigoEstudiante}</p>`;

  const renderLista = (titulo, lista, formatter) => {
    contenedor.innerHTML += `<h3>${titulo}</h3>`;
    if (Array.isArray(lista) && lista.length > 0) {
      lista.forEach(item => {
        const p = document.createElement("p");
        p.textContent = formatter(item);
        contenedor.appendChild(p);
      });
    } else {
      contenedor.innerHTML += `<p>✅ Paz y salvo</p>`;
    }
  };

  renderLista("Financiera", respuesta.objFinanciera, item => 
    `Debe $${item.montoAdeudado} por concepto de ${item.motivoDeuda}`
  );

  renderLista("Laboratorio", respuesta.objLaboratorio, item => 
    `Tiene pendiente el laboratorio: ${item.equipoPrestado}`
  );

  renderLista("Deportes", respuesta.objDeportes, item => 
    `Debe devolver el implemento: ${item.nombreElemento}`
  );

  contenedor.innerHTML += `<p><strong>Mensaje:</strong> ${respuesta.mensaje}</p>`;
}

function mostrarError(mensaje) {
  const contenedor = document.getElementById("resultado");
  contenedor.innerHTML = `<p style="color:red;"><strong>${mensaje}</strong></p>`;
}
