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

function consultarPazYSalvo() {
  const codigoEstudiante = document.getElementById("codigoEstudiante").value.trim();
  if (!codigoEstudiante) {
    alert("Ingrese un código de estudiante.");
    return;
  }

  codigoActual = codigoEstudiante;
  recibido = false;

  if (!stompClient || !stompClient.connected) {
    conectarWebSocket(codigoEstudiante, () => {
      hacerPeticion(codigoEstudiante);
    });
  }else{
    hacerPeticion(codigoEstudiante);
  }

  
}

function hacerPeticion(codigo) {
  fetch("http://localhost:5004/api/orquestadorSincrono", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify({ codigoEstudiante: codigo })
  })
    .then(response => {
      console.log("Código de respuesta:", response.status);
      console.log("Tipo de contenido:", response.headers.get("content-type"));
      return response.text();  // usar text() para ver si es un JSON válido
    })
    .then(text => {
      console.log("Texto recibido:", text);
      try {
        const data = JSON.parse(text);
        console.log("Objeto JSON:", data);
      } catch (e) {
        console.error("Respuesta no es JSON válido:", e);
      }
    })
    .catch(error => {
      console.error("Error al enviar petición:", error);
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
