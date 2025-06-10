let stompClient = null;
let recibido = false;
let codigoActual = null;

function conectarWebSocket(codigoEstudiante, callback) {
  const socket = new SockJS("http://localhost:5004/ws");
  stompClient = Stomp.over(socket);

  stompClient.connect({}, () => {
    console.log("Conectado al WebSocket como estudiante");

    stompClient.subscribe(`/notificacion/estudiante/${codigoEstudiante}`, (mensaje) => {
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

  codigoActual = codigoEstudiante;
  recibido = false;

  if (esAsincrono) {
    // Conexión WebSocket antes de enviar petición
    if (!stompClient || !stompClient.connected) {
      conectarWebSocket(codigoEstudiante, () => {
        hacerPeticion(codigoEstudiante, true);
      });
    } else {
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
        console.log("Petición asincrónica enviada, esperando respuesta por WebSocket...");
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
