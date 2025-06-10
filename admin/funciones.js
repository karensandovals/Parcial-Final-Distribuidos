let areaSeleccionada = null;
let stompClient = null;
let ultimoCodigoEstudiante = null;


function conectarArea() {
  areaSeleccionada = document.getElementById('selectArea').value;

  const socket = new SockJS('http://localhost:5004/ws');
  stompClient = Stomp.over(socket);

  stompClient.connect({}, function () {
    stompClient.subscribe(`/notificacion/general/${areaSeleccionada}`, function (mensaje) {
      mostrarMensaje(mensaje.body);
    });

    stompClient.subscribe(`/notificacion/${areaSeleccionada}`, function (mensaje) {
      mostrarDeudas(JSON.parse(mensaje.body));
    });

    document.getElementById('btnConectar').disabled = true;
    document.getElementById('btnDesconectar').disabled = false; 
    document.getElementById('btnEliminarDeudas').disabled = false; // Habilita 
  });
}

function desconectarArea() {
  if (stompClient) {
    stompClient.disconnect(() => {
      mostrarMensaje("Desconectado.");
      document.getElementById('btnConectar').disabled = false;
      document.getElementById('btnDesconectar').disabled = true;
      document.getElementById('deudas').innerHTML = '';
      document.getElementById('btnEliminarDeudas').disabled = true; // Deshabilita 
    });
  }
}

function mostrarDeudas(data) {
  const contenedor = document.getElementById('deudas');
  contenedor.innerHTML = '';

  if (!data || data.length === 0) {
    contenedor.innerHTML = '<p>El estudiante está a paz y salvo.</p>';
    return;
  }

  // Guardar el último código recibido (tomamos el del primer elemento)
  ultimoCodigoEstudiante = data[0].codigoEstudiante;

  data.forEach(item => {
    const p = document.createElement('p');
    if (areaSeleccionada === 'financiera') {
      p.textContent = `Estudiante ${item.codigoEstudiante} debe $${item.montoAdeudado} por ${item.motivoDeuda}.`;
    } else if (areaSeleccionada === 'laboratorio') {
      p.textContent = `Estudiante ${item.codigoEstudiante} debe devolver: ${item.equipoPrestado}.`;
    } else if (areaSeleccionada === 'deportes') {
      p.textContent = `Estudiante ${item.codigoEstudiante} debe el implemento: ${item.nombreElemento}.`;
    }
    contenedor.appendChild(p);
  });
}

function eliminarDeudas() {
  if (!areaSeleccionada) {
    alert("Primero selecciona un área.");
    return;
  }

  if (!ultimoCodigoEstudiante) {
    alert("No hay código de estudiante registrado. Espera a que llegue una notificación.");
    return;
  }

  const peticion = {
    codigoEstudiante: ultimoCodigoEstudiante
  };

  let endpoint = '';
  switch (areaSeleccionada) {
    case 'financiera':
      endpoint = '/api/eliminarDeudasFinanciera';
      break;
    case 'laboratorio':
      endpoint = '/api/eliminarDeudasLaboratorio';
      break;
    case 'deportes':
      endpoint = '/api/eliminarDeudasDeportes';
      break;
    default:
      alert("Área no válida.");
      return;
  }

  fetch(`http://localhost:5004${endpoint}`, {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(peticion)
  })
    .then(response => {
      if (!response.ok) {
        throw new Error("Error al eliminar la deuda.");
      }
      return response.text();
    })
    .then(msg => {
      mostrarMensaje(msg);
      document.getElementById('deudas').innerHTML = '';
      ultimoCodigoEstudiante = null; // Limpiar después de eliminar
    })
    .catch(error => {
      console.error(error);
      alert("Hubo un problema al eliminar la deuda.");
    });
}

function mostrarMensaje(msg) {
  const contenedor = document.getElementById('mensajes');
  const p = document.createElement('p');
  p.textContent = msg;
  p.classList.add('fw-bold', 'text-info');
  contenedor.appendChild(p);
}

